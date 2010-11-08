package py.com.fpuna.compiladores.analizadorlexico;

import py.com.fpuna.compiladores.exceptions.LexicalError;

/**
 *
 * @author markos
 */
public class Lexico {
    // expresion a analizar
    private StringBuffer regex;
    // conjunto de simbolos posibles
    private Alfabeto alphabet;
    // forma parte de la tabla de simbolos
    private String specials;

    public Lexico(String regex, String alfabeto) {
        this.regex = new StringBuffer(regex);
        this.alphabet = new Alfabeto(alfabeto);
        this.specials = "*+?|()";
    }


    /**
     * Consume la entrada y devuelve el siguiente a procesar. Si no se trata de
     * un token que pertenezca al alfabeto, entonces se lanza una Excepción.
     * <br><br>
     *
     * @return El siguiente caracter de la expresión regular
     * @throws java.lang.Exception Se lanza una excepción si el siguiente símbolo
     *                             no pertenece al alfabeto o a alguno de los
     *                             símbolos conocidos
     */
    public Token next() throws LexicalError {
        String s = consume();
        Token siguiente;

        if (s.equalsIgnoreCase(" ") || s.equalsIgnoreCase("\t")) {
            // Los espacios y tabuladores se ignoran
            siguiente = next();         

        } else if (this.specials.indexOf(s) >= 0 || this.alphabet.contiene(s) ||
                s.length() == 0) {
            siguiente = new Token(s);   

        } else {
            String except = "Simbolo no valido " + s;
            throw new LexicalError(except);
        }

        return siguiente;
    }

    /**
     * Extrae la primera letra de la regex y la devuelve como un String.
     *
     * @return El siguiente caracter en la regex
     */
    private String consume() {

        String consumido = "";
        if (this.regex.length() > 0) {
            consumido = Character.toString(this.regex.charAt(0));
            this.regex.deleteCharAt(0);
        }

        return consumido;
    }

    /**
     * 
     * @return alfabeto analizado
     */
    public Alfabeto getAlfabeto() {
        return alphabet;
    }

    /**
     * @return regex analizada
     */
    public StringBuffer getRegex() {
        return regex;
    }

    /**
     * @return regex analizada, como un String
     */
    public String getRegexString() {
        return regex.toString();
    }

    /**
     * @return specials os operadores y simbolos especiales del lenguaje
     */
    public String getSpecials() {
        return specials;
    }
}
