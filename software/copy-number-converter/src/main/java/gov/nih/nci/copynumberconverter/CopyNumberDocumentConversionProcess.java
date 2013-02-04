//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.copynumberconverter;

/**
 * CopyNumberDocumentConversionProcess contract.
 * @author dharley
 *
 */
public interface CopyNumberDocumentConversionProcess {
    
    /**
     * Converts the specified input file into the specified output file, using the specified ColumnInfo.
     * @param inputFile the input file path
     * @param outputFile the output file path
     * @param columnInfo the column info
     * @throws ConversionException if conversion fails
     */
    void convert(String inputFile, String outputFile, ColumnInfo columnInfo) throws ConversionException;

}
