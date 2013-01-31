//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A DataSet represents the collection of data across several hybridizations for a particular set of quantities
 * (QuantitationTypes) and design elements. 
 *   
 * @author dkokotov
 */
public class DataSet implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<DesignElement> designElements = new ArrayList<DesignElement>();
    private List<HybridizationData> datas = new ArrayList<HybridizationData>();
    private List<QuantitationType> quantitationTypes = new ArrayList<QuantitationType>();

    /**
     * @return the designElements
     */
    public List<DesignElement> getDesignElements() {
        return designElements;
    }

    /**
     * @param designElements the designElements to set
     */
    public void setDesignElements(List<DesignElement> designElements) {
        this.designElements = designElements;
    }

    /**
     * @return the datas
     */
    public List<HybridizationData> getDatas() {
        return datas;
    }

    /**
     * @param datas the datas to set
     */
    public void setDatas(List<HybridizationData> datas) {
        this.datas = datas;
    }

    /**
     * @return the quantitationTypes
     */
    public List<QuantitationType> getQuantitationTypes() {
        return quantitationTypes;
    }

    /**
     * @param quantitationTypes the quantitationTypes to set
     */
    public void setQuantitationTypes(List<QuantitationType> quantitationTypes) {
        this.quantitationTypes = quantitationTypes;
    }
}
