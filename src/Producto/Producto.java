package Producto;

import Connection.Conexion;
import java.awt.Color;
import java.awt.Font;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Diego
 */
public class Producto extends javax.swing.JInternalFrame {

    public Producto() {
        initComponents();
        setClosable(true);
                
        loadMarcas();
        showProductos();
        clearProducto();      
    }

    Conexion Conex = new Conexion();
    Connection Cn = Conex.Conexion();
    private int idProductoSelected = -1;
    private boolean edit = false;

    private void loadMarcas() {
        try {
            String sql = "SELECT id_marca, nombre_marca FROM marca";
            PreparedStatement ps = Cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            cmbMarca.removeAllItems();
            cmbMarca.addItem("Seleccione una marca");

            while (rs.next()) {
                cmbMarca.addItem(rs.getInt("id_marca") + " - " + rs.getString("nombre_marca"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error al cargar marcas: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        if (txtNombre.getText().trim().isEmpty()
                || txtDescripcion.getText().trim().isEmpty()
                || txtCodigoBarras.getText().trim().isEmpty()
                || txtColor.getText().trim().isEmpty()
                || txtPresentacion.getText().trim().isEmpty()
                || txtUnidadMedida.getText().trim().isEmpty()
                || jStock.getValue().equals(-1)
                || cmbMarca.getSelectedIndex() == 0) {

            JOptionPane.showMessageDialog(this, "⚠️ Todos los campos son obligatorios", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void saveProducto() {
        if (!validateFields()) {
            return;
        }

        try {
            int idMarca = Integer.parseInt(Objects.requireNonNull(cmbMarca.getSelectedItem()).toString().split(" - ")[0]);
            int stock = (int) jStock.getValue();
            
            String sql = "{CALL sp_producto(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement cs = Cn.prepareCall(sql);

            if (edit) {
                cs.setString(1, "UP");
                cs.setObject(2, idProductoSelected);
                cs.setInt(3, idMarca);
                cs.setString(4, txtNombre.getText().trim());
                cs.setString(5, txtDescripcion.getText().trim());
                cs.setString(6, txtCodigoBarras.getText().trim());
                cs.setString(7, txtColor.getText().trim());
                cs.setString(8, txtPresentacion.getText().trim());
                cs.setString(9, txtUnidadMedida.getText().trim());
                cs.setInt(10, stock);
                cs.setBoolean(11, true);
            } else {
                cs.setString(1, "IP");
                cs.setObject(2, null);
                cs.setInt(3, idMarca);
                cs.setString(4, txtNombre.getText().trim());
                cs.setString(5, txtDescripcion.getText().trim());
                cs.setString(6, txtCodigoBarras.getText().trim());
                cs.setString(7, txtColor.getText().trim());
                cs.setString(8, txtPresentacion.getText().trim());
                cs.setString(9, txtUnidadMedida.getText().trim());
                cs.setInt(10, stock);
                cs.setBoolean(11, true);
            }

            cs.execute();

            JOptionPane.showMessageDialog(this, edit
                    ? "✅ Producto actualizado correctamente"
                    : "✅ Producto guardado correctamente");

            showProductos();
            clearProducto();
            edit = false;
            idProductoSelected = -1;
            lblSave.setText("GUARDAR");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error al guardar producto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showProductos() {
        JTableHeader header = tblProductos.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(new Color(0x20, 0x41, 0x94));
        header.setBackground(new Color(220, 220, 220));

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "NOMBRE", "MARCA", "DESCRIPCION", "CODIGO DE BARRAS", "COLOR", "PRESENTACION", "UNIDAD DE MEDIDA", "STOCK"}, 0
        );

        try {
            String sql = "{CALL sp_producto('SP', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1)}";
            CallableStatement cs = Cn.prepareCall(sql);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_producto"),
                    rs.getString("nombre_producto"),
                    rs.getString("nombre_marca"),
                    rs.getString("descripcion"),
                    rs.getString("codigo_barras"),
                    rs.getString("color"),
                    rs.getString("presentacion"),
                    rs.getString("unidad_medida"),
                    rs.getInt("stock")
                });
            }

            tblProductos.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error al mostrar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateProducto() {
        int fila = tblProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleccione un producto para editar");
            return;
        }

        idProductoSelected = (int) tblProductos.getValueAt(fila, 0);
        edit = true;

        txtNombre.setText(tblProductos.getValueAt(fila, 1).toString());
        cmbMarca.setSelectedItem(getMarcaItemByName(tblProductos.getValueAt(fila, 2).toString()));
        txtDescripcion.setText(tblProductos.getValueAt(fila, 3).toString());
        txtCodigoBarras.setText(tblProductos.getValueAt(fila, 4).toString());
        txtColor.setText(tblProductos.getValueAt(fila, 5).toString());
        txtPresentacion.setText(tblProductos.getValueAt(fila, 6).toString());
        txtUnidadMedida.setText(tblProductos.getValueAt(fila, 7).toString());
        jStock.setValue(tblProductos.getValueAt(fila, 8));
        lblSave.setText("ACTUALIZAR");
    }
    
        private String getMarcaItemByName(String nombreMarca) {
        for (int i = 1; i < cmbMarca.getItemCount(); i++) {
            String item = cmbMarca.getItemAt(i);
            if (item.contains(nombreMarca)) {
                return item;
            }
        }
        return "Seleccione una marca";
    }

    private void deleteProducto() {
        int fila = tblProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Seleccione un producto para eliminar");
            return;
        }

        int idProducto = (int) tblProductos.getValueAt(fila, 0);
        String nombre = tblProductos.getValueAt(fila, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar el producto \"" + nombre + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "{CALL sp_producto(?, ?, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)}";
                 CallableStatement cs = Cn.prepareCall(sql);
                cs.setString(1, "DP");
                cs.setInt(2, idProducto);
                cs.execute();

                JOptionPane.showMessageDialog(this, "✅ Producto eliminado correctamente");
                showProductos();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "❌ Error al eliminar producto: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void clearProducto() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtCodigoBarras.setText("");
        txtColor.setText("");
        txtPresentacion.setText("");
        txtUnidadMedida.setText("");
        jStock.setValue(0);
        cmbMarca.setSelectedIndex(0);
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
        Eliminar = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cmbMarca = new javax.swing.JComboBox<>();
        lblSave = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtUnidadMedida = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtColor = new javax.swing.JTextField();
        jStock = new javax.swing.JSpinner();
        txtPresentacion = new javax.swing.JTextField();
        txtCodigoBarras = new javax.swing.JTextField();

        Editar.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        Editar.setText("Editar");
        Editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditarActionPerformed(evt);
            }
        });
        UD.add(Editar);

        Eliminar.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        Eliminar.setText("Eliminar");
        Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarActionPerformed(evt);
            }
        });
        UD.add(Eliminar);

