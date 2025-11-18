package com.poo.model;


import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Servicio que gestiona los turnos
 */
public class TurnoService {
    private List<Turno> listaTurnos;




    public TurnoService() {
        this.listaTurnos = new ArrayList<>();
    } //En el futuro puede reemplazarse por la BD


    public Turno crearTurno(Paciente paciente, Profesional profesional, LocalDateTime fechaYHoraInicio, int duracionMinutos) {
        Turno nuevoTurno = new Turno (paciente, profesional, fechaYHoraInicio,duracionMinutos);
        listaTurnos.add(nuevoTurno);
        nuevoTurno.getId();
        return nuevoTurno;
    }

    //Cambiar estado del turno
    public void cambiarEstadoturno (Turno turno, EstadoDeTurno nuevoEstado) {
        if (turno == null) {
            throw new IllegalArgumentException("El turno no puede ser nulo");
        }
        turno.cambiarEstado(nuevoEstado);
    }

    // Buscar turno por ID
    public Turno buscarTurnoPorId(int id){
        for (Turno t : listaTurnos) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }










}
