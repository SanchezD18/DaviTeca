import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Scanner;

public class Libros{

    public static void mostrarTabla(DefaultTableModel modelo) {
        Connection conn = Main.connect();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM libros");
            ResultSetMetaData metaData = rs.getMetaData();
            int columnas = metaData.getColumnCount();
            for (int i = 1; i <= columnas; i++){modelo.addColumn(metaData.getColumnName(i));}
            while (rs.next()) {
                Object[] filas = new Object[columnas];
                for (int i = 1; i <= columnas; i++){
                    if (i == 5){
                        if (rs.getObject(i).equals(1)){
                            filas[i-1] = "✅";
                        }else if (rs.getObject(i).equals(0)){filas[i-1] = "❌";
                    }}else filas[i-1] = rs.getObject(i);}
                modelo.addRow(filas);}

            Main.disconnect(conn);}
        catch (Exception e) {System.out.println(e.getMessage());}
        finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}



    public static void insertarInterfaz(JPanel panel, String titulo, String autor, String editorial){
        Connection conn = Main.connect();
        Scanner inputValue = new Scanner(System.in);

        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO Libros (titulo, autor, editorial) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, titulo);
            pstmt.setString(2, autor);
            pstmt.setString(3, editorial);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(panel,"Libro insertado exitosamente.");}
            Main.disconnect(conn);}
        catch (Exception e) {System.out.println("Error al insertar libro: " + e.getMessage());}
        finally {
            try {if (pstmt != null) pstmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}


    public static void actualizarInterfaz(){
        Scanner inputValue = new Scanner(System.in);
        Connection conn = Main.connect();
        int id = inputValue.nextInt();
        inputValue.nextLine();
        PreparedStatement pstmt = null;
        try {
            System.out.println("""
                ¿Qué quieres actualizar?
                \
                1. Título
                \
                2. Autor
                \
                3. Editorial
                \
                4. Disponibilidad\s
                """);

            int eleccion = inputValue.nextInt();
            inputValue.nextLine();
            String columna;
            String nuevoValor;
            switch (eleccion) {
                case 1 -> {
                    columna = "titulo";
                    System.out.print("Ingrese el nuevo título: ");}
                case 2 -> {
                    columna = "autor";
                    System.out.print("Ingrese el nuevo autor: ");}
                case 3 -> {
                    columna = "editorial";
                    System.out.print("Ingrese la nueva editorial: ");}
                case 4 -> {
                    columna = "disponible";
                    System.out.print("Ingrese la disponibilidad: ( 0.No disponible 1.Disponible ) ");}
                default -> {
                    System.out.println("Opción no válida.");
                    return;}}
            nuevoValor = inputValue.nextLine();
            String sql = "UPDATE libros SET " + columna + " = ? WHERE id_libro = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nuevoValor);
            pstmt.setInt(2, id);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Libro actualizado exitosamente.");}
            else {System.out.println("No se encontró un libro con el ID proporcionado.");}
            Main.disconnect(conn);
        }
        catch (Exception e) {System.out.println("Error al actualizar libro: " + e.getMessage());}
        finally {
            try {if (pstmt != null) pstmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}


    public static void eliminar(String id, Component esto){
        Connection conn = Main.connect();
        Scanner inputValue = new Scanner(System.in);

        int numId = Integer.parseInt(id);
        PreparedStatement pstmt = null;

        try {
            String sql = "DELETE FROM Libros WHERE id_libro = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, numId);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {JOptionPane.showMessageDialog(esto, "Libro eliminado exitosamente.");}
            else {System.out.println("No se encontró un libro con el ID proporcionado.");}}
        catch (Exception e) {System.out.println("Error al eliminar libro: " + e.getMessage());}
        finally {
            try {if (pstmt != null) pstmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}



    public static ResultSetMetaData consultarPorAlgo(String id){
        Connection conn = Main.connect();
        Statement stmt = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        ResultSetMetaData metaDataError = null;
        try {
            int numID = Integer.parseInt(id);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Libros WHERE id_libro = " + numID);
            ResultSetMetaData metaData = rs.getMetaData();
            Main.disconnect(conn);
            return metaData;}
        catch (Exception e) {System.out.println(e.getMessage());}
        finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (pStmt != null) pStmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}
    return metaDataError;}



    public static void menu(){
        boolean seguir = true;
        Scanner inputValue = new Scanner(System.in);
        while(seguir){
            System.out.println("""
                        Selecciona una opción:
                        \
                        1. Insertar libro.
                        \
                        2. Listar todos los libros.
                        \
                        3. Actualizar libro.
                        \
                        4. Eliminar libro por ID.
                        \
                        5. Consultar libro por...
                        \
                        0. Salir\s""");
            int eleccion = inputValue.nextInt();
            inputValue.nextLine();
            switch(eleccion){
                case 0 -> seguir = false;
                default -> System.out.println("Opción no válida. ");}}}


    public static JPanel crearPanelInsertar(){
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 253, 208));
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createRigidArea(new Dimension(200, 100)));
        JTextField campoTitulo = new JTextField(10);
        campoTitulo.setPreferredSize(new Dimension(200,40));
        campoTitulo.setMaximumSize(new Dimension(200,40));

        JTextField campoAutor = new JTextField(10);
        campoAutor.setPreferredSize(new Dimension(200,40));
        campoAutor.setMaximumSize(new Dimension(200,40));

        JTextField campoEditorial = new JTextField(10);
        campoEditorial.setPreferredSize(new Dimension(200,40));
        campoEditorial.setMaximumSize(new Dimension(200,40));


        JButton btnInsertar = new JButton("Insertar");
        btnInsertar.setPreferredSize(new Dimension(90,30));
        btnInsertar.setMaximumSize(new Dimension(90,30));
        btnInsertar.setFont(buttonFont);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(90, 30));
        btnCancelar.setMaximumSize(new Dimension(90,30 ));
        btnCancelar.setFont(buttonFont);

        panel.add(new JLabel("Título:"));
        panel.add(campoTitulo);
        panel.add(Box.createRigidArea(new Dimension(100, 40)));
        panel.add(new JLabel("Autor:"));
        panel.add(campoAutor);
        panel.add(Box.createRigidArea(new Dimension(100, 40)));
        panel.add(new JLabel("Editorial:"));
        panel.add(campoEditorial);
        panel.add(Box.createRigidArea(new Dimension(100, 100)));

        JPanel panelDebajo = new JPanel();
        panelDebajo.setLayout(new BoxLayout(panelDebajo,BoxLayout.X_AXIS));
        panelDebajo.add(btnInsertar);
        panelDebajo.add(btnCancelar);
        panel.add(panelDebajo);

        btnInsertar.addActionListener(e ->{
            insertarInterfaz(panel, campoTitulo.getText(), campoAutor.getText(), campoEditorial.getText());
            campoAutor.setText("");
            campoTitulo.setText("");
            campoEditorial.setText("");
        });
//        btnCancelar.addActionListener(e -> );

        return panel;}

    public static JPanel crearPanelActualizar(){
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

        JTextField campoTitulo = new JTextField(10);
        campoTitulo.setPreferredSize(new Dimension(200,40));
        campoTitulo.setMaximumSize(new Dimension(200,40));

        JTextField campoAutor = new JTextField(10);
        campoAutor.setPreferredSize(new Dimension(200,40));
        campoAutor.setMaximumSize(new Dimension(200,40));

        JTextField campoEditorial = new JTextField(10);
        campoEditorial.setPreferredSize(new Dimension(200,40));
        campoEditorial.setMaximumSize(new Dimension(200,40));

        JTextField campoDisponible = new JTextField(10);
        campoDisponible.setPreferredSize(new Dimension(200,40));
        campoDisponible.setMaximumSize(new Dimension(200,40));

        JButton btnInsertar = new JButton("Insertar");
        btnInsertar.setPreferredSize(new Dimension(90,30));
        btnInsertar.setMaximumSize(new Dimension(90,30));
        btnInsertar.setFont(buttonFont);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(90, 30));
        btnCancelar.setMaximumSize(new Dimension(90,30 ));
        btnCancelar.setFont(buttonFont);

        JButton btnRecopilar = new JButton("Recopilar");
        btnRecopilar.setPreferredSize(new Dimension(90, 30));
        btnRecopilar.setMaximumSize(new Dimension(90,30 ));
        btnRecopilar.setFont(buttonFont);
        btnRecopilar.addActionListener(e ->Libros.consultarPorAlgo(campoID.getText()));

        panel.add(new JLabel("ID:"));
        panel.add(campoID);
        panel.add(btnRecopilar);
        panel.add(Box.createRigidArea(new Dimension(200, 40)));
        panel.add(new JLabel("Título:"));
        panel.add(campoTitulo);
        panel.add(Box.createRigidArea(new Dimension(200, 40)));
        panel.add(new JLabel("Autor:"));
        panel.add(campoAutor);
        panel.add(Box.createRigidArea(new Dimension(200, 40)));
        panel.add(new JLabel("Editorial:"));
        panel.add(campoEditorial);
        panel.add(Box.createRigidArea(new Dimension(200, 40)));
        panel.add(new JLabel("Disponible:"));
        panel.add(campoDisponible);
        panel.add(Box.createRigidArea(new Dimension(200, 100)));

        JPanel panelDebajo = new JPanel();
        panelDebajo.setLayout(new BoxLayout(panelDebajo,BoxLayout.X_AXIS));
        panelDebajo.add(btnInsertar);
        panelDebajo.add(btnCancelar);
        panel.add(panelDebajo);

        btnInsertar.addActionListener(e ->{
            Libros.insertarInterfaz(panel, campoTitulo.getText(), campoAutor.getText(), campoEditorial.getText());
            campoAutor.setText("");
            campoTitulo.setText("");
            campoEditorial.setText("");
        });
        btnInsertar.addActionListener(e -> panel.setVisible(false));
        btnRecopilar.addActionListener(e -> panel.setVisible(false));

        return panel;}


    public static JPanel crearPanelEliminar(){
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 253, 208));

        return panel;}


    public static JPanel crearPanelConsultarPor(){
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 253, 208));

        return panel;}


    public static void mostrarPanelInsertarLibro(CardLayout cardLayout, JPanel panel){
        cardLayout.show(panel, "InsertarLibro");}

    public static void mostrarPanelActualizarLibro(CardLayout cardLayout, JPanel panel){
        cardLayout.show(panel,"ActualizarLibro");}

    public static void mostrarPanelEliminarLibro(CardLayout cardLayout, JPanel panel){
        cardLayout.show(panel,"EliminarLibro");}

    public static void mostrarPanelConsultarPor(CardLayout cardLayout, JPanel panel){
        cardLayout.show(panel, "ConsultarPorLibro");}



}
