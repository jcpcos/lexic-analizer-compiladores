/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fpuna.compiladores.analizadorlexico;

import py.com.fpuna.compiladores.analizadorlexico.Automata.TipoAutomata;
import py.com.fpuna.compiladores.analizadorlexico.Token.TipoToken;
import py.com.fpuna.compiladores.analizadorlexico.algoritmos.Minimizacion;
import py.com.fpuna.compiladores.analizadorlexico.algoritmos.Subconjunto;
import py.com.fpuna.compiladores.analizadorlexico.algoritmos.Thompson;
import py.com.fpuna.compiladores.exceptions.LexicalError;

/**
 *
 * @author markos
 */
public class Main {

    Lexico lexico;
    String regex;
    Token preanalisis;
    Alfabeto alfabeto;
    Automata automata;
    String Special;
    int posicion;
    boolean hayErrores = false;
    String errMsg = "";

    private void Match(String simbolo) throws LexicalError {

        Token tok = new Token(simbolo); // se crea un Token temporal para
        // compararlo con preanalisis

        if (getPreanalisis().compareTo(tok) == 0) {
            this.setPreanalisis(this.nextSymbol());
            this.Special = tok.getValor();
            this.incPosicion();
        } else {
        }
    }

    private Token nextSymbol() throws LexicalError {
        Token result = null;
        result = this.lexico.next();
        return result;
    }

    public Automata traducir() {
        this.automata = this.RE();

        if (!this.hayErrores) {
            if (preanalisis.getTipo() != TipoToken.FIN) {
                this.hayErrores = true;
                this.errMsg = "Quedaron caracteres sin analizar debido al siguiente Token no esperado["
                        + this.getPosicion() + "]: " + preanalisis.getValor();
            }
        }

        return this.automata;
    }

    public Main(String regex, String alfabeto) {
        this.setPosicion(0);
        this.regex = regex;
        this.alfabeto = new Alfabeto(alfabeto);
        this.lexico = new Lexico(regex, alfabeto); // creamos el analizador léxico
        try {
            // creamos el analizador léxico
            this.preanalisis = nextSymbol(); // obtenemos el primer símbolo desde el analizador léxico
        } catch (LexicalError ex) {

            this.hayErrores = true;
            this.errMsg =
                    "Se produjo un error FATAL en el traductor. La generación del AFN no puede continuar\n"
                    + "--> " + ex.getMessage();

            System.out.println(this.getErrMsg());

        }
        automata = new Automata();
    }

    private void init() throws Exception {

        

        System.out.println("--> Generacion de un AFN simple con:\n-->   regex (sin espacios)=" + regex + "\n-->   alfabeto=" + "ab");
        Main t = new Main(regex, "ab");
        Automata A = t.traducir();
        A.setAlpha(t.getAlfabeto());
        A.setRegex(t.getRegex());

        String salida_simple = A.imprimir();
        System.out.println(salida_simple);

        //Alg de Subconjuntos
        Subconjunto algSub = new Subconjunto(A);
        Automata AFD = algSub.ejecutar().convertAutomata();
        System.out.println("\nAFD\n____\n");
        System.out.println(AFD.imprimir());

        //Eliminar estados inalacanzables
        AFD = Subconjunto.eliminar_estados_inalcanzables(AFD);
        System.out.println("\nAFD sin estados inalcanzables\n___________\n");
        System.out.println(AFD.imprimir());

        //Alg de Minimizacion
        Minimizacion algMin = new Minimizacion(AFD);
        Automata AFDM = algMin.minimizar();
        System.out.println("\nAFDM\n_____\n");
        System.out.println(AFDM.imprimir());

        //Eliminar estados muertos
        AFDM.eliminarIslas();
        System.out.println("\nAFDM sin estados muertos\n________________\n");
        System.out.println(AFDM.imprimir());
    }

    public static void main(String[] args)throws Exception {
        new Main("(a|b)*", "ab");
    }

    private Thompson RE() {

        // automatas auxiliares de producciones llamadas
        Thompson Aux1 = null;
        Thompson Aux2;

        try {

            Aux1 = this.resimple();
            Aux2 = this.A();

            if (Aux2 != null) {
                Aux1.OR(Aux2);
            }
        } catch (LexicalError ex) {

            this.hayErrores = true;
            this.errMsg =
                    "Se produjo un error FATAL en el traductor. La generación del AFN no puede continuar\n"
                    + "--> " + ex.getMessage();

            System.out.println(this.getErrMsg());
        } 

        if (!(this.hayErrores)) {
            this.setAutomata(Aux1); // Actualizar el Automata Global
            Aux1.setAlpha(this.alfabeto);
            Aux1.setRegex(this.regex);
        }
        return Aux1;
    }

    /**
     * Producción A, que permite la recursión necesaria para producir cadenas
     * de expresiones regulares separadas por el operador "|" (disyunción) <br><br>
     *
     * @return null si derivó en vacío, en caso contrario, el automata generado
     * @throws exceptions.SyntaxError
     */
    private Thompson A() throws LexicalError {
        try {
            Token or = new Token("|");

            if (preanalisis.compareTo(or) == 0) {
                this.Match("|"); // si preanalisis es el esperado, consumimos,
                return RE();
            } else {
                return null;    // si es vacío se analiza en otra producción
            }
        } catch (LexicalError ex) {
            this.hayErrores = true;
            throw new LexicalError("se esperaba '|' en lugar de -> "
                    + this.preanalisis.getValor());
        }
    }

