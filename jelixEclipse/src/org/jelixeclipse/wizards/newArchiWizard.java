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

import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;


import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
import org.jelixeclipse.Activator;
import org.jelixeclipse.preferences.PreferenceConstants;
import org.jelixeclipse.utils.*;
import org.jelixeclipse.wizards.JelixIni;
import org.jelixeclipse.wizards.pages.newArchiWizardPage;

import java.io.*;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "php". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class newArchiWizard extends Wizard implements INewWizard {
	private newArchiWizardPage page;
	private ISelection selection;
	private String erreur = "";
	private Boolean mysqlConf = false;
	private String mysqlNomConn = "";
	private String mysqlHost = "";
	private String mysqlPort = "";
	private String mysqlDb = "";
	private String mysqlUser = "";
	private String mysqlPwd = "";
	private Boolean mysqlPersistence = false;

	/**
	 * Constructor for newArchiWizard.
	 */
	public newArchiWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new newArchiWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {

		final String jelixApplication = page.getJelixTextApplication();
		this.mysqlConf = page.getJelixMysqlConf();
		this.mysqlNomConn = page.getJelixMysqlNomConn();
		this.mysqlHost = page.getJelixMysqlHost();
		this.mysqlPort = page.getJelixMysqlPort();
		this.mysqlDb = page.getJelixMysqlDb();
		this.mysqlUser = page.getJelixMysqlUser();
		this.mysqlPwd = page.getJelixMysqlPwd();
		this.mysqlPersistence = page.getJelixMysqlPersistance();
		
		/* Verification saisie utilisateur */
		if (jelixApplication.equals("")){
			MessageDialog.openError(getShell(), "Erreur", "Veuillez saisir le nom d'un projet");
			return false;
		}
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(jelixApplication, monitor);
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
			MessageDialog.openError(getShell(), "Erreur", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
		String jelixApplication,
		IProgressMonitor monitor)
		throws CoreException {
		monitor.beginTask("Creation du projet " + jelixApplication, 2);
		
		/* on recupere l'objet de preference */
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		/* on prepare la commande jelix */
		String cmd = " --" + jelixApplication + " createapp ";
		
		/* creation et lancement du shell jelix */
		org.jelixeclipse.utils.JelixShell js = new JelixShell(cmd, store);
		Boolean res = js.play();
		if (!res){
			throwCoreException(js.getErreur());
		}
						
		monitor.worked(1);
				
		/* Preparation a l'ouverture du fichier de propriete */
		String dossier = "";
		String separateur = js.getSeparateur();
		String appli = jelixApplication;
		String fichier =   "application.init.php";
		if (store.getString(PreferenceConstants.P_PATH_JELIX).endsWith("/") || store.getString(PreferenceConstants.P_PATH_JELIX).endsWith("\\")){
			dossier = store.getString(PreferenceConstants.P_PATH_JELIX) + appli + separateur;
		}else{
			dossier = store.getString(PreferenceConstants.P_PATH_JELIX) + separateur + appli + separateur;
		}
		
		// on raffraichit le projet courant
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject pp = root.getProject(store.getString(PreferenceConstants.P_PATH_JELIX));
		pp.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		
		
		IResource resource = root.findMember(new Path(dossier));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Echec lors de l'ouverture du fichier ");
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fichier));
		
		/* On teste si le fichier est bien cree */
		if (file.exists()){
		
		/* Si les param�tres de connexions MySQL sont d�finis, 
		 * on valorise le fichier dbprofil.ini.php*/
		
		//final IFile fileDb = container.getFile(new Path("var/config/dbprofils.ini.php"));
		final IFile fileDb = container.getFile(new Path("var" + separateur + "config" + separateur + "dbprofils.ini.php"));
		if (fileDb.exists()){
			// on traite le fichier
			this.valoriserDbProfil(fileDb);
		}
		
		/* on essaye d'ouvrir le fichier cree */
		monitor.setTaskName("Ouverture du fichier ...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		}else{
			throwCoreException("Echec lors de l'ouverture du fichier ");
		}
		
		// on stocke le nom de l'application
		store.setValue(PreferenceConstants.P_NAME_APP_JELIX, jelixApplication);
		
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
	
	private void valoriserDbProfil(IFile f){
		if (this.mysqlConf){
			// on instancie l'objet template ini
			org.jelixeclipse.wizards.JelixIni template = new JelixIni();
			try{
				FileOutputStream fout = new FileOutputStream(f.getLocation().toOSString());
				
				String contenu = "";
				contenu = template.getEntete(); // entete
				contenu += template.getCommentaire(); // commentaire
				contenu += template.getDefinition(this.mysqlNomConn); // definition connexion
				contenu += template.getConnexion(this.mysqlNomConn,
						this.mysqlHost,
						this.mysqlDb,
						this.mysqlUser,
						this.mysqlPwd,
						this.mysqlPersistence); // connexion
				contenu += template.getPdo(); //pdo
				
				// ecriture
				fout.write(contenu.getBytes());				
				f.refreshLocal(IResource.DEPTH_INFINITE, null);

			}catch(Exception e){

			}
		}
	}
	
	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "jelixEclipse", IStatus.OK, message, null);
		throw new CoreException(status);
	}
	
}