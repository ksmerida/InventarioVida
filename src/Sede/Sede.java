/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Sede;

import User.*;
import Connection.Conexion;
import java.awt.Color;
import java.awt.Font;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Diego
 */
public class Sede extends javax.swing.JInternalFrame {

    /**
     * Creates new form User
     */
    public Sede() {
        initComponents();
        setClosable(true);
        showSedes();
        clearSede();
    }

    Conexion Conex = new Conexion();
    Connection Cn = Conex.Conexion();
    private int idSedeSelected = -1;
    private boolean edit = false;

    private boolean validateFields() {
        if (txtNames.getText().isEmpty()
                || txtDir.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }



        return true;
    }

private void saveSede() {
    if (!validateFields()) {
        return;
    }

    try {
        String sql;
        PreparedStatement ps;

        if (edit) {
            // Actualizar una sede existente
            sql = "UPDATE sede SET nombre_sede = ?, direccion = ?, estado = ? WHERE id_sede = ?";
            ps = Cn.prepareStatement(sql);
            ps.setString(1, txtNames.getText().trim());
            ps.setString(2, txtDir.getText().trim());
            ps.setBoolean(3, true);
            ps.setInt(4, idSedeSelected);
        } else {
            // Insertar una nueva sede
            sql = "INSERT INTO sede (nombre_sede, direccion, estado) VALUES (?, ?, ?)";
            ps = Cn.prepareStatement(sql);
            ps.setString(1, txtNames.getText().trim());
            ps.setString(2, txtDir.getText().trim());
            ps.setBoolean(3, true);
        }

        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, edit
                ? "✅ Sede actualizada correctamente"
                : "✅ Sede guardada correctamente");

        showSedes(); // Método que refresca la tabla
        clearSede(); // Limpia los campos
        edit = false;
        idSedeSelected = -1;
        lblSave.setText("GUARDAR");

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "❌ Error al guardar sede: " + e.getMessage());
        e.printStackTrace();
    }
}


private void showSedes() {
    JTableHeader header = tblSedes.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 18));
    header.setForeground(new Color(0x20, 0x41, 0x94));
    header.setBackground(new Color(220, 220, 220));

    DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "NOMBRE", "DIRECCIÓN"}, 0
    );

    try {
        String sql = "SELECT * FROM sede WHERE estado = 1";
        PreparedStatement ps = Cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("id_sede"),
                rs.getString("nombre_sede"),
                rs.getString("direccion")
            });
        }

        tblSedes.setModel(model);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "❌ Error al mostrar sedes: " + e.getMessage());
        e.printStackTrace();
    }
}


private void updateSede() {
    int fila = tblSedes.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "⚠️ Seleccione una sede para editar");
        return;
    }

    idSedeSelected = (int) tblSedes.getValueAt(fila, 0);
    edit = true;

    txtNames.setText((String) tblSedes.getValueAt(fila, 1));
    txtDir.setText((String) tblSedes.getValueAt(fila, 2));
    lblSave.setText("ACTUALIZAR");
}


private void deleteSede() {
    int fila = tblSedes.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "⚠️ Seleccione una sede para eliminar");
        return;
    }

    int idSede = (int) tblSedes.getValueAt(fila, 0);
    String nombre = (String) tblSedes.getValueAt(fila, 1);

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar la sede \"" + nombre + "\"?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            String sql = "UPDATE sede SET estado = 0 WHERE id_sede = ?";
            PreparedStatement ps = Cn.prepareStatement(sql);
            ps.setInt(1, idSede);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Sede eliminada correctamente");
            showSedes();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error al eliminar sede: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


private void clearSede() {
    txtNames.setText("");
    txtDir.setText("");
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
        txtNames = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDir = new javax.swing.JTextField();
        lblSave = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSedes = new javax.swing.JTable();

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
        setFont(new java.awt.Font("Century Gothic", 0, 10)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Century Gothic", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(32, 65, 148));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Sedes");

        jLabel2.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(32, 65, 148));
        jLabel2.setText("Nombre");

        txtNames.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        txtNames.setForeground(new java.awt.Color(32, 65, 148));
        txtNames.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));

        jLabel3.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(32, 65, 148));
        jLabel3.setText("Direccion");

        txtDir.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        txtDir.setForeground(new java.awt.Color(32, 65, 148));
        txtDir.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));
        txtDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDirActionPerformed(evt);
            }
        });

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

        tblSedes.setFont(new java.awt.Font("Century Gothic", 0, 16)); // NOI18N
        tblSedes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID Sede", "Nombre", "Direccion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSedes.setComponentPopupMenu(UD);
        tblSedes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jScrollPane1.setViewportView(tblSedes);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(28, 28, 28)
                        .addComponent(txtNames, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(36, 36, 36)
                        .addComponent(txtDir, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 20, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(289, 289, 289)
                .addComponent(lblSave)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNames, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtDir, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(50, 50, 50)
                .addComponent(lblSave)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lblSaveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSaveMouseClicked
        // TODO add your handling code here:
        saveSede();
    }//GEN-LAST:event_lblSaveMouseClicked

    private void EditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditarActionPerformed
        updateSede();
    }//GEN-LAST:event_EditarActionPerformed

    private void EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarActionPerformed
        deleteSede();
    }//GEN-LAST:event_EliminarActionPerformed

    private void txtDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Editar;
    private javax.swing.JMenuItem Eliminar;
    private javax.swing.JPopupMenu UD;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSave;
    private javax.swing.JTable tblSedes;
    private javax.swing.JTextField txtDir;
    private javax.swing.JTextField txtNames;
    // End of variables declaration//GEN-END:variables
}
