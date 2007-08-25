package org.jelixeclipse.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.eclipse.jface.preference.IPreferenceStore;

import java.io.File;
import org.eclipse.core.resources.*;

public class JelixShell {

	private String commande;
	private String erreur;
	private IPreferenceStore store;
	private String separateur = File.separator;
	private IProject project;

	/*
	 * Constructeur
	 */
	public JelixShell(IProject p, String cmd, IPreferenceStore s) {
		this.setCommande(cmd);
		this.setStore(s);
		this.setProject(p);
		this.setErreur(""); //$NON-NLS-1$
	}

	/*
	 * Getter/Setters
	 */
	public String getCommande() {
		return this.commande;
	}

	public String getErreur() {
		return this.erreur;
	}

	public IPreferenceStore getStore() {
		return this.store;
	}

	public String getSeparateur() {
		return this.separateur;
	}

	public IProject getProject() {
		return this.project;
	}

	public void setCommande(String c) {
		this.commande = c;
	}

	public void setErreur(String e) {
		this.erreur = e;
	}

	public void setStore(IPreferenceStore s) {
		this.store = s;
	}

	public void setSeparateur(String s) {
		this.separateur = s;
	}

	public void setProject(IProject p) {
		this.project = p;
	}

	/*
	 * Permet de lancer le shell
	 */
	public boolean play() {
		/* on recupere le chemin de php */
		String cheminPhp = JelixTools.getDefaultPhpExe();
		cheminPhp = this.verifFormatChemin(cheminPhp);

		/* on recupere le chemin du fichier jelix.php */
		String cheminScript = this.project.getLocation().toOSString();
		cheminScript = this.verifFormatChemin(cheminScript);
		if (!cheminScript.endsWith("/") && !cheminScript.endsWith("\\")) { //$NON-NLS-1$ //$NON-NLS-2$
			cheminScript += this.getSeparateur();
		}
		cheminScript += "lib" + this.getSeparateur() + "jelix-scripts" + this.getSeparateur() + "jelix.php"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		String cmd = this.getCommande();
		cmd = this.verifFormatChemin(cmd);

		/* on lance l'execution du script */
		try {
			String sc = "\"" + cheminPhp + "\" \"" + cheminScript + "\"" + cmd; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			Process p = Runtime.getRuntime().exec(sc);
			p.waitFor();

			// on recupere le retour du script
			InputStream out = new BufferedInputStream(p.getInputStream());
			byte[] b = new byte[1024];
			int n = out.read(b);

			// s'il y a un message, on le stocke
			if (n > 0) {
				String err = ""; //$NON-NLS-1$
				for (int i = 0; i < n; i++) {
					err += (char) b[i];
				}
				this.setErreur(err);
			}
		} catch (Exception e) {
			this.setErreur(e.getMessage());
		}
		return this.getErreur().equals(""); //$NON-NLS-1$
	}

	/*
	 * Reformate le chemin avec des guillements et les bons slash ou anti-slash
	 */
	public String verifFormatChemin(String c) {
		c.replace("/", this.getSeparateur()); //$NON-NLS-1$
		c.replace("\\", this.getSeparateur()); //$NON-NLS-1$
		return c;
	}

}
