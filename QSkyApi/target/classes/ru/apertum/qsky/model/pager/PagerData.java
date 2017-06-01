/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.model.pager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "pager_data")
@NamedQueries({
    @NamedQuery(name = "PagerData.findAll", query = "SELECT p FROM PagerData p"),
    @NamedQuery(name = "PagerData.findById", query = "SELECT p FROM PagerData p WHERE p.id = :id"),
    @NamedQuery(name = "PagerData.findByDataType", query = "SELECT p FROM PagerData p WHERE p.dataType = :dataType"),
    @NamedQuery(name = "PagerData.findByTextData", query = "SELECT p FROM PagerData p WHERE p.textData = :textData"),
    @NamedQuery(name = "PagerData.findByQuizCaption", query = "SELECT p FROM PagerData p WHERE p.quizCaption = :quizCaption"),
    @NamedQuery(name = "PagerData.findByStartDate", query = "SELECT p FROM PagerData p WHERE p.startDate = :startDate"),
    @NamedQuery(name = "PagerData.findByActive", query = "SELECT p FROM PagerData p WHERE p.active = :active")})
public class PagerData implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    @Expose
    @SerializedName("id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "data_type")
    @Expose
    @SerializedName("type")
    private int dataType;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1450)
    @Column(name = "text_data")
    @Expose
    @SerializedName("text")
    private String textData;
    @Size(max = 145)
    @Column(name = "quiz_caption")
    @Expose
    @SerializedName("qcap")
    private String quizCaption;
    @Basic(optional = false)
    @NotNull
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "active")
    private boolean active;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pagerDataId", fetch = FetchType.LAZY)
    private List<PagerResults> pagerResultsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pagerDataId", fetch = FetchType.EAGER)
    @Expose
    @SerializedName("quis_items")
    private List<PagerQuizItems> pagerQuizItemsList;

    public PagerData() {
    }

    public PagerData(Long id) {
        this.id = id;
    }

    public PagerData(Long id, int dataType, String textData, Date startDate, boolean active) {
        this.id = id;
        this.dataType = dataType;
        this.textData = textData;
        this.startDate = startDate;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getTextData() {
        return textData;
    }

    public void setTextData(String textData) {
        this.textData = textData;
    }

    public String getQuizCaption() {
        return quizCaption;
    }

    public void setQuizCaption(String quizCaption) {
        this.quizCaption = quizCaption;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<PagerResults> getPagerResultsList() {
        return pagerResultsList;
    }

    public void setPagerResultsList(List<PagerResults> pagerResultsList) {
        this.pagerResultsList = pagerResultsList;
    }

    public List<PagerQuizItems> getPagerQuizItemsList() {
        return pagerQuizItemsList;
    }

    public void setPagerQuizItemsList(List<PagerQuizItems> pagerQuizItemsList) {
        this.pagerQuizItemsList = pagerQuizItemsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PagerData)) {
            return false;
        }
        PagerData other = (PagerData) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.apertum.qsky.model.pager.PagerData[ id=" + id + " ]";
    }
    
}
