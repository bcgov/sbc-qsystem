/*
 * Copyright (C) 2013 Evgeniy Egorov
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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.ServiceLoader;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.MOVE;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.output.OutputException;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.client.common.WelcomeParams;
import ru.apertum.qsystem.common.GsonPool;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ClientException;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.IClientNetProperty;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.IPrintTicket;
import static ru.apertum.qsystem.server.QServer.clearAllQueue;
import ru.apertum.qsystem.server.model.ATreeModel;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;

/**
 *
 * @author Evgeniy Egorov
 */
public class PComplexService extends javax.swing.JPanel {

    final private File configFile;
    final IClientNetProperty netProperty;
    final ATreeModel<QService> servsTree;

    /**
     * Creates new form PComplexService
     *
     * @param tm дерево услуг
     * @param configFile для сохранения параметров и списка услуг
     * @param netProperty
     */
    public PComplexService(ATreeModel tm, File configFile, IClientNetProperty netProperty) {
        initComponents();
        this.configFile = configFile;
        this.netProperty = netProperty;
        servsTree = (ATreeModel<QService>) tm;
        treeServices.setModel(tm);

        listOfLists.setModel(new DefaultListModel<>());
        listFreeServices.setModel(new DefaultListModel<>());
        listFreeServices.setTransferHandler(thList);
        listServ1.setModel(new DefaultListModel<>());
        listServ1.setTransferHandler(thList);
        listServ2.setModel(new DefaultListModel<>());
        listServ2.setTransferHandler(thList);
        listServ3.setModel(new DefaultListModel<>());
        listServ3.setTransferHandler(thList);
        listServ4.setModel(new DefaultListModel<>());
        listServ4.setTransferHandler(thList);
        listServ5.setModel(new DefaultListModel<>());
        listServ5.setTransferHandler(thList);
        treeDepends.setModel(new DepServiceTree(new DepService("root1", Long.MIN_VALUE)));
        treeDepends.setTransferHandler(thDepTree);
        treeServices.setTransferHandler(new TransferHandler() {

            @Override
            public int getSourceActions(JComponent c) {
                return MOVE;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                //return (QService) ((JTree) c).getLastSelectedPathComponent();
                class Transferable2 implements Transferable {

                    public final ArrayList<QService> list = new ArrayList<>();

                    @Override
                    public DataFlavor[] getTransferDataFlavors() {
                        return new DataFlavor[0];
                    }

                    @Override
                    public boolean isDataFlavorSupported(DataFlavor flavor) {
                        return true;
                    }

                    @Override
                    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                        return list;
                    }
                }
                final Transferable2 t = new Transferable2();
                for (TreePath tp : ((JTree) c).getSelectionPaths()) {
                    t.list.add((QService) (tp.getLastPathComponent()));
                }
                return t;
            }
        });
        listFreeServices.setDropMode(DropMode.INSERT);

        listOfLists.addListSelectionListener((ListSelectionEvent e) -> {
            if (listOfLists.getSelectedIndex() != -1) {
                DefaultListModel<ComplexListOfServices> sll = (DefaultListModel<ComplexListOfServices>) (listOfLists.getModel());
                final ComplexListOfServices cmp = sll.get(listOfLists.getSelectedIndex());

                DefaultListModel<QService> sl = (DefaultListModel<QService>) (listFreeServices.getModel());
                sl.clear();
                for (QService ser : cmp.listFree) {
                    sl.addElement(ser);
                }
                sl = (DefaultListModel<QService>) (listServ1.getModel());
                sl.clear();
                for (QService ser : cmp.list1) {
                    sl.addElement(ser);
                }
                sl = (DefaultListModel<QService>) (listServ2.getModel());
                sl.clear();
                for (QService ser : cmp.list2) {
                    sl.addElement(ser);
                }
                sl = (DefaultListModel<QService>) (listServ3.getModel());
                sl.clear();
                for (QService ser : cmp.list3) {
                    sl.addElement(ser);
                }
                sl = (DefaultListModel<QService>) (listServ4.getModel());
                sl.clear();
                for (QService ser : cmp.list4) {
                    sl.addElement(ser);
                }
                sl = (DefaultListModel<QService>) (listServ5.getModel());
                sl.clear();
                for (QService ser : cmp.list5) {
                    sl.addElement(ser);
                }

            }
        });

