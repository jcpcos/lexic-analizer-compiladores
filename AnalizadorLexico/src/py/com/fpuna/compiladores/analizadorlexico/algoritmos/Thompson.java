/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fpuna.compiladores.analizadorlexico.algoritmos;

import java.util.Iterator;
import py.com.fpuna.compiladores.analizadorlexico.Automata;
import py.com.fpuna.compiladores.analizadorlexico.automata.Enlace;
import py.com.fpuna.compiladores.analizadorlexico.automata.Estado;

/**
 *
 * @author markos
 */
public class Thompson extends Automata {
    /**
     * Implementación de la generación de automatas según la definición de
     * Thompson para la operación "|"
     *
     * @param A2 Automata a seguir como camino alternativo al actual
     */
    public void thompson_or(Automata A2) {

        Automata A1 = this;

        // Obtenemos las referencias a los finales e iniciales correspondientes
        Estado final_A1 = A1.getFinales().getEstado(0);
        Estado final_A2 = A2.getFinales().getEstado(0);
        Estado inicial_A1 = A1.getInicial();
        Estado inicial_A2 = A2.getInicial();

        final_A1.setEstadofinal(false);
        final_A2.setEstadofinal(false);

        // Se crean 2 nuevos estados
        Estado estado_inicial = new Estado(0, true, false, false);
        Estado estado_final = new Estado(A1.getEstados().size() + A2.getEstados().size() + 1, false, true, false);


        // Actualizar estados iniciales de A1 y A2
        A1.getInicial().setEstadoinicial(false);
        A2.getInicial().setEstadoinicial(false);

        // Se incrementan los numeros de ambos automatas
        A1.renumerar(1);
        A2.renumerar(A1.getEstados().size() + 1);

        // Se crean los enlaces vacios desde el nuevo estado inicial

        // 1. Nuevo Inicio --> Inicio del Automata Actual
        estado_inicial.addEnlace(new Enlace(estado_inicial,
                inicial_A1,
                this.empty));

        // 2. Nuevo Inicio --> Inicio del Automata Alternativo
        estado_inicial.addEnlace(new Enlace(estado_inicial,
                inicial_A2,
                this.empty));

        // Se crean los enlaces desde los finales del Actual (A1) y el
        // alternativo (A2) hacia el Nuevo Estado Final.

        // 3. Fin del Actual (A1) --> Nuevo Estado Final
        final_A1.addEnlace(new Enlace(final_A1, estado_final, this.empty));

        // 4. Fin del Alternativo (A2) --> Nuevo Estado Final
        final_A2.addEnlace(new Enlace(final_A2, estado_final, this.empty));

        // Agregamos a A1 todos los estados de A2
        Iterator it = A2.getEstados().getIterator();
        while (it.hasNext()) {
            A1.getEstados().insertar((Estado) it.next());
        }

        // Agregamos a A1 los nuevos estados creados.
        A1.getEstados().insertar(estado_inicial);
        A1.getEstados().insertar(estado_final);

        // Actualizar referencias auxiliares al inicial y al final del actual
        A1.setInicial(estado_inicial);
        A1.getFinales().set(0, estado_final);
    }

    /**
     * Implementación de la generación de automatas según la definición de
     * Thompson para la operación de concatenación
     *
     * @param A2 Automata siguiente al actual
     */
    public void thompson_concat(Automata A2) {
        Automata A1 = this; //se agrega a este automata quedando A1 A2 osea this A2.

        // Obtener referencias al final de A1 y al inicial de de A2
        Estado final_A1 = A1.getFinales().getEstado(0);
        Estado inicial_A2 = A2.getInicial();

        // Se actualiza al estado inicial del Automata Siguiente (A2) para
        // que deje de ser inicial
        inicial_A2.setEstadoinicial(false);
        final_A1.setEstadofinal(false);

        // Renumeramos los estados del Automata siguiente
        int a1_estado_final = A1.getEstados().size() - 1;
        A2.renumerar(a1_estado_final);

        // Se fusiona el enlace inicial de A2 con el final de A1
        // 1. Primero agregamos los enlaces del inicio de A2, al final de A1
        Iterator<Enlace> enlaces_a2_inicio = inicial_A2.getEnlaces().getIterator();

        while (enlaces_a2_inicio.hasNext()) {
            Enlace current = enlaces_a2_inicio.next();
            current.setOrigen(final_A1);
            final_A1.addEnlace(current);
        }

        // 2. Agregar los demás estados de A2, excepto su inicial, al automata A1
        Iterator<Estado> estados_a2 = A2.getEstados().getIterator();

        while (estados_a2.hasNext()) {
            Estado est_a2 = estados_a2.next();

            // 2.1 Actualizar en el estado, todos los enlaces que apuntaban al
            //     inicio de A2 para que apunten al nuevo inicio, que es el final
            //     de A1 y a
            Iterator<Enlace> enlaces = est_a2.getEnlaces().getIterator();

            while (enlaces.hasNext()) {
                Enlace current = enlaces.next();
                Estado current_destino = current.getDestino();

                // Si el destino de este enlace
                if (current_destino.getId() == inicial_A2.getId()) {
                    current.setDestino(final_A1);
                }
            }

            // Agregar el estado al automata actual
            if (est_a2.getId() != inicial_A2.getId()) {
                A1.getEstados().insertar(est_a2);
            }
        }

        A1.getFinales().set(0, A2.getFinales().getEstado(0));
    }

