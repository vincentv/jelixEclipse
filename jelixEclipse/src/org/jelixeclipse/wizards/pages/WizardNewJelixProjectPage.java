/**
 * 
 */
package org.jelixeclipse.wizards.pages;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * @author vincent
 * 
 */
public class WizardNewJelixProjectPage extends WizardNewProjectCreationPage {

	private Button jelixImportationButton;
	private Composite jelixImportGroup;

	private Button jelixDownloadButton;
	private Group jelixDownloadGroup;
	private Button jelixDownloadSrcBerlios1;
	private Button jelixDownloadSrcBerlios2;

	private Button jelixLocalButton;
	private Group jelixLocalGroup;
	private FileFieldEditor jelixLibrairiesLocal;

	public WizardNewJelixProjectPage(String pageName) {
		super(pageName);
		setTitle(Messages.WizardNewJelixProjectPage_Title);
		setDescription(Messages.WizardNewJelixProjectPage_Description);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		Composite composite = (Composite) super.getControl();

		createOptions(composite);

		setControl(composite);
	}

	/**
	 * Fonction pour créer les options afficher sur la page de création du
	 * projet
	 * 
	 * @param parent
	 */
	private void createOptions(Composite parent) {

		// Importation librairie jelix
		jelixImportationButton = new Button(parent, SWT.CHECK);
		jelixImportationButton
				.setText(Messages.WizardNewJelixProjectPage_JelixLibraryCheckText);
		jelixImportationButton.setSelection(true);
		jelixImportationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				importSrc();
			}
		});

		jelixImportGroup = new Composite(parent, SWT.NULL);
		GridLayout layout2 = new GridLayout();
		jelixImportGroup.setLayout(layout2);
		layout2.numColumns = 1;
		layout2.verticalSpacing = 9;
		jelixImportGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Telechargement de Jelix
		jelixDownloadButton = new Button(jelixImportGroup, SWT.RADIO);
		jelixDownloadButton
				.setText(Messages.WizardNewJelixProjectPage_DownloadingRadioText);
		jelixDownloadButton.setSelection(true);
		jelixDownloadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				importSrcMoyen();
			}
		});

		jelixDownloadGroup = new Group(jelixImportGroup, SWT.NONE);
		jelixDownloadGroup.setLayout(new GridLayout());
		jelixDownloadGroup
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Création des check box pour le téléchargement
		jelixDownloadSrcBerlios1 = new Button(jelixDownloadGroup, SWT.RADIO);
		jelixDownloadSrcBerlios1
				.setText(Messages.WizardNewJelixProjectPage_Berlios1RadioText);
		jelixDownloadSrcBerlios1.setSelection(true);
		jelixDownloadSrcBerlios1.setSelection(false);

		jelixDownloadSrcBerlios2 = new Button(jelixDownloadGroup, SWT.RADIO);
		jelixDownloadSrcBerlios2
				.setText(Messages.WizardNewJelixProjectPage_Berlios2RadioText);

		// Récupération sur le disque local
		// Telechargement de Jelix
		jelixLocalButton = new Button(jelixImportGroup, SWT.RADIO);
		jelixLocalButton
				.setText(Messages.WizardNewJelixProjectPage_LocalDiskRadioText);
		jelixLocalButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				importSrcMoyen();
			}
		});

		jelixLocalGroup = new Group(jelixImportGroup, SWT.NONE);
		jelixLocalGroup.setLayout(new GridLayout());
		jelixLocalGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Emplacement sur le disque local
		jelixLibrairiesLocal = new FileFieldEditor(
				Messages.WizardNewJelixProjectPage_JelixPathExample, "",
				jelixLocalGroup);

		importSrc();
		importSrcMoyen();
	}

	public void importSrc() {
		// Import du framework JELIX ?
		if (jelixImportationButton.getSelection()) {
			jelixImportGroup.setVisible(true);
		} else {
			jelixImportGroup.setVisible(false);
		}
	}

	public void importSrcMoyen() {
		// moyen de récupération des sources
		if (jelixDownloadButton.getSelection()) {
			// on rend non accessible le panneau "local"
			jelixDownloadGroup.setEnabled(true);
			jelixLocalGroup.setEnabled(false);

		} else if (jelixLocalButton.getSelection()) {
			jelixDownloadGroup.setEnabled(false);
			jelixLocalGroup.setEnabled(true);

		} else {
			jelixDownloadGroup.setEnabled(false);
			jelixLocalGroup.setEnabled(false);
		}
	}

	public boolean getJelixImportationButton() {
		return jelixImportationButton.getSelection();
	}

	public boolean getJelixDownloadButton() {
		return jelixDownloadButton.getSelection();
	}

	public boolean getJelixLocalButton() {
		return jelixLocalButton.getSelection();
	}

	public boolean getJelixImportSrcDownloadBerlios1() {
		return jelixDownloadSrcBerlios1.getSelection();
	}

	public boolean getJelixImportSrcDownloadBerlios2() {
		return jelixDownloadSrcBerlios2.getSelection();
	}

	public String getJelixLibrairiesLocal() {
		return jelixLibrairiesLocal.getStringValue();
	}

}
