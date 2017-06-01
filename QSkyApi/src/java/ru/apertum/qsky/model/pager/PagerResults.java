/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.model.pager;

import java.io.Serializable;
import java.util.Date;
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
@Table(name = "pager_results")
@NamedQueries({
    @NamedQuery(name = "PagerResults.findAll", query = "SELECT p FROM PagerResults p"),
    @NamedQuery(name = "PagerResults.findById", query = "SELECT p FROM PagerResults p WHERE p.id = :id"),
    @NamedQuery(name = "PagerResults.findByIp", query = "SELECT p FROM PagerResults p WHERE p.ip = :ip"),
    @NamedQuery(name = "PagerResults.findByEventTime", query = "SELECT p FROM PagerResults p WHERE p.eventTime = :eventTime"),
    @NamedQuery(name = "PagerResults.findByInputData", query = "SELECT p FROM PagerResults p WHERE p.inputData = :inputData")})
public class PagerResults implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "ip")
    private String ip;
    @Basic(optional = false)
    @NotNull
    @Column(name = "event_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventTime;
    @Size(max = 545)
    @Column(name = "input_data")
    private String inputData;
    @Size(max = 45)
    @Column(name = "qsys_version")
    private String qsysVer;
    @Size(max = 45)
    @Column(name = "qsys_mac")
    private String qsysMac;
    @Size(max = 45)
    @Column(name = "qsys_token")
    private String qsysToken;
    @Column(name = "qsys_checkdb")
    private Integer qsysCheckdb = 0;
    @Column(name = "qsys_usrs")
    private Integer qsysUsrs = 0;
    @Column(name = "qsys_srvs")
    private Integer qsysSrvs = 0;
    @Column(name = "qsys_nm")
    private String qsysNm = "";
    @JoinColumn(name = "quiz_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private PagerQuizItems quizId;
    @JoinColumn(name = "pager_data_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PagerData pagerDataId;

    public PagerResults() {
    }

    public PagerResults(String ip, Date eventTime, String qsysVersion, String qsysMac, String qsysToken, Integer qsysCheckdb, Integer qsysUsrs, Integer qsysSrvs, String nm) {
        this.ip = ip;
        this.eventTime = eventTime;
        this.qsysVer = qsysVersion;
        this.qsysMac = qsysMac;
        this.qsysToken = qsysToken;
        this.qsysCheckdb = qsysCheckdb;
        this.qsysSrvs = qsysSrvs;
        this.qsysUsrs = qsysUsrs;
        this.qsysNm = nm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getQsysCheckdb() {
        return qsysCheckdb;
    }

    public void setQsysCheckdb(Integer qsysCheckdb) {
        this.qsysCheckdb = qsysCheckdb;
    }

    public Integer getQsysUsrs() {
        return qsysUsrs;
    }

    public void setQsysUsrs(Integer qsysUsrs) {
        this.qsysUsrs = qsysUsrs;
    }

    public Integer getQsysSrvs() {
        return qsysSrvs;
    }

    public void setQsysSrvs(Integer qsysSrvs) {
        this.qsysSrvs = qsysSrvs;
    }

    public String getQsysNm() {
        return qsysNm;
    }

    public void setQsysNm(String qsysNm) {
        this.qsysNm = qsysNm;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getQsysVer() {
        return qsysVer;
    }

    public void setQsysVer(String qsysVer) {
        this.qsysVer = qsysVer;
    }

    public String getQsysMac() {
        return qsysMac;
    }

    public String getQsysToken() {
        return qsysToken;
    }

    public PagerQuizItems getQuizId() {
        return quizId;
    }

    public void setQuizId(PagerQuizItems quizId) {
        this.quizId = quizId;
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
        if (!(object instanceof PagerResults)) {
            return false;
        }
        PagerResults other = (PagerResults) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "ru.apertum.qsky.model.pager.PagerResults[ id=" + id + " ]";
    }
}
