package Sede.DAO;

import Sede.model.Sede;
import Connection.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SedeDAO {

    public boolean create(Sede s) {
        String sql = "INSERT INTO sede (nombre_sede, direccion, estado) VALUES (?,?,?)";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDireccion());
            ps.setInt(2, s.getEstado());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) s.setId(rs.getInt(1));
                }
            }
            return rows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean update(Sede s) {
        String sql = "UPDATE sede SET nombre_sede=?, direccion=?, estado=? WHERE id_sede=?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDireccion());
            ps.setInt(2, s.getEstado());
            ps.setInt(5, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM sede WHERE id_sede=?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Sede findById(int id) {
        String sql = "SELECT * FROM sede WHERE id_sede=?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    public List<Sede> findAll() {
        List<Sede> lista = new ArrayList<>();
        String sql = "SELECT * FROM sede ORDER BY nombre_sede";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }

    public List<Sede> searchByName(String q) {
        List<Sede> lista = new ArrayList<>();
        String sql = "SELECT * FROM sede WHERE nombre_sede LIKE ? ORDER BY nombre_sede";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + q + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }

    private Sede map(ResultSet rs) throws SQLException {
        Sede s = new Sede();
        s.setId(rs.getInt("id_sede"));
        s.setNombre(rs.getString("nombre_sede"));
        s.setDireccion(rs.getString("direccion"));
        
        return s;
    }
}
