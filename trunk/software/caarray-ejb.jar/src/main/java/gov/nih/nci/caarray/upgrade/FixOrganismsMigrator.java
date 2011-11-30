/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray2-trunk
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caarray2-trunk Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caarray2-trunk Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray2-trunk Software; (ii) distribute and 
 * have distributed to and by third parties the caarray2-trunk Software and any 
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
package gov.nih.nci.caarray.upgrade;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.UnhandledException;

/**
 * @author dharley
 *
 */
public class FixOrganismsMigrator extends AbstractHibernateBasedCustomChange {
    
    private TermSource ncbiTermSource;
    private List<Organism> allOrganismsList;

    /**
     * {@inheritDoc}
     */
    public void doHibernateExecute(final SingleConnectionHibernateHelper hibernateHelper) {
        try {
            List<Organism> allOrganisms = getAllOrganisms(hibernateHelper);
            Map<String, List<Organism>> nameToOrganismsListMap = buildNamesToOrganismsListMap(allOrganisms);
            for (List<Organism> organismsWithSameNameList : nameToOrganismsListMap.values()) {
                if (organismsWithSameNameList.size() > 1) {
                    processOrganismsWithSameName(hibernateHelper, organismsWithSameNameList.get(0).getScientificName(),
                            organismsWithSameNameList);
                } else if (organismsWithSameNameList.size() == 1) {
                    Organism organism = organismsWithSameNameList.get(0);
                    if (null != organism.getTermSource()
                            && !ExperimentOntology.NCBI.getOntologyName().equals(organism.getTermSource().getName())) {
                        organism.setTermSource(getNcbiTermSource(hibernateHelper));
                    }
                }
            }
          } catch (Exception exception) {
              throw new UnhandledException("Cannot fix the Organisms in DB.", exception);
          }
    }
    
    private Map<String, List<Organism>> buildNamesToOrganismsListMap(final List<Organism>allOrganisms) {
        Map<String, List<Organism>> nameToOrganismsListMap = new HashMap<String, List<Organism>>();
        for (Organism organism : allOrganisms) {
            List<Organism> organismsWithThisName =
                nameToOrganismsListMap.get(organism.getScientificName().toUpperCase());
            if (null == organismsWithThisName) {
                organismsWithThisName = new ArrayList<Organism>();
                nameToOrganismsListMap.put(organism.getScientificName().toUpperCase(), organismsWithThisName);
            }
            organismsWithThisName.add(organism);
        }
        return nameToOrganismsListMap;
    }
    
    private TermSource getNcbiTermSource(final SingleConnectionHibernateHelper hibernateHelper) {
        if (null == ncbiTermSource) {
            ncbiTermSource = (TermSource) hibernateHelper.getCurrentSession().createQuery("FROM "
                    + TermSource.class.getName() + " as termSource WHERE termSource.name = '"
                    + ExperimentOntology.NCBI.getOntologyName() + "'").list().get(0); //there has to be at least one
        }
        return ncbiTermSource;
    }
    
    @SuppressWarnings({"PMD.UnusedNullCheckInEquals" })
    private Organism getNcbiSourcedOrganism(List<Organism> organismsList, final boolean removeFromList) {
        Organism ncbiOrganism = null;
        for (Organism organism : organismsList) {
            if (null != organism.getTermSource()
                    && ExperimentOntology.NCBI.getOntologyName().equals(organism.getTermSource().getName())) {
                ncbiOrganism = organism;
                if (removeFromList) {
                    organismsList.remove(organism);
                }
                break;
            }
        }
        return ncbiOrganism;
    }
    
