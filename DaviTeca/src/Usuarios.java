import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Usuarios {

    public static void registrar(String usuario, String contrasena, String email,
                                 String DNI, String nombre, String apellido, int telefono, Component esto){
        Connection conn = Main.connect();

        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        Statement stmt = null;
        try {
            int id_usuario;
            String sql = "INSERT INTO Usuarios (usuario, contrasena, email) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, usuario);
            pstmt.setString(2, contrasena);
            pstmt.setString(3, email);
            int rowsInserted = pstmt.executeUpdate();

            id_usuario = obtenerIDUsuario(usuario);


            String sql2 = "INSERT INTO Clientes (dni, nombre, apellido, telefono, id_usuario) VALUES (?, ?, ?, ?, ?)";
            pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setString(1, DNI);
            pstmt2.setString(2, nombre);
            pstmt2.setString(3, apellido);
            pstmt2.setInt(4, telefono);
            pstmt2.setInt(5, id_usuario);


            int rowsInserted2 = pstmt2.executeUpdate();
            if (rowsInserted > 0 && rowsInserted2 > 0) {JOptionPane.showMessageDialog(esto, "Usuario eliminado exitosamente.");}
        Main.disconnect(conn);}
        catch (Exception e) {JOptionPane.showMessageDialog(esto, "Error al eliminar usuario: " + e.getMessage());}
        finally {
            try {if (pstmt != null) pstmt.close();  if (pstmt2!= null) pstmt2.close();  if (stmt != null) stmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}



    public static void mostrarTabla(String usuario, DefaultTableModel modelo) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = Main.connect();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT Usuarios.id_usuario, usuario, contrasena, email, dni, nombre, apellido, telefono FROM Usuarios JOIN Clientes ON Usuarios.id_usuario = Clientes.id_usuario");
            if (usuario.equals("admin")){
            ResultSetMetaData metaData = rs.getMetaData();
            int columnas = metaData.getColumnCount();
            for (int i = 1; i <= columnas; i++){modelo.addColumn(metaData.getColumnName(i));}
            while (rs.next()) {
                Object[] filas = new Object[columnas];
                for (int i = 1; i <= columnas; i++){
                    filas[i - 1] = rs.getObject(i);}
                modelo.addRow(filas);}}
            else{
                ResultSetMetaData metaData = rs.getMetaData();
                int columnas = metaData.getColumnCount();
                for (int i = 1; i <= columnas; i++){modelo.addColumn(metaData.getColumnName(i));}
                while (rs.next()) {
                    Object[] filas = new Object[columnas];
                    for (int i = 1; i <= columnas; i++){
                        if(i == 3|| i == 5 || i == 8){filas[i - 1] = "*********";}
                        else {filas[i-1] = rs.getObject(i);}
                    }
                    modelo.addRow(filas);}
            }
            Main.disconnect(conn);
        }
        catch (Exception e) {System.out.println(e.getMessage());}
        finally {
            try {if (rs != null) rs.close();    if (stmt != null) stmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}


public static void cambiarContrasena(String usuario, String contrasenaAnterior, String contrasenaNueva, Component panel){
    Connection conn = Main.connect();
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        String sql = "SELECT contrasena FROM Usuarios WHERE usuario = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, usuario);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            String contrasenaActualBD = rs.getString("contrasena");

            if (!contrasenaAnterior.equals(contrasenaActualBD)) {
                JOptionPane.showMessageDialog(panel, "La contraseña actual es incorrecta", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            sql = "UPDATE Usuarios SET contrasena = ? WHERE usuario = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, contrasenaNueva);
            pstmt.setString(2, usuario);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(panel, "Contraseña cambiada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                panel.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(panel, "Error al cambiar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(panel, "Error de base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            Main.disconnect(conn);
        } catch (SQLException ex) {
            System.out.println("Error al cerrar conexión: " + ex.getMessage());
        }
    }
}

    public static void eliminarInterfaz(String id, Component esto, String usuario){
        Connection conn = Main.connect();
        int numId = Integer.parseInt(id);
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        try {
            if (usuario.equals("admin")){
                String sql = "DELETE FROM Usuarios WHERE id_usuario = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, numId);

                String sql2 = "DELETE FROM Clientes WHERE id_usuario = ?";
                pstmt2 = conn.prepareStatement(sql2);
                pstmt2.setInt(1, numId);
                pstmt2.executeUpdate();}

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {JOptionPane.showMessageDialog(esto, "Usuario eliminado exitosamente.");}
            else {JOptionPane.showMessageDialog(esto,"No se encontró un Usuario con el ID proporcionado.");}}
        catch (Exception e) {JOptionPane.showMessageDialog(esto, "Error al eliminar usuario: " + e.getMessage());}
        finally {
            try {if (pstmt != null) pstmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}


    public static void sentenciaPersonalizadaInterfaz(String sentencia, Component esto, String usuario){
        Connection conn = Main.connect();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            if (usuario.equals("admin")){
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sentencia);
            }
            else System.out.println("No eres administrador.");
            Main.disconnect(conn);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());}}}

    public static boolean login(String usuario, String contrasena){
        Connection conn = Main.connect();
        String usuarioComprobar = "", contrasenaComprobar = "";
        Statement stmt = null;
        if(usuario.isEmpty() || contrasena.isEmpty()){
            System.out.println("No has introducido datos.");}
        else{
        try {String sql = "SELECT * FROM Usuarios WHERE usuario =\"" + usuario + "\" AND contrasena =\"" + contrasena + "\"";
            stmt = conn.createStatement();
            ResultSet comprobacion = stmt.executeQuery(sql);
            while (comprobacion.next()) {
                usuarioComprobar = comprobacion.getString("usuario");
                contrasenaComprobar = comprobacion.getString("contrasena");
                System.out.println();}
            Main.disconnect(conn);
            return usuario.equals(usuarioComprobar) && contrasena.equals(contrasenaComprobar);}
        catch (Exception e) {System.out.println("Error en los datos de login: " + e.getMessage());}
        finally {
            try {if (stmt != null) stmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}
        return false;}


public static boolean validarDNI(String dni, Component esto){
    String regex = "^[a-zA-Z0-9]{9,10}$";
    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(dni);
    boolean matchFound = matcher.find();
        if(!matchFound) {JOptionPane.showMessageDialog(esto,"DNI inválido. Formato: 00000000X");
            return false;}
    return true;}


    public static boolean validarEmail(String email, Component esto){
        String regex = "^[a-z]+[0-9]{0,2}@[a-z]+\\.[a-z]{2,3}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        boolean matchFound = matcher.find();
            if(!matchFound) {JOptionPane.showMessageDialog(esto, "Email inválido. Formato: algo@ejemplo.com");
                return false;}
        return true;}


    public static boolean validarTelefono(int telefono, Component esto){
            if (telefono < 600000000 || telefono > 799999999){
           JOptionPane.showMessageDialog(esto, "Telefono no válido. (formato 600000000");
            return false;}
        return true;}


    public static int obtenerIDUsuario(String usuario){
        Connection conn = Main.connect();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            String sql2 = "SELECT id_usuario FROM Usuarios WHERE usuario = \"" + usuario + "\"";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql2);
            int id_usuario = rs.getInt("id_usuario");
            Main.disconnect(conn);
            return id_usuario;
        }
        catch (Exception e) {System.out.println(e.getMessage());}
        finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}
    return 0;}



    public static JPanel crearPanelRegistrarUsuario(){
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 253, 208));
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 25, 50, 25));
        panel.add(Box.createRigidArea(new Dimension(200, 50)));

        JTextField campoUsuario = new JTextField(10);
        campoUsuario.setPreferredSize(new Dimension(200,40));
        campoUsuario.setMaximumSize(new Dimension(200,40));

        JTextField campoContrasena = new JTextField(10);
        campoContrasena.setPreferredSize(new Dimension(200,40));
        campoContrasena.setMaximumSize(new Dimension(200,40));

        JTextField campoEmail = new JTextField(10);
        campoEmail.setPreferredSize(new Dimension(200,40));
        campoEmail.setMaximumSize(new Dimension(200,40));

        JTextField campoDNI = new JTextField(10);
        campoDNI.setPreferredSize(new Dimension(200,40));
        campoDNI.setMaximumSize(new Dimension(200,40));

        JTextField campoNombre = new JTextField(10);
        campoNombre.setPreferredSize(new Dimension(200,40));
        campoNombre.setMaximumSize(new Dimension(200,40));

        JTextField campoApellido = new JTextField(10);
        campoApellido.setPreferredSize(new Dimension(200,40));
        campoApellido.setMaximumSize(new Dimension(200,40));

        JTextField campoTelefono = new JTextField(10);
        campoTelefono.setPreferredSize(new Dimension(200,40));
        campoTelefono.setMaximumSize(new Dimension(200,40));



        JButton btnInsertar = new JButton("Registrar");
        btnInsertar.setPreferredSize(new Dimension(90,30));
        btnInsertar.setMaximumSize(new Dimension(90,30));
        btnInsertar.setFont(buttonFont);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(90, 30));
        btnCancelar.setMaximumSize(new Dimension(90,30 ));
        btnCancelar.setFont(buttonFont);


        panel.add(new JLabel("Usuario:"));
        panel.add(campoUsuario);
        panel.add(Box.createRigidArea(new Dimension(100, 20)));
        panel.add(new JLabel("Contraseña:"));
        panel.add(campoContrasena);
        panel.add(Box.createRigidArea(new Dimension(100, 20)));
        panel.add(new JLabel("Email:"));
        panel.add(campoEmail);
        panel.add(Box.createRigidArea(new Dimension(100, 20)));
        panel.add(new JLabel("DNI:"));
        panel.add(campoDNI);
        panel.add(Box.createRigidArea(new Dimension(100, 20)));
        panel.add(new JLabel("Nombre:"));
        panel.add(campoNombre);
        panel.add(Box.createRigidArea(new Dimension(100, 20)));
        panel.add(new JLabel("Apellido:"));
        panel.add(campoApellido);
        panel.add(Box.createRigidArea(new Dimension(100, 20)));
        panel.add(new JLabel("Teléfono:"));
        panel.add(campoTelefono);
        panel.add(Box.createRigidArea(new Dimension(100, 50)));

        JPanel panelDebajo = new JPanel();
        panelDebajo.setLayout(new BoxLayout(panelDebajo,BoxLayout.X_AXIS));
        panelDebajo.add(btnInsertar);
        panelDebajo.add(btnCancelar);
        panel.add(panelDebajo);


        btnInsertar.addActionListener(e ->{
            int telefonoInt = Integer.parseInt(campoTelefono.getText());
            if(Usuarios.validarDNI(campoDNI.getText(),panel) && Usuarios.validarEmail(campoEmail.getText(),panel) && Usuarios.validarTelefono(telefonoInt,panel)) {
                telefonoInt = Integer.parseInt(campoTelefono.getText());
                Usuarios.registrar(campoUsuario.getText(), campoContrasena.getText(), campoEmail.getText(), campoDNI.getText(), campoNombre.getText(), campoApellido.getText(), telefonoInt, panel);}
            else{JOptionPane.showMessageDialog(panel, "Revisa bien los datos");}
            campoUsuario.setText("");
            campoContrasena.setText("");
            campoEmail.setText("");
            campoDNI.setText("");
            campoNombre.setText("");
            campoApellido.setText("");
            campoTelefono.setText("");
        });
        btnCancelar.addActionListener(e -> panel.setVisible(false));
        return panel;}


    public static JPanel crearPanelCambiarContrasena(String usuario){
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 253, 208));
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 25, 50, 25));
        panel.add(Box.createRigidArea(new Dimension(200, 20)));
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JPasswordField campoContrasenaAnterior = new JPasswordField(5);
        campoContrasenaAnterior.setPreferredSize(new Dimension(200,30));
        campoContrasenaAnterior.setMaximumSize(new Dimension(200,30));

        JPasswordField campoContrasenaNueva = new JPasswordField(5);
        campoContrasenaNueva.setPreferredSize(new Dimension(200,30));
        campoContrasenaNueva.setMaximumSize(new Dimension(200,30));

        JPasswordField campoContrasenaNueva2 = new JPasswordField(5);
        campoContrasenaNueva2.setPreferredSize(new Dimension(200,30));
        campoContrasenaNueva2.setMaximumSize(new Dimension(200,30));

        JButton btnCambiar = new JButton("Cambiar");
        btnCambiar.setPreferredSize(new Dimension(90,30));
        btnCambiar.setMaximumSize(new Dimension(90,30));
        btnCambiar.setFont(buttonFont);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(90, 30));
        btnCancelar.setMaximumSize(new Dimension(90,30 ));
        btnCancelar.setFont(buttonFont);

        panel.add(new JLabel("Contraseña"));
        panel.add(new JLabel("Anterior:"));
        panel.add(campoContrasenaAnterior);
        panel.add(Box.createRigidArea(new Dimension(200, 40)));
        panel.add(new JLabel("Contraseña"));
        panel.add(new JLabel("Nueva:"));
        panel.add(campoContrasenaNueva);
        panel.add(Box.createRigidArea(new Dimension(200, 40)));
        panel.add(new JLabel("Confirmación"));
        panel.add(new JLabel("Nueva"));
        panel.add(new JLabel("Contraseña:"));
        panel.add(campoContrasenaNueva2);
        panel.add(Box.createRigidArea(new Dimension(200, 40)));

        JPanel panelDebajo = new JPanel();
        panelDebajo.setLayout(new BoxLayout(panelDebajo,BoxLayout.X_AXIS));
        panelDebajo.add(btnCambiar);
        panelDebajo.add(btnCancelar);
        panel.add(panelDebajo);

        btnCambiar.addActionListener(e ->{
            String contrasenaAnterior = new String(campoContrasenaAnterior.getPassword());
            String contrasenaNueva = new String(campoContrasenaNueva.getPassword());
            String contrasenaNueva2 = new String(campoContrasenaNueva2.getPassword());
            if (contrasenaAnterior.isEmpty() || contrasenaNueva.isEmpty() || contrasenaNueva2.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!contrasenaNueva.equals(contrasenaNueva2)) {
                JOptionPane.showMessageDialog(panel, "Las contraseñas nuevas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (contrasenaNueva.equals(contrasenaAnterior)) {
                JOptionPane.showMessageDialog(panel, "La contraseña nueva debe ser diferente a la actual.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Usuarios.cambiarContrasena(usuario, contrasenaAnterior,contrasenaNueva, panel);
            panel.setVisible(false);
        });
        btnCancelar.addActionListener(e -> panel.setVisible(false));
        return panel;}

    public static JPanel crearPanelActualizarDatos(String usuario){
        int idUsuario = obtenerIDUsuario(usuario);


        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 253, 208));
        return panel;}

    public static JPanel crearPanelEliminarUsuario(String usuario){
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 253, 208));
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 25, 50, 25));
        panel.add(Box.createRigidArea(new Dimension(200, 20)));
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JTextField campoID = new JTextField(10);
        campoID.setPreferredSize(new Dimension(200,40));
        campoID.setMaximumSize(new Dimension(200,40));

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setPreferredSize(new Dimension(90,30));
        btnEliminar.setMaximumSize(new Dimension(90,30));
        btnEliminar.setFont(buttonFont);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(90, 30));
        btnCancelar.setMaximumSize(new Dimension(90,30 ));
        btnCancelar.setFont(buttonFont);

        panel.add(new JLabel("ID:"));
        panel.add(campoID);
        panel.add(Box.createRigidArea(new Dimension(200, 40)));

        JPanel panelDebajo = new JPanel();
        panelDebajo.setLayout(new BoxLayout(panelDebajo,BoxLayout.X_AXIS));
        panelDebajo.add(btnEliminar);
        panelDebajo.add(btnCancelar);
        panel.add(panelDebajo);

        btnEliminar.addActionListener(e ->{
            Usuarios.eliminarInterfaz(campoID.getText(), panel, usuario);
            campoID.setText("");
            panel.setVisible(false);
        });
        btnCancelar.addActionListener(e -> panel.setVisible(false));
        return panel;}

    public static JPanel crearPanelSentenciaPersonalizada(String usuario){
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 253, 208));
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 25, 50, 25));
        panel.add(Box.createRigidArea(new Dimension(200, 20)));
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JTextField campoSentencia = new JTextField(10);
        campoSentencia.setPreferredSize(new Dimension(200,40));
        campoSentencia.setMaximumSize(new Dimension(200,40));

        JButton btnEliminar = new JButton("Ejecutar");
        btnEliminar.setPreferredSize(new Dimension(90,30));
        btnEliminar.setMaximumSize(new Dimension(90,30));
        btnEliminar.setFont(buttonFont);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(90, 30));
        btnCancelar.setMaximumSize(new Dimension(90,30 ));
        btnCancelar.setFont(buttonFont);

        JLabel sentencia = new JLabel("Sentencia:");
        sentencia.setFont(new Font("Arial", Font.BOLD,13));
        panel.add(sentencia);
        panel.add(campoSentencia);
        panel.add(Box.createRigidArea(new Dimension(200, 40)));

        JPanel panelDebajo = new JPanel();
        panelDebajo.setLayout(new BoxLayout(panelDebajo,BoxLayout.X_AXIS));
        panelDebajo.add(btnEliminar);
        panelDebajo.add(btnCancelar);
        panel.add(panelDebajo);

        btnEliminar.addActionListener(e ->{
            Usuarios.sentenciaPersonalizadaInterfaz(campoSentencia.getText(), panel, usuario);
            campoSentencia.setText("");
            panel.setVisible(false);
        });
        btnCancelar.addActionListener(e -> panel.setVisible(false));
        return panel;}


    public static void mostrarPanelRegistrarUsuario(CardLayout cardLayout, JPanel panel){
        cardLayout.show(panel, "RegistrarUsuario");}

    public static void mostrarPanelCambiarContrasena(CardLayout cardLayout, JPanel panel){
        cardLayout.show(panel,"CambiarContrasena");}

    public static void mostrarPanelActualizarDatos(CardLayout cardLayout, JPanel panel){
        cardLayout.show(panel,"ActualizarDatos");}

    public static void mostrarPanelEliminarUsuario(CardLayout cardLayout, JPanel panel){
        cardLayout.show(panel, "EliminarUsuario");}

    public static void mostrarPanelSentenciaPersonalizada(CardLayout cardLayout, JPanel panel){
        cardLayout.show(panel, "SentenciaPersonalizada");}

}
