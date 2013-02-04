//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.IndexColumn;

/**
 * Value holder for all the data values associated with a specific hybridization.
 */
@Entity
public class HybridizationData extends AbstractCaArrayObject {

    private static final long serialVersionUID = 8804648118447372387L;

    private DataSet dataSet;
    private Hybridization hybridization;
    private LabeledExtract labeledExtract;
    private List<AbstractDataColumn> columns = new ArrayList<AbstractDataColumn>();

    /**
     * @return the columns
     */
    @OneToMany(fetch = FetchType.LAZY)
    @IndexColumn(name = "COLUMN_INDEX")
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    public List<AbstractDataColumn> getColumns() {
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    private void setColumns(List<AbstractDataColumn> columns) {
        this.columns = columns;
    }

    /**
     * @return the hybridization
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "hybridizationdata_hybridization_fk")
    public Hybridization getHybridization() {
        return hybridization;
    }

    /**
     * @param hybridization the hybridization to set
     */
    public void setHybridization(Hybridization hybridization) {
        this.hybridization = hybridization;
    }

    /**
     * @return the labeledExtract
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "hybridizationdata_labeledextract_fk")
    public LabeledExtract getLabeledExtract() {
        return labeledExtract;
    }

    /**
     * @param labeledExtract the labeledExtract to set
     */
    public void setLabeledExtract(LabeledExtract labeledExtract) {
        this.labeledExtract = labeledExtract;
    }

    /**
     * @return the dataSet
     */
    @ManyToOne
    @JoinColumn(insertable = false, updatable = false, name = "data_set")
    @ForeignKey(name = "hybridizationdata_dataset_fk")
    public DataSet getDataSet() {
        return dataSet;
    }

    /**
     * @param dataSet the dataSet to set
     */
    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * Initialize the columns of this HybridizationData with empty value sets of given size.
     * 
     * @param types the types of the columns to initialize
     * @param numberOfRows the number of rows each columnn's array of values should have
     */
    public void prepareColumns(List<QuantitationType> types, int numberOfRows) {
        for (AbstractDataColumn column : columns) {
            if (!column.isLoaded() && types.contains(column.getQuantitationType())) {
                column.initializeArray(numberOfRows);
            }
        }
    }

    void addColumn(QuantitationType type) {
        AbstractDataColumn column = AbstractDataColumn.create(type);
        column.setHybridizationData(this);
        columns.add(column);
    }

    /**
     * Determine whether all columns for the given quantitation types in this HybridizationData have been loaded with
     * data values.
     * 
     * @param types the quantitation types of interest
     * @return true if each column in this HybridizationData whose type is in the given set of types has been loaded,
     *         e.g. column.isLoaded is true; false otherwise.
     */
    public boolean areColumnsLoaded(Collection<QuantitationType> types) {
        for (AbstractDataColumn column : columns) {
            if (types.contains(column.getQuantitationType()) && !column.isLoaded()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the column matching the provided type, if one exists.
     *
     * @param type get column for this type
     * @return the matching column or null.
     */
    public AbstractDataColumn getColumn(QuantitationType type) {
        for (AbstractDataColumn column : columns) {
            if (column.getQuantitationType().equals(type)) {
                return column;
            }
        }
        return null;
    }

    /**
     * Returns the column matching the provided type, if one exists.
     *
     * @param typeDescriptor get column for this type
     * @return the matching column or null.
     */
    public AbstractDataColumn getColumn(QuantitationTypeDescriptor typeDescriptor) {
        for (AbstractDataColumn column : columns) {
            if (column.getQuantitationType().getName().equals(typeDescriptor.getName())) {
                return column;
            }
        }
        return null;
    }

}
