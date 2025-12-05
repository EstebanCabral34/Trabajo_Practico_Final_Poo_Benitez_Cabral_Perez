package com.poo.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.poo.model.EstadoDeTurno;
import com.poo.model.Paciente;
import com.poo.model.Profesional;
import com.poo.model.Turno;
import com.poo.persistence.TurnoDAO;

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
        // Validaciones básicas
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

        // Validar horario laboral (09:00 - 18:00)
        validarHorarioLaboral(fechaHoraInicio, fechaHoraFin);

        try {
            // Verificar que no exista otro turno en el mismo horario para el profesional (30 minutos)
            if (turnoDAO.existeTurnoEnHorarioProfesional(profesional.getId(), fechaHoraInicio, null)) {
                throw new IllegalArgumentException("Ya existe un turno para este profesional en un horario cercano (30 minutos)");
            }

            // Verificar que no exista otro turno en el mismo horario para el paciente (30 minutos)
            if (turnoDAO.existeTurnoEnHorarioPaciente(paciente.getId(), fechaHoraInicio, null)) {
                throw new IllegalArgumentException("El paciente ya tiene un turno en un horario cercano (30 minutos)");
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
     * Valida que el turno esté dentro del horario laboral (09:00 - 18:00)
     */
    private void validarHorarioLaboral(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin) {
        // Obtener solo la hora sin la fecha
        int horaInicio = fechaHoraInicio.getHour();
        int minutoInicio = fechaHoraInicio.getMinute();

        // Validar hora de inicio
        if (horaInicio < 9 || (horaInicio == 18 && minutoInicio > 0) || horaInicio >= 18) {
            throw new IllegalArgumentException("Los turnos solo pueden crearse entre las 09:00 y las 18:00 horas");
        }

        // Validar hora de fin
        int horaFin = fechaHoraFin.getHour();
        int minutoFin = fechaHoraFin.getMinute();

        if (horaFin > 18 || (horaFin == 18 && minutoFin > 0)) {
            throw new IllegalArgumentException("Los turnos no pueden extenderse más allá de las 18:00 horas");
        }

        // Validar duración mínima (al menos 15 minutos)
        long duracionMinutos = java.time.Duration.between(fechaHoraInicio, fechaHoraFin).toMinutes();
        if (duracionMinutos < 15) {
            throw new IllegalArgumentException("La duración mínima del turno debe ser de 15 minutos");
        }

        // Validar duración máxima (máximo 2 horas)
        if (duracionMinutos > 120) {
            throw new IllegalArgumentException("La duración máxima del turno es de 2 horas");
        }
    }

    /**
     * Actualiza un turno existente con validaciones
     */
    public void actualizarTurno(Turno turno) {
        if (turno == null) {
            throw new IllegalArgumentException("El turno no puede ser nulo");
        }

        // Validar horario laboral para actualización
        validarHorarioLaboral(turno.getFechaYHoraInicio(), turno.getfechaYHoraFin());

        try {
            // Verificar que no exista otro turno en el mismo horario para el profesional (30 minutos)
            if (turnoDAO.existeTurnoEnHorarioProfesional(turno.getProfesional().getId(),
                    turno.getFechaYHoraInicio(), turno.getId())) {
                throw new IllegalArgumentException("Ya existe otro turno para este profesional en un horario cercano (30 minutos)");
            }

            // Verificar que no exista otro turno en el mismo horario para el paciente (30 minutos)
            if (turnoDAO.existeTurnoEnHorarioPaciente(turno.getPaciente().getId(),
                    turno.getFechaYHoraInicio(), turno.getId())) {
                throw new IllegalArgumentException("El paciente ya tiene otro turno en un horario cercano (30 minutos)");
            }

            turnoDAO.actualizar(turno);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar turno: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica disponibilidad de un profesional en una fecha/hora específica
     */
    public boolean verificarDisponibilidadProfesional(int profesionalId, LocalDateTime fechaHora) {
        try {
            return !turnoDAO.existeTurnoEnHorarioProfesional(profesionalId, fechaHora, null);
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar disponibilidad: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica disponibilidad de un paciente en una fecha/hora específica
     */
    public boolean verificarDisponibilidadPaciente(int pacienteId, LocalDateTime fechaHora) {
        try {
            return !turnoDAO.existeTurnoEnHorarioPaciente(pacienteId, fechaHora, null);
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar disponibilidad: " + e.getMessage(), e);
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
     * Elimina un turno
     */
    public void eliminarTurno(int id) {
        try {
            turnoDAO.eliminar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar turno: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene horarios disponibles para un profesional en una fecha específica
     */
    public List<LocalDateTime> obtenerHorariosDisponibles(int profesionalId, LocalDate fecha) {
        List<LocalDateTime> horariosDisponibles = new ArrayList<>();

        // Generar horarios de 9:00 a 18:00 cada 30 minutos
        LocalDateTime inicioDia = fecha.atTime(9, 0);
        LocalDateTime finDia = fecha.atTime(18, 0);

        LocalDateTime horaActual = inicioDia;
        while (horaActual.isBefore(finDia)) {
            if (verificarDisponibilidadProfesional(profesionalId, horaActual)) {
                horariosDisponibles.add(horaActual);
            }
            horaActual = horaActual.plusMinutes(30);
        }

        return horariosDisponibles;
    }
}
