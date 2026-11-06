/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.pos.ui;

/**
 *
 * @author ramel
 */
import com.pos.ui.panel.ReadReport;
import com.pos.dao.DiscountDAO;
import com.pos.ui.dialog.ConfirmPayment;
import com.pos.ui.panel.SecurityLogs;
import com.pos.ui.panel.SalesSummary;
import com.pos.ui.panel.MyAccount;
import com.pos.ui.panel.Employees;
import com.pos.ui.panel.ExpensesPanel;
import com.pos.ui.panel.InventoryPanel;
import com.pos.ui.panel.SalesPanel;
import javax.swing.JTextPane;
import com.pos.model.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.CardLayout;
import javax.swing.table.DefaultTableModel;
import com.pos.config.ConnectDb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.Dimension;
import com.pos.model.Product;
import com.pos.model.Cart;
import com.pos.model.CartItem;
import com.pos.model.Discount;
import com.pos.model.NoDiscount;
import com.pos.model.SeniorPwdDiscount;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import com.pos.service.InventoryService;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.Insets;
import com.pos.service.SecurityLogService;
import com.pos.service.DiscountService;
import com.pos.ui.dialog.ReceiptDialog;
import com.pos.service.ActionHistoryService;
import javax.swing.text.JTextComponent;

public class Dashboard extends javax.swing.JFrame {
    
    
    private CardLayout cardLayout;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Dashboard.class.getName());
    private User currentUser;
    private SalesSummary salesSummary;
    private SalesPanel salesPanel;
    private ExpensesPanel expensesPanel;
    private double currentTotalDue = 0;
    private final Cart cart = new Cart();
    private JPanel modernCartPanel;
    private InventoryPanel inventoryPanel;
    private CartItem selectedCartItem;
    private SecurityLogs securityLogsPanel;
    
    
    
    public User getCurrentUser() {
    return currentUser;    
    }
    
    
    
    private void setupRolePermissions() {
    String role = currentUser.getRole();

    // Hide restricted navigation buttons
    btnNavPos.setVisible(false);
    btnNavProducts.setVisible(false);
    btnNavEmployees.setVisible(false);
    btnNavExpenses.setVisible(false);
    btnNavSales.setVisible(false);
    btnNavSecurity.setVisible(false);
    btnNavReport.setVisible(false);

    // =========================
    // CASHIER
    // =========================
    if (role.equalsIgnoreCase("Cashier")) {

        btnNavPos.setVisible(true);
        btnNavProducts.setVisible(false);
        
        btnNavExpenses.setVisible(true);
    }
    // =========================
    // SHIFT SUPERVISOR
    // =========================
    else if (role.equalsIgnoreCase("Shift Supervisor")) {

        btnNavPos.setVisible(true);
        btnNavProducts.setVisible(true);
        btnNavExpenses.setVisible(true);
        btnNavSales.setVisible(true);
        btnNavSecurity.setVisible(true);
        btnNavReport.setVisible(true);
    }
    // =========================
    // STORE MANAGER / ADMIN
    // =========================
    else if (role.equalsIgnoreCase("Store Manager")
            || role.equalsIgnoreCase("Admin")) {

        btnNavPos.setVisible(true);
        btnNavProducts.setVisible(true);
        btnNavEmployees.setVisible(true);
        btnNavExpenses.setVisible(true);
        btnNavSales.setVisible(true);
        btnNavSecurity.setVisible(true);
        btnNavReport.setVisible(true);
    }

    pnlSidebar.revalidate();
    pnlSidebar.repaint();
    
    }
    
    
   private void setupSidebar() {
    pnlSidebar.removeAll();
    pnlSidebar.setLayout(new BorderLayout());
    pnlSidebar.setBackground(new Color(18, 18, 58));

    // Navigation buttons at the top
    JPanel navPanel = new JPanel();
    navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
    navPanel.setBackground(new Color(18, 18, 58));
    navPanel.setBorder(
        BorderFactory.createEmptyBorder(20, 20, 10, 20)
    );

    JButton[] navButtons = {
        btnNavPos,
        btnNavProducts,
        btnNavEmployees,
        btnNavExpenses,
        btnNavSales,
        btnNavReport,
        btnNavSecurity
    };

    Dimension buttonSize = new Dimension(168, 45);

    for (JButton button : navButtons) {
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        navPanel.add(button);
        navPanel.add(Box.createVerticalStrut(10));
    }

    // Account buttons at the bottom
    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
    bottomPanel.setBackground(new Color(18, 18, 58));
    bottomPanel.setBorder(
        BorderFactory.createEmptyBorder(10, 20, 20, 20)
    );

    JButton[] bottomButtons = {
        jButton9,
        btnSignOut
    };

    for (JButton button : bottomButtons) {
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        bottomPanel.add(button);
        bottomPanel.add(Box.createVerticalStrut(10));
    }

    pnlSidebar.add(navPanel, BorderLayout.NORTH);
    pnlSidebar.add(bottomPanel, BorderLayout.SOUTH);

    pnlSidebar.revalidate();
    pnlSidebar.repaint();
}
     
     
     
     public Dashboard(User user) {
     initComponents();
     setupCartLayout();
     this.currentUser = user;
    
     
     
    // =========================
    // CARD LAYOUT
    // =========================

     
       this.currentUser = user;

    cardLayout = new CardLayout();
    pnlMainContent.setLayout(cardLayout);

    salesPanel = new SalesPanel(this);
    pnlMainContent.add(salesPanel, "sales");

    inventoryPanel = new InventoryPanel(this);
    pnlMainContent.add(inventoryPanel, "inventory");

    expensesPanel = new ExpensesPanel(this);
    pnlMainContent.add(expensesPanel, "expenses");

    salesSummary = new SalesSummary();
    pnlMainContent.add(salesSummary, "salesSummary");

    securityLogsPanel = new SecurityLogs();
    pnlMainContent.add(securityLogsPanel, "securityLogs");
    
   

    // Now inventoryPanel already exists
    javax.swing.SwingUtilities.invokeLater(() -> {
        inventoryPanel.checkExpiringProducts();
    });
        
     
     
     setupSidebar();
     setupRolePermissions();
     setupCartTable();
     setupModernCart();
         
      jScrollPane1.getVerticalScrollBar().setPreferredSize(
        new Dimension(0, 0)
      );
     tblCart.getColumnModel().getColumn(0).setPreferredWidth(150); // Product
     tblCart.getColumnModel().getColumn(1).setPreferredWidth(50);  // Qty
     tblCart.getColumnModel().getColumn(2).setPreferredWidth(80);  // Price
     tblCart.getColumnModel().getColumn(3).setPreferredWidth(90);  // Total

     tblCart.getColumnModel().getColumn(0).setMinWidth(120);
     tblCart.getColumnModel().getColumn(1).setMinWidth(40);
     tblCart.getColumnModel().getColumn(2).setMinWidth(70);
     tblCart.getColumnModel().getColumn(3).setMinWidth(70);
     jScrollPane1.setHorizontalScrollBarPolicy(
     javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
     );
     jScrollPane1.setVerticalScrollBarPolicy(
     javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
     );
     javax.swing.table.DefaultTableCellRenderer centerRenderer =
        new javax.swing.table.DefaultTableCellRenderer();

     centerRenderer.setHorizontalAlignment(
        javax.swing.SwingConstants.CENTER
);

     tblCart.getColumnModel().getColumn(1)
         .setCellRenderer(centerRenderer);

    tblCart.getColumnModel().getColumn(2)
         .setCellRenderer(centerRenderer);

     tblCart.getColumnModel().getColumn(3)
         .setCellRenderer(centerRenderer);

        
    jLabel1.setText("Staff: " + user.getFullName() + " (" + user.getRole() + ")");
    
    
 
    
    
    
    // =========================
    // CART TABLE
    // =========================

    pnlCart.setVisible(true);

 
  
    // =========================
    // CLOCK
    // =========================

    lblTime.setText(
        LocalDateTime.now().format(
            DateTimeFormatter.ofPattern(
                "EEE, MMM dd, yyyy | hh:mm:ss a"
            )
        )
    );

    Timer liveClockTimer = new Timer(1000, e -> {
        lblTime.setText(
            LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(
                    "EEE, MMM dd, yyyy | hh:mm:ss a"
                )
            )
        );
    });

    liveClockTimer.setInitialDelay(0);
    liveClockTimer.start();
   


