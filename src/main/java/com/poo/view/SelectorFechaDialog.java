package com.poo.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SelectorFechaDialog extends JDialog {
    private LocalDateTime fechaSeleccionada;
    private boolean ok = false;
    
    // Componentes
    private JLabel lblMesAnio;
    private JPanel panelCalendario;
    private JSpinner spinnerHora;
    private JSpinner spinnerMinuto;
    private JButton btnMesAnterior;
    private JButton btnMesSiguiente;
    private JLabel lblFechaSeleccionada;
    
    // Estado
    private LocalDate fechaActual;
    private LocalDate diaSeleccionado;
    private YearMonth mesActual;
    
    // Colores
    private final Color COLOR_FONDO = new Color(45, 45, 45);
    private final Color COLOR_PANEL = new Color(60, 60, 60);
    private final Color COLOR_TEXTO = new Color(220, 220, 220);
    private final Color COLOR_ACENTO = new Color(66, 133, 244); // Azul Google
    private final Color COLOR_DIA_ACTUAL = new Color(52, 168, 83); // Verde Google
    private final Color COLOR_DIA_SELECCIONADO = new Color(66, 133, 244); // Azul selección
    private final Color COLOR_DIA_HOVER = new Color(80, 80, 80);
    private final Color COLOR_FIN_SEMANA = new Color(120, 120, 120);
    
    public SelectorFechaDialog(JFrame parent) {
        super(parent, "Seleccionar Fecha y Hora - Calendario", true);
        setSize(650, 550); // Aumentamos un poco el tamaño
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COLOR_FONDO);
        
        fechaActual = LocalDate.now();
        diaSeleccionado = fechaActual.plusDays(1); // Mañana por defecto
        mesActual = YearMonth.from(diaSeleccionado);
        
        // Panel superior - Navegación del mes
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central - Calendario y controles de hora (ahora con scroll)
        JPanel panelCentralContenedor = new JPanel(new BorderLayout());
        panelCentralContenedor.setBackground(COLOR_FONDO);
        
        JPanel panelCentral = new JPanel(new BorderLayout(15, 15));
        panelCentral.setBackground(COLOR_FONDO);
        panelCentral.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Panel del calendario
        JPanel panelCalendarioContainer = crearPanelCalendarioContainer();
        panelCentral.add(panelCalendarioContainer, BorderLayout.CENTER);
        
        // Panel de hora (contenedor scrollable)
        JPanel panelHoraContenedor = crearPanelHoraContenedor();
        panelCentral.add(panelHoraContenedor, BorderLayout.EAST);
        
        // Hacer el panel central scrollable si es necesario
        JScrollPane scrollCentral = new JScrollPane(panelCentral);
        scrollCentral.setBorder(null);
        scrollCentral.getVerticalScrollBar().setUnitIncrement(16);
        scrollCentral.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollCentral.getViewport().setBackground(COLOR_FONDO);
        
        panelCentralContenedor.add(scrollCentral, BorderLayout.CENTER);
        add(panelCentralContenedor, BorderLayout.CENTER);
        
        // Panel inferior - Botones
        JPanel panelInferior = crearPanelInferior();
        add(panelInferior, BorderLayout.SOUTH);
        
        actualizarCalendario();
        actualizarHoraPorDefecto();
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Botón mes anterior
        btnMesAnterior = new JButton("◀");
        btnMesAnterior.setFont(new Font("Arial", Font.BOLD, 16));
        btnMesAnterior.setBackground(COLOR_ACENTO);
        btnMesAnterior.setForeground(Color.WHITE);
        btnMesAnterior.setBorderPainted(false);
        btnMesAnterior.setFocusPainted(false);
        btnMesAnterior.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMesAnterior.addActionListener(e -> {
            mesActual = mesActual.minusMonths(1);
            actualizarCalendario();
        });
        panel.add(btnMesAnterior, BorderLayout.WEST);
        
        // Label mes/año
        lblMesAnio = new JLabel("", SwingConstants.CENTER);
        lblMesAnio.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMesAnio.setForeground(COLOR_TEXTO);
        panel.add(lblMesAnio, BorderLayout.CENTER);
        
        // Botón mes siguiente
        btnMesSiguiente = new JButton("▶");
        btnMesSiguiente.setFont(new Font("Arial", Font.BOLD, 16));
        btnMesSiguiente.setBackground(COLOR_ACENTO);
        btnMesSiguiente.setForeground(Color.WHITE);
        btnMesSiguiente.setBorderPainted(false);
        btnMesSiguiente.setFocusPainted(false);
        btnMesSiguiente.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMesSiguiente.addActionListener(e -> {
            mesActual = mesActual.plusMonths(1);
            actualizarCalendario();
        });
        panel.add(btnMesSiguiente, BorderLayout.EAST);
        
        // Fecha seleccionada
        lblFechaSeleccionada = new JLabel("", SwingConstants.CENTER);
        lblFechaSeleccionada.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFechaSeleccionada.setForeground(new Color(180, 180, 180));
        panel.add(lblFechaSeleccionada, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelCalendarioContainer() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_ACENTO, 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(400, 350)); // Tamaño fijo para el calendario
        
        // Días de la semana
        JPanel panelDiasSemana = new JPanel(new GridLayout(1, 7));
        panelDiasSemana.setBackground(COLOR_PANEL);
        
        String[] dias = {"LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM"};
        for (String dia : dias) {
            JLabel lblDia = new JLabel(dia, SwingConstants.CENTER);
            lblDia.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblDia.setForeground(dia.equals("SÁB") || dia.equals("DOM") ? COLOR_FIN_SEMANA : COLOR_TEXTO);
            lblDia.setBorder(new EmptyBorder(5, 0, 5, 0));
            panelDiasSemana.add(lblDia);
        }
        
        panel.add(panelDiasSemana, BorderLayout.NORTH);
        
        // Panel del calendario (días)
        panelCalendario = new JPanel(new GridLayout(6, 7, 3, 3));
        panelCalendario.setBackground(COLOR_PANEL);
        panel.add(panelCalendario, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelHoraContenedor() {
        JPanel panelContenedor = new JPanel(new BorderLayout());
        panelContenedor.setBackground(COLOR_FONDO);
        
        // Panel de hora con scroll
        JPanel panelHora = crearPanelHora();
        
        JScrollPane scrollHora = new JScrollPane(panelHora);
        scrollHora.setBorder(null);
        scrollHora.setPreferredSize(new Dimension(200, 350)); // Tamaño fijo
        scrollHora.getVerticalScrollBar().setUnitIncrement(16);
        scrollHora.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollHora.getViewport().setBackground(COLOR_PANEL);
        
        panelContenedor.add(scrollHora, BorderLayout.CENTER);
        return panelContenedor;
    }
    
    private JPanel crearPanelHora() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_ACENTO, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Título
        JLabel lblTituloHora = new JLabel("Seleccionar Hora");
        lblTituloHora.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloHora.setForeground(COLOR_TEXTO);
        lblTituloHora.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTituloHora);
        panel.add(Box.createVerticalStrut(20));
        
        // Panel para controles de hora
        JPanel panelControles = new JPanel(new GridLayout(4, 1, 10, 10));
        panelControles.setBackground(COLOR_PANEL);
        
        // Hora
        JPanel panelHoraControl = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelHoraControl.setBackground(COLOR_PANEL);
        
        JLabel lblHora = new JLabel("Hora:");
        lblHora.setForeground(COLOR_TEXTO);
        panelHoraControl.add(lblHora);
        
        spinnerHora = new JSpinner(new SpinnerNumberModel(9, 9, 18, 1));
        JSpinner.NumberEditor editorHora = new JSpinner.NumberEditor(spinnerHora, "00");
        spinnerHora.setEditor(editorHora);
        spinnerHora.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinnerHora.setPreferredSize(new Dimension(60, 30));
        panelHoraControl.add(spinnerHora);
        panelControles.add(panelHoraControl);
        
        // Minuto
        JPanel panelMinutoControl = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelMinutoControl.setBackground(COLOR_PANEL);
        
        JLabel lblMinuto = new JLabel("Minuto:");
        lblMinuto.setForeground(COLOR_TEXTO);
        panelMinutoControl.add(lblMinuto);
        
        spinnerMinuto = new JSpinner(new SpinnerNumberModel(0, 0, 55, 5));
        JSpinner.NumberEditor editorMinuto = new JSpinner.NumberEditor(spinnerMinuto, "00");
        spinnerMinuto.setEditor(editorMinuto);
        spinnerMinuto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinnerMinuto.setPreferredSize(new Dimension(60, 30));
        panelMinutoControl.add(spinnerMinuto);
        panelControles.add(panelMinutoControl);
        
        panel.add(panelControles);
        panel.add(Box.createVerticalStrut(20));
        
        // Horarios sugeridos
        JLabel lblSugeridos = new JLabel("Horarios Sugeridos:");
        lblSugeridos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSugeridos.setForeground(COLOR_TEXTO);
        lblSugeridos.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblSugeridos);
        panel.add(Box.createVerticalStrut(10));
        
        // Panel para botones de horarios sugeridos (con scroll propio si es necesario)
        JPanel panelHorariosSugeridos = new JPanel();
        panelHorariosSugeridos.setLayout(new BoxLayout(panelHorariosSugeridos, BoxLayout.Y_AXIS));
        panelHorariosSugeridos.setBackground(COLOR_PANEL);
        panelHorariosSugeridos.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Botones de horarios sugeridos
        String[] horarios = {"09:00", "09:30", "10:00", "10:30", "11:00", "11:30", 
                           "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"};
        
        for (String horario : horarios) {
            JButton btnHorario = crearBotonHorario(horario);
            btnHorario.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnHorario.setMaximumSize(new Dimension(120, 35));
            panelHorariosSugeridos.add(btnHorario);
            panelHorariosSugeridos.add(Box.createVerticalStrut(5));
        }
        
        // Hacer scrollable solo el panel de horarios sugeridos
        JScrollPane scrollHorarios = new JScrollPane(panelHorariosSugeridos);
        scrollHorarios.setBorder(BorderFactory.createEmptyBorder());
        scrollHorarios.setPreferredSize(new Dimension(150, 200));
        scrollHorarios.getVerticalScrollBar().setUnitIncrement(16);
        scrollHorarios.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollHorarios.getViewport().setBackground(COLOR_PANEL);
        
        panel.add(scrollHorarios);
        panel.add(Box.createVerticalStrut(20));
        
        // Panel para recordatorio de horario laboral
        JPanel panelRecordatorio = new JPanel();
        panelRecordatorio.setLayout(new BoxLayout(panelRecordatorio, BoxLayout.Y_AXIS));
        panelRecordatorio.setBackground(new Color(70, 70, 70));
        panelRecordatorio.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(100, 100, 100), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        panelRecordatorio.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblRecordatorio = new JLabel("<html><center><b>Horario Laboral</b><br>09:00 - 18:00</center></html>");
        lblRecordatorio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRecordatorio.setForeground(new Color(200, 200, 200));
        lblRecordatorio.setHorizontalAlignment(SwingConstants.CENTER);
        
        panelRecordatorio.add(lblRecordatorio);
        panel.add(panelRecordatorio);
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JButton crearBotonHorario(String horario) {
        JButton btnHorario = new JButton(horario);
        btnHorario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnHorario.setBackground(new Color(80, 80, 80));
        btnHorario.setForeground(COLOR_TEXTO);
        btnHorario.setFocusPainted(false);
        btnHorario.setBorderPainted(true);
        btnHorario.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        btnHorario.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        btnHorario.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnHorario.setBackground(new Color(100, 100, 100));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnHorario.setBackground(new Color(80, 80, 80));
            }
        });
        
        btnHorario.addActionListener(e -> {
            String[] partes = horario.split(":");
            spinnerHora.setValue(Integer.parseInt(partes[0]));
            spinnerMinuto.setValue(Integer.parseInt(partes[1]));
            
            // Feedback visual
            btnHorario.setBackground(COLOR_ACENTO);
            btnHorario.setForeground(Color.WHITE);
            
            // Restaurar otros botones después de un breve momento
            Timer timer = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    btnHorario.setBackground(new Color(80, 80, 80));
                    btnHorario.setForeground(COLOR_TEXTO);
                }
            });
            timer.setRepeats(false);
            timer.start();
        });
        
        return btnHorario;
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Botón Hoy
        JButton btnHoy = new JButton("Hoy");
        btnHoy.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHoy.setBackground(new Color(120, 120, 120));
        btnHoy.setForeground(Color.WHITE);
        btnHoy.setFocusPainted(false);
        btnHoy.setBorderPainted(false);
        btnHoy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHoy.addActionListener(e -> {
            diaSeleccionado = LocalDate.now().plusDays(1);
            mesActual = YearMonth.from(diaSeleccionado);
            actualizarCalendario();
            actualizarHoraPorDefecto();
        });
        panel.add(btnHoy);
        
        // Botón Cancelar
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancelar.setBackground(new Color(219, 68, 55)); // Rojo Google
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> {
            ok = false;
            setVisible(false);
        });
        panel.add(btnCancelar);
        
        // Botón Aceptar
        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAceptar.setBackground(COLOR_ACENTO);
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setFocusPainted(false);
        btnAceptar.setBorderPainted(false);
        btnAceptar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAceptar.addActionListener(e -> {
            if (validarSeleccion()) {
                ok = true;
                setVisible(false);
            }
        });
        panel.add(btnAceptar);
        
        return panel;
    }
    
    private void actualizarCalendario() {
        panelCalendario.removeAll();
        lblMesAnio.setText(mesActual.getMonth().toString() + " " + mesActual.getYear());
        
        // Obtener el primer día del mes y el día de la semana
        LocalDate primerDia = mesActual.atDay(1);
        int diaSemanaInicio = primerDia.getDayOfWeek().getValue() - 1; // Lunes=0, Domingo=6
        
        // Llenar días vacíos al inicio
        for (int i = 0; i < diaSemanaInicio; i++) {
            panelCalendario.add(crearPanelDiaVacio());
        }
        
        // Llenar los días del mes
        for (int dia = 1; dia <= mesActual.lengthOfMonth(); dia++) {
            LocalDate fechaDia = mesActual.atDay(dia);
            panelCalendario.add(crearPanelDia(fechaDia));
        }
        
        // Llenar días vacíos al final
        int totalCeldas = 42; // 6 semanas * 7 días
        int diasMostrados = diaSemanaInicio + mesActual.lengthOfMonth();
        for (int i = diasMostrados; i < totalCeldas; i++) {
            panelCalendario.add(crearPanelDiaVacio());
        }
        
        // Actualizar label de fecha seleccionada
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy");
        String fechaFormateada = diaSeleccionado.format(fmt);
        lblFechaSeleccionada.setText("Seleccionado: " + fechaFormateada.substring(0, 1).toUpperCase() + 
                                     fechaFormateada.substring(1));
        
        panelCalendario.revalidate();
        panelCalendario.repaint();
    }
    
    private JPanel crearPanelDiaVacio() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_PANEL);
        return panel;
    }
    
    private JPanel crearPanelDia(LocalDate fecha) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(new EmptyBorder(2, 2, 2, 2));
        
        // Determinar si es el día actual
        boolean esHoy = fecha.equals(LocalDate.now());
        boolean esSeleccionado = fecha.equals(diaSeleccionado);
        boolean esFinSemana = fecha.getDayOfWeek().getValue() >= 6;
        
        // Label del número del día
        JLabel lblNumero = new JLabel(String.valueOf(fecha.getDayOfMonth()), SwingConstants.CENTER);
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Configurar colores
        if (esHoy) {
            panel.setBackground(COLOR_DIA_ACTUAL);
            lblNumero.setForeground(Color.WHITE);
        } else if (esSeleccionado) {
            panel.setBackground(COLOR_DIA_SELECCIONADO);
            lblNumero.setForeground(Color.WHITE);
        } else if (esFinSemana) {
            panel.setBackground(COLOR_PANEL);
            lblNumero.setForeground(COLOR_FIN_SEMANA);
        } else {
            panel.setBackground(COLOR_PANEL);
            lblNumero.setForeground(COLOR_TEXTO);
        }
        
        // Si el día está en el pasado, hacerlo más tenue
        if (fecha.isBefore(LocalDate.now())) {
            lblNumero.setForeground(new Color(100, 100, 100));
        }
        
        panel.add(lblNumero, BorderLayout.CENTER);
        
        // Eventos del mouse
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!fecha.isBefore(LocalDate.now())) {
                    diaSeleccionado = fecha;
                    actualizarCalendario();
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!fecha.isBefore(LocalDate.now()) && !fecha.equals(diaSeleccionado) && !fecha.equals(LocalDate.now())) {
                    panel.setBackground(COLOR_DIA_HOVER);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!fecha.equals(diaSeleccionado) && !fecha.equals(LocalDate.now())) {
                    panel.setBackground(esFinSemana ? COLOR_PANEL : COLOR_PANEL);
                }
            }
        });
        
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return panel;
    }
    
    private void actualizarHoraPorDefecto() {
        // Establecer hora por defecto: 9:00 AM
        spinnerHora.setValue(9);
        spinnerMinuto.setValue(0);
    }
    
    private boolean validarSeleccion() {
        try {
            int hora = (int) spinnerHora.getValue();
            int minuto = (int) spinnerMinuto.getValue();
            
            // Validar horario laboral
            if (hora < 9 || hora > 18) {
                JOptionPane.showMessageDialog(this, 
                    "Los turnos solo pueden crearse entre las 09:00 y 18:00 horas",
                    "Horario inválido", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Validar que no sea 18:00 exacto o después
            if (hora == 18 && minuto > 0) {
                JOptionPane.showMessageDialog(this,
                    "Los turnos no pueden ser después de las 18:00",
                    "Horario inválido", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Combinar fecha y hora
            LocalTime horaSeleccionada = LocalTime.of(hora, minuto);
            fechaSeleccionada = LocalDateTime.of(diaSeleccionado, horaSeleccionada);
            
            // Validar que no sea en el pasado
            if (fechaSeleccionada.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this,
                    "No se pueden crear turnos en el pasado",
                    "Fecha inválida", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al validar la selección: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public LocalDateTime getFechaSeleccionada() {
        return fechaSeleccionada;
    }
    
    public boolean isOk() {
        return ok;
    }
    
    // Método estático para fácil uso
    public static LocalDateTime mostrarSelector(JFrame parent, LocalDateTime fechaActual) {
        SelectorFechaDialog dialog = new SelectorFechaDialog(parent);
        
        // Si se proporciona una fecha actual, establecerla en el diálogo
        if (fechaActual != null) {
            dialog.diaSeleccionado = fechaActual.toLocalDate();
            dialog.mesActual = YearMonth.from(fechaActual.toLocalDate());
            dialog.spinnerHora.setValue(fechaActual.getHour());
            dialog.spinnerMinuto.setValue((fechaActual.getMinute() / 5) * 5); // Redondear a múltiplos de 5
            dialog.actualizarCalendario();
        }
        
        dialog.setVisible(true);
        
        if (dialog.isOk()) {
            return dialog.getFechaSeleccionada();
        }
        return null;
    }
}