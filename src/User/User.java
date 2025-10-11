/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package User;

import Connection.Conexion;
import java.awt.Color;
import java.awt.Font;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author kylie
 */
public class User extends javax.swing.JInternalFrame {

    /**
     * Creates new form User
     */
    public User() {
        initComponents();
        setClosable(true);
        roles();
        showUsers();
        clearUsers();
    }

    Conexion Conex = new Conexion();
    Connection Cn = Conex.Conexion();
    private int idUserSelected = -1;
    private boolean edit = false;

    private void roles() {
        try {
            String sql = "{CALL sp_inventario_vida(?, NULL, NULL, NULL, NULL, NULL, NULL, NULL)}";
            CallableStatement cs = Cn.prepareCall(sql);
            cs.setString(1, "SR"); // SR = Select Roles
            ResultSet rs = cs.executeQuery();

            cmbRole.removeAllItems();
            while (rs.next()) {
                cmbRole.addItem(rs.getInt("id_rol") + " - " + rs.getString("nombre_rol"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar roles: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        if (txtNames.getText().isEmpty()
                || txtLastName.getText().isEmpty()
                || txtEmail.getText().isEmpty()
                || txtPassword.getPassword().length == 0
                || cmbRole.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String email = txtEmail.getText();
        String regexEmail = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (!email.matches(regexEmail)) {
            JOptionPane.showMessageDialog(this, "El correo ingresado no es válido", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void saveUser() {
        if (!validateFields()) {
            return;
        }

        try {
            String optionSelected = cmbRole.getSelectedItem().toString();
            int idRol = Integer.parseInt(optionSelected.split(" - ")[0]);

            String pass = new String(txtPassword.getPassword());
            String hashedPass = BCrypt.hashpw(pass, BCrypt.gensalt());

            String sql = "{CALL sp_inventario_vida(?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement cs = Cn.prepareCall(sql);
            if (edit) {
                cs.setString(1, "UU");
                cs.setInt(2, idUserSelected);
            } else {
                cs.setString(1, "IU");
                cs.setObject(2, null);
            }

            cs.setInt(3, idRol);
            cs.setString(4, txtNames.getText());
            cs.setString(5, txtLastName.getText());
            cs.setString(6, txtEmail.getText());
            cs.setString(7, hashedPass);
            cs.setBoolean(8, true);

            cs.execute();

            JOptionPane.showMessageDialog(this, edit
                    ? "✅ Usuario actualizado correctamente"
                    : "✅ Usuario guardado correctamente");

            showUsers();
            clearUsers();
            edit = false;
            idUserSelected = -1;
            lblSave.setText("GUARDAR");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showUsers() {
        JTableHeader header = tblUsers.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(new Color(0x20, 0x41, 0x94));
        header.setBackground(new Color(220, 220, 220));
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "ROL", "NOMBRES", "APELLIDOS", "CORREO"}, 0);
        try {
            String sql = "{CALL sp_inventario_vida(?, NULL, NULL, NULL, NULL, NULL, NULL, NULL)}";
            CallableStatement cs = Cn.prepareCall(sql);
            cs.setString(1, "SU"); // Select Users
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                if (rs.getBoolean("estado")) { // Solo activos
                    model.addRow(new Object[]{
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_rol"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("correo")
                    });
                }
            }

            tblUsers.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al mostrar usuarios: " + e.getMessage());
        }
    }

    private void updateUser() {
        int fila = tblUsers.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para editar");
            return;
        }

        idUserSelected = (int) tblUsers.getValueAt(fila, 0);
        edit = true;

        txtNames.setText((String) tblUsers.getValueAt(fila, 2));
        txtLastName.setText((String) tblUsers.getValueAt(fila, 3));
        txtEmail.setText((String) tblUsers.getValueAt(fila, 4));

        String rolTabla = (String) tblUsers.getValueAt(fila, 1);
        for (int i = 0; i < cmbRole.getItemCount(); i++) {
            if (cmbRole.getItemAt(i).contains(rolTabla)) {
                cmbRole.setSelectedIndex(i);
                break;
            }
        }
        lblSave.setText("ACTUALIZAR");
    }

    private void deleteUser() {
        int fila = tblUsers.getSelectedRow();
        if (fila == -1) {
            return;
        }

        int idUsuario = (int) tblUsers.getValueAt(fila, 0);
        String nombres = (String) tblUsers.getValueAt(fila, 2);
        String apellidos = (String) tblUsers.getValueAt(fila, 3);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar al usuario " + nombres + " " + apellidos + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "{CALL sp_inventario_vida(?, ?, NULL, NULL, NULL, NULL, NULL, NULL)}";
                CallableStatement cs = Cn.prepareCall(sql);
                cs.setString(1, "DU"); // Delete User
                cs.setInt(2, idUsuario);
                cs.execute();

                JOptionPane.showMessageDialog(this, "✅ Usuario eliminado correctamente");
                showUsers();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar usuario: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

private void clearUsers() {
    txtNames.setText("");
    txtLastName.setText("");
    txtEmail.setText("");
    txtPassword.setText("");
    if (cmbRole.getItemCount() > 0) { // Verificación
        cmbRole.setSelectedIndex(0);
    }
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
        txtLastName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        cmbRole = new javax.swing.JComboBox<>();
        lblSave = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        chkShowPass = new javax.swing.JCheckBox();

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
        jLabel1.setText("Usuarios");

        jLabel2.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(32, 65, 148));
        jLabel2.setText("Nombres");

        txtNames.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        txtNames.setForeground(new java.awt.Color(32, 65, 148));
        txtNames.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));
        txtNames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamesActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(32, 65, 148));
        jLabel3.setText("Apellidos");

        txtLastName.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        txtLastName.setForeground(new java.awt.Color(32, 65, 148));
        txtLastName.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));

        jLabel4.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(32, 65, 148));
        jLabel4.setText("Email");

        txtEmail.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        txtEmail.setForeground(new java.awt.Color(32, 65, 148));
        txtEmail.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));

        jLabel5.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(32, 65, 148));
        jLabel5.setText("Contraseña");

        txtPassword.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        txtPassword.setForeground(new java.awt.Color(32, 65, 148));
        txtPassword.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(32, 65, 148)));

        jLabel6.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(32, 65, 148));
        jLabel6.setText("Rol");

        cmbRole.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        cmbRole.setForeground(new java.awt.Color(32, 65, 148));
        cmbRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbRoleActionPerformed(evt);
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

        tblUsers.setFont(new java.awt.Font("Century Gothic", 0, 16)); // NOI18N
        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
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
        tblUsers.setComponentPopupMenu(UD);
        tblUsers.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jScrollPane1.setViewportView(tblUsers);

        chkShowPass.setBackground(new java.awt.Color(255, 255, 255));
        chkShowPass.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        chkShowPass.setForeground(new java.awt.Color(32, 65, 148));
        chkShowPass.setText("Mostrar Contraseña");
        chkShowPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowPassActionPerformed(evt);
            }
        });

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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtNames, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3))
                            .addComponent(cmbRole, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkShowPass))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(286, 286, 286)
                .addComponent(lblSave)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(31, 31, 31)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNames, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(cmbRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(chkShowPass)
                        .addGap(41, 41, 41)))
                .addComponent(lblSave)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void lblSaveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSaveMouseClicked
        // TODO add your handling code here:
        saveUser();
    }//GEN-LAST:event_lblSaveMouseClicked

    private void EditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditarActionPerformed
        updateUser();
    }//GEN-LAST:event_EditarActionPerformed

    private void EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarActionPerformed
        deleteUser();
    }//GEN-LAST:event_EliminarActionPerformed

    private void chkShowPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowPassActionPerformed
        if (chkShowPass.isSelected()) {
            txtPassword.setEchoChar((char) 0); // Muestra el texto
        } else {
            txtPassword.setEchoChar('•');    // Oculta el texto
        }
    }//GEN-LAST:event_chkShowPassActionPerformed

    private void txtNamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamesActionPerformed

    private void cmbRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRoleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbRoleActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Editar;
    private javax.swing.JMenuItem Eliminar;
    private javax.swing.JPopupMenu UD;
    private javax.swing.JCheckBox chkShowPass;
    private javax.swing.JComboBox<String> cmbRole;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSave;
    private javax.swing.JTable tblUsers;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtLastName;
    private javax.swing.JTextField txtNames;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}
