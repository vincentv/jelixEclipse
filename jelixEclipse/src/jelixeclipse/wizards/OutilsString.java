package jelixeclipse.wizards;

import sun.text.Normalizer;

/**
 * Utilitaires pour les chaines de caractères.
 * @author iubito (Sylvain Machefert)
 */
public class OutilsString {

    /**
    * Enlève les accents d'une chaîne, en les remplaçant par le caractères ASCII
    * sans accent correspondant.
    *
    * <p>Les symboles <tt>² ° ¨ £ µ § ¤</tt> sont supprimés.
    * @param s
    */
    static public String sansAccents(String s) {
        return Normalizer.normalize(s, Normalizer.DECOMP, 0).replaceAll("[^\\p{ASCII}]", "");
    }

    /**
    * Retourne la chaîne {@link String#trim() trim()ée}, ou chaîne vide <tt>""</tt> si null.
    * @param s
    */
    static public String trimNull(String s) {
        return (s == null) ? "" : s.trim();
    }

    /**
    * Formate un numéro de téléphone en séparant les chiffres par des points.
    * <p>Si le numéro comporte des caractères autres que des chiffres, + et séparateurs,
    * retourne le numéro inchangé.
    * <p>Si le numéro est égal à 0000000000 alors retourne une chaîne vide.
    * <p>conserve la position des séparateur autres que le point (-, espace, /, virgule)
    * en les remplaçant par des '.'
    * <p>Si aucun séparateur, découpe en groupe de 2 chiffres.
    * <p>si préfixé par +33, met 0 (ça reste en France), sinon met 00 à la place du +,
    * gère les parenthèses dans (+33)471...
    * @param Numero
    * @return le numéro formaté
    */
    static public String FormatTel(String Numero) {
        if (Numero == null)
            return "";
        if (!Numero.matches("^[0-9 ,/.\\+\\-\\(\\)]*$"))
            return Numero;
        Numero = Numero.trim();
        if (Numero.matches("^0*$"))
            return "";
        //Ici on a un numéro contenant des chiffres, séparateurs ou +.
        Numero = formatTelRemove0033(Numero);
        if (!Numero.matches("^[0-9]*$")) {
            //Si contient des séparateurs... (caractères non chiffres)
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
    * Formate un numéro de tel/fax sans les séparateurs,
    * en supprimant tous les caractères autres que les chiffres.
    * @param numfax
    * @return le numéro contenant que des chiffres.
    */
    public static String formatTelSansPoints(String num) {
        num = num.trim().replaceAll("[^0-9\\+]", "");
        if (num.matches("^0*$"))
            num = "";
        return num;
    }

    /**
    * Converti le '+' en 00, gère les éventuelles parenthèses (+33) 4...
    * <p>Enlève les 0033 au début d'un numéro français s'il a plus de 10 caractères.
    * <p>0033471... => 0471...
    * <p>+40 123... => 0040 123...
    * @param rs
    * @param Zone
    * @return le numéro sans le préfixe international si français (0033)
    */
    public static String formatTelRemove0033(String num) {
        num = num.replaceFirst("^(\\(|\\+)+ *", "00");
        //Si numéro français
        if (num.matches("^0*33.*$")
            && num.length() > 10) { //Enlève les 0033..., 033..., 33...
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
    * Renvoie true si le numéro de téléphone passé en paramètre est valide.
    * <p>Un numéro est valide s'il contient uniquement des chiffres et des points.
    * @param num
    */
    static public boolean isNumeroTelValide(String num) {
        String s = FormatTel(num);
        return ((s.length() > 0) && s.matches("^[0-9\\.]*$"));
    }
}
