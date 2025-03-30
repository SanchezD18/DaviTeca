
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private final JPanel mainPanel;

    public VentanaPrincipal() {
        setTitle("DaviTeca");
        Toolkit mipantalla= Toolkit.getDefaultToolkit();
        Dimension dimension = mipantalla.getScreenSize();
        this.setSize(dimension.width/4, dimension.height/2);
        this.setLocation(dimension.width/4, dimension.height/4);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        Image icono = mipantalla.getImage("libro.png");
        this.setIconImage(icono);
        this.setResizable(false);

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(255, 253, 208)); // Color crema
        Container panelPadre = mainPanel;

        creacionInterfazEntrada(panelPadre);
        add(mainPanel);
    }

    private void creacionInterfazEntrada(Container panelPadre) {
        Font fuenteBotones = new Font("Arial", Font.BOLD, 20);

        //Botón Login
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(fuenteBotones);
        btnLogin.setBounds(80, 60, 300, 100);
        btnLogin.addActionListener(e -> Login(panelPadre));
        btnLogin.setBackground(new Color(0xFDCDC0));
        mainPanel.add(btnLogin);

        //Botón Registrar
        JButton btnRegistrar = new JButton("Registrarse");
        btnRegistrar.setFont(fuenteBotones);
        btnRegistrar.setBounds(100,210,260,80);
        btnRegistrar.addActionListener(e -> Registrarse(panelPadre));
        btnRegistrar.setBackground(new Color(0xFFAD9B));
        mainPanel.add(btnRegistrar);

        //Botón Salir
        JButton btnSalir = new JButton("Salir");
        btnSalir.setFont(fuenteBotones);
        btnSalir.setBounds(120,340,220,60);
        btnSalir.addActionListener(e -> System.exit(0));
        mainPanel.add(btnSalir);
        btnSalir.setBackground(new Color(0xFF7676));

    }

    private void Login(Container panelPadre) {
        JDialog loginDialog = new JDialog(this, "Login", true);
        JPanel panel = new JPanel(new GridLayout(2, 3, 5, 5));
        panel.setBackground(new Color(255, 253, 208));
        JTextField txtUsuario = new JTextField();
        JPasswordField txtContrasena = new JPasswordField();
        JButton btnConfirmar = new JButton("Confirmar");
        JButton btnCancelar = new JButton("Cancelar");

        panel.add(new JLabel("Usuario:"));
        panel.add(txtUsuario);
        panel.add(new JLabel("Contraseña:"));
        panel.add(txtContrasena);
        panel.add(new JLabel());
        panel.add(btnConfirmar);
        panel.add(btnCancelar);

        btnConfirmar.addActionListener(e -> {
            String usuario = txtUsuario.getText();
            String contrasena = new String(txtContrasena.getPassword());

            if (Usuarios.login(usuario, contrasena)) {
                loginDialog.dispose();
                DaviTeca(usuario, contrasena, panelPadre);
                panelPadre.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas");
            }
        });
        btnCancelar.addActionListener(e -> loginDialog.dispose());

        loginDialog.add(panel);
        loginDialog.pack();
        loginDialog.setLocationRelativeTo(this);
        loginDialog.setVisible(true);
    }

    private void Registrarse(Container panelPadre) {
        JDialog registerDialog = new JDialog(this, "Registrarse", true);
        JPanel panel = new JPanel(new GridLayout(4, 4, 10, 10));
        panel.setBackground(new Color(255, 253, 208));
        JTextField usuario = new JTextField();
        JPasswordField txtContrasena = new JPasswordField();
        JTextField email = new JTextField();
        JTextField DNI = new JTextField();
        JTextField nombre = new JTextField();
        JTextField apellido = new JTextField();
        JTextField telefono = new JTextField();
        JButton btnConfirmar = new JButton("Confirmar");
        JButton btnCancelar = new JButton("Cancelar");

        panel.add(new JLabel("Usuario:"));
        panel.add(usuario);
        panel.add(new JLabel("Contraseña:"));
        panel.add(txtContrasena);
        panel.add(new JLabel("Email:"));
        panel.add(email);
        panel.add(new JLabel("DNI:"));
        panel.add(DNI);
        panel.add(new JLabel("Nombre:"));
        panel.add(nombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(apellido);
        panel.add(new JLabel("Teléfono:"));
        panel.add(telefono);

        panel.add(btnConfirmar);
        panel.add(btnCancelar);
        Component esto = this;

        btnConfirmar.addActionListener(e -> {

            String contrasena = new String(txtContrasena.getPassword());
            Usuarios.registrar(usuario.getText(),contrasena,email.getText(),DNI.getText(),nombre.getText(),apellido.getText(),telefono.getText(),esto);
            registerDialog.dispose();
            DaviTeca(usuario.getText(), contrasena, panelPadre);
            panelPadre.setVisible(false);
            });
        btnCancelar.addActionListener(e -> registerDialog.dispose());

        registerDialog.add(panel);
        registerDialog.pack();
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setVisible(true);
    }

    private void DaviTeca(String usuario, String contrasena, Container panelPadre) {

        JFrame menuFrame = new JFrame("DaviTeca");
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        Toolkit mipantalla= Toolkit.getDefaultToolkit();
        Image icono = mipantalla.getImage("libro.png");
        menuFrame.setIconImage(icono);
        Font fontBotones = new Font("Arial", Font.BOLD, 16);


        panel.setBackground(new Color(255, 253, 208));

        //Panel Norte
        JPanel panelNorte = new JPanel();
        panel.add(panelNorte, BorderLayout.NORTH);
        panelNorte.setBackground(new Color(148, 82, 2));
        JLabel textoBienvenida = new JLabel();
        Font textoBienveFont = new Font("Arial", Font.BOLD, 25);
        textoBienvenida.setForeground(Color.WHITE);
        textoBienvenida.setFont(textoBienveFont);
        textoBienvenida.setText("Bienvenid@ " + usuario + "! ¿Que deseas hacer hoy?");
        panelNorte.add(textoBienvenida);



        //Panel Centro
        CardLayout cardLayout = new CardLayout();
        JPanel panelCentro = new JPanel(cardLayout);
        panel.add(panelCentro, BorderLayout.CENTER);
        panelCentro.setBackground(new Color(66, 255, 0));

            //CardLayout
            JPanel panelVacio = crearPanelVacioEntrada();
            panelCentro.add(panelVacio, "Vacio");
            JPanel panelUsuarios = crearPanelUsuarios(usuario, contrasena);
            panelCentro.add(panelUsuarios,"Usuarios");
            JPanel panelLibros = crearPanelLibros(usuario, contrasena);
            panelCentro.add(panelLibros,"Libros");
            JPanel panelPrestamos = crearPanelPrestamos(usuario, contrasena);
            panelCentro.add(panelPrestamos,"Prestamos");

        //Panel Sud
        JPanel panelSud = new JPanel();
        panel.add(panelSud, BorderLayout.SOUTH);
        panelSud.setLayout(new FlowLayout());
        panelSud.setBackground(new Color(148, 82, 2));

            //Botón tablas Usuarios

            JButton btnUsuarios = new JButton("Usuarios");
            panelSud.add(Box.createVerticalStrut(10));
            btnUsuarios.setFont(fontBotones);
            panelSud.add(btnUsuarios);
            btnUsuarios.addActionListener(e -> mostrarPanelUsuarios(cardLayout,panelCentro));

            //Botón tablas Libros
            JButton btnLibros = new JButton("Libros");
            panelSud.add(Box.createVerticalStrut(10));
            btnLibros.setFont(fontBotones);
            panelSud.add(btnLibros);
            btnLibros.addActionListener(e -> mostrarPanelLibros(cardLayout,panelCentro));

            //Botón tablas Préstamos
            JButton btnPrestamos = new JButton("Préstamos");
            panelSud.add(Box.createVerticalStrut(10));
            btnPrestamos.setFont(fontBotones);
            panelSud.add(btnPrestamos);
            btnPrestamos.addActionListener(e -> mostrarPanelPrestamos(cardLayout,panelCentro));

            //Botón tablas Salir
            JButton btnSalir = new JButton("Salir");
            panelSud.add(Box.createVerticalStrut(10));
            panelSud.add(btnSalir);
            btnSalir.setFont(fontBotones);
            btnSalir.setBackground(new Color(0xFF7676));
            btnSalir.addActionListener(e -> {menuFrame.dispose();
            panelPadre.setVisible(true);});



        menuFrame.add(panel);
        menuFrame.setSize(1200, 680);
        menuFrame.setLocationRelativeTo(this);
        menuFrame.setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    private JPanel crearPanelUsuarios(String usuario, String contrasena) {
        CardLayout cardLayout = new CardLayout();
        JPanel panel = new JPanel(new BorderLayout());
        JPanel panelIzquierda = new JPanel();
        panelIzquierda.setBackground(new Color(255,253,208));
        JPanel panelCentro = new JPanel();
        JPanel panelDerecha = new JPanel(cardLayout);
        panelIzquierda.setLayout(new BoxLayout(panelIzquierda,BoxLayout.Y_AXIS));
        panelIzquierda.setBorder(BorderFactory.createEmptyBorder(150, 10, 50, 10));
        Font buttonFont = new Font("Arial", Font.BOLD, 12);

        panel.add(panelDerecha,BorderLayout.EAST);
        panel.add(panelCentro,BorderLayout.CENTER);
        panel.add(panelIzquierda,BorderLayout.WEST);



        //Panel Izquierdo
        //Botones tabla Usuarios
        JButton btnRegistrar = new JButton("Registrar usuario.");
        panelIzquierda.add(btnRegistrar);
        btnRegistrar.setPreferredSize(new Dimension(200,80));
        btnRegistrar.setMaximumSize(new Dimension(200,80 ));
        btnRegistrar.setFont(buttonFont);
        btnRegistrar.addActionListener(e -> Usuarios.menu(usuario, contrasena));
        panelIzquierda.add(Box.createRigidArea(new Dimension(50, 50)));

        JButton btnCambiarContrasena = new JButton("Cambiar contraseña.");
        btnCambiarContrasena.addActionListener(e -> JOptionPane.showMessageDialog(this, "pues vale."));
        btnCambiarContrasena.setPreferredSize(new Dimension(200,80));
        btnCambiarContrasena.setMaximumSize(new Dimension(200,80 ));
        btnCambiarContrasena.setFont(buttonFont);
        panelIzquierda.add(btnCambiarContrasena);
        panelIzquierda.add(Box.createRigidArea(new Dimension(50, 50)));

        JButton btnActualizarDatos = new JButton("Actualizar datos.");
        panelIzquierda.add(btnActualizarDatos);
        btnActualizarDatos.addActionListener(e -> Prestamos.devolucion(usuario));
        btnActualizarDatos.setPreferredSize(new Dimension(200,80));
        btnActualizarDatos.setMaximumSize(new Dimension(200,80 ));
        btnActualizarDatos.setFont(buttonFont);
        panelIzquierda.add(Box.createRigidArea(new Dimension(50, 50)));

        JButton btnEliminarUsuario = new JButton("Eliminar usuario por ID. (admin)");
        panelIzquierda.add(btnEliminarUsuario);
        btnEliminarUsuario.addActionListener(e -> Prestamos.devolucion(usuario));
        btnEliminarUsuario.setPreferredSize(new Dimension(200,80));
        btnEliminarUsuario.setMaximumSize(new Dimension(200,80 ));
        btnEliminarUsuario.setFont(buttonFont);
        panelIzquierda.add(Box.createRigidArea(new Dimension(50, 50)));

        JButton btnSentenciaPersonalizada = new JButton("Sentencia personalizada. (admin)");
        panelIzquierda.add(btnSentenciaPersonalizada);
        btnSentenciaPersonalizada.addActionListener(e -> Prestamos.devolucion(usuario));
        btnSentenciaPersonalizada.setPreferredSize(new Dimension(200,80));
        btnSentenciaPersonalizada.setMaximumSize(new Dimension(200,80 ));
        btnSentenciaPersonalizada.setFont(buttonFont);
        panelIzquierda.add(Box.createRigidArea(new Dimension(50, 50)));

        //Panel Centro
        panelCentro.setBackground(new Color(255, 253, 208));
        panelCentro.setLayout(new BoxLayout(panelCentro,BoxLayout.Y_AXIS));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(60, 0, 60, 0));

        DefaultTableModel modelo = new DefaultTableModel();
        JTable tablaUsuarios = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        panelCentro.add(scrollPane);
        Usuarios.mostrarTabla(usuario,modelo);

        //Panel Derecho

        JPanel panelVacio = crearPanelVacioEntrada();
        panelDerecha.add(panelVacio, "Vacio");


        return panel;}

    private JPanel crearPanelPrestamos(String usuario, String contrasena) {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel panelIzquierda = new JPanel();
        panelIzquierda.setBackground(new Color(255,253,208));
        JPanel panelCentro = new JPanel();
        panelCentro.setBackground(new Color(255,253,208));
        JPanel panelDerecha = new JPanel();
        panelDerecha.setBackground(new Color(255, 253, 208));


        panelIzquierda.setLayout(new BoxLayout(panelIzquierda,BoxLayout.Y_AXIS));
        panelIzquierda.setBorder(BorderFactory.createEmptyBorder(150, 10, 50, 10));
        Font buttonFont = new Font("Arial", Font.BOLD, 12);


        panel.add(panelDerecha,BorderLayout.EAST);
        panel.add(panelCentro,BorderLayout.CENTER);
        panel.add(panelIzquierda,BorderLayout.WEST);


        //Panel Izquierda
        //Botones tabla Usuarios
        JButton btnPedirPrestamo = new JButton("Pedir un libro prestado.");
        btnPedirPrestamo.addActionListener(e -> Prestamos.pedirPrestado(usuario));
        btnPedirPrestamo.setPreferredSize(new Dimension(200,50));
        btnPedirPrestamo.setMaximumSize(new Dimension(200,50 ));
        btnPedirPrestamo.setFont(buttonFont);
        panelIzquierda.add(btnPedirPrestamo);
        panelIzquierda.add(Box.createRigidArea(new Dimension(50, 50)));

        JButton btnListarHistorial = new JButton("Historial de prestamos.");
        panelIzquierda.add(btnListarHistorial);
        btnListarHistorial.addActionListener(e -> Prestamos.consultarTusPrestamos(usuario));
        btnListarHistorial.setPreferredSize(new Dimension(200,50));
        btnListarHistorial.setMaximumSize(new Dimension(200,50 ));
        btnListarHistorial.setFont(buttonFont);
        panelIzquierda.add(Box.createRigidArea(new Dimension(50, 50)));

        JButton btnRealizarDevolucion = new JButton("Realizar una devolución.");
        panelIzquierda.add(btnRealizarDevolucion);
        btnRealizarDevolucion.addActionListener(e -> Prestamos.devolucion(usuario));
        btnRealizarDevolucion.setPreferredSize(new Dimension(200,50));
        btnRealizarDevolucion.setMaximumSize(new Dimension(200,50 ));
        btnRealizarDevolucion.setFont(buttonFont);
        panelIzquierda.add(Box.createRigidArea(new Dimension(50, 50)));

        return panel;}


    private JPanel crearPanelLibros(String usuario, String contrasena) {
        CardLayout cardLayout = new CardLayout();
        JPanel panel = new JPanel(new BorderLayout());
        JPanel panelIzquierda = new JPanel();
        panelIzquierda.setBackground(new Color(255,253,208));
        JPanel panelCentro = new JPanel();
        panelCentro.setBackground(new Color(255,253,208));
        JPanel panelDerecha = new JPanel(cardLayout);
        panelDerecha.setBackground(new Color(255,253,208));
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        panel.add(panelDerecha,BorderLayout.EAST);
        panel.add(panelCentro,BorderLayout.CENTER);
        panel.add(panelIzquierda,BorderLayout.WEST);


        //PANEL IZQUIERDO
        panelIzquierda.setLayout(new BoxLayout(panelIzquierda,BoxLayout.Y_AXIS));
        panelIzquierda.setBorder(BorderFactory.createEmptyBorder(100, 0, 50, 35));

        //Botón tabla Libros
        JButton btnInsertarLibro = new JButton("Insertar un libro.");
        btnInsertarLibro.addActionListener(e -> Libros.mostrarPanelInsertarLibro(cardLayout,panelDerecha));
        btnInsertarLibro.setPreferredSize(new Dimension(200,80));
        btnInsertarLibro.setMaximumSize(new Dimension(200,80 ));
        btnInsertarLibro.setFont(buttonFont);
        panelIzquierda.add(btnInsertarLibro);
        panelIzquierda.add(Box.createRigidArea(new Dimension(50, 50)));

        JButton btnActualizarLibro = new JButton("Actualizar libro.");
        panelIzquierda.add(btnActualizarLibro);
        btnActualizarLibro.addActionListener(e -> Libros.mostrarPanelActualizarLibro(cardLayout,panelDerecha));
        btnActualizarLibro.setPreferredSize(new Dimension(200,80));
        btnActualizarLibro.setMaximumSize(new Dimension(200,80 ));
        btnActualizarLibro.setFont(buttonFont);
        panelIzquierda.add(Box.createRigidArea(new Dimension(50, 50)));

        JButton btnEliminarLibro = new JButton("Eliminar libro por ID.");
        panelIzquierda.add(btnEliminarLibro);
        btnEliminarLibro.addActionListener(e -> Prestamos.devolucion(usuario));
        btnEliminarLibro.setPreferredSize(new Dimension(200,80));
        btnEliminarLibro.setMaximumSize(new Dimension(200,80 ));
        btnEliminarLibro.setFont(buttonFont);
        panelIzquierda.add(Box.createRigidArea(new Dimension(50, 50)));

        JButton btnConsultarLibro = new JButton("Consultar libro por...");
        panelIzquierda.add(btnConsultarLibro);
        btnConsultarLibro.addActionListener(e -> Prestamos.devolucion(usuario));
        btnConsultarLibro.setPreferredSize(new Dimension(200,80));
        btnConsultarLibro.setMaximumSize(new Dimension(200,80 ));
        btnConsultarLibro.setFont(buttonFont);
        panelIzquierda.add(Box.createRigidArea(new Dimension(50, 50)));


        //PANEL CENTRAL
        panelCentro.setLayout(new BoxLayout(panelCentro,BoxLayout.Y_AXIS));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(60, 0, 60, 0));

        DefaultTableModel modelo = new DefaultTableModel();
        JTable tablaLibros = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaLibros);
        panelCentro.add(scrollPane);
        JButton btnRefrescar = new JButton("🔁");
        panelCentro.add(btnRefrescar);
        Libros.mostrarTabla(modelo);

        //PANEL DERECHA
        //CardLayout
        JPanel panelVacio = crearPanelVacioEntrada();
        panelDerecha.add(panelVacio, "Vacio");
        JPanel panelInsertar = Libros.crearPanelInsertar();
        panelDerecha.add(panelInsertar,"InsertarLibro");
        JPanel panelActualizar = Libros.crearPanelActualizar();
        panelDerecha.add(panelActualizar,"ActualizarLibro");
        JPanel panelEliminar = Libros.crearPanelEliminar();
        panelDerecha.add(panelEliminar,"EliminarLibro");
        JPanel panelConsultarPor = Libros.crearPanelConsultarPor();
        panelDerecha.add(panelConsultarPor, "ConsultarPorLibro");

        return panel;}

    private JPanel crearPanelVacio() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 253, 190));
        panel.add(new JLabel("Presiona un botón", SwingConstants.CENTER));



        return panel;}

    private JPanel crearPanelVacioEntrada() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel panelDerecha = new JPanel();
        panelDerecha.setBackground(new Color(148, 82, 2));
        JPanel panelCentro = new JPanel();
        panelCentro.setBackground(new Color(255, 253, 190));
        JPanel panelIzquierda = new JPanel();
        panelIzquierda.setBackground(new Color(148, 82, 2));

        panelCentro.add(new JLabel("Welcome", SwingConstants.LEFT));
        panelCentro.add(new JLabel("To DaviTeca!", SwingConstants.RIGHT));

        panel.add(panelDerecha,BorderLayout.WEST);
        panel.add(panelCentro,BorderLayout.CENTER);
        panel.add(panelIzquierda, BorderLayout.EAST);

        return panel;}




    public static void mostrarPanelUsuarios(CardLayout cardLayout, JPanel panel){
        cardLayout.show(panel,"Usuarios");}

    public static void mostrarPanelLibros(CardLayout cardLayout, JPanel panel){
        cardLayout.show(panel,"Libros");}

    public static void mostrarPanelPrestamos(CardLayout cardLayout, JPanel panel){
        cardLayout.show(panel,"Prestamos");}

    public static void mostrarPanelVacio(CardLayout cardLayout, JPanel panel){
        cardLayout.show(panel,"Vacio");}



}
