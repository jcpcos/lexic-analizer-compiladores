package py.com.fpuna.compiladores.analizadorlexico.algoritmos;

import java.util.Iterator;
import py.com.fpuna.compiladores.analizadorlexico.Automata;
import py.com.fpuna.compiladores.analizadorlexico.automata.Arco;
import py.com.fpuna.compiladores.analizadorlexico.automata.Estado;

/**
 *
 * @author markos
 */
public class Thompson extends Automata {

    public Thompson(String preanalisis, int tipo) {
        super(preanalisis, tipo);
    }

    public Thompson() {
        super();
    }

    public void OR(Thompson param) {
        // Obtenemos las referencias a los finales e iniciales correspondientes
        Estado tFinal = this.getFinales().getEstado(0);
        Estado paramFinal = param.getFinales().getEstado(0);
        Estado tInicial = this.getInicial();
        Estado paramInicial = param.getInicial();

        tFinal.setEstadofinal(false);
        paramFinal.setEstadofinal(false);

        // Se crean 2 nuevos estados
        Estado initialState = new Estado(0, true, false, false);
        Estado finalState = new Estado(this.getEstados().size() + param.getEstados().size() + 1,
                false, true, false);

        // Actualizar estados iniciales de A1 y A2
        getEstadoInicial().setEstadoinicial(false);
        param.getEstadoInicial().setEstadoinicial(false);

        // Se incrementan los numeros de ambos automatas
        renumerar(1);
        param.renumerar(this.getListaEstados().size() + 1);

        // Se crean los enlaces vacios desde el nuevo estado inicial

        // 1. Nuevo Inicio --> Inicio del Automata Actual
        initialState.addEnlace(new Arco(initialState,
                tInicial,
                EMPTY));

        // 2. Nuevo Inicio --> Inicio del Automata Alternativo
        initialState.addEnlace(new Arco(initialState,
                paramInicial,
                EMPTY));

        // Se crean los enlaces desde los finales del Actual (A1) y el
        // alternativo (A2) hacia el Nuevo Estado Final.

        // 3. Fin del Actual (A1) --> Nuevo Estado Final
        tFinal.addEnlace(new Arco(tFinal, finalState, EMPTY));

        // 4. Fin del Alternativo (A2) --> Nuevo Estado Final
        paramFinal.addEnlace(new Arco(paramFinal, finalState, EMPTY));

        // Agregamos a A1 todos los estados de A2
        Iterator it = param.getEstados().getIterator();
        while (it.hasNext()) {
            this.getEstados().insertar((Estado) it.next());
        }

        // Agregamos a A1 los nuevos estados creados.
        this.getEstados().insertar(initialState);
        this.getEstados().insertar(finalState);

        // Actualizar referencias auxiliares al inicial y al final del actual
        setEstadoInicial(initialState);
        this.getFinales().set(0, finalState);
    }

    public void Concat(Thompson param) {
        Estado finalState = getFinales().getEstado(0);
        Estado initialParam = param.getInicial();
        Iterator<Arco> enlacesParam = initialParam.getEnlaces().getIterator();

        initialParam.setEstadoinicial(false);
        finalState.setEstadofinal(false);

        int estadosFinales = getEstados().size() - 1;
        param.renumerar(estadosFinales);

        while (enlacesParam.hasNext()) {
            Arco current = enlacesParam.next();
            current.setOrigen(finalState);
            finalState.addEnlace(current);
        }

        Iterator<Estado> estadosParam = param.getEstados().getIterator();

        while (estadosParam.hasNext()) {
            Estado estadoParam = estadosParam.next();
            Iterator<Arco> enlaces = estadoParam.getEnlaces().getIterator();

            while (enlaces.hasNext()) {
                Arco enlaceActual = enlaces.next();
                Estado destinoActual = enlaceActual.getDestino();

                // Si el destino de este enlace
                if (destinoActual.getId() == initialParam.getId()) {
                    enlaceActual.setDestino(finalState);
                }
            }

            // Agregar el estado al automata actual
            if (estadoParam.getId() != initialParam.getId()) {
                this.getEstados().insertar(estadoParam);
            }
        }

        getFinales().set(0, param.getFinales().getEstado(0));
    }

    public void Common() {
        renumerar(1);

        // Se agregan 2 Estados nuevos (Un inicial y uno al final)
        Estado estado_inicial = new Estado(0, true, false, false);
        Estado estado_final = new Estado(getEstados().size() + 1, false, true, false);

        Estado ex_estado_inicial = getInicial();
        Estado ex_estado_final = getFinales().getEstado(0);

        ex_estado_inicial.setEstadoinicial(false);
        ex_estado_final.setEstadofinal(false);

        estado_inicial.addEnlace(new Arco(estado_inicial,
                ex_estado_inicial,
                EMPTY));

        ex_estado_final.addEnlace(new Arco(ex_estado_final,
                estado_final,
                EMPTY));

        // Actualizar referencias auxiliares
        setEstadoInicial(estado_inicial);
        getListaEstadosFinales().set(0, estado_final);

        getEstados().insertar(estado_inicial);
        getEstados().insertar(estado_final);
    }

    public void NoneOrOne() {
        Common();
        getEstadoInicial().addEnlace(new Arco(getEstadoInicial(),
                getListaEstadosFinales().getEstado(0),
                EMPTY));
    }

    public void Plus() {
        Estado inicio_original = getEstadoInicial();
        Estado fin_original = getFinales().getEstado(0);
        Common();
        fin_original.addEnlace(new Arco(fin_original,
                inicio_original,
                EMPTY));
    }

    public void Kleene() {
        Estado inicio_original = getEstadoInicial();
        Estado fin_original = getListaEstadosFinales().get(0);
        Common();

        fin_original.addEnlace(new Arco(fin_original,
                inicio_original,
                EMPTY));

        getEstadoInicial().addEnlace(new Arco(getEstadoInicial(),
                getListaEstadosFinales().getEstado(0),
                EMPTY));
    }
}
