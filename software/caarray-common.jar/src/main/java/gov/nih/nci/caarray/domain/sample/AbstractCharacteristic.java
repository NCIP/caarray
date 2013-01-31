//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.sample;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.vocabulary.Category;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.NotNull;

/**
 *
 */
@Entity
@Table(name = "characteristic")
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractCharacteristic extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;

    private AbstractBioMaterial bioMaterial;
    private Category category;

    /**
     * Hibernate-only constructor.
     */
    public AbstractCharacteristic() {
        // empty
    }

    /**
     * Creates a new AbstractCharacteristic with given category.
     * @param category the category
     */
    public AbstractCharacteristic(final Category category) {
        this.category = category;
    }

    /**
     * @return the abstractBioMaterial
     */
    @ManyToOne
    @ForeignKey(name = "characteristic_biomaterial_fk")
    public AbstractBioMaterial getBioMaterial() {
        return bioMaterial;
    }

    /**
     * @param abstractBioMaterial the abstractBioMaterial to set
     */
    public void setBioMaterial(AbstractBioMaterial abstractBioMaterial) {
        this.bioMaterial = abstractBioMaterial;
    }

    /**
     * @return the category for this characteristic
     */
    @ManyToOne(optional = false)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @NotNull
    public Category getCategory() {
        return category;
    }

    /**
     * Set the category.
     * @param category The category to set
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @return the value of this characteristic as a string displayable
     * in the ui
     */
    @Transient
    public abstract String getDisplayValue();

}
