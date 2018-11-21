package me.adpl;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Individuo {
    private static final AtomicInteger cuenta = new AtomicInteger(0);
    private int id;
    private int valor;
    private int generacion;
    private boolean evaluado;
    private int[] genotipo;

    public Individuo(int tamGenotipo) {
        this.id = cuenta.incrementAndGet();
        this.generacion = 0;
        this.evaluado = true;
        this.genotipo = generarGenotipo(tamGenotipo);
    }

    public Individuo(int valor, int generacion, boolean evaluado, int tamGenotipo) {
        this.id = cuenta.incrementAndGet();
        this.valor = valor;
        this.generacion = generacion;
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

    public int getGeneracion() {
        return generacion;
    }

    public void setGeneracion(int generacion) {
        this.generacion = generacion;
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

    public void setGenotipo(int[] genotipo, int inicio, int fin) {
        for ( int i = 0; i < 22; i++ ) {
            this.genotipo[i] = -1;
            if ( i >= inicio && i <= fin ) {
                this.genotipo[i] = genotipo[i];
            }
        }
    }

    public void setGenotipo(int pos, int v) {
        this.genotipo[pos] = v;
    }

    private int[] generarGenotipo(int tamGenotipo) {
        int genotipo[] = new int[tamGenotipo];
        Random rnd = new Random();
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

    public void mostrarGenotipo() {
        System.out.print("Individuo " + id + ": ");
        for ( int i = 0; i < 22; i++ ) {
            if ( genotipo[i] == - 1 )
                System.out.print("X ");
            else
                System.out.print(genotipo[i] + " ");
        }
        System.out.println(" = " + valor);
    }

    public void mutacion() {
        Random rnd = new Random();
        List<Integer> mutados = new ArrayList<>();
        double probabilidad = 0.001 * genotipo.length;
        double random;
        for ( int i = 0; i < genotipo.length; i++ ) {
            random = rnd.nextDouble();
            if ( random < 0.5 ) {
                //System.out.print(" | MUTA gen " + i);
                mutados.add(i);
            }
        }
        ////System.out.println();
        for ( int i = 0; i < (mutados.size()-1); i++ ) {
            int pos = mutados.get(i);
            int pos2 = mutados.get(i+1);
            int aux = genotipo[pos];
            genotipo[pos] = genotipo[pos2];
            genotipo[pos2] = aux;
        }
    }

    @Override
    public String toString() { return "Individuo " + id + ": " + valor; }
}
