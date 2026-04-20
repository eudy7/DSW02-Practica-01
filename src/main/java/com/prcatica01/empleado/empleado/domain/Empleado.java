package com.prcatica01.empleado.empleado.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "empleado")
public class Empleado {

    @EmbeddedId
    private EmpleadoId id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "direccion", nullable = false, length = 100)
    private String direccion;

    @Column(name = "telefono", nullable = false, length = 100)
    private String telefono;

    protected Empleado() {
    }

    public Empleado(EmpleadoId id, String nombre, String direccion, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public EmpleadoId getId() {
        return id;
    }

    public String getClave() {
        return id.getClavePrefijo() + "-" + id.getClaveNumero();
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void actualizarDatos(String nuevoNombre, String nuevaDireccion, String nuevoTelefono) {
        this.nombre = nuevoNombre;
        this.direccion = nuevaDireccion;
        this.telefono = nuevoTelefono;
    }
}
