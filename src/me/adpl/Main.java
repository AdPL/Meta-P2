package me.adpl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Properties configuracion = inicializarPropiedades();
        int stop = Integer.parseInt(configuracion.getProperty("limite_evaluaciones"));
        int nIndividuos = Integer.parseInt(configuracion.getProperty("poblacion"));

        Individuo poblacion[] = new Individuo[nIndividuos];

        for ( int i = 0; i < poblacion.length; i++ ) {
            poblacion[i] = new Individuo();
            poblacion[i].mostrarGenotipo();
        }

        algoritmoGeneticoEstacionario(poblacion, stop);
    }

    public static void algoritmoGeneticoEstacionario(Individuo[] poblacion, int stop) {
        int t = 0;
        List<Individuo> ganadoresTorneo = new ArrayList<>();

        do {
            t++;
            for ( Individuo individuo : poblacion ) {
                individuo.evaluar();
                individuo.mostrarGenotipo();
            }
            ganadoresTorneo.add(evaluacionPorTorneo(poblacion));
            ganadoresTorneo.add(evaluacionPorTorneo(poblacion));
            Individuo hijo1 = cruceEnOrden(ganadoresTorneo.get(0), ganadoresTorneo.get(1));
            Individuo hijo2 = cruceEnOrden(ganadoresTorneo.get(1), ganadoresTorneo.get(0));

            Individuo peor1, peor2;
            peor1 = poblacion[0];
            peor2 = poblacion[poblacion.length-1];

            Individuo peores[] = new Individuo[2];

            for ( Individuo i : poblacion ) {
                if ( peor1.getValor() > i.getValor() ) {
                    System.out.println("Peor 1: " + peor1.getValor() + " vs " + i.getValor() + " CAMBIO");
                    peor1 = i;
                }
                if ( i.getValor() != peor1.getValor() && peor2.getValor() > i.getValor() ) {
                    System.out.println("Peor 2: " + peor2.getValor() + " vs " + i.getValor() + " CAMBIO");
                    peor2 = i;
                }
            }

            System.out.println("PEORES:");
            peor1.mostrarGenotipo();
            peor2.mostrarGenotipo();
            System.out.println("HIJOS:");
            hijo1.mostrarGenotipo();
            hijo2.mostrarGenotipo();

            if ( hijo1.getValor() > peor1.getValor() ) {
                poblacion[peor1.getId()-1] = hijo1;
            } else if ( hijo2.getValor() > peor1.getValor() ) {
                poblacion[peor1.getId()-1] = hijo2;
            }

            //TODO: Gestionar correctamente prioridad de selección de hijos
            if ( hijo1.getValor() > hijo2.getValor() ) {
                if (hijo1.getValor() != peor1.getValor() && hijo1.getValor() > peor2.getValor()) {
                    poblacion[peor1.getId() - 1] = hijo1;
                } else if (hijo2.getValor() != peor1.getValor() && hijo2.getValor() > peor2.getValor()) {
                    poblacion[peor2.getId() - 1] = hijo2;
                }
            } else {
                if (hijo2.getValor() != peor1.getValor() && hijo2.getValor() > peor2.getValor()) {
                    poblacion[peor2.getId() - 1] = hijo2;
                } else if (hijo1.getValor() != peor2.getValor() && hijo1.getValor() > peor1.getValor()) {
                    poblacion[peor1.getId() - 1] = hijo1;
                }
            }

            System.out.println("");
            System.out.println("");
            for ( Individuo individuo : poblacion ) {
                individuo.evaluar();
                individuo.mostrarGenotipo();
            }

        } while ( t < stop );
    }

    public static Individuo cruceEnOrden(Individuo padre1, Individuo padre2) {
        Random r = new Random();
        int corte1, corte2;
        do {
            corte1 = r.nextInt(9);
            corte2 = r.nextInt(8)+1;
        } while ( corte1 >= corte2 );

        System.out.println("Cortes: ");
        System.out.println("Corte 1: " + corte1);
        System.out.println("Corte 2: " + corte2);
        for ( int i = 0; i < 9; i++ ) {
            if (i != corte1 && i != corte2) {
                System.out.print("  ");
            } else {
                System.out.print("* ");
            }
        }

        System.out.println();
        padre1.mostrarGenotipo();
        padre2.mostrarGenotipo();

        Individuo hijo1 = new Individuo();

        hijo1.setGenotipo(padre1.getGenotipo(), corte1, corte2);

        for ( int i = corte2+1; i < hijo1.getGenotipo().length; i++ ) {
            hijo1.setGenotipo(i, padre2.getGenotipo()[i]);
        }

        for ( int i = 0; i < corte1; i++ ) {
            hijo1.setGenotipo(i, padre2.getGenotipo()[i]);
        }

        hijo1.mostrarGenotipo();
        return hijo1;
    }

    //TODO: Realizar correctamente crucePMX, no funciona
    public static Individuo crucePMX(Individuo padre1, Individuo padre2) {
        Random r = new Random();
        int corte1, corte2;
        do {
            corte1 = r.nextInt(9);
            corte2 = r.nextInt(8)+1;
        } while ( corte1 >= corte2 );

        System.out.println("Cortes: ");
        System.out.println("Corte 1: " + corte1);
        System.out.println("Corte 2: " + corte2);
        for ( int i = 0; i < 9; i++ ) {
            if (i != corte1 && i != corte2) {
                System.out.print("  ");
            } else {
                System.out.print("* ");
            }
        }
        System.out.println();
        padre1.mostrarGenotipo();
        System.out.println();
        padre2.mostrarGenotipo();
        System.out.println();

        Individuo hijo1 = new Individuo();
        hijo1.setGenotipo(padre2.getGenotipo(), corte1, corte2);
        hijo1.mostrarGenotipo();
        System.out.println();

        int iniciosPadre1[] = new int[(corte2-corte1)+1];
        int iniciosPadre2[] = new int[(corte2-corte1)+1];
        for ( int i = 0; i < iniciosPadre1.length; i++ ) {
            iniciosPadre1[i] = padre1.getGenotipo()[corte1+i];
            iniciosPadre2[i] = padre2.getGenotipo()[corte1+i];
        }

        Stack<Integer> pila = new Stack<>();
        int numero = -1;
        int auxiliar = -1;

        for ( int n : iniciosPadre2 ) {
            boolean encontrado = false;
            int i = 0, j = 0, posicion = -1;

            for ( int x : iniciosPadre1 ) {
                if ( n == x ) {
                    encontrado = true;
                    break;
                }
            }


            if ( !encontrado ) {
                pila.addElement(n);
                auxiliar = n;
                do {
                    while (auxiliar != padre1.getGenotipo()[i]) {
                        if ( auxiliar == n ) posicion = i;
                        i++;
                    }

                    auxiliar = padre2.getGenotipo()[i];
                    pila.addElement(auxiliar);
                    i = 0;
                    System.out.println(pila);

                } while ( !pila.contains(auxiliar) );

                hijo1.setGenotipo(posicion, pila.pop());
                pila.clear();

                hijo1.mostrarGenotipo();

            }
        }

        return padre1;

    }

    public static Individuo evaluacionPorTorneo(Individuo[] poblacion) {
        Random r = new Random();
        int individuo1, individuo2;

        do {
            individuo1 = r.nextInt(poblacion.length);
            individuo2 = r.nextInt(poblacion.length);
        } while ( individuo1 == individuo2);

        if ( poblacion[individuo1].getValor() > poblacion[individuo2].getValor() )
            return poblacion[individuo1];
        else
            return poblacion[individuo2];
    }

    public static Properties inicializarPropiedades() {
        try {
            Properties configuracion = new Properties();

            configuracion.load(new FileInputStream("main.properties"));

            return configuracion;
        } catch (IOException e) {
            System.out.println("Error: No se pudo leer el fichero");
            return null;
        }
    }
}
