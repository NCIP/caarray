//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.MaxSerializableSize;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.validation.UniqueConstraint;
import gov.nih.nci.caarray.validation.UniqueConstraintField;
import gov.nih.nci.caarray.validation.UniqueConstraints;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * Contains an ordered list of the microarray design elements (features or probes) for which data values are provided.
 */
@Entity
@UniqueConstraints(constraints = {
        @UniqueConstraint(fields = {@UniqueConstraintField(name = "lsidAuthority"),
                @UniqueConstraintField(name = "lsidNamespace"),
                @UniqueConstraintField(name = "lsidObjectId") }) },
                message = "{designElementList.uniqueConstraint}")
public class DesignElementList extends AbstractCaArrayEntity {

    private static final long serialVersionUID = 4430513886275629776L;
    private static final int MAX_SERIALIZABLE_SIZE = 100000;

    private List<DesignElementReference> designElementReferences = new LinkedList<DesignElementReference>();
    private List<AbstractDesignElement> designElements = new LinkedList<AbstractDesignElement>();
    private String designElementType;

    /**
     * @return the designElements
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @IndexColumn(name = "designelement_index")
    @JoinTable(
            name = "designelementlist_designelement",
            joinColumns = { @JoinColumn(name = "designelementlist_id", insertable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "designelement_id", insertable = false, updatable = false) }
    )
    @ForeignKey(name = "deldedesignelementlist_fk", inverseName = "deldedesignelement_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @MaxSerializableSize(MAX_SERIALIZABLE_SIZE)
    @LazyCollection(LazyCollectionOption.EXTRA)
    public List<AbstractDesignElement> getDesignElements() {
        return designElements;
    }

    /**
     * @param designElements the designElements to set
     */
    public void setDesignElements(List<AbstractDesignElement> designElements) {
        this.designElements = designElements;
    }

    /**
     * @return the designElementType
     */
    public String getDesignElementType() {
        return designElementType;
    }

    /**
     * @param designElementType the designElementType to set
     */
    public void setDesignElementType(String designElementType) {
        DesignElementType.checkType(designElementType);
        this.designElementType = designElementType;
    }

    /**
     * @return the designElementType enum
     */
    @Transient
    public DesignElementType getDesignElementTypeEnum() {
        return DesignElementType.getByValue(getDesignElementType());
    }

    /**
     * @param designElementTypeEnum the designElementTypeEnum to set
     */
    public void setDesignElementTypeEnum(DesignElementType designElementTypeEnum) {
        if (designElementTypeEnum == null) {
            setDesignElementType(null);
        } else {
            setDesignElementType(designElementTypeEnum.getValue());
        }
    }

    /**
     * @return the designElementReferences
     */
    @OneToMany(mappedBy = "designElementList")
    @OrderBy("index asc")
    public List<DesignElementReference> getDesignElementReferences() {
        return designElementReferences;
    }

    /**
     * @param designElementReferences the designElementReferences to set
     */
    public void setDesignElementReferences(List<DesignElementReference> designElementReferences) {
        this.designElementReferences = designElementReferences;
    }

}
