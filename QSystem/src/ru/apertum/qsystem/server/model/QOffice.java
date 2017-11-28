package ru.apertum.qsystem.server.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * This is the office, which is what the system is multi-tenanted keyed on.
 *
 * @author Sean Rumsby
 */
@Entity
@Table(name = "offices")
public class QOffice implements IidGetter, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Expose
    @SerializedName("id")
    @Column(name = "id")
    private Long id;
    @Expose
    @SerializedName("name")
    @Column(name = "name")
    private String name;
    @Expose
    @Column(name = "smartboard_type")
    @SerializedName("smartboard_type")
    private String smartboard_type;
    @Column(name = "deleted")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date deleted;
    @ManyToMany(mappedBy = "offices")
    private Set<QService> services = new HashSet<>();

    public QOffice() {
        super();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmartboardType() {
        return smartboard_type;
    }

    public void setSmartboardType(String smartboard_type) {
        this.smartboard_type = smartboard_type;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

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
