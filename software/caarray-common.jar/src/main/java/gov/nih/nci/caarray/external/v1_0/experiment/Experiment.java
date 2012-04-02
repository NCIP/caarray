/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and 
 * have distributed to and by third parties the caarray-common-jar Software and any 
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
package gov.nih.nci.caarray.external.v1_0.experiment;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.factor.Factor;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Experiment represents a microarray experiment.
 * 
 * @author dkokotov
 */
public class Experiment extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;
    
    private String publicIdentifier;
    private String title;
    private String description;
    private Organism organism;
    private Set<ExperimentalContact> contacts = new HashSet<ExperimentalContact>();
    private Set<Term> experimentalDesigns = new HashSet<Term>();
    private Set<Term> replicateTypes = new HashSet<Term>();
    private Set<Term> normalizationTypes = new HashSet<Term>();
    private Set<Term> qualityControlTypes = new HashSet<Term>();
    private ArrayProvider arrayProvider;
    private Set<AssayType> assayTypes = new HashSet<AssayType>();
    private Set<Factor> factors = new HashSet<Factor>();
    private Set<ArrayDesign> arrayDesigns = new HashSet<ArrayDesign>();
    private Date lastDataModificationDate;

    /**
     * @return the public identifier for this experiment. This is a human readable permanent identifier for this
     *         experiment that can be used in publications to identify it.
     */
    public String getPublicIdentifier() {
        return publicIdentifier;
    }

    /**
     * @param publicIdentifier the public identifier for this experiment. This is a human readable permanent identifier
     *            for this experiment that can be used in publications to identify it.
     */
    public void setPublicIdentifier(String publicIdentifier) {
        this.publicIdentifier = publicIdentifier;
    }

    /**
     * @return the title of the experiment
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the long-form description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the organism from which the biomaterials in the experiment are drawn
     */
    public Organism getOrganism() {
        return organism;
    }

    /**
     * @param organism the organism to set
     */
    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    /**
     * @return a set of ExperimentContacts corresponding to the people and organizations involved with the experiment.
     */
    public Set<ExperimentalContact> getContacts() {
        return contacts;
    }

    /**
     * @param contacts the contacts to set
     */
    public void setContacts(Set<ExperimentalContact> contacts) {
        this.contacts = contacts;
    }

    /**
     * @return the set of MGED ontology terms defining the experimental design of this experiment.
     */
    public Set<Term> getExperimentalDesigns() {
        return experimentalDesigns;
    }

    /**
     * @param experimentalDesigns the experimentalDesigns to set
     */
    public void setExperimentalDesigns(Set<Term> experimentalDesigns) {
        this.experimentalDesigns = experimentalDesigns;
    }

    /**
     * @return the set of MGED ontology terms defining the replication strategy of this experiment.
     */
    public Set<Term> getReplicateTypes() {
        return replicateTypes;
    }

    /**
     * @param replicateTypes the replicateTypes to set
     */
    public void setReplicateTypes(Set<Term> replicateTypes) {
        this.replicateTypes = replicateTypes;
    }

    /**
     * @return the set of MGED ontology terms defining the normalization strategy of this experiment.
     */
    public Set<Term> getNormalizationTypes() {
        return normalizationTypes;
    }

    /**
     * @param normalizationTypes the normalizationTypes to set
     */
    public void setNormalizationTypes(Set<Term> normalizationTypes) {
        this.normalizationTypes = normalizationTypes;
    }

    /**
     * @return the provider of arrays used in this experiment.
     */
    public ArrayProvider getArrayProvider() {
        return arrayProvider;
    }

    /**
     * @param arrayProvider array provider to set
     */
    public void setArrayProvider(ArrayProvider arrayProvider) {
        this.arrayProvider = arrayProvider;
    }

    /**
     * @return the set of assay types associated with this experiment.
     */
    public Set<AssayType> getAssayTypes() {
        return assayTypes;
    }

    /**
     * @param assayTypes the assayTypes to set
     */
    public void setAssayTypes(Set<AssayType> assayTypes) {
        this.assayTypes = assayTypes;
    }

    /**
     * @return the set of experimental factors in this experiment.
     */
    public Set<Factor> getFactors() {
        return factors;
    }

    /**
     * @param factors the factors to set
     */
    public void setFactors(Set<Factor> factors) {
        this.factors = factors;
    }

    /**
     * @return the set of MGED ontology terms defining the quality control strategy of this experiment.
     */
    public Set<Term> getQualityControlTypes() {
        return qualityControlTypes;
    }

    /**
     * @param qualityControlTypes the qualityControlTypes to set
     */
    public void setQualityControlTypes(Set<Term> qualityControlTypes) {
        this.qualityControlTypes = qualityControlTypes;
    }
    
    /**
     * @return the set of array designs used in this experiment.
     */
    public Set<ArrayDesign> getArrayDesigns() {
        return arrayDesigns;
    }

    /**
     * @param arrayDesigns the arrayDesigns to set
     */
    public void setArrayDesigns(Set<ArrayDesign> arrayDesigns) {
        this.arrayDesigns = arrayDesigns;
    }

    /**
     * @return the date when the data of this experiment was last modified.
     */
    public Date getLastDataModificationDate() {
        return this.lastDataModificationDate;
    }

    /**
     * @param lastDataModificationDate the lastDataModificationDate to set.
     */
    public void setLastDataModificationDate(final Date lastDataModificationDate) {
        this.lastDataModificationDate = lastDataModificationDate;
    }

}
