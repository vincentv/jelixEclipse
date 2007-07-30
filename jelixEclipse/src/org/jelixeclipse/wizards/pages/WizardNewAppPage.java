/**
 * @author      Ginesty Thibault, TOULOUSE (31), FRANCE
 * @package     org.jelixeclipse.wizards
 * @version     0.0.3
 * @date        25/06/2007
 * @link        http://www.jelix.org
 * @licence     GNU General Public Licence see LICENCE file or http://www.gnu.org/licenses/gpl.html
 */

package org.jelixeclipse.wizards.pages;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardNewAppPage extends WizardPage {

	private Group jelixPanneauMysql;
	private Button jelixOpenConfigMysql;
	private Button jelixCheckMysqlPersistant;
	private Text jelixTextApplication;
	private Text jelixTextMysqlHost;
	private Text jelixTextMysqlPort;
	private Text jelixTextMysqlBd;
	private Text jelixTextMysqlUser;
	private Text jelixTextMysqlPwd;
	private Text jelixTextMysqlNomConn;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public WizardNewAppPage(ISelection selection) {
		super("wizardPage");
		setTitle("Nouvelle Application");
		setDescription("Cet assistant va generer pour vous le squelette d'une application JELIX");
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

		/* Nom de l'application */
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

		// groupe mysql
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
		jelixTextMysqlNomConn = new Text(jelixPanneauMysql, SWT.BORDER
				| SWT.SINGLE);
		GridData ggd = new GridData(GridData.FILL_HORIZONTAL);
		jelixTextMysqlNomConn.setLayoutData(ggd);
		jelixTextMysqlNomConn.setText("myappli");

		// hote
		label = new Label(jelixPanneauMysql, SWT.NONE);
		label.setText("H�te :");
		jelixTextMysqlHost = new Text(jelixPanneauMysql, SWT.BORDER
				| SWT.SINGLE);
		ggd = new GridData(GridData.FILL_HORIZONTAL);
		jelixTextMysqlHost.setLayoutData(ggd);
		jelixTextMysqlHost.setText("localhost");

		// port
		label = new Label(jelixPanneauMysql, SWT.NONE);
		label.setText("Port :");
		jelixTextMysqlPort = new Text(jelixPanneauMysql, SWT.BORDER
				| SWT.SINGLE);
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
		jelixTextMysqlUser = new Text(jelixPanneauMysql, SWT.BORDER
				| SWT.SINGLE);
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

		configMysql();
		setControl(container);
	}

	/*
	 * Affiche ou cache le panneau MySQL
	 */
	private void configMysql() {
		if (jelixOpenConfigMysql.getSelection() == true) {
			jelixPanneauMysql.setVisible(true);
		} else {
			jelixPanneauMysql.setVisible(false);
		}
	}

	public String getJelixTextApplication() {
		return jelixTextApplication.getText();
	}

	public Boolean getJelixMysqlConf() {
		return jelixOpenConfigMysql.getSelection();
	}

	public String getJelixMysqlNomConn() {
		return jelixTextMysqlNomConn.getText();
	}

	public String getJelixMysqlHost() {
		return jelixTextMysqlHost.getText();
	}

	public String getJelixMysqlPort() {
		return jelixTextMysqlPort.getText();
	}

	public String getJelixMysqlDb() {
		return jelixTextMysqlBd.getText();
	}

	public String getJelixMysqlUser() {
		return jelixTextMysqlUser.getText();
	}

	public String getJelixMysqlPwd() {
		return jelixTextMysqlPwd.getText();
	}

	public Boolean getJelixMysqlPersistance() {
		return jelixCheckMysqlPersistant.getSelection();
	}

}