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
package ru.apertum.qsystem.reports.formirovators;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import org.apache.http.HttpRequest;
import ru.apertum.qsystem.reports.common.Response;

/**
 * Формирует источник данных для отчета.
 * Здесь реализация всей  логики сбора данных и подготовки их для формирования документа.
 * @author Evgeniy Egorov
 */
public interface IFormirovator {

    /** 
     * Получение источника данных для отчета.
     * При построении отчета через коннект SQL-выражение находится в шаблоне отчета, по этому метод getDataSource() 
     * не вызывается.
     * @param driverClassName имя драйвера используемого для подключения к СУБД
     * @param password пароль с которым пользователь соединяется с базой
     * @param url использыемай база в СУБД
     * @param request данные пришедшие от браузера
     * @param username пользователь СУБД
     * @return Готовая структура для компилирования в документ.
     */
    public JRDataSource getDataSource(String driverClassName, String url, String username, String password, HttpRequest request);

    /*
     * Метод выполнения неких действия для подготовки данных отчета.
     * Если он возвращает заполненный массив байт, то его нужно отдать клиенту,
     * иначе если null то продолжаем генерировать отчет.
     * Метод выполняется в первую очередь перед поиском файла шаблона и вызовом методов
     * получения коннекта или данных.
     * Если HTTP-запрос inputData имеет параметр "Super: xxx", то этот запрос пришел от супер сайта и
     * необходимо сформировать свою часть консолидированного отчета и выдать.
     * @param driverClassName имя драйвера используемого для подключения к СУБД
     * @param request данные пришедшие от браузера
     * @param password пароль с которым пользователь соединяется с базой
     * @param url использыемай база в СУБД
     * @param username пользователь СУБД
     * @return массив байт для выдачи на клиента.
     * @deprecated будет разбит на методы получения диалога и валидации и будет изменен тип возвращаемого значения(preparationPerort)? ,будет удален
     
    public byte[] preparation(String driverClassName, String url, String username, String password, HttpRequest request);
*/
    /**
     * Метод выполнения неких действия для подготовки данных отчета.
     * Если он возвращает заполненный массив байт, то его нужно отдать клиенту,
     * иначе если null то продолжаем генерировать отчет.
     * @param driverClassName имя драйвера используемого для подключения к СУБД
     * @param request данные пришедшие от браузера
     * @param password пароль с которым пользователь соединяется с базой
     * @param url использыемай база в СУБД
     * @param username пользователь СУБД
     * @return данные для выдачи на клиента. или null если ничего выдавать не нужно
     */
    public Response preparationReport(String driverClassName, String url, String username, String password, HttpRequest request);

    /**
     * Получение страници диалога ввода параметров перед генерацией отчета.
     * Если диалог не нужет то вернуть null
     * @param driverClassName имя драйвера используемого для подключения к СУБД
     * @param request данные пришедшие от браузера
     * @param password пароль с которым пользователь соединяется с базой
     * @param url использыемай база в СУБД
     * @param username пользователь СУБД
     * @param errorMessage это сообщение об ошибке предыдущего ввода. Может быть null если вводится первый раз.
     * @return данные для выдачи на клиента или null.
     */
    public Response getDialog(String driverClassName, String url, String username, String password, HttpRequest request, String errorMessage);

    /**
     *
     * @param driverClassName имя драйвера используемого для подключения к СУБД
     * @param request данные пришедшие от браузера
     * @param password пароль с которым пользователь соединяется с базой
     * @param url использыемай база в СУБД
     * @param username пользователь СУБД
     * @param params параметры из HttpRequest request
     * @return null если все хорошо, сообщение обошибке если что-то не в порядке. Это сообщение будет передано
     * в повторный вызов метода getDialog()
     */
    public String validate(String driverClassName, String url, String username, String password, HttpRequest request, HashMap<String, String> params);

    /**
     * Метод формирования параметров для отчета.
     * В отчет нужно передать некие параметры. Они упаковываются в Мар.
     * Если параметры не нужны, то сформировать пустой Мар.
     * @param driverClassName имя драйвера используемого для подключения к СУБД
     * @param request данные пришедшие от браузера
     * @param password пароль с которым пользователь соединяется с базой
     * @param url использыемай база в СУБД
     * @param username пользователь СУБД
     * @return
     */
    public Map getParameters(String driverClassName, String url, String username, String password, HttpRequest request);

    /**
     * Метод получения коннекта к базе если отчет строится через коннект.
     * Если отчет строится не через коннект, а формироватором, то выдать null.
     * При построении отчета через коннект SQL-выражение находится в шаблоне отчета, по этому метод getDataSource() 
     * не вызывается.
     * @param driverClassName имя драйвера используемого для подключения к СУБД
     * @param request данные пришедшие от браузера
     * @param password пароль с которым пользователь соединяется с базой
     * @param url использыемай база в СУБД
     * @param username пользователь СУБД
     * @return коннект соединения к базе или null.
     */
    public Connection getConnection(String driverClassName, String url, String username, String password, HttpRequest request);
}
