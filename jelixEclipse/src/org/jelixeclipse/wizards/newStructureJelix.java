package org.jelixeclipse.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import org.eclipse.jface.preference.IPreferenceStore;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;
import org.eclipse.ui.*;
import org.jelixeclipse.Activator;
import org.jelixeclipse.preferences.PreferenceConstants;
import org.jelixeclipse.utils.OutilsZip;
import org.jelixeclipse.wizards.pages.newStructureJelixPage;

import java.net.URL;
import java.net.URLConnection;

//import jelixeclipse.newStructureJelix;


/**
 * This is a sample new wizard. Its role is to create a new file resource in the
 * provided container. If the container resource (a folder or a project) is
 * selected in the workspace when the wizard is opened, it will accept it as the
 * target container. The wizard creates one file with the extension "php". If a
 * sample multi-page editor (also available as a template) is registered for the
 * same extension, it will be able to open it.
 */

public class newStructureJelix extends Wizard implements INewWizard {
	private newStructureJelixPage page;

	private ISelection selection;

	private String jelixProjetNom;

	private String jelixProjetEmplacement;

	private Boolean jelixRecupSource;

	private Boolean jelixRecupSourceDownload;

	private Boolean jelixrecupSourceDownloadBerlios1;

	private Boolean jelixrecupSourceDownloadBerlios2;

	private Boolean jelixRecupSourceLocal;

	private String jelixRecupSourceLocalFile;

	/**
	 * Constructor for newStructureJelix.
	 */
	public newStructureJelix() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new newStructureJelixPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {

		// recup des valeurs saisies
		this.jelixProjetNom = page.getProjetJelixNom();
		//this.jelixProjetEmplacement = page.getProjetJelixEmplacement();
		this.jelixProjetEmplacement = "";
		this.jelixRecupSource = page.getImportSource();
		this.jelixRecupSourceDownload = page.getImportSourceDownload();
		this.jelixrecupSourceDownloadBerlios1 = page
				.getImportSourceDownloadBerlios1();
		this.jelixrecupSourceDownloadBerlios2 = page
				.getImportSourceDownloadBerlios2();
		this.jelixRecupSourceLocal = page.getImportSourceLocal();
		this.jelixRecupSourceLocalFile = page.getImportSourceLocalFile();

		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException
					.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * The worker method. It will find the container, create the file if missing
	 * or just replace its contents, and open the editor on the newly created
	 * file.
	 */

	private void doFinish(IProgressMonitor monitor) throws CoreException {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		if (PreferenceConstants.P_NAME_JELIX_ZIP.equals("")){
			// on affecte par default la valeur actuelle
			store.setValue(PreferenceConstants.P_NAME_JELIX_ZIP, "jelix-1.0b2.1-dev");
		}
		
		String versionJelix = PreferenceConstants.P_NAME_JELIX_ZIP;
		String versionJelixZip = PreferenceConstants.P_NAME_JELIX_ZIP + ".zip";
		
		monitor.beginTask("Creation du projet JELIX ", 2);

		monitor.setTaskName("Cr�ation du projet JELIX...");
		// creation du projet Eclipse
		String emplacement = this.jelixProjetEmplacement;

		String nvProjet = emplacement + "/" + this.jelixProjetNom;
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				nvProjet);
		project.create(null);
		project.open(null);
		monitor.worked(1);

		IFolder folder = project.getFolder("download");
		folder.create(true, true, monitor);
		folder.refreshLocal(IResource.DEPTH_INFINITE, monitor);

		// recup des repertoires JELIX si demand�
		if (this.jelixRecupSource) {
			// download des repertoires JELIX si demand�
			String destination;
			if (this.jelixRecupSourceDownload) {
				monitor.setTaskName("T�l�chargement des librairies JELIX...");
				String source;
				if (this.jelixrecupSourceDownloadBerlios1){
					source = "http://download.berlios.de/jelix/" + versionJelixZip;
				}else{
					source = "http://download2.berlios.de/jelix/" + versionJelixZip;
				}
				destination = folder.getLocation().toOSString() + "/";
				this.download(source, destination);
			} else {
				// copie des sources 
				monitor.setTaskName("Copie des librairies JELIX...");
				String source = this.jelixRecupSourceLocalFile;
				destination = folder.getLocation().toOSString() + "/";

				if (!newStructureJelix.copier(source, destination)){
					this.throwCoreException("Erreur lors de la copie des librairies JELIX");
				}
			}

			// on dezippe
			monitor.setTaskName("D�compression des librairies JELIX ...");
			try {
				OutilsZip.unzipToDir(destination
						+ versionJelixZip, destination);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}

			// on copie les repertoires et fichiers JELIX
			monitor
					.setTaskName("Transfert des fichiers dans le projet JELIX ...");
			IFolder jelixTemp = folder.getFolder(versionJelix);
			jelixTemp.refreshLocal(IResource.DEPTH_INFINITE, monitor);

			File f = new File(jelixTemp.getLocation().toOSString());
			File[] listeFichiers;
			listeFichiers = f.listFiles();
			for (int i = 0; i < listeFichiers.length; i++) {
				if (listeFichiers[i].isDirectory()) {
					// on cr�� le repertoire sur le projet
					IFolder tmpFolder = project.getFolder(listeFichiers[i]
							.getName());
					tmpFolder.refreshLocal(IResource.DEPTH_INFINITE, monitor);

					// on r�cup�re le r�pertoire courant
					IFolder rep = jelixTemp.getFolder(listeFichiers[i]
							.getName());
					rep.refreshLocal(IResource.DEPTH_INFINITE, monitor);

					// on copie dans le projet
					rep.copy(tmpFolder.getFullPath(), true, monitor);
				} else {
					// on cr�� le fichier sur le projet
					IFile tmpFile = project.getFile(listeFichiers[i].getName());
					tmpFile.refreshLocal(IResource.DEPTH_INFINITE, monitor);

					// on r�cup�re le fichier courant
					IFile tmpf = jelixTemp.getFile(listeFichiers[i].getName());
					tmpf.refreshLocal(IResource.DEPTH_INFINITE, monitor);

					// on copie le fichier sur le projet
					tmpf.copy(tmpFile.getFullPath(), true, monitor);

				}
			}

			// on supprime l'archive et le dossier d�compr�ss�*
			monitor.setTaskName("Suppression du repertoire temporaire ...");
			folder.delete(true, monitor);
		} // fin import source si demand�

		monitor.setTaskName("Enregistrement des pr�f�rences ...");
		
		// on enregistre le chemin relatif JELIX dans les pr�f�rences
		if (this.jelixProjetEmplacement.equals("")){
			this.jelixProjetEmplacement = "\\";
		}
		store.setValue(PreferenceConstants.P_PATH_JELIX, this.jelixProjetEmplacement + this.jelixProjetNom);	
		
		// on enregistre le chemin du projet JELIX dans les pr�ferences
		String cheminProjet = project.getLocation().toOSString() + this.jelixProjetEmplacement;
		store.setValue(PreferenceConstants.P_PATH_JELIX_SCRIPT, cheminProjet);	
		
		// on vide le chemin vers l'appli
		store.setValue(PreferenceConstants.P_NAME_APP_JELIX, "");	
		
		// on renseigne la valeur de la version jelix
		store.setValue(PreferenceConstants.P_NAME_JELIX_ZIP, versionJelix);

		monitor.worked(1);
	}

