//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.data;

/**
 * Interface that <code>enums</code> of specific quantitation types must implement in order to
 * register potentially new <code>QuantitationTypes</code> with caArray.
 */
public interface QuantitationTypeDescriptor {

    /**
     * Returns the name of the type.
     *
     * @return the quantitation type name.
     */
    String getName();

    /**
     * Returns the data type of values of this quantitation type.
     *
     * @return the data type.
     */
    DataType getDataType();

}
