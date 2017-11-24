/*
 *  Copyright (C) 2013 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Тут локализация на разные языки текста услуги
 * @author Evgeniy Egorov
 *
 */
@Entity
@Table(name = "services_langs")
public class QServiceLang implements Serializable, IidGetter {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Expose
    @SerializedName("id")
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    public final void setId(Long id) {
        this.id = id;
    }
    /**
     * Заголовок окна при вводе на пункте регистрации клиентом каких-то
     * данных перед постановкой в очередь после выбора услуги.
     * Также печатается на талоне рядом с введенными данными.
     */
    @Column(name = "input_caption")
    @Expose
    @SerializedName("input_caption")
    private String input_caption = "";

    public String getInput_caption() {
        return input_caption;
    }

    public void setInput_caption(String input_caption) {
        this.input_caption = input_caption;
    }
    /**
     * html текст информационного сообщения перед постановкой в очередь
     * Если этот параметр пустой, то не требуется показывать информационную напоминалку на пункте регистрации
     */
    @Column(name = "pre_info_html")
    @Expose
    @SerializedName("pre_info_html")
    private String preInfoHtml = "";

    public String getPreInfoHtml() {
        return preInfoHtml;
    }

    public void setPreInfoHtml(String preInfoHtml) {
        this.preInfoHtml = preInfoHtml;
    }
    /**
     * текст для печати при необходимости перед постановкой в очередь
     */
    @Column(name = "pre_info_print_text")
    @Expose
    @SerializedName("pre_info_print_text")
    private String preInfoPrintText = "";

    public String getPreInfoPrintText() {
        return preInfoPrintText;
    }

    public void setPreInfoPrintText(String preInfoPrintText) {
        this.preInfoPrintText = preInfoPrintText;
    }
    /**
     * текст для печати при необходимости перед постановкой в очередь
     */
    @Column(name = "ticket_text")
    @Expose
    @SerializedName("ticket_text")
    private String ticketText = "";

    public String getTicketText() {
        return ticketText;
    }

    public void setTicketText(String ticketText) {
        this.ticketText = ticketText;
    }
    
    /**
     * текст для вывода на главное табло в шаблоны панели вызванного и третью колонку пользователя
     */
    @Column(name = "tablo_text")
    @Expose
    @SerializedName("tablo_text")
    private String tabloText = "";

    /**
     * текст для вывода на главное табло в шаблоны панели вызванного и третью колонку пользователя
     * @return строчеп из БД
     */
    public String getTabloText() {
        return tabloText;
    }

    public void setTabloText(String tabloText) {
        this.tabloText = tabloText;
    }

    public QServiceLang() {
        super();
    }

    @Override
    public String toString() {
        return getLang() + " " + getName();
    }
    /**
     * Описание услуги.
     */
    @Expose
    @SerializedName("description")
    @Column(name = "description")
    private String description;

    public final void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    /**
     * Идентификатор языка, вообще любой текст, хоть кaрело-финский заводи
     */
    @Expose
    @SerializedName("lang")
    @Column(name = "lang")
    private String lang;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
    /**
     * Наименование услуги.
     */
    @Expose
    @SerializedName("name")
    @Column(name = "name")
    private String name;

    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
    /**
     * Надпись на кнопке услуги.
     */
    @Expose
    @SerializedName("buttonText")
    @Column(name = "button_text")
    private String buttonText;

    public String getButtonText() {
        return buttonText;
    }

    public final void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }
    @ManyToOne()
    @JoinColumn(name = "services_id", nullable = false, updatable = false)
    private QService service;

    public QService getService() {
        return service;
    }

    public void setService(QService service) {
        this.service = service;
    }
}
