/**
* @author      Ginesty Thibault, TOULOUSE (31), FRANCE
* @package     jelixeclipse.wizards
* @version     1.0
* @date        25/06/2007
* @link        http://www.jelix.org
* @licence     GNU General Public Licence see LICENCE file or http://www.gnu.org/licenses/gpl.html
*/

package jelixeclipse.wizards;

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

import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;


import jelixeclipse.Activator;
import jelixeclipse.preferences.PreferenceConstants;

import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
import java.io.*;

/**
 * This is a sample new wizard. Its role is to create a new file resource in the
 * provided container. If the container resource (a folder or a project) is
 * selected in the workspace when the wizard is opened, it will accept it as the
 * target container. The wizard creates one file with the extension "php". If a
 * sample multi-page editor (also available as a template) is registered for the
 * same extension, it will be able to open it.
 */

public class newModuleWizard extends Wizard implements INewWizard {
	private newModuleWizardPage page;

	private ISelection selection;
	
	private String erreur = "";

	/**
	 * Constructor for newModuleWizard.
	 */
	public newModuleWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new newModuleWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		final String jelixModule = page.getjelixTextModule();
		final Boolean jelixOpenFile = page.getJelixOpenFile();
		
		/* Verification saisie utilisateur */
		if (jelixModule.equals("")){
			MessageDialog.openError(getShell(), "Erreur", "Veuillez saisir un module");
			return false;
		}
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(jelixModule, jelixOpenFile, monitor);
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

	private void doFinish(String jelixModule, Boolean jelixOpenFile,
			IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Creation de " + jelixModule, 2);

		/* on récupère l'objet de preference */
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		/* on traite le chemin vers php et vers le repertoire de script */
		String cheminPhp = store
				.getString(PreferenceConstants.P_PATH_JELIX_PHP).replace("\\",
						"/");
		String cheminScript = store
				.getString(PreferenceConstants.P_PATH_JELIX_SCRIPT.replace(
						"\\", "/"));
		if (!cheminScript.endsWith("/")){
			cheminScript += "/";
		}
		cheminScript += "lib/jelix-scripts";
		
		String appli = store.getString(PreferenceConstants.P_NAME_APP_JELIX);
		String chemin = cheminPhp + " " + cheminScript + "/jelix.php";
		String cmd = " --" + appli + " createmodule " + jelixModule;
		
		/* on lance l'éxécution du script */
		try {
			Process p = Runtime.getRuntime().exec(chemin + cmd);
			p.waitFor();
			
			// on récupère le retour du script
		    InputStream out=new BufferedInputStream(p.getInputStream());
		    byte[] b=new byte[1024];
		    int    n=out.read(b);
		    
		    // s'il y a un message, on le stocke
		    if (n > 0){
		    	for (int i=0; i<n; i++){
		    		this.erreur += (char)b[i];
		    	}
		    }
		} catch (Exception e) {
			throwCoreException("Erreur lors de la creation \n Vérifiez dans votre configuration"
					+ "\n -- le chemin de l'executable Php  "
					+ "\n -- le nom de l'application JELIX "
					+ "\n -- le chemin du repertoire de script JELIX "
					+ "\n \n Window -> Preferences -> JELIX");
		}

		monitor.worked(1);
		
		/* Si message durant la génération, on l'affiche */
		if (!this.erreur.equals("")){
			throwCoreException("Erreur lors de la génération JELIX \n " + this.erreur);
		}

		if (jelixOpenFile) {
			/* on essaye d'ouvrir le fichier créé */

			String dossier = "";
			
			if (store.getString(PreferenceConstants.P_PATH_JELIX).endsWith("/") || store.getString(PreferenceConstants.P_PATH_JELIX).endsWith("\\")){
				dossier = store.getString(PreferenceConstants.P_PATH_JELIX) + appli + "/modules/" + jelixModule + "/controllers";
			}else{
				dossier = store.getString(PreferenceConstants.P_PATH_JELIX) + "/" + appli + "/modules/" + jelixModule + "/controllers";
			}
			
			String fichier = "default.classic.php";
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			
			/* on raffraichit le projet courant */			
			IFolder pp = root.getFolder(new Path(dossier));
			pp.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			
			IResource resource = root.findMember(new Path(dossier));

			if (!resource.exists() || !(resource instanceof IContainer)) {
				throwCoreException("Echec lors de l'ouverture du fichier ");
			}

			IContainer container = (IContainer) resource;
			final IFile file = container.getFile(new Path(fichier));
			
			/* on teste si le fichier est bien créé */
			if (file.exists()) {
				monitor.setTaskName("Ouverture du fichier ...");
				getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						IWorkbenchPage page = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage();
						try {
							IDE.openEditor(page, file, true);
							
						} catch (PartInitException e) {
						}
					}
				});
			} else {
				throwCoreException("Echec lors de l'ouverture du fichier ");
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
		this.selection = selection;
	}

}