package ru.apertum.qsystem.client.forms;

import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.server.model.QOffice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Holds the popup window to change the metadata related to an Office object.
 *
 * @author Sean Rumsby
 */
public class FOfficeChangeDialog extends javax.swing.JDialog {

    private static FOfficeChangeDialog officeChangeDialod;

    public FOfficeChangeDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
        this.setLocationRelativeTo(null);
    }

    private void init() {
        // Фича. По нажатию Escape закрываем форму
        // свернем по esc
        getRootPane().registerKeyboardAction((ActionEvent e) -> {
            setVisible(false);
        },
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Основной метод редактирования услуги.
     *
     * @param parent родительская форма
     * @param modal модальность
     * @param office Пользователь для редактирования
     */
    public static void changeOffice(Frame parent, boolean modal, QOffice office) {
        QLog.l().logger().info("Editing of office \"" + office + "\""); //NOI18N //NOI18N
        if (officeChangeDialod == null) {
            officeChangeDialod = new FOfficeChangeDialog(parent, modal);
        }

        QLog.l().logger().info("Office: " + office.getName());
        officeChangeDialod.office = office;
        officeChangeDialod.tfOfficeName.setText(office.getName());
        officeChangeDialod.tfOfficeSmartboard.setSelectedItem(office.getSmartboardType());
        officeChangeDialod.setVisible(true);
    }

    private QOffice office;

    private void saveOffice() {
        QLog.l().logger().info("Save the office");
        String smartboard = tfOfficeSmartboard.getSelectedItem().toString();

        if (smartboard != "nocallonsmartboard" || smartboard != "callbyname" || smartboard != "callbyticket") {
            smartboard = "callbyticket";
        }

        office.setSmartboardType(smartboard);
        office.setName(tfOfficeName.getText());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainOfficePropsPanel = new javax.swing.JPanel();
        jLabelOfficeName = new javax.swing.JLabel();
        jLabelOfficeName.setText("Office Name");
        jLabelSmartboardType = new javax.swing.JLabel();
        jLabelSmartboardType.setText("Smartboard Type");

        tfOfficeName = new javax.swing.JTextField();
        tfOfficeSmartboard = new javax.swing.JComboBox<>();

        tfOfficeSmartboard.addItem("callbyname");
        tfOfficeSmartboard.addItem("callbyticket");
        tfOfficeSmartboard.addItem("nocallonsmartboard");

        btnCancel = new javax.swing.JButton();
        btnCancel.setText("Cancel"); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnOk = new javax.swing.JButton();
        btnOk.setText("Save"); // NOI18N
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MainOfficePropsPanelLayout = new javax.swing.GroupLayout(MainOfficePropsPanel);
        MainOfficePropsPanel.setLayout(MainOfficePropsPanelLayout);
        MainOfficePropsPanelLayout.setHorizontalGroup(
            MainOfficePropsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(5)
            .addGroup(MainOfficePropsPanelLayout.createSequentialGroup()
                .addGroup(MainOfficePropsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelOfficeName)
                    .addComponent(jLabelSmartboardType))
                .addGap(18, 18, 18)
                .addGroup(MainOfficePropsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfOfficeName)
                    .addComponent(tfOfficeSmartboard))
                .addGap(18, 18, 18))
        );

        MainOfficePropsPanelLayout.setVerticalGroup(
            MainOfficePropsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(MainOfficePropsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(MainOfficePropsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelOfficeName)
                        .addComponent(tfOfficeName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(MainOfficePropsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelSmartboardType)
                        .addComponent(tfOfficeSmartboard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(5,5,5)
            .addGroup(layout.createParallelGroup()
                .addComponent(MainOfficePropsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnOk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel))))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(MainOfficePropsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancel)
                        .addComponent(btnOk))
                    .addContainerGap())
        );

        pack();
    }

    private void btnOkActionPerformed(ActionEvent evt) {
        if (office != null) {
            saveOffice();
        }
        setVisible(false);
    }

    private void btnCancelActionPerformed(ActionEvent evt) {
        setVisible(false);
    }

    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel jLabelOfficeName;
    private javax.swing.JLabel jLabelSmartboardType;
    private javax.swing.JTextField tfOfficeName;
    private javax.swing.JComboBox tfOfficeSmartboard;
    private javax.swing.JPanel MainOfficePropsPanel;
}
