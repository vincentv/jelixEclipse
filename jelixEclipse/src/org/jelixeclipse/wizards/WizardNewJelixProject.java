/**
 * 
 */
package org.jelixeclipse.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.jelixeclipse.Activator;
import org.jelixeclipse.preferences.PreferenceConstants;
import org.jelixeclipse.utils.JelixTools;
import org.jelixeclipse.wizards.pages.WizardNewJelixProjectPage;

/**
 * @author vincent
 * 
 */
public class WizardNewJelixProject extends Wizard implements INewWizard {

	private WizardNewJelixProjectPage page1;
	private IProject newProject;
	private IFolder jTemp;
	private String jelixVersion = "jelix-1.0b2.1-dev";
	private IWorkbench fWorkbench;
	private IConfigurationElement fConfigElement;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		boolean success = true;

		IRunnableWithProgress newProjectOp = new WorkspaceModifyDelegatingOperation(
				doFinish());

		// Run the project creation operation
		try {
			getContainer().run(false, true, newProjectOp);
		} catch (InvocationTargetException e) {
			success = false;
		} catch (InterruptedException e) {
			success = false;
		}

		// Select and reveal the project in the workbench window
		BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
		BasicNewResourceWizard.selectAndReveal(newProject, fWorkbench
				.getActiveWorkbenchWindow());
		return success;
	}

	/**
	 * The worker method. It will find the container, create the file if missing
	 * or just replace its contents, and open the editor on the newly created
	 * file.
	 * 
	 * @throws InvocationTargetException
	 */

	private IRunnableWithProgress doFinish() {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {

				IPreferenceStore store = Activator.getDefault()
						.getPreferenceStore();
				if (PreferenceConstants.P_NAME_JELIX_ZIP.equals("")) {
					// on affecte par default la valeur actuelle
					store.setValue(PreferenceConstants.P_NAME_JELIX_ZIP,
							jelixVersion);
				} else {
					jelixVersion = PreferenceConstants.P_NAME_JELIX_ZIP;
				}

				// On crée la description du projet
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				newProject = page1.getProjectHandle();
				IProjectDescription description = workspace
						.newProjectDescription(newProject.getName());

				// On defini le chemin du projet
				IPath oldPath = Platform.getLocation();
				IPath newPath = page1.getLocationPath();
				if (!oldPath.equals(newPath)) {
					oldPath = newPath;
					description.setLocation(newPath);
				}

				// On ajoute la nature Jelix et PHP au projet
				String[] natureIds = { "jelix",
						"org.eclipse.php.core.PHPNature" };
				description.setNatureIds(natureIds);

				// Create and open the project
				try {
					if (!newProject.exists()) {
						newProject.create(description, new SubProgressMonitor(
								monitor, 10));
					}

					if (!newProject.isOpen()) {
						newProject.open(new SubProgressMonitor(monitor, 10));
					}

					// On récupère le repertoire de Jelix si demandé
					if (page1.getJelixImportationButton()) {
						jTemp = newProject.getFolder("jTemp");
						jTemp.create(true, true, monitor);
						getJelixLib(monitor);
						jTemp.delete(true, monitor);
					}

					/* on stocke les preferences */
					monitor.setTaskName("Enregistrement des preferences ...");
					String separateur = File.separator;
					store.setValue(PreferenceConstants.P_PATH_JELIX, separateur
							+ newProject.getName());

					// on enregistre le chemin du projet JELIX dans les
					// preferences
					String cheminProjet = newProject.getLocation().toOSString()
							+ separateur;
					store.setValue(PreferenceConstants.P_PATH_JELIX_SCRIPT,
							cheminProjet);

					// on vide le chemin vers l'appli
					store.setValue(PreferenceConstants.P_NAME_APP_JELIX, "");

					// on renseigne la valeur de la version jelix
					store.setValue(PreferenceConstants.P_NAME_JELIX_ZIP,
							jelixVersion);

					monitor.worked(1);

				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}

			}
		};
	}

	/**
	 * Récupere l'archive Jelix et la rajoute au projet
	 * 
	 * @param jelixVersion
	 *            version de Jelix à récuperer
	 * @param monitor
	 * 
	 * @return
	 */
	private boolean getJelixLib(IProgressMonitor monitor) {

		IPath destination;
		boolean success = false;

		// On télécharge le répertoire si demandé
		monitor.setTaskName("Telechargement des librairies Jelix ...");
		if (this.page1.getJelixDownloadButton()) {
			destination = this.downloadJelix(jelixVersion, monitor);
		} else {
			destination = this.localJelix(jelixVersion, monitor);
		}

		monitor.setTaskName("decompression des librairies Jelix ...");
		success = JelixTools.unzip(new File(JelixTools.dirpath(destination)
				+ jelixVersion + ".zip"), destination);
		// OutilsZip.unzipToDir(destination + jelixVersion + ".zip",
		// destination);

		success = this.copyJelixLib(jelixVersion, monitor);

		return success;
	}

	/**
	 * Télécharge l'archive Jelix sur les serveur distant
	 * 
	 * @param jelixVersion
	 *            version de Jelix a téléchargé
	 * @param monitor
	 * @return la destination de l'archive Jelix
	 */
	private IPath downloadJelix(String jelixVersion, IProgressMonitor monitor) {

		String source;
		IPath destination;

		if (this.page1.getJelixImportSrcDownloadBerlios1()) {
			source = "http://download.berlios.de/jelix/" + jelixVersion
					+ ".zip";
		} else {
			source = "http://download2.berlios.de/jelix/" + jelixVersion
					+ ".zip";
		}

		destination = jTemp.getLocation();
		JelixTools.download(source, destination);

		return destination;
	}

	/**
	 * Récupère une archive Jelix en local
	 * 
	 * @param jelixVersion
	 *            version de Jelix à copier
	 * @param monitor
	 * @return la destination de l'archive Jelix
	 */
	private IPath localJelix(String jelixVersion, IProgressMonitor monitor) {
		Path source = new Path(this.page1.getJelixLibrairiesLocal());

		File sourceFile = source.toFile();
		IFile destFile = newProject.getFile(sourceFile.getName());

		try {
			((IFile) source.toFile()).copy(destFile.getLocation(), true,
					monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return destFile.getLocation();
	}

	/**
	 * Copie Jelix a son emplacement final
	 * 
	 * @param jelixVersion
	 *            version de Jelix à copier
	 * @param monitor
	 * @return true si la copie c'est bien passé
	 */
	private boolean copyJelixLib(String jelixVersion, IProgressMonitor monitor) {
		// on copie les repertoires et fichiers JELIX

		try {
			IFolder jelixTemp = jTemp.getFolder(jelixVersion);
			jelixTemp.refreshLocal(IResource.DEPTH_INFINITE, monitor);

			File f = new File(jelixTemp.getLocation().toOSString());
			File[] listeFichiers;
			listeFichiers = f.listFiles();

			for (File file : listeFichiers) {
				if (file.isDirectory()) {
					IFolder tmpFolder = newProject.getFolder(file.getName());
					IFolder rep = jelixTemp.getFolder(file.getName());
					rep.copy(tmpFolder.getFullPath(), true, monitor);
				} else {
					IFile tmpFile = newProject.getFile(file.getName());
					IFile tmpf = jelixTemp.getFile(file.getName());
					tmpf.copy(tmpFile.getFullPath(), true, monitor);
				}
			}

			return true;

		} catch (CoreException e) {
			return false;
		}

	}

	/*
	 * private boolean runJelixCommand(File location, String projName, String
	 * appName, IProgressMonitor monitor) { return false; }
	 */

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();

		setNeedsProgressMonitor(true);
		page1 = new WizardNewJelixProjectPage("new.jelix.project1");
		page1.setTitle("Nouveau Projet Jelix");
		page1.setDescription("Création d'un nouveau projet Jelix");
		addPage(page1);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.fWorkbench = workbench;
	}

	/*
	 * Stores the configuration element for the wizard. The config element will
	 * be used in <code>performFinish</code> to set the result perspective.
	 */
	public void setInitializationData(IConfigurationElement cfig,
			String propertyName, Object data) {
		fConfigElement = cfig;
	}
}
