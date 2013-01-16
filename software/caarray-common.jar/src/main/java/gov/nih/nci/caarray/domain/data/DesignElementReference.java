//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.array.AbstractDesignElement;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * @author dkokotov
 * 
 */
@Entity
@Table(name = "designelementlist_designelement")
public class DesignElementReference implements PersistentObject {
    private static final long serialVersionUID = 1L;
    
    private AbstractDesignElement designElement;
    private int index;
    private DesignElementList designElementList;
    private Long id;

    /**
     * @return database identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return this.id;
    }

    @SuppressWarnings({"PMD.UnusedPrivateMethod", "unused" })
    private void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the designElement
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "designelement_id")
    public AbstractDesignElement getDesignElement() {
        return designElement;
    }

    /**
     * @param designElement the designElement to set
     */
    public void setDesignElement(AbstractDesignElement designElement) {
        this.designElement = designElement;
    }

    /**
     * @return the index
     */
    @Column(name = "designelement_index")
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the designElementList
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "designelementlist_id")
    public DesignElementList getDesignElementList() {
        return designElementList;
    }

    /**
     * @param designElementList the designElementList to set
     */
    public void setDesignElementList(DesignElementList designElementList) {
        this.designElementList = designElementList;
    }
}
