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
        if (String.valueOf(dni).length() <= 6 || String.valueOf(dni).length() >= 9) {
            throw new IllegalArgumentException("El DNI es inválido: debe tener al menos 7 números.");
        }
        if (buscarProfesionalPorDni(dni) != null) {
            throw new IllegalArgumentException("Ya existe un profesional registrado con el DNI " + dni + ".");
        }
        Profesional nuevoProfesional = new Profesional(proxId++, nombre, apellido, dni, especialidad, agendas);
        listaProfesionales.add(nuevoProfesional);
        return nuevoProfesional;
    }

    // Baja de profesional /////////////////////////////////////////////////////////////
    public boolean bajaProfesional (int dni, TurnoService turnoService) {
        Profesional profAEliminar =  buscarProfesionalPorDni(dni);

        if (profAEliminar == null) {
            throw new IllegalArgumentException("Error: No existe el profesional con DNi:" + dni);
        }
        List<Turno> turnosProfesional = turnoService.buscarTurnosPorProfesional(profAEliminar); //creamos una lista con los turnos asignados al Profesional a eliminar.

        for (Turno t : turnosProfesional) { //En caso que el Profesional tenga turnos asignados, se lo comunicamos al usuario.
            EstadoDeTurno estado = t.getEstado();
            if (estado == EstadoDeTurno.PENDIENTE || estado == EstadoDeTurno.CONFIRMADO) {
                throw new IllegalStateException("Error: El profesional tiene turnos activos");
            }
        }
        listaProfesionales.remove(profAEliminar); // Eliminamos al profesional de la lista.
        return true;
    }

    // Modificar profesional ///////////////////////////////////////////////////////////////////////////////////////
    public Profesional modificarProfesional (int dni, String nuevoNombre, String nuevoApellido, String nuevaEspecialidad) {
        Profesional profAModificar = buscarProfesionalPorDni(dni);
        if (profAModificar == null) {
            throw new IllegalArgumentException("Error: No existe el profesional con DNi:" + dni);
        }
        if (nuevoNombre != null && !nuevoNombre.isEmpty()) {
            profAModificar.setNombre(nuevoNombre);
        }
        if (nuevoApellido != null && !nuevoApellido.isEmpty()) {
            profAModificar.setApellido(nuevoApellido);
        }
        if (nuevaEspecialidad != null && !nuevaEspecialidad.isEmpty()) {
            profAModificar.setEspecialidad(nuevaEspecialidad);
        }
        return profAModificar;
    }


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