    /**
     * Producción resimple
     *
     * @return Automata producido por la producción
     * @throws exceptions.SyntaxError
     * @throws exceptions.LexicalError
     */
    private Thompson resimple() throws LexicalError {
        Thompson Aux1 = this.rebasico();
        Thompson Aux2 = this.B();

        if (Aux2 != null) {
            Aux1.Concat(Aux2);
        }

        return Aux1;
    }

    /**
     * Producción rebasico.
     * @return Automata generado luego de derivar la producción
     */
    private Thompson rebasico() throws LexicalError {

        Thompson Aux1 = list();

        if (Aux1 != null) {
            char operator = op();

            switch (operator) {
                case '*':
                    Aux1.Kleene();
                    break;
                case '+':
                    Aux1.Plus();
                    break;
                case '?':
                    Aux1.NoneOrOne();
                    break;
                case 'E':
                    break;
            }
        } /*else if (preanalisis.) {
        throw new SyntaxError("se esperaba un símbolo del lenguaje y se encontró: "
        +this.preanalisis.getValor(),this.getPosicion());
        }*/

        return Aux1;
    }

    /**
     * La producción B debe verificar si preanalisis está en el conjunto primero
     * de resimple, y si está, volver a ejecutar resimple. En caso contrario debe
     * retornar null. <br> <br>
     *
     * El conjunto Primero de resimple es {"(",[alpha]}.
     *
     * @return Automata el automata producido por la producción, o null si la
     *                  producción deriva en vacío.
     * @throws exceptions.SyntaxError
     * @throws exceptions.LexicalError
     */
    private Thompson B() throws LexicalError {

        String current = preanalisis.getValor();
        Thompson result = null;

        if ((preanalisis.getTipo() != TipoToken.FIN)
                && (this.alfabeto.contiene(current) || current.compareTo("(") == 0)) {
            result = this.resimple();
        }

        return result;
    }

    private Thompson list() throws LexicalError {
        Token grupofirst = new Token("(");

        if (preanalisis.compareTo(grupofirst) == 0) {
            return this.grupo();
        } else {
            return this.leng();
        }
    }

    private char op() throws LexicalError {
        char operador = 'E';

        if (preanalisis.getValor().compareTo("") != 0) {
            operador = preanalisis.getValor().charAt(0);

            switch (operador) {
                case '*':
                    this.Match("*");
                    break;
                case '+':
                    this.Match("+");
                    break;
                case '?':
                    this.Match("?");
                    break;
                default:
                    return 'E';
            }
        }
        return operador;
    }

    private Thompson grupo() throws LexicalError {
        try {
            this.Match("(");
        } catch (Exception ex) {
            this.hayErrores = true;
            throw new LexicalError("se esperaba el símbolo -> '('");
        }

        Thompson Aux1 = this.RE();

        try {
            this.Match(")");
        } catch (LexicalError ex) {
            this.hayErrores = true;
            throw new LexicalError("se esperaba el símbolo -> ')'");
        }

        return Aux1;
    }

    /**
     *
     * @return
     */
    private Thompson leng() throws LexicalError {
        Thompson nuevo = null;
        try {
            if (preanalisis.getTipo() != TipoToken.FIN) {
                nuevo = new Thompson(preanalisis.getValor(), TipoAutomata.AFN.ordinal());
                this.Match(preanalisis.getValor());
            }
        } catch (LexicalError ex) {
            this.hayErrores = true;
            throw new LexicalError("Error Léxico en [" + this.getPosicion() + "]: el símbolo no pertenece al alfabeto");
        } catch (Exception ex) {
            this.hayErrores = true;
            throw new LexicalError("Error Léxico en [" + this.getPosicion() + "]: " + ex.getMessage());
        }

        return nuevo;
    }


    /* ----------------------- GETTERS Y SETTERS ------------------------ */
    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.setPosicion(0);
        this.regex = regex;
        this.lexico = new Lexico(regex, alfabeto.imprimir()); // creamos el analizador léxico

        try {
            // creamos el analizador léxico
            this.preanalisis = nextSymbol(); // obtenemos el primer símbolo desde el analizador léxico
        } catch (LexicalError ex) {
            this.hayErrores = true;
            this.errMsg =
                    "Se produjo un error FATAL en el traductor. La generación del AFN no puede continuar\n"
                    + "--> " + ex.getMessage();

            System.out.println(this.getErrMsg());
        }
        automata = new Automata();
    }

    public Token getPreanalisis() {
        return preanalisis;
    }

    public void setPreanalisis(Token preanalisis) {
        this.preanalisis = preanalisis;
    }

    public Alfabeto getAlfabeto() {
        return alfabeto;
    }

    public void setAlfabeto(Alfabeto alfabeto) {
        this.alfabeto = alfabeto;
    }

    public void setAlfabetoString(String alpha) {
        this.alfabeto = new Alfabeto(alpha);
    }

    public Automata getAutomata() {
        return automata;
    }

    public void setAutomata(Automata Aut) {
        this.automata = Aut;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public void incPosicion() {
        this.setPosicion(this.posicion + 1);
    }

    public boolean isHayErrores() {
        return hayErrores;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