        loadState();
    }
    private static final ResourceBundle translate = ResourceBundle.getBundle("ru/apertum/qsystem/client/forms/resources/PComplexService", Locales.getInstance().getLangCurrent());

    private String locMes(String key) {
        return translate.getString(key);
    }

    private boolean isGood(QService data) {
        boolean flag = true;
        DefaultListModel<QService> sl = (DefaultListModel<QService>) (listFreeServices.getModel());
        for (Object o : sl.toArray()) {
            if (((QService) o).getId().equals(data.getId())) {
                flag = false;
                break;
            }
        }
        if (flag) {
            sl = (DefaultListModel<QService>) (listServ1.getModel());
            for (Object o : sl.toArray()) {
                if (((QService) o).getId().equals(data.getId())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                sl = (DefaultListModel<QService>) (listServ2.getModel());
                for (Object o : sl.toArray()) {
                    if (((QService) o).getId().equals(data.getId())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    sl = (DefaultListModel<QService>) (listServ3.getModel());
                    for (Object o : sl.toArray()) {
                        if (((QService) o).getId().equals(data.getId())) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        sl = (DefaultListModel<QService>) (listServ4.getModel());
                        for (Object o : sl.toArray()) {
                            if (((QService) o).getId().equals(data.getId())) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            sl = (DefaultListModel<QService>) (listServ5.getModel());
                            for (Object o : sl.toArray()) {
                                if (((QService) o).getId().equals(data.getId())) {
                                    flag = false;
                                    break;
                                }
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return flag;
    }
    private final TransferHandler thList = new TransferHandler() {

        @Override
        public boolean canImport(TransferHandler.TransferSupport info) {
            // Get the string that is being dropped.
            final Transferable t = info.getTransferable();
            final ArrayList<QService> data;
            try {
                data = (ArrayList<QService>) t.getTransferData(DataFlavor.stringFlavor);
                if (data == null || data.isEmpty()) {
                    return false;
                }
                boolean flag = false;
                for (QService qService : data) {
                    flag = flag || isGood(qService);
                }
                if (!flag) {
                    return false;
                }
            } catch (UnsupportedFlavorException | IOException e) {
                return false;
            }
            return true;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport info) {
            if (!info.isDrop()) {
                return false;
            }
            final Transferable t = info.getTransferable();
            final ArrayList<QService> dataA;
            try {
                dataA = (ArrayList<QService>) t.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                System.err.println(e);
                return false;
            }
            if (dataA == null || dataA.isEmpty()) {
                return false;
            }
            final DefaultListModel<QService> sl = (DefaultListModel<QService>) (((JList) (info.getComponent())).getModel());
            dataA.stream().forEach((data) -> {
                if (data.isLeaf() && isGood(data)) {
                    sl.addElement(data);
                } else {
                    QServiceTree.sailToStorm(data, (TreeNode service) -> {
                        if (service.isLeaf() && isGood((QService) service)) {
                            sl.addElement((QService) service);
                        }
                    });
                }
            });
            return true;
        }
    };
    private final TransferHandler thDepTree = new TransferHandler() {

        @Override
        public boolean canImport(TransferHandler.TransferSupport info) {

            final JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
            final TreePath tp = dl.getPath();
            final DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tp.getLastPathComponent();
            return parent.getParent().getParent() == null;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport info) {
            if (!info.isDrop()) {
                return false;
            }
            final Transferable t = info.getTransferable();
            final ArrayList<QService> dataA;
            try {
                dataA = (ArrayList<QService>) t.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                System.err.println(e);
                return false;
            }
            if (dataA == null || dataA.isEmpty()) {
                return false;
            }
            final JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
            final TreePath tp = dl.getPath();
            final DepService parent = (DepService) tp.getLastPathComponent();
            if (parent.getParent().getParent() == null) {
                for (QService data : dataA) {
                    if (data.isLeaf()) {
                        boolean f = true;
                        final Enumeration<DepService> e = parent.children();
                        while (e.hasMoreElements()) {
                            final DepService ds = e.nextElement();
                            if (ds.getId().equals(data.getId())) {
                                f = false;
                                break;
                            }
                        }
                        if (f && !data.getId().equals(parent.getId())) {
                            ((DepServiceTree) treeDepends.getModel()).insertNodeInto(new DepService(data.getName(), data.getId()), parent, parent.getChildCount());
                        }
                    } else {
                        QServiceTree.sailToStorm(data, (TreeNode service) -> {
                            if (service.isLeaf()) {
                                final QService data1 = (QService) service;
                                boolean f = true;
                                final Enumeration<DepService> e = parent.children();
                                while (e.hasMoreElements()) {
                                    final DepService ds = e.nextElement();
                                    if (ds.getId().equals(data1.getId())) {
                                        f = false;
                                        break;
                                    }
                                }
                                if (f && !data1.getId().equals(parent.getId())) {
                                    ((DepServiceTree) treeDepends.getModel()).insertNodeInto(new DepService(data1.getName(), data1.getId()), parent, parent.getChildCount());
                                }
                            }
                        });
                    }
                }
                saveState();
            }

            return true;
        }
    };

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeServices = new javax.swing.JTree();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        listOfLists = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listServ1 = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        listServ2 = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        listServ3 = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        listServ4 = new javax.swing.JList();
        jScrollPane8 = new javax.swing.JScrollPane();
        listServ5 = new javax.swing.JList();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        listFreeServices = new javax.swing.JList();
        buttonSetInLine = new javax.swing.JButton();
        buttonSaveList = new javax.swing.JButton();
        buttonClearLists = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        treeDepends = new javax.swing.JTree();

        jSplitPane1.setDividerLocation(350);
        jSplitPane1.setContinuousLayout(true);

        treeServices.setBorder(javax.swing.BorderFactory.createTitledBorder("Оказываемые услуги"));
        treeServices.setDragEnabled(true);
        treeServices.setDropMode(javax.swing.DropMode.ON);
        treeServices.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeServicesMouseClicked(evt);
            }
        });
        treeServices.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                treeServicesKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(treeServices);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jSplitPane2.setDividerLocation(330);
        jSplitPane2.setContinuousLayout(true);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Сохраненные списки услуг"));

        listOfLists.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listOfLists.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listOfListsMouseClicked(evt);
            }
        });
        listOfLists.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listOfListsKeyPressed(evt);
            }
        });
        jScrollPane6.setViewportView(listOfLists);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(jPanel2);

        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));

        jPanel3.setBorder(new javax.swing.border.MatteBorder(null));

        jSplitPane3.setDividerLocation(195);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setAutoscrolls(true);
        jSplitPane3.setContinuousLayout(true);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Последовательнооказываемые услуги"));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));

        listServ1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listServ1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listFreeServicesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(listServ1);

        jPanel5.add(jScrollPane2);

        listServ2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listServ2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listFreeServicesMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(listServ2);

        jPanel5.add(jScrollPane3);

        listServ3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listServ3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listFreeServicesMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(listServ3);

        jPanel5.add(jScrollPane4);

        listServ4.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listServ4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listFreeServicesMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(listServ4);

        jPanel5.add(jScrollPane5);

        listServ5.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listServ5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listFreeServicesMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(listServ5);

        jPanel5.add(jScrollPane8);

        jSplitPane3.setRightComponent(jPanel5);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Безочередные услуги"));

        listFreeServices.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listFreeServices.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listFreeServicesMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(listFreeServices);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
        );

        jSplitPane3.setLeftComponent(jPanel4);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
        );

        buttonSetInLine.setText(translate.getString("inLine")); // NOI18N
        buttonSetInLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetInLineActionPerformed(evt);
            }
        });

        buttonSaveList.setText(translate.getString("save")); // NOI18N
        buttonSaveList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveListActionPerformed(evt);
            }
        });

        buttonClearLists.setText(translate.getString("clear")); // NOI18N
        buttonClearLists.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClearListsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonClearLists)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonSaveList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonSetInLine)
                .addContainerGap())
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonSetInLine)
                    .addComponent(buttonSaveList)
                    .addComponent(buttonClearLists))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Списки услуг", jPanel7);

        treeDepends.setExpandsSelectedPaths(false);
        treeDepends.setRootVisible(false);
        treeDepends.setShowsRootHandles(true);
        treeDepends.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeDependsMouseClicked(evt);
            }
        });
        jScrollPane9.setViewportView(treeDepends);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(translate.getString("dependances"), jPanel8); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Списки услуг");
        jTabbedPane1.getAccessibleContext().setAccessibleDescription("");

        jSplitPane2.setLeftComponent(jPanel1);

        jSplitPane1.setRightComponent(jSplitPane2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 913, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void listFreeServicesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listFreeServicesMouseClicked
        if (evt.getClickCount() > 1) {
            final JList list = ((JList) (evt.getComponent()));
            if (list.getSelectedIndex() != -1) {
                ((DefaultListModel) (list.getModel())).remove(list.getSelectedIndex());
            }
        }
    }//GEN-LAST:event_listFreeServicesMouseClicked

    private void buttonClearListsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClearListsActionPerformed
        if (JOptionPane.showConfirmDialog(this, translate.getString("readyForCleanServices"), translate.getString("cliningServices"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }
        ((DefaultListModel) (listFreeServices.getModel())).clear();
        ((DefaultListModel) (listServ1.getModel())).clear();
        ((DefaultListModel) (listServ2.getModel())).clear();
        ((DefaultListModel) (listServ3.getModel())).clear();
        ((DefaultListModel) (listServ4.getModel())).clear();
        ((DefaultListModel) (listServ5.getModel())).clear();
    }//GEN-LAST:event_buttonClearListsActionPerformed

    private void buttonSaveListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveListActionPerformed
        if (listFreeServices.getModel().getSize() + listServ1.getModel().getSize() + listServ2.getModel().getSize() + listServ3.getModel().getSize() + listServ4.getModel().getSize() + listServ5.getModel().getSize() == 0) {
            return;
        }

        final String inputData = (String) JOptionPane.showInputDialog(this, translate.getString("listName"), "***", 3, null, null, "");
        if (inputData == null || inputData.isEmpty()) {
            return;
        }
        final DefaultListModel<ComplexListOfServices> sll = (DefaultListModel<ComplexListOfServices>) (listOfLists.getModel());
        for (Object object : sll.toArray()) {
            if (((ComplexListOfServices) object).name.equalsIgnoreCase(inputData)) {
                JOptionPane.showMessageDialog(this, "Список с таким именем \"" + inputData + "\" уже существует.", "Не верное наименование.", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        final ComplexListOfServices el = new ComplexListOfServices(inputData);
        for (Object object : ((DefaultListModel) (listFreeServices.getModel())).toArray()) {
            el.listFree.add((QService) object);
        }
        for (Object object : ((DefaultListModel) (listServ1.getModel())).toArray()) {
            el.list1.add((QService) object);
        }
        for (Object object : ((DefaultListModel) (listServ2.getModel())).toArray()) {
            el.list2.add((QService) object);
        }
        for (Object object : ((DefaultListModel) (listServ3.getModel())).toArray()) {
            el.list3.add((QService) object);
        }
        for (Object object : ((DefaultListModel) (listServ4.getModel())).toArray()) {
            el.list4.add((QService) object);
        }
        for (Object object : ((DefaultListModel) (listServ5.getModel())).toArray()) {
            el.list5.add((QService) object);
        }
        ((DefaultListModel) (listOfLists.getModel())).addElement(el);

        saveState();
    }//GEN-LAST:event_buttonSaveListActionPerformed

    public static class ComplexListOfServices {

        @Expose
        @SerializedName("name")
        public final String name;
        @Expose
        @SerializedName("listFree")
        public final ArrayList<QService> listFree = new ArrayList<>();
        @Expose
        @SerializedName("list1")
        public final ArrayList<QService> list1 = new ArrayList<>();
        @Expose
        @SerializedName("list2")
        public final ArrayList<QService> list2 = new ArrayList<>();
        @Expose
        @SerializedName("list3")
        public final ArrayList<QService> list3 = new ArrayList<>();
        @Expose
        @SerializedName("list4")
        public final ArrayList<QService> list4 = new ArrayList<>();
        @Expose
        @SerializedName("list5")
        public final ArrayList<QService> list5 = new ArrayList<>();

        public ComplexListOfServices(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return (listFree.size() + list1.size() + list2.size() + list3.size() + list4.size() + list5.size()) + "/" + name;
        }
    }

    static class SaveList {

        public SaveList() {
        }

        public SaveList(ArrayList<ComplexListOfServices> backup, LinkedList<LinkedList<Long>> dependences) {
            this.backup = backup;
            this.dependences = dependences;
        }
        @Expose
        @SerializedName("backup")
        ArrayList<ComplexListOfServices> backup;

        @Expose
        @SerializedName("dependences")
        LinkedList<LinkedList<Long>> dependences;
    }

    /**
     * Сохранение состояния пула услуг в xml-файл на диск
     */
    public void saveState() {
        QLog.l().logger().info("Сохранение конфигурации комплексных услуг.");

        // в темповый файл
        final FileOutputStream fos;
        try {
            fos = new FileOutputStream(configFile);
        } catch (FileNotFoundException ex) {
            throw new ClientException("Не возможно создать временный файл конфигурации комплексных услуг. " + ex.getMessage());
        }
        Gson gson = null;
        try {
            gson = GsonPool.getInstance().borrowGson();
            // сохраняем списки услуг
            final ArrayList<ComplexListOfServices> li = new ArrayList<>();
            for (Object cs : ((DefaultListModel<ComplexListOfServices>) (listOfLists.getModel())).toArray()) {
                li.add((ComplexListOfServices) cs);
            }
            // сохраняем зависимости
            final LinkedList<LinkedList<Long>> deps = new LinkedList<>();
            final DepService root = (DepService) treeDepends.getModel().getRoot();
            final Enumeration<DepService> e = root.children();
            while (e.hasMoreElements()) {
                final DepService ds = e.nextElement();
                final LinkedList<Long> dSer = new LinkedList<>();
                dSer.add(ds.getId());
                final Enumeration<DepService> e1 = ds.children();
                while (e1.hasMoreElements()) {
                    final DepService ds1 = e1.nextElement();
                    dSer.add(ds1.getId());
                }
                deps.add(dSer);
            }
            fos.write(gson.toJson(new SaveList(li, deps)).getBytes("UTF-8"));
            fos.flush();
            fos.close();
        } catch (IOException ex) {
            throw new ServerException("Не возможно сохранить изменения в поток." + ex.getMessage());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }

        QLog.l().logger().info("Конфигурация комплексных услуг сохранена.");
    }

    public class DepService extends DefaultMutableTreeNode {

        final String name;
        final Long id;

        public DepService(String name, Long id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public Long getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public class DepServiceTree extends DefaultTreeModel {

        public DepServiceTree(final TreeNode root1) {
            super(root1);
            final DefaultTreeModel f = this;
            final ATreeModel tm = (ATreeModel) treeServices.getModel();
            QServiceTree.sailToStorm(tm.getRoot(), (TreeNode service) -> {
                final QService serv = (QService) service;
                if (serv.isLeaf()) {
                    final DepService ser = new DepService(serv.getName(), serv.getId());
                    f.insertNodeInto(ser, (MutableTreeNode) root1, root1.getChildCount());
                }
            });
        }

        public LinkedList<Long> getDeps(Long id) {
            final LinkedList<Long> deps = new LinkedList<>();
            deps.add(id);
            final DepServiceTree tm = (DepServiceTree) treeDepends.getModel();
            final Enumeration<DepService> e = ((DefaultMutableTreeNode) tm.getRoot()).children();
            while (e.hasMoreElements()) {
                final DepService ds = e.nextElement();
                if (id.equals(ds.getId())) {
                    final Enumeration<DepService> e1 = ds.children();
                    while (e1.hasMoreElements()) {
                        final DepService ds1 = e1.nextElement();
                        deps.add(ds1.getId());
                    }
                    break;
                }
            }
            return deps;
        }
    }

    /**
     * Загрузка состояния пула услуг из временного json-файла
     */
    public final void loadState() {

        QLog.l().logger().info("Пробуем восстановить состояние списков услуг.");
        File recovFile = configFile;
        if (recovFile.exists()) {
            QLog.l().logger().warn("Восстановление состояние системы после вчерашнего... нештатного завершения работы сервера.");
            //восстанавливаем состояние

            //для начала сделаем список зависимостей - дерево перврго уровня
            treeDepends.setModel(new DepServiceTree(new DepService("root", -1L)));

            final FileInputStream fis;
            try {
                fis = new FileInputStream(recovFile);
            } catch (FileNotFoundException ex) {
                throw new ClientException(ex);
            }
            final Scanner scan = new Scanner(fis, "utf8");
            String rec_data = "";
            while (scan.hasNextLine()) {
                rec_data += scan.nextLine();
            }
            try {
                fis.close();
            } catch (IOException ex) {
                throw new ClientException(ex);
            }

            SaveList recList;
            final Gson gson = GsonPool.getInstance().borrowGson();
            try {
                recList = gson.fromJson(rec_data, SaveList.class);
            } catch (JsonSyntaxException ex) {
                throw new ServerException("Не возможно интерпритировать сохраненные данные списков.\n" + ex.toString());
            } finally {
                GsonPool.getInstance().returnGson(gson);
            }
            if (recList == null) {
                recList = new SaveList(new ArrayList<>(), new LinkedList<>());
            }

            try {
                // загружаем зависимости в уже загруженные услуги(дерево зависимостей)
                final DepService root = (DepService) treeDepends.getModel().getRoot();
                final Enumeration<DepService> e = root.children();
                while (e.hasMoreElements()) {
                    final DepService ds = e.nextElement();
                    for (LinkedList<Long> rec : recList.dependences) {
                        if (rec.get(0).equals(ds.id)) {
                            rec.stream().forEach((long1) -> {
                                final QService ser = servsTree.getById(long1);
                                if (ser != null && !ser.getId().equals(ds.id)) {
                                    ds.add(new DepService(ser.getName(), long1));
                                }
                            });
                            break;
                        }
                    }
                }

                // загружаем списки сохраненные
                ((DefaultListModel) (listOfLists.getModel())).clear();
                for (ComplexListOfServices rec : recList.backup) {

                    ArrayList<QService> del = new ArrayList<>();
                    rec.listFree.stream().filter((service) -> (!servsTree.hasById(service.getId()))).forEach((service) -> {
                        del.add(service);
                    });
                    rec.listFree.removeAll(del);
                    del.clear();
                    rec.list1.stream().filter((service) -> (!servsTree.hasById(service.getId()))).forEach((service) -> {
                        del.add(service);
                    });
                    rec.list1.removeAll(del);
                    del.clear();
                    rec.list2.stream().filter((service) -> (!servsTree.hasById(service.getId()))).forEach((service) -> {
                        del.add(service);
                    });
                    rec.list2.removeAll(del);
                    del.clear();
                    rec.list3.stream().filter((service) -> (!servsTree.hasById(service.getId()))).forEach((service) -> {
                        del.add(service);
                    });
                    rec.list3.removeAll(del);
                    del.clear();
                    rec.list4.stream().filter((service) -> (!servsTree.hasById(service.getId()))).forEach((service) -> {
                        del.add(service);
                    });
                    rec.list4.removeAll(del);
                    del.clear();
                    rec.list5.stream().filter((service) -> (!servsTree.hasById(service.getId()))).forEach((service) -> {
                        del.add(service);
                    });
                    rec.list5.removeAll(del);

                    ((DefaultListModel) (listOfLists.getModel())).addElement(rec);
                }
            } catch (ServerException ex) {
                System.err.println("Востановление состояния сервера после изменения конфигурации. " + ex);
                clearAllQueue();
                QLog.l().logger().error("Востановление состояния сервера после изменения конфигурации. Для выключения сервера используйте команду exit. ", ex);
            }
        }
        saveState();
        QLog.l().logger().info("Восстановление списков завершено.");
    }

    private void buttonSetInLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSetInLineActionPerformed
        if (listFreeServices.getModel().getSize() + listServ1.getModel().getSize() + listServ2.getModel().getSize() + listServ3.getModel().getSize() + listServ4.getModel().getSize() + listServ5.getModel().getSize() == 0) {
            return;
        }

        String[] ss = {translate.getString("low"), translate.getString("normal"), translate.getString("hight"), translate.getString("vip")};
        final String name = (String) JOptionPane.showInputDialog(this,
                translate.getString("selectPrrity"),
                translate.getString("selectingPriorty"),
                JOptionPane.QUESTION_MESSAGE,
                null,
                ss,
                translate.getString("normal"));
        //Если не выбрали, то выходим
        if (name != null) {
            int priority = 1;
            if (name.equalsIgnoreCase(ss[0])) {
                priority = 0;
            }
            if (name.equalsIgnoreCase(ss[1])) {
                priority = 1;
            }
            if (name.equalsIgnoreCase(ss[2])) {
                priority = 2;
            }
            if (name.equalsIgnoreCase(ss[3])) {
                priority = 3;
            }

            //Если услуга требует ввода данных пользователем, то нужно получить эти данные из диалога ввода
            String inputData = null;
            String caption = "";
            boolean flag = false;
            for (Object object : ((DefaultListModel) (listFreeServices.getModel())).toArray()) {
                flag = flag || ((QService) object).getInput_required();
                if (flag) {
                    caption = ((QService) object).getInput_caption();
                    break;
                }
            }
            if (!flag) {
                for (Object object : ((DefaultListModel) (listServ1.getModel())).toArray()) {
                    flag = flag || ((QService) object).getInput_required();
                    if (flag) {
                        caption = ((QService) object).getInput_caption();
                        break;
                    }
                }
            }
            if (!flag) {
                for (Object object : ((DefaultListModel) (listServ2.getModel())).toArray()) {
                    flag = flag || ((QService) object).getInput_required();
                    if (flag) {
                        caption = ((QService) object).getInput_caption();
                        break;
                    }
                }
            }
            if (!flag) {
                for (Object object : ((DefaultListModel) (listServ3.getModel())).toArray()) {
                    flag = flag || ((QService) object).getInput_required();
                    if (flag) {
                        caption = ((QService) object).getInput_caption();
                        break;
                    }
                }
            }
            if (!flag) {
                for (Object object : ((DefaultListModel) (listServ4.getModel())).toArray()) {
                    flag = flag || ((QService) object).getInput_required();
                    if (flag) {
                        caption = ((QService) object).getInput_caption();
                        break;
                    }
                }
            }
            if (!flag) {
                for (Object object : ((DefaultListModel) (listServ5.getModel())).toArray()) {
                    flag = flag || ((QService) object).getInput_required();
                    if (flag) {
                        caption = ((QService) object).getInput_caption();
                        break;
                    }
                }
            }
            if (flag) {
                inputData = (String) JOptionPane.showInputDialog(this, caption, "***", 3, null, null, ""); //NOI18N
                if (inputData == null || inputData.isEmpty()) {
                    return;
                }
            }

            final LinkedList<LinkedList<LinkedList<Long>>> ids = new LinkedList<>();
            if (listFreeServices.getModel().getSize() != 0) {
                final LinkedList<LinkedList<Long>> list = new LinkedList<>();
                for (Object object : ((DefaultListModel) (listFreeServices.getModel())).toArray()) {
                    list.add(((DepServiceTree) (treeDepends.getModel())).getDeps(((QService) object).getId()));
                }
                ids.add(list);
            }
            if (listServ1.getModel().getSize() != 0) {
                final LinkedList<LinkedList<Long>> list = new LinkedList<>();
                for (Object object : ((DefaultListModel) (listServ1.getModel())).toArray()) {
                    list.add(((DepServiceTree) (treeDepends.getModel())).getDeps(((QService) object).getId()));
                }
                ids.add(list);
            }
            if (listServ2.getModel().getSize() != 0) {
                final LinkedList<LinkedList<Long>> list = new LinkedList<>();
                for (Object object : ((DefaultListModel) (listServ2.getModel())).toArray()) {
                    list.add(((DepServiceTree) (treeDepends.getModel())).getDeps(((QService) object).getId()));
                }
                ids.add(list);
            }
            if (listServ3.getModel().getSize() != 0) {
                final LinkedList<LinkedList<Long>> list = new LinkedList<>();
                for (Object object : ((DefaultListModel) (listServ3.getModel())).toArray()) {
                    list.add(((DepServiceTree) (treeDepends.getModel())).getDeps(((QService) object).getId()));
                }
                ids.add(list);
            }
            if (listServ4.getModel().getSize() != 0) {
                final LinkedList<LinkedList<Long>> list = new LinkedList<>();
                for (Object object : ((DefaultListModel) (listServ4.getModel())).toArray()) {
                    list.add(((DepServiceTree) (treeDepends.getModel())).getDeps(((QService) object).getId()));
                }
                ids.add(list);
            }
            if (listServ5.getModel().getSize() != 0) {
                final LinkedList<LinkedList<Long>> list = new LinkedList<>();
                for (Object object : ((DefaultListModel) (listServ5.getModel())).toArray()) {
                    list.add(((DepServiceTree) (treeDepends.getModel())).getDeps(((QService) object).getId()));
                }
                ids.add(list);
            }
            final QCustomer customer;
            try {
                customer = NetCommander.standInSetOfServices(netProperty, ids, "1", priority, inputData); //NOI18N
            } catch (Exception ex) {
                throw new ClientException("admin.print_ticket_error", ex);
            }
            //*******************
            // Напечатаем номерок
            //*******************
            printTicket(customer, (ATreeModel) treeServices.getModel());

            String pref = customer.getPrefix();
            pref = "".equals(pref) ? "" : pref + "-";
            JOptionPane.showMessageDialog(this, FAdmin.getLocaleMessage("admin.print_ticket.title") + " \"" + customer.getService().getName() + "\". " + FAdmin.getLocaleMessage("admin.print_ticket.title_1") + " \"" + pref + customer.getNumber() + "\"." + "\n\n" + customer.getService().getDescription(), FAdmin.getLocaleMessage("admin.print_ticket.caption"), JOptionPane.INFORMATION_MESSAGE);

        }
    }//GEN-LAST:event_buttonSetInLineActionPerformed

    public static synchronized void printTicket(final QCustomer customer, final ATreeModel tm) {

        // поддержка расширяемости плагинами
        boolean flag = false;
        for (final IPrintTicket event : ServiceLoader.load(IPrintTicket.class
        )) {
            QLog.l()
                    .logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
            try {
                flag = event.printTicketComplex(customer, tm);
            } catch (Throwable tr) {
                QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: " + tr);
            }
            // раз напечатили и хорошь
            if (flag) {
                return;
            }
        }

        final Printable canvas = new Printable() {

            private int write(String text, int line, int x, double kx, double ky) {
                g2.scale(kx, ky);
                final int y = (int) Math.round((WelcomeParams.getInstance().topMargin + line * WelcomeParams.getInstance().lineHeigth) / ky);
                g2.drawString(text, x, y);
                g2.scale(1 / kx, 1 / ky);
                return y;
            }

            private int writeLongString(String text, int line) {
                while (text.length() != 0) {
                    String prn;
                    if (text.length() > WelcomeParams.getInstance().lineLenght) {
                        int fl = 0;
                        for (int i = WelcomeParams.getInstance().lineLenght; i > 0; i--) {

                            if (" ".equals(text.substring(i - 1, i))) {
                                fl = i;
                                break;
                            }
                        }
                        int pos = fl == 0 ? WelcomeParams.getInstance().lineLenght : fl;
                        prn = text.substring(0, pos);
                        text = text.substring(pos, text.length());
                    } else {
                        prn = text;
                        text = "";
                    }
                    write(prn, ++line, WelcomeParams.getInstance().leftMargin, 1, 1);
                }
                return line;
            }
            Graphics2D g2;

            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex >= 1) {
                    return Printable.NO_SUCH_PAGE;
                }
                g2 = (Graphics2D) graphics;
                if (WelcomeParams.getInstance().logo) {
                    g2.drawImage(Uses.loadImage(this, WelcomeParams.getInstance().logoImg, "/ru/apertum/qsystem/client/forms/resources/logo_ticket_a.png"), WelcomeParams.getInstance().logoLeft, WelcomeParams.getInstance().logoTop, null);
                }
                g2.scale(WelcomeParams.getInstance().scaleHorizontal, WelcomeParams.getInstance().scaleVertical);
                //позиционируем начало координат 
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                int line = 1;
                write(((QService) tm.getRoot()).getDescription(), line, WelcomeParams.getInstance().leftMargin, 1.5, 1.5);
                line++;
                write(FWelcome.getLocaleMessage("ticket.your_number"), ++line, 80, 1, 1);

                int x;
                final String num = ("".equals(customer.getPrefix()) ? "" : customer.getPrefix() + "-") + customer.getNumber();
                switch (num.length()) {
                    case 1:
                        x = 21;
                        break;
                    case 2:
                        x = 18;
                        break;
                    case 3:
                        x = 15;
                        break;
                    case 4:
                        x = 12;
                        break;
                    case 5:
                        x = 9;
                        break;
                    case 6:
                        x = 6;
                        break;
                    case 7:
                        x = 3;
                        break;
                    default: {
                        x = 0;
                    }
                }
                write(num, ++line + 2, x, 6, 3);

                line = line + 3;

                write(FWelcome.getLocaleMessage("ticket.time") + " "
                        + (Locales.getInstance().isRuss
                                ? Uses.getRusDate(customer.getStandTime(), "dd MMMM HH:mm")
                                : (Locales.getInstance().isUkr
                                        ? Uses.getUkrDate(customer.getStandTime(), "dd MMMM HH:mm")
                                        : Locales.getInstance().format_for_label.format(customer.getStandTime()))), ++line, WelcomeParams.getInstance().leftMargin, 1.5, 1);

                // write(Locales.getInstance().isRuss ? Uses.getRusDate(customer.getStandTime(), "dd MMMM HH:mm") : (Locales.getInstance().isUkr ? Uses.getUkrDate(customer.getStandTime(), "dd MMMM HH:mm") : Uses.format_for_label.format(customer.getStandTime())), ++line, WelcomeParams.getInstance().leftMargin, 1, 1);
                // если клиент что-то ввел, то напечатаем это на его талоне
                if (customer.getService().getInput_required()) {
                    write(customer.getService().getTextToLocale(QService.Field.INPUT_CAPTION).replaceAll("<.*?>", ""), ++line, WelcomeParams.getInstance().leftMargin, 1, 1);
                    write(customer.getInput_data(), ++line, WelcomeParams.getInstance().leftMargin, 1, 1);
                    // если требуется, то введеное напечатаем как qr-код для быстрого считывания сканером
                    if (WelcomeParams.getInstance().input_data_qrcode) {
                        try {
                            final int matrixWidth = 130;
                            final HashMap<EncodeHintType, String> hints = new HashMap();
                            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                            final BitMatrix matrix = new QRCodeWriter().encode(customer.getInput_data(), BarcodeFormat.QR_CODE, matrixWidth, matrixWidth, hints);
                            //final BitMatrix matrix = new MultiFormatWriter().encode(customer.getInput_data(), BarcodeFormat.QR_CODE, matrixWidth, matrixWidth);
                            //Write Bit Matrix as image
                            final int y = (int) Math.round((WelcomeParams.getInstance().topMargin + line * WelcomeParams.getInstance().lineHeigth) / 1);
                            for (int i = 0; i < matrixWidth; i++) {
                                for (int j = 0; j < matrixWidth; j++) {
                                    if (matrix.get(i, j)) {
                                        g2.fillRect(WelcomeParams.getInstance().leftMargin * 2 + i, y + j - 10, 1, 1);
                                    }
                                }
                            }
                            line = line + 9;
                        } catch (WriterException ex) {
                            QLog.l().logger().error("Ошибка вывода штрихкода QR. " + ex);
                        }
                    }
                }

                write(FWelcome.getLocaleMessage("ticket.wait"), ++line, WelcomeParams.getInstance().leftMargin, 1.8, 1);

                line++;
                //write(FWelcome.getLocaleMessage("ticket.service"), ++line, WelcomeParams.getInstance().leftMargin, 1.5, 1);
                //write("№ 1 _____________________________", ++line, WelcomeParams.getInstance().leftMargin, 1.5, 1);
                g2.drawRect(WelcomeParams.getInstance().leftMargin, WelcomeParams.getInstance().topMargin + line * WelcomeParams.getInstance().lineHeigth,
                        (int) (WelcomeParams.getInstance().lineHeigth * 1.1), (int) (WelcomeParams.getInstance().lineHeigth * 1.1));
                final long n1 = customer.getService().getId();
                String name = " 1    " + customer.getService().getTextToLocale(QService.Field.NAME);
                line = writeLongString(name, line);
                name = customer.getService().getTextToLocale(QService.Field.DESCRIPTION);
                line = writeLongString(name, line);
                // если в услуге есть что напечатать на талоне, то напечатаем это на его талоне
                if (customer.getService().getTextToLocale(QService.Field.TICKET_TEXT) != null && !customer.getService().getTextToLocale(QService.Field.TICKET_TEXT).isEmpty()) {
                    String tt = customer.getService().getTextToLocale(QService.Field.TICKET_TEXT);
                    line = writeLongString(tt, line);
                }

                final LinkedList<QService> servs = new LinkedList<>();
                customer.getComplexId().stream().forEach((ids) -> {
                    ids.stream().filter((id) -> (id.getFirst() != n1)).forEach((id) -> {
                        QServiceTree.sailToStorm(tm.getRoot(), (TreeNode service) -> {
                            final QService serv = (QService) service;
                            if (id.getFirst() == serv.getId().longValue()) {
                                servs.add(serv);
                            }
                        });
                    });
                });
                for (QService qService : servs) {
                    ++line;
                    //write("№   _____________________________", ++line, WelcomeParams.getInstance().leftMargin, 1.5, 1);

                    g2.drawRect(WelcomeParams.getInstance().leftMargin, WelcomeParams.getInstance().topMargin + line * WelcomeParams.getInstance().lineHeigth,
                            (int) (WelcomeParams.getInstance().lineHeigth * 1.1), (int) (WelcomeParams.getInstance().lineHeigth * 1.1));
                    String str = "        " + qService.getTextToLocale(QService.Field.NAME);
                    line = writeLongString(str, line);
                    str = qService.getTextToLocale(QService.Field.DESCRIPTION);
                    line = writeLongString(str, line);
                    // если в услуге есть что напечатать на талоне, то напечатаем это на его талоне
                    if (qService.getTextToLocale(QService.Field.TICKET_TEXT) != null && !qService.getTextToLocale(QService.Field.TICKET_TEXT).isEmpty()) {
                        String tt = qService.getTextToLocale(QService.Field.TICKET_TEXT);
                        line = writeLongString(tt, line);
                    }
                    ++line;
                }

                write(WelcomeParams.getInstance().promoText, ++line, WelcomeParams.getInstance().leftMargin, 0.7, 0.4);
                int y = write("", ++line, 0, 1, 1);
                if (WelcomeParams.getInstance().barcode != 0) {

                    if (WelcomeParams.getInstance().barcode == 2) {
                        try {
                            final int matrixWidth = 100;
                            final HashMap<EncodeHintType, String> hints = new HashMap();
                            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                            final BitMatrix matrix = new QRCodeWriter().encode(customer.getInput_data(), BarcodeFormat.QR_CODE, matrixWidth, matrixWidth, hints);
                            //Write Bit Matrix as image
                            for (int i = 0; i < matrixWidth; i++) {
                                for (int j = 0; j < matrixWidth; j++) {
                                    if (matrix.get(i, j)) {
                                        g2.fillRect(WelcomeParams.getInstance().leftMargin * 2 + i, y + j - 18, 1, 1);
                                    }
                                }
                            }
                            line = line + 6;
                        } catch (WriterException ex) {
                            QLog.l().logger().error("Ошибка вывода штрихкода QR. " + ex);
                        }
                    }

                    if (WelcomeParams.getInstance().barcode == 1) {
                        try {
                            final Barcode barcode = BarcodeFactory.createCode128B(customer.getId().toString());
                            barcode.setBarHeight(5);
                            barcode.setBarWidth(1);
                            barcode.setDrawingText(false);
                            barcode.setDrawingQuietSection(false);
                            barcode.draw(g2, WelcomeParams.getInstance().leftMargin * 2, y - 7);
                            line = line + 2;
                        } catch (BarcodeException | OutputException ex) {
                            QLog.l().logger().error("Ошибка вывода штрихкода 128B. " + ex);
                        }
                    }
                }

                //Напечатаем текст внизу билета
                name = WelcomeParams.getInstance().bottomText;
                line = writeLongString(name, line);
                write(".", ++line + 2, 0, 1, 1);

                return Printable.PAGE_EXISTS;
            }
        };
        final PrinterJob job = PrinterJob.getPrinterJob();
        if (WelcomeParams.getInstance().printService != null) {
            try {
                job.setPrintService(WelcomeParams.getInstance().printService);
            } catch (PrinterException ex) {
                QLog.l().logger().error("Ошибка установки принтера: ", ex);
            }
        }
        job.setPrintable(canvas);
        try {
            job.print(WelcomeParams.getInstance().printAttributeSet);
            //job.print();
        } catch (PrinterException ex) {
            QLog.l().logger().error("Ошибка печати: ", ex);
        }
    }

    private void listOfListsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listOfListsMouseClicked
        if (evt.getClickCount() > 2) {
            final JList list = ((JList) (evt.getComponent()));
            if (list.getSelectedIndex() != -1) {
                if (JOptionPane.showConfirmDialog(this, locMes("del1") + " \"" + ((DefaultListModel) (list.getModel())).get(list.getSelectedIndex()) + "\"?", locMes("del2"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
                    return;
                }
                ((DefaultListModel) (list.getModel())).remove(list.getSelectedIndex());
                saveState();
            }
        } else {
            listOfLists.getListSelectionListeners()[0].valueChanged(null);
        }
    }//GEN-LAST:event_listOfListsMouseClicked

    private void treeServicesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeServicesMouseClicked
        final TreePath selectedPath = treeServices.getSelectionPath();
        if (selectedPath != null) {
            if (evt == null || evt.getClickCount() > 1) {
                final QService service = (QService) selectedPath.getLastPathComponent();
                final DefaultListModel<QService> sl = (DefaultListModel<QService>) (listFreeServices.getModel());
                if (service.isLeaf() && isGood((QService) service)) {
                    sl.addElement((QService) service);
                }
            }
        }
    }//GEN-LAST:event_treeServicesMouseClicked

    private void treeServicesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_treeServicesKeyPressed
        if (evt.getKeyCode() == 10 || evt.getKeyCode() == 32) {
            treeServicesMouseClicked(null);
        }
    }//GEN-LAST:event_treeServicesKeyPressed

    private void treeDependsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeDependsMouseClicked
        final TreePath selectedPath = treeDepends.getSelectionPath();
        if (selectedPath != null) {
            if (evt == null || evt.getClickCount() > 1) {
                final DepService service = (DepService) selectedPath.getLastPathComponent();
                final DefaultListModel<QService> sl = (DefaultListModel<QService>) (listFreeServices.getModel());
                if (service.isLeaf() && service.getParent().getParent() != null) {
                    (((DepServiceTree) treeDepends.getModel())).removeNodeFromParent(service);
                    saveState();
                }
            }
        }
    }//GEN-LAST:event_treeDependsMouseClicked

    private void listOfListsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listOfListsKeyPressed
        if ((evt.getKeyCode() == KeyEvent.VK_DELETE || evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) && listOfLists.getSelectedIndex() != -1) {
            if (JOptionPane.showConfirmDialog(this, locMes("del1") + " \"" + ((DefaultListModel) (listOfLists.getModel())).get(listOfLists.getSelectedIndex()) + "\"?", locMes("del2"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
                return;
            }
            ((DefaultListModel) (listOfLists.getModel())).remove(listOfLists.getSelectedIndex());
            saveState();
        }
    }//GEN-LAST:event_listOfListsKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClearLists;
    private javax.swing.JButton buttonSaveList;
    private javax.swing.JButton buttonSetInLine;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JList listFreeServices;
    private javax.swing.JList listOfLists;
    private javax.swing.JList listServ1;
    private javax.swing.JList listServ2;
    private javax.swing.JList listServ3;
    private javax.swing.JList listServ4;
    private javax.swing.JList listServ5;
    private javax.swing.JTree treeDepends;
    private javax.swing.JTree treeServices;
    // End of variables declaration//GEN-END:variables
}
