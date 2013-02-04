//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.IndexColumn;

/**
 * Contains hybridization data represented by an <code>AbstractArrayData</code> or as requested by a client.
 */
@Entity
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class DataSet extends AbstractCaArrayObject {

    private static final long serialVersionUID = 4430513886275629776L;

    private AbstractArrayData arrayData;
    private List<HybridizationData> hybridizationDataList = new ArrayList<HybridizationData>();
    private List<QuantitationType> quantitationTypes = new ArrayList<QuantitationType>();
    private DesignElementList designElementList;

    
    /**
     * @return the arrayData
     */
    @OneToOne(mappedBy = "dataSet")
    public AbstractArrayData getArrayData() {
        return arrayData;
    }

    /**
     * @param arrayData the arrayData to set
     */
    public void setArrayData(AbstractArrayData arrayData) {
        this.arrayData = arrayData;
    }

    /**
     * @return the hybridizationDatas
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_set")
    @IndexColumn(name = "HYBRIDIZATION_INDEX")
    @Cascade(CascadeType.ALL)
    public List<HybridizationData> getHybridizationDataList() {
        return hybridizationDataList;
    }

    /**
     * Creates a new <code>HybridizationData</code> for this <code>DataSet</code>.
     * @param hybridization <code>Hybridization</code> associated with the data
     *
     * @return the new HybridizationData.
     */
    public HybridizationData addHybridizationData(Hybridization hybridization) {
        HybridizationData hybridizationData = new HybridizationData();
        hybridizationData.setHybridization(hybridization);
        getHybridizationDataList().add(hybridizationData);
        hybridizationData.setDataSet(this);
        return hybridizationData;
    }

    /**
     * @param hybridizationDatas the hybridizationDatas to set
     */
    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    private void setHybridizationDataList(List<HybridizationData> hybridizationDatas) {
        this.hybridizationDataList = hybridizationDatas;
    }

    /**
     * @return the quantitationTypes
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @IndexColumn(name = "quantitationtype_index")
    @JoinTable(
            name = "datasetquantitationtype",
            joinColumns = { @JoinColumn(name = "dataset_id") },
            inverseJoinColumns = { @JoinColumn(name = "quantitationtype_id") }
    )
    @ForeignKey(name = "dataset_fk", inverseName = "quantitationtype_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public List<QuantitationType> getQuantitationTypes() {
        return quantitationTypes;
    }

    /**
     * @param quantitationTypes the quantitationTypes to set
     */
    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    private void setQuantitationTypes(List<QuantitationType> quantitationTypes) {
        this.quantitationTypes = quantitationTypes;
    }

    /**
     * Adds a new type to this <code>DataSet</code>, creating the appropriate columns for all
     * <code>HybridizationDatas</code>.
     *
     * @param type the type to add.
     */
    public void addQuantitationType(QuantitationType type) {
        quantitationTypes.add(type);
        for (HybridizationData hybridizationData : hybridizationDataList) {
            hybridizationData.addColumn(type);
        }
    }

    /**
     * Adds a list of new types to this <code>DataSet</code>, creating the appropriate columns for all
     * <code>HybridizationDatas</code>.
     *
     * @param types the types to add.
     */
    public void addQuantitationTypes(List<QuantitationType> types) {
        for (QuantitationType type : types) {
            addQuantitationType(type);
        }
    }
    /**
     * Initialize the columns of each HybridizationData in this DataSet with empty value sets of given size.
     * 
     * @param types the types of the columns to initialize
     * @param numberOfRows the number of rows each columnn's array of values should have
     */    
    public void prepareColumns(List<QuantitationType> types, int numberOfRows) {
        for (HybridizationData hybridizationData : hybridizationDataList) {
            hybridizationData.prepareColumns(types, numberOfRows);
        }
    }
    
    /**
     * Determine whether all columns for the given quantitation types in each HybridizationData in this DataSet have
     * been loaded with data values.
     * 
     * @param types the quantitation types of interest
     * @return true if for each HybridizationData in this DataSet, each column whose type is in the given set of types
     *         has been loaded, e.g. column.isLoaded is true; false otherwise.
     */
    public boolean datasLoaded(List<QuantitationType> types) {
        for (HybridizationData hybridizationData : hybridizationDataList) {
            if (!hybridizationData.areColumnsLoaded(types)) {
                return false;
            }
        }
        return true;
    }


    /**
     * @return the designElementList
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "DATASET_DESIGNELEMENTLIST_FK")
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
