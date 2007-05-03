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
package gov.nih.nci.caarray.application.vocabulary;

import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Source;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.evs.domain.DescLogicConcept;
import gov.nih.nci.evs.domain.Property;
import gov.nih.nci.evs.domain.Vocabulary;
import gov.nih.nci.evs.query.EVSQuery;
import gov.nih.nci.evs.query.EVSQueryImpl;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is responsible for brokering searches between the caArray
 * Vocabulary Service and the caCore EVS.
 *
 * Where caArray uses termininology such as 'Category' and 'Term', EVS
 * uses 'Concept', 'SuperConcept' and 'SubConcept'.
 *
 * Communication to the EVS API is done through the caCore-implemented
 * Spring Http-Invoker.  We simply obtain a remote service object via the idiom:
 * 'http://cabio.nci.nih.gov/cacore32/http/remoteService' and invoke calls
 * on this object.
 *
 * @author John Pike
 *
 */
public class EVSUtility  {

    /**
     *This gets us a handle to the caCORE EVS API.
     * to-do:  The URL must be externalized.  It will change with each release.
     */
    private static final String APP_SERVICE_URL = "http://cabio.nci.nih.gov/cacore32/http/remoteService";
    private static final String MGED_VOCAB = "MGED_Ontology";
    private static final String MGED_INST = "mged_instance";
    private static final String PROP_CONCEPT_TYPE = "Concept_Type";
    private static final String PROP_DEFINITION = "DEFINITION";

    private static final int MAX_NUM_RESULTS = 10;
    /**
     * LOG used by this class.
     */
    private static final Log LOG = LogFactory.getLog(EVSUtility.class);

    /**
     * Creates a new instance.
     */
    public EVSUtility() {
        super();
    }


