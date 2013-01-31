//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.carpla.domain.rplarray.RplaSample;

import com.fiveamsolutions.nci.commons.data.search.SortCriterion;


public enum RplaSampleSortCriterion implements SortCriterion<RplaSample> {
    /**
     * name.
     */
    NAME ("name"),
    RPLArray("rplArray")
    
    
    ;

    private final String orderField;

    private RplaSampleSortCriterion(String orderField) {
        this.orderField = orderField;
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderField() {
        return this.orderField;
    }
}
