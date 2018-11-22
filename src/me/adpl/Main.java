package me.adpl;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Properties configuracion = inicializarPropiedades();
        int stop = Integer.parseInt(configuracion.getProperty("limite_evaluaciones"));
        int nIndividuos = Integer.parseInt(configuracion.getProperty("poblacion"));

        List<Individuo> poblacion = new ArrayList<>();

        String contenido = readFile("cnf01.dat");

        int f[][] = dataGestFrecuencias(contenido);
        int d[][] = dataGestLocalizaciones(contenido);

        for ( int i = 0; i < nIndividuos; i++ ) {
            poblacion.add(new Individuo(f.length));
        }

        algoritmoGeneticoEstacionario(poblacion, stop, f, d);
    }


    //TODO: Falta mantener el Élite
    public static void algoritmoGeneticoEstacionario(List<Individuo> poblacion, int stop, int[][] f, int[][] d) {
        int t = 0;
        List<Individuo> ganadoresTorneo = new ArrayList<>();

        do {
            t++;
            for ( Individuo individuo : poblacion ) {
                individuo.evaluar(f, d);
            }
            // SELECCIÓN DE INDIVIDUOS POR TORNEO
            ganadoresTorneo.add(evaluacionPorTorneo(poblacion));
            ganadoresTorneo.add(evaluacionPorTorneo(poblacion));

            Individuo hijo1 = crucePMX(ganadoresTorneo.get(0), ganadoresTorneo.get(1));
            Individuo hijo2 = crucePMX(ganadoresTorneo.get(1), ganadoresTorneo.get(0));

            Individuo peor1, peor2;
            peor1 = poblacion.get(0);
            peor2 = poblacion.get(poblacion.size()-1);

            int posPeor1 = 0;
            int posPeor2 = 49;
            Individuo peores[] = new Individuo[2];

            for ( int i = 0; i < poblacion.size(); i++ ) {
                if ( peor1.getValor() < poblacion.get(i).getValor() ) {
                    peor1 = poblacion.get(i);
                    posPeor1 = i;
                }
                if ( poblacion.get(i).getValor() != peor1.getValor() && peor2.getValor() < poblacion.get(i).getValor() ) {
                    peor2 = poblacion.get(i);
                    posPeor2 = i;
                }
            }

            hijo1.evaluar(f, d);
            hijo2.evaluar(f, d);

            hijo1 = mutacion(hijo1);
            hijo2 = mutacion(hijo2);

            Individuo mejores[] = new Individuo[2];

            if ( hijo1.getValor() < hijo2.getValor() ) {
                mejores[0] = hijo1;
                mejores[1] = hijo2;
            } else {
                mejores[0] = hijo2;
                mejores[1] = hijo1;
            }

            if ( mejores[0].getValor() < peor1.getValor() ) {
                poblacion.set(posPeor1, mejores[0]);
                mejores[0] = peor1;
            }

            if ( mejores[0].getValor() < mejores[1].getValor() ) {
                if ( mejores[0].getValor() < peor2.getValor() ) {
                    poblacion.set(posPeor2, mejores[0]);
                }
            } else {
                if ( mejores[1].getValor() < peor2.getValor() ) {
                    poblacion.set(posPeor2, mejores[1]);
                }
            }

            for ( Individuo individuo : poblacion ) {
                individuo.evaluar(f, d);
            }

        } while ( t < stop );

        for ( Individuo i : poblacion ) {
            i.mostrarGenotipo();
        }
    }

    //TODO: Aplicar LOGS
    public static Individuo cruceEnOrden(Individuo padre1, Individuo padre2) {
        Random r = new Random();
        Individuo hijo = new Individuo(22);
        int[] genotipoPadre1 = padre1.getGenotipo();
        int[] genotipoPadre2 = padre2.getGenotipo();
        int[] genotipoHijo = new int[padre1.getGenotipo().length];
        boolean insertado = false;
        int corte1, corte2;

        do {
            corte1 = r.nextInt(22);
            corte2 = r.nextInt(21)+1;
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
        Individuo hijo = new Individuo(22);
        int[] genotipoPadre1 = padre1.getGenotipo();
        int[] genotipoPadre2 = padre2.getGenotipo();
        int[] genotipoHijo = new int[padre1.getGenotipo().length];
        int corte1, corte2;
        boolean insertado = false;

        do {
            corte1 = r.nextInt(22);
            corte2 = r.nextInt(21)+1;
        } while ( corte1 >= corte2 );

        for ( int i = 0; i < genotipoHijo.length; i++ ) {
            genotipoHijo[i] = -1;
        }

        int iniciosPadre1[] = new int[(corte2-corte1)+1];
        for ( int i = 0; i < iniciosPadre1.length; i++ ) {
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
        //hijo.mostrarGenotipo();

        return hijo;
    }

    public static Individuo mutacion(Individuo primero) {
        Random rnd = new Random();
        List<Integer> mutados = new ArrayList<>();
        int[] genotipoPrimero = primero.getGenotipo();
        double probabilidad = 0.001 * genotipoPrimero.length;
        double random;

        for ( int i = 0; i < genotipoPrimero.length; i++ ) {
            random = rnd.nextDouble();
            if ( random < 0.5 ) {
                mutados.add(i);
            }
        }

        for ( int i = 0; i < (mutados.size()-1); i++ ) {
            int pos = mutados.get(i);
            int pos2 = mutados.get(i+1);
            int aux = genotipoPrimero[pos];
            genotipoPrimero[pos] = genotipoPrimero[pos2];
            genotipoPrimero[pos2] = aux;
        }

        return primero;
    }

    public static Individuo evaluacionPorTorneo(List<Individuo> poblacion) {
        Random r = new Random();
        int individuo1, individuo2;

        do {
            individuo1 = r.nextInt(poblacion.size());
            individuo2 = r.nextInt(poblacion.size());
        } while ( individuo1 == individuo2);

        if ( poblacion.get(individuo1).getValor() < poblacion.get(individuo2).getValor() )
            return poblacion.get(individuo1);
        else
            return poblacion.get(individuo2);
    }

    public static Properties inicializarPropiedades() {
        try {
            Properties configuracion = new Properties();

            configuracion.load(new FileInputStream("main.properties"));

            return configuracion;
        } catch (IOException e) {
            //System.out.println("Error: No se pudo leer el fichero");
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
