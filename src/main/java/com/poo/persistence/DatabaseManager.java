package com.poo.persistence;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class DatabaseManager {
    private static final String DB_NAME = "turnos_medicos.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    private static DatabaseManager instance;
    private Connection connection;
    
    private DatabaseManager() {
        try {
            // Cargar el driver de SQLite
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver de SQLite");
            System.err.println("Agregá esta dependencia al pom.xml:");
            System.err.println("<dependency>");
            System.err.println("    <groupId>org.xerial</groupId>");
            System.err.println("    <artifactId>sqlite-jdbc</artifactId>");
            System.err.println("    <version>3.42.0.0</version>");
            System.err.println("</dependency>");
            e.printStackTrace();
        }
    }
    

    // Obtiene la instancia única del DatabaseManager (Singleton)

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
  
    // Obtiene una conexión a la base de datos
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            Statement stmt = connection.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.close();
        }
        return connection;
    }
    
    
    // Inicializa la base de datos ejecutando el script schema.sql
     
    private void initializeDatabase() {
        File dbFile = new File(DB_NAME);
        boolean isNewDB = !dbFile.exists();
        
        try (Connection conn = getConnection()) {
            if (isNewDB) {
                System.out.println("Creando nueva base de datos...");
            }
            
            // Ejecutar el script SQL
            executeSchemaScript(conn);
            System.out.println("Base de datos inicializada correctamente");
            
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    // Ejecuta el script schema.sql

    private void executeSchemaScript(Connection conn) throws SQLException {
        try {
            // Intentar leer el archivo schema.sql desde resources o desde el mismo directorio
            InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql");
            
            if (is == null) {
                // Si no está en resources, usar el script directamente
                executeDefaultSchema(conn);
                return;
            }
            
            Scanner scanner = new Scanner(is).useDelimiter(";");
            Statement stmt = conn.createStatement();
            
            while (scanner.hasNext()) {
                String sql = scanner.next().trim();
                if (!sql.isEmpty() && !sql.startsWith("--")) {
                    stmt.execute(sql);
                }
            }
            
            scanner.close();
            stmt.close();
            
        } catch (Exception e) {
            System.err.println("Error al ejecutar schema.sql, usando schema por defecto");
            executeDefaultSchema(conn);
        }
    }
    

    // Ejecuta el schema por defecto si no se encuentra el archivo

    private void executeDefaultSchema(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        // Crear tabla pacientes
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS pacientes (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre VARCHAR(100) NOT NULL, " +
            "apellido VARCHAR(100) NOT NULL, " +
            "dni INTEGER UNIQUE NOT NULL, " +
            "telefono VARCHAR(20), " +
            "obra_social VARCHAR(100), " +
            "fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
        );
        
        // Crear tabla profesionales
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS profesionales (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre VARCHAR(100) NOT NULL, " +
            "apellido VARCHAR(100) NOT NULL, " +
            "dni INTEGER UNIQUE NOT NULL, " +
            "especialidad VARCHAR(100) NOT NULL, " +
            "fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
        );
        
        // Crear tabla turnos
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS turnos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "paciente_id INTEGER NOT NULL, " +
            "profesional_id INTEGER NOT NULL, " +
            "fecha_hora_inicio TIMESTAMP NOT NULL, " +
            "fecha_hora_fin TIMESTAMP NOT NULL, " +
            "estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE', " +
            "fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (profesional_id) REFERENCES profesionales(id) ON DELETE CASCADE)"
        );
        
        // Crear índices
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_pacientes_dni ON pacientes(dni)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_profesionales_dni ON profesionales(dni)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_turnos_paciente ON turnos(paciente_id)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_turnos_profesional ON turnos(profesional_id)");
        
        stmt.close();
    }
    
    //Cierra la conexión a la base de datos

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión a la base de datos cerrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
    
    //Limpia toda la base de datos (útil para testing)

    public void resetDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("DROP TABLE IF EXISTS turnos");
            stmt.execute("DROP TABLE IF EXISTS pacientes");
            stmt.execute("DROP TABLE IF EXISTS profesionales");
            
            executeDefaultSchema(conn);
            System.out.println("Base de datos reiniciada");
            
        } catch (SQLException e) {
            System.err.println("Error al reiniciar la base de datos: " + e.getMessage());
        }
    }
}