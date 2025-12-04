package com.poo.controller;

import com.poo.model.Profesional;
import com.poo.persistence.ProfesionalDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para gestionar la lógica de negocio de Profesionales
 */
public class ProfesionalController {
    private ProfesionalDAO profesionalDAO;

    public ProfesionalController() {
        this.profesionalDAO = new ProfesionalDAO();
    }

    /**
     * Crea un nuevo profesional
     */
    public Profesional crearProfesional(String nombre, String apellido, int dni, String especialidad) {
        // Validaciones
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del profesional es obligatorio");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del profesional es obligatorio");
        }
        if (dni <= 0) {
            throw new IllegalArgumentException("El DNI debe ser un número válido");
        }
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La especialidad es obligatoria");
        }

        try {
            // Verificar si ya existe un profesional con ese DNI
            if (profesionalDAO.existePorDni(dni)) {
                throw new IllegalArgumentException("Ya existe un profesional con el DNI: " + dni);
            }

            // Crear nuevo profesional
            Profesional nuevoProfesional = new Profesional(
                0, // El ID lo asigna la BD
                nombre.trim(),
                apellido.trim(),
                dni,
                especialidad.trim(),
                new ArrayList<>()
            );

            // Insertar en la base de datos
            return profesionalDAO.insertar(nuevoProfesional);

        } catch (SQLException e) {
            throw new RuntimeException("Error al crear el profesional: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un profesional por DNI
     */
    public Profesional buscarPorDni(int dni) {
        try {
            return profesionalDAO.buscarPorDni(dni);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar profesional: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un profesional por ID
     */
    public Profesional buscarPorId(int id) {
        try {
            return profesionalDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar profesional: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todos los profesionales
     */
    public List<Profesional> obtenerTodos() {
        try {
            return profesionalDAO.obtenerTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener profesionales: " + e.getMessage(), e);
        }
    }

    /**
     * Busca profesionales por especialidad
     */
    public List<Profesional> buscarPorEspecialidad(String especialidad) {
        try {
            return profesionalDAO.buscarPorEspecialidad(especialidad);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar profesionales: " + e.getMessage(), e);
        }
    }

    /**
     * Busca profesionales por nombre o apellido
     */
    public List<Profesional> buscarPorNombre(String nombre) {
        try {
            return profesionalDAO.buscarPorNombre(nombre);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar profesionales: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene lista de especialidades únicas
     */
    public List<String> obtenerEspecialidades() {
        try {
            return profesionalDAO.obtenerEspecialidades();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener especialidades: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un profesional existente
     */
    public void actualizarProfesional(Profesional profesional) {
        if (profesional == null) {
            throw new IllegalArgumentException("El profesional no puede ser nulo");
        }

        try {
            profesionalDAO.actualizar(profesional);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar profesional: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un profesional
     */
    public void eliminarProfesional(int id) {
        try {
            profesionalDAO.eliminar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar profesional: " + e.getMessage(), e);
        }
    }
}