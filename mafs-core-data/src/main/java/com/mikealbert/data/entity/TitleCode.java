package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to TITLE_CODES table
 * @author sibley
 */
@Entity
@Table(name = "TITLE_CODES")
public class TitleCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "TITLE_CODE")
    private String titleCode;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;

    @OneToMany(mappedBy = "title", fetch = FetchType.LAZY)
    private List<ExternalAccount> externalAccountsList;

    public TitleCode() {
    }

    public TitleCode(String titleCode) {
        this.titleCode = titleCode;
    }

    public TitleCode(String titleCode, String description) {
        this.titleCode = titleCode;
        this.description = description;
    }

    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(String titleCode) {
        this.titleCode = titleCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ExternalAccount> getExternalAccountsList() {
        return externalAccountsList;
    }

    public void setExternalAccountsList(List<ExternalAccount> externalAccountsList) {
        this.externalAccountsList = externalAccountsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (titleCode != null ? titleCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TitleCode)) {
            return false;
        }
        TitleCode other = (TitleCode) object;
        if ((this.titleCode == null && other.titleCode != null) || (this.titleCode != null && !this.titleCode.equals(other.titleCode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.TitleCodes[ titleCode=" + titleCode + " ]";
    }

}
