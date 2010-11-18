package py.com.fpuna.compiladores.analizadorlexico;

import py.com.fpuna.compiladores.analizadorlexico.Automata.TipoAutomata;
import py.com.fpuna.compiladores.analizadorlexico.Token.TipoToken;
import py.com.fpuna.compiladores.analizadorlexico.algoritmos.Thompson;
import py.com.fpuna.compiladores.exceptions.LexicalError;

/**
 * Esta clase implementa los procedimientos necesarios para llevar a cabo el proceso de traducción.
 * Se basa en el siguiente BNF para definir un lenguaje de expresiones regulares.
 * 
 * 1:  simboloInicial -> simple s               [- simboloInicial()  -]
 * 2:  s  -> “|” simple s | Є                   [- bnf_s()  -]
 * 3:  simple -> basico t                       [- bnf_simple()  -]
 * 4:  t -> basico t | Є                        [- bnf_t()  -]
 * 5:  basico -> list op                        [- bnf_basico()  -]
 * 6:  op -> * | + | ? | Є                      [- simboloInicial()  -]
 * 7:  list -> grupo | leng                     [- simboloInicial()  -]
 * 8:  grupo -> “(” simboloInicial “)”          [- simboloInicial()  -]
 * 9:  leng -> [alfabeto del lenguaje]          [- simboloInicial()  -]
 * 
 * Se implementa un Traductor Dirigido por la Sintaxis que sigue este BNF y produce el automata
 * basándose en las construcciones de Thompson.
 */
public final class Analizador {
 
    private Lexico lexico;
    private String regex;
    private Token preanalisis;
    private Alfabeto alfabeto;
    private Thompson automata;
    private String Special;
    private int posicion;
    private boolean error = false;
    private String errMsg = ""; 

    
    public Analizador() {
    }

    /**
     * Constructor del Analizador Sintáctico
     * @param regex Expresión regular cuyo AFN queremos generar
     * @param alfabeto Alfabeto sobre el cual está definida la expresión regular
     */
    public Analizador(String regex, String alfabeto) {
        this.setPosicion(0);
        this.regex = regex;        
        this.alfabeto = new Alfabeto(alfabeto);        
        this.lexico = new Lexico(regex, alfabeto); // creamos el analizador léxico
        try {
            this.preanalisis = nextSymbol(); // obtenemos el primer símbolo desde el analizador léxico
        } catch (LexicalError ex) {
            this.error = true;
            this.errMsg = "Error FATAL en el traductor. La generación del AFN no puede continuar\n"+ex.getMessage();
            System.out.println(this.getErrMsg());
        }
        automata = new Thompson();
    }

    public Thompson traducir() {
        this.automata = this.simboloInicial();
        
        if (!this.isError()) {
            if (preanalisis.getTipo() != TipoToken.FIN) {
                this.error = true;
                this.errMsg = "Quedaron caracteres sin analizar debido al siguiente Token no esperado["+
                        this.getPosicion()+"]: "+preanalisis.getValor();
            }
        }
        
        return this.automata;
    }

     public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.setPosicion(0);
        this.regex = regex;
        this.lexico = new Lexico(regex, alfabeto); // creamos el analizador léxico

        try {
            this.preanalisis = nextSymbol(); // obtenemos el primer símbolo desde el analizador léxico
        } catch (LexicalError ex) {
            this.error = true;
            this.errMsg =  "Error FATAL en el traductor. La generación del AFN no puede continuar\n"+ex.getMessage();
            System.out.println(this.getErrMsg());
        }
        automata = new Thompson();
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

    public Automata getAutomata() {
        return automata;
    }

