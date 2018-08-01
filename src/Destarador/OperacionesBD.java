/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Destarador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


/**
 *
 * @author alber
 */
public class OperacionesBD {
      
    public OperacionesBD () {}
 
    public String destarar(String pb,String cod, String num_c, String cod1, String num_c1, String cod_p, String num_p) throws ClassNotFoundException, SQLException {
        Connection connection;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection("jdbc:ucanaccess://bd.accdb");
        } catch (ClassNotFoundException | SQLException ex) {
            return "error";
        }
        
        int peso_bruto = Integer.parseInt(pb);
        int num_cajas = 0; 
        if(!num_c.isEmpty()) num_cajas = Integer.parseInt(num_c);
        int num_cajas1 = 0; 
        if(!num_c1.isEmpty()) num_cajas1 = Integer.parseInt(num_c1);
        int num_palets = 0; 
        if(!num_p.isEmpty()) num_palets = Integer.parseInt(num_p);
        Float peso_caja, peso_caja1, peso_palet;
        
        PreparedStatement s;
        ResultSet rs;
        
        // Obtengo peso de la caja de tipo 1
        if(!cod.isEmpty()) {
            s = connection.prepareStatement("select peso from Caja where id = ? ");
            s.setString(1, cod);
            s.execute();
            rs = s.getResultSet();
            if(rs.next()) peso_caja = Float.parseFloat(rs.getString("peso"));
            else return "";
        }
        else peso_caja = 0f;
        
        // Obtengo peso de la caja de tipo 2
        if(!cod1.isEmpty()) {
            s = connection.prepareStatement("select peso from Caja where id = ? ");
            s.setString(1, cod1);
            s.execute();
            rs = s.getResultSet();
            if(rs.next()) peso_caja1 = Float.parseFloat(rs.getString("peso"));
            else return "";
        }
        else peso_caja1 = 0f;
        
        // Obtengo peso del palet
        if(!cod_p.isEmpty()) {
            s = connection.prepareStatement("select peso from Palet where id = ? ");
            s.setString(1, cod_p);
            s.execute();
            rs = s.getResultSet();
            if(rs.next()) peso_palet = Float.parseFloat(rs.getString("peso"));
            else return "";
        }
        else peso_palet = 0f;
            
