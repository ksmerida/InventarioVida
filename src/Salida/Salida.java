/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Salida;

import Connection.Conexion;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.*;
import javax.swing.text.*;
import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author dc10a
 */

public class Salida extends javax.swing.JInternalFrame {


    /**
     * Creates new form Salida
     */
    public Salida() {
        initComponents();
        // Configurar SPINNER de cantidad
        SpinnerNumberModel modeloSpinner = new SpinnerNumberModel(1, 1, 10000, 1);
        SPINER_CANT.setModel(modeloSpinner);
        SPINER_CANT.setFont(new Font("Century Gothic", Font.PLAIN, 18));
        
        // Configurar spinner de precio (decimal con 2 decimales)
        SpinnerNumberModel modeloPrecio = new SpinnerNumberModel(0.00, 0.00, 10000.00, 0.01);
        SPINER_PREC.setModel(modeloPrecio);
        SPINER_PREC.setFont(new Font("Century Gothic", Font.PLAIN, 18));
        ((JSpinner.NumberEditor) SPINER_PREC.getEditor()).getFormat().applyPattern("#0.00");


        setClosable(true);
        setTitle("Registro de Salidas");


        llenarComboBoxSede();
        llenarComboBoxUsuarios();
        llenarComboBoxProductos();
        clearFields();

        // Cargar bodegas dinámicamente al seleccionar sede
        BOX_sede.addActionListener(evt -> {
            if (BOX_sede.getSelectedItem() != null) {
                int idSede = Integer.parseInt(BOX_sede.getSelectedItem().toString().split(" ")[0]);
                llenarComboBoxBodegasPorSede(idSede);
            }
        });

        // Mostrar todas las salidas registradas
        showSalidas();
    }
    
    Conexion Conex = new Conexion();
    Connection Cn = Conex.Conexion();
    private int idMovimientoSeleccionado = -1;
    private int idDetalleSeleccionado = -1;
    private boolean edit = false;
    