        setBackground(new java.awt.Color(255, 255, 255));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Productos");
        setToolTipText("");
        setFont(new java.awt.Font("Century Gothic", 0, 10)); // NOI18N
        setFrameIcon(null);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Century Gothic", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(32, 65, 148));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Productos");

        jLabel2.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(32, 65, 148));
        jLabel2.setText("Nombre");

        txtNombre.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        txtNombre.setForeground(new java.awt.Color(32, 65, 148));
        txtNombre.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));

        jLabel3.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(32, 65, 148));
        jLabel3.setText("Marca");

        jLabel4.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(32, 65, 148));
        jLabel4.setText("Descripcion");

        txtDescripcion.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        txtDescripcion.setForeground(new java.awt.Color(32, 65, 148));
        txtDescripcion.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));

        jLabel5.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(32, 65, 148));
        jLabel5.setText("Codigo de Barras");

        cmbMarca.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        cmbMarca.setForeground(new java.awt.Color(32, 65, 148));
        cmbMarca.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblSave.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        lblSave.setForeground(new java.awt.Color(140, 198, 63));
        lblSave.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/guardar.png"))); // NOI18N
        lblSave.setText("GUARDAR");
        lblSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblSave.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSaveMouseClicked(evt);
            }
        });

        tblProductos.setFont(new java.awt.Font("Century Gothic", 0, 16)); // NOI18N
        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Producto", "Nombre", "Marca", "Descripccion", "Codigo de barras", "Color", "Presentacion", "Unidad de Medida", "Stock"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProductos.setComponentPopupMenu(UD);
        tblProductos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jScrollPane1.setViewportView(tblProductos);

        jLabel7.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(32, 65, 148));
        jLabel7.setText("Presentacion");

        jLabel8.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(32, 65, 148));
        jLabel8.setText("Color");

        jLabel9.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(32, 65, 148));
        jLabel9.setText("Unidad de Medida");

        txtUnidadMedida.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        txtUnidadMedida.setForeground(new java.awt.Color(32, 65, 148));
        txtUnidadMedida.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));

        jLabel10.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(32, 65, 148));
        jLabel10.setText("Stock");

        txtColor.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        txtColor.setForeground(new java.awt.Color(32, 65, 148));
        txtColor.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));

        jStock.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N

        txtPresentacion.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        txtPresentacion.setForeground(new java.awt.Color(32, 65, 148));
        txtPresentacion.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));

        txtCodigoBarras.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        txtCodigoBarras.setForeground(new java.awt.Color(32, 65, 148));
        txtCodigoBarras.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtUnidadMedida, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel10)
                                .addGap(51, 51, 51)
                                .addComponent(jStock, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(71, 71, 71)
                                        .addComponent(jLabel3))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(71, 71, 71)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel5))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbMarca, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPresentacion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(77, 77, 77))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(431, 431, 431)
                .addComponent(lblSave)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(728, Short.MAX_VALUE)
                    .addComponent(txtCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(67, 67, 67)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(cmbMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPresentacion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtUnidadMedida, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jStock, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addComponent(lblSave)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(153, 153, 153)
                    .addComponent(txtCodigoBarras, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(548, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 624, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lblSaveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSaveMouseClicked
        // TODO add your handling code here:
        saveProducto();
    }//GEN-LAST:event_lblSaveMouseClicked

    private void EditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditarActionPerformed
        updateProducto();
    }//GEN-LAST:event_EditarActionPerformed

    private void EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarActionPerformed
        deleteProducto();
    }//GEN-LAST:event_EliminarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Editar;
    private javax.swing.JMenuItem Eliminar;
    private javax.swing.JPopupMenu UD;
    private javax.swing.JComboBox<String> cmbMarca;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jStock;
    private javax.swing.JLabel lblSave;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTextField txtCodigoBarras;
    private javax.swing.JTextField txtColor;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPresentacion;
    private javax.swing.JTextField txtUnidadMedida;
    // End of variables declaration//GEN-END:variables
}
