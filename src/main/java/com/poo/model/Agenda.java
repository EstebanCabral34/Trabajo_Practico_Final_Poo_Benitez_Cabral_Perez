/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.poo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cabra
 */
public class Agenda {
    private LocalDate fecha;
    private List<Turno> turnos;
    private Profesional profesional;
    
    public Agenda(LocalDate fecha, List<Turno> turnos, Profesional profesional){
        this.fecha = fecha;
        this.turnos = (turnos != null) ? turnos : new ArrayList<>();
        this.profesional = profesional;
    }
    
    //Getters y Setters
    public void setFecha(LocalDate fecha){
        this.fecha = fecha;
    }
        public LocalDate getFecha(){
            return fecha;
        }
    
    public List<Turno> getTurnos(){
        return turnos;
    }
    
    public Profesional getProfesional(){
        return profesional;
    }
    
    //Agregar turno
    public void agregarTurno(Turno turno){
        turnos.add(turno);
    }
    //Eliminar turno
    public boolean eliminarTurno(Turno turno){
        return turnos.remove(turno);
    }
    
}
