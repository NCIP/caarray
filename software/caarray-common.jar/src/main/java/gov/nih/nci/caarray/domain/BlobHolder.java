//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * @author Scott Miller
 * 
 */
@Entity
public class BlobHolder implements PersistentObject {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Blob contents;

    /**
     * Returns the id.
     * 
     * @return the id
     */
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the id.
     * 
     * @param id the id to set
     * @deprecated should only be used by castor and hibernate
     */
    @Deprecated
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the contents
     */
    @Column(columnDefinition = "LONGBLOB", updatable = false, name = "contents")
    public Blob getContents() {
        return this.contents;
    }

    /**
     * @param contents the contents to set
     */
    public void setContents(Blob contents) {
        this.contents = contents;
    }
}
