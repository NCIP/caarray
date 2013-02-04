//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.array;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.Length;

/**
 * A hereditary unit consisting of a sequence of DNA that occupies a specific location on a chromosome and 
 * determines a particular characteristic in an organism.  The functional and physical unit of heredity passed 
 * from parent to offspring.
 */
@Entity
@BatchSize(size = AbstractProbeAnnotation.RELATED_BATCH_SIZE)
@SuppressWarnings("PMD.CyclomaticComplexity") // Cascading if-then-else
public class Gene extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;

    private String fullName;
    private String symbol;
    private String genbankAccession;
    private String genbankAccessionVersion;
    private String ensemblgeneID;
    private String unigeneclusterID;
    private String entrezgeneID;
    private String refseqAccession;
    private String thcAccession;
    private Set<Accession> additonalAccessions = new HashSet<Accession>();

    /**
     * Name of the UniGene database.
     */
    public static final String UNIGENE = "UniGene";

    /**
     * Name of the GenBank database.
     */
    public static final String GENBANK = "GenBank";

    /**
     * Name of the Entrez Gene database.
     */
    public static final String ENTREZ_GENE = "Entrez Gene";

    /**
     * Name of the Ensemble database.
     */
    public static final String ENSEMBLE = "Ensemble";

    /**
     * Name of the RefSeq database.
     */
    public static final String REF_SEQ = "RefSeq";

    /**
     * Name of the THC database.
     */
    public static final String THC = "THC";

    /**
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }
    
    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    /**
     * @return the fullName
     */
    @Length(max = LARGE_TEXT_FIELD_LENGTH)
    public String getFullName() {
        return fullName;
    }
    
    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the ensemblgeneID
     */
    public String getEnsemblgeneID() {
        return ensemblgeneID;
    }

    /**
     * @param ensemblgeneID the ensemblgeneID to set
     */
    public void setEnsemblgeneID(String ensemblgeneID) {
        this.ensemblgeneID = ensemblgeneID;
    }

    /**
     * @return the entrezgeneID
     */
    public String getEntrezgeneID() {
        return entrezgeneID;
    }

    /**
     * @param entrezgeneID the entrezgeneID to set
     */
    public void setEntrezgeneID(String entrezgeneID) {
        this.entrezgeneID = entrezgeneID;
    }

    /**
     * @return the genbankAccession
     */
    public String getGenbankAccession() {
        return genbankAccession;
    }

    /**
     * @param genbankAccession the genbankAccession to set
     */
    public void setGenbankAccession(String genbankAccession) {
        this.genbankAccession = genbankAccession;
    }

    /**
     * @return the genbankAccessionVersion
     */
    public String getGenbankAccessionVersion() {
        return genbankAccessionVersion;
    }

    /**
     * @param genbankAccessionVersion the genbankAccessionVersion to set
     */
    public void setGenbankAccessionVersion(String genbankAccessionVersion) {
        this.genbankAccessionVersion = genbankAccessionVersion;
    }

    /**
     * @return the unigeneclusterID
     */
    public String getUnigeneclusterID() {
        return unigeneclusterID;
    }

    /**
     * @param unigeneclusterID the unigeneclusterID to set
     */
    public void setUnigeneclusterID(String unigeneclusterID) {
        this.unigeneclusterID = unigeneclusterID;
    }
    
    /**
     * @return the refseqAccession
     */
    public String getRefseqAccession() {
        return refseqAccession;
    }

    /**
     * @param refseqAccession the refseqAccession to set
     */
    public void setRefseqAccession(String refseqAccession) {
        this.refseqAccession = refseqAccession;
    }
        
    /**
     * @return the thcAccession
     */
    public String getThcAccession() {
        return thcAccession;
    }

    /**
     * @param thcAccession the thcAccession to set
     */
    public void setThcAccession(String thcAccession) {
        this.thcAccession = thcAccession;
    }
    
   /**
     * @return the additional accessions
     * @deprecated should only be used by castor and hibernate.
     */
     @org.hibernate.annotations.CollectionOfElements
     @JoinTable(name = "accession", joinColumns = @JoinColumn(name = "gene_id"))
     @Column(updatable = false)
     private Set<Accession> getAdditionalAccessions() {
         return additonalAccessions;
     }

     /**
      * @param accessions the accessions to set
      */
     @SuppressWarnings("unused")
     private void setAdditionalAccessions(Set<Accession> additionalAccessions) {
         this.additonalAccessions = additionalAccessions;
     }
     
     /**
      * Add an accession to this gene.
      * @param databaseName the name of the public database.
      * @param accessionNumber the accession number to add.
      */
     @SuppressWarnings("PMD.CyclomaticComplexity")
     public void addAccessionNumber(String databaseName, String accessionNumber) {
         if (UNIGENE.equals(databaseName) && null == unigeneclusterID) {
             unigeneclusterID = accessionNumber;
         } else if (GENBANK.equals(databaseName) && null == genbankAccession) {
             genbankAccession = accessionNumber;
         } else if (ENTREZ_GENE.equals(databaseName) && null == entrezgeneID) {
             entrezgeneID = accessionNumber;
         } else if (ENSEMBLE.equals(databaseName) && null == ensemblgeneID) {
             ensemblgeneID = accessionNumber;
         } else if (REF_SEQ.equals(databaseName) && null == refseqAccession) {
             refseqAccession = accessionNumber;
         } else if (THC.equals(databaseName) && null == thcAccession) {
             thcAccession = accessionNumber;
         } else {
             additonalAccessions.add(new Accession(databaseName, accessionNumber));
         }
     }
     
     /**
      * Get all the accessions associated with this gene in the given public database.
      * @param databaseName the name of the public database.
      * @return a list of accessions.
      */
     @SuppressWarnings("deprecation")
     @Transient
     public List<String> getAccessionNumbers(String databaseName) {
         List<String> accessionList = new ArrayList<String>();
         
         String primaryAccessionNumber = getPrimaryAccessionNumber(databaseName);
         if (null != primaryAccessionNumber) {
             accessionList.add(primaryAccessionNumber);
         }
         
         for (Accession accession : getAdditionalAccessions()) {
             if (accession.getDatabaseName().equals(databaseName)) {
                 accessionList.add(accession.getAccessionNumber());
             }
         }
                
         return accessionList;     
     }
     
     private String getPrimaryAccessionNumber(String databaseName) {
        if (UNIGENE.equals(databaseName)) {
            return unigeneclusterID;
        } else if (GENBANK.equals(databaseName)) {
            return genbankAccession;
        } else if (ENTREZ_GENE.equals(databaseName)) {
            return entrezgeneID;
        } else if (ENSEMBLE.equals(databaseName)) {
            return ensemblgeneID;
        } else if (REF_SEQ.equals(databaseName)) {
            return refseqAccession;
        } else if (THC.equals(databaseName)) {
            return thcAccession;
        } else {
            return null;
        }
     }
}
