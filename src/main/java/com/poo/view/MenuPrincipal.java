package com.poo.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.poo.controller.PacienteController;
import com.poo.controller.ProfesionalController;
import com.poo.controller.TurnoController;
import com.poo.model.EstadoDeTurno;
import com.poo.model.Paciente;
import com.poo.model.Profesional;
import com.poo.model.Turno;

public class MenuPrincipal extends JFrame {

    private ProfesionalController profesionalController;
    private PacienteController pacienteController;
    private TurnoController turnoController;

    // Variables para actualizar listas
    private Runnable actualizarListaPacientes;
    private Runnable actualizarListaProfesionales;
    private Runnable actualizarListaTurnos;

    // Colores del tema oscuro
    private final Color COLOR_FONDO = new Color(30, 30, 30);
    private final Color COLOR_PANEL = new Color(40, 40, 40);
    private final Color COLOR_TEXTO = new Color(220, 220, 220);
    private final Color COLOR_ACENTO = new Color(52, 152, 219);
    private final Color COLOR_EXITO = new Color(46, 204, 113);
    private final Color COLOR_PELIGRO = new Color(231, 76, 60);
    private final Color COLOR_BOTON = new Color(60, 60, 60);

    public MenuPrincipal() {
        // Inicializar controladores (ahora usan BD)
        profesionalController = new ProfesionalController();
        pacienteController = new PacienteController();
        turnoController = new TurnoController();

        // Configurar ventana principal
        setTitle("Sistema de Gestión de Turnos Médicos");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());

