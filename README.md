# Sistema de Gestión de Turnos Médicos
Sistema completo de gestión de turnos médicos desarrollado en Java con interfaz gráfica Swing y base de datos SQLite.

## Requisitos del Sistema
Software Necesario

Java JDK 25 (o superior)
Apache Maven 3.6+ (para gestión de dependencias)
SQLite JDBC Driver 3.44.1.0 (se instala automáticamente con Maven)

### Verificar Instalación de Java
comando: java -version
Debería mostrar algo como: java version "25" o superior.

### Verificar Instalación de Maven
comando: mvn -version
Debería mostrar la versión de Maven instalada.

## Instalación y Compilación
### Clonar o Descargar el Proyecto
Si tienes el proyecto en un repositorio Git:
git clone https://github.com/EstebanCabral34/Trabajo_Practico_Final_Poo_Benitez_Cabral_Perez.git
cd Trabajo_Practico_Final_Poo_Benitez_Cabral_Perez

## Instalar Dependencias
Maven descargará automáticamente todas las dependencias necesarias (SQLite JDBC):
el comando: mvn clean install - deberia funcionar para:
Limpia compilaciones anteriores (clean)
Descarga las dependencias del pom.xml
Compila el proyecto
Genera el archivo JAR ejecutable

## Ejecutar la Aplicación
Ejecutar desde el archivo Main.java

## Funcionalidades Principales
Gestión de Pacientes

Agregar nuevos pacientes con DNI, nombre, apellido, teléfono y obra social
Buscar pacientes por DNI
Ver lista completa de pacientes registrados

Gestión de Profesionales

Agregar profesionales con especialidad
Modificar datos de profesionales existentes
Eliminar profesionales (solo si no tienen turnos activos)
Buscar por DNI o especialidad

Gestión de Turnos

Crear turnos con selector visual de fecha y hora
Horario laboral: 09:00 - 18:00 hs
Duración: 15 minutos a 2 horas
Estados: PENDIENTE, CONFIRMADO, ATENDIDO, AUSENTE, CANCELADO
Validación de solapamiento (margen de 30 minutos)
Visualización de todos los turnos

# Autores

Benitez Nicolas
Cabral Esteban
Pérez Lucas