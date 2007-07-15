package jelixeclipse.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.jface.preference.FileFieldEditor;


/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (php).
 */

public class newStructureJelixPage extends WizardPage {
	private Text containerText;

	private Text fileText;

	private ISelection selection;
	
	private Text jelixNomProjet;
	private Text jelixEmplacementProjet;
	private Button jelixImportSrc;
	private Button jelixImportSrcDownload;
	private Button jelixImportSrcLocal;
	private Button jelixImportSrcDownloadBerlios1;
	private Button jelixImportSrcDownloadBerlios2;
	
	private Group jelixPanneauDownload;
	private Group jelixPanneauLocal;

	private FileFieldEditor jelixLibrairiesLocal;
	
	private Button jelixBrowseLocal;
	
	private Composite container2;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public newStructureJelixPage(ISelection selection) {
		super("wizardPage");
		setTitle("Création d'un projet JELIX");
		setDescription("Cet assistant vous permet de créer un projet JELIX");
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		container.setLayoutData(gd);
		
		
		Composite entete = new Composite(container, SWT.NONE);
		entete.setBounds(new Rectangle(8, 10, 1000, 42));
		GridLayout lgEntete = new GridLayout();
		entete.setLayout(lgEntete);
		lgEntete.numColumns = 3;
		lgEntete.verticalSpacing = 9;
		gd = new GridData(GridData.FILL_HORIZONTAL);
		entete.setLayoutData(gd);
		

		/* Emplacement du projet */
		/*
		Label label = new Label(entete, SWT.NULL);
		label.setText("&Chemin projet:");
		
		jelixEmplacementProjet = new Text(entete, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixEmplacementProjet.setLayoutData(gd);
		
		Button button = new Button(entete, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		*/
		
		
		/* Nom du projet */
		Label label = new Label(entete, SWT.NULL);
		label.setText("&Nom du projet:");
		
		jelixNomProjet = new Text(entete, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixNomProjet.setLayoutData(gd);

		Label label2 = new Label(entete, SWT.NULL);
		label2.setText("");

				
		/* Import des sources */
		label = new Label(entete, SWT.NULL);
		label.setText("&Librairies JELIX : ");

		jelixImportSrc = new Button(entete, SWT.CHECK);
		jelixImportSrc.setSelection(true);
		jelixImportSrc.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				importSrc();
			}
		});
		gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixImportSrc.setLayoutData(gd);
		
		
		label = new Label(container, SWT.NULL);
		label.setText("");
		
		
		container2 = new Composite(container, SWT.NULL);
		GridLayout layout2 = new GridLayout();
		container2.setLayout(layout2);
		layout2.numColumns = 2;
		layout2.verticalSpacing = 9;		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		container2.setLayoutData(gd);
		
		/* Téléchargement */
		jelixImportSrcDownload = new Button(container2, SWT.RADIO);
		jelixImportSrcDownload.setSelection(true);
		jelixImportSrcDownload.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				importSrcMoyen();
			}
		});

		label = new Label(container2, SWT.NULL);
		label.setText("&Télechargement : ");
		
		label = new Label(container2, SWT.NULL);
		label.setText("");
		
		
		jelixPanneauDownload = new Group(container2, SWT.NONE);
		jelixPanneauDownload.setBounds(new Rectangle(8, 10, 1000, 42));
		GridLayout lg = new GridLayout();
		jelixPanneauDownload.setLayout(lg);
		lg.numColumns = 2;
		lg.verticalSpacing = 9;
		jelixPanneauDownload.setText(" Serveurs de téléchargement ");
		//gd = new GridData(GridData.FILL_HORIZONTAL);
		//jelixPanneauDownload.setLayoutData(gd);
		
		/* Sources de téléchargement */

		label = new Label(jelixPanneauDownload, SWT.NULL);
		label.setText("&Berlios 1 ");
		jelixImportSrcDownloadBerlios1 = new Button(jelixPanneauDownload, SWT.RADIO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixImportSrcDownloadBerlios1.setLayoutData(gd);
		
		label = new Label(jelixPanneauDownload, SWT.NULL);
		label.setText("&Berlios 2 ");
		jelixImportSrcDownloadBerlios2 = new Button(jelixPanneauDownload, SWT.RADIO);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixImportSrcDownloadBerlios2.setLayoutData(gd);

		
		/* disque local */
		
		jelixImportSrcLocal = new Button(container2, SWT.RADIO);
		//gd = new GridData(GridData.FILL_HORIZONTAL);
		//jelixImportSrcLocal.setLayoutData(gd);
		
		label = new Label(container2, SWT.NULL);
		label.setText("&Disque Local : ");
		
		label = new Label(container2, SWT.NULL);
		label.setText("");
		
		
		jelixPanneauLocal = new Group(container2, SWT.NONE);
		jelixPanneauLocal.setBounds(new Rectangle(8, 10, 1000, 42));
		GridLayout lgd = new GridLayout();
		jelixPanneauLocal.setLayout(lgd);
		lgd.numColumns = 2;
		lgd.verticalSpacing = 9;
		jelixPanneauLocal.setText(" Emplacement ");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		jelixPanneauLocal.setLayoutData(gd);
				
		jelixLibrairiesLocal = new FileFieldEditor("cheminJelixSrc", "",jelixPanneauLocal);

		initialize();
		setControl(container);
		importSrc();
		importSrcMoyen();
		//setControl(container2);
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

	
	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select new file container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				jelixEmplacementProjet.setText(((Path) result[0]).toString());
			}
		}
	}


	
	public void importSrc(){
		// Import du framework JELIX ?
		if (jelixImportSrc.getSelection()){
			container2.setVisible(true);
		}else{
			container2.setVisible(false);
		}
	}
	
	public void importSrcMoyen(){
		// moyen de récupération des sources
		if (jelixImportSrcDownload.getSelection()){
			// on rend non accessible le panneau "local"
			jelixPanneauLocal.setEnabled(false);
			jelixPanneauDownload.setEnabled(true);
		}else{
			jelixPanneauDownload.setEnabled(false);
			jelixPanneauLocal.setEnabled(true);
		}
	}
	
	public String getContainerName() {
		return containerText.getText();
	}

	public String getFileName() {
		return fileText.getText();
	}
	
	public String getProjetJelixNom(){
		return this.jelixNomProjet.getText();
	}
	
	public String getProjetJelixEmplacement(){
		return this.jelixEmplacementProjet.getText();
	}
	
	public Boolean getImportSource(){
		return this.jelixImportSrc.getSelection();
	}
	
	public Boolean getImportSourceDownload(){
		return this.jelixImportSrcDownload.getSelection();
	}
	
	public Boolean getImportSourceLocal(){
		return this.jelixImportSrcLocal.getSelection();
	}
	
	public Boolean getImportSourceDownloadBerlios1(){
		return this.jelixImportSrcDownloadBerlios1.getSelection();
	}
	
	public Boolean getImportSourceDownloadBerlios2(){
		return this.jelixImportSrcDownloadBerlios2.getSelection();
	}
	
	public String getImportSourceLocalFile(){
		return this.jelixLibrairiesLocal.getStringValue();
	}
	
}