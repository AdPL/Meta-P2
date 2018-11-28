package me.adpl;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class Main {
    static Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) throws IOException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");
        Properties configuracion = inicializarPropiedades();
        int stop, nIndividuos;
        double prob_generacional, prob_mutacion;
        long tStart, tEnd;
        boolean estacionario = false, generacional = false;
        String[] archivos = {" "};
        String[] algoritmos = {};

        //TODO: Ajustar correctamente la forma de ejecutar por el fichero main.properties o comando en consola

        if ( args.length == 0 ) {
            stop = Integer.parseInt(configuracion.getProperty("limite_evaluaciones"));
            nIndividuos = Integer.parseInt(configuracion.getProperty("poblacion"));
            String archivosEjecucion = configuracion.getProperty("input");
            String algoritmosEjecucion = configuracion.getProperty("algoritmo");
            prob_generacional = Double.parseDouble(configuracion.getProperty("probabilidad_cruce_generacional"));
            prob_mutacion = Double.parseDouble(configuracion.getProperty("probabilidad_de_mutacion"));

            archivos = archivosEjecucion.split(",");
            algoritmos = algoritmosEjecucion.split(",");

            for ( String algoritmo : algoritmos ) {
                if ( "estacionario".equals(algoritmo.toString()) ) {
                    estacionario = true;
                }
                if ( "generacional".equals(algoritmo.toString()) ) {
                    generacional = true;
                }
            }
        } else {
            System.out.println("Para la ejecución de los algoritmos puede");
            System.out.println("indicar los parámetros en el fichero main.properties");
            System.out.println("o como argumentos a través de la línea de comandos");
            System.out.println("Usando: java main.java <Número de evaluaciones> <Tamaño población> <Algoritmos a ejecutar> <ficheros>");
            System.out.println("Parámetros introducidos:");
            for ( String arg : args ) {
                System.out.println(arg);
            }
            stop = Integer.parseInt(args[0]);
            nIndividuos = Integer.parseInt(args[1]);
            if ( args[2] == "estacionario" ) {
                estacionario = true;
            }
            if ( args[2] == "generacional" ) {
                generacional = true;
            }
            if ( args[3] == "estacionario" ) {
                estacionario = true;
            }
            if ( args[3] == "generacional" ) {
                generacional = true;
            }
            archivos[0] = args[4];
            prob_generacional = Double.parseDouble(args[5]);
            prob_mutacion = Double.parseDouble(args[6]);
        }

        logger.setUseParentHandlers(false);

        Handler fileHandler = new FileHandler();
        fileHandler = new FileHandler("./ultima_ejecucion.log");
        SimpleFormatter simpleFormatter = new SimpleFormatter();
        fileHandler.setFormatter(simpleFormatter);
        logger.addHandler(fileHandler);

        logger.log(Level.INFO, "Parámetros de ejecución: ");
        logger.log(Level.INFO, "Condición de parada, stop = " + stop);
        logger.log(Level.INFO, "Tamaño de la población, nIndividuos = " + nIndividuos);
        logger.log(Level.INFO, "Ficheros a ejecutar: " + archivos);
        logger.log(Level.INFO, "Algoritmos que se ejecutaran: ");
        if ( generacional ) logger.log(Level.INFO, "GENERACIONAL");
        if ( estacionario ) logger.log(Level.INFO, "ESTACIONARIO");

        fileHandler.close();

        List<Individuo> poblacion = new ArrayList<>();

        for ( String a : archivos ) {
            poblacion.clear();
            String contenido = readFile(a);

            int f[][] = dataGestFrecuencias(contenido);
            int d[][] = dataGestLocalizaciones(contenido);

            System.out.println("Ejecución del fichero: " + a);

            fileHandler = new FileHandler("./" + a.substring(0, 5) + "_generacional.log");
            fileHandler.setFormatter(simpleFormatter);
            logger.addHandler(fileHandler);
            logger.log(Level.INFO, "Iniciando ejecución, fichero: " + a);

            if ( generacional ) {
                System.out.println("Ejecutando versión generacional | Almacenando en fichero: " + a.substring(0, 5) + "_generacional.log");
                logger.log(Level.INFO, " | VERSIÓN: GENERACIONAL | PMX ON");
                for (int i = 0; i < nIndividuos; i++) {
                    poblacion.add(new Individuo(f.length));
                    poblacion.get(i).evaluar(f, d);
                }
                tStart = System.currentTimeMillis();
                algoritmoGeneticoGeneracional(poblacion, true, prob_generacional, prob_mutacion, stop, f, d);
                tEnd = System.currentTimeMillis();
                logger.log(Level.INFO,"The task has taken " + (tEnd - tStart) + " milliseconds.");
                poblacion.clear();
                for (int i = 0; i < nIndividuos; i++) {
                    poblacion.add(new Individuo(f.length));
                    poblacion.get(i).evaluar(f, d);
                }
                logger.log(Level.INFO," | VERSIÓN: GENERACIONAL | PMX OFF");
                tStart = System.currentTimeMillis();
                algoritmoGeneticoGeneracional(poblacion, false, prob_generacional, prob_mutacion, stop, f, d);
                tEnd = System.currentTimeMillis();
                logger.log(Level.INFO,"The task has taken " + (tEnd - tStart) + " milliseconds.");
                poblacion.clear();
            }

            fileHandler.close();

            fileHandler = new FileHandler("./" + a.substring(0, 5) + "_estacionario.log");
            fileHandler.setFormatter(simpleFormatter);
            logger.addHandler(fileHandler);
            logger.log(Level.INFO, "Iniciando ejecución, fichero: " + a);

            if ( estacionario ) {
                System.out.println("Ejecutando versión estacionaria | Almacenando en fichero: " + a.substring(0, 5) + "_estacionario.log");
                for (int i = 0; i < nIndividuos; i++) {
                    poblacion.add(new Individuo(f.length));
                    poblacion.get(i).evaluar(f, d);
                }
                logger.log(Level.INFO," | VERSIÓN: ESTACIONARIA | PMX ON");
                tStart = System.currentTimeMillis();
                algoritmoGeneticoEstacionario(poblacion, true, prob_mutacion, stop, f, d);
                tEnd = System.currentTimeMillis();
                logger.log(Level.INFO,"The task has taken " + (tEnd - tStart) + " milliseconds.");
                poblacion.clear();
                for (int i = 0; i < nIndividuos; i++) {
                    poblacion.add(new Individuo(f.length));
                    poblacion.get(i).evaluar(f, d);
                }
                logger.log(Level.INFO," | VERSIÓN: ESTACIONARIA | PMX OFF");
                tStart = System.currentTimeMillis();
                algoritmoGeneticoEstacionario(poblacion, false, prob_mutacion, stop, f, d);
                tEnd = System.currentTimeMillis();
                logger.log(Level.INFO,"The task has taken " + (tEnd - tStart) + " milliseconds.");
                poblacion.clear();
            }
            fileHandler.close();
        }
    }

    public static void algoritmoGeneticoGeneracional(List<Individuo> poblacion, boolean PMX, double prob_cruce, double prob_mutacion, int stop, int[][] f, int[][] d) {
        Individuo mejor = obtenerMejorIndividuo(poblacion);
        Individuo elite = obtenerMejorIndividuo(poblacion);
        Individuo nuevoMejor;
        Individuo aux;
        int tamPoblacion = poblacion.size();
        int eval = 0;
        int t = 0;
        int mejorGeneracion = 0;
        Random rnd = new Random();
        double ejCruce;

        List<Individuo> ganadoresTorneo = new ArrayList<>();
        List<Individuo> hijos = new ArrayList<>();

        do {
            t++;
            ganadoresTorneo.clear();
            ////////////////////////////////////////////
            //// PROCESO DE SELECCIÓN DE INDIVIDUOS ////
            ////         POR TORNEO, K = 2          ////
            ////////////////////////////////////////////

            for ( int i = 0; i < tamPoblacion; i++ ) {
                ganadoresTorneo.add(seleccionPorTorneo(poblacion));
            }

            ////////////////////////////////////////////
            ////   RECOMBINACIÓN DE DESCENDIANTES   ////
            ////    MEDIANTE CRUCE EN ORDEN Y PMX   ////
            ////////////////////////////////////////////

            ejCruce = rnd.nextDouble();
            Individuo hijo1, hijo2;

            hijos.clear();
            for ( int i = 0; i < tamPoblacion-1; i++ ) {
                if ( PMX ) {
                    if ( ejCruce < prob_cruce ) {
                        hijos.add(crucePMX(ganadoresTorneo.get(i), ganadoresTorneo.get(i + 1)));
                        hijos.add(crucePMX(ganadoresTorneo.get(i + 1), ganadoresTorneo.get(i)));
                    } else {
                        hijos.add(ganadoresTorneo.get(i));
                        hijos.add(ganadoresTorneo.get(i+1));
                    }
                } else {
                    if ( ejCruce < prob_cruce ) {
                        hijos.add(cruceEnOrden(ganadoresTorneo.get(i), ganadoresTorneo.get(i + 1)));
                        hijos.add(cruceEnOrden(ganadoresTorneo.get(i + 1), ganadoresTorneo.get(i)));
                    } else {
                        hijos.add(ganadoresTorneo.get(i));
                        hijos.add(ganadoresTorneo.get(i+1));
                    }
                }

                ////////////////////////////////////////////
                ////   MUTACIÓN DE LOS DESCENCIENTES    ////
                ////      OBTENIDOS EN LOS CRUCES       ////
                ////////////////////////////////////////////
                hijos.get(i).mutacion(prob_mutacion);
            }

            //hijos.add(new Individuo(hijos.get(0).getGenotipo().length));
            poblacion.clear();
            for ( int i = 0; i < tamPoblacion; i++ ) {
                //////////////////////////////////////////////
                ////   EVALUACIÓN DE LOS DESCENCIENTES    ////
                ////      OBTENIDOS TRAS MUTAR GENES      ////
                //////////////////////////////////////////////
                hijos.get(i).evaluar(f, d);
                eval++;
                poblacion.add(hijos.get(i));
            }

            if ( !poblacion.contains(elite) ) {
                Individuo peor = obtenerPeorIndividuo(poblacion);
                if ( elite.getValor() < peor.getValor() ) {
                    peor.setId(elite.getId());
                    peor.setGenotipo(elite.getGenotipo());
                    peor.setValor(elite.getValor());
                }
            }

            nuevoMejor = obtenerMejorIndividuo(poblacion);
            if ( nuevoMejor.getValor() < mejor.getValor() ) {
                logger.log(Level.INFO, "CAMBIO DE ÉLITE: " + elite.toString());
                logger.log(Level.INFO, mejor.toString() + " ha sido el mejor durante " + mejorGeneracion + " generaciones. | DESDE LA " + (t-mejorGeneracion) + " HASTA LA " + (t) + "." );
                mejor.setId(nuevoMejor.getId());
                mejor.setGenotipo(nuevoMejor.getGenotipo());
                mejor.setValor(nuevoMejor.getValor());
                mejorGeneracion = 0;
            } else {
                mejorGeneracion++;
            }

        } while ( eval < stop );

        logger.log(Level.INFO, mejor.toString() + " ha sido el mejor durante " + mejorGeneracion + " generaciones. | DESDE LA " + (t-mejorGeneracion+1) + " HASTA LA " + (t+1) + "." );
        logger.log(Level.INFO, "Así el mejor final es: " + mejor.toString() + " | obtenido en la generación " + (t-mejorGeneracion+1));

    }

    public static void algoritmoGeneticoEstacionario(List<Individuo> poblacion, boolean PMX, double prob_mutacion, int stop, int[][] f, int[][] d) {
        Individuo mejor = poblacion.get(0), nuevoMejor;
        int t = 0, mejorGeneracion = 0, eval = 0;
        List<Individuo> ganadoresTorneo = new ArrayList<>();
        Random rnd = new Random();
        double ejCruce;

        do {
            ganadoresTorneo.clear();
            t++;

            ////////////////////////////////////////////
            //// PROCESO DE SELECCIÓN DE INDIVIDUOS ////
            ////         POR TORNEO, K = 2          ////
            ////////////////////////////////////////////

            ganadoresTorneo.add(seleccionPorTorneo(poblacion));
            ganadoresTorneo.add(seleccionPorTorneo(poblacion));

            ////////////////////////////////////////////
            ////   RECOMBINACIÓN DE DESCENDIANTES   ////
            ////    MEDIANTE CRUCE EN ORDEN Y PMX   ////
            ////////////////////////////////////////////

            ejCruce = rnd.nextDouble();
            Individuo hijo1, hijo2;

            if ( PMX ) {
                hijo1 = crucePMX(ganadoresTorneo.get(0), ganadoresTorneo.get(1));
                hijo2 = crucePMX(ganadoresTorneo.get(1), ganadoresTorneo.get(0));
            } else {
                hijo1 = cruceEnOrden(ganadoresTorneo.get(0), ganadoresTorneo.get(1));
                hijo2 = cruceEnOrden(ganadoresTorneo.get(1), ganadoresTorneo.get(0));
            }

            ////////////////////////////////////////////
            ////   MUTACIÓN DE LOS DESCENCIENTES    ////
            ////      OBTENIDOS EN LOS CRUCES       ////
            ////////////////////////////////////////////

            hijo1.mutacion(prob_mutacion);
            hijo2.mutacion(prob_mutacion);

            //////////////////////////////////////////////
            ////   EVALUACIÓN DE LOS DESCENCIENTES    ////
            ////      OBTENIDOS TRAS MUTAR GENES      ////
            //////////////////////////////////////////////

            //TODO: Revisar
            hijo1.evaluar(f, d);
            hijo2.evaluar(f, d);
            eval += 2;

            ////////////////////////////////////////////
            ////  COMPARACIÓN DE LOS DESCENCIENTES  ////
            ////      PARA SABER QUIEN TIENE        ////
            ////    MÁS PRIORIDAD PARA SUBSISTIR    ////
            ////////////////////////////////////////////

            Individuo mejores[] = new Individuo[2];

            if ( hijo1.getValor() < hijo2.getValor() ) {
                mejores[0] = hijo1;
                mejores[1] = hijo2;
            } else {
                mejores[0] = hijo2;
                mejores[1] = hijo1;
            }

            ////////////////////////////////////////////
            ////   SELECCIÓN DE LOS PEORES INDIV    ////
            ////   DE LA POBLACIÓN SIN TENER EN     ////
            ////      CUENTA LOS DESCENDIENTES      ////
            ////////////////////////////////////////////

            Individuo peor1, peor2;
            peor1 = poblacion.get(0);
            peor2 = poblacion.get(poblacion.size()-1);

            int posPeor1 = 0;
            int posPeor2 = poblacion.size()-1;
            Individuo peores[] = new Individuo[2];

            for ( int i = 0; i < poblacion.size()-1; i++ ) {
                if ( peor1.getValor() < poblacion.get(i).getValor() && peor1.getValor() != peor2.getValor() ) {
                    peor1 = poblacion.get(i);
                    posPeor1 = i;
                }
            }

            ////////////////////////////////////////////
            ////   COMPARACIÓN Y REEMPLAZAMIENTO    ////
            ////   DE INDIVIDUOS DESCENDIENTES Y    ////
            ////     PEORES, SE REEMPLAZA SII       ////
            ////           SON MEJORES              ////
            ////////////////////////////////////////////

            for ( int i = 1; i < poblacion.size(); i++ ) {
                if ( peor2.getValor() < poblacion.get(i).getValor() && peor1.getValor() != peor2.getValor() ) {
                    peor2 = poblacion.get(i);
                    posPeor2 = i;
                }
            }

            if ( peor1.getValor() < peor2.getValor() ) {
                if ( mejores[0].getValor() < peor2.getValor() ) poblacion.set(posPeor2, mejores[0]);
                if ( mejores[1].getValor() < peor1.getValor() ) poblacion.set(posPeor1, mejores[1]);
            } else {
                if ( mejores[0].getValor() < peor1.getValor() ) poblacion.set(posPeor1, mejores[0]);
                if ( mejores[1].getValor() < peor2.getValor() ) poblacion.set(posPeor2, mejores[1]);
            }

            ////////////////////////////////////////////////
            ////   SE REEVALUA LA POBLACIÓN COMPLETA    ////
            ////////////////////////////////////////////////

            for ( Individuo individuo : poblacion ) {
                individuo.evaluar(f, d);
                eval++;
            }

            ////////////////////////////////////////////////
            ////   GESTIÓN DE INFORMACIÓN PARA EL LOG   ////
            ////////////////////////////////////////////////

            nuevoMejor = obtenerMejorIndividuo(poblacion);
            if ( nuevoMejor.getValor() < mejor.getValor() ) {
                logger.log(Level.INFO, mejor.toString() + " ha sido el mejor durante " + mejorGeneracion + " generaciones. \t| DESDE LA " + (t-mejorGeneracion) + " HASTA LA " + (t) + "." );
                mejor.setId(nuevoMejor.getId());
                mejor.setGenotipo(nuevoMejor.getGenotipo());
                mejor.setValor(nuevoMejor.getValor());
                mejorGeneracion = 0;
            } else {
                mejorGeneracion++;
            }
        } while ( eval < stop );

        logger.log(Level.INFO, mejor.toString() + " ha sido el mejor durante " + mejorGeneracion + " generaciones. | DESDE LA " + (t-mejorGeneracion+1) + " HASTA LA " + (t+1) + "." );
        logger.log(Level.INFO, "Así el mejor final es: " + mejor.toString() + " | obtenido en la generación " + (t-mejorGeneracion+1));
    }

    public static Individuo obtenerMejorIndividuo(List<Individuo> poblacion) {
        Individuo mejor = poblacion.get(0);
        for ( Individuo i : poblacion ) {
            if ( i.getValor() < mejor.getValor() )
                mejor = i;
        }
        return mejor;
    }

    public static Individuo obtenerPeorIndividuo(List<Individuo> poblacion) {
        Individuo peor = poblacion.get(0);
        for ( Individuo i : poblacion ) {
            if ( i.getValor() > peor.getValor() )
                peor = i;
        }
        return peor;
    }

    //TODO: Aplicar LOGS
    public static Individuo cruceEnOrden(Individuo padre1, Individuo padre2) {
        Random r = new Random();
        int tamGenotipo = padre1.getGenotipo().length;
        Individuo hijo = new Individuo(tamGenotipo);
        int[] genotipoPadre1 = padre1.getGenotipo();
        int[] genotipoPadre2 = padre2.getGenotipo();
        int[] genotipoHijo = new int[tamGenotipo];
        boolean insertado = false;
        int corte1, corte2;

        do {
            corte1 = r.nextInt(tamGenotipo);
            corte2 = r.nextInt(tamGenotipo)+1;
        } while ( corte1 >= corte2 );

        for ( int i = 0; i < genotipoHijo.length; i++ ) {
            genotipoHijo[i] = -1;
        }

        for ( int i = corte1; i < corte2; i++ ) {
            genotipoHijo[i] = genotipoPadre1[i];
        }

        for ( int i = 0; i < genotipoPadre2.length; i++ ) {
            for ( int j = 0; j < genotipoHijo.length; j++ ) {
                if ( genotipoPadre2[i] == genotipoHijo[j] ) {
                    insertado = true;
                }
            }
            if ( !insertado ) {
                for ( int k = corte2; k < genotipoHijo.length; k++ ) {
                    if ( genotipoHijo[k] == -1 ) {
                        genotipoHijo[k] = genotipoPadre2[i];
                        break;
                    }
                }
            }
            insertado = false;
        }

        for ( int i = 0; i < genotipoPadre2.length; i++ ) {
            for ( int j = 0; j < genotipoHijo.length; j++ ) {
                if ( genotipoPadre2[i] == genotipoHijo[j] ) {
                    insertado = true;
                }
            }
            if ( !insertado ) {
                for ( int k = 0; k < corte1; k++ ) {
                    if ( genotipoHijo[k] == -1 ) {
                        genotipoHijo[k] = genotipoPadre2[i];
                        break;
                    }
                }
            }
            insertado = false;
        }

        hijo.setGenotipo(genotipoHijo);
        return hijo;
    }

    //TODO: APLICAR LOGS
    public static Individuo crucePMX(Individuo padre1, Individuo padre2) {
        Random r = new Random();
        int tamGenotipo = padre1.getGenotipo().length;
        Individuo hijo = new Individuo(tamGenotipo);
        int[] genotipoPadre1 = padre1.getGenotipo();
        int[] genotipoPadre2 = padre2.getGenotipo();
        int[] genotipoHijo = new int[padre1.getGenotipo().length];
        int corte1, corte2;
        boolean insertado = false;

        do {
            corte1 = r.nextInt(tamGenotipo);
            corte2 = r.nextInt(tamGenotipo)+1;
        } while ( corte1 >= corte2 );

        for ( int i = 0; i < genotipoHijo.length; i++ ) {
            genotipoHijo[i] = -1;
        }

        //TODO: Revisar que esto compara todos los posibles inicios
        int iniciosPadre1[] = new int[(corte2-corte1)+1];
        for ( int i = 0; i < iniciosPadre1.length-1; i++ ) {
            iniciosPadre1[i] = padre1.getGenotipo()[corte1+i];
        }

        for ( int i = corte1; i < corte2; i++ ) {
            genotipoHijo[i] = genotipoPadre2[i];
        }

        for ( int n : iniciosPadre1 ) {
            boolean continuar = false;
            boolean encontrado = false;
            int busq = n;
            int busqAux = n;
            int posicion = corte1;
            int posicion2 = corte1;

            do {
                for ( int i = 0; i < genotipoHijo.length; i++ ) {
                    if ( n == genotipoHijo[i] ) {
                        encontrado = true;
                        continuar = false;
                        break;
                    }
                }

                if ( !encontrado ) {
                    for ( int i = 0; i < genotipoPadre1.length; i++ ) {
                        if ( busq == genotipoPadre1[i] ) {
                            posicion = i;
                            busqAux = genotipoPadre2[i];
                            break;
                        }
                    }

                    for (int i = 0; i < genotipoPadre1.length; i++) {
                        if (busqAux == genotipoPadre1[i]) {
                            if ( genotipoHijo[i] == -1) {
                                genotipoHijo[i] = n;
                                continuar = false;
                                break;
                            } else {
                                busq = busqAux;
                                continuar = true;
                                break;
                            }
                        }
                    }
                }
            } while ( continuar );
            hijo.setGenotipo(genotipoHijo);
        }

        for ( int i = 0; i < genotipoPadre2.length; i++ ) {
            for ( int j = 0; j < genotipoHijo.length; j++ ) {
                if ( genotipoPadre2[i] == genotipoHijo[j] ) {
                    insertado = true;
                }
            }
            if ( !insertado ) {
                for ( int k = corte2; k < genotipoHijo.length; k++ ) {
                    if ( genotipoHijo[k] == -1 ) {
                        genotipoHijo[k] = genotipoPadre2[i];
                        break;
                    }
                }
            }
            insertado = false;
        }

        for ( int i = 0; i < genotipoPadre2.length; i++ ) {
            for ( int j = 0; j < genotipoHijo.length; j++ ) {
                if ( genotipoPadre2[i] == genotipoHijo[j] ) {
                    insertado = true;
                }
            }
            if ( !insertado ) {
                for ( int k = 0; k < corte1; k++ ) {
                    if ( genotipoHijo[k] == -1 ) {
                        genotipoHijo[k] = genotipoPadre2[i];
                        break;
                    }
                }
            }
            insertado = false;
        }

        hijo.setGenotipo(genotipoHijo);

        return hijo;
    }

    public static Individuo seleccionPorTorneo(List<Individuo> poblacion) {
        Random r = new Random();
        int individuo1, individuo2;

        do {
            individuo1 = r.nextInt(poblacion.size());
            individuo2 = r.nextInt(poblacion.size());
        } while ( individuo1 == individuo2);

        return ( poblacion.get(individuo1).getValor() < poblacion.get(individuo2).getValor() ) ? poblacion.get(individuo1) : poblacion.get(individuo2);
    }

    public static Properties inicializarPropiedades() {
        try {
            Properties configuracion = new Properties();

            configuracion.load(new FileInputStream("main.properties"));

            return configuracion;
        } catch (IOException e) {
            System.out.println("Error al leer el fichero");
            return null;
        }
    }

    public static String readFile(String archivo) throws IOException {
        String result = "", line;
        FileReader f = new FileReader(archivo);
        BufferedReader br = new BufferedReader(f);
        while((line = br.readLine()) != null) {
            result = result.concat(line + "\n");
        }
        br.close();
        return result;
    }

    public static int[][] dataGestFrecuencias(String text) throws IOException {
        String[] numbers;
        numbers = text.split("\\s+");

        int tam = Integer.parseInt(numbers[0]);
        int frecuencias[][] = new int[tam][tam];

        int i = 1, j = 0, k = 0;

        do {
            frecuencias[k][j] = Integer.parseInt(numbers[i]);
            j++;

            if ( j - tam == 0 ) {
                j = 0;
                k++;
            }

            i++;
        } while ( i < (tam*tam) );

        return frecuencias;
    }

    public static int[][] dataGestLocalizaciones(String text) throws IOException {
        String[] numbers;
        numbers = text.split("\\s+");

        int tam = Integer.parseInt(numbers[0]);
        int localizaciones[][] = new int[tam][tam];

        int i = 1, j = 0, k = 0;

        do {
            localizaciones[k][j] = Integer.parseInt(numbers[i+(tam*tam)]);
            j++;

            if ( j - tam == 0 ) {
                j = 0;
                k++;
            }

            i++;
        } while ( i < (tam*tam) );

        return localizaciones;
    }
}
