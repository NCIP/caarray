//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.array;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Transient;

/**
 * @author jscott
 *
 */
@Entity
@DiscriminatorValue("MIRNA")
public class MiRNAProbeAnnotation extends AbstractProbeAnnotation {
    private static final long serialVersionUID = 1L;
    private Set<Accession> accessions = new HashSet<Accession>();

    /**
     * Name of the miRNA database.
     */
    public static final String MIR = "mir";


    /**
     * @deprecated public scope for Castor serialization only.
     * @return the accessions
     */
    @org.hibernate.annotations.CollectionOfElements
    @JoinTable(name = "mirna_accession", joinColumns = @JoinColumn(name = "annotation_id"))
    @Column(updatable = false)
    @Deprecated
    private Set<Accession> getAccessions() {
        return accessions;
    }

    /**
     * @param accessions the accessions to set
     */
    @SuppressWarnings("unused")
    private void setAccessions(Set<Accession> accessions) {
        this.accessions = accessions;
    }
    
    /**
     * Add an accession to this gene.
     * @param databaseName the name of the public database.
     * @param accessionNumber the accession number to add.
     */
    public void addAccessionNumber(String databaseName, String accessionNumber) {
        accessions.add(new Accession(databaseName, accessionNumber));
    }
    
    /**
     * Get all the accessions associated with this gene in the given public database.
     * @param databaseName the name of the public database.
     * @return a list of accessions.
     */
    @Transient
    public List<String> getAccessionNumbers(String databaseName) {
        List<String> accessionList = new ArrayList<String>();
        
        for (Accession accession : getAccessions()) {
            if (accession.getDatabaseName().equals(databaseName)) {
                accessionList.add(accession.getAccessionNumber());
            }
        }
               
        return accessionList;     
    }
}

