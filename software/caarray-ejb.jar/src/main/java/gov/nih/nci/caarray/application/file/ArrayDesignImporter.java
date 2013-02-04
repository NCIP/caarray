//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.domain.array.ArrayDesign;

/**
 * Responsible for importing all the array designs in a file set.
 */
class ArrayDesignImporter {
    private final ArrayDesignService arrayDesignService;

    ArrayDesignImporter(ArrayDesignService arrayDesignService) {
        this.arrayDesignService = arrayDesignService;
    }

    void importArrayDesign(ArrayDesign arrayDesign) {
        arrayDesignService.importDesignDetails(arrayDesign);
    }
}
