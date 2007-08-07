package org.jelixeclipse.utils;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jelixeclipse.utils.messages"; //$NON-NLS-1$
	public static String JelixTools_DownloadingErrorMsg;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