    /**
     * Parte de las operaciones de implementación de kleene (*), plus (+) y
     * cerouno (?) que es común entre las tres. <br>
     *
     * Modifica el automata actual de la siguiente manera: <br>
     * <ul>
     *   <li>Agrega dos nuevos estados (uno al inicio y otro al final) </li>
     *   <li>Agrega dos nuevos enlaces vacíos
     *       <ul>
     *          <li>Uno para unir el nuevo inicio con el viejo</li>
     *          <li>Uno para unir el viejo fin con el nuevo</li>
     *       </ul>
     *   </li>     *
     * </ul>     *
     */
    public void thompson_common() {

        // Se realiza la operacion sobre el mismo objeto.
        Automata A1 = this;


        // Se incrementan en 1 los estados
        A1.renumerar(1);

        // Se agregan 2 Estados nuevos (Un inicial y uno al final)
        Estado estado_inicial = new Estado(0, true, false, false);
        Estado estado_final = new Estado(A1.getEstados().size() + 1, false, true, false);

        Estado ex_estado_inicial = A1.getInicial();
        Estado ex_estado_final = A1.getFinales().getEstado(0);

        ex_estado_inicial.setEstadoinicial(false);
        ex_estado_final.setEstadofinal(false);

        // Agregar vacíos al comienzo y al final
        estado_inicial.addEnlace(new Enlace(estado_inicial,
                ex_estado_inicial,
                this.empty));

        ex_estado_final.addEnlace(new Enlace(ex_estado_final,
                estado_final,
                this.empty));


        // Actualizar referencias auxiliares
        this.inicial = estado_inicial;
        this.finales.set(0, estado_final);

        A1.getEstados().insertar(estado_inicial);
        A1.getEstados().insertar(estado_final);


    }

    /**
     * Implementación de la operación '?' sobre el automata actual. <br>
     *
     * Consiste en Agregar al automata actual enlaces vacios al comienzo y al
     * final ademas de un enlace vacio entre el inicio y el final para permitir
     * que se pueda recorrer o no el Automata actual, tal como lo especifica la
     * operación ? <br>
     *
     * Observación: La operación '?' no está prevista entre las operaciones
     * originales de Thompson por lo que implementamos nuestra propia versión
     *
     */
    public void thompson_cerouno() {

        // Agrega dos nuevos estados al inicio y al final y los enlaza al
        // inicio y al final del automata original respectivamente,
        // por medio del símbolo vacío
        this.thompson_common();

        // Se agregan un enlace vacío entre el nuevo inicio y el nuevo fin
        this.inicial.addEnlace(new Enlace(this.inicial,
                this.finales.getEstado(0),
                this.empty));
    }

    /**
     *
     */
    public void thompson_plus() {

        Estado inicio_original = this.inicial;
        Estado fin_original = this.getFinales().getEstado(0);

        // Agrega dos nuevos estados al inicio y al final y los enlaza al
        // inicio y al final del automata original respectivamente,
        // por medio del símbolo vacío
        this.thompson_common();

        // Se agregan un enlace vacío entre el fin original y inicio original
        // para que se recorra el actual por lo menos una vez y pueda ser
        // recorrido más veces como lo especifica la operación '+'
        fin_original.addEnlace(new Enlace(fin_original,
                inicio_original,
                this.empty));
    }

    public void thompson_kleene() {
        Estado inicio_original = this.inicial;
        Estado fin_original = this.finales.get(0);

        // Agrega dos nuevos estados al inicio y al final y los enlaza al
        // inicio y al final del automata original respectivamente,
        // por medio del símbolo vacío
        this.thompson_common();

        // Se agrega un enlace vacío entre el fin original y inicio original
        // para que se recorra el actual más veces como lo especifica
        // la operación *
        fin_original.addEnlace(new Enlace(fin_original,
                inicio_original,
                this.empty));

        // Se agregan un enlace vacío entre el nuevo inicio y el nuevo fin
        this.inicial.addEnlace(new Enlace(this.inicial,
                this.finales.getEstado(0),
                this.empty));
    }
}
