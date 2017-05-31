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
package ru.apertum.qsystem.reports.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import org.apache.http.HttpRequest;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ReportException;
import ru.apertum.qsystem.reports.common.Response;
import ru.apertum.qsystem.reports.net.NetUtil;

/**
 * Базовый класс генераторов отчетов. сам себя складывает в HashMap [ String, IGenerator ] generators. Для получения отчета генератор использует методы
 * интерфейса IFormirovator. метод process генерирует отчет.
 *
 * @author Evgeniy Egorov
 */
@MappedSuperclass
public abstract class AGenerator implements IGenerator {

    /**
     * только для hibernate.
     */
    public AGenerator() {
        // javax.imageio.ImageIO.read(getClass().getResource("/ru/apertum/qsystem/reports/eximbank/resources/banner1.jpg"));
    }
    private String template;

    @Column(name = "template")
    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
    private String href;

    @Column(name = "href")
    @Override
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public AGenerator(String href, String resourceNameTemplate) {
        this.href = href;
        this.template = resourceNameTemplate;
    }

    /**
     * Абстрактный метод формирования данных отчета.
     *
     * @param request
     * @return
     */
    abstract protected JRDataSource getDataSource(HttpRequest request);

    /**
     * Абстрактный метод формирования параметров для отчета.
     *
     * @param request
     * @return
     */
    abstract protected Map getParameters(HttpRequest request);

    /**
     * Метод получения коннекта к базе если отчет строится через коннект.
     *
     * @param request
     * @return коннект соединения к базе или null.
     */
    abstract protected Connection getConnection(HttpRequest request);

    /**
     * Абстрактный метод выполнения неких действия для подготовки данных отчета. Если он возвращает заполненный массив байт, то его нужно отдать клиенту, иначе
     * если null то продолжаем генерировать отчет.
     *
     * @param request
     * @return массив байт для выдачи на клиента. Может быть null если выдовать ничего не надо.
     */
    abstract protected Response preparationReport(HttpRequest request);

    /**
     * Сформируем диалог дл ввода параметров
     *
     * @param request
     * @param errorMessage сообщение об ощибке предыдущего ввода, иначе null
     * @return
     */
    abstract protected Response getDialog(HttpRequest request, String errorMessage);

    /**
     * Проверка параметров если они были введены
     *
     * @param request
     * @param params параметры из request
     * @return сообщение об ошибке если была, иначе null
     */
    abstract protected String validate(HttpRequest request, HashMap<String, String> params);

