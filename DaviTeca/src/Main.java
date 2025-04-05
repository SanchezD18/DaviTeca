import javax.swing.*;
import java.sql.*;

public class Main {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }

    public static Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:DaviTeca.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos");
            e.printStackTrace();
        }
        return conn;
    }

    public static void disconnect(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}


