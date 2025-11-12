/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.poo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cabra
 */
public class GestorDeTurnos {
    private List<Profesional> profesionales;
    private List<Paciente> pacientes;
    private List<Turno> turnos;

    public GestorDeTurnos(List<Profesional> profesionales, List<Paciente> pacientes, List<Turno> turnos) {
        this.profesionales = (profesionales != null) ? profesionales : new ArrayList<>();
        this.pacientes = (pacientes != null) ? pacientes : new ArrayList<>();
        this.turnos = (turnos != null) ? turnos : new ArrayList<>();
    }

    // Getters
    public List<Profesional> getProfesionales(){ 
        return profesionales; 
    }
    public List<Paciente> getPacientes(){ 
        return pacientes; 
    }
    public List<Turno> getTurnos(){ 
        return turnos; 
    }

    //Registrar turno
    public void registrarTurno(Turno nuevoTurno){ 
        turnos.add(nuevoTurno); 
    }
    //Registrar profesional
    public void registrarProfesional(Profesional profesional){
        profesionales.add(profesional); 
    }
    //Registrar paciente
    public void registrarPaciente(Paciente paciente){ 
        pacientes.add(paciente);
    }

    //Crear turno
    public Turno crearTurno(Paciente paciente, Profesional profesional, LocalDateTime fechaYHora){
        Turno nuevoTurno = new Turno(turnos.size() + 1, 
               EstadoDeTurno.PENDIENTE, 
               fechaYHora, 
               paciente, 
               profesional);
        turnos.add(nuevoTurno);
        return nuevoTurno;
    }

    //Cancelar turno
    public void cancelarTurno(Turno turno){
        turno.cambiarEstado(EstadoDeTurno.CANCELADO);
    }

    //Cambiar estado de turno
    public void cambiarEstadoTurno(Turno turno, EstadoDeTurno nuevoEstado){
        turno.cambiarEstado(nuevoEstado);
    }

    //Busqueda de profesional por dni
    public Profesional buscarProfesionalPorDni(int dni){
        for (Profesional p : profesionales) if (p.getDni() == dni) return p;
        return null;
    }
    //Busqueda de paciente por dni
    public Paciente buscarPacientePorDni(int dni){
        for (Paciente p : pacientes) if (p.getDni() == dni) return p;
        return null;
    }
    //Busqueda de turno por profesional
    public List<Turno> buscarTurnosPorProfesional(Profesional profesional){
        List<Turno> resultado = new ArrayList<>();
        for (Turno t : turnos)
            if (t.getProfesional().equals(profesional)) resultado.add(t);
        return resultado;
    }
    //Busqueda de turno por especialidad
    public List<Turno> buscarTurnosPorEspecialidad(String especialidad){
        List<Turno> resultado = new ArrayList<>();
        for (Turno t : turnos)
            if (t.getProfesional().getEspecialidad().equalsIgnoreCase(especialidad))
                resultado.add(t);
        return resultado;
    }
    //Busqueda de turno por paciente
    public List<Turno> buscarTurnosPorPaciente(Paciente paciente){
        List<Turno> resultado = new ArrayList<>();
        for (Turno t : turnos)
            if (t.getPaciente().equals(paciente))
                resultado.add(t);
        return resultado;
    }
    //Datos Precargados
    public void precargarDatos(){
        Profesional prof1 = new Profesional(1, "Martin", "Perez", 12345678, "Cardiologia", new ArrayList<>());
        Profesional prof2 = new Profesional(2, "Luis", "Cabral", 87654321, "Dermatologia", new ArrayList<>());

        Paciente pac1 = new Paciente(1, "Micaela", "Díaz", 44556677, "1122334455", "OSDE", new ArrayList<>());
        Paciente pac2 = new Paciente(2, "Mauro", "López", 77889900, "1199887766", "Swiss Medical", new ArrayList<>());

        registrarProfesional(prof1);
        registrarProfesional(prof2);
        registrarPaciente(pac1);
        registrarPaciente(pac2);

        crearTurno(pac1, prof1, LocalDateTime.now().plusDays(1));
        crearTurno(pac2, prof2, LocalDateTime.now().plusDays(2));
    }
}

