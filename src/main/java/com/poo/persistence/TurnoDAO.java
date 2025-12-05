package com.poo.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.poo.model.EstadoDeTurno;
import com.poo.model.Paciente;
import com.poo.model.Profesional;
import com.poo.model.Turno;

// DAO (Data Access Object) para la entidad Turno
public class TurnoDAO {

    private DatabaseManager dbManager;
    private PacienteDAO pacienteDAO;
    private ProfesionalDAO profesionalDAO;

    public TurnoDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.pacienteDAO = new PacienteDAO();
        this.profesionalDAO = new ProfesionalDAO();
    }

    /**
     * Inserta un nuevo turno en la base de datos
     */
    public Turno insertar(Turno turno) throws SQLException {
        String sql = "INSERT INTO turnos (paciente_id, profesional_id, fecha_hora_inicio, fecha_hora_fin, estado) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, turno.getPaciente().getId());
            pstmt.setInt(2, turno.getProfesional().getId());
            pstmt.setTimestamp(3, Timestamp.valueOf(turno.getFechaYHoraInicio()));
            pstmt.setTimestamp(4, Timestamp.valueOf(turno.getfechaYHoraFin()));
            pstmt.setString(5, turno.getEstado().name());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo insertar el turno");
            }

            // Obtener el ID usando una consulta separada
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    turno.setId(rs.getInt(1));
                }
            }

            return turno;
        }
    }

    /**
     * Actualiza un turno existente
     */
    public void actualizar(Turno turno) throws SQLException {
        String sql = "UPDATE turnos SET paciente_id = ?, profesional_id = ?, "
                + "fecha_hora_inicio = ?, fecha_hora_fin = ?, estado = ? WHERE id = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, turno.getPaciente().getId());
            pstmt.setInt(2, turno.getProfesional().getId());
            pstmt.setTimestamp(3, Timestamp.valueOf(turno.getFechaYHoraInicio()));
            pstmt.setTimestamp(4, Timestamp.valueOf(turno.getfechaYHoraFin()));
            pstmt.setString(5, turno.getEstado().name());
            pstmt.setInt(6, turno.getId());

            pstmt.executeUpdate();
        }
    }

    /**
     * Actualiza solo el estado de un turno
     */
    public void actualizarEstado(int idTurno, EstadoDeTurno nuevoEstado) throws SQLException {
        String sql = "UPDATE turnos SET estado = ? WHERE id = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuevoEstado.name());
            pstmt.setInt(2, idTurno);

            pstmt.executeUpdate();
        }
    }

    /**
     * Elimina un turno por ID
     */
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM turnos WHERE id = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Busca un turno por ID
     */
    public Turno buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM turnos WHERE id = ?";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Recolectar datos primero
                    int pacienteId = rs.getInt("paciente_id");
                    int profesionalId = rs.getInt("profesional_id");
                    LocalDateTime fechaInicio = rs.getTimestamp("fecha_hora_inicio").toLocalDateTime();
                    LocalDateTime fechaFin = rs.getTimestamp("fecha_hora_fin").toLocalDateTime();
                    String estado = rs.getString("estado");

                    // Luego buscar paciente y profesional
                    Paciente paciente = pacienteDAO.buscarPorId(pacienteId);
                    Profesional profesional = profesionalDAO.buscarPorId(profesionalId);

                    if (paciente == null || profesional == null) {
                        throw new SQLException("No se encontró el paciente o profesional asociado al turno");
                    }

                    Turno turno = new Turno(id, fechaInicio, fechaFin, paciente, profesional);
                    turno.setEstado(EstadoDeTurno.valueOf(estado));
                    return turno;
                }
            }
        }
        return null;
    }

    //  Obtiene todos los turnos
    public List<Turno> obtenerTodos() throws SQLException {
        List<Turno> turnos = new ArrayList<>();
        String sql = "SELECT * FROM turnos ORDER BY fecha_hora_inicio DESC";

        try (Connection conn = dbManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            // Primero recolectar todos los resultados
            List<ResultSetData> resultados = new ArrayList<>();
            while (rs.next()) {
                resultados.add(new ResultSetData(
                        rs.getInt("id"),
                        rs.getInt("paciente_id"),
                        rs.getInt("profesional_id"),
                        rs.getTimestamp("fecha_hora_inicio").toLocalDateTime(),
                        rs.getTimestamp("fecha_hora_fin").toLocalDateTime(),
                        rs.getString("estado")
                ));
            }

            // Luego mapear los resultados a objetos Turno
            for (ResultSetData data : resultados) {
                Paciente paciente = pacienteDAO.buscarPorId(data.pacienteId);
                Profesional profesional = profesionalDAO.buscarPorId(data.profesionalId);

                if (paciente != null && profesional != null) {
                    Turno turno = new Turno(
                            data.id,
                            data.fechaHoraInicio,
                            data.fechaHoraFin,
                            paciente,
                            profesional
                    );
                    turno.setEstado(EstadoDeTurno.valueOf(data.estado));
                    turnos.add(turno);
                }
            }
        }
        return turnos;
    }

    // Clase auxiliar para almacenar los datos del ResultSet
    private static class ResultSetData {

        int id;
        int pacienteId;
        int profesionalId;
        LocalDateTime fechaHoraInicio;
        LocalDateTime fechaHoraFin;
        String estado;

        ResultSetData(int id, int pacienteId, int profesionalId,
                LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, String estado) {
            this.id = id;
            this.pacienteId = pacienteId;
            this.profesionalId = profesionalId;
            this.fechaHoraInicio = fechaHoraInicio;
            this.fechaHoraFin = fechaHoraFin;
            this.estado = estado;
        }
    }

    // Busca turnos por paciente
    public List<Turno> buscarPorPaciente(int pacienteId) throws SQLException {
        List<Turno> turnos = new ArrayList<>();
        String sql = "SELECT * FROM turnos WHERE paciente_id = ? ORDER BY fecha_hora_inicio DESC";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pacienteId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    turnos.add(mapearTurno(rs));
                }
            }
        }
        return turnos;
    }

    // Busca turnos por profesional
    public List<Turno> buscarPorProfesional(int profesionalId) throws SQLException {
        List<Turno> turnos = new ArrayList<>();
        String sql = "SELECT * FROM turnos WHERE profesional_id = ? ORDER BY fecha_hora_inicio DESC";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, profesionalId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    turnos.add(mapearTurno(rs));
                }
            }
        }
        return turnos;
    }

    /**
     * Busca turnos por estado
     */
    public List<Turno> buscarPorEstado(EstadoDeTurno estado) throws SQLException {
        List<Turno> turnos = new ArrayList<>();
        String sql = "SELECT * FROM turnos WHERE estado = ? ORDER BY fecha_hora_inicio DESC";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado.name());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    turnos.add(mapearTurno(rs));
                }
            }
        }
        return turnos;
    }

    /**
     * Busca turnos por especialidad
     */
    public List<Turno> buscarPorEspecialidad(String especialidad) throws SQLException {
        List<Turno> turnos = new ArrayList<>();
        String sql = "SELECT t.* FROM turnos t "
                + "INNER JOIN profesionales p ON t.profesional_id = p.id "
                + "WHERE p.especialidad LIKE ? "
                + "ORDER BY t.fecha_hora_inicio DESC";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + especialidad + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    turnos.add(mapearTurno(rs));
                }
            }
        }
        return turnos;
    }

    /**
     * Busca turnos por rango de fechas
     */
    public List<Turno> buscarPorRangoFechas(LocalDateTime desde, LocalDateTime hasta) throws SQLException {
        List<Turno> turnos = new ArrayList<>();
        String sql = "SELECT * FROM turnos WHERE fecha_hora_inicio BETWEEN ? AND ? ORDER BY fecha_hora_inicio";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(desde));
            pstmt.setTimestamp(2, Timestamp.valueOf(hasta));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    turnos.add(mapearTurno(rs));
                }
            }
        }
        return turnos;
    }

    /**
     * Verifica si existe un turno en el mismo horario para el mismo profesional
     */
    public boolean existeTurnoEnHorario(int profesionalId, LocalDateTime fechaHora, Integer turnoIdExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM turnos WHERE profesional_id = ? "
                + "AND fecha_hora_inicio = ? AND estado != 'CANCELADO'";

        if (turnoIdExcluir != null) {
            sql += " AND id != ?";
        }

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, profesionalId);
            pstmt.setTimestamp(2, Timestamp.valueOf(fechaHora));

            if (turnoIdExcluir != null) {
                pstmt.setInt(3, turnoIdExcluir);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Mapea un ResultSet a un objeto Turno
     */
    private Turno mapearTurno(ResultSet rs) throws SQLException {
        // Cargar paciente y profesional desde la BD
        Paciente paciente = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
        Profesional profesional = profesionalDAO.buscarPorId(rs.getInt("profesional_id"));

        if (paciente == null || profesional == null) {
            throw new SQLException("No se encontró el paciente o profesional asociado al turno");
        }

        Turno turno = new Turno(
                rs.getInt("id"),
                rs.getTimestamp("fecha_hora_inicio").toLocalDateTime(),
                rs.getTimestamp("fecha_hora_fin").toLocalDateTime(),
                paciente,
                profesional
        );

        // Establecer el estado
        turno.setEstado(EstadoDeTurno.valueOf(rs.getString("estado")));

        return turno;
    }

    /**
     * Verifica si existe un turno para el profesional en un rango de 30 minutos
     */
    public boolean existeTurnoEnHorarioProfesional(int profesionalId, LocalDateTime fechaHora, Integer turnoIdExcluir) throws SQLException {
        LocalDateTime fechaInicioMenos30 = fechaHora.minusMinutes(30);
        LocalDateTime fechaInicioMas30 = fechaHora.plusMinutes(30);

        String sql = "SELECT COUNT(*) FROM turnos WHERE profesional_id = ? "
                + "AND ((fecha_hora_inicio BETWEEN ? AND ?) "
                + "OR (fecha_hora_fin BETWEEN ? AND ?)) "
                + "AND estado != 'CANCELADO'";

        if (turnoIdExcluir != null) {
            sql += " AND id != ?";
        }

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            pstmt.setInt(paramIndex++, profesionalId);
            pstmt.setTimestamp(paramIndex++, Timestamp.valueOf(fechaInicioMenos30));
            pstmt.setTimestamp(paramIndex++, Timestamp.valueOf(fechaInicioMas30));
            pstmt.setTimestamp(paramIndex++, Timestamp.valueOf(fechaInicioMenos30));
            pstmt.setTimestamp(paramIndex++, Timestamp.valueOf(fechaInicioMas30));

            if (turnoIdExcluir != null) {
                pstmt.setInt(paramIndex, turnoIdExcluir);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Verifica si existe un turno para el paciente en un rango de 30 minutos
     */
    public boolean existeTurnoEnHorarioPaciente(int pacienteId, LocalDateTime fechaHora, Integer turnoIdExcluir) throws SQLException {
        LocalDateTime fechaInicioMenos30 = fechaHora.minusMinutes(30);
        LocalDateTime fechaInicioMas30 = fechaHora.plusMinutes(30);

        String sql = "SELECT COUNT(*) FROM turnos WHERE paciente_id = ? "
                + "AND ((fecha_hora_inicio BETWEEN ? AND ?) "
                + "OR (fecha_hora_fin BETWEEN ? AND ?)) "
                + "AND estado != 'CANCELADO'";

        if (turnoIdExcluir != null) {
            sql += " AND id != ?";
        }

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            pstmt.setInt(paramIndex++, pacienteId);
            pstmt.setTimestamp(paramIndex++, Timestamp.valueOf(fechaInicioMenos30));
            pstmt.setTimestamp(paramIndex++, Timestamp.valueOf(fechaInicioMas30));
            pstmt.setTimestamp(paramIndex++, Timestamp.valueOf(fechaInicioMenos30));
            pstmt.setTimestamp(paramIndex++, Timestamp.valueOf(fechaInicioMas30));

            if (turnoIdExcluir != null) {
                pstmt.setInt(paramIndex, turnoIdExcluir);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