javax.swing.KeyStroke undoKey = javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_Z,
        java.awt.event.InputEvent.CTRL_DOWN_MASK
);

javax.swing.KeyStroke redoKey = javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_Y,
        java.awt.event.InputEvent.CTRL_DOWN_MASK
);

RootPanel.getInputMap(
        javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
).put(undoKey, "globalUndo");

RootPanel.getActionMap().put("globalUndo",
        new javax.swing.AbstractAction() {
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {

        // Keep normal text editing shortcuts working.
        if (java.awt.KeyboardFocusManager
                .getCurrentKeyboardFocusManager()
                .getFocusOwner() instanceof JTextComponent) {
            return;
        }

        try {
            boolean undone = ActionHistoryService.undoLast(currentUser);

            if (undone) {
                JOptionPane.showMessageDialog(
                        Dashboard.this,
                        "Last recorded action undone.",
                        "Undo",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        Dashboard.this,
                        "There are no supported actions to undo.",
                        "Undo",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    Dashboard.this,
                    "Undo failed:\n" + ex.getMessage(),
                    "Undo Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
});

RootPanel.getInputMap(
        javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
).put(redoKey, "globalRedo");

RootPanel.getActionMap().put("globalRedo",
        new javax.swing.AbstractAction() {
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {

        if (java.awt.KeyboardFocusManager
                .getCurrentKeyboardFocusManager()
                .getFocusOwner() instanceof JTextComponent) {
            return;
        }

        try {
            boolean redone = ActionHistoryService.redoLast(currentUser);

            if (redone) {
                JOptionPane.showMessageDialog(
                        Dashboard.this,
                        "Last undone action restored.",
                        "Redo",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        Dashboard.this,
                        "There are no supported actions to redo.",
                        "Redo",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    Dashboard.this,
                    "Redo failed:\n" + ex.getMessage(),
                    "Redo Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
});



    setExtendedState(Dashboard.MAXIMIZED_BOTH);
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
     
     private void setupCartLayout() {
    jPanel1.removeAll();
    jPanel1.setLayout(new BorderLayout(0, 6));
    jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

    jLabel4.setText("No Discount");

    JPanel south = new JPanel();
    south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
    south.setBackground(Color.WHITE);

    jSeparator2.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    jSeparator1.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

    south.add(jSeparator2);
    south.add(Box.createVerticalStrut(4));
    south.add(totalsRow(jLabel3, lblSubtotal));
    south.add(totalsRow(jLabel4, lblDiscount));
    south.add(totalsRow(jLabel5, lblVAT));
    south.add(totalsRow(jLabel6, lblTotalDue));
    south.add(Box.createVerticalStrut(4));
    south.add(jSeparator1);
    south.add(Box.createVerticalStrut(8));

    JPanel actions = new JPanel(new java.awt.GridLayout(1, 3, 8, 0));
    actions.setBackground(Color.WHITE);
    actions.add(ButtonDisc);
    actions.add(ButtonRemove);
    actions.add(ButtonClear);
    fixHeight(actions, 32);
    south.add(actions);
    south.add(Box.createVerticalStrut(10));

    JPanel complete = new JPanel(new BorderLayout());
    complete.setBackground(Color.WHITE);
    ButtonComp.setFont(new Font("Segoe UI", Font.BOLD, 15));
    complete.add(ButtonComp, BorderLayout.CENTER);
    fixHeight(complete, 48);
    south.add(complete);

    jPanel1.add(jLabel2, BorderLayout.NORTH);
    jPanel1.add(jScrollPane1, BorderLayout.CENTER);
    jPanel1.add(south, BorderLayout.SOUTH);
}

private JPanel totalsRow(javax.swing.JLabel left, javax.swing.JLabel right) {
    JPanel row = new JPanel(new BorderLayout());
    row.setBackground(Color.WHITE);
    row.add(left, BorderLayout.WEST);
    row.add(right, BorderLayout.EAST);
    fixHeight(row, 28);
    return row;
}

private void fixHeight(javax.swing.JComponent c, int h) {
    c.setPreferredSize(new Dimension(0, h));
    c.setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
    c.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
}
     private void setupCartTable() {

}
     
    private void setupModernCart() {

    modernCartPanel = new JPanel();

    modernCartPanel.setLayout(
        new BoxLayout(modernCartPanel, BoxLayout.Y_AXIS)
    );

    modernCartPanel.setBackground(Color.WHITE);

    modernCartPanel.setBorder(
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    );

    jScrollPane1.setViewportView(modernCartPanel);

    jScrollPane1.setBorder(
        BorderFactory.createLineBorder(new Color(225, 225, 225))
    );

    jScrollPane1.setHorizontalScrollBarPolicy(
        javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );

    jScrollPane1.setVerticalScrollBarPolicy(
        javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
    );

    refreshCartTable();
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        RootPanel = new javax.swing.JPanel();
        pnlTopBar = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btnSignOut2 = new javax.swing.JButton();
        pnlSidebar = new javax.swing.JPanel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 19), new java.awt.Dimension(0, 19), new java.awt.Dimension(0, 19));
        btnNavPos = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        btnNavProducts = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        btnNavEmployees = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        btnNavExpenses = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        btnNavSales = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        btnNavReport = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        btnNavSecurity = new javax.swing.JButton();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        jButton9 = new javax.swing.JButton();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        btnSignOut = new javax.swing.JButton();
        pnlCart = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCart = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ButtonDisc = new javax.swing.JButton();
        ButtonClear = new javax.swing.JButton();
        ButtonComp = new javax.swing.JButton();
        lblSubtotal = new javax.swing.JLabel();
        lblDiscount = new javax.swing.JLabel();
        lblVAT = new javax.swing.JLabel();
        lblTotalDue = new javax.swing.JLabel();
        ButtonRemove = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        pnlMainContent = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        RootPanel.setBackground(new java.awt.Color(27, 40, 56));
        RootPanel.setLayout(new java.awt.BorderLayout());

        pnlTopBar.setBackground(new java.awt.Color(27, 31, 92));
        pnlTopBar.setPreferredSize(new java.awt.Dimension(0, 60));

        lblTitle.setBackground(new java.awt.Color(255, 255, 255));
        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("       POS AND INVENTORY SYSTEM");

        lblTime.setForeground(new java.awt.Color(255, 255, 255));
        lblTime.setText("THURSDAY, SEP 16, 2026 2:13:46 AM");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(56, 219, 248));
        jLabel1.setText("Staff: Operator Name (Role)");

        btnSignOut2.setBackground(new java.awt.Color(255, 0, 0));
        btnSignOut2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSignOut2.setForeground(new java.awt.Color(255, 255, 255));
        btnSignOut2.setText("Sign Out");
        btnSignOut2.addActionListener(this::btnSignOut2ActionPerformed);

        javax.swing.GroupLayout pnlTopBarLayout = new javax.swing.GroupLayout(pnlTopBar);
        pnlTopBar.setLayout(pnlTopBarLayout);
        pnlTopBarLayout.setHorizontalGroup(
            pnlTopBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTopBarLayout.createSequentialGroup()
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 761, Short.MAX_VALUE)
                .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(btnSignOut2)
                .addGap(27, 27, 27))
        );
        pnlTopBarLayout.setVerticalGroup(
            pnlTopBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTopBarLayout.createSequentialGroup()
                .addGroup(pnlTopBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTopBarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlTopBarLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(pnlTopBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSignOut2)
                            .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 17, Short.MAX_VALUE)))
                .addContainerGap())
        );

        RootPanel.add(pnlTopBar, java.awt.BorderLayout.NORTH);

        pnlSidebar.setBackground(new java.awt.Color(18, 18, 58));
        pnlSidebar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlSidebar.setMinimumSize(new java.awt.Dimension(109, 23));
        pnlSidebar.setPreferredSize(new java.awt.Dimension(200, 0));

        btnNavPos.setBackground(new java.awt.Color(51, 65, 85));
        btnNavPos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNavPos.setForeground(new java.awt.Color(255, 255, 255));
        btnNavPos.setText("POS");
        btnNavPos.setDoubleBuffered(true);
        btnNavPos.setMaximumSize(new java.awt.Dimension(200, 45));
        btnNavPos.setPreferredSize(new java.awt.Dimension(200, 45));
        btnNavPos.addActionListener(this::btnNavPosActionPerformed);

        btnNavProducts.setBackground(new java.awt.Color(51, 65, 85));
        btnNavProducts.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNavProducts.setForeground(new java.awt.Color(255, 255, 255));
        btnNavProducts.setText("Inventory");
        btnNavProducts.setMaximumSize(new java.awt.Dimension(200, 50));
        btnNavProducts.setPreferredSize(new java.awt.Dimension(200, 0));
        btnNavProducts.addActionListener(this::btnNavProductsActionPerformed);

        btnNavEmployees.setBackground(new java.awt.Color(51, 65, 85));
        btnNavEmployees.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNavEmployees.setForeground(new java.awt.Color(255, 255, 255));
        btnNavEmployees.setText("Employees");
        btnNavEmployees.setMaximumSize(new java.awt.Dimension(200, 50));
        btnNavEmployees.setPreferredSize(new java.awt.Dimension(200, 0));
        btnNavEmployees.addActionListener(this::btnNavEmployeesActionPerformed);

        btnNavExpenses.setBackground(new java.awt.Color(51, 65, 85));
        btnNavExpenses.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNavExpenses.setForeground(new java.awt.Color(255, 255, 255));
        btnNavExpenses.setText("Expenses");
        btnNavExpenses.setMaximumSize(new java.awt.Dimension(200, 50));
        btnNavExpenses.addActionListener(this::btnNavExpensesActionPerformed);

        btnNavSales.setBackground(new java.awt.Color(51, 65, 85));
        btnNavSales.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNavSales.setForeground(new java.awt.Color(255, 255, 255));
        btnNavSales.setText("Sales Summary");
        btnNavSales.setMaximumSize(new java.awt.Dimension(200, 50));
        btnNavSales.addActionListener(this::btnNavSalesActionPerformed);

        btnNavReport.setBackground(new java.awt.Color(51, 65, 85));
        btnNavReport.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNavReport.setForeground(new java.awt.Color(255, 255, 255));
        btnNavReport.setText("Z-Read Report");
        btnNavReport.setMaximumSize(new java.awt.Dimension(200, 50));
        btnNavReport.addActionListener(this::btnNavReportActionPerformed);

        btnNavSecurity.setBackground(new java.awt.Color(51, 65, 85));
        btnNavSecurity.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNavSecurity.setForeground(new java.awt.Color(255, 255, 255));
        btnNavSecurity.setText("Security Logs");
        btnNavSecurity.setMaximumSize(new java.awt.Dimension(200, 50));
        btnNavSecurity.addActionListener(this::btnNavSecurityActionPerformed);

        jButton9.setBackground(new java.awt.Color(51, 65, 85));
        jButton9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("My Account");
        jButton9.setMaximumSize(new java.awt.Dimension(200, 50));
        jButton9.addActionListener(this::jButton9ActionPerformed);

        btnSignOut.setBackground(new java.awt.Color(255, 0, 0));
        btnSignOut.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSignOut.setForeground(new java.awt.Color(255, 255, 255));
        btnSignOut.setText("Sign Out");
        btnSignOut.setMaximumSize(new java.awt.Dimension(200, 50));
        btnSignOut.addActionListener(this::btnSignOutActionPerformed);

        javax.swing.GroupLayout pnlSidebarLayout = new javax.swing.GroupLayout(pnlSidebar);
        pnlSidebar.setLayout(pnlSidebarLayout);
        pnlSidebarLayout.setHorizontalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlSidebarLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnNavReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNavSales, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                            .addComponent(btnNavExpenses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNavEmployees, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnNavProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnNavPos, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnNavSecurity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSignOut, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(20, 20, 20))
        );
        pnlSidebarLayout.setVerticalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addComponent(filler7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnNavPos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnNavProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnNavEmployees, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnNavExpenses, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnNavSales, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnNavReport, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnNavSecurity, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnSignOut, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        RootPanel.add(pnlSidebar, java.awt.BorderLayout.LINE_START);

        pnlCart.setBackground(new java.awt.Color(247, 247, 247));
        pnlCart.setName(""); // NOI18N
        pnlCart.setPreferredSize(new java.awt.Dimension(350, 0));
        pnlCart.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setMaximumSize(null);
        jScrollPane1.setMinimumSize(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(350, 430));

        tblCart.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblCart.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product", "Qty", "Price", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCart.setGridColor(new java.awt.Color(225, 225, 225));
        tblCart.setMaximumSize(new java.awt.Dimension(2147483647, 600));
        tblCart.setMinimumSize(new java.awt.Dimension(60, 600));
        tblCart.setName(""); // NOI18N
        tblCart.setPreferredSize(new java.awt.Dimension(300, 500));
        tblCart.setRowHeight(28);
        tblCart.setRowMargin(5);
        tblCart.setSelectionBackground(new java.awt.Color(255, 255, 255));
        tblCart.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblCart.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(tblCart);
        if (tblCart.getColumnModel().getColumnCount() > 0) {
            tblCart.getColumnModel().getColumn(0).setResizable(false);
            tblCart.getColumnModel().getColumn(1).setResizable(false);
            tblCart.getColumnModel().getColumn(2).setResizable(false);
            tblCart.getColumnModel().getColumn(3).setResizable(false);
        }

        jLabel2.setBackground(new java.awt.Color(245, 245, 245));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(30, 30, 30));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("CUSTOMER CART");

        jLabel3.setText("Subtotal:");

        jLabel4.setText("PWD/Senior Disc%");

        jLabel5.setText("VAT (12%)");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("TOTAL DUE:");

        ButtonDisc.setText("20% Disc");
        ButtonDisc.addActionListener(this::ButtonDiscActionPerformed);

        ButtonClear.setText("Clear");
        ButtonClear.addActionListener(this::ButtonClearActionPerformed);

        ButtonComp.setBackground(new java.awt.Color(0, 153, 0));
        ButtonComp.setForeground(new java.awt.Color(255, 255, 255));
        ButtonComp.setText("Complete Sale");
        ButtonComp.addActionListener(this::ButtonCompActionPerformed);

        lblSubtotal.setText("₱0.00");

        lblDiscount.setText("-₱0.00");

        lblVAT.setText("₱0.00");

        lblTotalDue.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotalDue.setForeground(new java.awt.Color(0, 51, 255));
        lblTotalDue.setText("₱0.00");

        ButtonRemove.setText("Remove");
        ButtonRemove.addActionListener(this::ButtonRemoveActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(28, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblVAT)
                                    .addComponent(lblDiscount)
                                    .addComponent(lblSubtotal)
                                    .addComponent(lblTotalDue))))
                        .addGap(24, 24, 24))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ButtonDisc, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ButtonRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ButtonClear, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(ButtonComp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 507, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lblSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblDiscount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblVAT))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblTotalDue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ButtonDisc, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                    .addComponent(ButtonRemove)
                    .addComponent(ButtonClear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonComp, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(82, 82, 82))
        );

        pnlCart.add(jPanel1, java.awt.BorderLayout.CENTER);

        RootPanel.add(pnlCart, java.awt.BorderLayout.LINE_END);

        pnlMainContent.setBackground(new java.awt.Color(247, 247, 247));

        javax.swing.GroupLayout pnlMainContentLayout = new javax.swing.GroupLayout(pnlMainContent);
        pnlMainContent.setLayout(pnlMainContentLayout);
        pnlMainContentLayout.setHorizontalGroup(
            pnlMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1001, Short.MAX_VALUE)
        );
        pnlMainContentLayout.setVerticalGroup(
            pnlMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 838, Short.MAX_VALUE)
        );

        RootPanel.add(pnlMainContent, java.awt.BorderLayout.CENTER);

        getContentPane().add(RootPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNavPosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavPosActionPerformed
      cardLayout.show(pnlMainContent, "sales");

    // Show customer cart on Sales Register
    pnlCart.setVisible(true);

    pnlMainContent.revalidate();
    pnlMainContent.repaint();

    RootPanel.revalidate();
    RootPanel.repaint();

    }//GEN-LAST:event_btnNavPosActionPerformed

    private void btnNavProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavProductsActionPerformed
    String role = currentUser.getRole();

    if (role.equalsIgnoreCase("Cashier")) {
        JOptionPane.showMessageDialog(
            this,
            "You do not have permission to access Inventory.",
            "Access Denied",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    cardLayout.show(pnlMainContent, "inventory");
    pnlCart.setVisible(false);

    pnlMainContent.revalidate();
    pnlMainContent.repaint();
    RootPanel.revalidate();
    RootPanel.repaint();
    }//GEN-LAST:event_btnNavProductsActionPerformed

    private void btnNavSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavSalesActionPerformed
    
    String role = currentUser.getRole();

    if (!role.equalsIgnoreCase("Shift Supervisor")
            && !role.equalsIgnoreCase("Store Manager")
            && !role.equalsIgnoreCase("Admin")) {

        JOptionPane.showMessageDialog(
            this,
            "You do not have permission to access Sales Summary.",
            "Access Denied",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    cardLayout.show(pnlMainContent, "salesSummary");

    pnlCart.setVisible(false);

    pnlMainContent.revalidate();
    pnlMainContent.repaint();

    RootPanel.revalidate();
    RootPanel.repaint();

    }//GEN-LAST:event_btnNavSalesActionPerformed

    private void btnNavReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavReportActionPerformed
String role = currentUser.getRole();

if (!role.equalsIgnoreCase("Shift Supervisor")
        && !role.equalsIgnoreCase("Store Manager")
        && !role.equalsIgnoreCase("Admin")) {

    JOptionPane.showMessageDialog(
            this,
            "You do not have permission to access Z-Read Report.",
            "Access Denied",
            JOptionPane.WARNING_MESSAGE
    );
    return;
}

ReadReport report = new ReadReport(
        this,
        true,
        currentUser.getUserId()
);

report.setTitle("Z-Read Report");
report.setLocationRelativeTo(this);
report.setVisible(true);
       }//GEN-LAST:event_btnNavReportActionPerformed

    private void btnNavSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavSecurityActionPerformed
cardLayout.show(pnlMainContent, "securityLogs");

    // Hide customer cart
    pnlCart.setVisible(false);

    // Refresh Security Logs
    if (securityLogsPanel != null) {
        securityLogsPanel.loadSecurityLogs();
    }

    pnlMainContent.revalidate();
    pnlMainContent.repaint();

    RootPanel.revalidate();
    RootPanel.repaint();
    }//GEN-LAST:event_btnNavSecurityActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
