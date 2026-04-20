package com.prcatica01.empleado.departamento.domain;

import com.prcatica01.empleado.empleado.domain.Empleado;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "empleado_departamento_historial")
public class HistorialAsignacionDepartamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
        @JoinColumn(name = "empleado_clave_prefijo", referencedColumnName = "clave_prefijo", nullable = false),
        @JoinColumn(name = "empleado_clave_numero", referencedColumnName = "clave_numero", nullable = false)
    })
    private Empleado empleado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @Column(name = "fecha_inicio", nullable = false)
    private Instant fechaInicio;

    @Column(name = "fecha_fin")
    private Instant fechaFin;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected HistorialAsignacionDepartamento() {
    }

    public HistorialAsignacionDepartamento(Empleado empleado, Departamento departamento, Instant fechaInicio) {
        this.empleado = empleado;
        this.departamento = departamento;
        this.fechaInicio = fechaInicio;
        this.createdAt = fechaInicio;
    }

    public Long getId() {
        return id;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public Instant getFechaInicio() {
        return fechaInicio;
    }

    public Instant getFechaFin() {
        return fechaFin;
    }

    public boolean isActiva() {
        return fechaFin == null;
    }

    public void cerrar(Instant fechaCierre) {
        this.fechaFin = fechaCierre;
    }
}
