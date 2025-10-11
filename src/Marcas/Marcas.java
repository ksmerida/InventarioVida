
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Marcas;


import Connection.Conexion;
import java.awt.Color;
import java.awt.Font;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class Marcas extends javax.swing.JInternalFrame {

   

    public Marcas() {
        initComponents();
        setClosable(true);
        showMarca();
        clearMarca();
    }

     private int idRoleSelected = -1;
    private boolean edit = false;
    Conexion Conex = new Conexion();
    Connection Cn = Conex.Conexion();
    
    private void saveMarca() {
        if (txtMarca.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del rol.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

       try {
    // Llamada con 5 parámetros, como en el procedimiento
    String sql = "{CALL sp_marca(?, ?, ?, ?, ?)}";
    CallableStatement cs = Cn.prepareCall(sql);

    if (edit) {
        cs.setString(1, "UR"); // Update Marca
        cs.setInt(2, idRoleSelected);
    } else {
        cs.setString(1, "IR"); // Insert Marca
        cs.setNull(2, java.sql.Types.INTEGER); // i_id_rol no se usa al insertar
    }

    cs.setString(3, txtMarca.getText()); // i_nombre_rol
    cs.setString(4, txtDescripcion.getText()); // i_descripcion
    cs.setBoolean(5, true); // i_estado (activo)

    cs.execute();

    JOptionPane.showMessageDialog(this, edit
            ? "✅ Marca actualizada correctamente"
            : "✅ Marca guardada correctamente");

    showMarca();
    clearMarca();
    edit = false;
    idRoleSelected = -1;
    lblMarca.setText("GUARDAR");
} catch (Exception e) {
    JOptionPane.showMessageDialog(this, "Error al guardar rol: " + e.getMessage());
    e.printStackTrace();
}
    }

    private void showMarca() {
        JTableHeader header = tblMarcas.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(new Color(0x20, 0x41, 0x94));
        header.setBackground(new Color(220, 220, 220));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "NOMBRE DE LA MARCA","DESCRIPCION"}, 0);
        try {
            String sql = "{CALL sp_marca(?, NULL, NULL, NULL,NULL)}";
            CallableStatement cs = Cn.prepareCall(sql);
            cs.setString(1, "SR"); // Select Marcas
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                if (rs.getBoolean("estado")) { // Solo activos
                    model.addRow(new Object[]{
                        rs.getInt("id_marca"),
                        rs.getString("nombre_marca"),
                        rs.getString("descripcion"),
                    });
                }
            }

            tblMarcas.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al mostrar Marca: " + e.getMessage());
        }
    }

    private void updateMarca() {
        int fila = tblMarcas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un rol para editar.");
            return;
        }

        idRoleSelected = (int) tblMarcas.getValueAt(fila, 0);
        txtMarca.setText((String) tblMarcas.getValueAt(fila, 1));
        edit = true;
        lblMarca.setText("ACTUALIZAR");
    }

    private void deleteMarca() {
        int fila = tblMarcas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una marca para eliminar.");
            return;
        }

        int idRol = (int) tblMarcas.getValueAt(fila, 0);
        String nombreMarca = (String) tblMarcas.getValueAt(fila, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar la marca " + nombreMarca + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "{CALL sp_marca(?, ?, NULL, NULL, NULL)}";
                CallableStatement cs = Cn.prepareCall(sql);
                cs.setString(1, "DR"); // Delete Role
                cs.setInt(2, idRol);
                cs.execute();

                JOptionPane.showMessageDialog(this, "✅ Marca eliminada correctamente");
                showMarca();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar Marca: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void clearMarca() {
        
        idRoleSelected = -1;
        edit = false;
        lblMarca.setText("GUARDAR");
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // </editor-fold>
    // </editor-fold>                        

    /*** @param args the command line arguments
     */
   
    // Variables declaration - do not modify                     
    // End of variables declaration                   



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
        txtMarca = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        lblMarca = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMarcas = new javax.swing.JTable();

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
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/cuenta.png"))); // NOI18N
        jLabel1.setText("Marcas");

        jLabel2.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(32, 65, 148));
        jLabel2.setText("Marca");

        txtMarca.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        txtMarca.setForeground(new java.awt.Color(32, 65, 148));
        txtMarca.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));
        txtMarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMarcaActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(32, 65, 148));
        jLabel3.setText("Descripcion");

        txtDescripcion.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        txtDescripcion.setForeground(new java.awt.Color(32, 65, 148));
        txtDescripcion.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));

        lblMarca.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        lblMarca.setForeground(new java.awt.Color(140, 198, 63));
        lblMarca.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMarca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/guardar.png"))); // NOI18N
        lblMarca.setText("GUARDAR");
        lblMarca.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblMarca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMarcaMouseClicked(evt);
            }
        });

        tblMarcas.setFont(new java.awt.Font("Century Gothic", 0, 16)); // NOI18N
        tblMarcas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID USUARIO", "ROL", "NOMBRES", "APELLIDOS", "EMAIL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMarcas.setComponentPopupMenu(UD);
        tblMarcas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jScrollPane1.setViewportView(tblMarcas);

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
                        .addGap(40, 40, 40)
                        .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(286, 286, 286)
                .addComponent(lblMarca)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addComponent(lblMarca)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lblMarcaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMarcaMouseClicked
        // TODO add your handling code here:
        saveMarca();
    }//GEN-LAST:event_lblMarcaMouseClicked

    private void EditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditarActionPerformed
        updateMarca();
    }//GEN-LAST:event_EditarActionPerformed

    private void EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarActionPerformed
        deleteMarca();
    }//GEN-LAST:event_EliminarActionPerformed

    private void txtMarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMarcaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMarcaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Editar;
    private javax.swing.JMenuItem Eliminar;
    private javax.swing.JPopupMenu UD;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JTable tblMarcas;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtMarca;
    // End of variables declaration//GEN-END:variables
}
