package me.adpl;

import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Individuo {
    private static final AtomicInteger cuenta = new AtomicInteger(0);
    private int id;
    private int valor;
    private boolean evaluado;
    private int[] genotipo;

    public Individuo(int tamGenotipo) {
        this.id = cuenta.incrementAndGet();
        this.evaluado = true;
        this.genotipo = generarGenotipo(tamGenotipo);
    }

    public Individuo(int valor, int generacion, boolean evaluado, int tamGenotipo) {
        this.id = cuenta.incrementAndGet();
        this.valor = valor;
        this.evaluado = evaluado;
        this.genotipo = generarGenotipo(tamGenotipo);
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public boolean isEvaluado() {
        return evaluado;
    }

    public void setEvaluado(boolean evaluado) {
        this.evaluado = evaluado;
    }

    public int[] getGenotipo() {
        return genotipo;
    }

    public void setGenotipo(int[] genotipo) {
        this.genotipo = genotipo;
    }

    public void setGenotipo(int pos, int v) {
        this.genotipo[pos] = v;
    }

    private int[] generarGenotipo(int tamGenotipo) {
        int genotipo[] = new int[tamGenotipo];
        Random rnd = new Random();
        rnd.setSeed(2123213);
        Set<Integer> generados = new HashSet<>();
        for (int i = 0; i < tamGenotipo; i++) {
            int aleatorio = -1;
            boolean generado = false;
            while (!generado) {
                int posible = rnd.nextInt(tamGenotipo);
                if (!generados.contains(posible)) {
                    generados.add(posible);
                    aleatorio = posible;
                    generado = true;
                }
            }
            genotipo[i] = aleatorio;
        }
        return genotipo;
    }

    //TODO: No evaluar si está evaluado - gestionar
    public void evaluar(int[][] f, int[][] d) {
        int suma = 0;

        for (int i = 0; i < f.length; i++) {
            for (int j = 0; j < d.length; j++) {
                if ( i != j ) {
                    suma += f[i][j] * d[genotipo[j]][genotipo[i]];
                }
            }
        }

        this.valor = suma;
    }

    public void mutacion(double prob_mutacion) {
        Random rnd = new Random();
        List<Pair<Integer, Integer>> mutados = new ArrayList<>();
        double probabilidad = prob_mutacion * genotipo.length;
        double random;
        for ( int i = 0; i < genotipo.length; i++ ) {
            random = rnd.nextDouble();

            if ( random < 0.5 ) {
                mutados.add(new Pair<>(i, genotipo[i]));
            }
        }

        Pair<Integer, Integer> gen1, gen2;
        while ( mutados.size() >= 2 ) {
            gen1 = mutados.remove(rnd.nextInt(mutados.size()));
            gen2 = mutados.remove(rnd.nextInt(mutados.size()));
            genotipo[gen1.getKey()] = gen2.getValue();
            genotipo[gen2.getKey()] = gen1.getValue();
            genotipo.toString();
        }

        if ( mutados.size() != 0 ) {
            gen1 = mutados.remove(0);
            int posGen2 = rnd.nextInt(genotipo.length);
            int valorGen = genotipo[posGen2];
            gen2 = new Pair<>(posGen2, valorGen);
            genotipo[gen1.getKey()] = gen2.getValue();
            genotipo[gen2.getKey()] = gen1.getValue();
        }
    }

    @Override
    public boolean equals(Object o) {
        if ( o == null ) return false;
        Individuo individuo = (Individuo) o;
        return ( this.genotipo == ((Individuo) o).getGenotipo() && this.valor == ((Individuo) o).getValor() ) ? true : false;
    }

    @Override
    public String toString() {
        String cadena = "";
        cadena += "Individuo " + id + ": ";
        for ( int i = 0; i < genotipo.length; i++ ) {
            if ( genotipo[i] == - 1 )
                cadena += "X ";
            else
                cadena += genotipo[i] + " ";
        }
        cadena += " = " + valor;
        return cadena;
    }
}
