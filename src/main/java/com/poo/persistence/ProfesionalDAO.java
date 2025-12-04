package com.poo.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.poo.model.Profesional;

/**
 * DAO (Data Access Object) para la entidad Profesional
 */
public class ProfesionalDAO {

    private DatabaseManager dbManager;

    public ProfesionalDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Inserta un nuevo profesional en la base de datos
     */
    public Profesional insertar(Profesional profesional) throws SQLException {
        String sql = "INSERT INTO profesionales (nombre, apellido, dni, especialidad) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, profesional.getNombre());
            pstmt.setString(2, profesional.getApellido());
            pstmt.setInt(3, profesional.getDni());
            pstmt.setString(4, profesional.getEspecialidad());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo insertar el profesional");
            }

            // Obtener el ID usando una consulta separada
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    profesional.setId(rs.getInt(1));
                }
            }

            return profesional;
        }
    }

    /**
     * Actualiza un profesional existente
     */
    public void actualizar(Profesional profesional) throws SQLException {
        String sql = "UPDATE profesionales SET nombre = ?, apellido = ?, dni = ?, especialidad = ? WHERE id = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, profesional.getNombre());
            pstmt.setString(2, profesional.getApellido());
            pstmt.setInt(3, profesional.getDni());
            pstmt.setString(4, profesional.getEspecialidad());
            pstmt.setInt(5, profesional.getId());

            pstmt.executeUpdate();
        }
    }

    /**
     * Elimina un profesional por ID
     */
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM profesionales WHERE id = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Busca un profesional por ID
     */
    public Profesional buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM profesionales WHERE id = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProfesional(rs);
                }
            }
        }
        return null;
    }

    /**
     * Busca un profesional por DNI
     */
    public Profesional buscarPorDni(int dni) throws SQLException {
        String sql = "SELECT * FROM profesionales WHERE dni = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dni);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProfesional(rs);
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todos los profesionales
     */
    public List<Profesional> obtenerTodos() throws SQLException {
        List<Profesional> profesionales = new ArrayList<>();
        String sql = "SELECT * FROM profesionales ORDER BY apellido, nombre";

        try (Connection conn = dbManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                profesionales.add(mapearProfesional(rs));
            }
        }
        return profesionales;
    }

    /**
     * Busca profesionales por especialidad
     */
    public List<Profesional> buscarPorEspecialidad(String especialidad) throws SQLException {
        List<Profesional> profesionales = new ArrayList<>();
        String sql = "SELECT * FROM profesionales WHERE especialidad LIKE ? ORDER BY apellido, nombre";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + especialidad + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    profesionales.add(mapearProfesional(rs));
                }
            }
        }
        return profesionales;
    }

    /**
     * Busca profesionales por nombre o apellido
     */
    public List<Profesional> buscarPorNombre(String nombre) throws SQLException {
        List<Profesional> profesionales = new ArrayList<>();
        String sql = "SELECT * FROM profesionales WHERE nombre LIKE ? OR apellido LIKE ? ORDER BY apellido, nombre";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String busqueda = "%" + nombre + "%";
            pstmt.setString(1, busqueda);
            pstmt.setString(2, busqueda);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    profesionales.add(mapearProfesional(rs));
                }
            }
        }
        return profesionales;
    }

    /**
     * Verifica si existe un profesional con el DNI dado
     */
    public boolean existePorDni(int dni) throws SQLException {
        String sql = "SELECT COUNT(*) FROM profesionales WHERE dni = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dni);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Obtiene lista de especialidades únicas
     */
    public List<String> obtenerEspecialidades() throws SQLException {
        List<String> especialidades = new ArrayList<>();
        String sql = "SELECT DISTINCT especialidad FROM profesionales ORDER BY especialidad";

        try (Connection conn = dbManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                especialidades.add(rs.getString("especialidad"));
            }
        }
        return especialidades;
    }

    /**
     * Mapea un ResultSet a un objeto Profesional
     */
    private Profesional mapearProfesional(ResultSet rs) throws SQLException {
        Profesional profesional = new Profesional(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getInt("dni"),
                rs.getString("especialidad"),
                new ArrayList<>() // Lista vacía de agendas
        );
        return profesional;
    }
}
