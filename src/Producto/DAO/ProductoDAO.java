package Producto.DAO;

import Producto.model.Producto;
import Connection.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public boolean create(Producto p) {
        String sql = "INSERT INTO producto (nombre, sku, descripcion, precio, cantidad, id_sede) VALUES (?,?,?,?,?,?)";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getSku());
            ps.setString(3, p.getDescripcion());
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getCantidad());
            if (p.getIdSede() != null) ps.setInt(6, p.getIdSede());
            else ps.setNull(6, Types.INTEGER);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) p.setId(rs.getInt(1));
                }
            }
            return rows > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    public boolean update(Producto p) {
        String sql = "UPDATE producto SET nombre=?, sku=?, descripcion=?, precio=?, cantidad=?, id_sede=? WHERE id_producto=?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getSku());
            ps.setString(3, p.getDescripcion());
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getCantidad());
            if (p.getIdSede() != null) ps.setInt(6, p.getIdSede());
            else ps.setNull(6, Types.INTEGER);
            ps.setInt(7, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM producto WHERE id_producto=?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    public Producto findById(int id) {
        String sql = "SELECT * FROM producto WHERE id_producto=?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    public List<Producto> findAll() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto ORDER BY nombre";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }

    public List<Producto> searchByName(String q) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE nombre LIKE ? ORDER BY nombre";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + q + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }

    private Producto map(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id_producto"));
        p.setNombre(rs.getString("nombre"));
        p.setSku(rs.getString("sku"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecio(rs.getDouble("precio"));
        p.setCantidad(rs.getInt("cantidad"));
        int sid = rs.getInt("id_sede");
        if (rs.wasNull()) p.setIdSede(null); else p.setIdSede(sid);
        return p;
    }
}
