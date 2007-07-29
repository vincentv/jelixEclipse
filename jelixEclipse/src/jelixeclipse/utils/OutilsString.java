package jelixeclipse.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;
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
        return Normalizer.normalize(s, Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    /**
    * Retourne la cha�ne {@link String#trim() trim()�e}, ou cha�ne vide <tt>""</tt> si null.
    * @param s
    */
    static public String trimNull(String s) {
        return (s == null) ? "" : s.trim();
    }
}
