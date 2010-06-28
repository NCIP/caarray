/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
