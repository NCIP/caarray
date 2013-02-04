//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.data;


import java.util.List;

/**
 * Interface that <code>enums</code> of specific supported array data types must implement in order to
 * register potentially new <code>ArrayDataTypes</code> with caArray.
 */
public interface ArrayDataTypeDescriptor {
    
    /**
     * Returns the name of this data type for display in the application (e.g. "Affymetrix CEL").
     * 
     * @return the data type name.
     */
    String getName();
    
    /**
     * Returns the version of this data type. This may be null if there is only one handler for a given
     * type. If different handlers are required for different versions (e.g. GenePix GPR versions differ in the
     * QuantitationTypes that may be returned), then different versions should be registered that use the same name
     * (e.g. "Genepix, Version 3.0", "Genepix, Version 4.0", etc.).
     * 
     * @return the version.
     */
    String getVersion();
    
    /**
     * Returns the complete set of quantitation types that might be returned from a data file of this type.
     * 
     * @return the quantitation types.
     */
    List<QuantitationTypeDescriptor> getQuantitationTypes();
    
    /**
     * Indicates whether the <code>ArrayDataType</code> given is equivalent to this descriptor, matching
     * on name and version.
     * 
     * @param arrayDataType the type to check
     * @return true if equivalent to this descriptor, false otherwise.
     */
    boolean isEquivalent(ArrayDataType arrayDataType);
}
