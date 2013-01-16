//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.io;

import java.io.File;

/**
 * Interface for file wrapper, mostly to deffer the conversion of non-traditional files into {@link java.io.File}.
 * @author gax
 * @since 2.4.0
 */
public interface FileRef {
    /**
     * @return file name as it would appear in a magetab document entry.
     */
    String getName();

    /**
     * @return convert the reference into an actual java file.
     */
    File getAsFile();

    /**
     * @return true if this reference actually points to data.
     */
    boolean exists();
    
    /**
     * @return true if the referenced file is a partial file.
     */
    boolean isPartialFile();

}