    private void llenarComboBoxSede() {
        String sql = "SELECT id_sede, nombre_sede FROM sede ORDER BY nombre_sede ASC";
        try (Connection con = new Conexion().Conexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            BOX_sede.removeAllItems();
            while (rs.next()) {
                int id = rs.getInt("id_sede");
                String nombre = rs.getString("nombre_sede");
                BOX_sede.addItem(id + " - " + nombre);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar sedes: " + e.getMessage());
        }
    }
    
    private void llenarComboBoxBodegasPorSede(int idSede) {
        String sql = "SELECT id_bodega, nombre_bodega FROM bodega WHERE id_sede = ? ORDER BY nombre_bodega ASC";
        try (Connection con = new Conexion().Conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSede);
            ResultSet rs = ps.executeQuery();

            BOX_bodegaOrigen.removeAllItems();
            BOX_bodegaDestino.removeAllItems();

            while (rs.next()) {
                int id = rs.getInt("id_bodega");
                String nombre = rs.getString("nombre_bodega");
                BOX_bodegaOrigen.addItem(id + " - " + nombre);
                BOX_bodegaDestino.addItem(id + " - " + nombre);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar bodegas: " + e.getMessage());
        }
    }
    
    private void llenarComboBoxUsuarios() {
        String sql = "SELECT id_usuario, nombres FROM usuario ORDER BY id_usuario ASC";
        try (Connection con = new Conexion().Conexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            BOX_responsable.removeAllItems();
            BOX_receptor.removeAllItems();

            while (rs.next()) {
                int id = rs.getInt("id_usuario");
                String nombre = rs.getString("nombres");
                BOX_responsable.addItem(id + " - " + nombre);
                BOX_receptor.addItem(id + " - " + nombre);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage());
        }
    }
    
    private void llenarComboBoxProductos() {
        String sql = "SELECT id_producto, nombre_producto FROM producto ORDER BY nombre_producto ASC";
        try (Connection con = new Conexion().Conexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            BOX_producto.removeAllItems();
            while (rs.next()) {
                int id = rs.getInt("id_producto");
                String nombre = rs.getString("nombre_producto");
                BOX_producto.addItem(id + " - " + nombre);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
        }
    }
    
    private void showSalidas() {
        JTableHeader header = TABLA_SALIDA.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setForeground(new Color(0x20, 0x41, 0x94));
        header.setBackground(new Color(220, 220, 220));

        DefaultTableModel model = new DefaultTableModel(
            new Object[]{
                "ID MOVIMIENTO", "ID DETALLE", "SEDE",
                "BODEGA ORIGEN", "BODEGA DESTINO",
                "RESPONSABLE", "RECEPTOR",
                "PRODUCTO", "CANTIDAD", "PRECIO UNITARIO",
                "FECHA", "OBSERVACIONES"
            }, 0
        );

        try {
            String sql =
                "SELECT " +
                "  m.id_movimiento, " +
                "  d.id_detalle, " +
                "  s.nombre_sede, " +
                "  bo.nombre_bodega AS bodega_origen, " +
                "  bd.nombre_bodega AS bodega_destino, " +
                "  ur.nombres AS responsable, " +
                "  ure.nombres AS receptor, " +
                "  p.nombre_producto, " +              // 👈 agregado
                "  d.cantidad, " +
                "  d.precio_unitario, " +
                "  m.fecha, " +
                "  m.observaciones " +
                "FROM movimiento m " +
                "INNER JOIN detalle_movimiento d ON m.id_movimiento = d.id_movimiento " +
                "INNER JOIN producto p ON d.id_producto = p.id_producto " + // 👈 nuevo JOIN
                "INNER JOIN bodega bo ON m.id_bodega_origen = bo.id_bodega " +
                "INNER JOIN bodega bd ON m.id_bodega_destino = bd.id_bodega " +
                "INNER JOIN sede s ON bo.id_sede = s.id_sede " +
                "INNER JOIN usuario ur ON m.id_usuario_responsable = ur.id_usuario " +
                "INNER JOIN usuario ure ON m.id_usuario_receptor = ure.id_usuario " +
                "WHERE m.tipo_movimiento = 'SALIDA' " +
                "ORDER BY m.fecha DESC";

            Statement st = Cn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_movimiento"),
                    rs.getInt("id_detalle"),
                    rs.getString("nombre_sede"),
                    rs.getString("bodega_origen"),
                    rs.getString("bodega_destino"),
                    rs.getString("responsable"),
                    rs.getString("receptor"),
                    rs.getString("nombre_producto"),     // 👈 agregado
                    rs.getInt("cantidad"),
                    rs.getBigDecimal("precio_unitario").setScale(2, RoundingMode.HALF_UP),
                    rs.getString("fecha"),
                    rs.getString("observaciones")
                });
            }

            TABLA_SALIDA.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al mostrar salidas: " + e.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // GUARDAR NUEVA SALIDA O ACTUALIZAR EXISTENTE
    private void saveSalida() {
        try {
            if (BOX_sede.getSelectedItem() == null ||
                BOX_bodegaOrigen.getSelectedItem() == null ||
                BOX_bodegaDestino.getSelectedItem() == null ||
                BOX_responsable.getSelectedItem() == null ||
                BOX_receptor.getSelectedItem() == null ||
                TXT_OBS.getText().isEmpty() || 
                BOX_producto.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar todos los campos obligatorios");
                return;
            }

            // --- Obtener valores seleccionados ---
            int idSede = Integer.parseInt(BOX_sede.getSelectedItem().toString().split(" ")[0]);
            int idBodegaOrigen = Integer.parseInt(BOX_bodegaOrigen.getSelectedItem().toString().split(" ")[0]);
            int idBodegaDestino = Integer.parseInt(BOX_bodegaDestino.getSelectedItem().toString().split(" ")[0]);
            int idResponsable = Integer.parseInt(BOX_responsable.getSelectedItem().toString().split(" ")[0]);
            int idReceptor = Integer.parseInt(BOX_receptor.getSelectedItem().toString().split(" ")[0]);
            int idProducto = Integer.parseInt(BOX_producto.getSelectedItem().toString().split(" ")[0]);
            int cantidad = (int) SPINER_CANT.getValue();
            BigDecimal precioBD = new BigDecimal(SPINER_PREC.getValue().toString());
            double precio = precioBD.setScale(2, RoundingMode.HALF_UP).doubleValue();

            String observaciones = TXT_OBS.getText().trim();

            // --- Validaciones ---
            if (cantidad <= 0 || precio <= 0) {
                JOptionPane.showMessageDialog(this, "Cantidad y precio deben ser mayores que cero",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // --- INSERTAR O ACTUALIZAR MOVIMIENTO ---
            String sql = "{CALL sp_movimiento_vida(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement cs = Cn.prepareCall(sql);

            if (edit) {
                cs.setString(1, "UM"); // Update movimiento
                cs.setInt(2, idMovimientoSeleccionado);
            } else {
                cs.setString(1, "IM"); // Insert movimiento
                cs.setObject(2, null);
            }

            cs.setString(3, "SALIDA");              // tipo_movimiento (fijo)
            cs.setInt(4, idBodegaOrigen);
            cs.setInt(5, idBodegaDestino);
            cs.setInt(6, idResponsable);
            cs.setInt(7, idReceptor);
            cs.setString(8, observaciones);
            // Ajuste manual de -6 horas (zona Centroamérica)
            long ahora = System.currentTimeMillis() - (6 * 60 * 60 * 1000);
            cs.setTimestamp(9, new java.sql.Timestamp(ahora));

            cs.execute();

            // --- Obtener ID del movimiento recién creado (solo en modo insert) ---
            int idMovimiento;
            if (!edit) {
                String query = "SELECT MAX(id_movimiento) AS id FROM movimiento WHERE tipo_movimiento = 'SALIDA'";
                Statement st = Cn.createStatement();
                ResultSet rs = st.executeQuery(query);
                rs.next();
                idMovimiento = rs.getInt("id");
            } else {
                idMovimiento = idMovimientoSeleccionado;
            }

            String sqlDet = "{CALL sp_detalle_salida_vida(?, ?, ?, ?, ?, ?)}";
            CallableStatement cs2 = Cn.prepareCall(sqlDet);

            cs2.setString(1, edit ? "US" : "IS");
            if (edit) {
                cs2.setInt(2, idDetalleSeleccionado);
            } else {
                cs2.setNull(2, java.sql.Types.INTEGER);
            }
            cs2.setInt(3, idMovimiento);                    // i_id_movimiento
            cs2.setInt(4, idProducto);                      // i_id_producto
            cs2.setInt(5, cantidad);                        // i_cantidad
            cs2.setDouble(6, precio);                       // i_precio_unitario

            cs2.execute();


            // --- ACTUALIZAR STOCK EN LA BODEGA DE ORIGEN ---
            String sqlStock = "UPDATE stock_bodega SET cantidad = cantidad - ? WHERE id_bodega = ? AND id_producto = ?";
            PreparedStatement ps = Cn.prepareStatement(sqlStock);
            ps.setInt(1, cantidad);
            ps.setInt(2, idBodegaOrigen);
            ps.setInt(3, idProducto);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, edit
                    ? "✅ Salida actualizada correctamente"
                    : "✅ Salida registrada correctamente");

            // --- Refrescar tabla ---
            showSalidas();
            clearFields();
            edit = false;
            idMovimientoSeleccionado = -1;
            BTN_SAVE.setText("GUARDAR");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar salida: " + e.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // EDITAR SALIDA (CARGAR DATOS A LOS CAMPOS)
    private void updateSalida() {
        int fila = TABLA_SALIDA.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una salida para editar");
            return;
        }

        idMovimientoSeleccionado = Integer.parseInt(TABLA_SALIDA.getValueAt(fila, 0).toString());
        idDetalleSeleccionado = Integer.parseInt(TABLA_SALIDA.getValueAt(fila, 1).toString());
        edit = true;

        String sede          = TABLA_SALIDA.getValueAt(fila, 2).toString();
        String bodegaOrigen  = TABLA_SALIDA.getValueAt(fila, 3).toString();
        String bodegaDestino = TABLA_SALIDA.getValueAt(fila, 4).toString();
        String responsable   = TABLA_SALIDA.getValueAt(fila, 5).toString();
        String receptor      = TABLA_SALIDA.getValueAt(fila, 6).toString();
        String producto      = TABLA_SALIDA.getValueAt(fila, 7).toString();  // 👈 nuevo
        int cantidad         = Integer.parseInt(TABLA_SALIDA.getValueAt(fila, 8).toString());
        BigDecimal precio    = new BigDecimal(TABLA_SALIDA.getValueAt(fila, 9).toString().replace(",", "."))
                                    .setScale(2, RoundingMode.HALF_UP);
        String observaciones = TABLA_SALIDA.getValueAt(fila, 11).toString();

        TXT_OBS.setText(observaciones);
        SPINER_CANT.setValue(cantidad);
        SPINER_PREC.setValue(precio.doubleValue());

        // Seleccionar SEDE
        for (int i = 0; i < BOX_sede.getItemCount(); i++) {
            if (BOX_sede.getItemAt(i).contains(sede)) {
                BOX_sede.setSelectedIndex(i);
                break;
            }
        }

        int idSede = Integer.parseInt(BOX_sede.getSelectedItem().toString().split(" ")[0]);
        llenarComboBoxBodegasPorSede(idSede);

        // Seleccionar bodegas
        for (int i = 0; i < BOX_bodegaOrigen.getItemCount(); i++) {
            if (BOX_bodegaOrigen.getItemAt(i).contains(bodegaOrigen)) {
                BOX_bodegaOrigen.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < BOX_bodegaDestino.getItemCount(); i++) {
            if (BOX_bodegaDestino.getItemAt(i).contains(bodegaDestino)) {
                BOX_bodegaDestino.setSelectedIndex(i);
                break;
            }
        }

        // Seleccionar usuarios
        for (int i = 0; i < BOX_responsable.getItemCount(); i++) {
            if (BOX_responsable.getItemAt(i).contains(responsable)) {
                BOX_responsable.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < BOX_receptor.getItemCount(); i++) {
            if (BOX_receptor.getItemAt(i).contains(receptor)) {
                BOX_receptor.setSelectedIndex(i);
                break;
            }
        }

        // Seleccionar producto 👇
        for (int i = 0; i < BOX_producto.getItemCount(); i++) {
            if (BOX_producto.getItemAt(i).contains(producto)) {
                BOX_producto.setSelectedIndex(i);
                break;
            }
        }

        BTN_SAVE.setText("ACTUALIZAR");
        JOptionPane.showMessageDialog(this, "Editando salida #" + idMovimientoSeleccionado);
    }
    
    // LIMPIAR CAMPOS
    private void clearFields() {
        if (BOX_sede.getItemCount() > 0) BOX_sede.setSelectedIndex(0);
        if (BOX_bodegaOrigen.getItemCount() > 0) BOX_bodegaOrigen.setSelectedIndex(0);
        if (BOX_bodegaDestino.getItemCount() > 0) BOX_bodegaDestino.setSelectedIndex(0);
        if (BOX_responsable.getItemCount() > 0) BOX_responsable.setSelectedIndex(0);
        if (BOX_receptor.getItemCount() > 0) BOX_receptor.setSelectedIndex(0);
        TXT_OBS.setText("");
        SPINER_CANT.setValue(1);
        SPINER_PREC.setValue(0.00);

        
        BOX_sede.setSelectedIndex(-1);
        BOX_bodegaOrigen.setSelectedIndex(-1);
        BOX_bodegaDestino.setSelectedIndex(-1);
        BOX_responsable.setSelectedIndex(-1);
        BOX_receptor.setSelectedIndex(-1);
        BOX_producto.setSelectedIndex(-1);
    }


    



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        UD = new javax.swing.JPopupMenu();
        Editar = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        BOX_sede = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        BOX_bodegaOrigen = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        BOX_bodegaDestino = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        BOX_responsable = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        BOX_receptor = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        TXT_OBS = new javax.swing.JTextField();
        BTN_SAVE = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TABLA_SALIDA = new javax.swing.JTable();
        BOX_producto = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        SPINER_CANT = new javax.swing.JSpinner();
        SPINER_PREC = new javax.swing.JSpinner();

        Editar.setText("Editar");
        Editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditarActionPerformed(evt);
            }
        });
        UD.add(Editar);

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Century Gothic", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(32, 65, 148));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Salidas");

        jLabel2.setFont(new java.awt.Font("Century Gothic", 1, 20)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(32, 65, 148));
        jLabel2.setText("Sede");

        BOX_sede.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        BOX_sede.setForeground(new java.awt.Color(32, 65, 148));
        BOX_sede.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SALIDA", "ENTRADA" }));
        BOX_sede.setSelectedIndex(-1);
        BOX_sede.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BOX_sedeActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Century Gothic", 1, 20)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(32, 65, 148));
        jLabel3.setText("Bodega de origen");

        BOX_bodegaOrigen.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        BOX_bodegaOrigen.setForeground(new java.awt.Color(32, 65, 148));
        BOX_bodegaOrigen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setFont(new java.awt.Font("Century Gothic", 1, 20)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(32, 65, 148));
        jLabel4.setText("Bodega de destino");

        BOX_bodegaDestino.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        BOX_bodegaDestino.setForeground(new java.awt.Color(32, 65, 148));
        BOX_bodegaDestino.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setFont(new java.awt.Font("Century Gothic", 1, 20)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(32, 65, 148));
        jLabel5.setText("Usuario responsable");

        BOX_responsable.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        BOX_responsable.setForeground(new java.awt.Color(32, 65, 148));
        BOX_responsable.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setFont(new java.awt.Font("Century Gothic", 1, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(32, 65, 148));
        jLabel6.setText("Usuario receptor");

        BOX_receptor.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        BOX_receptor.setForeground(new java.awt.Color(32, 65, 148));
        BOX_receptor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel7.setFont(new java.awt.Font("Century Gothic", 1, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(32, 65, 148));
        jLabel7.setText("Producto");

        jLabel8.setFont(new java.awt.Font("Century Gothic", 1, 20)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(32, 65, 148));
        jLabel8.setText("Observaciones");

        TXT_OBS.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        TXT_OBS.setForeground(new java.awt.Color(32, 65, 148));
        TXT_OBS.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(32, 65, 148)));

        BTN_SAVE.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        BTN_SAVE.setForeground(new java.awt.Color(140, 198, 63));
        BTN_SAVE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/guardar.png"))); // NOI18N
        BTN_SAVE.setText("GUARDAR");
        BTN_SAVE.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BTN_SAVE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BTN_SAVEMouseClicked(evt);
            }
        });

        TABLA_SALIDA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID MOVIMIENTO", "ID BODEGA ORIGEN", "ID BODEGA DESTINO", "ID USUARIO RESPONSABLE", "ID USUARIO RECEPTOR", "TIPO MOVIMIENTO", "FECHA", "OBSERVACIONES"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TABLA_SALIDA.setComponentPopupMenu(UD);
        jScrollPane1.setViewportView(TABLA_SALIDA);

        BOX_producto.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        BOX_producto.setForeground(new java.awt.Color(32, 65, 148));
        BOX_producto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel9.setFont(new java.awt.Font("Century Gothic", 1, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(32, 65, 148));
        jLabel9.setText("Cantidad");

        jLabel10.setFont(new java.awt.Font("Century Gothic", 1, 20)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(32, 65, 148));
        jLabel10.setText("Precio unitario");

        SPINER_CANT.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        SPINER_PREC.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(87, 87, 87)
                                .addComponent(TXT_OBS, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(BOX_bodegaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(BOX_sede, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(25, 25, 25))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(38, 38, 38)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(BOX_receptor, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(SPINER_CANT, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel10))
                                .addGap(64, 64, 64)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SPINER_PREC, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(BOX_responsable, 0, 150, Short.MAX_VALUE)
                                .addComponent(BOX_producto, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(BOX_bodegaOrigen, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap(73, Short.MAX_VALUE))))
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BTN_SAVE)
                .addGap(310, 310, 310))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BOX_sede, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(BOX_bodegaOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(BOX_bodegaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(BOX_responsable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(BOX_receptor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(BOX_producto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(SPINER_CANT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SPINER_PREC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(TXT_OBS, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(BTN_SAVE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BTN_SAVEMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BTN_SAVEMouseClicked
        // TODO add your handling code here:
        saveSalida();
    }//GEN-LAST:event_BTN_SAVEMouseClicked

    private void EditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditarActionPerformed
        // TODO add your handling code here:
        updateSalida();
    }//GEN-LAST:event_EditarActionPerformed

    private void BOX_sedeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BOX_sedeActionPerformed
        // TODO add your handling code here:
        if (BOX_sede.getSelectedItem() != null) {
            int idSede = Integer.parseInt(BOX_sede.getSelectedItem().toString().split(" ")[0]);
            llenarComboBoxBodegasPorSede(idSede);
        }
    }//GEN-LAST:event_BOX_sedeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> BOX_bodegaDestino;
    private javax.swing.JComboBox<String> BOX_bodegaOrigen;
    private javax.swing.JComboBox<String> BOX_producto;
    private javax.swing.JComboBox<String> BOX_receptor;
    private javax.swing.JComboBox<String> BOX_responsable;
    private javax.swing.JComboBox<String> BOX_sede;
    private javax.swing.JLabel BTN_SAVE;
    private javax.swing.JMenuItem Editar;
    private javax.swing.JSpinner SPINER_CANT;
    private javax.swing.JSpinner SPINER_PREC;
    private javax.swing.JTable TABLA_SALIDA;
    private javax.swing.JTextField TXT_OBS;
    private javax.swing.JPopupMenu UD;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
