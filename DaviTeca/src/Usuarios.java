import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Usuarios {
    public static void menu(String usuario, String contrasena){
        boolean seguir = true;
        Scanner inputValue = new Scanner(System.in);
        while(seguir){
            System.out.println("""
                        Selecciona una opción:
                        \
                        1. Registrar Usuario.
                        \
                        2. Listar todos los datos de los usuarios.
                        \
                        3. Cambiar contraseña.
                        \
                        4. Actualizar algún dato.
                        \
                        5. Eliminar usuario por ID. (admin)
                        \
                        6. Sentencia personalizada. (admin)
                        \
                        0. Salir\s""");
            int eleccion = inputValue.nextInt();
            inputValue.nextLine();
            switch(eleccion){
                case 0, 1 -> seguir = false;
                case 2 -> Usuarios.consultarTodo(usuario);
                case 3 -> Usuarios.cambiarContrasena(usuario, contrasena);
                case 4 -> Usuarios.actualizarDatos(usuario);
                case 5 -> Usuarios.eliminar(usuario);
                case 6 -> Usuarios.sentenciaPersonalizada(usuario);
                default -> System.out.println("Opción no válida.");}}}



    public static void registrar(String usuario, String contrasena, String email,
                                 String DNI, String nombre, String apellido, String telefono, Component esto){
        Connection conn = Main.connect();
        email = validarEmail(email,esto);
        DNI = validarDNI(DNI,esto);
        int telefonoInt = Integer.parseInt(telefono);
        telefonoInt = validarTelefono(telefonoInt,esto);

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
            pstmt2.setInt(4, telefonoInt);
            pstmt2.setInt(5, id_usuario);


            int rowsInserted2 = pstmt2.executeUpdate();
            if (rowsInserted > 0 && rowsInserted2 > 0) {
                System.out.println("Usuario insertado exitosamente.");
                System.out.println();}

        Main.disconnect(conn);}
        catch (Exception e) {System.out.println("Error al insertar usuario y cliente: " + e.getMessage());}
        finally {
            try {if (pstmt != null) pstmt.close();  if (pstmt2!= null) pstmt2.close();  if (stmt != null) stmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}



    public static void consultarTodo(String usuario) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = Main.connect();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Usuarios INNER JOIN Clientes ON Usuarios.id_usuario = Clientes.id_usuario");

            while (rs.next()) {
                System.out.println("ID Usuario: " + rs.getInt("id_usuario") + '\n'
                        + "Nombre de Usuario: " + rs.getString("usuario") + '\n'
                        + "Contraseña: " + (Objects.equals(usuario, "admin")?rs.getString("contrasena"):"*********") + '\n'
                        + "Contacto: " + rs.getString("email") + '\n'
                        + "DNI: " + (Objects.equals(usuario, "admin")?rs.getString("dni"):"**********") + '\n'
                        + "Nombre: " + rs.getString("nombre") + '\n'
                        + "Apellido: " + rs.getString("apellido") + '\n'
                        + "Teléfono: " + (Objects.equals(usuario, "admin")?rs.getString("telefono"):"**********"));
                System.out.println();}
        Main.disconnect(conn);
        }
        catch (Exception e) {System.out.println(e.getMessage());}
        finally {
            try {if (rs != null) rs.close();    if (stmt != null) stmt.close();}
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


    public static void actualizarDatos(String usuario){
        Scanner inputValue = new Scanner(System.in);
        Connection conn = Main.connect();
        consultarTodo(usuario);
        int id_usuario = obtenerIDUsuario(usuario);
        PreparedStatement pstmt = null;
        try {
            System.out.println("""
                ¿Qué quieres actualizar?
                \
                1. DNI
                \
                2. Nombre
                \
                3. Apellido
                \
                4. Telefono\s
                """);

            int eleccion = inputValue.nextInt();
            inputValue.nextLine();
            String columna;
            String nuevoValor;
            switch (eleccion) {
                case 1 -> {
                    columna = "dni";
                    System.out.print("Ingrese el nuevo dni: ");}
                case 2 -> {
                    columna = "nombre";
                    System.out.print("Ingrese el nuevo nombre: ");}
                case 3 -> {
                    columna = "apellido";
                    System.out.print("Ingrese tu nuevo apellido: ");}
                case 4 -> {
                    columna = "telefono";
                    System.out.print("Ingrese su nuevo teléfono: ");}
                default -> {
                    System.out.println("Opción no válida.");
                    return;}}
            nuevoValor = inputValue.nextLine();
            String sql = "UPDATE Clientes SET " + columna + " = ? WHERE id_usuario = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nuevoValor);
            pstmt.setInt(2, id_usuario);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Dato actualizados exitosamente.");}
            else {System.out.println("Error al actualizar tus datos.");}
            Main.disconnect(conn);
        }
        catch (Exception e) {System.out.println("Error al actualizar tus datos: " + e.getMessage());}
        finally {
            try {if (pstmt != null) pstmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}
    }



public static void cambiarContrasena(String usuario, String contrasena){
    Scanner inputValue = new Scanner(System.in);
    PreparedStatement pstmt = null;
    Connection conn = Main.connect();
    try {
        System.out.println("Introduce tu contraseña anterior: ");
        String contrasenaComprobar = inputValue.nextLine();

        if (contrasena.equals(contrasenaComprobar)){
            System.out.println("Introduce la nueva contraseña: ");
            String nuevoValor = inputValue.nextLine();
            String sql = "UPDATE Usuarios SET contrasena = ? WHERE usuario =\"" + usuario + "\"";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nuevoValor);

        int rowsUpdated = pstmt.executeUpdate();
        if (rowsUpdated > 0) {System.out.println("Contraseña actualizada correctamente.");}
        else {System.out.println("La contraseña anterior es errónea.");}}
    Main.disconnect(conn);
    }

    catch (Exception e) {System.out.println("Error al actualizar la contraseña: " + e.getMessage());}
    finally {
        try {if (pstmt != null) pstmt.close();}
        catch (Exception ex) {System.out.println(ex.getMessage());}}}



    public static void eliminar(String usuario){
        Connection conn = Main.connect();
        Scanner inputValue = new Scanner(System.in);
        String eleccion;
        if (usuario.equals("admin")){
        consultarTodo(usuario);
        System.out.print("Ingrese el ID del usuario que desea eliminar: ");
        int id = inputValue.nextInt();
        inputValue.nextLine();
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        try {
            String sql = "DELETE FROM Usuarios WHERE id_usuario = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            System.out.println("Quieres eliminar también los datos de cliente?  S/N ");
            eleccion = inputValue.nextLine();
            if (eleccion.equalsIgnoreCase("s")){
                String sql2 = "DELETE FROM Clientes WHERE id_usuario = ?";
                pstmt2 = conn.prepareStatement(sql2);
                pstmt2.setInt(1, id);
                pstmt2.executeUpdate();}
            else{System.out.println("De acuerdo.");}

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {System.out.println("Usuario eliminado exitosamente.");}
            else {System.out.println("No se encontró un usuario con el ID proporcionado.");}

        Main.disconnect(conn);}

        catch (Exception e) {System.out.println("Error al eliminar usuario: " + e.getMessage());}
        finally {
            try {if (pstmt != null && pstmt2!= null){ pstmt.close();  pstmt2.close();}}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}
        else System.out.println("No tienes permisos suficientes.");}



        public static void sentenciaPersonalizada(String usuario){
            Connection conn = Main.connect();
            Statement stmt = null;
            ResultSet rs = null;
            Scanner inputValue = new Scanner(System.in);
            try {
                if (usuario.equals("admin")){
                    System.out.println("Introduce la sentencia a realizar: ");
                String sentencia = inputValue.nextLine();
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


public static String validarDNI(String dni, Component esto){
    String regex = "^[a-zA-Z0-9]{9,10}$";
    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(dni);
    boolean matchFound = matcher.find();
    while (!matchFound){
        if(matchFound) {matchFound = true;}
        else {
            JOptionPane.showMessageDialog(esto,"Error en el DNI (formato 00000000X)");
            break;
        }}
    return dni;}


    public static String validarEmail(String email, Component esto){
        String regex = "^[a-z]+[0-9]{0,2}@[a-z]+\\.[a-z]{2,3}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        boolean matchFound = matcher.find();
        while (!matchFound){
            if(matchFound) {matchFound = true;}
            else {JOptionPane.showMessageDialog(esto, "Error en el email. (formato algo@ejemplo.com)");
                break;
            }}
        return email;}


    public static int validarTelefono(int telefono, Component esto){
        boolean correcto = false;
        while(!correcto){
            if (telefono < 600000000 || telefono > 799999999){
           JOptionPane.showMessageDialog(esto, "Telefono no válido. (formato 600000000");
            break;}
            else {correcto = true;}}
        return telefono;}


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
}
