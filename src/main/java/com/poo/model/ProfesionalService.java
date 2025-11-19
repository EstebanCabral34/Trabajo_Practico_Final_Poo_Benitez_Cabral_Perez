package com.poo.model;

import java.util.ArrayList;
import java.util.List;

public class ProfesionalService {
    private List<Profesional> listaProfesionales;
    private int proxId = 0;


    public ProfesionalService(){
        this.listaProfesionales = new ArrayList<>();
        precargarDatos();
    }

    // Alta de profesional ///////////////////////////////////////////////////////////////////////////////////////
    public Profesional altaProfesional (String nombre, String apellido, int dni, String especialidad, List <Agenda> agendas) {
        if (nombre == null || nombre.isEmpty() || apellido == null || apellido.isEmpty() || especialidad == null || especialidad.isEmpty() || dni <= 0){
            throw new IllegalArgumentException("El nombre, apellido, dni y especialidad del profesional son obligatorios.");
        }
        if (buscarProfesionalPorDni(dni) != null) {
            throw new IllegalArgumentException("ERROR: Ya existe un profesional con el DNi:" + dni + ".");
        }
        Profesional nuevoProfesional = new Profesional(proxId++, nombre, apellido, dni, especialidad, agendas);
        listaProfesionales.add(nuevoProfesional);
        return nuevoProfesional;
    }

    // Baja de profesional /////////////////////////////////////////////////////////////
    // Getters///////////////////////////////////////////////////////////////////////////////////////
    public List<Profesional> getListaProfesionales() {
        return listaProfesionales;
    }

    public int getProxId() {
        return proxId;
    }

    // Buscar profesional por dni ///////////////////////////////////////////////////////////////////////////////////////
    public Profesional buscarProfesionalPorDni(int dni){
        for (Profesional profesional : listaProfesionales) {
            if (profesional.getDni() == dni) return profesional;
        }
        return null;
    }

    // Metodo para precargar datos ///////////////////////////////////////////////////////////////////////////////////////
    private void precargarDatos(){
        altaProfesional("Martin", "Perez", 12345678, "Cardiologia", new ArrayList<>());
        altaProfesional("Luis", "Cabral", 87654321, "Dermatologia", new ArrayList<>());
    }














}
