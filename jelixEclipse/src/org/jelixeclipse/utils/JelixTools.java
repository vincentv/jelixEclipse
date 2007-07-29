/**
 * 
 */
package org.jelixeclipse.utils;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author vincent
 * 
 */
public class JelixTools {

	/**
	 * Télécharge un document
	 * 
	 * @param urlDocument
	 *            url du document à télécharger
	 * @param path
	 *            dossier ou enregistrer le document
	 */
	public static void download(String urlDocument, String path) {
		java.io.FileOutputStream destinationFile = null;
		try {
			URL url = new URL(urlDocument);
			java.io.File destination = new java.io.File(path
					+ new java.io.File(urlDocument).getName());

			destination.createNewFile();

			URLConnection connection = url.openConnection();
			connection.setDoOutput(false);
			destinationFile = new java.io.FileOutputStream(destination);

			java.io.InputStream is = url.openStream();

			byte[] buffer = new byte[512 * 1024];
			int reads;

			while ((reads = is.read(buffer)) != -1) {
				destinationFile.write(buffer, 0, reads);
			}
		} catch (java.io.IOException ioe) {
			System.out.println("I/O Error on Navigation.download()");
		} catch (Exception e) {
		} finally {
			try {
				destinationFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static IProject currentProject(ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection ss = (IStructuredSelection) selection;
		Object element = ss.getFirstElement();
		if (element instanceof IResource)
			return ((IResource) element).getProject();
		if (!(element instanceof IAdaptable))
			return null;
		IAdaptable adaptable = (IAdaptable) element;
		Object adapter = adaptable.getAdapter(IResource.class);
		return ((IResource) adapter).getProject();
	}
	

}