    private void processOrganismsWithSameName(final SingleConnectionHibernateHelper hibernateHelper,
            final String nameOfOrganisms, final List<Organism> organismsWithSameNameList) {
        // the NCBI term sourced organism will be removed from the list, so the list can be deleted later
        Organism ncbiSourcedOrganismWithThisName = getNcbiSourcedOrganism(organismsWithSameNameList, true);
        if (null == ncbiSourcedOrganismWithThisName) {
            ncbiSourcedOrganismWithThisName = new Organism();
            ncbiSourcedOrganismWithThisName.setScientificName(nameOfOrganisms);
            ncbiSourcedOrganismWithThisName.setTermSource(ncbiTermSource);
            hibernateHelper.getCurrentSession().save(ncbiSourcedOrganismWithThisName);
        }
        for (Organism organism : organismsWithSameNameList) {
            fixAllExperiments(hibernateHelper, organism, ncbiSourcedOrganismWithThisName);
            fixAllArrayDesigns(hibernateHelper, organism, ncbiSourcedOrganismWithThisName);
            fixAllBiomaterials(hibernateHelper, organism, ncbiSourcedOrganismWithThisName);
            // the NCBI term sourced organism has already been removed from the list, so it is safe to delete
            hibernateHelper.getCurrentSession().delete(organism);
        }
    }
    
    private void fixAllExperiments(final SingleConnectionHibernateHelper hibernateHelper,
            final Organism organismToReplace, final Organism ncbiSourcedOrganism) {
        List<Experiment> allExperimentsWithBadOrganism = getAllExperimentsForOrganism(hibernateHelper,
                organismToReplace);
        for (Experiment experiment : allExperimentsWithBadOrganism) {
            experiment.setOrganism(ncbiSourcedOrganism);
            hibernateHelper.getCurrentSession().save(experiment);
        }
    }
    
    private void fixAllArrayDesigns(final SingleConnectionHibernateHelper hibernateHelper,
            final Organism organismToReplace, final Organism ncbiSourcedOrganism) {
        List<ArrayDesign> allArrayDesignsWithBadOrganism = getAllArrayDesignsForOrganism(hibernateHelper,
                organismToReplace);
        for (ArrayDesign arrayDesign : allArrayDesignsWithBadOrganism) {
            arrayDesign.setOrganism(ncbiSourcedOrganism);
            hibernateHelper.getCurrentSession().save(arrayDesign);
        }
    }
    
    private void fixAllBiomaterials(final SingleConnectionHibernateHelper hibernateHelper,
            final Organism organismToReplace, final Organism ncbiSourcedOrganism) {
        List<AbstractBioMaterial> allAbstractBioMaterialsWithBadOrganism = getAllAbstractBioMaterialsForOrganism(
                hibernateHelper, organismToReplace);
        for (AbstractBioMaterial biomaterial : allAbstractBioMaterialsWithBadOrganism) {
            biomaterial.setOrganism(ncbiSourcedOrganism);
            hibernateHelper.getCurrentSession().save(biomaterial);
        }
    }
    
    private List<Experiment> getAllExperimentsForOrganism(final SingleConnectionHibernateHelper hibernateHelper,
            final Organism targetOrganism) {
        return hibernateHelper.getCurrentSession().createQuery("SELECT experiment FROM "
                + Experiment.class.getName() + " experiment WHERE experiment.organism = :organism").
                setEntity("organism", targetOrganism).list();
    }
    
    private List<ArrayDesign> getAllArrayDesignsForOrganism(final SingleConnectionHibernateHelper hibernateHelper,
            final Organism targetOrganism) {
        return hibernateHelper.getCurrentSession().createQuery("SELECT arrayDesign FROM "
                + ArrayDesign.class.getName() + " arrayDesign WHERE arrayDesign.organism = :organism").
                setEntity("organism", targetOrganism).list();
    }
    
    private List<AbstractBioMaterial> getAllAbstractBioMaterialsForOrganism(
            final SingleConnectionHibernateHelper hibernateHelper, final Organism targetOrganism) {
        return hibernateHelper.getCurrentSession().createQuery("SELECT biomaterial FROM "
                + AbstractBioMaterial.class.getName() + " biomaterial WHERE biomaterial.organism = :organism").
                setEntity("organism", targetOrganism).list();
    }
    
    private List<Organism> getAllOrganisms(final SingleConnectionHibernateHelper hibernateHelper) {
        if (null == allOrganismsList) {
            allOrganismsList = hibernateHelper.getCurrentSession().createQuery("FROM "
                    + Organism.class.getName()).list();
        }        
        return allOrganismsList;
    }
    
}