MyAccount account = new MyAccount(this, true, currentUser);
account.setVisible(true);       
    }//GEN-LAST:event_jButton9ActionPerformed

    private void btnSignOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSignOutActionPerformed
       int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to sign out and lock terminal?",
        "Sign Out Confirmation",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {

        SecurityLogService.logAction(
            currentUser.getUsername(),
            currentUser.getRole(),
            "Logout",
            "User logged out successfully"
        );

        new Login().setVisible(true);
        this.dispose();
    }
    }//GEN-LAST:event_btnSignOutActionPerformed

    private void btnSignOut2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSignOut2ActionPerformed
int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to sign out and lock terminal?",
            "Sign Out Confirmation",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {

        // Record logout activity
        SecurityLogService.logAction(
            currentUser.getUsername(),
            currentUser.getRole(),
            "Logout",
            "User logged out successfully"
        );

        // Return to Login Screen
        new Login().setVisible(true);
        this.dispose();
    }
    }//GEN-LAST:event_btnSignOut2ActionPerformed

    private void btnNavEmployeesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavEmployeesActionPerformed
    if (!currentUser.getRole().equalsIgnoreCase("Store Manager")
    && !currentUser.getRole().equalsIgnoreCase("Admin")) {
    JOptionPane.showMessageDialog(
            this,
            "You do not have permission to access Employee Management.",
            "Access Denied",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    Employees employeesDialog = new Employees(this, true);
    employeesDialog.setLocationRelativeTo(this);
    employeesDialog.setVisible(true);
    }//GEN-LAST:event_btnNavEmployeesActionPerformed

    private void ButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonClearActionPerformed
   cart.clear();
selectedCartItem = null;

ButtonDisc.setText("PWD/Senior Discount");
jLabel4.setText("PWD/Senior Discount");

refreshCartTable();   
    }//GEN-LAST:event_ButtonClearActionPerformed

    private void ButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonRemoveActionPerformed
       if (selectedCartItem == null) {

        JOptionPane.showMessageDialog(
            this,
            "Please select an item in the cart first.",
            "Remove Item",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    String productName =
        selectedCartItem.getProduct().getProductName();

    int confirm =
        JOptionPane.showConfirmDialog(
            this,
            "Remove " + productName + " from the cart?",
            "Remove Item",
            JOptionPane.YES_NO_OPTION
        );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }
    
     if (cart.remove(selectedCartItem)) {

     selectedCartItem = null;

     refreshCartTable();
    }
    
    }//GEN-LAST:event_ButtonRemoveActionPerformed

    private void ButtonDiscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonDiscActionPerformed

    DiscountService discountService = new DiscountService();

    java.util.List<Discount> discounts =
            discountService.getActiveDiscounts(currentUser);

    if (discounts.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "No active discounts available.",
                "Discount",
                JOptionPane.INFORMATION_MESSAGE
        );
        return;
    }

    Discount selected = (Discount) JOptionPane.showInputDialog(
            this,
            "Select Discount:",
            "Available Discounts",
            JOptionPane.PLAIN_MESSAGE,
            null,
            discounts.toArray(),
            discounts.get(0)
    );

    // Stop if the cashier cancels the selection.
    if (selected == null) {
        return;
    }

    // Apply the selected discount to the cart.
    cart.setDiscount(selected);

    // Show percentage only on the button.
    ButtonDisc.setText(
            String.format(
                    "%.0f%% Discount",
                    selected.getRate() * 100
            )
    );

    // Show the selected discount name and percentage.
    jLabel4.setText(
            selected.getName()
            + " ("
            + String.format(
                    "%.0f%%",
                    selected.getRate() * 100
            )
            + ")"
    );

    // Update the discount amount and total due.
    recalculateTotals();

    JOptionPane.showMessageDialog(
            this,
            selected.getName() + " discount applied."
    );
    }//GEN-LAST:event_ButtonDiscActionPerformed

    private void ButtonCompActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonCompActionPerformed
    if (cart.isEmpty()) {
        JOptionPane.showMessageDialog(
            this,
            "Your cart is empty.",
            "Empty Cart",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // Open Confirm Payment dialog
    ConfirmPayment payment =
        new ConfirmPayment(this, true, currentTotalDue);

    payment.setLocationRelativeTo(this);
    payment.setVisible(true);

    // If user cancelled payment
    if (!payment.isConfirmed()) {
        return;
    }

    // Get payment information from ConfirmPayment
    String paymentMethod = payment.getPaymentMethod();
    String referenceNumber = payment.getReferenceNumber();
    String bank = payment.getBank();
    double cashReceived = payment.getCashReceived();
    double change = payment.getChange();

    InventoryService inventoryService = new InventoryService();

    // =====================================================
    // STEP 1: CHECK STOCK FIRST
    // =====================================================

    for (CartItem item : cart.getItems()) {

        int piecesNeeded =
            item.getTotalPiecesToDeduct();

        int available =
            inventoryService.getTotalStockInPieces(
                item.getProduct().getProductId()
            );
        System.out.println(  "DEBUG - Product ID: " + item.getProduct().getProductId() + " | Pieces Needed: "+ piecesNeeded + " | Available: "+ available);

        if (piecesNeeded > available) {

            JOptionPane.showMessageDialog(
                this,
                "Not enough stock for "
                + item.getProduct().getProductName()
                + ".\n\nSale cancelled.",
                "Insufficient Stock",
                JOptionPane.WARNING_MESSAGE
            );

            salesPanel.refreshProducts();
            return;
        }
    }

    // =====================================================
// STEP 2 + STEP 3: DEDUCT STOCK + SAVE TRANSACTION
// ONE DATABASE TRANSACTION
// =====================================================

String invoiceNumber =
        "INV-" + System.currentTimeMillis();

String combinedMethod = paymentMethod;

if ("Bank Transfer".equalsIgnoreCase(paymentMethod)
        && bank != null
        && !bank.isEmpty()) {

    combinedMethod =
            paymentMethod + " - " + bank;
}

String insertTx =
        "INSERT INTO transactions "
        + "(invoice_number, user_id, customer_id, "
        + "subtotal_amount, discount_amount, final_amount, "
        + "cash_rendered, change_amount, payment_method, "
        + "transaction_date) "
        + "VALUES (?, ?, NULL, ?, ?, ?, ?, ?, ?, NOW())";

String insertDetail =
        "INSERT INTO transaction_details "
        + "(transaction_id, product_id, unit_type, quantity_sold, "
        + "total_pieces_deducted, unit_price, subtotal) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?)";

try (Connection conn = ConnectDb.getConnection()) {

    conn.setAutoCommit(false);

    try {

        // =================================================
        // STEP 2: DEDUCT STOCK USING FIFO
        // =================================================

        for (CartItem item : cart.getItems()) {

            System.out.println(
                "Product: "
                + item.getProduct().getProductName()
            );

            System.out.println(
                "Product ID: "
                + item.getProduct().getProductId()
            );

            System.out.println(
                "Pieces to deduct: "
                + item.getTotalPiecesToDeduct()
            );

            boolean ok =
                inventoryService.processFIFODeduction(
                    conn,
                    item.getProduct().getProductId(),
                    item.getTotalPiecesToDeduct()
                );

            if (!ok) {

                throw new SQLException(
                    "Stock deduction failed for "
                    + item.getProduct().getProductName()
                );
            }
        }

        // =================================================
        // STEP 3A: SAVE TRANSACTION
        // =================================================

        int newTransactionId;

        try (PreparedStatement ps =
                conn.prepareStatement(
                    insertTx,
                    Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(
                1,
                invoiceNumber
            );

            ps.setInt(
                2,
                currentUser.getUserId()
            );

            ps.setDouble(
                3,
                cart.getSubtotal()
            );
            
            System.out.println("===== TRANSACTION DEBUG =====");
            System.out.println("Subtotal: " + cart.getSubtotal());
            System.out.println("Discount: " + cart.getDiscountAmount());
            System.out.println("Total: " + cart.getTotal());
            System.out.println("Cash Received: " + cashReceived);
            System.out.println("Change: " + change);
            System.out.println("Payment: " + combinedMethod);
            System.out.println("=============================");

            ps.setDouble( 
                4,
                cart.getDiscountAmount()
            );

            ps.setDouble(
                5,
                cart.getTotal()
            );

            // Cash received
            ps.setDouble(
                6,
                "Cash".equalsIgnoreCase(paymentMethod)
                    ? cashReceived
                    : cart.getTotal()
            );

            // Change
            ps.setDouble(
                7,
                "Cash".equalsIgnoreCase(paymentMethod)
                    ? change
                    : 0.0
            );

            ps.setString(
                8,
                combinedMethod
            );

            ps.executeUpdate();

            // Get generated transaction ID
            try (ResultSet keys =
                    ps.getGeneratedKeys()) {

                if (!keys.next()) {

                    throw new SQLException(
                        "Failed to get new transaction ID."
                    );
                }

                newTransactionId =
                    keys.getInt(1);
            }
        }

        // =================================================
        // STEP 3B: SAVE TRANSACTION DETAILS
        // =================================================

        try (PreparedStatement ps =
                conn.prepareStatement(insertDetail)) {

            for (CartItem item :
                    cart.getItems()) {

                ps.setInt(
                    1,
                    newTransactionId
                );

                ps.setInt(
                    2,
                    item.getProduct().getProductId()
                );

                ps.setString(
                    3,
                    item.getUnitType()
                );

                ps.setInt(
                    4,
                    item.getQuantity()
                );

                ps.setInt(
                    5,
                    item.getTotalPiecesToDeduct()
                );

                ps.setDouble(
                    6,
                    item.getUnitPrice()
                );

                ps.setDouble(
                    7,
                    item.getSubtotal()
                );

                ps.addBatch();
            }

            ps.executeBatch();
        }

        // =================================================
        // EVERYTHING SUCCEEDED
        // =================================================

        conn.commit();

        // Security log AFTER successful transaction
        SecurityLogService.logAction(
            currentUser.getUsername(),
            currentUser.getRole(),
            "Process Sale",
            "Invoice: " + invoiceNumber
        );

    } catch (SQLException e) {

        // =================================================
        // SOMETHING FAILED → ROLLBACK EVERYTHING
        // =================================================

        try {
            conn.rollback();
        } catch (SQLException rollbackError) {

            System.err.println(
                "Rollback failed: "
                + rollbackError.getMessage()
            );
        }

        JOptionPane.showMessageDialog(
            this,
            "Sale could not be completed.\n\n"
            + "No stock or transaction changes were saved.\n\n"
            + e.getMessage(),
            "Sale Failed",
            JOptionPane.ERROR_MESSAGE
        );

        salesPanel.refreshProducts();

        return;
    }

} catch (SQLException e) {

    JOptionPane.showMessageDialog(
        this,
        "Database connection error:\n"
        + e.getMessage(),
        "Database Error",
        JOptionPane.ERROR_MESSAGE
    );

    return;
}

    // =====================================================
    // STEP 4: SUCCESS MESSAGE
    // =====================================================

    ReceiptDialog receipt = new ReceiptDialog(
        this,
        invoiceNumber,
        currentUser.getUsername(),
        cart.getItems(),
        cart.getSubtotal(),
        cart.getVatableSales(),
        cart.getDiscountAmount(),
        cart.getVat(),
        cart.getVatExemption(),
        cart.getTotal(),
        paymentMethod,
        bank,
        referenceNumber,
        cashReceived,
        change
    );

    receipt.setVisible(true);

    // =====================================================
    // STEP 5: CLEAR CART
    // =====================================================

    cart.clear();

    refreshCartTable();

    salesPanel.refreshProducts();

    if (salesSummary != null) {
        salesSummary.refreshSummary();
    }
    
    }//GEN-LAST:event_ButtonCompActionPerformed

    private void btnNavExpensesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavExpensesActionPerformed
     
     

    cardLayout.show(pnlMainContent, "expenses");

    pnlCart.setVisible(false);

    pnlMainContent.revalidate();
    pnlMainContent.repaint();

    RootPanel.revalidate();
    
    }//GEN-LAST:event_btnNavExpensesActionPerformed

   
    public static void main(String args[]) {
         javax.swing.JOptionPane.showMessageDialog(null,
        "This form cannot be run directly.\nPlease run Login.java to start the application.",
        "Cannot Run Dashboard",
        javax.swing.JOptionPane.WARNING_MESSAGE);
    
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonClear;
    private javax.swing.JButton ButtonComp;
    private javax.swing.JButton ButtonDisc;
    private javax.swing.JButton ButtonRemove;
    private javax.swing.JPanel RootPanel;
    private javax.swing.JButton btnNavEmployees;
    private javax.swing.JButton btnNavExpenses;
    private javax.swing.JButton btnNavPos;
    private javax.swing.JButton btnNavProducts;
    private javax.swing.JButton btnNavReport;
    private javax.swing.JButton btnNavSales;
    private javax.swing.JButton btnNavSecurity;
    private javax.swing.JButton btnSignOut;
    private javax.swing.JButton btnSignOut2;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblDiscount;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalDue;
    private javax.swing.JLabel lblVAT;
    private javax.swing.JPanel pnlCart;
    private javax.swing.JPanel pnlMainContent;
    private javax.swing.JPanel pnlSidebar;
    private javax.swing.JPanel pnlTopBar;
    private javax.swing.JTable tblCart;
    // End of variables declaration//GEN-END:variables
 
    public void addProductToCart(Product product) {
        addProductToCart(product, "Piece", 1);
    }

    public void addProductToCart(Product product, String unitType, int quantity) {
        if (!product.isInStock()) {
            JOptionPane.showMessageDialog(this, "Out of stock: " + product.getProductName());
            return;
        }
        if ("Bulk".equalsIgnoreCase(unitType) && !product.hasBulkPricing()) {
            JOptionPane.showMessageDialog(this,
                "Bulk pricing is not configured for " + product.getProductName() + ".",
                "Bulk Not Available", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!cart.add(product, unitType, quantity)) {
            JOptionPane.showMessageDialog(this,
                "Not enough stock for " + product.getProductName() + ".",
                "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }
        refreshCartTable();
    }

    private void undoLastCartAction() {
        if (!cart.undoLast()) {
            JOptionPane.showMessageDialog(this, "Nothing to undo.");
            return;
        }
        refreshCartTable();
    }

    private void refreshCartTable() {

      if (modernCartPanel == null) {
        return;
    }

    modernCartPanel.removeAll();

    for (CartItem item : cart.getItems()) {

        JPanel card = createCartItemCard(item);

        card.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        modernCartPanel.add(card);
        modernCartPanel.add(Box.createVerticalStrut(8));
    }

    modernCartPanel.revalidate();
    modernCartPanel.repaint();

    recalculateTotals();
    }
    
     private JPanel createCartItemCard(CartItem item) {

     // Main product card
    JPanel card = new JPanel(new BorderLayout(8, 5));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 225, 225)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    card.setPreferredSize(new Dimension(0, 95));
    card.setMinimumSize(new Dimension(0, 95));
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

    // Product information
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setBackground(Color.WHITE);

    JTextPane nameLabel = UiUtil.wrappedText(
            item.getDisplayName(),
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(30, 30, 30),
            150, 40, false
    );

    JLabel priceLabel = new JLabel(
            String.format("₱%,.2f each", item.getUnitPrice())
    );
    priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    priceLabel.setForeground(new Color(110, 110, 110));
    priceLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

    infoPanel.add(nameLabel);
    infoPanel.add(Box.createVerticalStrut(4));
    infoPanel.add(priceLabel);

    card.add(infoPanel, BorderLayout.CENTER);

    // Quantity controls: minus, quantity, plus
    JPanel quantityPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 4, 0)
    );
    quantityPanel.setBackground(Color.WHITE);

    JButton minusButton = new JButton("-");
    minusButton.setFocusable(false);
    minusButton.setMargin(new Insets(0, 0, 0, 0));
    minusButton.setFont(new Font("Dialog", Font.BOLD, 16));
    minusButton.setForeground(new Color(55, 65, 81));
    minusButton.setBackground(new Color(240, 243, 247));
    minusButton.setPreferredSize(new Dimension(30, 30));
    minusButton.setBorder(BorderFactory.createLineBorder(
            new Color(210, 215, 222)
    ));
    minusButton.setOpaque(true);

    JLabel quantityLabel = new JLabel(
            String.valueOf(item.getQuantity()),
            javax.swing.SwingConstants.CENTER
    );
    quantityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    quantityLabel.setForeground(new Color(30, 30, 30));
    quantityLabel.setPreferredSize(new Dimension(28, 30));

    JButton plusButton = new JButton("+");
    plusButton.setFocusable(false);
    plusButton.setMargin(new Insets(0, 0, 0, 0));
    plusButton.setFont(new Font("Dialog", Font.BOLD, 16));
    plusButton.setForeground(Color.WHITE);
    plusButton.setBackground(new Color(37, 99, 235));
    plusButton.setPreferredSize(new Dimension(30, 30));
    plusButton.setBorder(BorderFactory.createEmptyBorder());
    plusButton.setOpaque(true);

    quantityPanel.add(minusButton);
    quantityPanel.add(quantityLabel);
    quantityPanel.add(plusButton);

    card.add(quantityPanel, BorderLayout.EAST);

    // Line total
    JLabel totalLabel = new JLabel(
            String.format("₱%,.2f", item.getSubtotal())
    );
    totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    totalLabel.setForeground(Color.BLACK);
    totalLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

    card.add(totalLabel, BorderLayout.SOUTH);

    // Select this item when clicking its card
    java.awt.event.MouseAdapter selectListener =
            new java.awt.event.MouseAdapter() {
        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            selectedCartItem = item;
        }
    };

    card.addMouseListener(selectListener);
    infoPanel.addMouseListener(selectListener);
    nameLabel.addMouseListener(selectListener);
    priceLabel.addMouseListener(selectListener);
    totalLabel.addMouseListener(selectListener);

    // Decrease quantity
    minusButton.addActionListener(e -> {
        selectedCartItem = item;

        if (cart.decrease(item)) {
            refreshCartTable();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Quantity cannot be less than 1.",
                    "Quantity",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    });

    // Increase quantity
    plusButton.addActionListener(e -> {
        selectedCartItem = item;

        if (cart.increase(item)) {
            refreshCartTable();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Not enough stock for "
                            + item.getProductName() + ".",
                    "Insufficient Stock",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    });

    return card;
     }

    // =====================================================
    // PLUS
    // =====================================================

         
    
   private void recalculateTotals() {
    double subtotal = cart.getSubtotal();
    double discount = cart.getDiscountAmount();
    double vatExempt = cart.getVatExemption();
    currentTotalDue = cart.getTotal();

    lblSubtotal.setText(String.format("₱%.2f", subtotal));
    lblDiscount.setText(String.format("-₱%.2f", discount));

    if (vatExempt > 0) {
        jLabel5.setText("Less: VAT exemption");
        lblVAT.setText(String.format("-₱%.2f", vatExempt));
    } else {
        jLabel5.setText("VAT (12%) included");
        lblVAT.setText(String.format("₱%.2f", cart.getVat()));
    }

    lblTotalDue.setText(String.format("₱%.2f", currentTotalDue));
}

    public void refreshSalesPanel() {
        salesPanel.refreshProducts();
        pnlMainContent.revalidate();
        pnlMainContent.repaint();
        
    }        
}
