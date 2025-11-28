/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.poo.Main;

import com.poo.model.*;

import java.time.LocalDateTime;

/**
 *
 * @author cabra
 */
public class Main {

    public static void main(String[] args) {

        // 1. Inicializar Servicios
        ProfesionalService profesionalService = new ProfesionalService();
        PacienteService pacienteService = new PacienteService();
        // **IMPORTANTE:** TurnoService necesita ProfessionalService para operar con Agendas/Turnos
        TurnoService turnoService = new TurnoService(profesionalService);

        // Obtener datos precargados
        Profesional profCardiologia = profesionalService.buscarProfesionalPorDni(12345678); // Martin Perez (Con turnos)
        Profesional profDermatologia = profesionalService.buscarProfesionalPorDni(87654321); // Luis Cabral (Lo usaremos sin turnos)
        Paciente pacientePrueba = pacienteService.buscarPacientePorDni(77889900); // Mauro López


        // ----------------------------------------------------
        // PREPARACIÓN DE ESCENARIOS
        // ----------------------------------------------------

        System.out.println("--- PREPARACIÓN DE BAJA ---");

        // Creamos un turno PENDIENTE para Martin Perez (DNI 12345678)
        if (profCardiologia != null && pacientePrueba != null) {
            LocalDateTime inicioActivo = LocalDateTime.now().plusDays(5).withHour(10).withMinute(0);
            LocalDateTime finActivo = inicioActivo.plusMinutes(30);

            turnoService.crearTurno(inicioActivo, finActivo, pacientePrueba, profCardiologia);
            System.out.println("✅ Turno Activo creado para DNI: " + profCardiologia.getDni());
        }

        // Verificamos que Luis Cabral (DNI 87654321) no tenga turnos activos
        System.out.println("✅ Profesional sin turnos preparado: DNI " + profDermatologia.getDni());


        // ====================================================
        // PRUEBA DE BAJA (ProfesionalService.bajaProfesional)
        // ====================================================

        // --- PRUEBA A: FALLO POR INEXISTENCIA (DNI no encontrado) ---
        int dniInexistente = 99999999;
        System.out.println("\n--- PRUEBA A: FALLO - DNI INEXISTENTE (" + dniInexistente + ") ---");
        try {
            profesionalService.bajaProfesional(dniInexistente, turnoService);
            System.err.println("❌ FALLO: El sistema eliminó un DNI inexistente.");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ ÉXITO: Baja rechazada por inexistencia. Mensaje: " + e.getMessage());
        }

        // --- PRUEBA B: FALLO POR REGLA DE NEGOCIO (Turnos Activos) ---
        int dniConTurnos = profCardiologia.getDni();
        System.out.println("\n--- PRUEBA B: FALLO - TURNOS ACTIVOS (" + dniConTurnos + ") ---");
        try {
            profesionalService.bajaProfesional(dniConTurnos, turnoService);
            System.err.println("❌ FALLO: El sistema eliminó al profesional A PESAR de tener turnos activos.");
        } catch (IllegalStateException e) {
            System.out.println("✅ ÉXITO: Baja rechazada por regla de negocio. Mensaje: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ FALLO: Se lanzó un tipo de error incorrecto. Mensaje: " + e.getMessage());
        }

        // --- PRUEBA C: ÉXITO (Baja de profesional sin turnos) ---
        int dniSinTurnos = profDermatologia.getDni();
        System.out.println("\n--- PRUEBA C: ÉXITO - SIN TURNOS (" + dniSinTurnos + ") ---");

        // Contamos antes de eliminar para verificar el éxito
        int totalAntes = profesionalService.getListaProfesionales().size();

        try {
            boolean exito = profesionalService.bajaProfesional(dniSinTurnos, turnoService);
            if (exito) {
                int totalDespues = profesionalService.getListaProfesionales().size();

                System.out.println("✅ ÉXITO: Profesional " + dniSinTurnos + " dado de baja correctamente.");
                System.out.println("Total Profesionales: " + totalAntes + " -> " + totalDespues);
            }
        } catch (Exception e) {
            System.err.println("❌ FALLO: Baja falló inesperadamente: " + e.getMessage());
        }



    }
}
