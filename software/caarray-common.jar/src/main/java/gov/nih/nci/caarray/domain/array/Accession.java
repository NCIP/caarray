//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Uniquely identifies a gene relative to a public sequence database.
 */
@Embeddable
public class Accession implements Serializable {
    private static final long serialVersionUID = 1L;

    private String databaseName;
    private String accessionNumber;

    /**
     * default ctor for castor.
     * @deprecated public for castor and hibernate only.
     */
    @Deprecated
    public Accession() {
        // For castor and hibernate
    }
    
    /**
     * @param databaseName the public sequence database
     * @param accessionNumber the identifier of the gene in the database
     */
    public Accession(String databaseName, String accessionNumber) {
        if (StringUtils.isEmpty(databaseName)) {
            throw new IllegalArgumentException("databaseName must not be empty");
        }
        
        if (StringUtils.isEmpty(accessionNumber)) {
            throw new IllegalArgumentException("accessionNumber must not be empty");
        }
        
        this.databaseName = databaseName;
        this.accessionNumber = accessionNumber;
   }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int seed1 = 2323;
        final int seed2 = 1061;
        
        return new HashCodeBuilder(seed1, seed2)
        .append(accessionNumber)
        .append(databaseName)
        .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof Accession)) {
            return false;
        }
        
        Accession other = (Accession) obj;
        return new EqualsBuilder()
        .append(accessionNumber, other.accessionNumber)
        .append(databaseName, other.databaseName)
        .isEquals();
    }

   /**
    * @return the databaseName
    * @deprecated should only be used by castor and hibernate.
    */
    @Deprecated
    @Column(nullable = false, updatable = false)
    public String getDatabaseName() {
        return databaseName;
    }
    
   /**
    * @param databaseName the databaseName to set
    * @deprecated should only be used by castor and hibernate.
    */
    @Deprecated
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * @return the accessionNumber
     * * @deprecated should only be used by castor and hibernate.
     */
    @Deprecated
    @Column(nullable = false, updatable = false)
    public String getAccessionNumber() {
         return accessionNumber;
    }
    
   /**
    * @param accessionNumber the accessionNumber to set
    * @deprecated should only be used by castor and hibernate.
    */
    @Deprecated
    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }
}
