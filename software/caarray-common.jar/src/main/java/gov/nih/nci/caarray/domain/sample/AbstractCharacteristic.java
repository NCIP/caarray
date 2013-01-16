//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.sample;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.AbstractUnitableValue;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.validator.NotNull;

/**
 *
 */
@Entity
@Table(name = "characteristic")
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractCharacteristic extends AbstractUnitableValue {
    private static final long serialVersionUID = 1L;

    private AbstractBioMaterial bioMaterial;
    private Category category;
    private Term unit;

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
     * Creates a new AbstractCharacteristic with given category and unit.
     * @param category the category
     * @param unit the unit
     */
    public AbstractCharacteristic(final Category category, final Term unit) {
        this.category = category;
        this.unit = unit;
    }

    /**
     * @return the abstractBioMaterial
     */
    @ManyToOne
    @LazyToOne(LazyToOneOption.PROXY)
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
     * Gets the unit.
     *
     * @return the unit
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "characteristic_measurement_unit_fk")
    public Term getUnit() {
        return unit;
    }

    /**
     * Sets the unit.
     *
     * @param unitVal the unit
     */
    public void setUnit(final Term unitVal) {
        this.unit = unitVal;
    }
}
