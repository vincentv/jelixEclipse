/**
* @author      Ginesty Thibault, TOULOUSE (31), FRANCE
* @package     jelixeclipse.wizards
* @version     1.0
* @date        25/06/2007
* @link        http://www.jelix.org
* @licence     GNU General Public Licence see LICENCE file or http://www.gnu.org/licenses/gpl.html
*/

package jelixeclipse.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

import java.awt.*;
import java.io.File;


import jelixeclipse.Activator;
import jelixeclipse.preferences.PreferenceConstants;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (php).
 */

public class newDaoWizardPage extends WizardPage {


	private Text jelixTextDao;

	private Text jelixTextTable;

	private Combo jelixTextModule;
	//private Text jelixTextModule;
	
	private Button jelixOpenFile;

	private ISelection selection;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public newDaoWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("Nouveau DAO");
		setDescription("Cet assistant va g�n�rer un DAO");
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;

		Label label = new Label(container, SWT.NULL);
		label.setText("&Nom du module :");

		jelixTextModule = new Combo(container, SWT.READ_ONLY);

		// calcul du path jusqu'au repertoire des modules
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String cheminEnv = store
		.getString(PreferenceConstants.P_PATH_JELIX_SCRIPT.replace(
				"\\", "/"));
		if (!cheminEnv.endsWith("/")){
			cheminEnv += "/";
		}
		String myPath = cheminEnv + store.getString(PreferenceConstants.P_NAME_APP_JELIX);
		
		// listage des modules
		List ll = new List();
		File f = new File(myPath + "/modules");
		
		// on v�rifie que le chemin annonc� est valide
		if (!f.exists()){
			MessageDialog.openError(getShell(), "Erreur", "Erreur lors de la r�cup�ration des modules du projet \n \n" +
					"V�rifiez dans votre configuration \n" +
					"-- le nom de l'application JELIX \n" +
					"-- le chemin relatif du repertoire des modules \n" +
					"\n Window -> Preferences -> JELIX");
		}
		
		ll = this.listerRepertoire(f);
		for (int k=0; k<ll.getItemCount(); k++){
			jelixTextModule.add(ll.getItem(k).toString());
		}
		
		//jelixTextModule = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixTextModule.setLayoutData(gd);

		label = new Label(container, SWT.NULL);
		label.setText("Nom du &Dao :");

		jelixTextDao = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixTextDao.setLayoutData(gd);

		label = new Label(container, SWT.NULL);
		label.setText("Nom de la &Table:");

		jelixTextTable = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixTextTable.setLayoutData(gd);
		
		label = new Label(container, SWT.NULL);
		label.setText("&Ouvrir le fichier Dao :");
		jelixOpenFile = new Button(container, SWT.CHECK);
		jelixOpenFile.setSelection(true);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixOpenFile.setLayoutData(gd);

		initialize();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
			}
		}

	}

	public String getJelixTextModule() {
		return jelixTextModule.getText();
	}

	public String getJelixTextDao() {
		return jelixTextDao.getText();
	}

	public String getJelixTextTable() {
		return jelixTextTable.getText();
	}
	
	public Boolean getJelixOpenFile() {
		return jelixOpenFile.getSelection();
	}
	

	public List listerRepertoire(File repertoire) {
		File[] listefichiers;
		List listeModule = new List();
		
		int i;
		listefichiers = repertoire.listFiles();
		for (i = 0; i < listefichiers.length; i++) {
			if (listefichiers[i].isDirectory() == true) {
				listeModule.add(listefichiers[i].getName());
			}
		}
		
		return listeModule;
	}
}