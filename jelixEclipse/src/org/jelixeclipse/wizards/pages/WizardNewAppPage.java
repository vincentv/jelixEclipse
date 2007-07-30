/**
* @author      Ginesty Thibault, TOULOUSE (31), FRANCE
* @package     jelixeclipse.wizards
* @version     1.0
* @date        25/06/2007
* @link        http://www.jelix.org
* @licence     GNU General Public Licence see LICENCE file or http://www.gnu.org/licenses/gpl.html
*/

package org.jelixeclipse.wizards.pages;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.graphics.Rectangle;


/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (php).
 */

public class WizardNewAppPage extends WizardPage {
	
	private Text jelixTextApplication;

	private ISelection selection;
	
	private Button jelixOpenConfigMysql;	
	private Group jelixPanneauMysql;
	private Text jelixTextMysqlHost;
	private Text jelixTextMysqlPort;
	private Text jelixTextMysqlBd;
	private Text jelixTextMysqlUser;
	private Text jelixTextMysqlPwd;
	private Text jelixTextMysqlNomConn;
	private Button jelixCheckMysqlPersistant;
	

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public WizardNewAppPage(ISelection selection) {
		super("wizardPage");
		setTitle("Nouvelle Application");
		setDescription("Cet assistant va g�n�rer pour vous le squelette d'une application JELIX");
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
		label.setText("&Nom de l'application :");

		jelixTextApplication = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixTextApplication.setLayoutData(gd);
		
		// checkbox panneau mysql
		label = new Label(container, SWT.NULL);
		label.setText("&Connexion MySQL :");
		jelixOpenConfigMysql = new Button(container, SWT.CHECK);
		jelixOpenConfigMysql.setSelection(false);
		jelixOpenConfigMysql.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				configMysql();
			}
		});
		
		// espace
		label = new Label(container, SWT.NULL);
		label.setText("");
		label = new Label(container, SWT.NULL);
		label.setText("");		
		
		//groupe mysql	
		label = new Label(container, SWT.NULL);
		label.setText("");
		jelixPanneauMysql = new Group(container, SWT.NONE);
		jelixPanneauMysql.setBounds(new Rectangle(8, 10, 1000, 42));
		GridLayout lg = new GridLayout();
		jelixPanneauMysql.setLayout(lg);
		lg.numColumns = 2;
		lg.verticalSpacing = 9;
		jelixPanneauMysql.setText(" Param�tres MySQL ");
		
			// nom de la connexion
			label = new Label(jelixPanneauMysql, SWT.NONE);
			label.setText("Nom de la connexion :");
			jelixTextMysqlNomConn = new Text(jelixPanneauMysql, SWT.BORDER | SWT.SINGLE);
			GridData ggd = new GridData(GridData.FILL_HORIZONTAL);
			jelixTextMysqlNomConn.setLayoutData(ggd);
			jelixTextMysqlNomConn.setText("myappli");
		
			// hote
			label = new Label(jelixPanneauMysql, SWT.NONE);
			label.setText("H�te :");
			jelixTextMysqlHost = new Text(jelixPanneauMysql, SWT.BORDER | SWT.SINGLE);
			ggd = new GridData(GridData.FILL_HORIZONTAL);
			jelixTextMysqlHost.setLayoutData(ggd);
			jelixTextMysqlHost.setText("localhost");
			
			// port
			label = new Label(jelixPanneauMysql, SWT.NONE);
			label.setText("Port :");
			jelixTextMysqlPort = new Text(jelixPanneauMysql, SWT.BORDER | SWT.SINGLE);
			ggd = new GridData(GridData.FILL_HORIZONTAL);
			jelixTextMysqlPort.setLayoutData(ggd);
			jelixTextMysqlPort.setText("3306");
			
			// nom de la base
			label = new Label(jelixPanneauMysql, SWT.NONE);
			label.setText("Base de donn�es :");
			jelixTextMysqlBd = new Text(jelixPanneauMysql, SWT.BORDER | SWT.SINGLE);
			ggd = new GridData(GridData.FILL_HORIZONTAL);
			jelixTextMysqlBd.setLayoutData(ggd);
			jelixTextMysqlBd.setText("jelix");
			
			// user
			label = new Label(jelixPanneauMysql, SWT.NONE);
			label.setText("Utilisateur :");
			jelixTextMysqlUser = new Text(jelixPanneauMysql, SWT.BORDER | SWT.SINGLE);
			ggd = new GridData(GridData.FILL_HORIZONTAL);
			jelixTextMysqlUser.setLayoutData(ggd);
			
			// password
			label = new Label(jelixPanneauMysql, SWT.NONE);
			label.setText("Mot de passe :");
			jelixTextMysqlPwd = new Text(jelixPanneauMysql, SWT.BORDER | SWT.SINGLE);
			ggd = new GridData(GridData.FILL_HORIZONTAL);
			jelixTextMysqlPwd.setLayoutData(ggd);
			
			// persistence
			label = new Label(jelixPanneauMysql, SWT.NONE);
			label.setText("Connexion persistante :");
			jelixCheckMysqlPersistant = new Button(jelixPanneauMysql, SWT.CHECK);
			jelixCheckMysqlPersistant.setSelection(true);
			ggd = new GridData(GridData.FILL_HORIZONTAL);
			jelixCheckMysqlPersistant.setLayoutData(ggd);
			
			// on attache le groupe au panneau
			ggd = new GridData(GridData.FILL_HORIZONTAL);
			jelixPanneauMysql.setLayoutData(ggd);
			
		initialize();
		configMysql();
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
	
	/*
	 * 
	 */
	private void configMysql(){
		if (jelixOpenConfigMysql.getSelection() == true){
			// affichage du panneau mysql
			jelixPanneauMysql.setVisible(true);
			
		}else{
			// on rend invisible le panneau mysql
			jelixPanneauMysql.setVisible(false);
		}
	}
	
	public String getJelixTextApplication() {
		return jelixTextApplication.getText();
	}
	
	public Boolean getJelixMysqlConf(){
		return jelixOpenConfigMysql.getSelection();
	}
	
	public String getJelixMysqlNomConn(){
		return jelixTextMysqlNomConn.getText();
	}
	
	public String getJelixMysqlHost(){
		return jelixTextMysqlHost.getText();
	}
	
	public String getJelixMysqlPort(){
		return jelixTextMysqlPort.getText();
	}
	
	public String getJelixMysqlDb(){
		return jelixTextMysqlBd.getText();
	}
	
	public String getJelixMysqlUser(){
		return jelixTextMysqlUser.getText();
	}
	
	public String getJelixMysqlPwd(){
		return jelixTextMysqlPwd.getText();
	}
	
	public Boolean getJelixMysqlPersistance(){
		return jelixCheckMysqlPersistant.getSelection();
	}
	
	
}