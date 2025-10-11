/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Bodega;

import Connection.Conexion;
import java.awt.Color;
import java.awt.Font;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.JDesktopPane;

/**
 *
 * @author dc10a
 */
public class Bodega extends javax.swing.JInternalFrame {

    /**
     * Creates new form Bodega
     */
    public Bodega() {
        initComponents();
        setClosable(true);
        llenarComboBoxID_Sede();
        clearFields();
        showBodegas();
        clearFields();
    }
    
    Conexion Conex = new Conexion();
    Connection Cn = Conex.Conexion();
    private int idBodegaSeleccionada = -1;
    private boolean edit = false;

    
    private boolean validarCampos() {
        if (TXT_nombre.getText().isEmpty()
                || TXT_nombre.getText().isEmpty()
                || TXT_desc.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // LLAVE FORÁNEA SEDE (muestra ID y nombre legible)
    private void llenarComboBoxID_Sede() {
        String sql = "SELECT id_sede, nombre_sede FROM sede ORDER BY id_sede ASC";

        try (Connection con = new Conexion().Conexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            BOX_idsede.removeAllItems(); // Limpia el ComboBox antes de llenarlo

            while (rs.next()) {
                int id = rs.getInt("id_sede");
                String nombre = rs.getString("nombre_sede");
                BOX_idsede.addItem(id + " - " + nombre); // Muestra ID y nombre
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar las sedes: " + e.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    // Método auxiliar para obtener el nombre de la sede según su ID
    private String obtenerNombreSede(int idSede) {
        String nombre = "Desconocida";
        String sql = "SELECT nombre_sede FROM sede WHERE id_sede = ?";

        try (Connection con = new Conexion().Conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSede);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nombre = rs.getString("nombre_sede");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener nombre de sede: " + e.getMessage());
        }

        return nombre;
    }

    // MOSTRAR EN LA TABLA LOS REGISTROS
    // MOSTRAR EN LA TABLA LOS REGISTROS
private void showBodegas() {
    JTableHeader header = TABLA_BODEGA.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 18));
    header.setForeground(new Color(0x20, 0x41, 0x94));
    header.setBackground(new Color(220, 220, 220));

    DefaultTableModel model = new DefaultTableModel(
        new Object[]{"ID BODEGA", "SEDE", "NOMBRE", "DESCRIPCIÓN"}, 0
    );

    try {
        String sql = "{CALL sp_bodega_vida(?, NULL, NULL, NULL, NULL, NULL)}";
        CallableStatement cs = Cn.prepareCall(sql);
        cs.setString(1, "SB"); // Solo bodegas activas
        ResultSet rs = cs.executeQuery();

        while (rs.next()) {
            int idSede = rs.getInt("id_sede");
            String nombreSede = obtenerNombreSede(idSede);

            model.addRow(new Object[]{
                rs.getInt("id_bodega"),
                idSede + " - " + nombreSede,
                rs.getString("nombre_bodega"),
                rs.getString("descripcion")
            });
        }

        TABLA_BODEGA.setModel(model);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al mostrar bodegas: " + e.getMessage());
        e.printStackTrace();
    }
}



    
    // GUARDAR REGISTROS
private void saveBodega() {
    if (!validarCampos()) {
        return;
    }

    try {
        String sql = "{CALL sp_bodega_vida(?, ?, ?, ?, ?, ?)}";
        CallableStatement cs = Cn.prepareCall(sql);

        // Determinar si se está editando o insertando
        if (edit) {
            cs.setString(1, "UB"); // Update
            cs.setInt(2, idBodegaSeleccionada);
        } else {
            cs.setString(1, "IB"); // Insert
            cs.setObject(2, null);
        }

        // Obtener valores desde los componentes
        String sedeSeleccionada = BOX_idsede.getSelectedItem().toString();
        int idSede = Integer.parseInt(sedeSeleccionada.split(" - ")[0].trim());
        String nombre = TXT_nombre.getText().trim();
        String descripcion = TXT_desc.getText().trim();

        // Asignar parámetros restantes
        cs.setInt(3, idSede);
        cs.setString(4, nombre);
        cs.setString(5, descripcion);
        cs.setBoolean(6, true); // Siempre ACTIVO

        // Ejecutar SP
        cs.execute();

        JOptionPane.showMessageDialog(this, edit
                ? "✅ Bodega actualizada correctamente"
                : "✅ Bodega guardada correctamente");

        BTN_SAVE.setText("GUARDAR");
        showBodegas();
        clearFields();
        edit = false;
        idBodegaSeleccionada = -1;

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al guardar la bodega: " + e.getMessage());
        e.printStackTrace();
    }
}

    
    // EDITAR Y ACTUALIZAR
    // EDITAR Y ACTUALIZAR
private void updateBodega() {
    int fila = TABLA_BODEGA.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione una bodega para editar");
        return;
    }

    idBodegaSeleccionada = (int) TABLA_BODEGA.getValueAt(fila, 0);
    edit = true;

    TXT_nombre.setText((String) TABLA_BODEGA.getValueAt(fila, 2));
    TXT_desc.setText((String) TABLA_BODEGA.getValueAt(fila, 3));

    // Seleccionar sede correcta
    String idSedeTabla = TABLA_BODEGA.getValueAt(fila, 1).toString();
    for (int i = 0; i < BOX_idsede.getItemCount(); i++) {
        if (BOX_idsede.getItemAt(i).equals(idSedeTabla)) {
            BOX_idsede.setSelectedIndex(i);
            break;
        }
    }

    BTN_SAVE.setText("ACTUALIZAR");
}

    
    // ELIMINAR BODEGA
    private void deleteBodega() {
        int fila = TABLA_BODEGA.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una bodega para eliminar");
            return;
        }

        int idBodega = (int) TABLA_BODEGA.getValueAt(fila, 0);
        String nombre = (String) TABLA_BODEGA.getValueAt(fila, 2);
        String descripcion = (String) TABLA_BODEGA.getValueAt(fila, 3);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar la bodega \"" + nombre + "\"?\nDescripción: " + descripcion,
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "{CALL sp_bodega_vida(?, ?, NULL, NULL, NULL, NULL)}";
                CallableStatement cs = Cn.prepareCall(sql);
                cs.setString(1, "DB"); // Delete Bodega (eliminación lógica)
                cs.setInt(2, idBodega);
                cs.execute();

                JOptionPane.showMessageDialog(this, "✅ Bodega eliminada correctamente");
                showBodegas(); // refrescar la tabla

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar bodega: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    // LIMPIAR CAMPOS
    private void clearFields() {
        TXT_nombre.setText("");
        TXT_desc.setText("");

        BOX_idsede.setSelectedIndex(-1);
        if (BOX_idsede.getItemCount() > 0) {
            BOX_idsede.setSelectedIndex(0);
        }

        BOX_idsede.setSelectedIndex(-1);
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
        TXT_nombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        BOX_idsede = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        TXT_desc = new javax.swing.JTextField();
        BTN_SAVE = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TABLA_BODEGA = new javax.swing.JTable();

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

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Century Gothic", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(32, 65, 148));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Bodegas");

        jLabel2.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(32, 65, 148));
        jLabel2.setText("Nombre");

