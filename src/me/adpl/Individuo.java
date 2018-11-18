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

    public Individuo() {
        this.id = cuenta.incrementAndGet();
        this.generacion = 0;
        this.evaluado = true;
        this.genotipo = generarGenotipo();
        this.evaluar();
    };

    public Individuo(int valor, int generacion, boolean evaluado) {
        this.id = cuenta.incrementAndGet();
        this.valor = valor;
        this.generacion = generacion;
        this.evaluado = evaluado;
        this.genotipo = generarGenotipo();
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
        for ( int i = 0; i < 9; i++ ) {
            this.genotipo[i] = 0;
            if ( i >= inicio && i <= fin ) {
                this.genotipo[i] = genotipo[i];
            }
        }
    }

    public void setGenotipo(int pos, int v) {
        this.genotipo[pos] = v;
    }

    private int[] generarGenotipo() {
        int genotipo[] = new int[9];
        Random rnd = new Random();
        Set<Integer> generados = new HashSet<>();
        for ( int i = 0; i < 9; i++ ) {
            genotipo[i] = rnd.nextInt(9);
        }
        return genotipo;
    }

    public void evaluar() {
        int valor = 0;
        for ( int i = 0; i < 9; i++ ) {
            valor += genotipo[i] * i;
        }
        this.valor = valor;
    }

    public void mostrarGenotipo() {
        System.out.print("Individuo " + id + ": ");
        for ( int i = 0; i < 9; i++ ) {
            System.out.print(genotipo[i] + " ");
        }
        this.evaluar();
        System.out.println(" = " + valor);
    }

    @Override
    public String toString() { return "Individuo " + id + ": " + valor; }
}
