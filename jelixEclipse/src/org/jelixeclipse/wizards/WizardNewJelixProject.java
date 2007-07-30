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
				monitor.beginTask("", 5);
				IPreferenceStore store = Activator.getDefault()
						.getPreferenceStore();
				if (PreferenceConstants.P_NAME_JELIX_ZIP.equals("")) {
					// on affecte par default la valeur actuelle
					store.setValue(PreferenceConstants.P_NAME_JELIX_ZIP,
							jelixVersion);
				} else {
					jelixVersion = PreferenceConstants.P_NAME_JELIX_ZIP;
				}

				// Création d'un nouveau projet Jelix
				createNewProject(monitor);
				// Importation des librairie Jelix
				importJelixLib(monitor);

				monitor.done();

			}
		};
	}

	/**
	 * Création d'un nouveau projet dans le workspace et affection de la
	 * description du projet
	 * 
	 * @param monitor
	 * @return
	 */
	private boolean createNewProject(IProgressMonitor monitor) {
		boolean success = true;

		monitor.setTaskName("Création du projet ...");
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
		String[] natureIds = { "jelix", "org.eclipse.php.core.PHPNature" };
		description.setNatureIds(natureIds);

		try {
			if (!newProject.exists()) {
				newProject.create(description, new SubProgressMonitor(monitor,
						10));
			}
			if (!newProject.isOpen()) {
				newProject.open(new SubProgressMonitor(monitor, 10));
			}
		} catch (CoreException e) {
			success = false;
			e.printStackTrace();
		}

		monitor.worked(1);
		return success;
	}

	/**
	 * Importation des librairies Jelix, à partir d'une archive local ou
	 * distante.
	 * 
	 * @param monitor
	 * @return la reussite de l'importation
	 */
	private boolean importJelixLib(IProgressMonitor monitor) {
		boolean success = true;
		if (page1.getJelixImportationButton()) {
			// monitor.setTaskName("Importation des librairies Jelix ...");
			if (page1.getJelixDownloadButton()) {
				success = getRemoteJelix(jelixVersion, monitor);
			} else {
				success = getlocalJelix(jelixVersion, monitor);
			}
		}
		return success;

	}

	/**
	 * Importe les librairies Jelix à partir d'une archive distante sur les
	 * serveur de Berlios
	 * 
	 * @param jelixVersion
	 *            version de Jelix a téléchargé
	 * @param monitor
	 * @return La reussite de l'importation des librairies
	 */
	private boolean getRemoteJelix(String jelixVersion, IProgressMonitor monitor) {

		String source;
		boolean success = true;
		IPath destination;

		monitor.setTaskName("Telechargement des librairies Jelix ...");
		if (this.page1.getJelixImportSrcDownloadBerlios1()) {
			source = "http://download.berlios.de/jelix/" + jelixVersion
					+ ".zip";
		} else {
			source = "http://download2.berlios.de/jelix/" + jelixVersion
					+ ".zip";
		}
		destination = newProject.getLocation();
		// destination = jTemp.getLocation();
		JelixTools.download(source, destination);
		String JelixArchive = JelixTools.dirpath(destination) + jelixVersion
				+ ".zip";
		monitor.worked(2);

		monitor.setTaskName("Décompression des librairies Jelix ...");
		success = JelixTools.unzip(new File(JelixArchive), destination);
		monitor.worked(3);

		monitor.setTaskName("Déplacement des librairies Jelix ...");
		success = this.copyJelixLib(jelixVersion, monitor);
		monitor.worked(4);

		monitor.setTaskName("Néttoyage ...");
		File tmpArchiveJelix = new File(JelixTools.dirpath(newProject
				.getLocation())
				+ jelixVersion + ".zip");
		tmpArchiveJelix.delete();

		IFolder tmpJelix = newProject.getFolder(jelixVersion);
		try {
			tmpJelix.delete(true, monitor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		monitor.worked(5);
		return success;
	}

	/**
	 * Importe les librairies Jelix à partir d'une archive locale
	 * 
	 * @param jelixVersion
	 *            version de Jelix a téléchargé
	 * @param monitor
	 * @return La reussite de l'importation des librairies
	 */
	private boolean getlocalJelix(String jelixVersion, IProgressMonitor monitor) {
		Path source = new Path(this.page1.getJelixLibrairiesLocal());

		// File sourceFile = source.toFile();
		// IFile destFile = newProject.getFile(sourceFile.getName());
		boolean success = true;

		monitor.setTaskName("Décompression des librairies Jelix ...");
		success = JelixTools.unzip(source.toFile(), newProject.getLocation());

		try {
			newProject.refreshLocal(2, monitor);
			IResource[] tmp = newProject.members(IResource.FOLDER);
			jelixVersion = tmp[tmp.length - 1].getName();

			monitor.setTaskName("Déplacement des librairies Jelix ...");
			success = this.copyJelixLib(jelixVersion, monitor);

			monitor.setTaskName("Néttoyage ...");
			IFolder tmpJelix = newProject.getFolder(jelixVersion);
			tmpJelix.delete(true, monitor);
			
		} catch (CoreException e) {
			success = false;
			e.printStackTrace();
		}

		return success;
	}

	/**
	 * Copie les librairie Jelix à leur emplacement définitif
	 * 
	 * @param jelixVersion
	 *            version des librairie Jelix a copier
	 * @param monitor
	 * @return La réussite de la copie des librairie Jelix
	 */
	private boolean copyJelixLib(String jelixVersion, IProgressMonitor monitor) {
		// on copie les repertoires et fichiers JELIX

		try {
			IFolder jelixTemp = newProject.getFolder(jelixVersion);
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
