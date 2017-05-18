/*
 * Copyright (C) 2010 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
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
package ru.apertum.qsystem.server.http;

/*
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import ru.apertum.qsystem.common.QLog;
 * 
 */

/**
 *
 * @author Evgeniy Egorov
 */
public class QWebSocketHandler {
    /*
    
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
    QLog.l().logger().trace("Обрашение на WebSocket: \"" + target + "\" по базе " + response.getCharacterEncoding());
    if (!"/qsystem/ws".equalsIgnoreCase(target))  {
    QLog.l().logger().trace("Отклоняем");
    return;
    }
    try {
    super.handle(target, baseRequest, request, response);
    } catch (IOException | ServletException ex) {
    QLog.l().logger().error("ERROR!!!", ex);
    }
    }
    /**
     * Набор открытых сокетов
     *
    private final Set<QWebSocket> webSockets = new CopyOnWriteArraySet<>();
    
    /**
     * Выполняется когда пытается открыться новое соединение
     * @param request
     * @param protocol протокол (бывает двух видов ws и wss)
     * @return 
     *
    @Override
    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
    QLog.l().logger().trace("Зпрос на соенинение по WebSocket: \"" + request.getPathInfo() + "\" по протоколу " + protocol);
    // У нас есть два варианта
    // Либо мы не пускаем клиента и вызываем исключение
    //    throw new Exception();
    // Либо возвращаем объект, который будет соединять сервер с клиентом
    //   и обрабатывать запросы от клиента 
    return new QWebSocket();
    }
    
    private class QWebSocket implements WebSocket.OnTextMessage {
    
    /**
     * Хранилище соединения
     *
    private Connection connection;
    
    @Override
    public void onMessage(String data) {
    QLog.l().logger().trace("Получено сообщение по WebSocket: \"" + data + "\"");
    try {
    // Цикл шарит по набору сокетов ChatWebSocketHandler::webSockets
    for (QWebSocket webSocket : webSockets) {
    
    // и каждому рассылает сообщение с флагом in для всех
    // кроме автора, автору - флаг out
    webSocket.connection.sendMessage((webSocket.equals(this) ? "out|" : ("in|" + "QSystem: " + "|")) + data);
    }
    } catch (IOException ex) {
    QLog.l().logger().error("ERROR!!!", ex);
    // Все ошибки будут приводить к разъединению клиента от сервера
    connection.close();
    }
    }
    
    @Override
    public void onOpen(Connection connection) {
    QLog.l().logger().trace("Открывается соединение по WebSocket. MaxIdleTime = " + connection.getMaxIdleTime() + "; MaxTextMessageSize=" + connection.getMaxTextMessageSize());
    // Сохраняем соединение в свойство ChatWebSocket::connection
    this.connection = connection;
    // Добавляем себя в глобальный набор ChatWebSocketHandler::webSockets
    webSockets.add(this);
    }
    
    @Override
    public void onClose(int closeCode, String message) {
    QLog.l().logger().trace("Закрывается соединение по WebSocket. code=" + closeCode + "; mess " + message);
    // Удаляем себя из глобального набора ChatWebSocketHandler::webSockets
    webSockets.remove(this);
    }
    }
     */
}
