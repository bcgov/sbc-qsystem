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
package ru.apertum.qsystem.client.model;

import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.client.common.WelcomeParams;
import ru.apertum.qsystem.client.forms.*;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.cmd.RpcGetServiceState.ServiceState;
import ru.apertum.qsystem.common.model.ATalkingClock;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.IWelcome;
import ru.apertum.qsystem.server.model.QAdvanceCustomer;
import ru.apertum.qsystem.server.model.QAuthorizationCustomer;
import ru.apertum.qsystem.server.model.QService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Сдесь реализован класс кнопки пользователя при выборе услуги. Класс кнопки пользователя при выборе услуги. Кнопка умеет слать задание на сервер для
 * постановки в очередь.
 *
 * @author Evgeniy Egorov
 */
public class QButton extends JButton {

    /**
     * Услуга, висящая на кнопке
     */
    private final QService service;
    /**
     * Маркировка сайта, который соотверствует услуге, которая висит на этой кнопке.
     */
    private final FWelcome form;
    private final JPanel parent;
    /**
     * Состояния кнопок
     */
    private final boolean isActive;

    public boolean isIsActive() {
        return isActive;
    }
    private final boolean isVisible;

    public boolean isIsVisible() {
        return isVisible;
    }
    private final boolean isForPrereg;
    private boolean isDummy = false;

    public boolean isIsForPrereg() {
        return isForPrereg;
    }
    private final static int FOR_DUMMY = 3;
    private final static int FOR_PREREG = 2;
    private final static int NO_ACTIVE = 0;
    private final static int NO_VISIBLE = -1;
    private final static HashMap<String, Image> IMGS = new HashMap<>();

    public QButton() {
        service = null;
        form = null;
        parent = null;
        isActive = true;
        isVisible = true;
        isForPrereg = false;
    }

    public QButton(String resourceName) {
        service = null;
        form = null;
        parent = null;
        isActive = true;
        isVisible = true;
        isForPrereg = false;

        init(resourceName);
    }