    /**
     * Метод получения документа-отчета или другого какого документа в виде массива байт. Сдесь испольщуем методы интерфейса IFormirovator для получения отчета.
     *
     * @param request ? какого формата отчет хотим получить(html, pdf, rtf)
     * @return данные документа.
     */
    @Override
    public Response process(HttpRequest request) {
        QLog.l().logRep().debug("Генерируем : \"" + href + "\"");

        /*
         * Перед формированием отчета возможно необходимо получить некие параметры.
         * Для этого надо выдать клиенту форму для заполнения и принять от него введенные данные.
         * А по этим данным уже формировать отчетные данные. 
         */
        /*
         * Логика следующая:
         *
         * если параметров нет, то вызвать метод возвращающий страницу запроса параметров
         * и если этот метод вернет null, то параметры не требуются. Если этот метод вернет массив
         * байт, то отдать его пользователю для ввода параметров.
         *
         * Если по запросу страници параметров получили null, то нужно отвалидировать параметры
         * валидация null если все нормально и по getParameters(...) можно получать параметры.
         * если косяк, то нужно каким-то образом перевыдать форму ввода параметров с сообщением
         * что накосячили в прошлый раз.
         */
        //сначала диалог для параметров
        final HashMap<String, String> params = NetUtil.getParameters(request);
        if (params.isEmpty()) {
            final Response dialog = getDialog(request, null);
            if (dialog != null) {
                return dialog;
            }
        } else {
            String err = validate(request, params);
            if (err != null) {
                final Response dialog = getDialog(request, err);
                if (dialog != null) {
                    return dialog;
                }
            }
        }

        // ну и если что можно еще что нибудь замастырить
        final Response before = preparationReport(request);
        if (before != null) {
            return before;
        }

        // Компиляция отчета, попробуем без компиляции, есть же уже откампиленные
        //InputStream is = getClass().getResourceAsStream(template);
        //JasperReport jasperReport = JasperCompileManager.compileReport(is);
        // Получение готового к экспорту отчета
        //JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, xmlDataSource); - это вариант с предкампиляцией
        // это тип полученных данных, пойдет в http заголовок
        String dataType = null;
        try {
            // Шаблон может быть в виде файла, иначе в виде ресурса
            final File f_temp = new File(template);
            final InputStream inStr;
            if (f_temp.exists()) {
                inStr = new FileInputStream(f_temp);
            } else {
                inStr = getClass().getResourceAsStream(template);
            }
            if (inStr == null) {
                throw new ReportException("Шаблон не найден. \"" + template + "\" Либо отсутствует требуемый файл, либо некорректная запись в базе данных.");
            }
            // теперь посмотрим, не сформировали ли коннект 
            //если есть коннект, то строим отчет по коннекту, иначе формируем данные формироватором.
            final Connection conn = getConnection(request);
            final Map paramsForFilling = getParameters(request);
            paramsForFilling.put(JRParameter.REPORT_LOCALE, Locales.getInstance().getLangCurrent());
            final JasperPrint jasperPrint;
            if (conn == null) {
                jasperPrint = JasperFillManager.fillReport(inStr, paramsForFilling, getDataSource(request));//это используя уже откампиленный
            } else {
                jasperPrint = JasperFillManager.fillReport(inStr, paramsForFilling, conn);//это используя уже откампиленный
            }
            byte[] result = null;

            final String subject = NetUtil.getUrl(request);
            int dot = subject.lastIndexOf(".");
            final String format = subject.substring(dot + 1);

            final File ff = new File(Uses.TEMP_FOLDER);
            if (!ff.exists()) {
                ff.mkdir();
            }
            if (Uses.REPORT_FORMAT_HTML.equalsIgnoreCase(format)) {
                final JRHtmlExporter exporter = new JRHtmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

                // сгенерим отчет во временный файл
                // это нужно для того, что если совместно с html генеряться картинки, например графики,
                // то их сбросить на диск и потом с диска выдать по запросу броузера.
                // для этого нужно чтоб вебсервер умел выдовать и файла с диска и файлы из временных папок.
                final JRHtmlExporter exporterToTempFile = new JRHtmlExporter();
                exporterToTempFile.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporterToTempFile.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, Uses.TEMP_FOLDER + File.separator + "temphtml.html");
                exporterToTempFile.exportReport();

                final StringBuffer buf = new StringBuffer("UTF-8");
                exporter.setParameter(JRExporterParameter.OUTPUT_STRING_BUFFER, buf);
                exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
                exporter.exportReport();
                result = buf.toString().replaceAll("nullpx", "resources/px").replaceFirst("<body text=\"#000000\"", "<body text=\"#000000\"  background=\"resources/fp.png\" bgproperties=\"fixed\"").replaceAll("bgcolor=\"white\"", "bgcolor=\"CCDDEE\"").replaceAll("nullimg_", "img_").getBytes("UTF-8");
                dataType = "text/html";
            } else if (Uses.REPORT_FORMAT_RTF.equalsIgnoreCase(format)) {
                final JRRtfExporter exporter = new JRRtfExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
                exporter.exportReport();
                result = baos.toByteArray();
                dataType = "application/rtf";
            } else if (Uses.REPORT_FORMAT_XLSX.equalsIgnoreCase(format)) {
                //final JROdsExporter exporter = new JROdsExporter(); 
                final JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
                exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, true);
                exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, true);
                exporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, true);
                exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, true);
                exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, true);
                exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, true);
                exporter.exportReport();
                result = baos.toByteArray();
                //dataType = "application/ods";    
                dataType = "application/xlsx";
            } else if (Uses.REPORT_FORMAT_PDF.equalsIgnoreCase(format)) {
                // создадим файл со шрифтами если его нет
                final File f = new File("tahoma.ttf");
                if (!f.exists()) {
                    try (FileOutputStream fo = new FileOutputStream(f)) {
                        final InputStream inStream = getClass().getResourceAsStream("/ru/apertum/qsystem/reports/fonts/tahoma.ttf");
                        final byte[] b = Uses.readInputStream(inStream);
                        fo.write(b);
                        fo.flush();
                    }
                }
                result = genPDF(jasperPrint);
                dataType = "application/pdf";
            } else if (Uses.REPORT_FORMAT_CSV.equalsIgnoreCase(format)) {
                JRCsvExporter exporter = new JRCsvExporter();
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
                exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "cp1251");
                exporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, ";");
                exporter.exportReport();
                result = baos.toByteArray();
            }
            return new Response(result, dataType);
        } catch (FileNotFoundException ex) {
            throw new ReportException("Не найден файл шрифтов для генерации PDF. " + ex);
        } catch (IOException ex) {
            throw new ReportException("Ошибка декодирования при вводе/выводе. " + ex);
        } catch (JRException ex) {
            throw new ReportException("Ошибка генерации. " + ex);
        }

    }

    /**
     * Метод генерации PDF-отчетов через файл. Вынесен в отдельный метод для синхронизации.
     *
     * @param jasperPrint этот готовый отчет и экспортим в PDF
     * @return возвращает готовый отчет в виде массива байт
     * @throws net.sf.jasperreports.engine.JRException
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    synchronized private static byte[] genPDF(JasperPrint jasperPrint) throws JRException, FileNotFoundException, IOException {
        // сгенерим отчет во временный файл
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, Uses.TEMP_FOLDER + File.separator + "temppdf.pdf");
        exporter.exportReport();
        // отправим данные из файла и удалим его
        final File pdf = new File(Uses.TEMP_FOLDER + File.separator + "temppdf.pdf");
        final FileInputStream inStream = new FileInputStream(pdf);
        pdf.delete();
        return Uses.readInputStream(inStream);
    }
}
