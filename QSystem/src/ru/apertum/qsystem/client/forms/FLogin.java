/*
 * Copyright (C) 2014 Evgeniy Egorov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.client.forms;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.help.Helper;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ClientException;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.hibernate.AnnotationSessionFactoryBean;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;

/**
 *
 * @author Evgeniy Egorov
 */
public class FLogin extends javax.swing.JDialog {

    /**
     * Результат
     */
    private static boolean ok = false;
    /**
     * Количество неудачных попыток, если 0 то бесконечно
     */
    private static int count = 0;
    private static int was = 0;
    /**
     * Параметры соединения.
     */
    private INetProperty netProperty;
    private QUserList userList;
    private JFrame parent;
    /**
     * Уровни логирования
     */
    public static final int LEVEL_USER = 1;
    public static final int LEVEL_REPORT = 2;
    public static final int LEVEL_ADMIN = 3;
    /**
     * текущий уровень доступа для диалога
     */
    private int level = LEVEL_USER;

    final public int getLevel() {
        return level;
    }

    final public void setLevel(int level) {
        this.level = level;
        passwordField.setText("");
        labelServer.setText("");
        switch (level) {
            case LEVEL_USER:
                labelLavel.setText(LABEL_USER);
                break;
            case LEVEL_REPORT:
                labelLavel.setText(LABEL_REPORT);
                break;
            case LEVEL_ADMIN:
                final AnnotationSessionFactoryBean as = (AnnotationSessionFactoryBean) Spring.getInstance().getFactory().getBean("conf");
                if (as.getServers().size() > 1) {
                    labelServer.setText("<html>" + as.getName() + "<br>" + as.getUrl());
                }
                labelLavel.setText(LABEL_ADMIN);
                break;
            default:
                labelLavel.setText(LABEL_USER);
        }
    }
    private static ResourceMap localeMap = null;

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FLogin.class);
        }
        return localeMap.getString(key);
    }
    /**
     * Надпись о доступе
     */
    private static final String LABEL_USER = " " + getLocaleMessage("messages.access.work");
    private static final String LABEL_REPORT = " " + getLocaleMessage("messages.access.report");
    private static final String LABEL_ADMIN = " " + getLocaleMessage("messages.access.admin");
    /**
     * Используемая ссылка на диалоговое окно.
     */
    private static FLogin loginForm;

    /**
     * Creates new form FLogin
     *
     * @param netProperty
     * @param parent
     * @param modal
     * @param level
     */
    public FLogin(INetProperty netProperty, JFrame parent, boolean modal, int level) {
        super(parent, modal);
        initComponents();
        init(null, netProperty, parent, modal, level);
    }

    /**
     * Creates new form FLogin
     *
     * @param userList
     * @param parent
     * @param modal
     * @param level
     */
    public FLogin(QUserList userList, JFrame parent, boolean modal, int level) {
        super(parent, modal);
        initComponents();
        init(userList, null, parent, modal, level);
    }

    private QUser fastUser = null;

    /**
     * Тут надо четко понимать, что либо userList не NULL, либо netProperty не NULL
     *
     * @param userList
     * @param netProperty
     * @param parent
     * @param modal
     * @param level
     */
    private void init(QUserList userList, INetProperty netProperty, JFrame parent, boolean modal, int level) {
        try {
            setIconImage(ImageIO.read(FLogin.class.getResource("/ru/apertum/qsystem/client/forms/resources/client.png")));
        } catch (IOException ex) {
            System.err.println(ex);
        }
        setSize(500, 375);
        setAlwaysOnTop(true);
        this.netProperty = netProperty;
        this.userList = userList;
        this.parent = parent;
        this.userGetter = new GetUserFromServer();
        setLevel(level);

        final LinkedList<QUser> users = netProperty != null ? NetCommander.getUsers(netProperty) : userList.getItems();

        ArrayList<QUser> users_ = new ArrayList<>();
        for (QUser user : users) {
            if (user.getId().toString().equals(QConfig.cfg().getUserID())) {
                ok = true;
                fastUser = user;
                return;
            }

            boolean flag = false;
            switch (getLevel()) {
                case LEVEL_ADMIN:
                    flag = user.getAdminAccess();
                    break;
                case LEVEL_REPORT:
                    flag = user.getReportAccess();
                    break;
                case LEVEL_USER:
                    flag = user.getServicesCnt() != 0;
                    break;
            }

            if (flag) {
                users_.add(user);
            }
        }
        afterCreate(users_.toArray());
    }

    /**
     * Чтоб не дублировать код
     *
     * @param str список пользователей
     */
    private void afterCreate(Object[] users) {
        jLabel1.setText(getLocaleMessage("jLabel1.text"));
        jLabel2.setText(getLocaleMessage("jLabel2.text"));
        buttonEnter.setText(getLocaleMessage("pressOK.Action.text"));
        buttonExit.setText(getLocaleMessage("pressCancel.Action.text"));
        FAbout.loadVersionSt();
        labelName.setText(FAbout.NAME_);
        DefaultComboBoxModel m = new DefaultComboBoxModel(users);
        comboBoxUser.setModel(m);
        final File f = new File("temp/lusr");
        if (f.exists()) {
            String str = "";
            try (FileInputStream fis = new FileInputStream(f); Scanner s = new Scanner(new InputStreamReader(fis, "UTF-8"))) {
                while (s.hasNextLine()) {
                    final String line = s.nextLine().trim();
                    str += line;
                }
            } catch (IOException ex) {
            }
            if (!"".equals(str) && str.matches("-?\\d+(\\.\\d+)?")) {
                for (int i = 0; i < m.getSize(); i++) {
                    if (((QUser) (m.getElementAt(i))).getId().equals(Long.parseLong(str))) {
                        m.setSelectedItem(m.getElementAt(i));
                        break;
                    }
                }
            }
        }
        //привязка помощи к форме.
        final Helper helper = Helper.getHelp(level == LEVEL_ADMIN ? "ru/apertum/qsystem/client/help/admin.hs" : "ru/apertum/qsystem/client/help/client.hs");
        helper.enableHelpKey(qPanel1, "loginning");
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                labelLavel.setLocation(2, getHeight() - 15);
                labelServer.setLocation(5, getHeight() - 45);
            }
        });
    }

    /**
     * Логирование без предварительно созданного списка пользователей. Этот список определяется путем отправки задания на сервер.
     *
     * @param netProperty свойства коннекта
     * @param owner относительно этого контрола модальность и позиционирование
     * @param modal режим модальности
     * @param count количество неудачных попыток, если 0 то бесконечно
     * @param level Уровень доступа, см. LEVEL_USER, LEVEL_REPORT, LEVEL_ADMIN
     * @return залогиневшiйся юзер.
     */
    public static QUser logining(INetProperty netProperty, JFrame owner, boolean modal, int count, int level) {
        QLog.l().logger().info("Вход в систему.");
        if (loginForm == null) {
            loginForm = new FLogin(netProperty, owner, modal, level);
        } else if (loginForm.netProperty != netProperty || loginForm.parent != owner || loginForm.getLevel() != level) {
            loginForm = new FLogin(netProperty, owner, modal, level);
        }
        FLogin.count = count;
        if (owner == null) {
            // Отцентирируем
            final Toolkit kit = Toolkit.getDefaultToolkit();
            loginForm.setLocationRelativeTo(null);
            //loginForm.setLocation((Math.round(kit.getScreenSize().width - loginForm.getWidth()) / 2),
            //        (Math.round(kit.getScreenSize().height - loginForm.getHeight()) / 2));
        }
        Uses.closeSplash();
        if (!ok) {
            loginForm.setVisible(true);
        }
        if (!ok) {
            System.exit(0);
        }
        final QUser user = loginForm.fastUser != null ? loginForm.fastUser : (QUser) loginForm.comboBoxUser.getSelectedItem(); 
        QLog.l().logger().info("Вход в систему выполнен. Пользователь \"" + user + "\", уровень доступа \"" + level + "\".");
        final File f = new File("temp/lusr");
        try {
            try (FileOutputStream fos = new FileOutputStream(f)) {
                fos.write(user.getId().toString().getBytes());
                fos.flush();
            }
        } catch (IOException ex) {
        }
        return user;
    }

    /**
     * Логирование имея уже готовый список возможных пользователей для логирования.
     *
     * @param userList список пользователей
     * @param owner относительно этого контрола модальность и позиционирование
     * @param modal режим модальности
     * @param count количество неудачных попыток, если 0 то бесконечно
     * @param level Уровень доступа, см. LEVEL_USER, LEVEL_REPORT, LEVEL_ADMIN
     * @return залогиневшийся юзер.
     */
    public static QUser logining(QUserList userList, JFrame owner, boolean modal, int count, int level) {
        QLog.l().logger().info("Вход в систему.");
        if (loginForm == null) {
            loginForm = new FLogin(userList, owner, modal, level);
        } else if (loginForm.userList != userList || loginForm.parent != owner || loginForm.getLevel() != level) {
            loginForm = new FLogin(userList, owner, modal, level);
        }
        FLogin.count = count;
        if (owner == null) {
            // Отцентирируем
            final Toolkit kit = Toolkit.getDefaultToolkit();
            loginForm.setLocationRelativeTo(null);
            //loginForm.setLocation((Math.round(kit.getScreenSize().width - loginForm.getWidth()) / 2),
            //        (Math.round(kit.getScreenSize().height - loginForm.getHeight()) / 2));
        }
        Uses.closeSplash();
        if (!ok) {
            loginForm.setVisible(true);
        }
        if (!ok) {
            System.exit(0);
        }
        final QUser user = loginForm.fastUser != null ? loginForm.fastUser : (QUser) loginForm.comboBoxUser.getSelectedItem();
        QLog.l().logger().info("Вход в систему выполнен. Пользователь \"" + user + "\", уровень доступа \"" + level + "\".");
        final File f = new File("temp/lusr");
        try {
            try (FileOutputStream fos = new FileOutputStream(f)) {
                fos.write(user.getId().toString().getBytes());
                fos.flush();
            }
        } catch (IOException ex) {
        }
        return user;
    }

    /**
     * Получить выбранного юзера по его имени для разных случаев.
     */
    private interface IGetUser {

        public QUser getUser();
    }
    private IGetUser userGetter;

    private class GetUserFromServer implements IGetUser {

        @Override
        public QUser getUser() {
            return fastUser != null ? fastUser : (QUser) comboBoxUser.getSelectedItem();
        }
    }

    private class GetUserFromList implements IGetUser {

        @Override
        public QUser getUser() {
            return fastUser != null ? fastUser : (QUser) comboBoxUser.getSelectedItem();
        }
    }

    private boolean checkLogin() {
        final QUser user = userGetter.getUser();
        switch (getLevel()) {
            case LEVEL_ADMIN:
                if (!user.getAdminAccess()) {
                    JOptionPane.showMessageDialog(this, getLocaleMessage("messages.noAccess.mess"), getLocaleMessage("messages.noAccess.caption"), JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                break;
            case LEVEL_REPORT:
                if (!user.getReportAccess()) {
                    JOptionPane.showMessageDialog(this, getLocaleMessage("messages.noAccess.mess"), getLocaleMessage("messages.noAccess.caption"), JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                break;
            case LEVEL_USER:
                break;
            default:
                throw new ClientException("Нет такого уровня доступа.");

        }

        final String userPass = user.getPassword();
        if (!userPass.equals(new String(passwordField.getPassword()))) {
            JOptionPane.showMessageDialog(this, getLocaleMessage("messages.noAccessUser.mess"), getLocaleMessage("messages.noAccess.caption"), JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public void pressOK() {
        ok = checkLogin();
        if (ok) {
            this.setVisible(false);
        } else {
            was++;
            if (was == count) {
                System.exit(0);
            }
        }
    }

    public void pressCancel() {
        System.exit(0);
    }

    private void keyPress(java.awt.event.KeyEvent evt) {

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            pressOK();
        }
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        qPanel1 = new ru.apertum.qsystem.client.model.QPanel();
        labelLavel = new javax.swing.JLabel();
        labelServer = new javax.swing.JLabel();
        buttonEnter = new javax.swing.JButton();
        buttonExit = new javax.swing.JButton();
        comboBoxUser = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelName = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        labelLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        qPanel1.setBorder(new javax.swing.border.MatteBorder(null));
        qPanel1.setBackgroundImgage("/ru/apertum/qsystem/client/forms/resources/fon_login.jpg");

        labelLavel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelLavel.setForeground(new java.awt.Color(255, 0, 0));
        labelLavel.setText("ComponentOfSystem");

        labelServer.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelServer.setForeground(new java.awt.Color(255, 255, 0));
        labelServer.setText("serverBD");

        buttonEnter.setText("Enter");
        buttonEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEnterActionPerformed(evt);
            }
        });
        buttonEnter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                buttonEnterKeyPressed(evt);
            }
        });

        buttonExit.setText("Exit");
        buttonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExitActionPerformed(evt);
            }
        });
        buttonExit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                buttonExitKeyPressed(evt);
            }
        });

        comboBoxUser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboBoxUser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                comboBoxUserKeyPressed(evt);
            }
        });

        jLabel1.setText("User");

        passwordField.setText("jPasswordField1");
        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                passwordFieldKeyPressed(evt);
            }
        });

        jLabel2.setText("Password");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/key02.png"))); // NOI18N

        labelName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labelName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelName.setText("jLabel4");

        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(1, 140));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 108, Short.MAX_VALUE)
        );

        jPanel3.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel3.setOpaque(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 23, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel4.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel4.setOpaque(false);
        jPanel4.setPreferredSize(new java.awt.Dimension(120, 12));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jPanel5.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel5.setOpaque(false);
        jPanel5.setPreferredSize(new java.awt.Dimension(120, 26));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 48, Short.MAX_VALUE)
        );

        jPanel6.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel6.setOpaque(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 28, Short.MAX_VALUE)
        );

        jPanel7.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel7.setOpaque(false);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 88, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );

        labelLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/label.png"))); // NOI18N

        javax.swing.GroupLayout qPanel1Layout = new javax.swing.GroupLayout(qPanel1);
        qPanel1.setLayout(qPanel1Layout);
        qPanel1Layout.setHorizontalGroup(
            qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(qPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(qPanel1Layout.createSequentialGroup()
                .addGroup(qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(qPanel1Layout.createSequentialGroup()
                        .addGroup(qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qPanel1Layout.createSequentialGroup()
                                .addContainerGap(96, Short.MAX_VALUE)
                                .addGroup(qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qPanel1Layout.createSequentialGroup()
                                        .addComponent(buttonEnter, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(buttonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                                            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qPanel1Layout.createSequentialGroup()
                                                .addGroup(qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jLabel1)
                                                    .addComponent(jLabel2))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(passwordField, javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(comboBoxUser, javax.swing.GroupLayout.Alignment.TRAILING, 0, 185, Short.MAX_VALUE)))))))
                            .addGroup(qPanel1Layout.createSequentialGroup()
                                .addComponent(labelLavel)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(qPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelServer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        qPanel1Layout.setVerticalGroup(
            qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(qPanel1Layout.createSequentialGroup()
                .addGroup(qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelName)
                    .addGroup(qPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(qPanel1Layout.createSequentialGroup()
                        .addGroup(qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(qPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(comboBoxUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addGap(11, 11, 11)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonExit)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonEnter))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addComponent(labelServer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelLavel))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(qPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(qPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEnterActionPerformed
        pressOK();
    }//GEN-LAST:event_buttonEnterActionPerformed

    private void buttonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExitActionPerformed
        pressCancel();
    }//GEN-LAST:event_buttonExitActionPerformed

    private void comboBoxUserKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_comboBoxUserKeyPressed
        keyPress(evt);
    }//GEN-LAST:event_comboBoxUserKeyPressed

    private void passwordFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passwordFieldKeyPressed
        keyPress(evt);
    }//GEN-LAST:event_passwordFieldKeyPressed

    private void buttonEnterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buttonEnterKeyPressed
        keyPress(evt);
    }//GEN-LAST:event_buttonEnterKeyPressed

    private void buttonExitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buttonExitKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            pressCancel();
        }
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }//GEN-LAST:event_buttonExitKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonEnter;
    private javax.swing.JButton buttonExit;
    private javax.swing.JComboBox comboBoxUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel labelLabel;
    private javax.swing.JLabel labelLavel;
    private javax.swing.JLabel labelName;
    private javax.swing.JLabel labelServer;
    private javax.swing.JPasswordField passwordField;
    private ru.apertum.qsystem.client.model.QPanel qPanel1;
    // End of variables declaration//GEN-END:variables
}
