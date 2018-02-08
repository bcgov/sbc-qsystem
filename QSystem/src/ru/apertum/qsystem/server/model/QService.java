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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.ICustomerChangePosition;
import ru.apertum.qsystem.server.ServerProps;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.model.calendar.QCalendar;
import ru.apertum.qsystem.server.model.schedule.QBreak;
import ru.apertum.qsystem.server.model.schedule.QBreaks;
import ru.apertum.qsystem.server.model.schedule.QSchedule;

/**
 * ÐœÐ¾Ð´ÐµÐ»ÑŒ Ð´Ð°Ð½Ð½Ñ‹Ñ… Ð´Ð»Ñ� Ñ„ÑƒÐ½ÐºÑ†Ð¸Ð¾Ð½Ð¸Ñ€Ð¾Ð²Ð°Ð½Ð¸Ñ� Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸ Ð²ÐºÐ»ÑŽÑ‡Ð°ÐµÑ‚ Ð² Ñ�ÐµÐ±Ñ�: - Ñ�Ñ‚Ñ€ÑƒÐºÑ‚ÑƒÑ€Ñƒ Ñ…Ñ€Ð°Ð½ÐµÐ½Ð¸Ñ� - Ð¼ÐµÑ‚Ð¾Ð´Ñ‹ Ð´Ð¾Ñ�Ñ‚ÑƒÐ¿Ð°
 * - Ð¼ÐµÑ‚Ð¾Ð´Ñ‹ Ð¼Ð°Ð½Ð¸Ð¿ÑƒÐ»Ð¸Ñ€Ð¾Ð²Ð°Ð½Ð¸Ñ� - Ð»Ð¾Ð³Ð¸Ñ€Ð¾Ð²Ð°Ð½Ð¸Ðµ Ð¸Ñ‚ÐµÑ€Ð°Ñ†Ð¸Ð¹ Ð“Ð»Ð°Ð²Ð½Ñ‹Ð¹ ÐºÐ»Ð°Ñ�Ñ� Ð¼Ð¾Ð´ÐµÐ»Ð¸ Ð´Ð°Ð½Ð½Ñ‹Ñ…. Ð¡Ð¾Ð´ÐµÑ€Ð¶Ð¸Ñ‚ Ð¾Ð±ÑŠÐµÐºÑ‚Ñ‹
 * Ð²Ñ�ÐµÑ… ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð² Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸ Ðº Ñ�Ñ‚Ð¾Ð¹ ÑƒÑ�Ð»ÑƒÐ³Ðµ. Ð˜Ð¼ÐµÐµÑ‚ Ð²Ñ�Ðµ Ð½ÐµÐ¾Ð±Ñ…Ð¾Ð´Ð¸Ð¼Ñ‹Ðµ Ð¼ÐµÑ‚Ð¾Ð´Ñ‹ Ð´Ð»Ñ� Ð¼Ð°Ð½Ð¸Ð¿ÑƒÐ»Ð¸Ñ€Ð¾Ð²Ð°Ð½Ð¸Ñ�
 * ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð°Ð¼Ð¸ Ð² Ð¿Ñ€ÐµÐ´ÐµÐ»Ð°Ñ… Ð¾Ð´Ð½Ð¾Ð¹ Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸
 *
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "services")
public class QService extends DefaultMutableTreeNode implements ITreeIdGetter, Transferable,
    Serializable {

    /**
     * data flavor used to get back a DnDNode from data transfer
     */
    public static final DataFlavor DND_NODE_FLAVOR = new DataFlavor(QService.class,
        "Drag and drop Node");
    /**
     * list of all flavors that this DnDNode can be transfered as
     */
    protected static DataFlavor[] flavors = {QService.DND_NODE_FLAVOR};
    /**
     * Ð¿Ð¾Ñ�Ð»ÐµÐ´Ð½Ð¸Ð¹ Ð½Ð¾Ð¼ÐµÑ€, Ð²Ñ‹Ð´Ð°Ð½Ð½Ñ‹Ð¹ Ð¿Ð¾Ñ�Ð»ÐµÐ´Ð½ÐµÐ¼Ñƒ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ñƒ Ð¿Ñ€Ð¸ Ð½Ð¾Ð¼ÐµÑ€Ð¸Ñ€Ð¾Ð²Ð°Ð½Ð¸Ð¸ ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð¾Ð² Ð¾Ð±Ñ‰ÐµÐ¼ Ñ€Ñ�Ð´Ð¾Ð¼ Ð´Ð»Ñ�
     * Ð²Ñ�ÐµÑ… ÑƒÑ�Ð»ÑƒÐ³. ÐžÐ³Ñ€Ð°Ð½Ð¸Ñ‡ÐµÐ½Ð¸Ðµ Ñ�Ð°Ð¼Ð¾Ð³Ð¾ Ð¼Ð¸Ð½Ð¸Ð¼Ð°Ð»ÑŒÐ½Ð¾ Ð²Ð¾Ð·Ð¼Ð¾Ð¶Ð½Ð¾Ð³Ð¾ Ð½Ð¾Ð¼ÐµÑ€Ð° ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð° Ð¿Ñ€Ð¸ Ñ�ÐºÐ²Ð¾Ð·Ð½Ð¾Ð¼
     * Ð½ÑƒÐ¼ÐµÑ€Ð¸Ñ€Ð¾Ð²Ð°Ð½Ð¸Ð¸ Ð¿Ñ€Ð¾Ð¸Ñ�Ñ…Ð¾Ð´Ð¸Ñ‚ Ð¿Ñ€Ð¸ Ð¾Ð¿Ñ€ÐµÐ´ÐµÐ»ÐµÐ½Ð¸Ð¸ Ð¿Ð°Ñ€Ð°Ð¼ÐµÑ‚Ñ€Ð¾Ð² Ð½ÑƒÐ¼ÐµÑ€Ð°Ñ†Ð¸Ð¸.
     */
    @Transient
    private static volatile int lastStNumber = Integer.MIN_VALUE;
    /**
     * Ð¼Ð½Ð¾Ð¶ÐµÑ�Ñ‚Ð²Ð¾ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð², Ð²Ñ�Ñ‚Ð°Ð²ÑˆÐ¸Ñ… Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ Ðº Ñ�Ñ‚Ð¾Ð¹ ÑƒÑ�Ð»ÑƒÐ³Ðµ A lot of custom-made people who are
     * waiting for this service
     */
    @Transient
    private final PriorityQueue<QCustomer> customers = new PriorityQueue<>();
    @Transient
    //@Expose
    //@SerializedName("clients")
    private final LinkedBlockingDeque<QCustomer> clients = new LinkedBlockingDeque<>(customers);
    @Transient
    @Expose
    @SerializedName("inner_services")
    private final LinkedList<QService> childrenOfService = new LinkedList<>();
    @Id
    @Column(name = "id")
    //@GeneratedValue(strategy = GenerationType.AUTO) Ð°Ð²Ñ‚Ð¾ Ð½ÐµÐ»ÑŒÐ·Ñ�, Ñ‚.Ðº. id Ð½ÑƒÐ¶Ð½Ñ‹ Ð´Ð»Ñ� Ñ„Ð¾Ñ€Ð¼Ð¸Ñ€Ð¾Ð²Ð°Ð½Ð¸Ñ� Ð´ÐµÑ€ÐµÐ²Ð°
    @Expose
    @SerializedName("id")
    private Long id = new Date().getTime();
    /**
     * Ð¿Ñ€Ð¸Ð·Ð½Ð°Ðº ÑƒÐ´Ð°Ð»ÐµÐ½Ð¸Ñ� Ñ� Ð¿Ñ€Ð¾Ñ�Ñ‚Ð°Ð²Ð»ÐµÐ½Ð¸Ð¼ Ð´Ð°Ñ‚Ñ‹
     */
    @Column(name = "deleted")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date deleted;
    /**
     * Ð¡Ð¾Ñ�Ñ‚Ð¾Ñ�Ð½Ð¸Ðµ ÑƒÑ�Ð»ÑƒÐ³Ð¸. 1 - Ð´Ð¾Ñ�Ñ‚ÑƒÐ¿Ð½Ð°, 0 - Ð½ÐµÐ´Ð¾Ñ�Ñ‚ÑƒÐ¿Ð½Ð°, -1 - Ð½ÐµÐ²Ð¸Ð´Ð¸Ð¼Ð°.
     */
    @Column(name = "status")
    @Expose
    @SerializedName("status")
    private Integer status;
    /**
     * ÐŸÑƒÐ½ÐºÑ‚Ð¾Ð² Ñ€ÐµÐ³Ð¸Ñ�Ñ‚Ñ€Ð°Ñ†Ð¸Ð¸ Ð¼Ð¾Ð¶ÐµÑ‚ Ð±Ñ‹Ñ‚ÑŒ Ð¼Ð½Ð¾Ð³Ð¾. Ð�Ð°Ð±Ð¾Ñ€Ñ‹ ÐºÐ½Ð¾Ð¿Ð¾Ðº Ð½Ð° Ñ€Ð°Ð·Ð½Ñ‹Ñ… ÐºÐ¸Ð¾Ñ�ÐºÐ°Ñ… Ð¼Ð¾Ð³ÑƒÑ‚ Ð±Ñ‹Ñ‚ÑŒ Ñ€Ð°Ð·Ð½Ñ‹Ðµ.
     * Ð£ÐºÐ°Ð·Ð°Ð½Ð¸Ðµ Ð´Ð»Ñ� ÐºÐ°ÐºÐ¾Ð³Ð¾ Ð¿ÑƒÐ½ÐºÑ‚Ð° Ñ€ÐµÐ³Ð¸Ñ�Ñ‚Ñ€Ð°Ñ†Ð¸Ð¸ ÑƒÑ�Ð»ÑƒÐ³Ð°, 0-Ð´Ð»Ñ� Ð²Ñ�ÐµÑ…, Ñ…-Ð´Ð»Ñ� ÐºÐ¸Ð¾Ñ�ÐºÐ° Ñ….
     */
    @Column(name = "point")
    @Expose
    @SerializedName("point")
    private Integer point = 0;
    /**
     * Ð�Ð¾Ñ€Ð¼Ð°Ñ‚Ð¸Ð². Ð¡Ñ€ÐµÐ´Ð½ÐµÐµ Ð²Ñ€ÐµÐ¼Ñ� Ð¾ÐºÐ°Ð·Ð°Ð½Ð¸Ñ� Ñ�Ñ‚Ð¾Ð¹ ÑƒÑ�Ð»ÑƒÐ³Ð¸. Ð—Ð°Ñ‡ÐµÐ¼ Ð½Ð°Ð´Ð¾? Ð�Ðµ Ð·Ð½Ð°ÑŽ. ÐŸÐ¾ÐºÐ° Ð´Ð»Ñ� Ð¼Ð°Ñ€ÑˆÑ€ÑƒÑ‚Ð¸Ð·Ð°Ñ†Ð¸Ð¸ Ð¿Ñ€Ð¸
     * Ð¼ÐµÐ´Ð¾Ñ�Ð¼Ð¾Ñ‚Ñ€Ðµ. ÐœÐ¾Ð¶ÐµÑ‚ Ð¿Ð¾Ñ‚Ð¾Ð¼ Ñ‚Ð¾Ð¶Ðµ Ð¿Ñ€Ð¸Ð¼ÐµÐ½ÐµÐ¼.
     */
    @Column(name = "duration")
    @Expose
    @SerializedName("duration")
    private Integer duration = 1;
    /**
     * Ð’Ñ€ÐµÐ¼Ñ� Ð¾Ð±Ñ�Ð·Ð°Ñ‚ÐµÐ»ÑŒÐ½Ð¾Ð³Ð¾ Ð¾Ð¶Ð¸Ð´Ð°Ð½Ð¸Ñ� Ð¿Ð¾Ñ�ÐµÑ‚Ð¸Ñ‚ÐµÐ»Ñ�.
     */
    @Column(name = "expectation")
    @Expose
    @SerializedName("exp")
    private Integer expectation = 0;
    /**
     * ÑˆÐ°Ð±Ð»Ð¾Ð½ Ð·Ð²ÑƒÐºÐ¾Ð²Ð¾Ð³Ð¾ Ð¿Ñ€Ð¸Ð³Ð»Ð°ÑˆÐµÐ½Ð¸Ñ�. null Ð¸Ð»Ð¸ 0... - Ð¸Ñ�Ð¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÑŒ Ñ€Ð¾Ð´Ð¸Ñ‚ÐµÐ»ÑŒÑ�ÐºÐ¸Ð¹. Ð”Ð°Ð»ÐµÐµ Ñ‡Ñ‚Ð¾ Ð¸Ð³Ñ€Ð°ÐµÐ¼ Ð°
     * Ñ‡Ñ‚Ð¾ Ð½ÐµÑ‚.
     */
    @Column(name = "sound_template")
    @Expose
    @SerializedName("sound_template")
    private String soundTemplate;
    @Column(name = "advance_limit")
    @Expose
    @SerializedName("advance_limit")
    private Integer advanceLimit = 1;
    @Column(name = "day_limit")
    @Expose
    @SerializedName("day_limit")
    private Integer dayLimit = 0;
    @Column(name = "person_day_limit")
    @Expose
    @SerializedName("person_day_limit")
    private Integer personDayLimit = 0;
    /**
     * Ð­Ñ‚Ð¾ Ð¾Ð³Ñ€Ð°Ð½Ð¸Ñ‡ÐµÐ½Ð¸Ðµ Ð² Ð´Ð½Ñ�Ñ…, Ð² Ð¿Ñ€ÐµÐ´ÐµÐ»Ð°Ñ… ÐºÐ¾Ñ‚Ð¾Ñ€Ð¾Ð³Ð¾ Ð¼Ð¾Ð¶Ð½Ð¾ Ð·Ð°Ð¿Ð¸Ñ�Ð°Ñ‚ÑŒÑ�Ñ� Ð²Ð¿ÐµÑ€ÐµÐ´ Ð¿Ñ€Ð¸ Ð¿Ñ€ÐµÐ´Ð²Ð°Ñ€Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾Ð¹
     * Ð·Ð°Ð¿Ð¸Ñ�Ð¸ Ð¼Ð¾Ð¶ÐµÑ‚ Ð±Ñ‹Ñ‚ÑŒ null Ð¸Ð»Ð¸ 0 ÐµÑ�Ð»Ð¸ Ð½ÐµÑ‚ Ð¾Ð³Ñ€Ð°Ð½Ð¸Ñ‡ÐµÐ½Ð¸Ñ�
     */
    @Column(name = "advance_limit_period")
    @Expose
    @SerializedName("advance_limit_period")
    private Integer advanceLimitPeriod = 0;
    /**
     * Ð”ÐµÐ»ÐµÐ½Ð¸Ðµ Ñ�ÐµÑ‚ÐºÐ¸ Ð¿Ñ€ÐµÐ´Ð²Ð°Ñ€Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾Ð¹ Ð·Ð°Ð¿Ð¸Ñ�Ð¸
     */
    @Column(name = "advance_time_period")
    @Expose
    @SerializedName("advance_time_period")
    private Integer advanceTimePeriod = 60;
    /**
     * Ð¡Ð¿Ð¾Ñ�Ð¾Ð± Ð²Ñ‹Ð·Ð¾Ð²Ð° ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð° ÑŽÐ·ÐµÑ€Ð¾Ð¼ 1 - Ñ�Ñ‚Ð°Ð½Ð´Ð°Ñ€Ñ‚Ð½Ð¾ 2 - backoffice, Ñ‚.Ðµ. Ð²Ñ‹Ð·Ð¾Ð² Ñ�Ð»ÐµÐ´ÑƒÑŽÑ‰ÐµÐ³Ð¾ Ð±ÐµÐ· Ñ‚Ð°Ð±Ð»Ð¾ Ð¸
     * Ð·Ð²ÑƒÐºÐ°, Ð·Ð°Ð¿ÐµÑ€ÑˆÐµÐ½Ð¸Ðµ Ñ‚Ð¾Ð»ÑŒÐºÐ¾ Ñ€ÐµÐ´Ð¸Ñ€ÐµÐºÑ‚Ð¾Ð¼
     */
    @Column(name = "enable")
    @Expose
    @SerializedName("enable")
    private Integer enable = 1;
    @Column(name = "seq_id")
    private Integer seqId = 0;
    /**
     * Ð¢Ñ€ÐµÐ±Ð¾Ð²Ð°Ñ‚ÑŒ Ð¸Ð»Ð¸ Ð½ÐµÑ‚ Ð¾Ñ‚ Ð¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»Ñ� Ð¿Ð¾Ñ�Ð»Ðµ Ð¾ÐºÐ¾Ð½Ñ‡Ð°Ð½Ð¸Ñ� Ñ€Ð°Ð±Ð¾Ñ‚Ñ‹ Ñ� ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð¾Ð¼ Ð¿Ð¾ Ñ�Ñ‚Ð¾Ð¹ ÑƒÑ�Ð»ÑƒÐ³Ðµ Ð¾Ð±Ð¾Ð·Ð½Ð°Ñ‡Ð¸Ñ‚ÑŒ
     * Ñ€ÐµÐ·ÑƒÐ»ÑŒÑ‚Ð°Ñ‚ Ñ�Ñ‚Ð¾Ð¹ Ñ€Ð°Ð±Ð¾Ñ‚Ñ‹ Ð²Ñ‹Ð±Ñ€Ð°Ð² Ð¿ÑƒÐ½ÐºÑ‚ Ð¸Ð· Ñ�Ð»Ð¾Ð²Ð°Ñ€Ñ� Ñ€ÐµÐ·ÑƒÐ»ÑŒÑ‚Ð°Ñ‚Ð¾Ð²
     */
    @Column(name = "result_required")
    @Expose
    @SerializedName("result_required")
    private Boolean result_required = false;
    /**
     * Ð¢Ñ€ÐµÐ±Ð¾Ð²Ð°Ñ‚ÑŒ Ð¸Ð»Ð¸ Ð½ÐµÑ‚ Ð½Ð° Ð¿ÑƒÐ½ÐºÑ‚Ðµ Ñ€ÐµÐ³Ð¸Ñ�Ñ‚Ñ€Ð°Ñ†Ð¸Ð¸ Ð²Ð²Ð¾Ð´Ð° Ð¾Ñ‚ ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð° ÐºÐ°ÐºÐ¸Ñ…-Ñ‚Ð¾ Ð´Ð°Ð½Ð½Ñ‹Ñ… Ð¿ÐµÑ€ÐµÐ´ Ð¿Ð¾Ñ�Ñ‚Ð°Ð½Ð¾Ð²ÐºÐ¾Ð¹ Ð²
     * Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ Ð¿Ð¾Ñ�Ð»Ðµ Ð²Ñ‹Ð±Ð¾Ñ€Ð° ÑƒÑ�Ð»ÑƒÐ³Ð¸.
     */
    @Column(name = "input_required")
    @Expose
    @SerializedName("input_required")
    private Boolean input_required = false;
    /**
     * Ð�Ð° Ð³Ð»Ð°Ð²Ð½Ð¾Ð¼ Ñ‚Ð°Ð±Ð»Ð¾ Ð²Ñ‹Ð·Ð¾Ð² Ð¿Ð¾ ÑƒÑ�Ð»ÑƒÐ³Ðµ Ð¿Ñ€Ð¸ Ð½Ð°Ð»Ð¸Ñ‡Ð¸Ð¸ Ñ‚Ñ€ÐµÑ‚ÑŒÐµÐ¹ ÐºÐ¾Ð»Ð¾Ð½ÐºÐµ Ð´ÐµÐ»Ð°Ñ‚ÑŒ Ñ‚Ð°Ðº, Ñ‡Ñ‚Ð¾ Ñ�Ñ‚Ñƒ Ñ‚Ñ€ÐµÑ‚ÑŒÑŽ
     * ÐºÐ¾Ð»Ð¾Ð½ÐºÑƒ Ð·Ð°Ð¿Ð¾Ð»Ð½Ñ�Ñ‚ÑŒ Ð½Ðµ Ñ�Ñ‚Ñ€Ð¾Ð¹ÐºÐ¾Ð¹ Ñƒ ÑŽÐ·ÐµÑ€Ð°, Ð° Ð²Ð²ÐµÐ´ÐµÐ½Ð½Ð¾Ð¹ Ð¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»ÐµÐ¼ Ñ�Ñ‚Ñ€Ð¾Ñ‡ÐºÐ¾Ð¹
     */
    @Column(name = "inputed_as_ext")
    @Expose
    @SerializedName("inputed_as_ext")
    private Boolean inputedAsExt = false;
    /**
     * Ð—Ð°Ð³Ð¾Ð»Ð¾Ð²Ð¾Ðº Ð¾ÐºÐ½Ð° Ð¿Ñ€Ð¸ Ð²Ð²Ð¾Ð´Ðµ Ð½Ð° Ð¿ÑƒÐ½ÐºÑ‚Ðµ Ñ€ÐµÐ³Ð¸Ñ�Ñ‚Ñ€Ð°Ñ†Ð¸Ð¸ ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð¾Ð¼ ÐºÐ°ÐºÐ¸Ñ…-Ñ‚Ð¾ Ð´Ð°Ð½Ð½Ñ‹Ñ… Ð¿ÐµÑ€ÐµÐ´ Ð¿Ð¾Ñ�Ñ‚Ð°Ð½Ð¾Ð²ÐºÐ¾Ð¹ Ð²
     * Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ Ð¿Ð¾Ñ�Ð»Ðµ Ð²Ñ‹Ð±Ð¾Ñ€Ð° ÑƒÑ�Ð»ÑƒÐ³Ð¸. Ð¢Ð°ÐºÐ¶Ðµ Ð¿ÐµÑ‡Ð°Ñ‚Ð°ÐµÑ‚Ñ�Ñ� Ð½Ð° Ñ‚Ð°Ð»Ð¾Ð½Ðµ Ñ€Ñ�Ð´Ð¾Ð¼ Ñ� Ð²Ð²ÐµÐ´ÐµÐ½Ð½Ñ‹Ð¼Ð¸ Ð´Ð°Ð½Ð½Ñ‹Ð¼Ð¸.
     */
    @Column(name = "input_caption")
    @Expose
    @SerializedName("input_caption")
    private String input_caption = "";
    /**
     * html Ñ‚ÐµÐºÑ�Ñ‚ Ð¸Ð½Ñ„Ð¾Ñ€Ð¼Ð°Ñ†Ð¸Ð¾Ð½Ð½Ð¾Ð³Ð¾ Ñ�Ð¾Ð¾Ð±Ñ‰ÐµÐ½Ð¸Ñ� Ð¿ÐµÑ€ÐµÐ´ Ð¿Ð¾Ñ�Ñ‚Ð°Ð½Ð¾Ð²ÐºÐ¾Ð¹ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ Ð•Ñ�Ð»Ð¸ Ñ�Ñ‚Ð¾Ñ‚ Ð¿Ð°Ñ€Ð°Ð¼ÐµÑ‚Ñ€ Ð¿ÑƒÑ�Ñ‚Ð¾Ð¹,
     * Ñ‚Ð¾ Ð½Ðµ Ñ‚Ñ€ÐµÐ±ÑƒÐµÑ‚Ñ�Ñ� Ð¿Ð¾ÐºÐ°Ð·Ñ‹Ð²Ð°Ñ‚ÑŒ Ð¸Ð½Ñ„Ð¾Ñ€Ð¼Ð°Ñ†Ð¸Ð¾Ð½Ð½ÑƒÑŽ Ð½Ð°Ð¿Ð¾Ð¼Ð¸Ð½Ð°Ð»ÐºÑƒ Ð½Ð° Ð¿ÑƒÐ½ÐºÑ‚Ðµ Ñ€ÐµÐ³Ð¸Ñ�Ñ‚Ñ€Ð°Ñ†Ð¸Ð¸
     */
    @Column(name = "pre_info_html")
    @Expose
    @SerializedName("pre_info_html")
    private String preInfoHtml = "";
    /**
     * Ñ‚ÐµÐºÑ�Ñ‚ Ð´Ð»Ñ� Ð¿ÐµÑ‡Ð°Ñ‚Ð¸ Ð¿Ñ€Ð¸ Ð½ÐµÐ¾Ð±Ñ…Ð¾Ð´Ð¸Ð¼Ð¾Ñ�Ñ‚Ð¸ Ð¿ÐµÑ€ÐµÐ´ Ð¿Ð¾Ñ�Ñ‚Ð°Ð½Ð¾Ð²ÐºÐ¾Ð¹ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ
     */
    @Column(name = "pre_info_print_text")
    @Expose
    @SerializedName("pre_info_print_text")
    private String preInfoPrintText = "";
    /**
     * Ñ‚ÐµÐºÑ�Ñ‚ Ð´Ð»Ñ� Ð¿ÐµÑ‡Ð°Ñ‚Ð¸ Ð¿Ñ€Ð¸ Ð½ÐµÐ¾Ð±Ñ…Ð¾Ð´Ð¸Ð¼Ð¾Ñ�Ñ‚Ð¸ Ð¿ÐµÑ€ÐµÐ´ Ð¿Ð¾Ñ�Ñ‚Ð°Ð½Ð¾Ð²ÐºÐ¾Ð¹ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ
     */
    @Column(name = "ticket_text")
    @Expose
    @SerializedName("ticket_text")
    private String ticketText = "";
    /**
     * Ñ‚ÐµÐºÑ�Ñ‚ Ð´Ð»Ñ� Ð²Ñ‹Ð²Ð¾Ð´Ð° Ð½Ð° Ð³Ð»Ð°Ð²Ð½Ð¾Ðµ Ñ‚Ð°Ð±Ð»Ð¾ Ð² ÑˆÐ°Ð±Ð»Ð¾Ð½Ñ‹ Ð¿Ð°Ð½ÐµÐ»Ð¸ Ð²Ñ‹Ð·Ð²Ð°Ð½Ð½Ð¾Ð³Ð¾ Ð¸ Ñ‚Ñ€ÐµÑ‚ÑŒÑŽ ÐºÐ¾Ð»Ð¾Ð½ÐºÑƒ Ð¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»Ñ�
     * Text to display on the main display in the panel templates called and the third column of the
     * user
     */
    @Column(name = "tablo_text")
    @Expose
    @SerializedName("tablo_text")
    private String tabloText = "";
    /**
     * Ð Ð°Ñ�Ð¿Ð¾Ð»Ð¾Ð¶ÐµÐ½Ð¸Ðµ ÐºÐ½Ð¾Ð¿ÐºÐ¸ Ð½Ð° Ð¿ÑƒÐ½ÐºÑ‚Ðµ Ñ€ÐµÐ³Ð¸Ñ�Ñ‚Ñ€Ð°Ñ†Ð¸Ð¸
     */
    @Column(name = "but_x")
    @Expose
    @SerializedName("but_x")
    private Integer butX = 100;
    @Column(name = "but_y")
    @Expose
    @SerializedName("but_y")
    private Integer butY = 100;
    @Column(name = "but_b")
    @Expose
    @SerializedName("but_b")
    private Integer butB = 100;
    @Column(name = "but_h")
    @Expose
    @SerializedName("but_h")
    private Integer butH = 100;
    /**
     * Ð¿Ð¾Ñ�Ð»ÐµÐ´Ð½Ð¸Ð¹ Ð½Ð¾Ð¼ÐµÑ€, Ð²Ñ‹Ð´Ð°Ð½Ð½Ñ‹Ð¹ Ð¿Ð¾Ñ�Ð»ÐµÐ´Ð½ÐµÐ¼Ñƒ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ñƒ Ð¿Ñ€Ð¸ Ð½Ð¾Ð¼ÐµÑ€Ð¸Ñ€Ð¾Ð²Ð°Ð½Ð¸Ð¸ ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð¾Ð² Ð¾Ð±Ð¾Ñ�Ð¾Ð±Ð»ÐµÐ½Ð¾ Ð²
     * ÑƒÑ�Ð»ÑƒÐ³Ðµ. Ñ‚ÑƒÑ‚ Ñ‚Ð°ÐºÐ¾Ð¹ Ð·Ð°Ð¼ÑƒÑ‚. ÐºÐ¾Ð³Ð´Ð° ÑƒÑ�Ð»ÑƒÐ³Ñƒ Ñ�Ð¾Ð·Ð´Ð°ÐµÑˆÑŒ Ð¸Ð· json Ð³Ð´Ðµ-Ñ‚Ð¾ Ð½Ð° ÐºÐ»Ð¸ÐµÐ½Ñ‚Ðµ, Ñ‚Ð¾ Ñ‚Ð°Ð¼ Ð¶Ðµ
     * Ñ�Ð¿Ñ€Ð¸Ð½Ð³-ÐºÐ¾Ð½Ñ‚ÐµÐºÑ�Ñ‚ Ð½Ðµ Ð¿Ð¾Ð´Ð½Ñ�Ñ‚ Ð´Ð° Ð¸ Ð½ÑƒÐ¶Ð½Ð¾ Ñ�Ñ‚Ð¾ Ñ‚Ð¾Ð»ÑŒÐºÐ¾ Ð² ÐºÐ°Ñ‡ÐµÑ�Ñ‚Ð²Ðµ Ð´Ð°Ð½Ð½Ñ‹Ñ….
     */
    @Transient
    private int lastNumber = Integer.MIN_VALUE;
    // Ñ‡Ñ‚Ð¾Ð± ÐºÐ°Ð¶Ð´Ñ‹Ð¹ Ñ€Ð°Ð· Ð² Ð±Ð´ Ð½Ðµ Ð»Ð°Ð·Ð¸Ñ‚ÑŒ Ð´Ð»Ñ� Ð¿Ñ€Ð¾Ð²ÐµÑ€ÐºÐ¸ Ñ�ÐºÐ¾Ð»ÑŒÐºÐ¾ Ð¿Ñ€ÐµÐ´Ð²Ð°Ñ€Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ñ‹Ñ… Ñ�ÐµÐ³Ð¾Ð´Ð½Ñ� Ð¿Ð¾ Ñ�Ñ‚Ð¾Ð¹ ÑƒÑ�Ð»ÑƒÐ³Ðµ
    @Transient
    private int day_y = -100; // Ð´Ð»Ñ� Ñ�Ð¼ÐµÐ½Ñ‹ Ð´Ð½Ñ� Ð¿Ñ€Ð¾Ð²ÐµÑ€ÐºÐ¸
    @Transient
    private int dayAdvs = -100; // Ð´Ð»Ñ� Ñ�Ð¼ÐµÐ½Ñ‹ Ð´Ð½Ñ� Ð¿Ñ€Ð¾Ð²ÐµÑ€ÐºÐ¸
    /**
     * Ð¡ÐºÐ¾Ð»ÑŒÐºÐ¾ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð² ÑƒÐ¶Ðµ Ð¿Ñ€Ð¾ÑˆÐ»Ð¾ ÑƒÑ�Ð»ÑƒÐ³Ñƒ Ñ�ÐµÐ³Ð¾Ð´Ð½Ñ�
     */
    @Transient
    @Expose
    @SerializedName("countPerDay")
    private int countPerDay = 0;
    /**
     * Ð¢ÐµÐºÑƒÑ‰Ð¸Ð¹ Ð´ÐµÐ½ÑŒ, Ð½ÑƒÐ¶ÐµÐ½ Ð´Ð»Ñ� ÑƒÑ‡ÐµÑ‚Ð° ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð° ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð² Ð¾Ð±Ñ€Ð°Ð±Ð¾Ñ‚Ð°Ð½Ð½Ñ‹Ñ… Ð² Ñ�Ñ‚Ð¾Ð¹ ÑƒÑ�Ð»ÑƒÐ³Ðµ Ð² Ñ‚ÐµÐºÑƒÑ‰Ð¸Ð¹
     * Ð´ÐµÐ½ÑŒ
     */
    @Transient
    @Expose
    @SerializedName("day")
    private int day = 0;
    /**
     * ÐžÐ¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ ÑƒÑ�Ð»ÑƒÐ³Ð¸.
     */
    @Expose
    @SerializedName("description")
    @Column(name = "description")
    private String description;
    /**
     * ÐŸÑ€ÐµÑ„Ð¸ÐºÑ� ÑƒÑ�Ð»ÑƒÐ³Ð¸.
     */
    @Expose
    @SerializedName("service_prefix")
    @Column(name = "service_prefix")
    private String prefix = "";
    /**
     * Ð�Ð°Ð¸Ð¼ÐµÐ½Ð¾Ð²Ð°Ð½Ð¸Ðµ ÑƒÑ�Ð»ÑƒÐ³Ð¸.
     */
    @Expose
    @SerializedName("name")
    @Column(name = "name")
    private String name;
    /**
     * Ð�Ð°Ð´Ð¿Ð¸Ñ�ÑŒ Ð½Ð° ÐºÐ½Ð¾Ð¿ÐºÐµ ÑƒÑ�Ð»ÑƒÐ³Ð¸.
     */
    @Expose
    @SerializedName("buttonText")
    @Column(name = "button_text")
    private String buttonText;
    /**
     * Ð“Ñ€ÑƒÐ¿Ð¿Ð¸Ñ€Ð¾Ð²ÐºÐ° ÑƒÑ�Ð»ÑƒÐ³.
     */
    @Expose
    @SerializedName("parentId")
    @Column(name = "prent_id")
    private Long parentId;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "link_service_id")
    private QService link;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id")
    private QSchedule schedule;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinColumn(name = "calendar_id")
    private QCalendar calendar;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "services_id")
    @Expose
    @SerializedName("langs")
    private Set<QServiceLang> langs = new HashSet<>();

    @ManyToMany(cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(
        name = "services_offices",
        joinColumns = {@JoinColumn(name = "service_id")},
        inverseJoinColumns = {@JoinColumn(name = "office_id")}
    )
    private Set<QOffice> offices = new HashSet<>();
    @Expose
    @SerializedName("smartboard")
    @Column(name = "smartboard_yn")
    private String smartboard;
    @Transient
    private HashMap<String, QServiceLang> qslangs = null;
    /**
     * Ð•Ñ�Ð»Ð¸ Ð½Ðµ NULL Ð¸ Ð½Ðµ Ð¿ÑƒÑ�Ñ‚Ð°Ñ�, Ñ‚Ð¾ Ñ�Ñ‚Ð° ÑƒÑ�Ð»ÑƒÐ³Ð° Ð½ÐµÐ´Ð¾Ñ�Ñ‚ÑƒÐ¿Ð½Ð° Ð¸ Ñ�ÐµÑ€Ð²ÐµÑ€ Ð¾Ð±Ð»Ð°Ð¼Ð°ÐµÑ‚ Ð¿Ð¾Ñ�Ñ‚Ð°Ð½Ð¾Ð²ÐºÑƒ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ
     * Ð²Ñ‹ÐºÐ¸Ð½ÑƒÐ² Ð¿Ñ€Ð¸Ñ‡Ð¸Ð½Ñƒ Ð¸Ð· Ñ�Ñ‚Ð¾Ð³Ð¾ Ð¿Ð¾Ð»Ñ� Ð½Ð° Ð¿ÑƒÐ½ÐºÑ‚ Ñ€ÐµÐ³Ð¸Ñ�Ñ‚Ñ€Ð°Ñ†Ð¸Ð¸
     */
    @Transient
    private String tempReasonUnavailable;
    /**
     * ÐŸÐ¾ Ñ�ÑƒÑ‚Ð¸ Ð³Ñ€ÑƒÐ¿Ð¿Ð° Ð¾Ð±ÑŠÐµÐ´Ð¸Ð½ÐµÐ½Ð¸Ñ� ÑƒÑ�Ð»ÑƒÐ³ Ð¸Ð»Ð¸ ÐºÐ¾ÐµÑ€Ð½ÑŒ Ð²Ñ�ÐµÐ³Ð¾ Ð´ÐµÑ€ÐµÐ²Ð°. Ð¢Ð¾ Ð²Ð¾ Ñ‡Ñ‚Ð¾ Ð²ÐºÐ»ÑŽÑ‡ÐµÐ½Ð° Ð´Ð°Ð½Ð½Ñ‹Ñ� ÑƒÑ�Ð»ÑƒÐ³Ð°.
     * In fact, a group of services or the core of the whole tree. What is included in this
     * service.
     */
    @Transient
    private QService parentService;

    public QService() {
        super();
    }

    public static void clearNextStNumber() {
        lastStNumber = ServerProps.getInstance().getProps().getFirstNumber() - 1;
    }

    private PriorityQueue<QCustomer> getCustomers() {
        return customers;
    }

    /**
     * Ð­Ñ‚Ð¾ Ð²Ñ�Ðµ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ñ‹ Ñ�Ñ‚Ð¾Ñ�Ñ‰Ð¸Ðµ Ðº Ñ�Ñ‚Ð¾Ð¹ ÑƒÑ�Ð»ÑƒÐ³Ðµ Ð² Ð²Ð¸Ð´Ðµ Ñ�Ð¿Ð¸Ñ�ÐºÐ° Ð¢Ð¾Ð»ÑŒÐºÐ¾ Ð´Ð»Ñ� Ð±Ð°ÐºÐ°Ð¿Ð° Ð½Ð° Ð´Ð¸Ñ�Ðº These are all
     * the custodians standing for this service in the form of a list Only for bakap to disk
     */
    public LinkedBlockingDeque<QCustomer> getClients() {
        return clients;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPopup(){
        return (getId()+ ", position=start_before");
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    /**
     * Ð¡Ð¾Ñ�Ñ‚Ð¾Ñ�Ð½Ð¸Ðµ ÑƒÑ�Ð»ÑƒÐ³Ð¸. Ð’Ð»Ð¸Ñ�ÐµÑ‚ Ð½Ð° Ñ�Ð¾Ñ�Ñ‚Ð¾Ñ�Ð½Ð¸Ðµ ÐºÐ½Ð¾Ð¿ÐºÐ¸ Ð½Ð° ÐºÐ¸Ð¾Ñ�ÐºÐµ, Ð¿Ñ€Ð¸ Ñ€ÐµÐ´Ð¸Ñ€ÐµÐºÑ‚Ðµ
     *
     * @return 1 - Ð´Ð¾Ñ�Ñ‚ÑƒÐ¿Ð½Ð°, 0 - Ð½ÐµÐ´Ð¾Ñ�Ñ‚ÑƒÐ¿Ð½Ð°, -1 - Ð½ÐµÐ²Ð¸Ð´Ð¸Ð¼Ð°, 2 - Ñ‚Ð¾Ð»ÑŒÐºÐ¾ Ð´Ð»Ñ� Ð¿Ñ€ÐµÐ´Ð²Ð°Ñ€Ð¸Ð»Ð¾Ð²ÐºÐ¸, 3 -
     * Ð·Ð°Ð³Ð»ÑƒÑˆÐºÐ°
     */
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * Ð’Ñ€ÐµÐ¼Ñ� Ð¾Ð±Ñ�Ð·Ð°Ñ‚ÐµÐ»ÑŒÐ½Ð¾Ð³Ð¾ Ð¾Ð¶Ð¸Ð´Ð°Ð½Ð¸Ñ� Ð¿Ð¾Ñ�ÐµÑ‚Ð¸Ñ‚ÐµÐ»Ñ�.
     *
     * @return Ð² Ð¼Ð¸Ð½ÑƒÑ‚Ð°Ñ…
     */
    public Integer getExpectation() {
        return expectation;
    }

    public void setExpectation(Integer expectation) {
        this.expectation = expectation;
    }

    public String getSoundTemplate() {
        return soundTemplate;
    }

    public void setSoundTemplate(String soundTemplate) {
        this.soundTemplate = soundTemplate;
    }

    public Integer getAdvanceLimit() {
        return advanceLimit;
    }

    public void setAdvanceLinit(Integer advanceLimit) {
        this.advanceLimit = advanceLimit;
    }

    public Integer getDayLimit() {
        return dayLimit;
    }

    public void setDayLimit(Integer dayLimit) {
        this.dayLimit = dayLimit;
    }

    public Integer getPersonDayLimit() {
        return personDayLimit;
    }

    public void setPersonDayLimit(Integer personDayLimit) {
        this.personDayLimit = personDayLimit;
    }

    public Integer getAdvanceLimitPeriod() {
        return advanceLimitPeriod;
    }

    public void setAdvanceLimitPeriod(Integer advanceLimitPeriod) {
        this.advanceLimitPeriod = advanceLimitPeriod;
    }

    public Integer getAdvanceTimePeriod() {
        return advanceTimePeriod;
    }

    public void setAdvanceTimePeriod(Integer advanceTimePeriod) {
        this.advanceTimePeriod = advanceTimePeriod;
    }

    /**
     * Ð¡Ð¿Ð¾Ñ�Ð¾Ð± Ð²Ñ‹Ð·Ð¾Ð²Ð° ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð° ÑŽÐ·ÐµÑ€Ð¾Ð¼ 1 - Ñ�Ñ‚Ð°Ð½Ð´Ð°Ñ€Ñ‚Ð½Ð¾ 2 - backoffice, Ñ‚.Ðµ. Ð²Ñ‹Ð·Ð¾Ð² Ñ�Ð»ÐµÐ´ÑƒÑŽÑ‰ÐµÐ³Ð¾ Ð±ÐµÐ· Ñ‚Ð°Ð±Ð»Ð¾ Ð¸
     * Ð·Ð²ÑƒÐºÐ°, Ð·Ð°Ð¿ÐµÑ€ÑˆÐµÐ½Ð¸Ðµ Ñ‚Ð¾Ð»ÑŒÐºÐ¾ Ñ€ÐµÐ´Ð¸Ñ€ÐµÐºÑ‚Ð¾Ð¼
     *
     * @return int index
     */
    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer getSeqId() {
        return seqId;
    }

    public void setSeqId(Integer seqId) {
        this.seqId = seqId;
    }

    public Boolean getResult_required() {
        return result_required;
    }

    public void setResult_required(Boolean result_required) {
        this.result_required = result_required;
    }

    public Boolean getInput_required() {
        return input_required;
    }

    public void setInput_required(Boolean input_required) {
        this.input_required = input_required;
    }

    /**
     * Ð Ð°Ð·Ñ€ÐµÑˆÐµÐ½Ð¸Ðµ Ð²Ñ‹Ð²Ð¾Ð´Ð¸Ñ‚ÑŒ Ð½Ð° Ñ‚Ð°Ð±Ð»Ð¾ Ð²Ð²ÐµÐ´ÐµÐ½Ñ‹Ðµ Ð¿Ð¾Ñ�ÐµÑ‚Ð¸Ñ‚ÐµÐ»ÐµÐ¼ Ð½Ð° ÐºÐ¸Ð¾Ñ�ÐºÐµ(Ð¸Ð»Ð¸ ÐµÑ‰Ðµ ÐºÐ°Ðº) Ð´Ð°Ð½Ð½Ñ‹Ðµ.
     */
    public Boolean getInputedAsExt() {
        return inputedAsExt;
    }

    public void setInputedAsExt(Boolean inputedAsExt) {
        this.inputedAsExt = inputedAsExt;
    }

    public String getInput_caption() {
        return input_caption;
    }

    public void setInput_caption(String input_caption) {
        this.input_caption = input_caption;
    }

    public String getPreInfoHtml() {
        return preInfoHtml;
    }

    public void setPreInfoHtml(String preInfoHtml) {
        this.preInfoHtml = preInfoHtml;
    }

    public String getPreInfoPrintText() {
        return preInfoPrintText;
    }

    public void setPreInfoPrintText(String preInfoPrintText) {
        this.preInfoPrintText = preInfoPrintText;
    }

    public String getTicketText() {
        return ticketText;
    }

    public void setTicketText(String ticketText) {
        this.ticketText = ticketText;
    }

    /**
     * Ñ‚ÐµÐºÑ�Ñ‚ Ð´Ð»Ñ� Ð²Ñ‹Ð²Ð¾Ð´Ð° Ð½Ð° Ð³Ð»Ð°Ð²Ð½Ð¾Ðµ Ñ‚Ð°Ð±Ð»Ð¾ Ð² ÑˆÐ°Ð±Ð»Ð¾Ð½Ñ‹ Ð¿Ð°Ð½ÐµÐ»Ð¸ Ð²Ñ‹Ð·Ð²Ð°Ð½Ð½Ð¾Ð³Ð¾ Ð¸ Ñ‚Ñ€ÐµÑ‚ÑŒÑŽ ÐºÐ¾Ð»Ð¾Ð½ÐºÑƒ Ð¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»Ñ�
     * Text to display on the main display in the panel templates called and the third column of the
     * user
     *
     * @return Ñ�Ñ‚Ñ€Ð¾Ñ‡ÐµÐ¿ Ð¸Ð· Ð‘Ð” :: String from DB
     */
    public String getTabloText() {
        return tabloText;
    }

    public void setTabloText(String tabloText) {
        this.tabloText = tabloText;
    }

    public Integer getButB() {
        return butB;
    }

    public void setButB(Integer butB) {
        this.butB = butB;
    }

    public Integer getButH() {
        return butH;
    }

    public void setButH(Integer butH) {
        this.butH = butH;
    }

    public Integer getButX() {
        return butX;
    }

    // ***************************************************************************************
    // ********************  ÐœÐ•Ð¢ÐžÐ”Ð« Ð£ÐŸÐ Ð�Ð’Ð›Ð•Ð�Ð˜Ð¯ Ð­Ð›Ð•ÐœÐ•Ð�Ð¢Ð�ÐœÐ˜ Ð˜ Ð¡Ð¢Ð Ð£ÐšÐ¢Ð£Ð Ð« ************************
    // ***************************************************************************************

    public void setButX(Integer butX) {
        this.butX = butX;
    }

    public Integer getButY() {
        return butY;
    }

    public void setButY(Integer butY) {
        this.butY = butY;
    }

    @Override
    public String toString() {
        return getName().trim().isEmpty() ? "<NO_NAME>" : getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof QService) {
            final QService o = (QService) obj;
            return (id == null ? o.getId() == null : id.equals(o.getId()))
                && (name == null ? o.getName() == null : name.equals(o.getName()));
        } else {
            return false;
        }
    }

    /**
     * ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð½Ð¾Ð¼ÐµÑ€ Ð´Ð»Ñ� Ñ�Ð´ÐµÐ»ÑƒÑŽÑ‰ÐµÐ³Ð¾ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð°. ÐŸÑ€Ð¾Ð¸Ð·Ð¾Ð¹Ð´ÐµÑ‚ Ð¸Ð½ÐºÑ€ÐµÐ¼ÐµÐ½Ñ‚ Ñ�Ñ‡ÐµÑ‚Ñ‡Ð¸ÐºÐ° Ð½Ð¾Ð¼ÐµÑ€Ð¾Ð².
     */
    public int getNextNumber() {
        synchronized (QService.class) {
            if (lastNumber == Integer.MIN_VALUE) {
                lastNumber = ServerProps.getInstance().getProps().getFirstNumber() - 1;
            }
            if (lastStNumber == Integer.MIN_VALUE) {
                lastStNumber = ServerProps.getInstance().getProps().getFirstNumber() - 1;
            }
            if (lastNumber >= ServerProps.getInstance().getProps().getLastNumber()) {
                clearNextNumber();
            }
            if (lastStNumber >= ServerProps.getInstance().getProps().getLastNumber()) {
                clearNextStNumber();
            }
            // ÑƒÑ‡Ñ‚ÐµÐ¼ Ð²Ð½Ð¾Ð²ÑŒ Ð¿Ð¾Ñ�Ñ‚Ð°Ð²Ð»ÐµÐ½Ð½Ð¾Ð³Ð¾. Ð¿Ñ€Ð¸Ð±Ð°Ð²Ð¸Ð¼ Ð¾Ð´Ð½Ð¾Ð³Ð¾ Ðº ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ñƒ Ñ�ÐµÐ³Ð¾Ð´Ð½Ñ� Ð¿Ñ€Ð¸ÑˆÐµÐ´ÑˆÐ¸Ñ… Ðº Ð´Ð°Ð½Ð½Ð¾Ð¹ ÑƒÑ�Ð»ÑƒÐ³Ðµ
            final int today = new GregorianCalendar().get(GregorianCalendar.DAY_OF_YEAR);
            if (today != day) {
                day = today;
                setCountPerDay(0);
            }
            countPerDay++;

            // 0 - Ð¾Ð±Ñ‰Ð°Ñ� Ð½ÑƒÐ¼ÐµÑ€Ð°Ñ†Ð¸Ñ�, 1 - Ð´Ð»Ñ� ÐºÐ°Ð¶Ð´Ð¾Ð¹ ÑƒÑ�Ð»ÑƒÐ³Ð¸ Ñ�Ð²Ð¾Ñ� Ð½ÑƒÐ¼ÐµÑ€Ð°Ñ†Ð¸Ñ�
            if (ServerProps.getInstance().getProps().getNumering()) {
                return ++lastNumber;
            } else {
                return ++lastStNumber;
            }
        }
    }

    /**
     * Ð£Ð·Ð½Ð°Ñ‚ÑŒ Ñ�ÐºÐ¾Ð»ÑŒÐºÐ¾ Ð¿Ñ€ÐµÐ´Ð²Ð°Ñ€Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾ Ð·Ð°Ð¿Ð¸Ñ�Ð°Ð½Ð½Ñ‹Ñ… Ð´Ð»Ñ� Ñ�Ñ‚Ð¾Ð¹ ÑƒÑ�Ð»ÑƒÐ³Ð¸ Ð½Ð° Ð´Ð°Ñ‚Ñƒ
     *
     * @param date Ð½Ð° Ñ�Ñ‚Ñƒ Ð´Ð°Ñ‚Ñƒ ÑƒÐ·Ð½Ð°ÐµÐ¼ ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð·Ð°Ð¿Ð¸Ñ�Ð°Ð½Ð½Ñ‹Ñ… Ð¿Ñ€ÐµÐ´Ð²Ð°Ñ€Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾
     * @param strictStart false - Ð¿Ñ€Ð¾Ñ�Ñ‚Ð¾ ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð·Ð°Ð¿Ð¸Ñ�Ð°Ð½Ð½Ñ‹Ñ… Ð½Ð° Ñ�Ñ‚Ð¾Ñ‚ Ð´ÐµÐ½ÑŒ, true - ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾
     * Ð·Ð°Ð¿Ð¸Ñ�Ð°Ð½Ð½Ñ‹Ñ… Ð½Ð° Ñ�Ñ‚Ð¾Ñ‚ Ð´ÐµÐ½ÑŒ Ð½Ð°Ñ‡Ð¸Ð½Ð°Ñ� Ñ� Ð²Ñ€ÐµÐ¼ÐµÐ½Ð¸ date
     * @return ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ð·Ð°Ð¿Ð¸Ñ�Ð°Ð½Ð½Ñ‹Ñ… Ð¿Ñ€ÐµÐ´Ð²Ð°Ñ€Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾
     */
    public int getAdvancedCount(Date date, boolean strictStart) {
        final GregorianCalendar forDay = new GregorianCalendar();
        forDay.setTime(date);

        final GregorianCalendar today = new GregorianCalendar();
        if (!strictStart && forDay.get(GregorianCalendar.DAY_OF_YEAR) == today
            .get(GregorianCalendar.DAY_OF_YEAR)
            && day_y != today.get(GregorianCalendar.DAY_OF_YEAR)) {
            day_y = today.get(GregorianCalendar.DAY_OF_YEAR);
            dayAdvs = -100;
        }
        if (!strictStart && forDay.get(GregorianCalendar.DAY_OF_YEAR) == today
            .get(GregorianCalendar.DAY_OF_YEAR) && dayAdvs >= 0) {
            return dayAdvs;
        }

        final DetachedCriteria dc = DetachedCriteria.forClass(QAdvanceCustomer.class);
        dc.setProjection(Projections.rowCount());
        if (!strictStart) {
            forDay.set(GregorianCalendar.HOUR_OF_DAY, 0);
            forDay.set(GregorianCalendar.MINUTE, 0);
        }
        final Date today_m = forDay.getTime();
        forDay.set(GregorianCalendar.HOUR_OF_DAY, 23);
        forDay.set(GregorianCalendar.MINUTE, 59);
        dc.add(Restrictions.between("advanceTime", today_m, forDay.getTime()));
        dc.add(Restrictions.eq("service", this));
        final Long cnt = (Long) (Spring.getInstance().getHt().findByCriteria(dc).get(0));
        final int i = cnt.intValue();

        forDay.setTime(date);
        if (!strictStart && forDay.get(GregorianCalendar.DAY_OF_YEAR) == today
            .get(GregorianCalendar.DAY_OF_YEAR)) {
            dayAdvs = i;
        }

        QLog.l().logger()
            .trace("ÐŸÐ¾Ñ�Ð¼Ð¾Ñ‚Ñ€ÐµÐ»Ð¸ Ñ�ÐºÐ¾Ð»ÑŒÐºÐ¾ Ð¿Ñ€ÐµÐ´Ð²Ð°Ñ€Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ñ‹Ñ… Ð·Ð°Ð¿Ð¸Ñ�Ð°Ð»Ð¾Ñ�ÑŒ Ð² " + getName() + ". Ð˜Ñ… " + i);
        return i;
    }

    /**
     * Ð˜Ñ�Ñ�Ñ�Ðº Ð»Ð¸Ð¼Ð¸Ñ‚ Ð½Ð° Ð¾Ð´Ð¸Ð½Ð°ÐºÐ¾Ð²Ñ‹Ðµ Ð²Ð²ÐµÐ´ÐµÐ½Ð½Ñ‹Ðµ Ð´Ð°Ð½Ð½Ñ‹Ðµ Ð² Ð´ÐµÐ½ÑŒ Ð¿Ð¾ ÑƒÑ�Ð»ÑƒÐ³Ðµ Ð¸Ð»Ð¸ Ð½ÐµÑ‚
     *
     * @return true - Ð¿Ñ€ÐµÐ²Ñ‹ÑˆÐµÐ½, Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ Ñ�Ñ‚Ð°Ð½Ð¾Ð²Ð¸Ñ‚ÑŒÑ�Ñ� Ð½ÐµÐ»ÑŒÐ·Ñ�; false - Ð¼Ð¾Ð¶Ð½Ð¾ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ Ð²Ñ�Ñ‚Ð°Ñ‚ÑŒ
     */
    public boolean isLimitPersonPerDayOver(String data) {
        final int today = new GregorianCalendar().get(GregorianCalendar.DAY_OF_YEAR);
        if (today != day) {
            day = today;
            setCountPerDay(0);
        }
        return getPersonDayLimit() != 0 && getPersonDayLimit() <= getCountPersonsPerDay(data);
    }

    private int getCountPersonsPerDay(String data) {
        int cnt = 0;
        cnt = customers.stream()
            .filter((customer) -> (data.equalsIgnoreCase(customer.getInput_data())))
            .map((_item) -> 1).reduce(cnt, Integer::sum);
        if (getPersonDayLimit() <= cnt) {
            return cnt;
        }
        QLog.l().logger()
            .trace("Ð—Ð°Ð³Ñ€ÑƒÐ·Ð¸Ð¼ ÑƒÐ¶Ðµ Ð¾Ð±Ñ€Ð°Ð±Ð¾Ñ‚Ð°Ð½Ð½Ñ‹Ñ… ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð² Ñ� Ñ‚Ð°ÐºÐ¸Ð¼Ð¸ Ð¶Ðµ Ð´Ð°Ð½Ð½Ñ‹Ð¼Ð¸ \"" + data + "\"");
        // Ð—Ð°Ð³Ñ€ÑƒÐ·Ð¸Ð¼ ÑƒÐ¶Ðµ Ð¾Ð±Ñ€Ð°Ð±Ð¾Ñ‚Ð°Ð½Ð½Ñ‹Ñ… ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð²
        final GregorianCalendar gc = new GregorianCalendar();
        gc.set(GregorianCalendar.HOUR, 0);
        gc.set(GregorianCalendar.MINUTE, 0);
        gc.set(GregorianCalendar.SECOND, 0);
        final Date start = gc.getTime();
        gc.add(GregorianCalendar.DAY_OF_YEAR, 1);
        final Date finish = gc.getTime();
        QLog.l().logger().trace("FROM QCustomer a WHERE "
            + " start_time >'" + Uses.FORMAT_FOR_REP.format(start) + "' "
            + " and start_time <= '" + Uses.FORMAT_FOR_REP.format(finish) + "' "
            + " and  input_data = '" + data + "' "
            + " and service_id = " + getId());
        final List<QCustomer> custs = Spring.getInstance().getHt().find("FROM QCustomer a WHERE "
            + " start_time >'" + Uses.FORMAT_FOR_REP.format(start) + "' "
            + " and start_time <= '" + Uses.FORMAT_FOR_REP.format(finish) + "' "
            + " and  input_data = '" + data + "' "
            + " and service_id = " + getId());
        QLog.l().logger().trace(
            "Ð—Ð°Ð³Ñ€ÑƒÐ·Ð¸Ð»Ð¸ ÑƒÐ¶Ðµ Ð¾Ð±Ñ€Ð°Ð±Ð¾Ñ‚Ð°Ð½Ð½Ñ‹Ñ… ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð² Ñ� Ñ‚Ð°ÐºÐ¸Ð¼Ð¸ Ð¶Ðµ Ð´Ð°Ð½Ð½Ñ‹Ð¼Ð¸ \"" + data + "\". Ð˜Ñ… " + (cnt
                + custs.size()));
        return cnt + custs.size();
    }

    /**
     * Ð˜Ñ�Ñ�Ñ�Ðº Ð»Ð¸Ð¼Ð¸Ñ‚ Ð½Ð° Ð²Ð¾Ð·Ð¼Ð¾Ð¶Ð½Ñ‹Ñ… Ð¾Ð±Ñ€Ð°Ð±Ð¾Ñ‚Ð°Ð½Ð½Ñ‹Ñ… Ð² Ð´ÐµÐ½ÑŒ Ð¿Ð¾ ÑƒÑ�Ð»ÑƒÐ³Ðµ Ð¸Ð»Ð¸ Ð½ÐµÑ‚
     *
     * @return true - Ð¿Ñ€ÐµÐ²Ñ‹ÑˆÐµÐ½, Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ Ñ�Ñ‚Ð°Ð½Ð¾Ð²Ð¸Ñ‚ÑŒÑ�Ñ� Ð½ÐµÐ»ÑŒÐ·Ñ�; false - Ð¼Ð¾Ð¶Ð½Ð¾ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ Ð²Ñ�Ñ‚Ð°Ñ‚ÑŒ
     */
    public boolean isLimitPerDayOver() {
        final Date now = new Date();
        int advCusts = getAdvancedCount(now,
            true); //Ñ�ÐºÐ¾Ð»ÑŒÐºÐ¾ Ð¿Ñ€ÐµÐ´Ð²Ð°Ñ€Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾Ð·Ð°Ð¿Ð¸Ñ�Ð°Ð½Ð½Ñ‹Ñ… ÑƒÐ¶Ðµ ÐµÑ�Ñ‚ÑŒ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸ Ð² Ð¾Ñ�Ñ‚Ð°Ð²ÑˆÐµÐµÑ�Ñ� Ð²Ñ€ÐµÐ¼Ñ�(true)
        final int today = new GregorianCalendar().get(GregorianCalendar.DAY_OF_YEAR);
        if (today != day) {
            day = today;
            setCountPerDay(0);
        }
        final long p = getPossibleTickets();
        final long c = getCountPerDay();
        final boolean f = getDayLimit() != 0 && (p <= c + advCusts);
        if (f) {
            QLog.l().logger().trace(
                "Customers overflow: DayLimit()=" + getDayLimit() + " && PossibleTickets=" + p
                    + " <= CountPerDay=" + c + " + advCusts=" + advCusts);
        }
        return f;// getDayLimit() != 0 && getPossibleTickets(now) <= getCountPerDay() + advCusts;
    }

    /**
     * ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ñ‚Ð°Ð»Ð¾Ð½Ð¾Ð², ÐºÐ¾Ñ‚Ð¾Ñ€Ñ‹Ðµ Ð²Ñ�Ðµ ÐµÑ‰Ðµ Ð¼Ð¾Ð¶Ð½Ð¾ Ð²Ñ‹Ð´Ð°Ñ‚ÑŒ ÑƒÑ‡Ð¸Ñ‚Ñ‹Ð²Ð°Ñ� Ð¾Ð³Ñ€Ð°Ð½Ð¸Ñ‡ÐµÐ½Ð¸Ðµ Ð½Ð° Ð²Ñ€ÐµÐ¼Ñ�
     * Ñ€Ð°Ð±Ð¾Ñ‚Ñ‹ Ñ� Ð¾Ð´Ð½Ð¸Ð¼ ÐºÐ»Ð¸ÐµÑ‚Ð¾Ð¼
     *
     * @return Ð¾Ñ�Ñ‚Ð°Ð²ÑˆÐµÐµÑ�Ñ� Ð²Ñ€ÐµÐ¼Ñ� Ñ€Ð°Ð±Ð¾Ñ‚Ñ‹ Ð¿Ð¾ ÑƒÑ�Ð»ÑƒÐ³Ðµ / Ð¾Ð³Ñ€Ð°Ð½Ð¸Ñ‡ÐµÐ½Ð¸Ðµ Ð½Ð° Ð²Ñ€ÐµÐ¼Ñ� Ñ€Ð°Ð±Ð¾Ñ‚Ñ‹ Ñ� Ð¾Ð´Ð½Ð¸Ð¼ ÐºÐ»Ð¸ÐµÑ‚Ð¾Ð¼
     */
    public long getPossibleTickets() {
        if (getDayLimit() != 0) {
            // Ð¿Ð¾Ð´Ñ�Ñ‡Ð¸Ñ‚Ð°ÐµÐ¼ Ð¾Ð³Ñ€Ð°Ð½Ð¸Ñ‡ÐµÐ½Ð¸Ðµ Ð½Ð° Ð²Ñ‹Ð´Ð°Ñ‡Ñƒ Ñ‚Ð°Ð»Ð¾Ð½Ð¾Ð²
            final GregorianCalendar gc = new GregorianCalendar();
            final Date now = new Date();
            gc.setTime(now);
            long dif = getSchedule().getWorkInterval(gc.getTime()).finish.getTime() - now.getTime();

            int ii = gc.get(GregorianCalendar.DAY_OF_WEEK) - 1;
            if (ii < 1) {
                ii = 7;
            }
            final QBreaks qb;
            switch (ii) {
                case 1:
                    qb = getSchedule().getBreaks_1();
                    break;
                case 2:
                    qb = getSchedule().getBreaks_2();
                    break;
                case 3:
                    qb = getSchedule().getBreaks_3();
                    break;
                case 4:
                    qb = getSchedule().getBreaks_4();
                    break;
                case 5:
                    qb = getSchedule().getBreaks_5();
                    break;
                case 6:
                    qb = getSchedule().getBreaks_6();
                    break;
                case 7:
                    qb = getSchedule().getBreaks_7();
                    break;
                default:
                    throw new AssertionError();
            }
            if (qb != null) {// Ð¼Ð¾Ð¶ÐµÑ‚ Ð²Ð¾Ð¾Ð±Ñ‰Ðµ Ð¿ÐµÑ€ÐµÑ€Ñ‹Ð²Ð¾Ð² Ð½ÐµÑ‚
                for (QBreak br : qb.getBreaks()) {
                    if (br.getTo_time().after(now)) {
                        if (br.getFrom_time().before(now)) {
                            dif = dif - (br.getTo_time().getTime() - now.getTime());
                        } else {
                            dif = dif - br.diff();
                        }
                    }
                }
            }
            QLog.l().logger().trace(
                "ÐžÑ�Ñ‚Ð°Ð»Ð¾Ñ�ÑŒ Ñ€Ð°Ð±Ð¾Ñ‡ÐµÐ³Ð¾ Ð²Ñ€ÐµÐ¼ÐµÐ½Ð¸ " + (dif / 1000 / 60) + " Ð¼Ð¸Ð½ÑƒÑ‚. Ð•Ñ�Ð»Ð¸ Ð½Ð° ÐºÐ°Ð¶Ð´Ð¾Ð³Ð¾ "
                    + getDayLimit() + " Ð¼Ð¸Ð½ÑƒÑ‚, Ñ‚Ð¾ Ð¾Ñ�Ñ‚Ð°ÐµÑ‚Ñ�Ñ� Ð¿Ñ€Ð¸Ð½Ñ�Ñ‚ÑŒ " + (dif / 1000 / 60
                    / getDayLimit())
                    + " Ð¿Ð¾Ñ�ÐµÑ‚Ð¸Ñ‚ÐµÐ»ÐµÐ¹.");
            return dif / 1000 / 60 / getDayLimit();
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public int getCountPerDay() {
        return countPerDay;
    }

    public void setCountPerDay(int countPerDay) {
        this.countPerDay = countPerDay;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void clearNextNumber() {
        lastNumber = ServerProps.getInstance().getProps().getFirstNumber() - 1;
    }

    public void addCustomerForRecoveryOnly(QCustomer customer) {
        if (customer.getPrefix() != null) {
            final int number = customer.getNumber();
            // Ñ‚ÑƒÑ‚ Ð±Ñ‹ Ð½Ðµ Ð½ÑƒÐ¶Ð½Ð¾ Ð¿Ñ€Ð¾Ð²ÐµÑ€Ñ�Ñ‚ÑŒ Ð¿Ð¾Ñ�Ð»ÐµÐ´Ð½Ð¸Ð¹ Ð²Ñ‹Ð´Ð°Ð½Ð½Ñ‹Ð¹ ÐµÑ�Ð»Ð¸ Ñ�Ñ‚Ð¾ Ð¿Ñ€Ð¾Ð¸Ñ�Ñ…Ð¾Ð´Ð¸Ñ‚ Ñ� Ñ€ÐµÐ´Ð¸Ñ€ÐµÐºÑ‚ÐµÐ½Ð½Ñ‹Ð¹Ð¼
            if (CustomerState.STATE_REDIRECT != customer.getState()
                && CustomerState.STATE_WAIT_AFTER_POSTPONED != customer.getState()
                && CustomerState.STATE_WAIT_COMPLEX_SERVICE != customer.getState()) {
                if (number > lastNumber) {
                    lastNumber = number;
                }
                if (number > lastStNumber) {
                    lastStNumber = number;
                }
            }
        }
        addCustomer(customer);
    }

    /**
     * Ð”Ð¾Ð±Ð°Ð²Ð¸Ñ‚ÑŒ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ Ð¿Ñ€Ð¸ Ñ�Ñ‚Ð¾Ð¼ Ð¿Ñ€Ð¾Ñ�Ñ‚Ð°Ð²Ð¸Ñ‚Ñ�Ñ� Ð½Ð°Ð·Ð²Ð°Ð½Ð¸Ðµ Ñ�ÐµÑ€Ð²Ð¸Ñ�Ð°, Ð² ÐºÐ¾Ñ‚Ð¾Ñ€Ñ‹Ð¹ Ð²Ñ�Ñ€Ð°Ð», Ð¸ ÐµÐ³Ð¾ Ð¾Ð¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ,
     * ÐµÑ�Ð»Ð¸ Ñƒ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð° Ð½ÐµÑ‚Ñƒ Ð¿Ñ€ÐµÑ„Ð¸ÐºÑ�Ð°, Ñ‚Ð¾ Ð¿Ñ€Ð¾Ñ�Ñ‚Ð°Ð²Ð¸Ñ‚Ñ�Ñ� Ð¸ Ð¿Ñ€ÐµÑ„Ð¸ÐºÑ�.
     *
     * @param customer Ñ�Ñ‚Ð¾ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€ ÐºÐ¾Ñ‚Ð¾Ñ€Ð¾Ð³Ð¾ Ð´Ð¾Ð±Ð°Ð²Ð»Ñ�ÐµÐ¼ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ Ðº ÑƒÑ�Ð»ÑƒÐ³Ðµ
     */
    public void addCustomer(QCustomer customer) {
        //QLog.l().logQUser().debug("==> addCustomer");
        if (customer.getPrefix() == null) {
            //QLog.l().logQUser().debug("Set Prefix");
            customer.setPrefix(getPrefix());
        }
        if (customer == null) {
            //QLog.l().logQUser().debug("customer is null");
        }
        //QLog.l().logQUser().debug(customer.getPriority());
        if (!getCustomers().add(customer)) {
            throw new ServerException("Ð�ÐµÐ²Ð¾Ð·Ð¼Ð¾Ð¶Ð½Ð¾ Ð´Ð¾Ð±Ð°Ð²Ð¸Ñ‚ÑŒ Ð½Ð¾Ð²Ð¾Ð³Ð¾ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð° Ð² Ñ…Ñ€Ð°Ð½Ð¸Ð»Ð¸Ñ‰Ðµ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð².");
        }

        // Ð¿Ð¾Ð´Ð´ÐµÑ€Ð¶ÐºÐ° Ñ€Ð°Ñ�ÑˆÐ¸Ñ€Ñ�ÐµÐ¼Ð¾Ñ�Ñ‚Ð¸ Ð¿Ð»Ð°Ð³Ð¸Ð½Ð°Ð¼Ð¸/ Ð¾Ð¿Ñ€ÐµÐ´ÐµÐ»Ð¸Ð¼ ÐºÑƒÐ´Ð° Ð²Ð»ÐµÐ· ÐºÐ»Ð¸ÐµÐ½Ñ‚
        QCustomer before = null;
        QCustomer after = null;
        for (Iterator<QCustomer> itr = getCustomers().iterator(); itr.hasNext(); ) {
            final QCustomer c = itr.next();
            if (!customer.getId().equals(c.getId())) {
                if (customer.compareTo(c) == 1) {
                    // c - Ð¿ÐµÑ€Ð²ÐµÐµ, Ð¾Ð¿Ñ€ÐµÐ´ÐµÐ»Ñ�ÐµÐ¼ before
                    if (before == null) {
                        before = c;
                    } else if (before.compareTo(c) == -1) {
                        before = c;
                    }
                } else if (customer.compareTo(c) != 0) {
                    // c - Ð¿Ð¾Ñ�Ð»Ðµ, Ð¾Ð¿Ñ€ÐµÐ´ÐµÐ»Ñ�ÐµÐ¼ after
                    if (after == null) {
                        after = c;
                    } else if (after.compareTo(c) == 1) {
                        after = c;
                    }
                }
            }
        }
        // Ð¿Ð¾Ð´Ð´ÐµÑ€Ð¶ÐºÐ° Ñ€Ð°Ñ�ÑˆÐ¸Ñ€Ñ�ÐµÐ¼Ð¾Ñ�Ñ‚Ð¸ Ð¿Ð»Ð°Ð³Ð¸Ð½Ð°Ð¼Ð¸
        for (final ICustomerChangePosition event : ServiceLoader.load(ICustomerChangePosition.class)) {
            QLog.l().logger().info("Ð’Ñ‹Ð·Ð¾Ð² SPI Ñ€Ð°Ñ�ÑˆÐ¸Ñ€ÐµÐ½Ð¸Ñ�. ÐžÐ¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ: " + event.getDescription());
            event.insert(customer, before, after);
        }

        clients.clear();
        clients.addAll(getCustomers());
    }

    /**
     * Ð’Ñ�ÐµÐ³Ð¾ Ñ…Ð¾Ñ€Ð¾ÑˆÐµÐ³Ð¾, Ð²Ñ�Ðµ Ñ�Ð²Ð¾Ð±Ð¾Ð´Ð½Ñ‹!
     */
    public void freeCustomers() {
        // Ð¿Ð¾Ð´Ð´ÐµÑ€Ð¶ÐºÐ° Ñ€Ð°Ñ�ÑˆÐ¸Ñ€Ñ�ÐµÐ¼Ð¾Ñ�Ñ‚Ð¸ Ð¿Ð»Ð°Ð³Ð¸Ð½Ð°Ð¼Ð¸
        for (final ICustomerChangePosition event : ServiceLoader
            .load(ICustomerChangePosition.class)) {
            QLog.l().logger().info("Ð’Ñ‹Ð·Ð¾Ð² SPI Ñ€Ð°Ñ�ÑˆÐ¸Ñ€ÐµÐ½Ð¸Ñ�. ÐžÐ¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ: " + event.getDescription());
            for (Iterator<QCustomer> itr = getCustomers().iterator(); itr.hasNext(); ) {
                event.remove(itr.next());
            }
        }
        getCustomers().clear();
        clients.clear();
        clients.addAll(getCustomers());
    }

    /**
     * ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ, Ð½Ð¾ Ð½Ðµ ÑƒÐ´Ð°Ð»Ñ�Ñ‚ÑŒ. NoSuchElementException Ð¿Ñ€Ð¸ Ð½ÐµÑƒÐ´Ð°Ñ‡Ðµ
     *
     * @return Ð¿ÐµÑ€Ð²Ð¾Ð³Ð¾ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð°
     */
    public QCustomer getCustomer() {
        return getCustomers().element();
    }

    /**
     * ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð¸ ÑƒÐ´Ð°Ð»Ð¸Ñ‚ÑŒ. NoSuchElementException Ð¿Ñ€Ð¸ Ð½ÐµÑƒÐ´Ð°Ñ‡Ðµ
     *
     * @return Ð¿ÐµÑ€Ð²Ð¾Ð³Ð¾ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð°
     */
    public QCustomer removeCustomer() {
        final QCustomer customer = getCustomers().remove();

        // Ð¿Ð¾Ð´Ð´ÐµÑ€Ð¶ÐºÐ° Ñ€Ð°Ñ�ÑˆÐ¸Ñ€Ñ�ÐµÐ¼Ð¾Ñ�Ñ‚Ð¸ Ð¿Ð»Ð°Ð³Ð¸Ð½Ð°Ð¼Ð¸
        for (final ICustomerChangePosition event : ServiceLoader
            .load(ICustomerChangePosition.class)) {
            QLog.l().logger().info("Ð’Ñ‹Ð·Ð¾Ð² SPI Ñ€Ð°Ñ�ÑˆÐ¸Ñ€ÐµÐ½Ð¸Ñ�. ÐžÐ¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ: " + event.getDescription());
            event.remove(customer);
        }

        clients.clear();
        clients.addAll(getCustomers());
        return customer;
    }

    /**
     * ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð½Ð¾ Ð½Ðµ ÑƒÐ´Ð°Ð»Ñ�Ñ‚ÑŒ. null Ð¿Ñ€Ð¸ Ð½ÐµÑƒÐ´Ð°Ñ‡Ðµ
     *
     * @return Ð¿ÐµÑ€Ð²Ð¾Ð³Ð¾ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð°
     */
    public QCustomer peekCustomer() {
        return getCustomers().peek();
    }

    public QCustomer peekCustomerByOffice(QOffice office) {
        //QLog.l().logQUser().debug("peekCustomerByOffice: " + office);

        //  CM:  Get a list of all customers wanting this service.
        PriorityQueue<QCustomer> customers = getCustomers();
        QCustomer customer = null;

        //  CM:  Loop through all customers to see if they are in the office input.   
        for (Iterator<QCustomer> itr = customers.iterator(); itr.hasNext(); ) {
            final QCustomer cust = itr.next();
            //  QLog.l().logQUser().debug("Polling customer: " + cust);
            // QLog.l().logQUser().debug("  Office: " + cust.getOffice());
            // QLog.l().logQUser().debug(" Service: " + cust.getService().name);
            if (cust.getOffice().equals(office)) {
                customer = cust;
                break;
            }
        }

        return customer;
    }

    public PriorityQueue<QCustomer> peekAllCustomerByOffice(QOffice office) {

        //  Debug.
        // QLog.l().logQUser().debug("==> Start: peekAllCustomerByOffice: " + office);

        //  CM:  Init vars of all customers wanting this service, and those in input office.
        PriorityQueue<QCustomer> customers = getCustomers();
        PriorityQueue<QCustomer> custHere = new PriorityQueue<QCustomer>();
        QCustomer customer = null;

        //  CM:  Loop through all customers to see if they are in the office input.   
        for (Iterator<QCustomer> itr = customers.iterator(); itr.hasNext();) {
            final QCustomer cust = itr.next();
            //QLog.l().logQUser().debug("Polling customer: " + cust);
            //QLog.l().logQUser().debug("  Office: " + cust.getOffice());
            //QLog.l().logQUser().debug(" Service: " + cust.getService().name);
            if (cust.getOffice().equals(office)) {
                custHere.add(cust);
            }
        }

        //  Debug.
        // QLog.l().logQUser().debug("==> End: peekAllCustomerByOffice: " + office + "; Customers: " + custHere.size());
        if (custHere.size() != 0) {
            // QLog.l().logQUser().debug("==> End: peekAllCustomerByOffice: " + office + "; Customers: " + custHere.size());
        }

        return custHere;
    }

    /**
     * ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð¸ ÑƒÐ´Ð°Ð»Ð¸Ñ‚ÑŒ. Ð¼Ð¾Ð¶ÐµÑ‚ Ð²ÐµÑ€Ð½ÑƒÑ‚ÑŒ null Ð¿Ñ€Ð¸ Ð½ÐµÑƒÐ´Ð°Ñ‡Ðµ
     *
     * @return Ð¿ÐµÑ€Ð²Ð¾Ð³Ð¾ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð°
     */
    public QCustomer polCustomer() {
        final QCustomer customer = getCustomers().poll();
        if (customer != null) {
            // Ð¿Ð¾Ð´Ð´ÐµÑ€Ð¶ÐºÐ° Ñ€Ð°Ñ�ÑˆÐ¸Ñ€Ñ�ÐµÐ¼Ð¾Ñ�Ñ‚Ð¸ Ð¿Ð»Ð°Ð³Ð¸Ð½Ð°Ð¼Ð¸
            for (final ICustomerChangePosition event : ServiceLoader
                .load(ICustomerChangePosition.class)) {
                QLog.l().logger().info("Ð’Ñ‹Ð·Ð¾Ð² SPI Ñ€Ð°Ñ�ÑˆÐ¸Ñ€ÐµÐ½Ð¸Ñ�. ÐžÐ¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ: " + event.getDescription());
                event.remove(customer);
            }
        }

        clients.clear();
        clients.addAll(getCustomers());
        return customer;
    }

    public QCustomer polCustomerByOffice(QOffice office) {

        //  CM:  NOTE:  First part of this code is identical to peekCustomerByOffice code.
        //  CM:  peekCustomerByOffice finds the next customer.  This routine finds next
        //  CM:  customer and then removes them from the queue.
        QLog.l().logQUser().debug("polCustomerByOffice: " + office);
        PriorityQueue<QCustomer> customers = getCustomers();
        QCustomer customer = null;

        for (Iterator<QCustomer> itr = customers.iterator(); itr.hasNext(); ) {
            final QCustomer cust = itr.next();
            QLog.l().logQUser().debug("Polling customer: " + cust);
            QLog.l().logQUser().debug("  Office: " + cust.getOffice());
            if (cust.getOffice().equals(office)) {
                customer = cust;
                break;
            }
        }

        //  CM:  Code from here on is additional to the peekCustomerByOffice code.
        if (customer != null) {

            //  CM:  This gets executed, when customer is not null.
            QLog.l().logQUser().debug("Cust not null: " + customer.getName() + "; Comments: " + customer.getTempComments());
            int Count = 0;

            // Ð¿Ð¾Ð´Ð´ÐµÑ€Ð¶ÐºÐ° Ñ€Ð°Ñ�ÑˆÐ¸Ñ€Ñ�ÐµÐ¼Ð¾Ñ�Ñ‚Ð¸ Ð¿Ð»Ð°Ð³Ð¸Ð½Ð°Ð¼Ð¸
            //  CM:  However, this DOES NOT appear to remove any customers, as debug never gets called.
            for (final ICustomerChangePosition event : ServiceLoader
                .load(ICustomerChangePosition.class)) {
                QLog.l().logQUser().debug("Removing customer out of the queue");
                event.remove(customer);
                Count++;
            }

            //  CM:  This does get called, indicating there are no events in the ServiceLoader.load()
            if (Count == 0) {
                QLog.l().logQUser().debug("It appears customer not removed from event queue");
            }
        }

        //  CM:  This appears to have no effect.  Size of clients before/after call is identical.
        int BeforeClear = clients.size();
        clients.clear();
        int AfterClear = clients.size();
        clients.addAll(getCustomers());
        int AfterAdd = clients.size();
        QLog.l().logQUser().debug("Clients before clear: " + BeforeClear + "; after clear: " + AfterClear + "; after add: " + AfterAdd);

        return customer;
    }

    public QCustomer polCustomerSelected(QCustomer customer) {

        //  Debug
        // QLog.l().logQUser().debug("==> Start polCustSel");

        //  CM:  NOTE!!  This code identical to last part of polCustomerByOffice.
        //  CM:  Only difference is this routine doesn't search for a customer.
        //  CM:  It already knows the customer to be served.
        if (customer != null) {

            //  CM:  This gets executed, when customer is not null.
            // QLog.l().logQUser().debug("    --> Cust not null: " + customer.getName() + "; Comments: " + customer.getTempComments());
            int Count = 0;

            // Ð¿Ð¾Ð´Ð´ÐµÑ€Ð¶ÐºÐ° Ñ€Ð°Ñ�ÑˆÐ¸Ñ€Ñ�ÐµÐ¼Ð¾Ñ�Ñ‚Ð¸ Ð¿Ð»Ð°Ð³Ð¸Ð½Ð°Ð¼Ð¸
            //  CM:  However, this DOES NOT appear to remove any customers, as debug never gets called.
            for (final ICustomerChangePosition event : ServiceLoader.load(ICustomerChangePosition.class)) {
                // QLog.l().logQUser().debug("    --> Removing customer out of the queue");
                event.remove(customer);
                Count++;
            }

            //  CM:  This does get called, indicating there are no events in the ServiceLoader.load()
            if (Count == 0) {
                // QLog.l().logQUser().debug("    --> It appears customer not removed from event queue");
            }
        }

        //  CM:  This appears to have no effect.  Size of clients before/after call is identical.
        int BeforeClear = clients.size();
        clients.clear();
        int AfterClear = clients.size();
        clients.addAll(getCustomers());
        int AfterAdd = clients.size();
        // QLog.l().logQUser().debug("    --> Clients before clear: " + BeforeClear + "; after clear: " + AfterClear + "; after add: " + AfterAdd);

        // QLog.l().logQUser().debug("==> End polCustSel");

        return customer;
    }

    /**
     * Ð£Ð´Ð°Ð»Ð¸Ñ‚ÑŒ Ð»ÑŽÐ±Ð¾Ð³Ð¾ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð°. Remove any in the queue of the customizer.
     *
     * @param customer ÑƒÐ´Ð°Ð»Ñ�ÐµÐ¼Ñ‹Ð¹ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€ :: Removable custodian
     * @return Ð¼Ð¾Ð¶ÐµÑ‚ Ð²ÐµÑ€Ð½ÑƒÑ‚ÑŒ false Ð¿Ñ€Ð¸ Ð½ÐµÑƒÐ´Ð°Ñ‡Ðµ :: Can return false on failure
     */
    public boolean removeCustomer(QCustomer customer) {
        final Boolean res = getCustomers().remove(customer);
        if (customer != null && res) {
            // Ð¿Ð¾Ð´Ð´ÐµÑ€Ð¶ÐºÐ° Ñ€Ð°Ñ�ÑˆÐ¸Ñ€Ñ�ÐµÐ¼Ð¾Ñ�Ñ‚Ð¸ Ð¿Ð»Ð°Ð³Ð¸Ð½Ð°Ð¼Ð¸
            for (final ICustomerChangePosition event : ServiceLoader
                .load(ICustomerChangePosition.class)) {
                QLog.l().logger().info("Ð’Ñ‹Ð·Ð¾Ð² SPI Ñ€Ð°Ñ�ÑˆÐ¸Ñ€ÐµÐ½Ð¸Ñ�. ÐžÐ¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ: " + event.getDescription());
                event.remove(customer);
            }
        }
        clients.clear();
        clients.addAll(getCustomers());
        return res;
    }

    /**
     * ÐŸÐ¾Ð»ÑƒÑ‡ÐµÐ½Ð¸Ðµ ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð° ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð², Ñ�Ñ‚Ð¾Ñ�Ñ‰Ð¸Ñ… Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸.
     *
     * @return ÐºÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð² Ð² Ñ�Ñ‚Ð¾Ð¹ ÑƒÑ�Ð»ÑƒÐ³Ðµ
     */
    public int getCountCustomers() {
        return getCustomers().size();
    }

    public int getCountCustomersByOffice(QOffice office) {
        PriorityQueue<QCustomer> customers = getCustomers();
        int count = 0;

        for (Iterator<QCustomer> itr = customers.iterator(); itr.hasNext(); ) {
            final QCustomer c = itr.next();
            if (c.getOffice().equals(office)) {
                count += 1;
            }
        }

        return count;
    }

    public boolean changeCustomerPriorityByNumber(String number, int newPriority) {
        for (QCustomer customer : getCustomers()) {
            if (number.equalsIgnoreCase(customer.getPrefix() + customer.getNumber())) {
                customer.setPriority(newPriority);
                removeCustomer(customer); // ÑƒÐ±Ñ€Ð°Ñ‚ÑŒ Ð¸Ð· Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸
                addCustomer(customer);// Ð¿ÐµÑ€ÐµÐ¿Ð¾Ñ�Ñ‚Ð°Ð²Ð¸Ð»Ð¸ Ñ‡Ñ‚Ð¾Ð±Ñ‹ Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð½Ð¾Ñ�Ñ‚ÑŒ Ð¿ÐµÑ€ÐµÐ¸Ð½Ð»ÐµÐºÑ�Ð¸Ð»Ð¾Ð²Ð°Ð»Ð°Ñ�ÑŒ
                return true;
            }
        }
        return false;
    }

    public QCustomer gnawOutCustomerByNumber(String number) {
        for (QCustomer customer : getCustomers()) {
            if (number.equalsIgnoreCase(customer.getPrefix() + customer.getNumber())) {
                removeCustomer(customer); // ÑƒÐ±Ñ€Ð°Ñ‚ÑŒ Ð¸Ð· Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸
                return customer;
            }
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrefix() {
        return prefix == null ? "" : prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? "" : prefix;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public QService getLink() {
        return link;
    }

    public void setLink(QService link) {
        this.link = link;
    }

    public QSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(QSchedule schedule) {
        this.schedule = schedule;
    }

    public QCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(QCalendar calendar) {
        this.calendar = calendar;
    }

    public Set<QServiceLang> getLangs() {
        return langs;
    }

    public void setLangs(Set<QServiceLang> langs) {
        this.langs = langs;
    }

    ;

    public Set<QOffice> getOffices() {
        return offices;
    }

    public String getSmartboard() {
        return smartboard;
    }

    public void setSmartboard(String smartboard) {
        this.smartboard = smartboard;
    }

    public QServiceLang getServiceLang(String nameLocale) {
        if (qslangs == null) {
            qslangs = new HashMap<>();
            getLangs().stream().forEach((sl) -> {
                qslangs.put(sl.getLang(), sl);
            });
        }
        return qslangs.get(nameLocale);
    }
    //*******************************************************************************************************************
    //*******************************************************************************************************************
    //********************** Ð ÐµÐ°Ð»Ð¸Ð·Ð°Ñ†Ð¸Ñ� Ð¼ÐµÑ‚Ð¾Ð´Ð¾Ð² ÑƒÐ·Ð»Ð° Ð² Ð´ÐµÑ€ÐµÐ²Ðµ :: Implementing node methods in a tree*************************

    public String getTextToLocale(Field field) {
        final String nl = Locales.getInstance().getNameOfPresentLocale();
        final QServiceLang sl = getServiceLang(nl);
        switch (field) {
            case BUTTON_TEXT:
                return
                    !Locales.getInstance().isWelcomeMultylangs() || sl == null
                        || sl.getButtonText() == null
                        || sl.getButtonText().isEmpty() ? getButtonText() : sl.getButtonText();
            case INPUT_CAPTION:
                return !Locales.getInstance().isWelcomeMultylangs() || sl == null
                    || sl.getInput_caption() == null || sl.getInput_caption().isEmpty()
                    ? getInput_caption()
                    : sl.getInput_caption();
            case PRE_INFO_HTML:
                return !Locales.getInstance().isWelcomeMultylangs() || sl == null
                    || sl.getPreInfoHtml() == null || sl.getPreInfoHtml().isEmpty()
                    ? getPreInfoHtml()
                    : sl.getPreInfoHtml();
            case PRE_INFO_PRINT_TEXT:
                return !Locales.getInstance().isWelcomeMultylangs() || sl == null
                    || sl.getPreInfoPrintText() == null || sl.getPreInfoPrintText().isEmpty()
                    ? getPreInfoPrintText() : sl.getPreInfoPrintText();
            case TICKET_TEXT:
                return
                    !Locales.getInstance().isWelcomeMultylangs() || sl == null
                        || sl.getTicketText() == null
                        || sl.getTicketText().isEmpty() ? getTicketText() : sl.getTicketText();
            case DESCRIPTION:
                return !Locales.getInstance().isWelcomeMultylangs() || sl == null
                    || sl.getDescription() == null || sl.getDescription().isEmpty()
                    ? getDescription()
                    : sl.getDescription();
            case NAME:
                return
                    !Locales.getInstance().isWelcomeMultylangs() || sl == null
                        || sl.getName() == null || sl
                        .getName().isEmpty() ? getName() : sl.getName();
            default:
                throw new AssertionError();
        }

    }

    public String getTempReasonUnavailable() {
        return tempReasonUnavailable;
    }

    public void setTempReasonUnavailable(String tempReasonUnavailable) {
        this.tempReasonUnavailable = tempReasonUnavailable;
    }

    public LinkedList<QService> getChildren() {
        return childrenOfService;
    }

    @Override
    public void addChild(ITreeIdGetter child) {
        if (!childrenOfService
            .contains((QService) child)) { // Ð±Ñ‹Ð²Ð°ÐµÑ‚ Ñ‡Ñ‚Ð¾ Ð´Ð¾Ð±Ð°Ð²Ð»Ñ�ÐµÐ¼ Ð¿Ð¾Ð²Ñ‚Ð¾Ñ€Ð½Ð¾ ÑƒÐ¶ÐµÐ´Ð¾Ð±Ð°Ð²Ð»ÐµÐ½Ð½Ñ‹Ð¹
            childrenOfService.add((QService) child);
        }
    }

    @Override
    public QService getChildAt(int childIndex) {
        return childrenOfService.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return childrenOfService.size();
    }

    @Override
    public QService getParent() {
        return parentService;
    }

    @Override
    public void setParent(MutableTreeNode newParent) {
        parentService = (QService) newParent;
        if (parentService != null) {
            setParentId(parentService.id);
        } else {
            parentId = null;
        }
    }

    @Override
    public int getIndex(TreeNode node) {
        return childrenOfService.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return getChildCount() == 0;
    }

    @Override
    public Enumeration children() {
        return Collections.enumeration(childrenOfService);
    }

    @Override
    public void insert(MutableTreeNode child, int index) {
        child.setParent(this);
        this.childrenOfService.add(index, (QService) child);
    }

    @Override
    public void remove(int index) {
        this.childrenOfService.remove(index);
    }

    @Override
    public void remove(MutableTreeNode node) {
        this.childrenOfService.remove((QService) node);
    }

    @Override
    public void removeFromParent() {
        getParent().remove(getParent().getIndex(this));
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return true;
    }

    @Override
    public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException, IOException {
        if (this.isDataFlavorSupported(flavor)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public static enum Field {

        /**
         * Ð�Ð°Ð´Ð¿Ð¸Ñ�ÑŒ Ð½Ð° ÐºÐ½Ð¾Ð¿ÐºÐµ
         */
        BUTTON_TEXT,
        /**
         * Ð·Ð°Ð³Ð¾Ð»Ð¾Ð²Ð¾Ðº Ð²Ð²Ð¾Ð´Ð° ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð¾Ð¼
         */
        INPUT_CAPTION,
        /**
         * Ñ‡Ð¸Ñ‚Ð°ÐµÐ¼ Ð¿ÐµÑ€ÐµÐ´ Ñ‚ÐµÐ¼ ÐºÐ°Ðº Ð²Ñ�Ñ‚Ð°Ñ‚ÑŒ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ
         */
        PRE_INFO_HTML,
        /**
         * Ð¿ÐµÑ‡Ð°Ñ‚Ð°ÐµÐ¼ Ð¿Ð¾Ð´Ñ�ÐºÐ°Ð·ÐºÑƒ Ð¿ÐµÑ€ÐµÐ´ Ñ‚ÐµÐ¼ ÐºÐ°Ðº Ð²Ñ�Ñ‚Ð°Ñ‚ÑŒ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ
         */
        PRE_INFO_PRINT_TEXT,
        /**
         * Ñ‚ÐµÐºÑ�Ñ‚ Ð½Ð° Ñ‚Ð°Ð»Ð¾Ñ‚Ðµ Ð¿ÐµÑ€Ñ�Ð¾Ð½Ð°Ð»ÑŒÐ½Ð¾ ÑƒÑ�Ð»ÑƒÐ³Ð¸
         */
        TICKET_TEXT,
        /**
         * Ð¾Ð¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ ÑƒÑ�Ð»ÑƒÐ³Ð¸
         */
        DESCRIPTION,
        /**
         * Ð¸Ð¼Ñ� ÑƒÑ�Ð»ÑƒÐ³Ð¸
         */
        NAME
    }
}
