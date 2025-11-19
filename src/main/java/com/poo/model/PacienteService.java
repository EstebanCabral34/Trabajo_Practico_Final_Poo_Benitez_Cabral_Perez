package com.poo.model;


import java.util.ArrayList;
import java.util.List;

public class PacienteService {
    private List<Paciente> listaPacientes;
    private int proxId = 0;

    public PacienteService(){
        this.listaPacientes = new ArrayList<>();
        precargarDatos();
    }

    public Paciente altaPaciente(String nombre, String apellido, int dni, String telefono, String obraSocial){
        if (nombre == null || dni == 0) {
            throw new IllegalArgumentException("El nombre y DNI del paciente son obligatorios.");
        }
        if (buscarPacientePorDni (dni) != null) {
            throw new IllegalArgumentException("ERROR: Ya existe un paciente con el DNI:" + dni + ".");
        }

        Paciente nuevoPaciente = new Paciente(proxId++, nombre, apellido, dni, telefono, obraSocial, new ArrayList<>());
        listaPacientes.add(nuevoPaciente);
        return nuevoPaciente;
    }


    //Getter de listaPacientes ///////////////////////////////////////////////////////////////////////

    public List<Paciente> getListaPacientes(){
        return listaPacientes;
    }

    // Buscar paciente por DNI ///////////////////////////////////////////////////////////////////////
    public Paciente buscarPacientePorDni(int dni){
        for (Paciente paciente : listaPacientes) {
            if (paciente.getDni() == dni) {
                return paciente;
            }
        }
        return null;
    }

    // Metodo para precargar Datos ///////////////////////////////////////////////////////////////////////
    private void precargarDatos(){
        altaPaciente("Micaela", "Diaz", 44556677, "1122334455", "OSDE");
        altaPaciente("Mauro", "López", 77889900, "1199887766", "Swiss Medical");
    }















}
