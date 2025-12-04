package com.poo.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.poo.model.Paciente;

/**
 * DAO (Data Access Object) para la entidad Paciente Maneja todas las
 * operaciones CRUD en la base de datos
 */
public class PacienteDAO {

    private DatabaseManager dbManager;

    public PacienteDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Inserta un nuevo paciente en la base de datos
     */
    public Paciente insertar(Paciente paciente) throws SQLException {
        String sql = "INSERT INTO pacientes (nombre, apellido, dni, telefono, obra_social) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, paciente.getNombre());
            pstmt.setString(2, paciente.getApellido());
            pstmt.setInt(3, paciente.getDni());
            pstmt.setString(4, paciente.getTelefono());
            pstmt.setString(5, paciente.getObraSocial());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo insertar el paciente");
            }

            // Obtener el ID usando una consulta separada
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    paciente.setId(rs.getInt(1));
                }
            }

            return paciente;
        }
    }

    /**
     * Actualiza un paciente existente
     */
    public void actualizar(Paciente paciente) throws SQLException {
        String sql = "UPDATE pacientes SET nombre = ?, apellido = ?, dni = ?, telefono = ?, obra_social = ? WHERE id = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, paciente.getNombre());
            pstmt.setString(2, paciente.getApellido());
            pstmt.setInt(3, paciente.getDni());
            pstmt.setString(4, paciente.getTelefono());
            pstmt.setString(5, paciente.getObraSocial());
            pstmt.setInt(6, paciente.getId());

            pstmt.executeUpdate();
        }
    }

    /**
     * Elimina un paciente por ID
     */
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM pacientes WHERE id = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Busca un paciente por ID
     */
    public Paciente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM pacientes WHERE id = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPaciente(rs);
                }
            }
        }
        return null;
    }

    /**
     * Busca un paciente por DNI
     */
    public Paciente buscarPorDni(int dni) throws SQLException {
        String sql = "SELECT * FROM pacientes WHERE dni = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dni);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPaciente(rs);
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todos los pacientes
     */
    public List<Paciente> obtenerTodos() throws SQLException {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes ORDER BY apellido, nombre";

        try (Connection conn = dbManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pacientes.add(mapearPaciente(rs));
            }
        }
        return pacientes;
    }

    /**
     * Busca pacientes por nombre o apellido (búsqueda parcial)
     */
    public List<Paciente> buscarPorNombre(String nombre) throws SQLException {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes WHERE nombre LIKE ? OR apellido LIKE ? ORDER BY apellido, nombre";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String busqueda = "%" + nombre + "%";
            pstmt.setString(1, busqueda);
            pstmt.setString(2, busqueda);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pacientes.add(mapearPaciente(rs));
                }
            }
        }
        return pacientes;
    }

    /**
     * Verifica si existe un paciente con el DNI dado
     */
    public boolean existePorDni(int dni) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pacientes WHERE dni = ?";

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
     * Mapea un ResultSet a un objeto Paciente
     */
    private Paciente mapearPaciente(ResultSet rs) throws SQLException {
        Paciente paciente = new Paciente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getInt("dni"),
                rs.getString("telefono"),
                rs.getString("obra_social"),
                new ArrayList<>() // Lista vacía de turnos, se carga por separado si es necesario
        );
        return paciente;
    }
}
