/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.model.pager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "pager_quiz_items")
@NamedQueries({
    @NamedQuery(name = "PagerQuizItems.findAll", query = "SELECT p FROM PagerQuizItems p"),
    @NamedQuery(name = "PagerQuizItems.findById", query = "SELECT p FROM PagerQuizItems p WHERE p.id = :id"),
    @NamedQuery(name = "PagerQuizItems.findByItemText", query = "SELECT p FROM PagerQuizItems p WHERE p.itemText = :itemText")})
public class PagerQuizItems implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    @Expose
    @SerializedName("id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 145)
    @Column(name = "item_text")
    @Expose
    @SerializedName("text")
    private String itemText;
    @OneToMany(mappedBy = "quizId", fetch = FetchType.LAZY)
    private List<PagerResults> pagerResultsList;
    @JoinColumn(name = "pager_data_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private PagerData pagerDataId;

    public PagerQuizItems() {
    }

    public PagerQuizItems(Long id) {
        this.id = id;
    }

    public PagerQuizItems(Long id, String itemText) {
        this.id = id;
        this.itemText = itemText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public List<PagerResults> getPagerResultsList() {
        return pagerResultsList;
    }

    public void setPagerResultsList(List<PagerResults> pagerResultsList) {
        this.pagerResultsList = pagerResultsList;
    }

    public PagerData getPagerDataId() {
        return pagerDataId;
    }

    public void setPagerDataId(PagerData pagerDataId) {
        this.pagerDataId = pagerDataId;
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
        if (!(object instanceof PagerQuizItems)) {
            return false;
        }
        PagerQuizItems other = (PagerQuizItems) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.apertum.qsky.model.pager.PagerQuizItems[ id=" + id + " ]";
    }
}
