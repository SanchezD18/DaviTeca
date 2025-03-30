import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

public class Prestamos {
    public static void menu(String usuario, String contrasena){
        boolean seguir = true;
        Scanner inputValue = new Scanner(System.in);
        while(seguir){
            System.out.println("""
                        Selecciona una opción:
                        \
                        1. Pedir un libro prestado.
                        \
                        2. Listar historial de prestamos.
                        \
                        3. Realizar una devolución.
                        \
                        0. Salir\s""");
            int eleccion = inputValue.nextInt();
            inputValue.nextLine();
            switch(eleccion){
                case 0 -> seguir = false;
                case 1 -> Prestamos.pedirPrestado(usuario);
                case 2 -> Prestamos.consultarTusPrestamos(usuario);
                case 3 -> Prestamos.devolucion(usuario);
                default -> System.out.println("Opción no válida.");}}}

    public static void pedirPrestado(String usuario){
        Connection conn = Main.connect();
        Scanner inputValue = new Scanner(System.in);
        System.out.print("Ingrese el ID del libro que desea: ");
        int id_libro = inputValue.nextInt();
        inputValue.nextLine();

        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        Statement stmt = null;
        try {
            LocalDate fecha_prestamosLD = LocalDate.now();
            String fecha_prestamos = String.valueOf(fecha_prestamosLD);

            LocalDate fecha_devolucionLD = fecha_prestamosLD.plusDays(7);
            String fecha_devolucion = String.valueOf(fecha_devolucionLD);

            String sql3 = "SELECT id_usuario FROM Usuarios WHERE usuario = \"" + usuario + "\"";
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql3);
            int id_usuario = rs.getInt("id_usuario");

            String sql2 = "UPDATE libros SET disponible = 0 WHERE id_libro = ?";
            pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setInt(1, id_libro);
            pstmt2.executeUpdate();

            String sql = "INSERT INTO Prestamos (fecha_prestamo, fecha_devolucion, id_usuario, id_libro) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fecha_prestamos);
            pstmt.setString(2, fecha_devolucion);
            pstmt.setInt(3, id_usuario);
            pstmt.setInt(4, id_libro);
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Prestamo realizado.");
                System.out.println();}
            Main.disconnect(conn);}
        catch (Exception e) {System.out.println("Error al realizar el prestamo: " + e.getMessage());}
        finally {
            try {if (pstmt != null) pstmt.close();  if (stmt != null) stmt.close();    if (pstmt2 != null) pstmt2.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}

    public static void devolucion(String usuario){
        consultarTusPrestamos(usuario);
        Connection conn = Main.connect();
        Scanner inputValue = new Scanner(System.in);
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        Statement stmt4 = null;

        try {
            System.out.println("Introduce el ID del prestamos a devolver: ");
            int id_prestamo = inputValue.nextInt();
            inputValue.nextLine();

            LocalDate fecha_devolucionLD = LocalDate.now();
            String fecha_devolucion = String.valueOf(fecha_devolucionLD);

            String sql4 = "SELECT id_libro FROM Prestamos WHERE id_prestamo = " + id_prestamo;
            stmt4 = conn.createStatement();
            ResultSet rs = stmt4.executeQuery(sql4);
            int id_libro = rs.getInt("id_libro");

            String sql2 = "UPDATE libros SET disponible = 1 WHERE id_libro = ?";
            pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setInt(1, id_libro);
            pstmt2.executeUpdate();

            String sql = "UPDATE Prestamos SET fecha_devolucion = ? WHERE id_prestamo = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fecha_devolucion);
            pstmt.setInt(2, id_prestamo);



                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {System.out.println("Libro devuelto correctamente.");}
                else {System.out.println("Error al devolver el libro..");}
            Main.disconnect(conn);
        }

        catch (Exception e) {System.out.println("Error al actualizar la contraseña: " + e.getMessage());}
        finally {
            try {if (pstmt != null) pstmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}


    public static void consultarTusPrestamos(String usuario) {
        Connection conn = Main.connect();
        Statement stmt = null;
        Statement stmt2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;


        try {

            String sql2 = "SELECT id_usuario FROM Usuarios WHERE usuario = \"" + usuario + "\"";
            stmt2 = conn.createStatement();
            rs2 = stmt2.executeQuery(sql2);
            int id_usuario = rs2.getInt("id_usuario");

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT p.id_prestamo, p.fecha_prestamo, p.fecha_devolucion, l.titulo FROM Prestamos p join Libros l on l.id_libro = p.id_libro WHERE id_usuario =" + id_usuario );

            while (rs.next()) {
                System.out.println("ID Prestamo: " + rs.getInt("id_prestamo") + '\n'
                                + "Libro prestado: " + rs.getString("titulo") + '\n'
                                + "Fecha Prestamo: " + rs.getString("fecha_prestamo") + '\n'
                                + "Fecha Devolucion: " + rs.getString("fecha_devolucion"));
                System.out.println();}

            Main.disconnect(conn);}
        catch (Exception e) {System.out.println(e.getMessage());}
        finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}




}
