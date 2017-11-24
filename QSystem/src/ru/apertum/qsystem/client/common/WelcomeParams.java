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
package ru.apertum.qsystem.client.common;

import java.awt.Font;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ClientException;

/**
 * Класс загрузки и предоставления настроек пункта регистрации
 *
 * @author Evgeniy Egorov
 */
public class WelcomeParams {

    /**
     * Константы хранения параметров в файле.
     */
    private static final String POINT = "point";
    private static final String PAPER_WIDHT = "paper_widht";
    private static final String LEFT_MARGIN = "left_margin";
    private static final String TOP_MARGIN = "top_margin";
    private static final String LINE_HEIGTH = "line_heigth";
    private static final String LINE_LENGTH = "line_length";
    private static final String SCALE_VERTICAL = "scale_vertical";
    private static final String SCALE_HORIZONTAL = "scale_horizontal";
    private static final String PRNAME = "printer.Name";
    private static final String EXECUTIVE = "printer.MediaSizeName.EXECUTIVE";
    private static final String PRINTABLE_AREA = "printer.MediaPrintableArea";
    // параметр размера бумаги. A0 A1 ... A10 B0 B2 ... B10 C0 C1 ... C6. Пустое или неверное значение - отключен
    private static final String MEDIA_SIZE_NAME = "printer.MediaSizeName";
    // параметр размера бумаги <ширина,длинна>. Пустое или неверное значение - отключен
    private static final String FIND_MEDIA = "printer.findMedia";
    private static final String ORIENTATION_PRINT = "printer.OrientationRequested";
    private static final String LOGO = "logo";
    private static final String BARCODE = "barcode";
    private static final String INPUT_DATA_QR = "input_data_qrcode";
    private static final String LOGO_LEFT = "logo_left";
    private static final String LOGO_TOP = "logo_top";
    private static final String DELAY_PRINT = "delay_print";
    private static final String DELAY_BACK = "delay_back";
    private static final String LOGO_IMG = "logo_img";
    private static final String BACKGROUND_IMG = "background_img";
    private static final String TXT_FONT_NAME = "ticket_font_name";
    private static final String TXT_FONT_SIZE = "ticket_font_size";
    private static final String TXT_FONT_H1_SIZE = "ticket_fontH1_size";
    private static final String TXT_FONT_H2_SIZE = "ticket_fontH2_size";
    private static final String PROMO_TXT = "promo_text";
    private static final String WAIT_TXT = "wait_text";
    private static final String BOTTOM_TXT = "bottom_text";
    private static final String BOTTOM_GAP = "bottom_gap";
    private static final String ASK_LIMIT = "ask_limit";
    private static final String PAGE_LINES_COUNT = "page_lines_count";
    private static final String INFO_BUTTON = "info_button";// кнопка информационной системы на пункте регистрации
    private static final String RESPONSE_BUTTON = "response_button";// - кнопка обратной связи на пункте регистрации
    private static final String ADVANCE_BUTTON = "advance_button";// - кнопка предварительной записи на пункте регистрации
    private static final String STAND_ADVANCE_BUTTON = "stand_advance_button";// - присутствие кнопки предварительной записи на пункте регистрации (0/1)
    private static final String NUMERIC_KEYBOARD = "numeric_keyboard";// - цифровая клавиатура при вводе юзерской инфы
    private static final String ALPHABETIC_KEYBOARD = "alphabetic_keyboard";// - буквенная клавиатура при вводе юзерской инфы
    private static final String SPEC_KEYBOARD = "spec_keyboard";// - буквенная клавиатура при вводе юзерской инфы
    private static final String INPUT_FONT_SIZE = "input_font_size";// - размер шрифта вводимого текста клиентом
    private static final String LINES_BUTTON_COUNT = "lines_button_count";// - количество рядов кнопок на киоске, если будет привышение, то начнотся листание страниц
    private static final String ONE_COLUMN_BUTTON_COUNT = "one_column_buttons_count";// - количество кнопок на киоске в один стобл
    private static final String TWO_COLUMNS_BUTTON_COUNT = "two_columns_buttons_count";// - количество кнопок на киоске в два столба
    private static final String BUTTON_TYPE = "button_type";// - это внешний вид кнопки. Если его нет или ошибочный, то стандартный вид. Иначе номер вида или картинка в png желательно
    private static final String SERV_BUTTON_TYPE = "serv_button_type";// - вид управляющей кнопки на пункте регистрации. Если его нет или ошибочный, то стандартный вид. Иначе номер вида или картинка в png желательно
    private static final String SERV_VERT_BUTTON_TYPE = "serv_vert_button_type";// - вид вертикальной управляющей кнопки на пункте регистрации. Если его нет или ошибочный, то стандартный вид. Иначе номер вида или картинка в png желательно
    private static final String BUTTON_IMG = "button_img";// - это присутствие пиктограммы услуги или группы на кнопке
    private static final String RESPONSE_IMG = "response_img";// - это присутствие пиктограммы отзыва или группы отзывов на кнопках отзывов
    private static final String BUTTON_TOSTART_IMG = "button_tostart_img";// - это пиктограмма на кнопке "В начало"
    private static final String BUTTON_GOBACK_IMG = "button_goback_img";// - это пиктограмма на кнопке "Назад"
    private static final String TOP_SIZE = "top_size";// - это ширина верхней панели на п.р. с видом кнопок
    private static final String TOP_IMG = "top_img";// - это картинка на верхней панели на п.р. с видом кнопок
    private static final String TOP_IMG_SECONDARY = "top_img_secondary";// - это картинка на верхней панели на п.р. на вторичных диалогах
    private static final String PATTERN_GET_TICKET = "pattern_get_ticket";// - это шаблон текста для диалога забрать талон
    private static final String GET_TICKET_IMG = "get_ticket_img";// - это картинка для диалогаплучения талона. пустое значение - картинка по умолчанию
    private static final String PATTERN_CONFIRMATION_START = "pattern_confirmation_start";// - это шаблон текста для диалога подтверждения стоять в очереди. Встроенный текст dialogue_text.take_ticket dialog.text_before_people [[endRus]]
    private static final String CONFIRMATION_START_IMG = "confirmation_start_img";// - это картинка для диалога подтверждения стоять в очереди. пустое значение - картинка по умолчанию
    private static final String PATTERN_INFO_DIALOG = "pattern_info_dialog";// - это шаблон текста для информационных диалогов Встроенный текст dialog.message
    private static final String PATTERN_PICK_ADVANCE_TITLE = "pattern_pick_advance_title";// - шаблон текста для диалога выбора предварительной услуги. Встроенный текст: dialog_text.part1 dialog_text.part2
    private static final String INFO_BUTTON_HTMLTEXT = "info_button_htmltext";
    private static final String RESPONSE_BUTTON_HTMLTEXT = "response_button_htmltext";
    private static final String TOP_URL = "top_url";
    private static final String BTN_FONT = "serv_button_font";
    private static final String BTN_ADV_FONT = "serv_adv_button_font";
    //#RU Примерный объем талонов в рулоне
    //#EN Approximate amount of tickets in a roll
    private static final String PAPER_SIZE_ALARM = "paper_size_alarm";
    private static final String PAPER_ALARM_STEP = "paper_alarm_step";
    public int point; // указание для какого пункта регистрации услуга, 0-для всех, х-для киоска х.
    public int paperWidht; // ширина талона в пикселах
    public int leftMargin; // отступ слева
    public int topMargin; // отступ сверху
    public int lineHeigth = 12; // Ширина строки
    public int lineLenght = 40; // Длинна стоки на квитанции
    public double scaleVertical = 0.8; // маштабирование по вертикале
    public double scaleHorizontal = 0.8; // машcтабирование по горизонтали
    public PrintRequestAttributeSet printAttributeSet = new HashPrintRequestAttributeSet(); // атрибуты печати ринтера
    public boolean logo = true; // присутствие логотипа на квитанции
    public int barcode = 1; // присутствие штрихкода на квитанции
    public boolean input_data_qrcode = true; // присутствие qr-штрихкода на квитанции если клиент ввел свои персональные данные
    public boolean info = true; // кнопка информационной системы на пункте регистрации
    public String infoURL = null; // кнопка информационной системы на пункте регистрации
    public String responseURL = null; // - кнопка обратной связи на пункте регистрации
    public String infoHtml = ""; // кнопка информационной системы на пункте регистрации
    public String responseHtml = ""; // - кнопка обратной связи на пункте регистрации
    public boolean response = true; // - кнопка обратной связи на пункте регистрации
    public boolean advance = true; // - кнопка предварительной записи на пункте регистрации
    public boolean standAdvance = true; // присутствие кнопки предварительной записи на пункте регистрации (0/1)
    public int logoLeft = 50; // Отступ печати логотипа слева
    public int logoTop = -5; // Отступ печати логотипа сверху
    public String logoImg = "/ru/apertum/qsystem/client/forms/resources/logo_ticket.png"; // логотип сверху
    public String backgroundImg = "/ru/apertum/qsystem/client/forms/resources/fon_welcome.jpg"; // фоновая картинка
    public String ticketFontName = ""; // Шрифт для текста талона
    public int ticketFontSize = 0; // Размер шрифта для текста талона
    public int ticketFontH1Size = 0; // Размер шрифта для текста талона
    public int ticketFontH2Size = 0; // Размер шрифта для текста талона
    public String promoText = "Aperum projects, e-mail: info@aperum.ru"; // промотекст, печатающийся мелким шрифтом перед штрихкодом.
    public String bottomText = "\u041f\u0440\u0438\u044f\u0442\u043d\u043e\u0433\u043e \u043e\u0436\u0438\u0434\u0430\u043d\u0438\u044f. \u0421\u043f\u0430\u0441\u0438\u0431\u043e."; // произвольный текст, печатающийся в конце квитанции после штрихкода
    public int bottomGap = 2; // Сколько строк оставить пустыми в конце напечатанного талона
    public String waitText = ""; // текст, "Ожидайте вызова на табло". Если пусто, то текст по умолчанию.
    public int askLimit = 3; // Критический размер очереди после которого спрашивать клиентов о готовности встать в очередь
    public int pageLinesCount = 30; // Количество строк на странице.
    public int linesButtonCount = 5; // количество рядов кнопок на киоске, если будет привышение, то начнотся листание страниц
    public int oneColumnButtonCount = 3; // количество кнопок на киоске в одном столбце
    public int twoColumnButtonCount = 10; // количество кнопок на киоске в двух столбцах
    public String buttonType = ""; // - это внешний вид кнопки. Если его нет или ошибочный, то стандартный вид. Иначе номер вида или картинка в png желательно
    public String servButtonType = ""; // - это внешний вид сервисной кнопки. Если его нет или ошибочный, то стандартный вид. Иначе номер вида или картинка в png желательно
    public String servVertButtonType = ""; // - это внешний вид вертикальной сервисной кнопки. Если его нет или ошибочный, то стандартный вид. Иначе номер вида или картинка в png желательно
    public boolean buttonImg = true; // - это присутствие пиктограммы услуги или группы на кнопке
    public boolean responseImg = true; // - это присутствие пиктограммы отзыва или группы на кнопке
    public File buttonToStratImg = null; // - это пиктограммы  на кнопке
    public File buttonGoBackImg = null; // - это пиктограммы  на кнопке
    public int topSize = -1; // - это ширина верхней панели на п.р. с видом кнопок
    public String topImg = ""; // - это картинка на верхней панели на п.р. с видом кнопок
    public String topURL = ""; // - а верхней панели пункта регистрации, там где заголовок и картинка в углу, можно вывести вэб-контент по URL. Оставьте пустым если не требуется
    public String topImgSecondary = ""; // - это картинка на верхней панели на п.р. на вторичных диалогах
    public PrintService printService = null; // - это принтер определенный по имени printer.Name
    public String patternGetTicket = ""; // - это шаблон текста для диалога забрать талон
    public String getTicketImg = ""; // - это картинка для диалогаплучения талона. пустое значение - картинка по умолчанию
    public String patternConfirmationStart = ""; // - это это шаблон текста для диалога подтверждения стоять в очереди. Встроенный текст dialogue_text.take_ticket dialog.text_before_people [[endRus]]
    public String confirmationStartImg = ""; // - это картинка для диалога подтверждения стоять в очереди. пустое значение - картинка по умолчанию
    public String patternInfoDialog = ""; // шаблон текста для информационных диалогов Встроенный текст dialog.message
    public String patternPickAdvanceTitle = ""; // шаблон текста для выбора предварительной услуги диалогов Встроенный текст: dialog_text.part1 dialog_text.part2
    public Font btnFont = null;
    public Font btnAdvFont = null;
    /**
     * Задержка заставки при печати в мсек.
     */
    public int delayPrint = 3000;
    public int delayBack = 40000;
    //параметры СОМ-порта для кнопок кнопочного интерфейса
    public String buttons_COM = "COM1";
    public int buttons_databits = 8;
    public int buttons_speed = 9600;
    public int buttons_parity = 0;
    public int buttons_stopbits = 1;
    public boolean numeric_keyboard = true; // - цифровая клавиатура при вводе юзерской инфы
    public boolean alphabetic_keyboard = true; // - буквенная клавиатура при вводе юзерской инфы
    public String spec_keyboard = ""; // - специальная клавиатура при вводе юзерской инфы
    public int input_font_size = 64; // - размер шрифта при вводе юзерской инфы
    public int paper_size_alarm = 700; //  Примерный объем талонов в рулоне
    public int paper_alarm_step = 30; //  Примерный объем талонов в рулоне