        TXT_nombre.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        TXT_nombre.setForeground(new java.awt.Color(32, 65, 148));
        TXT_nombre.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));

        jLabel3.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(32, 65, 148));
        jLabel3.setText("Sede");

        BOX_idsede.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        BOX_idsede.setForeground(new java.awt.Color(32, 65, 148));
        BOX_idsede.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(32, 65, 148));
        jLabel4.setText("Descripción");

        TXT_desc.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        TXT_desc.setForeground(new java.awt.Color(32, 65, 148));
        TXT_desc.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(288, 288, 288)
                        .addComponent(BTN_SAVE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(TXT_nombre)
                            .addComponent(BOX_idsede, 0, 240, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TXT_desc, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(TXT_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(TXT_desc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(BOX_idsede, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addComponent(BTN_SAVE)
                .addGap(0, 23, Short.MAX_VALUE))
        );

        TABLA_BODEGA.setFont(new java.awt.Font("Century Gothic", 0, 16)); // NOI18N
        TABLA_BODEGA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID BODEGA", "ID SEDE", "NOMBRE", "DESCRIPCIÓN"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TABLA_BODEGA.setComponentPopupMenu(UD);
        TABLA_BODEGA.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jScrollPane1.setViewportView(TABLA_BODEGA);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BTN_SAVEMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BTN_SAVEMouseClicked
        // TODO add your handling code here:
        saveBodega();
    }//GEN-LAST:event_BTN_SAVEMouseClicked

    private void EditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditarActionPerformed
        // TODO add your handling code here:
        updateBodega();
    }//GEN-LAST:event_EditarActionPerformed

    private void EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarActionPerformed
        // TODO add your handling code here:
        deleteBodega();
    }//GEN-LAST:event_EliminarActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> BOX_idsede;
    private javax.swing.JLabel BTN_SAVE;
    private javax.swing.JMenuItem Editar;
    private javax.swing.JMenuItem Eliminar;
    private javax.swing.JTable TABLA_BODEGA;
    private javax.swing.JTextField TXT_desc;
    private javax.swing.JTextField TXT_nombre;
    private javax.swing.JPopupMenu UD;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