        // Calculo peso neto si existe el palet y la caja
        Float peso_neto = peso_bruto - (peso_caja * num_cajas) - (peso_caja1 * num_cajas1) - (peso_palet * num_palets);
        long n = Math.round(peso_neto);
        return Long.toString(n);
    }
    
    public String addCaja(String n, String p, String r) throws ClassNotFoundException, SQLException {
        Connection connection;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection("jdbc:ucanaccess://bd.accdb");
        } catch (ClassNotFoundException | SQLException ex) {
            return "error";
        }
        
        Statement s = connection.createStatement();
        int rs = s.executeUpdate("INSERT INTO Caja values (NULL,'" + n + "'," + p + ",'" + r + "');");
        
        return "";
    }
    
    public String removeCaja(String c) throws ClassNotFoundException, SQLException {
        Connection connection;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection("jdbc:ucanaccess://bd.accdb");
        } catch (ClassNotFoundException | SQLException ex) {
            return "error";
        }
        
        PreparedStatement s = connection.prepareStatement("DELETE FROM Caja where id = ? ");
        s.setString(1, c);
        s.executeUpdate();
        
        return "";
    }
    
    public boolean[] existe_id(String cod,int opcion) throws ClassNotFoundException, SQLException {
        boolean [] b = new boolean[2];
        
        Connection connection = null;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection("jdbc:ucanaccess://bd.accdb");
            b[0] = true;
        } catch (ClassNotFoundException | SQLException ex) {
            b[0] = false;
            b[1] = false;
            return b;
        }
        
        String sentencia = "";
        if(opcion==1) sentencia = "select id from Caja where id = ? ";
        else sentencia = "select id from Palet where id = ? ";
        PreparedStatement s = connection.prepareStatement(sentencia);
        s.setString(1, cod);
        s.execute();
        ResultSet rs = s.getResultSet();
        b[1] = rs.next();
        
        return b;
    }
    
    public void modificarCaja(String dato, String cod, int opcion) throws ClassNotFoundException, SQLException {
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        Connection connection = DriverManager.getConnection("jdbc:ucanaccess://bd.accdb");
        
        String sentencia = "";
        switch (opcion) {
            case 1:
                sentencia = "UPDATE Caja SET nombre = ? WHERE id = ?";
                break;
            case 2:
                sentencia = "UPDATE Caja SET peso = ? WHERE id = ?";
                break;
            default:
                sentencia = "UPDATE Caja SET ruta = ? WHERE id = ?";
                break;
        }
        PreparedStatement s = connection.prepareStatement(sentencia);
        s.setString(1, dato);
        s.setString(2, cod);
        s.executeUpdate();
    }
    
    public ArrayList<String> showCaja(String c) throws ClassNotFoundException, SQLException {
        ArrayList<String> res = new ArrayList<String>();
        
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        Connection connection = DriverManager.getConnection("jdbc:ucanaccess://bd.accdb");
        
        PreparedStatement s = connection.prepareStatement("select * from Caja where id = ? ");
        s.setString(1, c);
        s.execute();
        ResultSet rs = s.getResultSet();
        if(rs.next()) {
            res.add(rs.getString("nombre"));
            res.add(rs.getString("peso"));
            res.add(rs.getString("ruta"));
        }
        return res;
    }
    
    public ArrayList<ArrayList<String>> obtenerCajas() throws SQLException, ClassNotFoundException {
        ArrayList<ArrayList<String>> a = new ArrayList<>();
        
        Connection connection;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection("jdbc:ucanaccess://bd.accdb");
        } catch (ClassNotFoundException | SQLException ex) {
            ArrayList<String> s = new ArrayList<String>();
            s.add("error");
            a.add(s);
            return a;
        }
        
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM Caja;");
        while(rs.next()) {
            ArrayList<String> tupla = new ArrayList<>();
            tupla.add(rs.getString("id"));
            tupla.add(rs.getString("nombre"));
            tupla.add(rs.getString("peso"));
            tupla.add(rs.getString("ruta"));
            
            a.add(tupla);
        }
        return a;
    }
    
    public String addPalet(String n, String p, String r) throws ClassNotFoundException, SQLException {
        Connection connection;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection("jdbc:ucanaccess://bd.accdb");
        } catch (ClassNotFoundException | SQLException ex) {
            return "error";
        }
        
        Statement s = connection.createStatement();
        int rs = s.executeUpdate("INSERT INTO Palet values (NULL,'" + n + "'," + p + ",'" + r + "');");
        
        return "";
    }
    
    public ArrayList<ArrayList<String>> obtenerPalets() throws SQLException, ClassNotFoundException {
        ArrayList<ArrayList<String>> a = new ArrayList<>();
        
        Connection connection;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection("jdbc:ucanaccess://bd.accdb");
        } catch (ClassNotFoundException | SQLException ex) {
            ArrayList<String> s = new ArrayList<String>();
            s.add("error");
            a.add(s);
            return a;
        }
        
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM Palet;");
        while(rs.next()) {
            ArrayList<String> tupla = new ArrayList<>();
            tupla.add(rs.getString("id"));
            tupla.add(rs.getString("nombre"));
            tupla.add(rs.getString("peso"));
            tupla.add(rs.getString("ruta"));
            
            a.add(tupla);
        }
        return a;
    }
    
    public ArrayList<String> showPalet(String c) throws ClassNotFoundException, SQLException {
        ArrayList<String> res = new ArrayList<String>();
        
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        Connection connection = DriverManager.getConnection("jdbc:ucanaccess://bd.accdb");
        
        PreparedStatement s = connection.prepareStatement("select * from Palet where id = ? ");
        s.setString(1, c);
        s.execute();
        ResultSet rs = s.getResultSet();
        if(rs.next()) {
            res.add(rs.getString("nombre"));
            res.add(rs.getString("peso"));
            res.add(rs.getString("ruta"));
        }
        return res;
    }
    
    public void modificarPalet(String dato, String cod, int opcion) throws ClassNotFoundException, SQLException {
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        Connection connection = DriverManager.getConnection("jdbc:ucanaccess://bd.accdb");
        
        String sentencia = "";
        switch (opcion) {
            case 1:
                sentencia = "UPDATE Palet SET nombre = ? WHERE id = ?";
                break;
            case 2:
                sentencia = "UPDATE Palet SET peso = ? WHERE id = ?";
                break;
            default:
                sentencia = "UPDATE Palet SET ruta = ? WHERE id = ?";
                break;
        }
        PreparedStatement s = connection.prepareStatement(sentencia);
        s.setString(1, dato);
        s.setString(2, cod);
        s.executeUpdate();
    }
    
    public String removePalet(String c) throws ClassNotFoundException, SQLException {
        Connection connection;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection("jdbc:ucanaccess://bd.accdb");
        } catch (ClassNotFoundException | SQLException ex) {
            return "error";
        }
        
        PreparedStatement s = connection.prepareStatement("DELETE FROM Palet where id = ? ");
        s.setString(1, c);
        s.executeUpdate();
        
        return "";
    }
}
