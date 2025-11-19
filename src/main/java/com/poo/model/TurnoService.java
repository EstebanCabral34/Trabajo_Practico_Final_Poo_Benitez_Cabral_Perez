package com.poo.model;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


//Servicio que gestiona los turnos

public class TurnoService {
    private List<Turno> listaTurnos;
    private int idTurno = 0;




    public TurnoService() {
        this.listaTurnos = new ArrayList<>();
    } //En el futuro puede reemplazarse por la BD


    public Turno crearTurno(LocalDateTime fechaYHoraInicio, LocalDateTime fechaYHoraFin, Paciente paciente, Profesional profesional) {
        int nuevoId = idTurno++;
        Turno nuevoTurno = new Turno (nuevoId, fechaYHoraInicio, fechaYHoraFin, paciente, profesional);
        listaTurnos.add(nuevoTurno);
        return nuevoTurno;
    }

    //Cambiar estado del turno //////////////////////////////////////////////////////////////////////////
    public void cambiarEstadoturno (Turno turno, EstadoDeTurno nuevoEstado) {
        if (turno == null) {
            throw new IllegalArgumentException("El turno no puede ser nulo");
        }
        turno.cambiarEstado(nuevoEstado);
    }

    //Cancelar turno //////////////////////////////////////////////////////////////////////////
    public void cancelarTurno (Turno turno) {
        if (turno == null) {
            throw new IllegalArgumentException("El turno no puede ser nulo");
        }
        cambiarEstadoturno (turno, EstadoDeTurno.CANCELADO);
        //Mensaje de notificacion por turno cancelado
    }

    // Buscar turno por ID //////////////////////////////////////////////////////////////////////////
    public Turno buscarTurnoPorId(int id){
        for (Turno t : listaTurnos) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    //Busqueda de turno por profesional //////////////////////////////////////////////////////////////////////////
    public List<Turno> buscarTurnosPorProfesional(Profesional profesional) {
        List<Turno> turnosProf = new ArrayList<>();
        for (Turno t : listaTurnos) {
            if (t.getProfesional().equals(profesional)) {
                listaTurnos.add(t);
            }
        }
        return turnosProf;
    }

    //Busqueda de turno por especialidad //////////////////////////////////////////////////////////////////////////
    public List<Turno> buscarTurnoPorEspecialidad (String especialidad) {
        List<Turno> turnosEspecialidad = new ArrayList<>();
        for (Turno t : listaTurnos) {
            if (t.getProfesional().getEspecialidad().equals(especialidad)) {
                listaTurnos.add(t);
            }
        }
        return turnosEspecialidad;
    }




}
