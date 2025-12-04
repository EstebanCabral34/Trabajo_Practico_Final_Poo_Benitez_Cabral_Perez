package com.poo.controller;

import com.poo.model.EstadoDeTurno;
import com.poo.model.Paciente;
import com.poo.model.Profesional;
import com.poo.model.Turno;
import com.poo.persistence.TurnoDAO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador para gestionar la lógica de negocio de Turnos
 */
public class TurnoController {
    private TurnoDAO turnoDAO;
    private PacienteController pacienteController;
    private ProfesionalController profesionalController;

    public TurnoController() {
        this.turnoDAO = new TurnoDAO();
        this.pacienteController = new PacienteController();
        this.profesionalController = new ProfesionalController();
    }

    /**
     * Crea un nuevo turno
     */
    public Turno crearTurno(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, 
                           Paciente paciente, Profesional profesional) {
        // Validaciones
        if (fechaHoraInicio == null || fechaHoraFin == null) {
            throw new IllegalArgumentException("Las fechas del turno son obligatorias");
        }
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente es obligatorio");
        }
        if (profesional == null) {
            throw new IllegalArgumentException("El profesional es obligatorio");
        }
        if (fechaHoraFin.isBefore(fechaHoraInicio)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        if (fechaHoraInicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede crear un turno en el pasado");
        }

        try {
            // Verificar que no exista otro turno en el mismo horario para el profesional
            if (turnoDAO.existeTurnoEnHorario(profesional.getId(), fechaHoraInicio, null)) {
                throw new IllegalArgumentException("Ya existe un turno para este profesional en ese horario");
            }

            // Crear el turno
            Turno nuevoTurno = new Turno(
                0, // El ID lo asigna la BD
                fechaHoraInicio,
                fechaHoraFin,
                paciente,
                profesional
            );

            // Insertar en la base de datos
            return turnoDAO.insertar(nuevoTurno);

        } catch (SQLException e) {
            throw new RuntimeException("Error al crear el turno: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un turno por ID
     */
    public Turno buscarPorId(int id) {
        try {
            return turnoDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar turno: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todos los turnos
     */
    public List<Turno> obtenerTodos() {
        try {
            return turnoDAO.obtenerTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener turnos: " + e.getMessage(), e);
        }
    }

    /**
     * Busca turnos por paciente
     */
    public List<Turno> buscarPorPaciente(int pacienteId) {
        try {
            return turnoDAO.buscarPorPaciente(pacienteId);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar turnos: " + e.getMessage(), e);
        }
    }

    /**
     * Busca turnos por profesional
     */
    public List<Turno> buscarPorProfesional(int profesionalId) {
        try {
            return turnoDAO.buscarPorProfesional(profesionalId);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar turnos: " + e.getMessage(), e);
        }
    }

    /**
     * Busca turnos por especialidad
     */
    public List<Turno> buscarPorEspecialidad(String especialidad) {
        try {
            return turnoDAO.buscarPorEspecialidad(especialidad);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar turnos: " + e.getMessage(), e);
        }
    }

    /**
     * Busca turnos por estado
     */
    public List<Turno> buscarPorEstado(EstadoDeTurno estado) {
        try {
            return turnoDAO.buscarPorEstado(estado);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar turnos: " + e.getMessage(), e);
        }
    }

    /**
     * Busca turnos por rango de fechas
     */
    public List<Turno> buscarPorRangoFechas(LocalDateTime desde, LocalDateTime hasta) {
        try {
            return turnoDAO.buscarPorRangoFechas(desde, hasta);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar turnos: " + e.getMessage(), e);
        }
    }

    /**
     * Cambia el estado de un turno
     */
    public void cambiarEstado(int turnoId, EstadoDeTurno nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }

        try {
            Turno turno = turnoDAO.buscarPorId(turnoId);
            if (turno == null) {
                throw new IllegalArgumentException("No existe un turno con ID: " + turnoId);
            }

            turnoDAO.actualizarEstado(turnoId, nuevoEstado);

        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar estado del turno: " + e.getMessage(), e);
        }
    }

    /**
     * Cancela un turno
     */
    public void cancelarTurno(int turnoId) {
        cambiarEstado(turnoId, EstadoDeTurno.CANCELADO);
    }

    /**
     * Confirma un turno
     */
    public void confirmarTurno(int turnoId) {
        cambiarEstado(turnoId, EstadoDeTurno.CONFIRMADO);
    }

    /**
     * Marca un turno como atendido
     */
    public void marcarAtendido(int turnoId) {
        cambiarEstado(turnoId, EstadoDeTurno.ATENDIDO);
    }

    /**
     * Marca un turno como ausente
     */
    public void marcarAusente(int turnoId) {
        cambiarEstado(turnoId, EstadoDeTurno.AUSENTE);
    }

    /**
     * Actualiza un turno existente
     */
    public void actualizarTurno(Turno turno) {
        if (turno == null) {
            throw new IllegalArgumentException("El turno no puede ser nulo");
        }

        try {
            turnoDAO.actualizar(turno);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar turno: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un turno
     */
    public void eliminarTurno(int id) {
        try {
            turnoDAO.eliminar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar turno: " + e.getMessage(), e);
        }
    }
}