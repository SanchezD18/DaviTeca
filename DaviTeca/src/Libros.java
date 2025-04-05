import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Libros{

    public static void mostrarTabla(DefaultTableModel modelo) {
        Connection conn = Main.connect();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            modelo.setRowCount(0);
            modelo.setColumnCount(0);
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


    public static void actualizarInterfaz(JPanel panel, String id, String titulo, String autor, String editorial, String disponible){

        Connection conn = Main.connect();
        PreparedStatement pstmt = null;
        try {
            int idLibro = Integer.parseInt(id);
            int disponibleInt = ("1".equals(disponible) || "✅".equals(disponible)) ? 1 : 0;
            String sql = "UPDATE libros SET titulo = ?, autor = ?, editorial = ?, disponible = ? WHERE id_libro = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, titulo);
            pstmt.setString(2, autor);
            pstmt.setString(3, editorial);
            pstmt.setInt(4, disponibleInt);
            pstmt.setInt(5, idLibro);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(panel, "¡Libro actualizado!👌", "Éxito", JOptionPane.INFORMATION_MESSAGE);}
            else {JOptionPane.showMessageDialog(panel, "Error: ID no encontrado", "Error", JOptionPane.ERROR_MESSAGE);}
            Main.disconnect(conn);
        }
        catch (Exception e) {System.out.println("Error al actualizar libro: " + e.getMessage());}
        finally {
            try {if (pstmt != null) pstmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}

    public static void mostrarTablaConsulta(String sql){
        JDialog dialog = new JDialog();
        dialog.setTitle("Resultados de la búsqueda:");
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        Toolkit mipantalla= Toolkit.getDefaultToolkit();
        Image icono = mipantalla.getImage("libro.png");
        dialog.setIconImage(icono);
        dialog.setResizable(false);

        DefaultTableModel modelo = new DefaultTableModel();
        JTable tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);

        try {
            Connection conn = Main.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                modelo.addColumn(metaData.getColumnName(i));
            }

            while (rs.next()) {
                Object[] fila = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    if (i == 5) {
                        fila[i-1] = rs.getInt(i) == 1 ? "✅" : "❌";
                    } else {
                        fila[i-1] = rs.getObject(i);
                    }
                }
                modelo.addRow(fila);
            }

            for (int i = 0; i < tabla.getColumnCount(); i++) {
                tabla.getColumnModel().getColumn(i).setPreferredWidth(150);
            }
            Main.disconnect(conn);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Error al ejecutar la consulta: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    public static void eliminarInterfaz(String id, Component esto){
        Connection conn = Main.connect();
        int numId = Integer.parseInt(id);
        PreparedStatement pstmt = null;

        try {
            String sql = "DELETE FROM Libros WHERE id_libro = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, numId);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {JOptionPane.showMessageDialog(esto, "Libro eliminado exitosamente.");}
            else {JOptionPane.showMessageDialog(esto,"No se encontró un libro con el ID proporcionado.");}}
        catch (Exception e) {JOptionPane.showMessageDialog(esto, "Error al eliminar libro: " + e.getMessage());}
        finally {
            try {if (pstmt != null) pstmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}}



    public static String[] consultarPorId(String id){
        Connection conn = Main.connect();
        Statement stmt = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String[] datosLibro = new String[5];
        try {
            int numID = Integer.parseInt(id);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Libros WHERE id_libro = " + numID);
            if (rs.next()){
                datosLibro[0] = rs.getString("id_libro");
                datosLibro[1] = rs.getString("titulo");
                datosLibro[2] = rs.getString("autor");
                datosLibro[3] = rs.getString("editorial");
                datosLibro[4] = rs.getInt("disponible") == 1 ? "Disponible ✅" : "No disponible ❌";

            }
            Main.disconnect(conn);
            return datosLibro;}
        catch (Exception e) {System.out.println(e.getMessage());}
        finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (pStmt != null) pStmt.close();}
            catch (Exception ex) {System.out.println(ex.getMessage());}}
    return datosLibro;}


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
        btnCancelar.addActionListener(e -> panel.setVisible(false));

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

        JComboBox<String> comboDisponible = new JComboBox<>(new String[]{"Disponible ✅", "No disponible ❌"});
        comboDisponible.setPreferredSize(new Dimension(200, 40));
        comboDisponible.setMaximumSize(new Dimension(200, 40));


        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setPreferredSize(new Dimension(90,30));
        btnActualizar.setMaximumSize(new Dimension(90,30));
        btnActualizar.setFont(buttonFont);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(90, 30));
        btnCancelar.setMaximumSize(new Dimension(90,30 ));
        btnCancelar.setFont(buttonFont);

        JButton btnRecopilar = new JButton("Recopilar");
        btnRecopilar.setPreferredSize(new Dimension(90, 30));
        btnRecopilar.setMaximumSize(new Dimension(90,30 ));
        btnRecopilar.setFont(buttonFont);

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
        panel.add(comboDisponible);
        panel.add(Box.createRigidArea(new Dimension(200, 100)));

        JPanel panelDebajo = new JPanel();
        panelDebajo.setLayout(new BoxLayout(panelDebajo,BoxLayout.X_AXIS));
        panelDebajo.add(btnActualizar);
        panelDebajo.add(btnCancelar);
        panel.add(panelDebajo);
        btnRecopilar.addActionListener(e -> {
            String id = campoID.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Introduce un ID de un libro válido.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String[] datosLibro = consultarPorId(id);
            if (datosLibro[0] != null) {
                campoTitulo.setText(datosLibro[1]);
                campoAutor.setText(datosLibro[2]);
                campoEditorial.setText(datosLibro[3]);
                comboDisponible.setSelectedItem(datosLibro[4]);
            }
        });

        btnActualizar.addActionListener(e ->{
            String id = campoID.getText().trim();
            String titulo = campoTitulo.getText().trim();
            String autor = campoAutor.getText().trim();
            String editorial = campoEditorial.getText().trim();
            String disponible = comboDisponible.getSelectedItem().toString().contains("✅") ? "1" : "0";
            if (id.isEmpty() || titulo.isEmpty() || autor.isEmpty() || editorial.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "¡Todos los campos son obligatorios!", "Error", JOptionPane.WARNING_MESSAGE);
                return;}
                Libros.actualizarInterfaz(panel,id,titulo,autor,editorial,disponible);
                campoID.setText("");
                campoAutor.setText("");
                campoTitulo.setText("");
                campoEditorial.setText("");
                panel.setVisible(false);
        });
        btnCancelar.addActionListener(e -> panel.setVisible(false));
        return panel;}


    public static JPanel crearPanelEliminar(){
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
            Libros.eliminarInterfaz(campoID.getText(), panel);
            campoID.setText("");
            panel.setVisible(false);
        });
        btnCancelar.addActionListener(e -> panel.setVisible(false));
        return panel;}


    public static JPanel crearPanelConsultarPor(){
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

        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.setPreferredSize(new Dimension(90,30));
        btnConsultar.setMaximumSize(new Dimension(90,30));
        btnConsultar.setFont(buttonFont);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(90, 30));
        btnCancelar.setMaximumSize(new Dimension(90,30 ));
        btnCancelar.setFont(buttonFont);

        ButtonGroup grupoDisponibilidad = new ButtonGroup();
        JRadioButton radioAmbas = new JRadioButton("Ambas", true);
        JRadioButton radioDisponible = new JRadioButton("Disponible");
        JRadioButton radioNoDisponible = new JRadioButton("No disponible");
        grupoDisponibilidad.add(radioAmbas);
        grupoDisponibilidad.add(radioDisponible);
        grupoDisponibilidad.add(radioNoDisponible);
        radioAmbas.setBackground(new Color(255, 253, 208));
        radioDisponible.setBackground(new Color(255, 253, 208));
        radioNoDisponible.setBackground(new Color(255, 253, 208));


        panel.add(new JLabel("Título:"));
        panel.add(campoTitulo);
        panel.add(Box.createRigidArea(new Dimension(200, 40)));
        panel.add(new JLabel("Autor:"));
        panel.add(campoAutor);
        panel.add(Box.createRigidArea(new Dimension(200, 40)));
        panel.add(new JLabel("Editorial:"));
        panel.add(campoEditorial);
        panel.add(Box.createRigidArea(new Dimension(200, 40)));
        panel.add(new JLabel("Disponibilidad:"));
        panel.add(radioAmbas);
        panel.add(radioDisponible);
        panel.add(radioNoDisponible);
        panel.add(Box.createRigidArea(new Dimension(200, 40)));


        JPanel panelDebajo = new JPanel();
        panelDebajo.setLayout(new BoxLayout(panelDebajo,BoxLayout.X_AXIS));
        panelDebajo.add(btnConsultar);
        panelDebajo.add(btnCancelar);
        panel.add(panelDebajo);

        btnConsultar.addActionListener(e ->{
            StringBuilder sql = new StringBuilder("SELECT * FROM libros WHERE 1=1");
            if (!campoTitulo.getText().isEmpty()) {sql.append(" AND titulo LIKE '%").append(campoTitulo.getText()).append("%'");}
            if (!campoAutor.getText().isEmpty()) {sql.append(" AND autor LIKE '%").append(campoAutor.getText()).append("%'");}
            if (!campoEditorial.getText().isEmpty()) {sql.append(" AND editorial LIKE '%").append(campoEditorial.getText()).append("%'");}
            if (radioDisponible.isSelected()) {sql.append(" AND disponible = 1");}
            else if (radioNoDisponible.isSelected()) {sql.append(" AND disponible = 0");}

            mostrarTablaConsulta(sql.toString());
        });

        btnCancelar.addActionListener(e -> panel.setVisible(false));
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