    /**
     * Returns all terms that belong to the category for the name given (including all subcategories). In EVS, these
     * categories are concepts, and items in a category are simply subconcepts for that concept.
     *
     * @param conceptName   find subconcepts for this concept.
     * @return          the matching Terms.
     * @throws VocabularyServiceException exc.
     */
     public List<Term> getConcepts(final String conceptName) throws VocabularyServiceException {

        final ApplicationService appService = getApplicationInstance();
        ArrayList<DescLogicConcept> subConcepts = new ArrayList<DescLogicConcept>();
        DescLogicConcept concept = new DescLogicConcept();
        List<Term> terms = new ArrayList<Term>();
        List<Vocabulary> vocab = new ArrayList<Vocabulary>();

        EVSQuery evs = new EVSQueryImpl();
        try {
            vocab = getVocabularyByName(evs, appService, MGED_VOCAB);
            concept = getEVSConcept(conceptName, evs, vocab, appService);

            if (concept != null) {
                if (conceptIsInstance(concept)) {
                    addConceptToTermList(null, concept, terms);
                }  else {
                    subConcepts = (ArrayList<DescLogicConcept>) getEVSConceptList(concept, vocab, appService);

                    int test = 0;
                    //set the category as the current concept...as we go deeper in the tree, the
                    //category will always be set to the parent concept
                    String categoryName = conceptName;
                    while (!subConcepts.isEmpty()) {
                        //the first time through, skip this part... obtain conceptInstances for the current concept
                        test++;
                        if (test > 1) {
                            List<DescLogicConcept> tempList = new ArrayList<DescLogicConcept>();
                            //let's always test the first element, and put the others in a tempList
                            tempList.addAll(subConcepts);
                            tempList.remove(0);
                            categoryName = subConcepts.get(0).getName();
                            subConcepts = (ArrayList<DescLogicConcept>) getEVSConceptList(
                                    subConcepts.get(0), vocab, appService);

                            //when the tempList is empty, we're done
                            if (!tempList.isEmpty()) {
                                subConcepts.addAll(tempList);
                            }
                        }
                        obtainConceptInstances(subConcepts, terms, categoryName);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw createVocabException(e);

        }
        return terms;
    }


    /**
     * @param e
     */
    private VocabularyServiceException createVocabException(Exception e) {
        VocabularyServiceException vse = new VocabularyServiceException(e);
        vse.setErrorCode(VocabularyServiceException.BAD_EVS_SVC_RESPONSE);
        return vse;
    }



    /**
     *  This method will loop through the list of subconcepts, and if any are of type
     *  mged_instance they will be converted to Terms and removed from the subconcept list.
     * @param subConcepts
     * @param terms
     * @param categoryName
     * @param iteratorList
     */
    private void obtainConceptInstances(
            List<DescLogicConcept> subConcepts, List<Term> terms, String categoryName) {
        DescLogicConcept subConcept;
        List<DescLogicConcept> iteratorList = new ArrayList<DescLogicConcept>(subConcepts);
        for (Iterator<DescLogicConcept> conceptIter = iteratorList.iterator(); conceptIter.hasNext();) {
            subConcept = conceptIter.next();
            if (conceptIsInstance(subConcept)) {
                addConceptToTermList(categoryName, subConcept, terms);
                subConcepts.remove(subConcept);
            } else {
                LOG.debug("This is not really a problem");
                 //concept is not a term, it stays on the list
                //just continue
            }
        }
    }


    /**
     * @param appService
     * @param evs
     * @param concept
     * @return
     * @throws ApplicationException
     */
    private List<String> getSubConceptNames(
            ApplicationService appService, EVSQuery evs, DescLogicConcept concept) throws ApplicationException {
        List<String> subConcepts;
        evs.getSubConcepts(
                  concept.getVocabulary().getName(), concept.getName(), Boolean.FALSE, Boolean.FALSE);
        subConcepts = (ArrayList<String>) appService.evsSearch(evs);
        return subConcepts;
    }


      /**
         * Given a concept name, searches all of the vocabularies and returns when it finds a matching concept in ANY
         * vocabulary.
         *
         * @param conceptName               conceptname
         * @param evs                       instantiated EVSQuery object
         * @param vocabs                    list of Vocabulary objects
         * @param appService                instantiated ApplicationService handle
         * @return <code>DescLogicConcept</code>    an EVS concept object
         * @throws ApplicationException     an exception
         */
    private DescLogicConcept getEVSConcept(final String conceptName, final EVSQuery evs, final List<Vocabulary> vocabs,
            final ApplicationService appService) throws ApplicationException {

        List<DescLogicConcept> concepts = new ArrayList<DescLogicConcept>();
        try {
            for (Vocabulary vocab : vocabs) {
                evs.searchDescLogicConcepts(vocab.getName(), conceptName, MAX_NUM_RESULTS);
                concepts = (ArrayList<DescLogicConcept>) appService.evsSearch(evs);
                // I found the concept
                if (!concepts.isEmpty()) {
                    break;
                }
            }
        } catch (ApplicationException e) {
            throw e;
        }
        if (concepts.isEmpty()) {
            return null;
        } else {
            return concepts.get(0);
        }
    }

     /**
         * Given a concept name, searches all of the vocabularies and returns ALL subconcepts from ALL found concepts.
         *
         * @param conceptName               name to search
         * @param evs                       instantiated EVSQuery object
         * @param vocabs                    list of Vocabulary objects
         * @param appService                instantiated ApplicationService handle
         * @return <code>List&lt;DescLogicConcept&gt;</code>      list of EVS Concept objects
         * @throws ApplicationException     an exception
         */
    private List<DescLogicConcept> getEVSConceptList(final DescLogicConcept concept,
            final List<Vocabulary> vocabs, final ApplicationService appService) throws ApplicationException {
        EVSQuery evs = null;
        List<DescLogicConcept> concepts = new ArrayList<DescLogicConcept>();
        List<String> subConceptList = this.getSubConceptNames(appService, evs, concept);
        try {
            for (int i = 0; i < subConceptList.size(); i++) {
                String subConcept = subConceptList.get(i);
                evs = new EVSQueryImpl();
                evs.searchDescLogicConcepts(vocabs.get(0).getName(), subConcept, MAX_NUM_RESULTS);
                concepts.addAll((ArrayList<DescLogicConcept>) appService.evsSearch(evs));
            }
        } catch (ApplicationException e) {
            throw new ApplicationException(e);
        }
        return concepts;
    }


    /**
     * This method gets all of the vocabularies from the EVS repository.
     * @param evs       instantiated EVSQuery object
     * @param appService       instantiated ApplicationService handle
     * @return          list of Vocabulary objects
     * @throws ApplicationException     an exception
     *
     */
    public List<Vocabulary> getVocabularyList(final EVSQuery evs, final ApplicationService appService)
        throws ApplicationException {

        evs.getAllVocabularies();
        return (ArrayList<Vocabulary>) appService.evsSearch(evs);
    }


    /**
     * @return appService
     */
    public ApplicationService getApplicationInstance() {
        return ApplicationService.getRemoteInstance(APP_SERVICE_URL);
    }


    /**
     * This method gets a specific vocabulary from the EVS repository.
     * @param evs       instantiated EVSQuery object
     * @param appService       instantiated ApplicationService handle
     * @return          list of vocab objects
     * @throws ApplicationException     an exception
     *
     */
    private List<Vocabulary> getVocabularyByName(
            final EVSQuery evs, final ApplicationService appService, final String name)
        throws ApplicationException {

        evs.getVocabularyByName(name);
        return (ArrayList<Vocabulary>) appService.evsSearch(evs);
    }

    /**
     * This method determines whether the given concept is of CONCEPT_TYPE="mged_instance".  These
     * concept types represent real Terms, in the caArray sense.
     * @param evs       instantiated EVSQuery object
     * @param appService       instantiated ApplicationService handle
     * @return          list of Vocabulary objects
     * @throws ApplicationException     an exception
     *
     */
    private boolean conceptIsInstance(DescLogicConcept concept) {
        List list = concept.getPropertyCollection();
        for (int i = 0; i < list.size(); i++) {
            Property p = (Property) list.get(i);

            //if the concept is of type MGED_INSTANCE
            if (p.getName().equals(PROP_CONCEPT_TYPE)) {
                if (p.getValue().equals(MGED_INST)) {
                    return true;
                }
            }
        }
        return false;
    }


    /** This will iterate the Concept's vector of properties and
     *  return the value for the matching name.
     * @param concept
     * @param name
     * @return
     */
    private String getConceptPropertyValue(DescLogicConcept concept, String name) {
        String defValue = null;
        List list = concept.getPropertyCollection();
        for (int i = 0; i < list.size(); i++) {
            Property p = (Property) list.get(i);
            if (p.getName().equals(name)) {
                defValue = p.getValue();
            }
        }
        return defValue;
    }

    /** This method creates a term for the given concept and category, and adds it to the Term list,
     *  in essence convering EVS Concepts to caArray Terms.
     * @param conceptName
     * @param concept
     * @param terms
     */
    private void addConceptToTermList(String categoryName, DescLogicConcept concept, List<Term> terms) {
        Source aSource = new Source();
        List<Term> children = new ArrayList<Term>();
        aSource.setName(MGED_VOCAB);
        aSource.setUrl(APP_SERVICE_URL);
        Category aCategory = new Category();
        aCategory.setName(categoryName);
        Term aTerm = new Term();
        aTerm.setValue(concept.getName());
        aTerm.setDescription(getConceptPropertyValue(concept, EVSUtility.PROP_DEFINITION));
        aTerm.setCategory(aCategory);
        aTerm.setSource(aSource);

        children.add(aTerm);
        aCategory.setChildren(children);
        terms.add(aTerm);
    }

}
