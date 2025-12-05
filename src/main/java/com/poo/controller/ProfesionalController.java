package com.poo.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.poo.model.Profesional;
import com.poo.persistence.ProfesionalDAO;

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
     * Modifica un profesional existente
     */
    public Profesional modificarProfesional(int id, String nuevoNombre, String nuevoApellido, 
                                          int nuevoDni, String nuevaEspecialidad) {
        try {
            // Buscar el profesional existente
            Profesional profesional = profesionalDAO.buscarPorId(id);
            if (profesional == null) {
                throw new IllegalArgumentException("No existe un profesional con ID: " + id);
            }

            // Validaciones
            if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
                profesional.setNombre(nuevoNombre.trim());
            } else {
                throw new IllegalArgumentException("El nombre del profesional es obligatorio");
            }
            
            if (nuevoApellido != null && !nuevoApellido.trim().isEmpty()) {
                profesional.setApellido(nuevoApellido.trim());
            } else {
                throw new IllegalArgumentException("El apellido del profesional es obligatorio");
            }
            
            if (nuevoDni > 0 && nuevoDni != profesional.getDni()) {
                // Verificar si el nuevo DNI ya existe (si es diferente al actual)
                Profesional profesionalConMismoDni = profesionalDAO.buscarPorDni(nuevoDni);
                if (profesionalConMismoDni != null && profesionalConMismoDni.getId() != id) {
                    throw new IllegalArgumentException("Ya existe otro profesional con el DNI: " + nuevoDni);
                }
                profesional.setDni(nuevoDni);
            }
            
            if (nuevaEspecialidad != null && !nuevaEspecialidad.trim().isEmpty()) {
                profesional.setEspecialidad(nuevaEspecialidad.trim());
            } else {
                throw new IllegalArgumentException("La especialidad es obligatoria");
            }

            // Actualizar en la base de datos
            profesionalDAO.actualizar(profesional);
            return profesional;

        } catch (SQLException e) {
            throw new RuntimeException("Error al modificar el profesional: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un profesional (con validaciones)
     */
    public boolean eliminarProfesional(int id) {
        try {
            // Primero verificar si el profesional existe
            Profesional profesional = profesionalDAO.buscarPorId(id);
            if (profesional == null) {
                throw new IllegalArgumentException("No existe un profesional con ID: " + id);
            }

            // Verificar si el profesional tiene turnos activos
            // Nota: Necesitarás inyectar o acceder al TurnoController para esta validación
            // Por ahora, vamos a eliminar directamente y manejar la restricción de clave foránea en la BD
            
            profesionalDAO.eliminar(id);
            return true;

        } catch (SQLException e) {
            // Si hay error por restricción de clave foránea (turnos asociados)
            if (e.getMessage().contains("FOREIGN KEY constraint failed")) {
                throw new IllegalStateException("No se puede eliminar el profesional porque tiene turnos asociados. "
                    + "Primero debe cancelar o reasignar los turnos.");
            }
            throw new RuntimeException("Error al eliminar profesional: " + e.getMessage(), e);
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
     * Actualiza un profesional existente (método alternativo)
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
}