        // Panel superior
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);

        // Crear pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(COLOR_FONDO);
        tabbedPane.setForeground(COLOR_TEXTO);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Agregar las tres pestañas
        tabbedPane.addTab("  Pacientes  ", crearPanelPacientes());
        tabbedPane.addTab("  Profesionales  ", crearPanelProfesionales());
        tabbedPane.addTab("  Turnos  ", crearPanelTurnos());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 52, 54));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Sistema de Gestión de Turnos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(COLOR_TEXTO);
        panel.add(titulo, BorderLayout.WEST);

        JLabel subtitulo = new JLabel("Base de Datos SQLite");
        subtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtitulo.setForeground(new Color(150, 150, 150));
        panel.add(subtitulo, BorderLayout.EAST);

        return panel;
    }

    // ==================== PANEL DE PACIENTES ====================
    private JPanel crearPanelPacientes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel izquierdo - Formulario con Scroll
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBackground(COLOR_FONDO);

        // Agregar Nuevo Paciente
        JPanel panelAgregar = crearPanelSeccion("Agregar Nuevo Paciente");
        JTextField txtNombrePac = crearCampoTexto("Nombre del paciente");
        JTextField txtApellidoPac = crearCampoTexto("Apellido");
        JTextField txtDniPac = crearCampoTexto("DNI");
        JTextField txtTelPac = crearCampoTexto("Teléfono");
        JTextField txtObraSocial = crearCampoTexto("Obra Social");

        panelAgregar.add(txtNombrePac);
        panelAgregar.add(Box.createVerticalStrut(10));
        panelAgregar.add(txtApellidoPac);
        panelAgregar.add(Box.createVerticalStrut(10));
        panelAgregar.add(txtDniPac);
        panelAgregar.add(Box.createVerticalStrut(10));
        panelAgregar.add(txtTelPac);
        panelAgregar.add(Box.createVerticalStrut(10));
        panelAgregar.add(txtObraSocial);
        panelAgregar.add(Box.createVerticalStrut(15));

        JButton btnAgregarPac = crearBoton("+ Agregar Paciente", COLOR_EXITO);

        // Buscar Paciente
        JPanel panelBuscar = crearPanelSeccion("Buscar Paciente");
        JTextField txtBuscarDni = crearCampoTexto("DNI del paciente");
        JButton btnBuscar = crearBoton("Buscar", COLOR_ACENTO);

        btnBuscar.addActionListener(e -> {
            try {
                int dni = Integer.parseInt(obtenerTexto(txtBuscarDni));
                Paciente p = pacienteController.buscarPorDni(dni);
                if (p != null) {
                    String info = String.format("Paciente encontrado:\nID: %d\nNombre: %s %s\nDNI: %d\nTeléfono: %s\nObra Social: %s",
                            p.getId(), p.getNombre(), p.getApellido(), p.getDni(), p.getTelefono(), p.getObraSocial());
                    JOptionPane.showMessageDialog(this, info);
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró paciente con DNI: " + dni, "No encontrado", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: Ingrese un DNI válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelBuscar.add(txtBuscarDni);
        panelBuscar.add(Box.createVerticalStrut(10));
        panelBuscar.add(btnBuscar);

        btnAgregarPac.addActionListener(e -> {
            try {
                String nombre = obtenerTexto(txtNombrePac);
                String apellido = obtenerTexto(txtApellidoPac);
                int dni = Integer.parseInt(obtenerTexto(txtDniPac));
                String tel = obtenerTexto(txtTelPac);
                String os = obtenerTexto(txtObraSocial);

                Paciente p = pacienteController.crearPaciente(nombre, apellido, dni, tel, os);
                JOptionPane.showMessageDialog(this, "Paciente agregado exitosamente\nID: " + p.getId());
                limpiarCampos(txtNombrePac, txtApellidoPac, txtDniPac, txtTelPac, txtObraSocial);
                actualizarListaPacientes.run();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: El DNI debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelAgregar.add(btnAgregarPac);

        panelIzquierdo.add(panelAgregar);
        panelIzquierdo.add(Box.createVerticalStrut(20));
        panelIzquierdo.add(panelBuscar);
        panelIzquierdo.add(Box.createVerticalGlue());

        // Hacer el panel izquierdo scrollable
        JScrollPane scrollIzquierdo = new JScrollPane(panelIzquierdo);
        scrollIzquierdo.setBorder(null);
        scrollIzquierdo.getVerticalScrollBar().setUnitIncrement(16);
        scrollIzquierdo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Panel derecho - Lista
        JPanel panelLista = crearPanelLista("Lista de Pacientes");
        DefaultListModel<String> modeloPacientes = new DefaultListModel<>();
        JList<String> listaPacientes = new JList<>(modeloPacientes);
        listaPacientes.setBackground(COLOR_PANEL);
        listaPacientes.setForeground(COLOR_TEXTO);
        listaPacientes.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPacientes = new JScrollPane(listaPacientes);
        scrollPacientes.setBorder(BorderFactory.createLineBorder(COLOR_BOTON));
        scrollPacientes.getVerticalScrollBar().setUnitIncrement(16);
        panelLista.add(scrollPacientes, BorderLayout.CENTER);

        // Actualizar lista inicial (ahora desde BD)
        actualizarListaPacientes = () -> {
            modeloPacientes.clear();
            for (Paciente p : pacienteController.obtenerTodos()) {
                modeloPacientes.addElement(formatearPaciente(p));
            }
        };
        actualizarListaPacientes.run();

        // Layout
        JPanel panelContenedor = new JPanel(new GridLayout(1, 2, 15, 0));
        panelContenedor.setBackground(COLOR_FONDO);
        panelContenedor.add(scrollIzquierdo);  // Usar el scroll en lugar del panel directamente
        panelContenedor.add(panelLista);

        panelPrincipal.add(panelContenedor, BorderLayout.CENTER);
        return panelPrincipal;
    }

    // ==================== PANEL DE PROFESIONALES ====================
    private JPanel crearPanelProfesionales() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel izquierdo
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBackground(COLOR_FONDO);

        // Agregar Profesional
        JPanel panelAgregar = crearPanelSeccion("Agregar Nuevo Profesional");
        JTextField txtNombreProf = crearCampoTexto("Nombre del profesional");
        JTextField txtApellidoProf = crearCampoTexto("Apellido");
        JTextField txtDniProf = crearCampoTexto("DNI");
        JTextField txtEspecialidad = crearCampoTexto("Especialidad");

        panelAgregar.add(txtNombreProf);
        panelAgregar.add(Box.createVerticalStrut(10));
        panelAgregar.add(txtApellidoProf);
        panelAgregar.add(Box.createVerticalStrut(10));
        panelAgregar.add(txtDniProf);
        panelAgregar.add(Box.createVerticalStrut(10));
        panelAgregar.add(txtEspecialidad);
        panelAgregar.add(Box.createVerticalStrut(15));

        JButton btnAgregarProf = crearBoton("+ Agregar Profesional", COLOR_EXITO);

        // Modificar Profesional
        JPanel panelModificar = crearPanelSeccion("Modificar Profesional");
        JTextField txtIdModificar = crearCampoTexto("ID del profesional a modificar");
        JTextField txtNuevoNombre = crearCampoTexto("Nuevo nombre (opcional)");
        JTextField txtNuevoApellido = crearCampoTexto("Nuevo apellido (opcional)");
        JTextField txtNuevoDni = crearCampoTexto("Nuevo DNI (opcional)");
        JTextField txtNuevaEspecialidad = crearCampoTexto("Nueva especialidad (opcional)");

        JButton btnModificar = crearBoton("✏️ Modificar Profesional", new Color(241, 196, 15)); // Amarillo

        panelModificar.add(txtIdModificar);
        panelModificar.add(Box.createVerticalStrut(10));
        panelModificar.add(txtNuevoNombre);
        panelModificar.add(Box.createVerticalStrut(10));
        panelModificar.add(txtNuevoApellido);
        panelModificar.add(Box.createVerticalStrut(10));
        panelModificar.add(txtNuevoDni);
        panelModificar.add(Box.createVerticalStrut(10));
        panelModificar.add(txtNuevaEspecialidad);
        panelModificar.add(Box.createVerticalStrut(15));
        panelModificar.add(btnModificar);

        // Eliminar Profesional
        JPanel panelEliminar = crearPanelSeccion("Eliminar Profesional");
        JTextField txtIdEliminar = crearCampoTexto("ID del profesional a eliminar");
        JButton btnEliminar = crearBoton("🗑️ Eliminar Profesional", COLOR_PELIGRO);

        panelEliminar.add(txtIdEliminar);
        panelEliminar.add(Box.createVerticalStrut(15));
        panelEliminar.add(btnEliminar);

        btnAgregarProf.addActionListener(e -> {
            try {
                String nombre = obtenerTexto(txtNombreProf);
                String apellido = obtenerTexto(txtApellidoProf);
                int dni = Integer.parseInt(obtenerTexto(txtDniProf));
                String esp = obtenerTexto(txtEspecialidad);

                Profesional p = profesionalController.crearProfesional(nombre, apellido, dni, esp);
                JOptionPane.showMessageDialog(this, "Profesional agregado exitosamente\nID: " + p.getId(),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos(txtNombreProf, txtApellidoProf, txtDniProf, txtEspecialidad);
                actualizarListaProfesionales.run();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: El DNI debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelAgregar.add(btnAgregarProf);

        // Acción para modificar profesional
        btnModificar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(obtenerTexto(txtIdModificar));
                String nuevoNombre = obtenerTexto(txtNuevoNombre);
                String nuevoApellido = obtenerTexto(txtNuevoApellido);
                String nuevoDniStr = obtenerTexto(txtNuevoDni);
                String nuevaEspecialidad = obtenerTexto(txtNuevaEspecialidad);

                // Convertir DNI solo si se proporcionó un valor
                Integer nuevoDni = null;
                if (!nuevoDniStr.isEmpty()) {
                    nuevoDni = Integer.parseInt(nuevoDniStr);
                }

                // Modificar el profesional
                Profesional profesionalModificado = profesionalController.modificarProfesional(
                        id,
                        nuevoNombre.isEmpty() ? null : nuevoNombre,
                        nuevoApellido.isEmpty() ? null : nuevoApellido,
                        nuevoDni != null ? nuevoDni : -1, // Usar -1 para indicar que no se cambia
                        nuevaEspecialidad.isEmpty() ? null : nuevaEspecialidad
                );

                JOptionPane.showMessageDialog(this,
                        "Profesional modificado exitosamente\nID: " + id
                        + "\nNombre: " + profesionalModificado.getNombre() + " " + profesionalModificado.getApellido()
                        + "\nDNI: " + profesionalModificado.getDni()
                        + "\nEspecialidad: " + profesionalModificado.getEspecialidad(),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // Limpiar campos de modificación
                limpiarCampos(txtIdModificar, txtNuevoNombre, txtNuevoApellido, txtNuevoDni, txtNuevaEspecialidad);
                actualizarListaProfesionales.run();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: El ID y DNI deben ser números válidos", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Acción para eliminar profesional
        btnEliminar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(obtenerTexto(txtIdEliminar));

                // Confirmar eliminación
                int confirmacion = JOptionPane.showConfirmDialog(
                        this,
                        "¿Está seguro de eliminar el profesional con ID " + id + "?\n"
                        + "Esta acción no se puede deshacer.",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirmacion == JOptionPane.YES_OPTION) {
                    boolean eliminado = profesionalController.eliminarProfesional(id);
                    if (eliminado) {
                        JOptionPane.showMessageDialog(this,
                                "Profesional eliminado exitosamente",
                                "Éxito", JOptionPane.INFORMATION_MESSAGE);

                        // Limpiar campo de eliminación
                        txtIdEliminar.setText("");
                        actualizarListaProfesionales.run();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: El ID debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Buscar Profesional
        JPanel panelBuscar = crearPanelSeccion("Buscar Profesional");
        JTextField txtBuscarDniProf = crearCampoTexto("DNI del profesional");
        JButton btnBuscarProf = crearBoton("🔍 Buscar", COLOR_ACENTO);

        btnBuscarProf.addActionListener(e -> {
            try {
                int dni = Integer.parseInt(obtenerTexto(txtBuscarDniProf));
                Profesional p = profesionalController.buscarPorDni(dni);
                if (p != null) {
                    String info = String.format("Profesional encontrado:\nID: %d\nNombre: %s %s\nDNI: %d\nEspecialidad: %s",
                            p.getId(), p.getNombre(), p.getApellido(), p.getDni(), p.getEspecialidad());
                    JOptionPane.showMessageDialog(this, info, "Resultado de búsqueda", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró profesional con DNI: " + dni,
                            "No encontrado", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: Ingrese un DNI válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });



        JPanel panelBuscarBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBuscarBotones.setBackground(COLOR_PANEL);
        panelBuscarBotones.add(btnBuscarProf);

        panelBuscar.add(txtBuscarDniProf);
        panelBuscar.add(Box.createVerticalStrut(10));
        panelBuscar.add(panelBuscarBotones);

        panelIzquierdo.add(panelAgregar);
        panelIzquierdo.add(Box.createVerticalStrut(20));
        panelIzquierdo.add(panelModificar);
        panelIzquierdo.add(Box.createVerticalStrut(20));
        panelIzquierdo.add(panelEliminar);
        panelIzquierdo.add(Box.createVerticalStrut(20));
        panelIzquierdo.add(panelBuscar);
        panelIzquierdo.add(Box.createVerticalGlue());

        // Hacer el panel izquierdo scrollable
        JScrollPane scrollIzquierdo = new JScrollPane(panelIzquierdo);
        scrollIzquierdo.setBorder(null);
        scrollIzquierdo.getVerticalScrollBar().setUnitIncrement(16);
        scrollIzquierdo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Panel derecho - Lista
        JPanel panelLista = crearPanelLista("Lista de Profesionales");
        DefaultListModel<String> modeloProfesionales = new DefaultListModel<>();
        JList<String> listaProfesionales = new JList<>(modeloProfesionales);
        listaProfesionales.setBackground(COLOR_PANEL);
        listaProfesionales.setForeground(COLOR_TEXTO);
        listaProfesionales.setFont(new Font("Monospaced", Font.PLAIN, 12));
        listaProfesionales.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollProfesionales = new JScrollPane(listaProfesionales);
        scrollProfesionales.setBorder(BorderFactory.createLineBorder(COLOR_BOTON));
        scrollProfesionales.getVerticalScrollBar().setUnitIncrement(16);
        panelLista.add(scrollProfesionales, BorderLayout.CENTER);

        // Panel de información del profesional seleccionado
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(COLOR_PANEL);
        panelInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelInfo.setVisible(false);

        JLabel lblInfoTitulo = new JLabel("Profesional Seleccionado");
        lblInfoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInfoTitulo.setForeground(COLOR_TEXTO);
        lblInfoTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblInfoDetalle = new JLabel();
        lblInfoDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfoDetalle.setForeground(COLOR_TEXTO);
        lblInfoDetalle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelInfo.add(lblInfoTitulo);
        panelInfo.add(Box.createVerticalStrut(10));
        panelInfo.add(lblInfoDetalle);
        panelLista.add(panelInfo, BorderLayout.SOUTH);

        // Listener para selección de profesionales
        listaProfesionales.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = listaProfesionales.getSelectedValue();
                if (selected != null) {
                    panelInfo.setVisible(true);
                    lblInfoDetalle.setText("<html>" + selected.replace(" | ", "<br>") + "</html>");

                    // Extraer ID del profesional seleccionado para facilitar modificación/eliminación
                    try {
                        String[] partes = selected.split("\\|");
                        if (partes.length > 0) {
                            String idStr = partes[0].replace("ID:", "").trim();
                            txtIdModificar.setText(idStr);
                            txtIdEliminar.setText(idStr);
                        }
                    } catch (Exception ex) {
                        // Ignorar error si no se puede parsear
                    }
                } else {
                    panelInfo.setVisible(false);
                }
            }
        });

        actualizarListaProfesionales = () -> {
            modeloProfesionales.clear();
            for (Profesional p : profesionalController.obtenerTodos()) {
                modeloProfesionales.addElement(formatearProfesional(p));
            }
            panelInfo.setVisible(false);
            // Limpiar campos después de actualizar
            txtIdModificar.setText("");
            txtIdEliminar.setText("");
        };
        actualizarListaProfesionales.run();

        // Layout
        JPanel panelContenedor = new JPanel(new GridLayout(1, 2, 15, 0));
        panelContenedor.setBackground(COLOR_FONDO);
        panelContenedor.add(scrollIzquierdo);  // Usar el scroll en lugar del panel directamente
        panelContenedor.add(panelLista);

        panelPrincipal.add(panelContenedor, BorderLayout.CENTER);
        return panelPrincipal;
    }

    // ==================== PANEL DE TURNOS ====================
    private JPanel crearPanelTurnos() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel izquierdo
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBackground(COLOR_FONDO);

        // Crear Turno
        JPanel panelCrear = crearPanelSeccion("Crear Nuevo Turno");
        JTextField txtDniPacTurno = crearCampoTexto("DNI del paciente");
        JTextField txtDniProfTurno = crearCampoTexto("DNI del profesional");
        JTextField txtFechaTurno = crearCampoTexto("Fecha (dd/MM/yyyy HH:mm)");
        txtFechaTurno.setText(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        txtFechaTurno.setForeground(COLOR_TEXTO);
        txtFechaTurno.setEditable(false); // Hacer no editable para forzar uso del calendario
        txtFechaTurno.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Agregar tooltip
        txtFechaTurno.setToolTipText("Haz clic para seleccionar fecha y hora");

        // Agregar listener para abrir el selector de fecha
        txtFechaTurno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                abrirSelectorFecha(txtFechaTurno);
            }
        });
        // También permitir que se pueda hacer clic en cualquier parte del campo
        txtFechaTurno.addActionListener(e -> abrirSelectorFecha(txtFechaTurno));

        panelCrear.add(txtDniPacTurno);
        panelCrear.add(Box.createVerticalStrut(10));
        panelCrear.add(txtDniProfTurno);
        panelCrear.add(Box.createVerticalStrut(10));
        panelCrear.add(txtFechaTurno);
        panelCrear.add(Box.createVerticalStrut(15));

        JButton btnCrearTurno = crearBoton("+ Crear Turno", COLOR_EXITO);

        // Gestionar Turno - Panel con todos los estados
        JPanel panelGestionar = crearPanelSeccion("Gestionar Turno");
        JTextField txtIdTurno = crearCampoTexto("ID del turno");

        // Panel para botones de estados
        JPanel panelBotonesEstados = new JPanel(new GridLayout(2, 3, 10, 10));
        panelBotonesEstados.setBackground(COLOR_PANEL);
        panelBotonesEstados.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Botones para cada estado
        JButton btnCancelar = crearBoton("Cancelar", COLOR_PELIGRO);
        JButton btnConfirmar = crearBoton("Confirmar", new Color(41, 128, 185));
        JButton btnMarcarAtendido = crearBoton("Atendido", COLOR_EXITO);
        JButton btnMarcarAusente = crearBoton("Ausente", new Color(230, 126, 34));
        JButton btnPendiente = crearBoton("Pendiente", new Color(149, 165, 166));
        JButton btnVerDetalle = crearBoton("Ver Detalle", COLOR_ACENTO);

        // Acciones para cada botón
        btnCancelar.addActionListener(e -> cambiarEstadoTurno(txtIdTurno, EstadoDeTurno.CANCELADO));
        btnConfirmar.addActionListener(e -> cambiarEstadoTurno(txtIdTurno, EstadoDeTurno.CONFIRMADO));
        btnMarcarAtendido.addActionListener(e -> cambiarEstadoTurno(txtIdTurno, EstadoDeTurno.ATENDIDO));
        btnMarcarAusente.addActionListener(e -> cambiarEstadoTurno(txtIdTurno, EstadoDeTurno.AUSENTE));
        btnPendiente.addActionListener(e -> cambiarEstadoTurno(txtIdTurno, EstadoDeTurno.PENDIENTE));

        // Botón para ver detalles del turno
        btnVerDetalle.addActionListener(e -> {
            try {
                int id = Integer.parseInt(obtenerTexto(txtIdTurno));
                Turno turno = turnoController.buscarPorId(id);
                if (turno != null) {
                    String detalles = String.format(
                            "<html><b>Detalles del Turno:</b><br>"
                            + "ID: %d<br>"
                            + "Estado: %s<br>"
                            + "Fecha: %s<br>"
                            + "Duración: %d minutos<br>"
                            + "Paciente: %s %s (DNI: %d)<br>"
                            + "Profesional: %s %s - %s<br>"
                            + "Teléfono paciente: %s<br>"
                            + "Obra social: %s</html>",
                            turno.getId(),
                            turno.getEstado(),
                            turno.getFechaYHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            java.time.Duration.between(turno.getFechaYHoraInicio(), turno.getfechaYHoraFin()).toMinutes(),
                            turno.getPaciente().getNombre(), turno.getPaciente().getApellido(), turno.getPaciente().getDni(),
                            turno.getProfesional().getNombre(), turno.getProfesional().getApellido(), turno.getProfesional().getEspecialidad(),
                            turno.getPaciente().getTelefono(),
                            turno.getPaciente().getObraSocial()
                    );
                    JOptionPane.showMessageDialog(this, detalles, "Detalles del Turno", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró un turno con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: Ingrese un ID válido", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Agregar botones al panel
        panelBotonesEstados.add(btnPendiente);
        panelBotonesEstados.add(btnConfirmar);
        panelBotonesEstados.add(btnMarcarAtendido);
        panelBotonesEstados.add(btnMarcarAusente);
        panelBotonesEstados.add(btnCancelar);
        panelBotonesEstados.add(btnVerDetalle);

        panelGestionar.add(txtIdTurno);
        panelGestionar.add(Box.createVerticalStrut(15));
        panelGestionar.add(panelBotonesEstados);
        panelGestionar.add(Box.createVerticalStrut(10));

        // Botón para actualizar lista
        JButton btnActualizarLista = crearBoton("Actualizar Lista", new Color(155, 89, 182));
        btnActualizarLista.addActionListener(e -> {
            actualizarListaTurnos.run();
            JOptionPane.showMessageDialog(this, "Lista actualizada", "Información", JOptionPane.INFORMATION_MESSAGE);
        });
        panelGestionar.add(btnActualizarLista);

        btnCrearTurno.addActionListener(e -> {
            try {
                int dniPac = Integer.parseInt(obtenerTexto(txtDniPacTurno));
                int dniProf = Integer.parseInt(obtenerTexto(txtDniProfTurno));

                Paciente pac = pacienteController.buscarPorDni(dniPac);
                Profesional prof = profesionalController.buscarPorDni(dniProf);

                if (pac == null || prof == null) {
                    JOptionPane.showMessageDialog(this, "Paciente o profesional no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                LocalDateTime fechaInicio = LocalDateTime.parse(txtFechaTurno.getText(), formatter);
                LocalDateTime fechaFin = fechaInicio.plusHours(1);

                Turno t = turnoController.crearTurno(fechaInicio, fechaFin, pac, prof);
                JOptionPane.showMessageDialog(this, "Turno creado exitosamente\nID: " + t.getId());
                limpiarCampos(txtDniPacTurno, txtDniProfTurno);
                txtFechaTurno.setText(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                txtFechaTurno.setForeground(COLOR_TEXTO);
                actualizarListaTurnos.run();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Error: Formato de fecha incorrecto", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelCrear.add(btnCrearTurno);

        panelIzquierdo.add(panelCrear);
        panelIzquierdo.add(Box.createVerticalStrut(20));
        panelIzquierdo.add(panelGestionar);
        panelIzquierdo.add(Box.createVerticalGlue());

        // Hacer el panel izquierdo scrollable
        JScrollPane scrollIzquierdo = new JScrollPane(panelIzquierdo);
        scrollIzquierdo.setBorder(null);
        scrollIzquierdo.getVerticalScrollBar().setUnitIncrement(16);
        scrollIzquierdo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Panel derecho - Lista
        JPanel panelLista = crearPanelLista("Lista de Turnos");
        DefaultListModel<String> modeloTurnos = new DefaultListModel<>();
        JList<String> listaTurnos = new JList<>(modeloTurnos);
        listaTurnos.setBackground(COLOR_PANEL);
        listaTurnos.setForeground(COLOR_TEXTO);
        listaTurnos.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JScrollPane scrollTurnos = new JScrollPane(listaTurnos);
        scrollTurnos.setBorder(BorderFactory.createLineBorder(COLOR_BOTON));
        scrollTurnos.getVerticalScrollBar().setUnitIncrement(16);
        panelLista.add(scrollTurnos, BorderLayout.CENTER);

        // Panel de información del turno seleccionado
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(COLOR_PANEL);
        panelInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelInfo.setVisible(false);

        JLabel lblInfoTitulo = new JLabel("Turno Seleccionado");
        lblInfoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInfoTitulo.setForeground(COLOR_TEXTO);
        lblInfoTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblInfoDetalle = new JLabel();
        lblInfoDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfoDetalle.setForeground(COLOR_TEXTO);
        lblInfoDetalle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelInfo.add(lblInfoTitulo);
        panelInfo.add(Box.createVerticalStrut(10));
        panelInfo.add(lblInfoDetalle);
        panelLista.add(panelInfo, BorderLayout.SOUTH);

        // Listener para selección de turnos
        listaTurnos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = listaTurnos.getSelectedValue();
                if (selected != null) {
                    panelInfo.setVisible(true);
                    lblInfoDetalle.setText("<html>" + selected.replace(" | ", "<br>") + "</html>");
                } else {
                    panelInfo.setVisible(false);
                }
            }
        });

        actualizarListaTurnos = () -> {
            modeloTurnos.clear();
            for (Turno t : turnoController.obtenerTodos()) {
                modeloTurnos.addElement(formatearTurno(t));
            }
            panelInfo.setVisible(false);
        };
        actualizarListaTurnos.run();

        // Layout
        JPanel panelContenedor = new JPanel(new GridLayout(1, 2, 15, 0));
        panelContenedor.setBackground(COLOR_FONDO);
        panelContenedor.add(scrollIzquierdo);  // Usar el scroll en lugar del panel directamente
        panelContenedor.add(panelLista);

        panelPrincipal.add(panelContenedor, BorderLayout.CENTER);
        return panelPrincipal;
    }

    // ==================== MÉTODOS AUXILIARES ====================
    private JPanel crearPanelSeccion(String titulo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BOTON),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(15));

        return panel;
    }

    private void abrirSelectorFecha(JTextField campoFecha) {
        try {
            // Intentar parsear la fecha actual si existe
            LocalDateTime fechaActual = null;
            String textoActual = obtenerTexto(campoFecha);
            if (!textoActual.isEmpty() && !textoActual.equals(campoFecha.getClientProperty("placeholder"))) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    fechaActual = LocalDateTime.parse(textoActual, formatter);
                } catch (Exception e) {
                    // Si no se puede parsear, usar fecha por defecto
                    fechaActual = LocalDateTime.now().plusDays(1);
                }
            }

            // Mostrar el selector de fecha
            SelectorFechaDialog dialog = new SelectorFechaDialog(this);
            dialog.setVisible(true);

            if (dialog.isOk()) {
                LocalDateTime fechaSeleccionada = dialog.getFechaSeleccionada();
                if (fechaSeleccionada != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    campoFecha.setText(fechaSeleccionada.format(formatter));
                    campoFecha.setForeground(COLOR_TEXTO);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al seleccionar fecha: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstadoTurno(JTextField txtIdTurno, EstadoDeTurno estado) {
        try {
            int id = Integer.parseInt(obtenerTexto(txtIdTurno));
            String nombreEstado = estado.toString().toLowerCase();

            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    String.format("¿Está seguro de cambiar el estado del turno %d a '%s'?", id, nombreEstado),
                    "Confirmar cambio de estado",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                // Usar el método correspondiente según el estado
                switch (estado) {
                    case CANCELADO:
                        turnoController.cancelarTurno(id);
                        break;
                    case CONFIRMADO:
                        turnoController.confirmarTurno(id);
                        break;
                    case ATENDIDO:
                        turnoController.marcarAtendido(id);
                        break;
                    case AUSENTE:
                        turnoController.marcarAusente(id);
                        break;
                    case PENDIENTE:
                        turnoController.cambiarEstado(id, EstadoDeTurno.PENDIENTE);
                        break;
                }

                JOptionPane.showMessageDialog(this,
                        String.format("Turno %d actualizado a estado: %s", id, nombreEstado),
                        "Estado actualizado",
                        JOptionPane.INFORMATION_MESSAGE);

                actualizarListaTurnos.run();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Ingrese un ID de turno válido", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel crearPanelLista(String titulo) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BOTON),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_TEXTO);
        panel.add(lblTitulo, BorderLayout.NORTH);

        return panel;
    }

    private JTextField crearCampoTexto(String placeholder) {
        JTextField campo = new JTextField();
        campo.setBackground(COLOR_BOTON);
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_TEXTO);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                new EmptyBorder(8, 10, 8, 10)
        ));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Placeholder
        campo.setText(placeholder);
        campo.setForeground(new Color(150, 150, 150));
        campo.putClientProperty("placeholder", placeholder);

        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (campo.getText().equals(campo.getClientProperty("placeholder"))) {
                    campo.setText("");
                    campo.setForeground(COLOR_TEXTO);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (campo.getText().isEmpty()) {
                    campo.setForeground(new Color(150, 150, 150));
                    campo.setText((String) campo.getClientProperty("placeholder"));
                }
            }
        });

        return campo;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

    private String obtenerTexto(JTextField campo) {
        String texto = campo.getText();
        String placeholder = (String) campo.getClientProperty("placeholder");
        if (texto.equals(placeholder)) {
            return "";
        }
        return texto;
    }

    private void limpiarCampos(JTextField... campos) {
        for (JTextField campo : campos) {
            campo.setText("");
            String placeholder = (String) campo.getClientProperty("placeholder");
            if (placeholder != null) {
                campo.setText(placeholder);
                campo.setForeground(new Color(150, 150, 150));
            }
        }
    }

    private String formatearPaciente(Paciente p) {
        return String.format("ID: %-3d | DNI: %-10d | %s, %s | OS: %s",
                p.getId(), p.getDni(), p.getApellido(), p.getNombre(), p.getObraSocial());
    }

    private String formatearProfesional(Profesional p) {
        return String.format("ID: %-3d | DNI: %-10d | %s, %s | %s",
                p.getId(), p.getDni(), p.getApellido(), p.getNombre(), p.getEspecialidad());
    }

    private String formatearTurno(Turno t) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("ID: %-3d | %s | Pac: %s | Prof: %s | %s",
                t.getId(),
                t.getFechaYHoraInicio().format(fmt),
                t.getPaciente().getApellido(),
                t.getProfesional().getApellido(),
                t.getEstado());
    }

    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
        });
    }
}
