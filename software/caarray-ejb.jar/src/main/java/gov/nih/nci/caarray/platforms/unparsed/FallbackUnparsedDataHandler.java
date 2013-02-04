//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.platforms.unparsed;

import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import com.google.inject.Inject;

/**
 * A non parsing Data handler that can process any file type; used as fallback for a normally parsable array file when
 * the corresponding array design is missing or not parsable.
 * 
 * @author gax
 */
public class FallbackUnparsedDataHandler extends UnparsedDataHandler {

    /**
     * default ctor.
     * 
     * @param dataStorageFacade data storage facade for retrieving data
     */
    @Inject
    public FallbackUnparsedDataHandler(DataStorageFacade dataStorageFacade) {
        super(dataStorageFacade);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isFileSupported(CaArrayFile dataFile) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(MageTabDocumentSet mTabSet, FileValidationResult result, ArrayDesign design) {
        if (design == null) {
            result.addMessage(Type.INFO, "Not parsed due to missing array design");
        } else {
            result.addMessage(Type.INFO, "Not parsed because array design " + design.getName() + " is not parsed");
        }
    }

}
