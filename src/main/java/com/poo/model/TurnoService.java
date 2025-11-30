package com.poo.model;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


//Servicio que gestiona los turnos

public class TurnoService {
    private List<Turno> listaTurnos;
    private int idTurno = 1;
    private ProfesionalService profesionalService;
    private static final int HORAS_MIN_ANTICIPACION = 24;




    public TurnoService() {
        this.listaTurnos = new ArrayList<>();
    } //En el futuro puede reemplazarse por la BD

    public TurnoService(ProfesionalService profesionalService) {
        this.listaTurnos = new ArrayList<>();
        this.profesionalService = profesionalService;
    }

    //Crear Turno ////////////////////////////////////////////////////////////////////////////////////////
    public Turno crearTurno(LocalDateTime fechaYHoraInicio, LocalDateTime fechaYHoraFin, Paciente paciente, Profesional profesional) {

        Turno nuevoTurno = new Turno (-1, fechaYHoraInicio, fechaYHoraFin, paciente, profesional); //creamos turno con ID = -1, para que nunca coincida con el ID real y verificar solapamiento de fechas y horas.
        if (haySolapamiento(nuevoTurno, profesional)) {
            throw new IllegalArgumentException("Error: el horario seleccionado se solapa con un turno existente del profesional.");
        }
        //Si pasa la validacion, creamos el turno final con el ID correspondiente.
        int nuevoId = idTurno++;
        Turno turnoFinal = new Turno(nuevoId, fechaYHoraInicio, fechaYHoraFin, paciente, profesional);
        listaTurnos.add(turnoFinal);
        return turnoFinal;
    }

    // Metodo privado para verificar solapamientos
    private boolean haySolapamiento (Turno nuevoTurno, Profesional profesional) {
        List<Turno> turnosDelProf = buscarTurnosPorProfesional(profesional);
        for (Turno turnoExistente : turnosDelProf) { //verificamos entre los turnos existentes del Profesional
            EstadoDeTurno estado = turnoExistente.getEstado();
            if (estado != EstadoDeTurno.CANCELADO && estado != EstadoDeTurno.AUSENTE) {
                if (nuevoTurno.seSolapaCon(turnoExistente)) {
                    return true; //Hay solapamiento
                }
            }
        }
        return false; // No hay solapamiento
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
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioTurno = turno.getFechaYHoraInicio();
        Duration tiempoRestante = Duration.between(ahora, inicioTurno);

        //validamos que al momento de cancelar hayan las suficientes horas de anticipacion.
        if (tiempoRestante.toHours() < HORAS_MIN_ANTICIPACION) {
            throw new IllegalStateException("Error: Las cancelaciones deben realizarse al menos con " +  HORAS_MIN_ANTICIPACION + " horas de anticipacion.");
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
        
        // Si profesional es null, retorna TODOS los turnos
        if (profesional == null) {
            return new ArrayList<>(listaTurnos);
        }
        
        for (Turno t : listaTurnos) {
            if (t.getProfesional().getId() == profesional.getId()) {
                turnosProf.add(t);  // ← CORREGIDO: antes decía listaTurnos.add(t)
            }
        }
        return turnosProf;
    }

    //Busqueda de turno por especialidad //////////////////////////////////////////////////////////////////////////
    public List<Turno> buscarTurnoPorEspecialidad (String especialidad) {
        List<Turno> turnosEspecialidad = new ArrayList<>();
        for (Turno t : listaTurnos) {
            if (t.getProfesional().getEspecialidad().equalsIgnoreCase(especialidad)) {
                turnosEspecialidad.add(t);  // ← CORREGIDO: antes decía listaTurnos.add(t)
            }
        }
        return turnosEspecialidad;
    }
    
    //Busqueda de turno por paciente //////////////////////////////////////////////////////////////////////////
    public List<Turno> buscarTurnosPorPaciente(Paciente paciente) {
        List<Turno> turnosPaciente = new ArrayList<>();
        if (paciente == null) {
            return turnosPaciente;
        }
        for (Turno t : listaTurnos) {
            if (t.getPaciente().equals(paciente)) {
                turnosPaciente.add(t);
            }
        }
        return turnosPaciente;
    }
    
    // Getter para lista completa //////////////////////////////////////////////////////////////////////////
    public List<Turno> getListaTurnos() {
        return listaTurnos;
    }
}