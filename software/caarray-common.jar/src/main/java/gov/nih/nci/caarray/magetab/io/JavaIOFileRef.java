//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.io;

import java.io.File;

/**
 * Plain wrapper of a java file.
 * @author gax
 * @since 2.4.0
 */
public class JavaIOFileRef implements FileRef {

    private final File file;

    /**
     * wrapper ctor.
     * @param file file to wrap.
     */
    public JavaIOFileRef(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
        this.file = file;
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists() {
        return file.exists();
    }

    /**
     * {@inheritDoc}
     */
    public File getAsFile() {
        return file;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return file.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return file.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.file.equals(((JavaIOFileRef) obj).file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return file.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPartialFile() {
        return false;
    }
}
