/*
 *  Copyright (C) 2010 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.server.controller;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.PriorityQueue;
import javax.imageio.ImageIO;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import ru.apertum.qsystem.client.forms.AFBoardRedactor;
import ru.apertum.qsystem.client.forms.FBoardConfig;
import ru.apertum.qsystem.client.forms.FIndicatorBoard;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.SoundPlayer;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.QUser;

/**
 * Вывод информации на мониторы. Класс-менеджер вывода информации на общее табло в виде монитора.
 *
 * @author Evgeniy Egorov
 */
public class QIndicatorBoardMonitor extends AIndicatorBoard {

    protected FIndicatorBoard indicatorBoard = null;
    protected String configFile;

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        final String err = ("/".equals(File.separator)) ? "\\" : "/";
        while (configFile.contains(err)) {
            configFile = configFile.replace(err, File.separator);
        }
        this.configFile = configFile;
    }

    /**
     * Заткнуть звук видеороликов для озвучки вызова голосом.
     *
     * @param mute
     */
    public void setMute(boolean mute) {
        if (indicatorBoard != null) {
            indicatorBoard.setMute(mute);
        }
    }

    /**
     * Создадим форму, спозиционируем, сконфигурируем и покажем
     */
    protected void initIndicatorBoard() {
        if (indicatorBoard == null) {
            final Element rootParams = getConfig();
            indicatorBoard = FIndicatorBoard.getIndicatorBoard(rootParams);
            if (indicatorBoard == null) {
                QLog.l().logger().warn("Табло не демонстрируется. Отключено в настройках.");
                return;
            }
            try {
                indicatorBoard.setIconImage(ImageIO.read(QIndicatorBoardMonitor.class.getResource("/ru/apertum/qsystem/client/forms/resources/recent.png")));
            } catch (IOException ex) {
                System.err.println(ex);
            }
            // Определим форму на монитор
            indicatorBoard.toPosition(QConfig.cfg().isDebug(), Integer.parseInt(rootParams.attributeValue("x")), Integer.parseInt(rootParams.attributeValue("y")));

            // ушло в абстрактный метод setLinesCount(indicatorBoard.getLinesCount());
            setPause(indicatorBoard.getPause());
            if (!records.isEmpty()) {
                showOnBoard(new LinkedList<>(records.values()));
            }

            java.awt.EventQueue.invokeLater(() -> {
                indicatorBoard.setVisible(true);
            });
        }
    }

    public QIndicatorBoardMonitor() {
        QLog.l().logger().info("Создание табло для телевизоров или мониторов.");
    }

    @Override
    protected Integer getLinesCount() {
        return indicatorBoard == null ? 1000000 : indicatorBoard.getLinesCount();
    }

    /**
     * Переопределено что бы вызвать появление таблички с номером вызванного поверх главного табло
     *
     * @param user
     * @param customer
     */
    @Override
    public synchronized void inviteCustomer(QUser user, QCustomer customer) {
        super.inviteCustomer(user, customer);
        if (indicatorBoard != null) {
            indicatorBoard.showCallPanel(customer.getFullNumber(), user.getPoint(), user.getTabloText(), customer.getService().getTabloText(), customer.getService().getInputedAsExt() ? (customer.getInput_data() == null ? "" : customer.getInput_data()) : "");
        }
    }

    @Override
    protected void showOnBoard(LinkedList<Record> records) {
        if (indicatorBoard != null) {
            int i = 0;
            for (Record rec : records) {
                indicatorBoard.printRecord(i++, rec.customerPrefix, rec.customerNumber, rec.point, rec.ext_data, rec.getState() == CustomerState.STATE_INVITED ? 0 : -1);
            }
            for (int t = i; t < getLinesCount(); t++) {
                indicatorBoard.printRecord(t, "", null, "", "", -1);
            }
            markShowed(records);

            if (QConfig.cfg().isServer()) { // если это не сервер, то QServiceTree полезет в спринг
                final LinkedList<String> nexts = new LinkedList<>();
                final PriorityQueue<QCustomer> customers = new PriorityQueue<>();
                QServiceTree.getInstance().getNodes().stream().filter((service) -> (service.isLeaf())).forEach((service) -> {
                    service.getClients().stream().forEach((qCustomer) -> {
                        customers.add(qCustomer);
                    });
                });
                QCustomer qCustomer = customers.poll();
                while (qCustomer != null) {
                    nexts.add(qCustomer.getPrefix() + QConfig.cfg().getNumDivider(qCustomer.getPrefix()) + qCustomer.getNumber());
                    qCustomer = customers.poll();
                }
                indicatorBoard.showNext(nexts);
            }
        }
    }

    /**
     * Чтоб ближайших обновить.
     *
     * @param customer
     */
    @Override
    public void customerStandIn(QCustomer customer) {
        if (indicatorBoard != null && QConfig.cfg().isServer()) { // если это не сервер, то QServiceTree полезет в спринг
            final LinkedList<String> nexts = new LinkedList<>();
            final PriorityQueue<QCustomer> customers = new PriorityQueue<>();
            QServiceTree.getInstance().getNodes().stream().filter((service) -> (service.isLeaf())).forEach((service) -> {
                service.getClients().stream().forEach((qCustomer) -> {
                    customers.add(qCustomer);
                });
            });
            QCustomer qCustomer = customers.poll();
            while (qCustomer != null) {
                nexts.add(qCustomer.getPrefix() + qCustomer.getNumber());
                qCustomer = customers.poll();
            }
            indicatorBoard.showNext(nexts);
        }
    }

    /**
     *
     * @param record
     * @deprecated при конфигурации с мониторами в качестве табло пользовательские моники подключаются к пользовательским компам.
     */
    @Deprecated
    @Override
    protected void showToUser(Record record) {
    }

    @Override
    public Element getConfig() {
        final File boardFile = new File(getConfigFile());
        if (boardFile.exists()) {
            try {
                return new SAXReader(false).read(boardFile).getRootElement();
            } catch (DocumentException ex) {
                QLog.l().logger().error("Невозможно прочитать файл конфигурации главного табло. " + ex.getMessage());
                return DocumentHelper.createElement("Ответ");
            }
        } else {
            QLog.l().logger().warn("Файл конфигурации главного табло \"" + configFile + "\" не найден. ");
            return DocumentHelper.createElement("Ответ");
        }
    }

    @Override
    public void saveConfig(Element element) {
        // в темповый файл
        final FileOutputStream fos;
        try {
            fos = new FileOutputStream(getConfigFile());
        } catch (FileNotFoundException ex) {
            throw new ServerException("Не возможно создать файл конфигурации главного табло. " + ex.getMessage());
        }
        try {
            fos.write(element.asXML().getBytes("UTF-8"));
            fos.flush();
            fos.close();
        } catch (IOException ex) {
            throw new ServerException("Не возможно сохранить изменения в поток при сохранении файла конфигурации главного табло." + ex.getMessage());
        }
    }

    @Override
    public AFBoardRedactor getRedactor() {
        if (boardConfig == null) {
            boardConfig = FBoardConfig.getBoardConfig(null, false);
        }
        return boardConfig;
    }
    /**
     * Используемая ссылка на диалоговое окно. Singleton
     */
    private static FBoardConfig boardConfig;

    @Override
    public void showBoard() {
        // Для прерывания звука в роликах при звуковом оповещении.
        SoundPlayer.setStartListener((ActionEvent e) -> {
            setMute(true);
        });
        SoundPlayer.setFinishListener((ActionEvent e) -> {
            setMute(false);
        });
        initIndicatorBoard();
    }

    /**
     * Выключить информационное табло.
     */
    @Override
    public synchronized void close() {
        super.close();
        if (indicatorBoard != null) {
            indicatorBoard.closeVideo();
            indicatorBoard.setVisible(false);
            indicatorBoard = null;
        }
    }

    @Override
    public void refresh() {
        close();
        indicatorBoard = null;
        initIndicatorBoard();
    }

    @Override
    public void clear() {
        records.clear();
        showOnBoard(new LinkedList(records.values()));
    }

    @Override
    public String getDescription() {
        return "Default Tablo.";
    }

    @Override
    public long getUID() {
        return 1;
    }

    @Override
    public Object getBoardForm() {
        return indicatorBoard;
    }
}
