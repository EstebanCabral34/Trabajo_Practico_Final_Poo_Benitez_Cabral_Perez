package com.poo.controller;

import com.poo.model.Paciente;
import com.poo.persistence.PacienteDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para gestionar la lógica de negocio de Pacientes
 * Actúa como intermediario entre la vista y la capa de persistencia
 */
public class PacienteController {
    private PacienteDAO pacienteDAO;

    public PacienteController() {
        this.pacienteDAO = new PacienteDAO();
    }

    /**
     * Crea un nuevo paciente
     */
    public Paciente crearPaciente(String nombre, String apellido, int dni, String telefono, String obraSocial) {
        // Validaciones
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del paciente es obligatorio");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del paciente es obligatorio");
        }
        if (dni <= 0) {
            throw new IllegalArgumentException("El DNI debe ser un número válido");
        }

        try {
            // Verificar si ya existe un paciente con ese DNI
            if (pacienteDAO.existePorDni(dni)) {
                throw new IllegalArgumentException("Ya existe un paciente con el DNI: " + dni);
            }

            // Crear nuevo paciente
            Paciente nuevoPaciente = new Paciente(
                0, // El ID lo asigna la BD
                nombre.trim(),
                apellido.trim(),
                dni,
                telefono != null ? telefono.trim() : "",
                obraSocial != null ? obraSocial.trim() : "",
                new ArrayList<>()
            );

            // Insertar en la base de datos
            return pacienteDAO.insertar(nuevoPaciente);

        } catch (SQLException e) {
            throw new RuntimeException("Error al crear el paciente: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un paciente por DNI
     */
    public Paciente buscarPorDni(int dni) {
        try {
            return pacienteDAO.buscarPorDni(dni);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar paciente: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un paciente por ID
     */
    public Paciente buscarPorId(int id) {
        try {
            return pacienteDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar paciente: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todos los pacientes
     */
    public List<Paciente> obtenerTodos() {
        try {
            return pacienteDAO.obtenerTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener pacientes: " + e.getMessage(), e);
        }
    }

    /**
     * Busca pacientes por nombre o apellido
     */
    public List<Paciente> buscarPorNombre(String nombre) {
        try {
            return pacienteDAO.buscarPorNombre(nombre);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar pacientes: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un paciente existente
     */
    public void actualizarPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }

        try {
            pacienteDAO.actualizar(paciente);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar paciente: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un paciente
     */
    public void eliminarPaciente(int id) {
        try {
            pacienteDAO.eliminar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar paciente: " + e.getMessage(), e);
        }
    }
}