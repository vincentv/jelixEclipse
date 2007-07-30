/**
* @author      Ginesty Thibault, TOULOUSE (31), FRANCE
* @package     jelixeclipse.wizards
* @version     1.0
* @date        25/06/2007
* @link        http://www.jelix.org
* @licence     GNU General Public Licence see LICENCE file or http://www.gnu.org/licenses/gpl.html
*/

package org.jelixeclipse.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import org.eclipse.jface.preference.IPreferenceStore;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;



import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
import org.jelixeclipse.Activator;
import org.jelixeclipse.preferences.PreferenceConstants;
import org.jelixeclipse.utils.JelixOpenPage;
import org.jelixeclipse.utils.JelixShell;
import org.jelixeclipse.utils.JelixTools;
import org.jelixeclipse.wizards.pages.newDaoWizardPage;

public class newDaoWizard extends Wizard implements INewWizard {
	private newDaoWizardPage page;
	private ISelection mSelection;
	private IWorkbench fWorkbench;
	private IProject currentProject;

	/**
	 * Constructor for newDaoWizard.
	 */
	public newDaoWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new newDaoWizardPage(mSelection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {

		final String jelixModule = page.getJelixTextModule();
		final String jelixDao = page.getJelixTextDao();
		final String jelixTable = page.getJelixTextTable();
		final Boolean jelixOpenFile = page.getJelixOpenFile();
		this.currentProject = JelixTools.currentProject(this.mSelection);
		final String jelixAppli = page.getJelixTextAppli();
		
		/* Verification saisie utilisateur */
		if (jelixModule.equals("")){
			MessageDialog.openError(getShell(), "Erreur", "Veuillez s�lectionner un module");
			return false;
		}
		if (jelixDao.equals("")){
			MessageDialog.openError(getShell(), "Erreur", "Veuillez saisir un nom de DAO");
			return false;
		}
		if (jelixTable.equals("")){
			MessageDialog.openError(getShell(), "Erreur", "Veuillez saisir le nom d'une table");
			return false;
		}
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					/* Lancement de la methode sur le click Finish */
					doFinish(jelixAppli,jelixModule, jelixDao, jelixTable, jelixOpenFile,
							monitor);
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
			MessageDialog.openError(getShell(), "Erreur", realException
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

	private void doFinish(String jelixAppli, String jelixModule, String jelixDao,
			String jelixTable, Boolean jelixOpenFile, IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask("Creation de " + jelixDao, 2);

		/* on recupere l'objet de preference */
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String cmd = " --" + jelixAppli + " createdao " + jelixModule + " "
				+ jelixDao + " " + jelixTable;
		
		/* on lance la generation du script */
		org.jelixeclipse.utils.JelixShell js = new JelixShell(this.currentProject, cmd, store);
		Boolean res = js.play();
		if (!res){
			throwCoreException(js.getErreur());
		}

		monitor.worked(1);

		/* on ouvre le fichier si l'utilisateur a cocher la case */
		if (jelixOpenFile) {
			
			/* on essaye d'ouvrir le fichier cree */			
			String dossier = "";
			String separateur = File.separator;
			if (this.currentProject.getName().endsWith("/")
					|| this.currentProject.getName().endsWith("\\")) {
				dossier = separateur + this.currentProject.getName()
						+ jelixAppli + separateur + "modules" + separateur
						+ jelixModule + separateur + "daos";
				;
			} else {
				dossier = separateur + this.currentProject.getName()
						+ separateur + jelixAppli + separateur + "modules"
						+ separateur + jelixModule + separateur + "daos";
				;
			}
			
			String fichier = jelixDao + ".dao.xml";
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			
			/* on raffraichit le projet courant */			
			this.currentProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			
			IResource resource = root.findMember(new Path(dossier));
			if (!resource.exists() || !(resource instanceof IContainer)) {
				throwCoreException("Echec lors de l'ouverture du fichier "
						+ fichier);
			}

			IContainer container = (IContainer) resource;
			final IFile file = container.getFile(new Path(fichier));
			
			if (file.exists()) {
				monitor.setTaskName("Ouverture du fichier...");
				JelixOpenPage.Open(this, file);

			} else {
				throwCoreException("Echec lors de l'ouverture du fichier "
						+ fichier);
			}
		}

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
		this.mSelection = selection;
		this.fWorkbench = workbench;
	}
}