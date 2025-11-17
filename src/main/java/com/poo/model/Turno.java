/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.poo.model;

import java.time.LocalDateTime;

/**
 *
 * @author cabra
 */
public class Turno {
    private int id;
    private EstadoDeTurno estado;
    private LocalDateTime fechaYHoraInicio;
    private LocalDateTime fechaYHoraFin;
    private Paciente paciente;
    private Profesional profesional;
    

    //Getters y Setters
    public void setId(int id){
        this.id = id;
    }
        public int getId(){
            return id;
        }
    
    public void setEstado(EstadoDeTurno estado){
        this.estado = estado;
    }
        public EstadoDeTurno getEstado(){
            return estado;
        }
    
    public void setFechaYHoraInicio(LocalDateTime fechaYHoraInicio) {
    this.fechaYHoraInicio = fechaYHoraInicio;
    }
        public LocalDateTime getFechaYHoraInicio(){
            return fechaYHoraInicio;
        }

    public void setfechaYHoraFin(LocalDateTime fechaYHoraFin) {
        this.fechaYHoraFin = fechaYHoraFin;
    }
    public LocalDateTime getfechaYHoraFin(){
        return fechaYHoraFin;
    }

    public Paciente getPaciente(){
        return paciente;
    }
    
        public Profesional getProfesional(){
            return profesional;
        }

    //Constructor
    public Turno (int id, EstadoDeTurno estado, LocalDateTime fechaYHoraInicio, LocalDateTime fechaYHoraFin, Paciente paciente, Profesional profesional){
        this.id = id;
        this.estado = estado;
        this.fechaYHoraInicio = fechaYHoraInicio;
        this.fechaYHoraFin = fechaYHoraFin;
        this.paciente = paciente;
        this.profesional = profesional;
    }


    //Cambiar estado de turno
    public void cambiarEstado(EstadoDeTurno nuevoEstado) {
    this.estado = nuevoEstado;
    }
    
}
