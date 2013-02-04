//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.domain.array;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;

/**
 * Represents a single, physical microarray used in an experiment.
 */
@Entity
public class Array extends AbstractCaArrayEntity {

    private static final long serialVersionUID = 1234567890L;

    private String batch;
    private String serialNumber;
    private ProtocolApplication production;
    private ArrayDesign design;
    private ArrayGroup arrayGroup;

    /**
     * Gets the batch.
     *
     * @return the batch
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getBatch() {
        return batch;
    }

    /**
     * Sets the batch.
     *
     * @param batchVal
     *            the batch
     */
    public void setBatch(final String batchVal) {
        this.batch = batchVal;
    }

    /**
     * Gets the serialNumber.
     *
     * @return the serialNumber
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serialNumber.
     *
     * @param serialNumberVal
     *            the serialNumber
     */
    public void setSerialNumber(final String serialNumberVal) {
        this.serialNumber = serialNumberVal;
    }

    /**
     * Gets the production.
     *
     * @return the production
     */
    @ManyToOne
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE })
    @ForeignKey(name = "array_production_idk")
    public ProtocolApplication getProduction() {
        return production;
    }

    /**
     * Sets the production.
     *
     * @param productionVal
     *            the production
     */
    public void setProduction(final ProtocolApplication productionVal) {
        this.production = productionVal;
    }

    /**
     * Gets the design.
     *
     * @return the design
     */
    @ManyToOne()
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "array_design_idk")
    public ArrayDesign getDesign() {
        return design;
    }

    /**
     * Sets the design.
     *
     * @param designVal
     *            the design
     */
    public void setDesign(final ArrayDesign designVal) {
        this.design = designVal;
    }

    /**
     * @return the array group
     */
    @ManyToOne
    @ForeignKey(name = "array_group_idk")
    public ArrayGroup getArrayGroup() {
        return arrayGroup;
    }

    /**
     * @param arrayGroup new array group
     */
    public void setArrayGroup(ArrayGroup arrayGroup) {
        this.arrayGroup = arrayGroup;
    }
}
