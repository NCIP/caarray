//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.MaxSerializableSize;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.validation.UniqueConstraint;
import gov.nih.nci.caarray.validation.UniqueConstraintField;
import gov.nih.nci.caarray.validation.UniqueConstraints;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.IndexColumn;

/**
 * Contains an ordered list of the microarray design elements (features or probes) for which data values are provided.
 */
@Entity
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@UniqueConstraints(constraints = {
        @UniqueConstraint(fields = {@UniqueConstraintField(name = "lsidAuthority"),
                @UniqueConstraintField(name = "lsidNamespace"),
                @UniqueConstraintField(name = "lsidObjectId") }) },
                message = "{designElementList.uniqueConstraint}")
public final class DesignElementList extends AbstractCaArrayEntity {

    private static final long serialVersionUID = 4430513886275629776L;
    private static final int BATCH_SIZE = 200;
    private static final int MAX_SERIALIZABLE_SIZE = 100000;

    private List<AbstractDesignElement> designElements = new ArrayList<AbstractDesignElement>();
    private String designElementType;

    /**
     * @return the designElements
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @IndexColumn(name = "designelement_index")
    @JoinTable(
            name = "designelementlist_designelement",
            joinColumns = { @JoinColumn(name = "designelementlist_id") },
            inverseJoinColumns = { @JoinColumn(name = "designelement_id") }
    )
    @ForeignKey(name = "deldedesignelementlist_fk", inverseName = "deldedesignelement_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @BatchSize(size = BATCH_SIZE)
    @MaxSerializableSize(MAX_SERIALIZABLE_SIZE)
    public List<AbstractDesignElement> getDesignElements() {
        return designElements;
    }

    /**
     * @param designElements the designElements to set
     */
    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    private void setDesignElements(List<AbstractDesignElement> designElements) {
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

}