    private void init(String resourceName) {
        setFocusPainted(false);
        // Нарисуем картинку на кнопке если надо. Загрузить можно из файла или ресурса
        if ("".equals(resourceName)) {
            background = null;
        } else {
            background = IMGS.get(resourceName);
            if (background == null) {
                File file = new File(resourceName);
                if (file.exists()) {
                    try {
                        background = ImageIO.read(file);
                        IMGS.put(resourceName, background);
                    } catch (IOException ex) {
                        background = null;
                        QLog.l().logger().error(ex);
                    }
                } else {
                    final DataInputStream inStream = new DataInputStream(getClass().getResourceAsStream(resourceName));
                    byte[] b = null;
                    try {
                        b = new byte[inStream.available()];
                        inStream.readFully(b);
                    } catch (IOException ex) {
                        background = null;
                        QLog.l().logger().error(ex);
                    }
                    background = new ImageIcon(b).getImage();
                    IMGS.put(resourceName, background);
                }
            }
        }

        //займемся внешним видом
        // либо просто стандартная кнопка, либо картинка на кнопке если она есть
        if (background == null) {
            setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED), new BevelBorder(BevelBorder.RAISED)));
        } else {
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
        }
    }

    public QButton(final QService service, FWelcome frm, JPanel prt, String resourceName) {
        super();
        this.form = frm;
        this.service = service;
        this.parent = prt;

        // посмотрим доступна ли данная услуга или группа услуг
        isVisible = NO_VISIBLE != service.getStatus();
        isActive = NO_ACTIVE != service.getStatus() && isVisible;
        isForPrereg = FOR_PREREG == service.getStatus() && isActive;

        init(service, resourceName);
    }

    private void init(final QService service, String resourceName) {

        setFocusPainted(false);

        isDummy = FOR_DUMMY == service.getStatus() && isActive;
        QLog.l().logger().trace("Create button by Steven for \"" + service.getName() + "\" ID=" + service.getId() + " states:"
                + (isVisible ? " Visible" : " Hide") + (isActive ? " Active" : " Pasive") + (isForPrereg ? " ForPrereg" : " ForAll") + (isDummy ? " Dummy" : " Real"));
        if (!isVisible) {
            setVisible(false);
            return;
        }

        // Нарисуем картинку на кнопке если надо. Загрузить можно из файла или ресурса
        if (isDummy || "".equals(resourceName)) {
            background = null;
        } else {
            background = IMGS.get(resourceName);
            if (background == null) {
                File file = new File(resourceName);
                if (file.exists()) {
                    try {
                        background = ImageIO.read(file);
                        IMGS.put(resourceName, background);
                    } catch (IOException ex) {
                        background = null;
                        QLog.l().logger().error(ex);
                    }
                } else {
                    final DataInputStream inStream = new DataInputStream(getClass().getResourceAsStream(resourceName));
                    byte[] b = null;
                    try {
                        b = new byte[inStream.available()];
                        inStream.readFully(b);
                    } catch (IOException ex) {
                        background = null;
                        QLog.l().logger().error(ex);
                    }
                    background = new ImageIcon(b).getImage();
                    IMGS.put(resourceName, background);
                }
            }
        }

        refreshText();
        setSize(1, 1);
        if (WelcomeParams.getInstance().buttonImg) {
            if (service.isLeaf()) {
                setIcon(new ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/serv_btn.png")));
            } else {
                setIcon(new ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/folder.png")));
            }
        }

        // заглушка. не надо ей ничего отрисовывать кроме надписи на кнопки. и нажимать ее не надо
        if (isDummy) {
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            return;
        }

        //займемся внешним видом
        // либо просто стандартная кнопка, либо картинка на кнопке если она есть
        if (background == null) {
            setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED), new BevelBorder(BevelBorder.RAISED)));
        } else {
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
        }

        addActionListener((ActionEvent e) -> {
            QLog.l().logger().info("Pressed button \"" + service.getName() + "\" ID=" + service.getId());
            for (final IWelcome event : ServiceLoader.load(IWelcome.class)) {
                QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
                try {
                    event.buttonPressed(service);
                } catch (Throwable tr) {
                    QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: " + tr);
                }
            }
            try {
                // "Услуги" и "Группа" это одно и тоже.
                if (!service.isLeaf()) {
                    form.showButtons(service, (JPanel) getParent());
                    if (form.clockBack.isActive()) {
                        form.clockBack.stop();
                    }
                    if (form.clockBack.getInterval() > 999) {
                        form.clockBack.start();
                    }
                }
                if (service.isLeaf()) {// отсюда и до конца :)

                    // просто если в тексте предварительного чтива URL, то надо показать этот УРЛ и не ставить в очередь. Пусть почитает и далее сам решит что и зачем без чтива.
                    final String pattern = "(file|http|ftp|https):\\/\\/\\/*[\\w\\-_:\\/]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
                    final String txt = Uses.prepareAbsolutPathForImg(service.getPreInfoHtml().trim());
                    Pattern replace = Pattern.compile(pattern);
                    Matcher matcher = replace.matcher(txt);
                    if (isActive && txt != null && !txt.isEmpty()
                            && (matcher.matches() || txt.contains("localhost") || txt.contains("127.0.0.1"))) {
                        FInfoDialogWeb.showInfoDialogWeb(this.form, true, true, WelcomeParams.getInstance().delayBack * 4, txt);
                        return;
                    }

                    //  в зависимости от активности формируем сообщение и шлем запрос на сервер об статистике
                    if (isActive) {
                        // Услуга активна. Посмотрим не предварительная ли это запись.
                        // Если Предварительная запись, то пытаемся предватительно встать и выходим из обработке кнопки.
                        if (FWelcome.isAdvanceRegim()) {
                            form.setAdvanceRegim(false);

                            //Если услуга требует ввода данных пользователем, то нужно получить эти данные из диалога ввода, т.к. потом при постановки в очередь предварительных
                            // нет ввода данных, только номера регистрации.
                            String inputData = null;
                            if (service.getInput_required()) {
                                inputData = FInputDialog.showInputDialog(form, true, FWelcome.netProperty, false, WelcomeParams.getInstance().delayBack, service.getTextToLocale(QService.Field.INPUT_CAPTION));
                                if (inputData == null) {
                                    return;
                                }
                            }

                            final QAdvanceCustomer res = FAdvanceCalendar.showCalendar(form, true, FWelcome.netProperty, service, true, WelcomeParams.getInstance().delayBack * 2, form.advancedCustomer, inputData, "");
                            //Если res == null значит отказались от выбора
                            if (res == null) {
                                form.showMed();
                                return;
                            }

                            // приложим введенное клиентом чтобы потом напечатать.
                            if (service.getInput_required()) {
                                res.setAuthorizationCustomer(new QAuthorizationCustomer(inputData));
                            }

                            for (final IWelcome event : ServiceLoader.load(IWelcome.class)) {
                                QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
                                try {
                                    event.readyNewAdvCustomer(res, service);
                                } catch (Throwable tr) {
                                    QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: " + tr);
                                }
                            }

                            //вешаем заставку
                            final GregorianCalendar gc_time = new GregorianCalendar();
                            gc_time.setTime(res.getAdvanceTime());
                            int t = gc_time.get(GregorianCalendar.HOUR_OF_DAY);
                            String t_m = ("" + gc_time.get(GregorianCalendar.MINUTE) + "0000").substring(0, 2);
                            if (t == 0) {
                                t = 24;
                                gc_time.add(GregorianCalendar.HOUR_OF_DAY, -1);
                            }
                            form.showDelayFormPrint("<HTML><p align=center>"
                                    + "<span style='font-size:60.0pt;color:green'>" + FWelcome.getLocaleMessage("qbutton.take_adv_ticket") + "</span>"
                                    + "<br>"
                                    + "<span style='font-size:80.0pt;color:blue'>"
                                    + (Locales.getInstance().isRuss ? Uses.getRusDate(gc_time.getTime(), Locales.DATE_FORMAT_FULL) : Locales.getInstance().format_dd_MMMM_yyyy.format(gc_time.getTime()))
                                    + "</span><br>"
                                    + "<span style='font-size:80.0pt;color:blue'>"
                                    + FWelcome.getLocaleMessage("qbutton.take_adv_ticket_come_to") + " " + t + ":" + t_m + " "
                                    + "</span>"
                                    + "</p>",
                                    WelcomeParams.getInstance().getTicketImg);
                            // печатаем результат
                            new Thread(() -> {
                                QLog.l().logger().info("Печать этикетки бронирования.");
                                FWelcome.printTicketAdvance(res);
                            }).start();
                            // выходим, т.к. вся логика предварительной записи в форме предварительного календаря
                            return;
                        }

                        /*
                         * Если только возможна предвариловка, то просто заканчиваем с сообщением этого файта
                         */
                        if (isForPrereg) {
                            form.lock(WelcomeParams.getInstance().patternInfoDialog.replace("dialog.message", FWelcome.getLocaleMessage("messages.only_for_prereg")));
                            form.clockUnlockBack.start();
                            return;
                        }

                        // Отсюда действие по нажатия кнопки чтоб просто встать в очередь
                        // Узнать, есть ли информация для прочнения в этой услуге.
                        // Если текст информации не пустой, то показать диалог сэтим текстом
                        // У диалога должны быть кнопки "Встать в очередь", "Печать", "Отказаться".
                        // если есть текст, то показываем диалог
                        if (service.getPreInfoHtml() != null && !"".equals(service.getPreInfoHtml())) {

                            // поддержка расширяемости плагинами
                            // покажим преинфо из плагинов
                            boolean flag = true;
                            for (final IWelcome event : ServiceLoader.load(IWelcome.class)) {
                                QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
                                boolean f = false;
                                try {
                                    f = event.showPreInfoDialog(form, FWelcome.netProperty, service.getTextToLocale(QService.Field.PRE_INFO_HTML), service.getTextToLocale(QService.Field.PRE_INFO_PRINT_TEXT), true, true, WelcomeParams.getInstance().delayBack * 2);
                                } catch (Throwable tr) {
                                    QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: " + tr);
                                }
                                flag = flag && f;
                            }

                            if (!flag || !FPreInfoDialog.showPreInfoDialog(form, service.getTextToLocale(QService.Field.PRE_INFO_HTML), service.getTextToLocale(QService.Field.PRE_INFO_PRINT_TEXT), true, true, WelcomeParams.getInstance().delayBack * 2)) {
                                // выходим т.к. кастомер отказался продолжать
                                return;
                            }
                        }

                        // Если режим инфокиоска, то сразу уходим, т.к. вставать в очередь нет нужды
                        // Показали информацию и все
                        if (FWelcome.isInfo) {
                            return;
                        }

                        // узнать статистику по предлагаемой услуги и спросить потенциального кастомера
                        // будет ли он стоять или нет
                         ServiceState servState;
                        try {
                            servState = NetCommander.aboutService(FWelcome.netProperty, service.getId());
                        } catch (Exception ex) {
                            // гасим жестоко, пользователю незачем видеть ошибки. выставим блокировку
                            QLog.l().logger().error("Гасим жестоко. Невозможно отправить команду на сервер. ", ex);
                            form.lock(form.LOCK_MESSAGE);
                            return;
                        }
                        // Если приехал текст причины, то покажем ее и не дадим встать в очередь
                        if (servState.getMessage() != null && !"".equals(servState.getMessage())) {
                            form.lock(WelcomeParams.getInstance().patternInfoDialog.replace("dialog.message", servState.getMessage()));
                            form.clockUnlockBack.start();
                            return;
                        }
                        // Если услуга не обрабатывается ни одним пользователем то в count вернется Uses.LOCK_INT
                        // вот трех еще потерплю, а больше низачто!
                        if (servState.getCode() == Uses.LOCK_INT) {
                            form.lock(WelcomeParams.getInstance().patternInfoDialog.replace("dialog.message", FWelcome.getLocaleMessage("qbutton.service_not_available")));
                            form.clockUnlockBack.start();
                            return;
                        }
                        if (servState.getCode() == Uses.LOCK_FREE_INT) {
                            form.lock(WelcomeParams.getInstance().patternInfoDialog.replace("dialog.message", FWelcome.getLocaleMessage("qbutton.service_not_available_by_schedule")));
                            form.clockUnlockBack.start();
                            return;
                        }
                        if (servState.getCode() == Uses.LOCK_PER_DAY_INT) {
                            form.lock(WelcomeParams.getInstance().patternInfoDialog.replace("dialog.message", FWelcome.getLocaleMessage("qbutton.clients_enough")));
                            form.clockUnlockBack.start();
                            return;
                        }
                        if (WelcomeParams.getInstance().askLimit < 1 || servState.getCode() >= WelcomeParams.getInstance().askLimit) {
                            // Выведем диалог о том будет чел сотять или пошлет нахер всю контору.
                            if (!FConfirmationStart2.getMayContinue(form, servState.getCode())) {
                                return;
                            }
                        }
                    }
                    // ну если неактивно, т.е. надо показать отказ, или продолжить вставать в очередь
                    if (form.clockBack.isActive()) {
                        form.clockBack.stop();//т.к. есть какой-то логический конец, то не надо в корень автоматом.

                    }

                    String inputData = null;
                    ATalkingClock clock;
                    if (!isActive) {
                        clock = form.showDelayFormPrint(WelcomeParams.getInstance().patternInfoDialog.replace("dialog.message", FWelcome.getLocaleMessage("qbutton.right_naw_can_not")) + "</span>", "/ru/apertum/qsystem/client/forms/resources/noActive.png");
                    } else {
                        //Если услуга требует ввода данных пользователем, то нужно получить эти данные из диалога ввода
                        if (service.getInput_required()) {

                            // поддержка расширяемости плагинами
                            // запросим ввода данных
                            String flag = null;
                            int cntIn = 0;
                            for (final IWelcome event : ServiceLoader.load(IWelcome.class)) {
                                QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
                                String f = null;
                                try {
                                    f = event.showInputDialog(form, true, FWelcome.netProperty, false, WelcomeParams.getInstance().delayBack, service.getTextToLocale(QService.Field.INPUT_CAPTION), service);
                                    cntIn++;
                                } catch (Throwable tr) {
                                    QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: " + tr);
                                }
                                flag = (f == null ? flag : (flag == null ? f : (flag + " " + f)));
                            }
                            if (cntIn != 0 && flag == null) {
                                return;
                            }
                            inputData = (flag == null ? FInputDialog.showInputDialog(form, true, FWelcome.netProperty, false, WelcomeParams.getInstance().delayBack, service.getTextToLocale(QService.Field.INPUT_CAPTION))
                                    : flag);
                            if (inputData == null) {
                                return;
                            }
                            // если ввели, то нужно спросить у сервера есть ли возможность встать в очередь с такими введенными данными

                            //@return 1 - превышен, 0 - можно встать. 2 - забанен
                            int limitPersonOver;
                            try {
                                limitPersonOver = NetCommander.aboutServicePersonLimitOver(FWelcome.netProperty, service.getId(), inputData);
                            } catch (Exception ex) {
                                // гасим жестоко, пользователю незачем видеть ошибки. выставим блокировку
                                QLog.l().logger().error("Гасим жестоко опрос превышения лимита по введенным данным, но не лочим киоск. Невозможно отправить команду на сервер. ", ex);
                                return;
                            }
                            if (limitPersonOver != 0) {
                                form.lock(limitPersonOver == 1 ? WelcomeParams.getInstance().patternInfoDialog.replace("dialog.message", FWelcome.getLocaleMessage("qbutton.ticket_with_nom_finished"))
                                        : "<HTML><p align=center><b><span style='font-size:60.0pt;color:red'>" + FWelcome.getLocaleMessage("qbutton.denail_by_lost") + "</span></b></p>");
                                form.clockUnlockBack.start();
                                return;
                            }
                        }
                        clock = form.showDelayFormPrint(WelcomeParams.getInstance().patternInfoDialog.replace("dialog.message", FWelcome.getLocaleMessage("qbutton.take_ticket")),
                                WelcomeParams.getInstance().getTicketImg);
                    }

                    //выполним задание если услуга активна
                    if (isActive) {
                         QCustomer res;
                        try {
                            res = NetCommander.standInService(FWelcome.netProperty, service.getId(), "1", 1, inputData);
                        } catch (Exception ex) {
                            // гасим жестоко, пользователю незачем видеть ошибки. выставим блокировку
                            QLog.l().logger().error("Невозможно отправить команду на сервер. ", ex);
                            form.lock(form.LOCK_MESSAGE);
                            clock.stop();
                            return;
                        }
                        for (final IWelcome event : ServiceLoader.load(IWelcome.class)) {
                            QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
                            try {
                                event.readyNewCustomer(res, service);
                            } catch (Throwable tr) {
                                QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: " + tr);
                            }
                        }
                        clock.stop();
                        form.showDelayFormPrint(WelcomeParams.getInstance().patternGetTicket.replace("dialogue_text.take_ticket", FWelcome.getLocaleMessage("qbutton.take_ticket")).
                                replace("dialogue_text.your_nom", FWelcome.getLocaleMessage("qbutton.your_nom")).
                                replace("dialogue_text.number", res.getPrefix() + QConfig.cfg().getNumDivider(res.getPrefix()) + res.getNumber()),
                                WelcomeParams.getInstance().getTicketImg);

                        QLog.l().logger().info("Печать этикетки.");

                        new Thread(() -> {
                            FWelcome.printTicket(res);
                        }).start();
                    }
                }
            } catch (Exception ex) {
                QLog.l().logger().error("Ошибка при попытки обработать нажатие кнопки постановки в ачередь. " + ex.toString());
            }
        });//addActionListener

    }
    private Image background;

    @Override
    public void paintComponent(Graphics g) {
        if (background != null) {
            //Image scaledImage = background.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH); // это медленный вариант
            final Image scaledImage = resizeToBig(background, getWidth(), getHeight());
            final Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(scaledImage, 0, 0, null, null);
            super.paintComponent(g);
        } else {
            super.paintComponent(g);
        }
    }

    private Image resizeToBig(Image originalImage, int biggerWidth, int biggerHeight) {
        final BufferedImage resizedImage = new BufferedImage(biggerWidth, biggerHeight, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = resizedImage.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(originalImage, 0, 0, biggerWidth, biggerHeight, this);
        g.dispose();

        return resizedImage;
    }

    public final void refreshText() {
        setText(Uses.prepareAbsolutPathForImg(service.getTextToLocale(QService.Field.BUTTON_TEXT)));
    }
}
