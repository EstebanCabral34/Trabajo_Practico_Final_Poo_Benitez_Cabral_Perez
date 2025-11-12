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
    private LocalDateTime fechaYHora;
    private Paciente paciente;
    private Profesional profesional;
    
    //Constructor
    public Turno (int id, EstadoDeTurno estado, LocalDateTime fechaYHora, Paciente paciente, Profesional profesional){
        this.id = id;
        this.estado = estado;
        this.fechaYHora = fechaYHora;
        this.paciente = paciente;
        this.profesional = profesional;
    }
       
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
    
    public void setFechaYHora(LocalDateTime fechaYHora) {
    this.fechaYHora = fechaYHora;
    }
        public LocalDateTime getFechaYHora(){
            return fechaYHora;
        }
    
    public Paciente getPaciente(){
        return paciente;
    }
    
        public Profesional getProfesional(){
            return profesional;
        }
    
    //Cambiar estado de turno
    public void cambiarEstado(EstadoDeTurno nuevoEstado) {
    this.estado = nuevoEstado;
    }
    
}
