/**
 * @author      Ginesty Thibault, TOULOUSE (31), FRANCE
 * @package     org.jelixeclipse.wizards
 * @version     0.0.3
 * @date        25/06/2007
 * @link        http://www.jelix.org
 * @licence     GNU General Public Licence see LICENCE file or http://www.gnu.org/licenses/gpl.html
 */

package org.jelixeclipse.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.jelixeclipse.Activator;
import org.jelixeclipse.utils.JelixOpenPage;
import org.jelixeclipse.utils.JelixShell;
import org.jelixeclipse.utils.JelixTools;
import org.jelixeclipse.wizards.pages.WizardNewDaoPage;

public class WizardNewDao extends Wizard implements INewWizard {
	private WizardNewDaoPage page;
	private ISelection mSelection;
	private IProject currentProject;

	/**
	 * Constructor for WizardNewDao.
	 */
	public WizardNewDao() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */
	@Override
	public void addPages() {
		page = new WizardNewDaoPage(mSelection);
		addPage(page);
	}

	@Override
	public boolean performFinish() {

		final String jelixModule = page.getJelixTextModule();
		final String jelixDao = page.getJelixTextDao();
		final String jelixTable = page.getJelixTextTable();
		final Boolean jelixOpenFile = page.getJelixOpenFile();
		this.currentProject = JelixTools
				.currentProject((IStructuredSelection) this.mSelection);
		final String jelixAppli = page.getJelixTextAppli();

		/* Verification saisie utilisateur */
		if (jelixModule.equals("")) { //$NON-NLS-1$
			MessageDialog.openError(getShell(), Messages.WizardNewDao_Error,
					Messages.WizardNewDao_ModuleSelectionErrorMsg);
			return false;
		}
		if (jelixDao.equals("")) { //$NON-NLS-1$
			MessageDialog.openError(getShell(), Messages.WizardNewDao_Error,
					Messages.WizardNewDao_DAONameErrorMsg);
			return false;
		}
		if (jelixTable.equals("")) { //$NON-NLS-1$
			MessageDialog.openError(getShell(), Messages.WizardNewDao_Error,
					Messages.WizardNewDao_TableNameErrorMsg);
			return false;
		}

		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					/* Lancement de la methode sur le click Finish */
					doFinish(jelixAppli, jelixModule, jelixDao, jelixTable,
							jelixOpenFile, monitor);
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
			MessageDialog.openError(getShell(), Messages.WizardNewDao_Error,
					realException.getMessage());
			return false;
		}
		return true;
	}

	private void doFinish(String jelixAppli, String jelixModule,
			String jelixDao, String jelixTable, Boolean jelixOpenFile,
			IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(Messages.WizardNewDao_CreationOfMsg + jelixDao, 2);

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		/* on lance la generation du script */
		String cmd = " --" + jelixAppli + " createdao " + jelixModule + " " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ jelixDao + " " + jelixTable; //$NON-NLS-1$
		org.jelixeclipse.utils.JelixShell js = new JelixShell(
				this.currentProject, cmd, store);
		Boolean res = js.play();
		if (!res) {
			throwCoreException(js.getErreur());
		}
		monitor.worked(1);

		/* on ouvre le fichier si l'utilisateur a cocher la case */
		if (jelixOpenFile) {
			String separateur = File.separator;
			String dossier = separateur + this.currentProject.getName()
					+ separateur + jelixAppli + separateur + "modules" //$NON-NLS-1$
					+ separateur + jelixModule + separateur + "daos"; //$NON-NLS-1$
			;
			String fichier = jelixDao + ".dao.xml"; //$NON-NLS-1$
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			this.currentProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			IResource resource = root.findMember(new Path(dossier));
			if (!resource.exists() || !(resource instanceof IContainer)) {
				throwCoreException(Messages.WizardNewDao_OpeningFileThrowMsg
						+ fichier);
			}
			IContainer container = (IContainer) resource;
			final IFile file = container.getFile(new Path(fichier));

			if (file.exists()) {
				monitor.setTaskName(Messages.WizardNewDao_OpeningFile);
				JelixOpenPage.Open(this, file);

			} else {
				throwCoreException(Messages.WizardNewDao_OpeningFileThrowMsg
						+ fichier);
			}
		}
		monitor.worked(2);
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "jelixEclipse", IStatus.OK, //$NON-NLS-1$
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
	}
}