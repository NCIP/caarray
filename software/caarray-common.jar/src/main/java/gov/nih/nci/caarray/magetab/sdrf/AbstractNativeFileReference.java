//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.data.NativeDataFile;

/**
 * Base class for nodes that represent a native format file.
 */
public abstract class AbstractNativeFileReference extends AbstractSampleDataRelationshipNode {
    
    private NativeDataFile nativeDataFile;

    /**
     * @return the nativeDataFile
     */
    public final NativeDataFile getNativeDataFile() {
        return nativeDataFile;
    }

    /**
     * @param nativeDataFile the nativeDataFile to set
     */
    public final void setNativeDataFile(NativeDataFile nativeDataFile) {
        this.nativeDataFile = nativeDataFile;
    }

}
