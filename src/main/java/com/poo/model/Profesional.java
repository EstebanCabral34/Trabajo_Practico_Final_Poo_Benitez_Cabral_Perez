/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.poo.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cabra
 */
public class Profesional extends Usuario {
    private String especialidad;
    private List<Agenda> agendas;
    
    //Constructor
    public Profesional(int id, String nombre, String apellido, int dni, String especialidad, List<Agenda> agendas){
        super(id, nombre, apellido, dni);
        this.especialidad = especialidad;
        this.agendas = (agendas != null) ? agendas : new ArrayList<>();
    }
    
    //Getters y Setters
    public void setEspecialidad(String especialidad){
        this.especialidad = especialidad;
    }
        public String getEspecialidad(){
            return especialidad;
        }

    public void setAgendas(List<Agenda> agendas) {
        this.agendas = agendas;
    }
        public List<Agenda> getAgendas() {
            return agendas;
        }
    
    //Metodo Heredado
    @Override
    public void mostrarDatos(){
        System.out.println("Profesional: " + getNombre() + " " + getApellido() + " | Especialidad: " + getEspecialidad() + " | DNI: " + getDni());
    }
}
