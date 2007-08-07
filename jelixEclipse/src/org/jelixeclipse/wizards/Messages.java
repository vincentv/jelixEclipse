package org.jelixeclipse.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jelixeclipse.wizards.messages"; //$NON-NLS-1$
	public static String WizardNewApp_Error;
	public static String WizardNewApp_OpeningFileTaskMsg;
	public static String WizardNewApp_OpeningFileThrowMsg;
	public static String WizardNewApp_ProjectCreationTaskMsg;
	public static String WizardNewApp_ProjectNameErrorMsg;
	public static String WizardNewDao_CreationOfMsg;
	public static String WizardNewDao_DAONameErrorMsg;
	public static String WizardNewDao_Error;
	public static String WizardNewDao_ModuleSelectionErrorMsg;
	public static String WizardNewDao_OpeningFile;
	public static String WizardNewDao_OpeningFileThrowMsg;
	public static String WizardNewDao_TableNameErrorMsg;
	public static String WizardNewJelixProject_CleanTaskMsg;
	public static String WizardNewJelixProject_DownloadingLibraryTaskMsg;
	public static String WizardNewJelixProject_LibraryDecompressionTaskMsg;
	public static String WizardNewJelixProject_MovingOfLibraryTaskMsg;
	public static String WizardNewJelixProject_ProjectCreation;
	public static String WizardNewModule_CreationOf;
	public static String WizardNewModule_Error;
	public static String WizardNewModule_ModuleNameErrorMsg;
	public static String WizardNewModule_OpeningFile;
	public static String WizardNewModule_OpeningFileThrowMsg;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
