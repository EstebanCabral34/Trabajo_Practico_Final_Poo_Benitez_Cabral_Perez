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
public class Paciente extends Usuario {
    private String telefono;
    private String obraSocial;
    private List<Turno> historialturnos;
    
    //Constructor
    public Paciente(int id, String nombre, String apellido, int dni, String telefono, String obraSocial, List<Turno> historialturnos){
        super(id, nombre, apellido, dni);
            this.telefono = telefono;
            this.obraSocial = obraSocial;
            this.historialturnos = (historialturnos != null) ? historialturnos : new ArrayList<>();
    }
    //Getters y Setters
    public void setTelefono(String telefono){
        this.telefono = telefono;
    }
        public String getTelefono(){
            return telefono;
        }
    
    public void setObraSocial(String obraSocial){
        this.obraSocial = obraSocial;
    }
        public String getObraSocial(){
            return obraSocial;
        }
    
    public List<Turno> getHistorialTurnos(){
        return historialturnos;
    }
    
    //Agregar turno al historial del paciente
     public void agregarTurnoAlHistorial(Turno turno) {
        historialturnos.add(turno);
    }
     
    //Metodo Heredado
    @Override
    public void mostrarDatos(){
        System.out.println("Paciente: " + getNombre() + " " + getApellido() + " | DNI: " + getDni() + " | Telefono: " + getTelefono() + " | ObraSocial: " + getObraSocial());
    }
}
