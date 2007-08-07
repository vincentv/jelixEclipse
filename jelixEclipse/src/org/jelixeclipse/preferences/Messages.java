package org.jelixeclipse.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jelixeclipse.preferences.messages"; //$NON-NLS-1$
	public static String JelixPreferencePage_JelixVersion;
	public static String JelixPreferencePage_MysqlPath;
	public static String JelixPreferencePage_PHPPath;
	public static String PreferenceConstants_JelixDefaultVersion;
	public static String PreferenceConstants_MySQLDefaultPath;
	public static String PreferenceConstants_PHPDefaultPath;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
