package ru.apertum.qsystem.server.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.*;
import javax.persistence.*;

import org.hibernate.Criteria;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.common.QLog;

/**
 * This is the office, which is what the system is multi-tenanted keyed on.
 *
 * @author Sean Rumsby
 */
@Entity
@Table(name = "offices")
public class QOffice implements IidGetter, Serializable {

    public QOffice(){
        super();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Expose
    @SerializedName("id")
    @Column(name = "id")
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Expose
    @SerializedName("name")
    @Column(name = "name")
    private String name;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Expose
    @Column(name = "smartboard_type")
    @SerializedName("smartboard_type")
    private String smartboard_type;

    public String getSmartboardType() {
        return smartboard_type;
    }

    public void setSmartboardType(String smartboard_type) {
        this.smartboard_type = smartboard_type;
    }

    @Column(name = "deleted")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date deleted;

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    @ManyToMany(mappedBy = "offices")
    private Set<QService> services = new HashSet<>();

    public Set<QService> getServices() {
        return services;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof QOffice) {
            final QOffice o = (QOffice) obj;
            return (id == null ? o.getId() == null : id.equals(o.getId()))
                    && (name == null ? o.getName() == null : name.equals(o.getName()));
        } else {
            return false;
        }
    }
}
