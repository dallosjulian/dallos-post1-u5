package com.ejemplo.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Modelo de dominio para representar una Tarea en el sistema.
 * Cumple con la especificación JavaBean para integración con Servlet y JSP/EL.
 */
public class Tarea implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String titulo;
    private boolean completada;
    private String categoria;
    private String prioridad; // "Alta", "Media", "Baja"
    private Date fechaLimite;

    /**
     * Constructor por defecto requerido por el estándar JavaBean.
     */
    public Tarea() {
        this.completada = false;
    }

    /**
     * Constructor completo para inicializar una tarea con todos sus atributos clave.
     *
     * @param id          Identificador único de la tarea
     * @param titulo      Título descriptivo de la tarea
     * @param categoria   Categoría temática (ej. "Estudio", "Evaluación", "General")
     * @param prioridad   Nivel de prioridad ("Alta", "Media", "Baja")
     * @param fechaLimite Fecha límite asignada para la tarea
     */
    public Tarea(int id, String titulo, String categoria, String prioridad, Date fechaLimite) {
        this.id = id;
        this.titulo = titulo;
        this.categoria = categoria;
        this.prioridad = prioridad;
        this.fechaLimite = fechaLimite;
        this.completada = false;
    }

    /**
     * Constructor extendido con estado de completitud.
     */
    public Tarea(int id, String titulo, boolean completada, String categoria, String prioridad, Date fechaLimite) {
        this.id = id;
        this.titulo = titulo;
        this.completada = completada;
        this.categoria = categoria;
        this.prioridad = prioridad;
        this.fechaLimite = fechaLimite;
    }

    // --- Getters y Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isCompletada() {
        return completada;
    }

    public boolean getCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", completada=" + completada +
                ", categoria='" + categoria + '\'' +
                ", prioridad='" + prioridad + '\'' +
                ", fechaLimite=" + fechaLimite +
                '}';
    }
}
