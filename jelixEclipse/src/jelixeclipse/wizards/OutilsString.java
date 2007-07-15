package jelixeclipse.wizards;

import sun.text.Normalizer;

/**
 * Utilitaires pour les chaines de caract�res.
 * @author iubito (Sylvain Machefert)
 */
public class OutilsString {

    /**
    * Enl�ve les accents d'une cha�ne, en les rempla�ant par le caract�res ASCII
    * sans accent correspondant.
    *
    * <p>Les symboles <tt>� � � � � � �</tt> sont supprim�s.
    * @param s
    */
    static public String sansAccents(String s) {
        return Normalizer.normalize(s, Normalizer.DECOMP, 0).replaceAll("[^\\p{ASCII}]", "");
    }

    /**
    * Retourne la cha�ne {@link String#trim() trim()�e}, ou cha�ne vide <tt>""</tt> si null.
    * @param s
    */
    static public String trimNull(String s) {
        return (s == null) ? "" : s.trim();
    }

    /**
    * Formate un num�ro de t�l�phone en s�parant les chiffres par des points.
    * <p>Si le num�ro comporte des caract�res autres que des chiffres, + et s�parateurs,
    * retourne le num�ro inchang�.
    * <p>Si le num�ro est �gal � 0000000000 alors retourne une cha�ne vide.
    * <p>conserve la position des s�parateur autres que le point (-, espace, /, virgule)
    * en les rempla�ant par des '.'
    * <p>Si aucun s�parateur, d�coupe en groupe de 2 chiffres.
    * <p>si pr�fix� par +33, met 0 (�a reste en France), sinon met 00 � la place du +,
    * g�re les parenth�ses dans (+33)471...
    * @param Numero
    * @return le num�ro format�
    */
    static public String FormatTel(String Numero) {
        if (Numero == null)
            return "";
        if (!Numero.matches("^[0-9 ,/.\\+\\-\\(\\)]*$"))
            return Numero;
        Numero = Numero.trim();
        if (Numero.matches("^0*$"))
            return "";
        //Ici on a un num�ro contenant des chiffres, s�parateurs ou +.
        Numero = formatTelRemove0033(Numero);
        if (!Numero.matches("^[0-9]*$")) {
            //Si contient des s�parateurs... (caract�res non chiffres)
            Numero = Numero.replaceAll("[^0-9]+", ".");
            return Numero;
        }
        if (Numero.matches("^[0-9]*$") && (Numero.length() == 10)) {
            StringBuffer r = new StringBuffer();
            for (int i = 0, j = Numero.length(); i < j; i++) {
                r.append(Numero.charAt(i));
                if ((i % 2 == 1) && (i < (j - 1)))
                    r.append('.');
            }
            return r.toString();
        }
        return Numero;
    }

    /**
    * Formate un num�ro de tel/fax sans les s�parateurs,
    * en supprimant tous les caract�res autres que les chiffres.
    * @param numfax
    * @return le num�ro contenant que des chiffres.
    */
    public static String formatTelSansPoints(String num) {
        num = num.trim().replaceAll("[^0-9\\+]", "");
        if (num.matches("^0*$"))
            num = "";
        return num;
    }

    /**
    * Converti le '+' en 00, g�re les �ventuelles parenth�ses (+33) 4...
    * <p>Enl�ve les 0033 au d�but d'un num�ro fran�ais s'il a plus de 10 caract�res.
    * <p>0033471... => 0471...
    * <p>+40 123... => 0040 123...
    * @param rs
    * @param Zone
    * @return le num�ro sans le pr�fixe international si fran�ais (0033)
    */
    public static String formatTelRemove0033(String num) {
        num = num.replaceFirst("^(\\(|\\+)+ *", "00");
        //Si num�ro fran�ais
        if (num.matches("^0*33.*$")
            && num.length() > 10) { //Enl�ve les 0033..., 033..., 33...
            num = num.replaceFirst("^0*33 *\\)? *", "");
            if (num.charAt(0) != '0')
                //Si 0*33 4 71..., remet un 0 devant -> 04 71...
                num = "0" + num;
        }
        else
            num = num.replaceFirst("\\)", "");
        return num;
    }

    /**
    * Renvoie true si le num�ro de t�l�phone pass� en param�tre est valide.
    * <p>Un num�ro est valide s'il contient uniquement des chiffres et des points.
    * @param num
    */
    static public boolean isNumeroTelValide(String num) {
        String s = FormatTel(num);
        return ((s.length() > 0) && s.matches("^[0-9\\.]*$"));
    }
}
