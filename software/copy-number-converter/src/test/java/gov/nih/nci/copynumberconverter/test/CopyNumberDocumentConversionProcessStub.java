//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.copynumberconverter.test;

import gov.nih.nci.copynumberconverter.ColumnInfo;
import gov.nih.nci.copynumberconverter.ConversionException;
import gov.nih.nci.copynumberconverter.CopyNumberDocumentConversionProcess;

public class CopyNumberDocumentConversionProcessStub implements
        CopyNumberDocumentConversionProcess {

    public void convert(String inputFile, String outputFile,
            ColumnInfo columnInfo) throws ConversionException {
        // does nothing
    }

}
