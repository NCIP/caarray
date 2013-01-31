//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.data;

import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * HybridizationData represents the set of measurements captured from a single hybridization.
 * 
 * @author dkokotov
 */
public class HybridizationData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<AbstractDataColumn> dataColumns = new ArrayList<AbstractDataColumn>();
    private Hybridization hybridization;

    /**
     * @return the dataColumns
     */
    public List<AbstractDataColumn> getDataColumns() {
        return dataColumns;
    }

    /**
     * @param dataColumns the dataColumns to set
     */
    public void setDataColumns(List<AbstractDataColumn> dataColumns) {
        this.dataColumns = dataColumns;
    }

    /**
     * @return the hybridization
     */
    public Hybridization getHybridization() {
        return hybridization;
    }

    /**
     * @param hybridization the hybridization to set
     */
    public void setHybridization(Hybridization hybridization) {
        this.hybridization = hybridization;
    }
}
