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

import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.evs.domain.DescLogicConcept;
import gov.nih.nci.evs.domain.Vocabulary;
import gov.nih.nci.evs.query.EVSQuery;
import gov.nih.nci.evs.query.EVSQueryImpl;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
public final class EVSUtility  {

    /**
     *This gets us a handle to the caCORE EVS API.
     * to-do:  The URL must be externalized.  It will change with each release.
     */
    private static final String APP_SERVICE_URL = "http://cabio.nci.nih.gov/cacore32/http/remoteService";

    /**
     *This is used as a limiter to the number of EVS matches that we wish to obtain.
     */
    private static final int MAX_NUM_RESULTS = 10;

    /**
     * Creates a new instance.
     */
    public EVSUtility() {
        super();
    }


    /**
     * Returns all terms that belong to the category for the name given (including all subcategories). In EVS, these
     * categories are concepts, and items in a category are simply subconcepts for that concept. NOTE: This method will
     * only return all of the concepts for the FIRST matching term that is found...that is, as soon as a conceptName is
     * found in ANY vocabulary, all matching subconcepts for THAT term are returned...
     *
     * @param conceptName   find subconcepts for this concept.
     * @return          the matching Terms.
     */
     public List<Term> getConcepts(final String conceptName) {

        final ApplicationService appService = ApplicationService.getRemoteInstance(APP_SERVICE_URL);
        List<String> subConcepts = new ArrayList<String>();
        DescLogicConcept concept = new DescLogicConcept();
        List<Term> terms = new ArrayList<Term>();
        try {
            EVSQuery evs = new EVSQueryImpl();
            List<Vocabulary> allVocabs = getVocabularyList(evs, appService);
            concept = getEVSConcept(conceptName, evs, allVocabs, appService);

            if (concept == null) {
                throw new ApplicationException();
                // throw new NoMatchingTermException();??
                // return some error/empty list, etc??
            } else {
                evs = new EVSQueryImpl();
                evs.getSubConcepts(concept.getVocabulary().getName(), concept.getName(), Boolean.FALSE, Boolean.FALSE);

                subConcepts = (ArrayList<String>) appService.evsSearch(evs);
                conceptsToTerms(subConcepts, terms);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return terms;
    }


     /**
         * Returns all terms that belong to the category for the name given (including all subcategories). In EVS, these
         * categories are concepts, and items in a category are simply subconcepts for that concept. NOTE: This method
         * will return ALL of the concepts from ALL EVS vocabularies
         *
         * @param conceptName       find subconcepts for this concept.
         * @return                  the matching Terms.
         */
      public List<Term> getAllConcepts(final String conceptName) {

          ApplicationService appService = ApplicationService.getRemoteInstance(APP_SERVICE_URL);
          List <String> subConcepts = new ArrayList<String>();
          List <DescLogicConcept> concepts = new ArrayList<DescLogicConcept>();
          List <Term> terms = new ArrayList<Term>();
          try {
              EVSQuery evs = new EVSQueryImpl();
              List<Vocabulary> allVocabs = getVocabularyList(evs, appService);
              concepts = getEVSConceptList(conceptName, evs, allVocabs, appService);

              if (concepts.isEmpty()) {
                  throw new ApplicationException();
                // throw new NoMatchingTermException();??
                // return some error/empty list, etc??
            } else {
                  DescLogicConcept concept = null;
                  evs = new EVSQueryImpl();
                  for (Iterator<DescLogicConcept> i = concepts.iterator(); i.hasNext();) {
                      concept = i.next();
                      evs.getSubConcepts(
                              concept.getVocabulary().getName(), concept.getName(), Boolean.FALSE, Boolean.FALSE);
                      subConcepts = (ArrayList<String>) appService.evsSearch(evs);
                      conceptsToTerms(subConcepts, terms);
                  }
              }

          } catch (Exception e) {
              e.printStackTrace();
          }
          return terms;
     }


    /**
     * This method converts EVS Concepts to caArray <code>Term</code> objects.
     *
     * @param subConcepts       list of EVS Concepts
     * @param terms             list of caArray Terms
     */
    private void conceptsToTerms(final List<String> subConcepts, final List<Term> terms) {
        String subConcept;
        for (Iterator<String> i = subConcepts.iterator(); i.hasNext();) {
            subConcept = i.next();
            Term newTerm = new Term();
            newTerm.setValue(subConcept);
            //newTerm.setCategory(concept);
            terms.add(newTerm);
        }
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
            for (Iterator<Vocabulary> i = vocabs.iterator(); i.hasNext();) {
                Vocabulary vocab = i.next();
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
    private List<DescLogicConcept> getEVSConceptList(final String conceptName, final EVSQuery evs,
            final List<Vocabulary> vocabs, final ApplicationService appService) throws ApplicationException {

        List<DescLogicConcept> concepts = new ArrayList<DescLogicConcept>();

        try {
            for (int i = 0; i < vocabs.size(); i++) {
                Vocabulary vocab = vocabs.get(i);
                evs.searchDescLogicConcepts(vocab.getName(), conceptName, MAX_NUM_RESULTS);
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
    private List<Vocabulary> getVocabularyList(final EVSQuery evs, final ApplicationService appService)
        throws ApplicationException {

        evs.getAllVocabularies();
        return (ArrayList<Vocabulary>) appService.evsSearch(evs);
    }



}
