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
package ru.apertum.qsystem.server.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 * Сетевые настройки системы. Класс работает как с XML, так и с hibernate.
 *
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "net")
public class QNet implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Порт сервера для приема команд.
     */
    @Column(name = "server_port")
    private Integer serverPort;
    /**
     * Порт сервера через который передается web содержимое отчетов.
     */
    @Column(name = "web_server_port")
    private Integer webServerPort;
    /**
     * UDP Порт клиента, на который идет рассылка широковещательных пакетов.
     */
    @Column(name = "client_port")
    private Integer clientPort;
    /**
     * Время начала приема заявок на постановку в очередь
     */
    @Column(name = "start_time")
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date startTime;
    /**
     * Время завершения приема заявок на постановку в очередь
     */
    @Column(name = "finish_time")
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date finishTime;
    /**
     * Версия БД или конфигурационного файла. Для определения совместимости и возможности вариантов
     * ардейта.
     */
    @Column(name = "version")
    private String version = "Не присвоена";
    /**
     * Ограничение по максимально возможному номеру.
     */
    @Column(name = "last_number")
    private Integer lastNumber;
    /**
     * Количество доп. приоритетов
     */
    @Column(name = "ext_priority")
    private Integer extPriorNumber;
    /**
     * Ограничение по минимально возможному номеру.
     */
    @Column(name = "first_number")
    private Integer firstNumber;
    /**
     * 0 - общая нумерация, 1 - для каждой услуги своя нумерация
     */
    @Column(name = "numering")
    private Boolean numering;
    /**
     * 0 - кабинет, 1 - окно, 2 - стойка
     */
    @Column(name = "point")
    private Integer point;
    /**
     * 0 - нет оповещения, 1 - только сигнал, 2 - сигнал + голос
     */
    @Column(name = "sound")
    private Integer sound;
    /**
     * 0 - по умолчанию, ну и т.д. по набору звуков
     */
    @Column(name = "voice")
    private Integer voice = 0;
    /**
     * Время нахождения в блеклисте в минутах. 0 - попавшие в блекслист не блокируются
     */
    @Column(name = "black_time")
    private Integer blackTime = 0;
    /**
     * Это ID филиала в котором установлена система. Нужно для идентификации в облачном сервисе
     */
    @Column(name = "branch_id")
    private Long branchOfficeId;
    /**
     * URL облачного сервиса, к которому будет коннектится плагин Зачем это в БД? Да чо-бы проще
     * было настраивать, а то придется как-то плагин отдельно админить. не все догадаются.
     */
    @Column(name = "sky_server_url")
    private String skyServerUrl;
    /**
     * адрес зонного сервера отображения хода очереди, к которому будет коннектится плагин Зачем это
     * в БД? Да чо-бы проще было настраивать, а то придется как-то плагин отдельно админить. не все
     * догадаются.
     */
    @Column(name = "zone_board_serv_addr")
    private String zoneBoardServAddr;
    @Transient
    private String[] zbsal = null;
    /**
     * Это порт зонального сервера отображения очереди на котором он будет принимать данные Нужно
     * для идентификации в облачном сервисе
     */
    @Column(name = "zone_board_serv_port")
    private Integer zoneBoardServPort;
    /**
     * Это количество повторных вызовов посетителя перед тем как при очередном повторном вызове
     * клиент будет удален
     */
    @Column(name = "limit_recall")
    private Integer limitRecall;
    /**
     * Свободное расположение кнопок на пункте регистрации
     */
    @Column(name = "button_free_design")
    private Boolean buttonFreeDesign;
    /////////////////////////////////////////////////////////
    // Numeration
    /////////////////////////////////////////////////////////

    /**
     * Для настроек нурациии. Сдесь будут имеццо настройки для ведения нумерирования клиентов и
     * формирования для них индикации на табло.
     */
    public QNet() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public Integer getWebServerPort() {
        return webServerPort;
    }

    public void setWebServerPort(Integer webServerPort) {
        this.webServerPort = webServerPort;
    }

    public Integer getClientPort() {
        return clientPort;
    }

    public void setClientPort(Integer clientPort) {
        this.clientPort = clientPort;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(Integer lastNumber) {
        this.lastNumber = lastNumber;
    }

    public Integer getExtPriorNumber() {
        return extPriorNumber;
    }

    public void setExtPriorNumber(Integer extPriorNumber) {
        this.extPriorNumber = extPriorNumber;
    }

    public Integer getFirstNumber() {
        return firstNumber;
    }

    public void setFirstNumber(Integer firstNumber) {
        this.firstNumber = firstNumber;
    }

    public Boolean getNumering() {
        return numering;
    }

    public void setNumering(Boolean numering) {
        this.numering = numering;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Integer getSound() {
        return sound;
    }

    public void setSound(Integer sound) {
        this.sound = sound;
    }

    public Integer getVoice() {
        return voice;
    }

    public void setVoice(Integer voice) {
        this.voice = voice;
    }

    public Integer getBlackTime() {
        return blackTime;
    }

    public void setBlackTime(Integer blackTime) {
        this.blackTime = blackTime;
    }

    public Long getBranchOfficeId() {
        return branchOfficeId;
    }

    public void setBranchOfficeId(Long branchOfficeId) {
        this.branchOfficeId = branchOfficeId;
    }

    public String getSkyServerUrl() {
        return skyServerUrl;
    }

    public void setSkyServerUrl(String skyServerUrl) {
        this.skyServerUrl = skyServerUrl;
    }

    public String getZoneBoardServAddr() {
        return zoneBoardServAddr;
    }

    public void setZoneBoardServAddr(String zoneBoardServAddr) {
        this.zoneBoardServAddr = zoneBoardServAddr;
    }

    public String[] getZoneBoardServAddrList() {
        if (zbsal == null || zbsal.length == 0) {
            String l = getZoneBoardServAddr();
            l = l.replaceAll("  ", " ");
            zbsal = l.split(", |; |,|;| ");
        }
        return zbsal;
    }

    public Integer getZoneBoardServPort() {
        return zoneBoardServPort;
    }

    public void setZoneBoardServPort(Integer zoneBoardServPort) {
        this.zoneBoardServPort = zoneBoardServPort;
    }

    public Integer getLimitRecall() {
        return limitRecall;
    }

    public void setLimitRecall(Integer limitRecall) {
        this.limitRecall = limitRecall;
    }

    public Boolean getButtonFreeDesign() {
        return buttonFreeDesign;
    }

    public void setButtonFreeDesign(Boolean buttonFreeDesign) {
        this.buttonFreeDesign = buttonFreeDesign;
    }

}
