/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.poo.model;

/**
 *
 * @author cabra
 */
public abstract class Usuario {
    private int id;
    private String nombre;
    private String apellido;
    private int dni;
    
    //Constructor
    public Usuario(int id, String nombre, String apellido, int dni){
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
    }
    
    //Getters y Setters
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
            return id;
    }
    
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public String getNombre(){
            return nombre;
    }
        
    public void setApellido(String apellido){
        this.apellido = apellido;
    }
    public String getApellido(){
            return apellido;
    }
      
    public void setDni(int dni){
        this.dni = dni;
    }    
    public int getDni(){
        return dni;
    }





    //Metodo Abstracto
    public abstract void mostrarDatos();
    
    
}
