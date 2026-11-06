/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.pos.ui;

/**
 *
 * @author ramel
 */
import com.pos.service.UserService;
import com.pos.model.User;
import com.pos.service.SecurityLogService;
public class Login extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Login.class.getName());
    

   
    public Login() {
        initComponents();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);

          setLocationRelativeTo(null); //
   
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        rootPanel = new javax.swing.JPanel();
        pnlLoginCard = new javax.swing.JPanel();
        lblUsername = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        TXTUSERNAME = new javax.swing.JTextField();
        TXTPASSWORD = new javax.swing.JPasswordField();
        btnSignIn = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        rootPanel.setBackground(new java.awt.Color(15, 23, 42));
        rootPanel.setLayout(new java.awt.GridBagLayout());

        pnlLoginCard.setBackground(new java.awt.Color(255, 255, 255));
        pnlLoginCard.setForeground(new java.awt.Color(30, 41, 59));
        pnlLoginCard.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        pnlLoginCard.setMinimumSize(new java.awt.Dimension(6, 6));
        pnlLoginCard.setPreferredSize(new java.awt.Dimension(1, 4));

        lblUsername.setBackground(new java.awt.Color(100, 116, 139));
        lblUsername.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(51, 65, 85));
        lblUsername.setText("Username:");

        lblPassword.setBackground(new java.awt.Color(0, 0, 0));
        lblPassword.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPassword.setForeground(new java.awt.Color(51, 65, 85));
        lblPassword.setText("Password:");

        TXTUSERNAME.setBackground(new java.awt.Color(248, 250, 252));
        TXTUSERNAME.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        TXTUSERNAME.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 208, 208)), javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        TXTUSERNAME.addActionListener(this::TXTUSERNAMEActionPerformed);

        TXTPASSWORD.setBackground(new java.awt.Color(248, 250, 252));
        TXTPASSWORD.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        TXTPASSWORD.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 208, 208), 2), javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        TXTPASSWORD.addActionListener(this::TXTPASSWORDActionPerformed);

        btnSignIn.setBackground(new java.awt.Color(0, 0, 102));
        btnSignIn.setForeground(new java.awt.Color(255, 255, 255));
        btnSignIn.setText("Sign In");
        btnSignIn.addActionListener(this::btnSignInActionPerformed);

        btnReset.setBackground(new java.awt.Color(204, 204, 204));
        btnReset.setText("Reset");
        btnReset.addActionListener(this::btnResetActionPerformed);

        lblTitle.setBackground(new java.awt.Color(0, 20, 153));
        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(0, 0, 128));
        lblTitle.setText("Beauty Store POS");

        lblSubtitle.setForeground(new java.awt.Color(100, 116, 139));
        lblSubtitle.setText("      Inventory Login");

        jSeparator1.setForeground(new java.awt.Color(226, 232, 240));

        javax.swing.GroupLayout pnlLoginCardLayout = new javax.swing.GroupLayout(pnlLoginCard);
        pnlLoginCard.setLayout(pnlLoginCardLayout);
        pnlLoginCardLayout.setHorizontalGroup(
            pnlLoginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLoginCardLayout.createSequentialGroup()
                .addGroup(pnlLoginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlLoginCardLayout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addGroup(pnlLoginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblUsername)
                            .addComponent(lblPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(55, 55, 55)
                        .addGroup(pnlLoginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlLoginCardLayout.createSequentialGroup()
                                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(90, 90, 90)
                                .addComponent(btnSignIn, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(TXTPASSWORD, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TXTUSERNAME, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlLoginCardLayout.createSequentialGroup()
                        .addGap(198, 198, 198)
                        .addComponent(lblTitle))
                    .addGroup(pnlLoginCardLayout.createSequentialGroup()
                        .addGap(230, 230, 230)
                        .addComponent(lblSubtitle)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLoginCardLayout.createSequentialGroup()
                .addGap(0, 55, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );
        pnlLoginCardLayout.setVerticalGroup(
            pnlLoginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLoginCardLayout.createSequentialGroup()
                .addGroup(pnlLoginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlLoginCardLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lblTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblSubtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addGroup(pnlLoginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblUsername)
                            .addComponent(TXTUSERNAME, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(43, 43, 43)
                        .addComponent(TXTPASSWORD, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48)
                        .addGroup(pnlLoginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSignIn, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlLoginCardLayout.createSequentialGroup()
                        .addGap(209, 209, 209)
                        .addComponent(lblPassword)))
                .addContainerGap(81, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 573;
        gridBagConstraints.ipady = 412;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(186, 471, 312, 510);
        rootPanel.add(pnlLoginCard, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rootPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
     
    private void TXTUSERNAMEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TXTUSERNAMEActionPerformed
    TXTPASSWORD.requestFocus();        // TODO add your handling code here:
    }//GEN-LAST:event_TXTUSERNAMEActionPerformed

    private void btnSignInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSignInActionPerformed
 String username = TXTUSERNAME.getText().trim();
    String password = new String(TXTPASSWORD.getPassword()).trim();

    UserService userService = new UserService();
    User user = userService.authenticate(username, password);

   if (user != null) {

    SecurityLogService.logAction(
        username,
        user.getRole(),
        "Login",
        "User logged in successfully"
    );

    javax.swing.JOptionPane.showMessageDialog(this,
        "Welcome, " + user.getFullName() + "!\nRole: " + user.getRole(),
        "Login Successful",
        javax.swing.JOptionPane.INFORMATION_MESSAGE);

    Dashboard dashboard = new Dashboard(user);
    dashboard.setVisible(true);
    this.dispose();

} else {

    SecurityLogService.logAction(
        username,
        "Unknown",
        "Failed Login",
        "Failed login attempt"
    );

    javax.swing.JOptionPane.showMessageDialog(this,
        "Invalid username or password.",
        "Login Failed",
        javax.swing.JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_btnSignInActionPerformed
    
    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
TXTUSERNAME.setText("");
    TXTPASSWORD.setText("");
    TXTUSERNAME.requestFocus();
    
        // TODO add your handling code here:
    }//GEN-LAST:event_btnResetActionPerformed

    private void TXTPASSWORDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TXTPASSWORDActionPerformed
    btnSignInActionPerformed(evt);
    }//GEN-LAST:event_TXTPASSWORDActionPerformed
   
    /**
     * @param args the command line arguments
     */
  
    public static void main(String args[]) {
        
        
        
        /* Set the Nimbus look and feel */  
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPasswordField TXTPASSWORD;
    private javax.swing.JTextField TXTUSERNAME;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSignIn;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel pnlLoginCard;
    private javax.swing.JPanel rootPanel;
    // End of variables declaration//GEN-END:variables
}