    public void setAutomata(Thompson Aut) {
        this.automata = Aut;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public void incPosicion() {
        this.setPosicion(this.posicion+1);
    }

    public boolean isError() {
        return error;
    }

    public String getErrMsg() {
        return errMsg;
    }

    /**
     * Corresponde al símbolo inicial de la gramática de expresiones regulares.
     * Las producciones que pueden ser vacío, retornan un valor null en ese caso.
     * @return Autoamata producido por la producción << simboloInicial => simple s >>
     */
    private Thompson simboloInicial() {

        Thompson automata1 = null, automata2;
                
        try {
            automata1 = this.bnf_simple();
            automata2 = this.bnf_s();
            if (automata2 != null) {
                automata1.OR(automata2);
            }
        } catch (LexicalError ex) {            
            this.error = true;
            this.errMsg = "Error FATAL en el traductor. La generación del AFN no puede continuar\n"+ex.getMessage();
            System.out.println(this.getErrMsg());
        }       
        if (!(this.error) ){
            this.setAutomata(automata1); // Actualizar el Thompson Global
            automata1.setAlpha(this.alfabeto);
            automata1.setRegex(this.regex);
        }
        return automata1;
    }

    /**
     * Segunda producción del BNF que permite la recursión necesaria para producir cadenas
     * de expresiones regulares separadas por el operador "|" (disyunción) 
     * @return null si derivó en vacío, en caso contrario, el Thompson generado
     * @throws LexicalError
     */
    private Thompson bnf_s() throws LexicalError {
        try {            
            Token or = new Token("|");
            if (preanalisis.compareTo(or) == 0) {    
                this.matching("|"); // si preanalisis es el esperado, consumimos,
                return simboloInicial();
            } else {                 
                return null;    // si es vacío se analiza en otra producción
            }         
        } catch (LexicalError ex) {
            this.error = true;
            throw new LexicalError("se esperaba '|' en lugar de "+this.preanalisis.getValor());            
        }
    }
    
    /**
     * Tercera producción del BNF
     * @return Thompson producido por la producción
     */
    private Thompson bnf_simple() throws LexicalError {

        Thompson automata1 = this.bnf_basico();
        Thompson automata2 = this.bnf_t();
        if (automata2 != null) {
            automata1.Concat(automata2);
        }
        
        return automata1;
    }

    /**
     * La cuarta producción comprueba si preanalisis está en el conjunto primero
     * de simple, y si está, volver a ejecutar bnf_simple. En caso contrario retorna null.
     * El conjunto Primero de resimple es {"(",[alpha]}.
     * @return Thompson producido por la producción, o null si la producción deriva en vacío.
     * @throws LexicalError
     */
    private Thompson bnf_t() throws  LexicalError {

        String current = preanalisis.getValor();
        Thompson result = null;
        if ( (preanalisis.getTipo() != TipoToken.FIN) &&
             (this.alfabeto.contiene(current) || current.compareTo("(")==0)
           ) {
            result = this.bnf_simple();
        }
        return result;
    }

    /**
     * Quinta producción del BNF
     * @return
     * @throws LexicalError
     */
    private Thompson bnf_basico() throws LexicalError{
        Thompson automata1 = list();

        if (automata1 != null) {
            char operator = op();
            switch (operator) {
                case '*':
                    automata1.Kleene();
                    break;
                case '+':
                    automata1.Plus();
                    break;
                case '?':
                    automata1.NoneOrOne();
                    break;
                case 'E':
                    break;
            }
        }
        return automata1;
    }       

    /**
     * Sexta producción
     * @return
     * @throws LexicalError
     */
    private char op() throws LexicalError {
        char operador = 'E';        
        
        if (preanalisis.getValor().compareTo("") != 0) {
            operador = preanalisis.getValor().charAt(0);

            switch (operador) {
                case '*':
                    this.matching("*");
                    break;
                case '+':
                    this.matching("+");
                    break;
                case '?':
                    this.matching("?");
                    break;
                default:
                    return 'E';
            }
        }
        return operador;
    }

    /**
     * Producción 7
     * @return
     * @throws LexicalError
     */
    private Thompson list() throws  LexicalError {

        Token grupofirst = new Token("(");

        if(preanalisis.compareTo(grupofirst) == 0) {
            return this.grupo();
        } else {
            return this.leng();
        }
    }

    /**
     * Producción 8
     * @return
     * @throws LexicalError
     */
    private Thompson grupo() throws LexicalError {
        try {
            this.matching("(");
        } catch (LexicalError ex) {
            this.error = true;
            throw new LexicalError("se esperaba el símbolo -> '('");
        }
        
        Thompson Aux1 = this.simboloInicial();
        
        try {
            this.matching(")");
        } catch (LexicalError ex) {
            this.error = true;
            throw new LexicalError("se esperaba el simbolo -> ')'");
        }
        
        return Aux1;
    }
    
    /**
     * Producción 9
     * @return
     * @throws LexicalError
     */
    private Thompson leng() throws LexicalError {
        Thompson nuevo = null;
        try {
            if (preanalisis.getTipo() != TipoToken.FIN) {
                nuevo = new Thompson(preanalisis.getValor(),TipoAutomata.AFN.ordinal());
                this.matching(preanalisis.getValor());
            }
        } catch (LexicalError ex) {
            this.error = true;
            throw new LexicalError("Error Léxico en [" + this.getPosicion() + "]: el símbolo no pertenece al alfabeto");
        } catch (Exception ex) {
            this.error = true;
            throw new LexicalError("Error Léxico en [" + this.getPosicion() + "]: "+ex.getMessage());
        }

        return nuevo;
    }

   /**
     * Llamada al analizador léxico para obtener el sgte caracter de la cadena de entrada.
     * Si el analizador léxico encuentra un error (como que el caracter no
     * pertenece al alfabeto) se atrapa la excepción, se informa en la salida y
     * se aborta el análisis. <br><br>
     * @return Token que contiene el símbolo siguiente a procesar
     * @exception LexicalError
     */
    private Token nextSymbol() throws LexicalError {
        Token result = null;
        result = this.lexico.next();
        return result;
    }
   
    /**
     * Implementación del procedimiento que se encarga de parear el símbolo de
     * preanálisis actual con la entrada esperada según la sintaxis del lenguaje
     *
     * @param simbolo Símbolo esperado
     * @throws LexicalError
     */
    private void matching(String simbolo) throws LexicalError {

        Token tok = new Token(simbolo); // se crea un Token temporal para
                                        // compararlo con preanalisis

        if ( getPreanalisis().compareTo(tok) == 0 ) {
            this.setPreanalisis(this.nextSymbol());
            this.Special = tok.getValor();
            this.incPosicion();
        } else {
            throw new LexicalError(tok.getValor());
        }
    }    

}
