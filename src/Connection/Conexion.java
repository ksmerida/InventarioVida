/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Connection;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author kylie
 */
public class Conexion {
    Connection conect = null;
    String user = "root";
    String pass = "";
    String url = "jdbc:mysql://127.0.0.1:3306/inventario?useSSL=false&serverTimezone=UTC";

    public Connection Conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conect = DriverManager.getConnection(url, user, pass);
            System.out.println("Conexión exitosa!");
        } catch (Exception e) {
            System.out.println("Error De Conexion");
            e.printStackTrace();
        }
        return conect;
    }

    public java.sql.Connection desconected() {
        conect = null;
        if (conect != null) {
            System.out.println("Error Al Desconectar");
        }
        return conect;
    }
}
