package org.jelixeclipse.utils;

public class JelixIni {
	private String entete = ""; //$NON-NLS-1$
	private String definition = ""; //$NON-NLS-1$
	private String commentaire = ""; //$NON-NLS-1$
	private String pdo = ""; //$NON-NLS-1$
	private String cr = "\r\n"; //$NON-NLS-1$

	/* constructeur */
	public JelixIni() {
		// entete
		this.entete = ";<?php die(''); ?>" + this.cr; //$NON-NLS-1$
		this.entete += ";for security reasons, don't remove or modify the first line" + this.cr; //$NON-NLS-1$
		this.entete += this.cr;

		// definition
		this.definition = "; nom de la connexion utilis�e par d�faut" + this.cr; //$NON-NLS-1$
		this.definition += "default = "; //$NON-NLS-1$

		// commentaire
		this.commentaire = "; chaque section correspond � une connexion" + this.cr; //$NON-NLS-1$
		this.commentaire += "; le nom de la section est le nom de la connexion utilis� dans jDb et jDao" + this.cr; //$NON-NLS-1$
		this.commentaire += "; la liste des param�tres d�pend du driver." + this.cr; //$NON-NLS-1$
		this.commentaire += this.cr;

		// pdo
		this.pdo = "; pour pdo :" + this.cr; //$NON-NLS-1$
		this.pdo += ";driver=pdo" + this.cr; //$NON-NLS-1$
		this.pdo += ";dsn=mysql:host=localhost;dbname=test" + this.cr; //$NON-NLS-1$
		this.pdo += ";user=" + this.cr; //$NON-NLS-1$
		this.pdo += ";password="; //$NON-NLS-1$
	}

	public String getEntete() {
		return this.entete;
	}

	public String getDefinition(String nomConnexion) {
		return this.definition + nomConnexion + this.cr + this.cr;
	}

	public String getCommentaire() {
		return this.commentaire;
	}

	public String getConnexion(String nomConnexion, String host, String db,
			String user, String pwd, Boolean persistent) {
		String res = ""; //$NON-NLS-1$

		res = "[" + nomConnexion + "]" + this.cr; //$NON-NLS-1$ //$NON-NLS-2$
		res += "; pour la plupart des drivers :" + this.cr; //$NON-NLS-1$
		res += "driver=\"mysql\"" + this.cr; //$NON-NLS-1$
		res += "database=\"" + db + "\"" + this.cr; //$NON-NLS-1$ //$NON-NLS-2$
		res += "host=\"" + host + "\"" + this.cr; //$NON-NLS-1$ //$NON-NLS-2$
		res += "user=\"" + user + "\"" + this.cr; //$NON-NLS-1$ //$NON-NLS-2$
		res += "password=\"" + pwd + "\"" + this.cr; //$NON-NLS-1$ //$NON-NLS-2$
		if (persistent) {
			res += "persistent= on" + this.cr; //$NON-NLS-1$
		} else {
			res += "persistent= off" + this.cr; //$NON-NLS-1$
		}
		res += this.cr;

		return res;
	}

	public String getPdo() {
		return this.pdo;
	}

}