	/**
	 * We will initialize file contents with a sample text.
	 */

	private InputStream openContentStream() {
		String contents = "This is the initial file contents for *.php file that should be word-sorted in the Preview page of the multi-page editor";
		return new ByteArrayInputStream(contents.getBytes());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "jelixEclipse", IStatus.OK,
				message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	public void download(String u, String path) {
		java.io.FileOutputStream destinationFile = null;
		try {
			URL url = new URL(u);
			java.io.File destination = new java.io.File(path
					+ new java.io.File(u).getName());

			destination.createNewFile();

			URLConnection connection = url.openConnection();
			connection.setDoOutput(false);
			destinationFile = new java.io.FileOutputStream(destination);

			java.io.InputStream is = url.openStream();

			byte[] buffer = new byte[512 * 1024];
			int reads;

			while ((reads = is.read(buffer)) != -1) {
				destinationFile.write(buffer, 0, reads);
			}
		} catch (java.io.IOException ioe) {
			System.out.println("I/O Error on Navigation.download()");
		} catch (Exception e) {
		} finally {
			try {
				destinationFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** copie le fichier source dans le fichier resultat
	 * retourne vrai si cela r�ussit
	 */
	public static boolean copier( String u, String path )
	{
	        boolean resultat = false;
	        
	        // Declaration des flux
	        java.io.FileInputStream sourceFile=null;
	        java.io.FileOutputStream destinationFile=null;
	        
	        try {
	                
					java.io.File destination = new java.io.File(path
						+ new java.io.File(u).getName());
	        	
					java.io.File source = new java.io.File(u);
					
	        		// Cr�ation du fichier :
	        		destination.createNewFile();
	                
	                // Ouverture des flux
	                sourceFile = new java.io.FileInputStream(source);
	                destinationFile = new java.io.FileOutputStream(destination);
	                
	                // Lecture par segment de 0.5Mo 
	                byte buffer[]=new byte[512*1024];
	                int nbLecture;
	                
	                while( (nbLecture = sourceFile.read(buffer)) != -1 ) {
	                        destinationFile.write(buffer, 0, nbLecture);
	                } 
	                
	                // Copie r�ussie
	                resultat = true;
	        } catch( java.io.FileNotFoundException f ) {
	                
	        } catch( java.io.IOException e ) {
	                
	        } finally {
	                // Quoi qu'il arrive, on ferme les flux
	                try {
	                        sourceFile.close();
	                } catch(Exception e) { }
	                try {
	                        destinationFile.close();
	                } catch(Exception e) { }
	        } 
	        return( resultat );
	}

}