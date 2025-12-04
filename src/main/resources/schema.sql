-- Tabla de Pacientes
CREATE TABLE IF NOT EXISTS pacientes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni INTEGER UNIQUE NOT NULL,
    telefono VARCHAR(20),
    obra_social VARCHAR(100),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Profesionales
CREATE TABLE IF NOT EXISTS profesionales (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni INTEGER UNIQUE NOT NULL,
    especialidad VARCHAR(100) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Turnos
CREATE TABLE IF NOT EXISTS turnos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    paciente_id INTEGER NOT NULL,
    profesional_id INTEGER NOT NULL,
    fecha_hora_inicio TIMESTAMP NOT NULL,
    fecha_hora_fin TIMESTAMP NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE,
    FOREIGN KEY (profesional_id) REFERENCES profesionales(id) ON DELETE CASCADE,
    CHECK (estado IN ('PENDIENTE', 'CONFIRMADO', 'ATENDIDO', 'AUSENTE', 'CANCELADO'))
);

-- Índices para mejorar el rendimiento de las búsquedas
CREATE INDEX IF NOT EXISTS idx_pacientes_dni ON pacientes(dni);
CREATE INDEX IF NOT EXISTS idx_profesionales_dni ON profesionales(dni);
CREATE INDEX IF NOT EXISTS idx_turnos_paciente ON turnos(paciente_id);
CREATE INDEX IF NOT EXISTS idx_turnos_profesional ON turnos(profesional_id);
CREATE INDEX IF NOT EXISTS idx_turnos_fecha ON turnos(fecha_hora_inicio);
CREATE INDEX IF NOT EXISTS idx_turnos_estado ON turnos(estado);

-- Datos de ejemplo (opcional)
INSERT OR IGNORE INTO pacientes (id, nombre, apellido, dni, telefono, obra_social) VALUES 
(1, 'Micaela', 'Díaz', 44556677, '1122334455', 'OSDE'),
(2, 'Mauro', 'López', 77889900, '1199887766', 'Swiss Medical');

INSERT OR IGNORE INTO profesionales (id, nombre, apellido, dni, especialidad) VALUES 
(1, 'Martin', 'Perez', 12345678, 'Cardiologia'),
(2, 'Luis', 'Cabral', 87654321, 'Dermatologia');