    private WelcomeParams() {
        loadSettings();
    }

    public static WelcomeParams getInstance() {
        return WelcomeParamsHolder.INSTANCE;
    }

    /**
     * Загрузим настройки.
     */
    private void loadSettings() {
        QLog.l().logger().debug(
            "\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u043c \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u044b \u0438\u0437 \u0444\u0430\u0439\u043b\u0430 \"config"
                + File.separator + "welcome.property\"");
        final Properties settings = new Properties();
        final FileInputStream in;
        InputStreamReader inR = null;
        try {
            in = new FileInputStream("config" + File.separator + "welcome.properties");
            inR = new InputStreamReader(in, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new ClientException(
                "\u041f\u0440\u043e\u0431\u043b\u0435\u043c\u044b \u0441 \u043a\u043e\u0434\u0438\u0440\u043e\u0432\u043a\u043e\u0439 \u043f\u0440\u0438 \u0447\u0442\u0435\u043d\u0438\u0438. "
                    + ex);
        } catch (FileNotFoundException ex) {
            throw new ClientException(
                "\u041f\u0440\u043e\u0431\u043b\u0435\u043c\u044b \u0441 \u0444\u0430\u0439\u043b\u043e\u043c \u043f\u0440\u0438 \u0447\u0442\u0435\u043d\u0438\u0438. "
                    + ex);
        }
        try {
            settings.load(inR);
        } catch (IOException ex) {
            throw new ClientException(
                "\u041f\u0440\u043e\u0431\u043b\u0435\u043c\u044b \u0441 \u0447\u0442\u0435\u043d\u0438\u0435\u043c \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u043e\u0432. "
                    + ex);
        }
        paper_size_alarm =
            settings.getProperty(PAPER_SIZE_ALARM, "").trim().isEmpty() ? 700 : Integer
                .parseInt(settings
                    .getProperty(PAPER_SIZE_ALARM,
                        "700")); // - размер шрифта при вводе юзерской инфы
        paper_alarm_step =
            settings.getProperty(PAPER_ALARM_STEP, "").trim().isEmpty() ? 30 : Integer
                .parseInt(settings
                    .getProperty(PAPER_ALARM_STEP,
                        "30")); // - размер шрифта при вводе юзерской инфы

        point = settings.containsKey(POINT) ? Integer.parseInt(settings.getProperty(POINT))
            : 1; // указание для какого пункта регистрации услуга, 0-для всех, х-для киоска х.
        paperWidht = Integer
            .parseInt(settings.getProperty(PAPER_WIDHT, "250")); // ширина талона в пикселах
        leftMargin = Integer.parseInt(settings.getProperty(LEFT_MARGIN)); // отступ слева
        topMargin = Integer.parseInt(settings.getProperty(TOP_MARGIN)); //  отступ сверху
        lineHeigth = Integer.parseInt(settings.getProperty(LINE_HEIGTH)); // Ширина строки
        lineLenght = Integer
            .parseInt(settings.getProperty(LINE_LENGTH)); // Длинна стоки на квитанции
        scaleVertical = Double
            .parseDouble(settings.getProperty(SCALE_VERTICAL)); // маштабирование по вертикале
        scaleHorizontal = Double
            .parseDouble(settings.getProperty(SCALE_HORIZONTAL)); // машcтабирование по вертикале
        logo = "1".equals(settings.getProperty(LOGO)) || "true"
            .equals(settings.getProperty(LOGO)); // присутствие логотипа на квитанции
        input_data_qrcode =
            "1".equals(settings.getProperty(INPUT_DATA_QR)) || "true".equals(settings
                .getProperty(
                    INPUT_DATA_QR)); // присутствие qr-штрихкода на квитанции если клиент ввел свои персональные данные
        barcode = "1".equals(settings.getProperty(BARCODE)) ? 1
            : ("2".equals(settings.getProperty(BARCODE)) ? 2
                : 0); // присутствие штрихкода на квитанции 1-128B/2-QR
        logoLeft = Integer
            .parseInt(settings.getProperty(LOGO_LEFT)); // Отступ печати логотипа слева
        logoTop = Integer.parseInt(settings.getProperty(LOGO_TOP)); // Отступ печати логотипа сверху
        delayPrint = Integer
            .parseInt(settings.getProperty(DELAY_PRINT)); // Задержка заставки при печати в мсек.
        delayBack = Integer
            .parseInt(settings.getProperty(DELAY_BACK)); // Задержка заставки при печати в мсек.
        logoImg = settings.getProperty(LOGO_IMG);
        backgroundImg = settings.containsKey(BACKGROUND_IMG) ? settings.getProperty(BACKGROUND_IMG)
            : "/ru/apertum/qsystem/client/forms/resources/fon_welcome.jpg";
        if (!new File(backgroundImg).exists()) {
            backgroundImg = "/ru/apertum/qsystem/client/forms/resources/fon_welcome.jpg";
        }
        ticketFontName = settings.getProperty(TXT_FONT_NAME, "");
        String tfs = settings.getProperty(TXT_FONT_SIZE, "0");
        ticketFontSize = Integer.parseInt(tfs.isEmpty() ? "0" : tfs);
        tfs = settings.getProperty(TXT_FONT_H1_SIZE, "80");
        ticketFontH1Size = Integer.parseInt(tfs.isEmpty() ? "80" : tfs);
        tfs = settings.getProperty(TXT_FONT_H2_SIZE, "16");
        ticketFontH2Size = Integer.parseInt(tfs.isEmpty() ? "16" : tfs);
        promoText = settings.getProperty(PROMO_TXT, "");
        bottomText = settings.getProperty(BOTTOM_TXT, "");
        bottomGap =
            (settings.containsKey(BOTTOM_GAP) && !settings.getProperty(BOTTOM_GAP, "").isEmpty())
                ? Integer.parseInt(settings.getProperty(BOTTOM_GAP, "2")) : 2;
        if (bottomGap > 10) {
            bottomGap = 10;
        }
        if (bottomGap < 0) {
            bottomGap = 0;
        }
        waitText = settings.getProperty(WAIT_TXT, "");
        askLimit = Integer.parseInt(settings.getProperty(ASK_LIMIT,
            "0")); // Критический размер очереди после которого спрашивать клиентов о готовности встать в очередь
        pageLinesCount = settings.getProperty(PAGE_LINES_COUNT) == null ? 70
            : Integer
                .parseInt(settings.getProperty(PAGE_LINES_COUNT)); // Количество строк на странице
        linesButtonCount = settings.getProperty(LINES_BUTTON_COUNT) == null ? 5 : Integer.parseInt(
            settings.getProperty(
                LINES_BUTTON_COUNT)); // количество рядов кнопок на киоске, если будет привышение, то начнотся листание страниц
        oneColumnButtonCount = settings.containsKey(ONE_COLUMN_BUTTON_COUNT) ? Integer
            .parseInt(settings.getProperty(ONE_COLUMN_BUTTON_COUNT))
            : 3; // количество рядов кнопок на киоске, если будет привышение, то начнотся листание страниц
        twoColumnButtonCount = settings.containsKey(TWO_COLUMNS_BUTTON_COUNT) ? Integer
            .parseInt(settings.getProperty(TWO_COLUMNS_BUTTON_COUNT))
            : 10; // количество рядов кнопок на киоске, если будет привышение, то начнотся листание страниц
        buttons_COM = settings.getProperty("buttons_COM");
        buttons_databits = Integer.parseInt(settings.getProperty("buttons_databits"));
        buttons_speed = Integer.parseInt(settings.getProperty("buttons_speed"));
        buttons_parity = Integer.parseInt(settings.getProperty("buttons_parity"));
        buttons_stopbits = Integer.parseInt(settings.getProperty("buttons_stopbits"));
        infoURL = settings.getProperty(INFO_BUTTON, null);
        if (infoURL.length() < 4) {
            infoURL = null;
        }
        info =
            (infoURL != null && infoURL.length() > 3) || "1"
                .equals(settings.getProperty(INFO_BUTTON))
                || "true".equals(settings
                .getProperty(INFO_BUTTON)); // кнопка информационной системы на пункте регистрации
        responseURL = settings.getProperty(RESPONSE_BUTTON, null);
        if (responseURL.length() < 4) {
            responseURL = null;
        }
        response = (responseURL != null && responseURL.length() > 3) || "1"
            .equals(settings.getProperty(RESPONSE_BUTTON)) || "true".equals(
            settings.getProperty(RESPONSE_BUTTON)); // - кнопка обратной связи на пункте регистрации
        infoHtml = settings.getProperty(INFO_BUTTON_HTMLTEXT, "");
        responseHtml = settings.getProperty(RESPONSE_BUTTON_HTMLTEXT, "");
        advance = "1".equals(settings.getProperty(ADVANCE_BUTTON)) || "true".equals(settings
            .getProperty(ADVANCE_BUTTON)); // - кнопка предварительной записи на пункте регистрации
        standAdvance =
            "1".equals(settings.getProperty(STAND_ADVANCE_BUTTON)) || "true".equals(settings
                .getProperty(
                    STAND_ADVANCE_BUTTON)); // присутствие кнопки предварительной записи на пункте регистрации (0/1)

        numeric_keyboard = !settings.containsKey(NUMERIC_KEYBOARD) || "1"
            .equals(settings.getProperty(NUMERIC_KEYBOARD)) || "true".equals(
            settings
                .getProperty(NUMERIC_KEYBOARD)); // - цифровая клавиатура при вводе юзерской инфы
        alphabetic_keyboard = !settings.containsKey(ALPHABETIC_KEYBOARD) || "1"
            .equals(settings.getProperty(ALPHABETIC_KEYBOARD)) || "true".equals(
            settings
                .getProperty(ALPHABETIC_KEYBOARD));// - буквенная клавиатура при вводе юзерской инфы
        spec_keyboard =
            settings.containsKey(SPEC_KEYBOARD) ? settings.getProperty(SPEC_KEYBOARD).trim()
                : "";// - специальная при вводе юзерской инфы
        input_font_size = settings.containsKey(INPUT_FONT_SIZE) ? Integer
            .parseInt(settings.getProperty(INPUT_FONT_SIZE))
            : 64; // - размер шрифта при вводе юзерской инфы
        if (settings.containsKey(BUTTON_TYPE)) {
            switch (settings.getProperty(BUTTON_TYPE)) {
                case "1":
                    buttonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn1.png";
                    break;
                case "2":
                    buttonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn2.png";
                    break;
                case "3":
                    buttonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn3.png";
                    break;
                case "4":
                    buttonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn4.png";
                    break;
                case "5":
                    buttonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn5.png";
                    break;
                case "6":
                    buttonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn6.png";
                    break;
                case "7":
                    buttonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn7.png";
                    break;
                case "8":
                    buttonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn8.png";
                    break;
                case "9":
                    buttonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn9.png";
                    break;
                case "10":
                    buttonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn10.png";
                    break;
                case "11":
                    buttonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn11.png";
                    break;
                case "12":
                    buttonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn12.png";
                    break;
                default: {
                    final File f = new File(settings.getProperty(BUTTON_TYPE));
                    if (f.exists()) {
                        buttonType = settings.getProperty(BUTTON_TYPE);
                    } else {
                        buttonType = "";
                    }
                }
            }
        } else {
            buttonType = "";
        }
        if (settings.containsKey(SERV_BUTTON_TYPE)) {
            switch (settings.getProperty(SERV_BUTTON_TYPE)) {
                case "1":
                    servButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn1.png";
                    break;
                case "2":
                    servButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn2.png";
                    break;
                case "3":
                    servButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn3.png";
                    break;
                case "4":
                    servButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn4.png";
                    break;
                case "5":
                    servButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn5.png";
                    break;
                case "6":
                    servButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn6.png";
                    break;
                case "7":
                    servButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn7.png";
                    break;
                case "8":
                    servButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn8.png";
                    break;
                case "9":
                    servButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn9.png";
                    break;
                case "10":
                    servButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn10.png";
                    break;
                case "11":
                    servButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn11.png";
                    break;
                case "12":
                    servButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn12.png";
                    break;
                default: {
                    final File f = new File(settings.getProperty(SERV_BUTTON_TYPE));
                    if (f.exists()) {
                        servButtonType = settings.getProperty(SERV_BUTTON_TYPE);
                    } else {
                        servButtonType = "";
                    }
                }
            }
        } else {
            servButtonType = "";
        }
        if (settings.containsKey(SERV_VERT_BUTTON_TYPE)) {
            switch (settings.getProperty(SERV_VERT_BUTTON_TYPE)) {
                case "1":
                    servVertButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn1.png";
                    break;
                case "2":
                    servVertButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn2.png";
                    break;
                case "3":
                    servVertButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn3.png";
                    break;
                case "4":
                    servVertButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn4.png";
                    break;
                case "5":
                    servVertButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn5.png";
                    break;
                case "6":
                    servVertButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn6.png";
                    break;
                case "7":
                    servVertButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn7.png";
                    break;
                case "8":
                    servVertButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn8.png";
                    break;
                case "9":
                    servVertButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn9.png";
                    break;
                case "10":
                    servVertButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn10.png";
                    break;
                case "11":
                    servVertButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn11.png";
                    break;
                case "12":
                    servVertButtonType = "/ru/apertum/qsystem/client/forms/resources/buttons/btn12.png";
                    break;
                default: {
                    final File f = new File(settings.getProperty(SERV_VERT_BUTTON_TYPE));
                    if (f.exists()) {
                        servVertButtonType = settings.getProperty(SERV_VERT_BUTTON_TYPE);
                    } else {
                        servVertButtonType = "";
                    }
                }
            }
        } else {
            servVertButtonType = "";
        }
        buttonGoBackImg = new File(settings.getProperty(BUTTON_GOBACK_IMG, ""));
        buttonGoBackImg = buttonGoBackImg.exists() ? buttonGoBackImg : null;
        buttonToStratImg = new File(settings.getProperty(BUTTON_TOSTART_IMG, ""));
        buttonToStratImg = buttonToStratImg.exists() ? buttonToStratImg : null;
        buttonImg = "1".equals(settings.getProperty(BUTTON_IMG, "1")) || "true".equals(
            settings
                .getProperty(BUTTON_IMG, "true")); // кнопка присутствие картинки на кнопках услуг
        responseImg = "1".equals(settings.getProperty(RESPONSE_IMG, "1")) || "true".equals(settings
            .getProperty(RESPONSE_IMG, "true")); // кнопка присутствие картинки на кнопках отзывов
        topImg = settings.getProperty(TOP_IMG, "");
        topURL = settings.getProperty(TOP_URL, "");
        topImgSecondary = settings.getProperty(TOP_IMG_SECONDARY, "");
        patternGetTicket = settings.getProperty(PATTERN_GET_TICKET,
            "<HTML><b><p align=center><span style='font-size:60.0pt;color:green'>dialogue_text.take_ticket<br></span><span style='font-size:60.0pt;color:blue'>dialogue_text.your_nom<br></span><span style='font-size:130.0pt;color:blue'>dialogue_text.number</span></p>");
        patternConfirmationStart = settings.getProperty(PATTERN_CONFIRMATION_START,
            "<HTML><b><p align=center><span style='font-size:60.0pt;color:green'>dialog.text_before</span><br><span style='font-size:100.0pt;color:red'>dialog.count</span><br><span style='font-size:60.0pt;color:red'>dialog.text_before_people[[endRus]]</span></p></b>");
        getTicketImg = settings
            .getProperty(GET_TICKET_IMG,
                "/ru/apertum/qsystem/client/forms/resources/getTicket.png");
        if ("".equals(getTicketImg) || !new File(getTicketImg).exists()) {
            getTicketImg =
                Uses.firstMonitor.getDefaultConfiguration().getBounds().height < 910 || QConfig
                    .cfg()
                    .isDebug() || QConfig.cfg().isDemo()
                    ? "/ru/apertum/qsystem/client/forms/resources/getTicketSmall.png"
                    : "/ru/apertum/qsystem/client/forms/resources/getTicket.png";
        }
        confirmationStartImg = settings.getProperty(CONFIRMATION_START_IMG,
            "/ru/apertum/qsystem/client/forms/resources/vopros.png");
        if ("".equals(confirmationStartImg) || !new File(confirmationStartImg).exists()) {
            confirmationStartImg = "/ru/apertum/qsystem/client/forms/resources/vopros.png";
        }
        patternInfoDialog = settings.getProperty(PATTERN_INFO_DIALOG,
            "<HTML><p align=center><b><span style='font-size:60.0pt;color:red'>dialog.message</span></b></p>");
        patternPickAdvanceTitle = settings.getProperty(PATTERN_PICK_ADVANCE_TITLE,
            "<html><p align=center><span style='font-size:55.0;color:#DC143C'>dialog_text.part1</span><br><span style='font-size:45.0;color:#DC143C'><i>dialog_text.part2</i>");

        topSize = Integer.parseInt(settings.getProperty(TOP_SIZE, "-1").isEmpty() ? "-1"
            : settings.getProperty(TOP_SIZE, "-1"));
        if ("1".equals(settings.getProperty(EXECUTIVE, "0"))) {
            printAttributeSet.add(MediaSizeName.EXECUTIVE);
        }
        if (!"".equals(settings.getProperty(PRNAME, ""))) {
            // Get array of all print services
            final PrintService[] services = PrinterJob.lookupPrintServices();
            // Retrieve specified print service from the array
            for (int index = 0; printService == null && index < services.length; index++) {
                if (services[index].getName().equalsIgnoreCase(settings.getProperty(PRNAME, ""))) {
                    printService = services[index];
                }
            }
        }
        if (!"".equals(settings.getProperty(PRINTABLE_AREA, ""))
            && settings.getProperty(PRINTABLE_AREA, "").split(",").length == 4) {
            final String[] ss = settings.getProperty(PRINTABLE_AREA, "").split(",");
            printAttributeSet.add(new MediaPrintableArea(
                Integer.parseInt(ss[0]), // отсуп слева
                Integer.parseInt(ss[1]), // отсуп сверху
                Integer.parseInt(ss[2]), // ширина
                Integer.parseInt(ss[3]), // высота
                MediaPrintableArea.MM));
        }
        if (!"".equals(settings.getProperty(FIND_MEDIA, ""))
            && settings.getProperty(FIND_MEDIA, "").split(",").length == 2) {
            final String[] ss = settings.getProperty(FIND_MEDIA, "").split(",");
            final MediaSizeName mediaSizeName = MediaSize
                .findMedia(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), MediaPrintableArea.MM);
            printAttributeSet.add(mediaSizeName);
        }
        switch (settings.getProperty(ORIENTATION_PRINT, "")) {
            case "1":
                printAttributeSet.add(OrientationRequested.LANDSCAPE);
                break;
            case "2":
                printAttributeSet.add(OrientationRequested.PORTRAIT);
                break;
            case "3":
                printAttributeSet.add(OrientationRequested.REVERSE_LANDSCAPE);
                break;
            case "4":
                printAttributeSet.add(OrientationRequested.REVERSE_PORTRAIT);
                break;
            default:
                ;
        }
        switch (settings.getProperty(MEDIA_SIZE_NAME, "")) {
            case "A0":
                printAttributeSet.add(MediaSizeName.ISO_A0);
                break;
            case "A1":
                printAttributeSet.add(MediaSizeName.ISO_A1);
                break;
            case "A2":
                printAttributeSet.add(MediaSizeName.ISO_A2);
                break;
            case "A3":
                printAttributeSet.add(MediaSizeName.ISO_A3);
                break;
            case "A4":
                printAttributeSet.add(MediaSizeName.ISO_A4);
                break;
            case "A5":
                printAttributeSet.add(MediaSizeName.ISO_A5);
                break;
            case "A6":
                printAttributeSet.add(MediaSizeName.ISO_A6);
                break;
            case "A7":
                printAttributeSet.add(MediaSizeName.ISO_A7);
                break;
            case "A8":
                printAttributeSet.add(MediaSizeName.ISO_A8);
                break;
            case "A9":
                printAttributeSet.add(MediaSizeName.ISO_A9);
                break;
            case "A10":
                printAttributeSet.add(MediaSizeName.ISO_A10);
                break;
            case "B0":
                printAttributeSet.add(MediaSizeName.ISO_B0);
                break;
            case "B1":
                printAttributeSet.add(MediaSizeName.ISO_B1);
                break;
            case "B2":
                printAttributeSet.add(MediaSizeName.ISO_B2);
                break;
            case "B3":
                printAttributeSet.add(MediaSizeName.ISO_B3);
                break;
            case "B4":
                printAttributeSet.add(MediaSizeName.ISO_B4);
                break;
            case "B5":
                printAttributeSet.add(MediaSizeName.ISO_B5);
                break;
            case "B6":
                printAttributeSet.add(MediaSizeName.ISO_B6);
                break;
            case "B7":
                printAttributeSet.add(MediaSizeName.ISO_B7);
                break;
            case "B8":
                printAttributeSet.add(MediaSizeName.ISO_B8);
                break;
            case "B9":
                printAttributeSet.add(MediaSizeName.ISO_B9);
                break;
            case "B10":
                printAttributeSet.add(MediaSizeName.ISO_B10);
                break;
            case "C0":
                printAttributeSet.add(MediaSizeName.ISO_C0);
                break;
            case "C1":
                printAttributeSet.add(MediaSizeName.ISO_C1);
                break;
            case "C2":
                printAttributeSet.add(MediaSizeName.ISO_C2);
                break;
            case "C3":
                printAttributeSet.add(MediaSizeName.ISO_C3);
                break;
            case "C4":
                printAttributeSet.add(MediaSizeName.ISO_C4);
                break;
            case "C5":
                printAttributeSet.add(MediaSizeName.ISO_C5);
                break;
            default:
                ;
        }
        String ptn = settings.getProperty(BTN_FONT, "");
        if (!ptn.isEmpty() && ptn.split("-").length == 3) {
            btnFont = Font.decode(ptn);
        }
        ptn = settings.getProperty(BTN_ADV_FONT, "");
        if (!ptn.isEmpty() && ptn.split("-").length == 3) {
            btnAdvFont = Font.decode(ptn);
        }
    }

    private static class WelcomeParamsHolder {

        private static final WelcomeParams INSTANCE = new WelcomeParams();
    }
}
