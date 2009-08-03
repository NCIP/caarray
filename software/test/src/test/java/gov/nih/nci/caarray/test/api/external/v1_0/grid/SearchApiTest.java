/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The test
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This test Software License (the License) is between NCI and You. You (or
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
 * its rights in the test Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the test Software; (ii) distribute and
 * have distributed to and by third parties the test Software and any
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
package gov.nih.nci.caarray.test.api.external.v1_0.grid;

 import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;
import gov.nih.nci.caarray.external.v1_0.data.DataType;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileCategory;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.ExperimentalContact;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import java.rmi.RemoteException;
import static org.junit.Assert.*;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;


import gov.nih.nci.caarray.external.v1_0.factor.Factor;
import gov.nih.nci.caarray.external.v1_0.factor.FactorValue;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationCriterion;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationColumn;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationSet;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.external.v1_0.sample.Characteristic;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.value.AbstractValue;
import gov.nih.nci.caarray.external.v1_0.value.TermValue;
import gov.nih.nci.caarray.external.v1_0.value.UserDefinedValue;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.UnsupportedCategoryException;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InvalidReferenceFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.UnsupportedCategoryFault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.apache.axis.AxisFault;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

/**
 * A client downloading array design details through the CaArray Grid gridClient.
 *
 * @author Rashmi Srinivasa
 * @author gax
 */
public class SearchApiTest extends AbstractExternalGridApiTest {


    @Test
    public void testGetAllPrincipalInvestigators() throws RemoteException {
        logForSilverCompatibility(TEST_NAME, "testGetAllPrincipalInvestigators");
        Person[] allPis = gridClient.getAllPrincipalInvestigators();
        assertEquals(3, allPis.length);

        logForSilverCompatibility(TEST_OUTPUT, "test Person transmission");
        Person p = null;
        for (Person x : allPis) {
            if ("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Person:10".equals(x.getId())) {
                p = x;
                break;
            }
        }
        assertNotNull(p);
        assertEquals("EmailAddress", "JEGreen@nih.gov", p.getEmailAddress());
        assertEquals("FirstName", "Jeffrey", p.getFirstName());
        assertEquals("LastName", "Green", p.getLastName());
        assertEquals("MiddleInitials", "E", p.getMiddleInitials());
    }

    /////////////////////////////////////////////////////////////

    @Test(expected = IncorrectEntityTypeFault.class)
    public void testGetAllCharacteristicCategories_NonEx() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testGetAllCharacteristicCategories_NonEx");
        String qtid = "URN:LSID:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:1";
        gridClient.getAllCharacteristicCategories(new CaArrayEntityReference(qtid));
    }

    @Test(expected = NoEntityMatchingReferenceFault.class)
    public void testGetAllCharacteristicCategories_Bad() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testGetAllCharacteristicCategories_Bad");
        String qtid = "foo";
        gridClient.getAllCharacteristicCategories(new CaArrayEntityReference(qtid));
    }

    @Test
    public void testGetAllCharacteristicCategories_All() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testGetAllCharacteristicCategories_All");
        Category[] l = gridClient.getAllCharacteristicCategories(null);
        assertEquals(9, l.length);
    }

    @Test
    public void testGetAllCharacteristicCategories_Ex() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testGetAllCharacteristicCategories_Ex");
        String type = gov.nih.nci.caarray.external.v1_0.experiment.Experiment.class.getName();
        String eid = "URN:LSID:" + type + ":1";
        CaArrayEntityReference ref = new CaArrayEntityReference(eid);
        Category[] l = gridClient.getAllCharacteristicCategories(ref);
        assertEquals(9, l.length);
    }

    /////////////////////////////////////////////////////

    @Test(expected = IncorrectEntityTypeFault.class)
    public void testGetTermsForCategory_NonCategory() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testGetTermsForCategory_NonCategory");
        String qtid = "URN:LSID:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:1";
        gridClient.getTermsForCategory(new CaArrayEntityReference(qtid), null);
    }

    @Test
    public void testGetTermsForCategory_Null() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testGetTermsForCategory_Null");
        try{
            gridClient.getTermsForCategory(null, null);
            fail("should fail");
        } catch(AxisFault e) {
            assertTrue(e.toString(),  e.getMessage().contains(NullPointerException.class.getName()));
        }
    }

    @Test
    public void testGetTermsForCategory_Category() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testGetTermsForCategory_Category");
        String cid = "URN:LSID:"+Category.class.getName()+":231"; // DerivedBioAssayType
        Term[] l = gridClient.getTermsForCategory(new CaArrayEntityReference(cid), null);
        assertEquals(25, l.length);

        logForSilverCompatibility(TEST_OUTPUT, "test Term transmission");
        Term term = null;
        for (Term t : l) {
            if ("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Term:8".equals(t.getId())) {
                term = t;
                break;
            }
        }
        assertNotNull(term);
        logForSilverCompatibility(TEST_OUTPUT, "test Term transfer");
        assertEquals("MBEI", term.getValue());
        assertEquals("MO_756", term.getAccession());
        assertEquals("http://mged.sourceforge.net/ontologies/MGEDontology.php#MBEI", term.getUrl());
        TermSource s = term.getTermSource();
        assertEquals("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource:1", s.getId());
        assertEquals("MO", s.getName());
        assertEquals("1.3.1.1", s.getVersion());
        assertEquals("http://mged.sourceforge.net/ontologies/MGEDontology.php", s.getUrl());
    }

    @Test
    public void testGetTermsForCategory_CategoryAndPrefix() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testGetTermsForCategory_CategoryAndPrefix");
        String cid = "URN:LSID:"+Category.class.getName()+":231"; // DerivedBioAssayType
        String prefix = "a";
        Term[] l = gridClient.getTermsForCategory(new CaArrayEntityReference(cid), prefix);
        assertEquals(6, l.length);
        for (Term t : l) {
            assertTrue("should start with " + prefix + " but value was "+t.getValue(), t.getValue().toLowerCase().startsWith(prefix));
        }
    }

    ////////////////////////////////////////////////////

    @Test
    public void testsearchForBiomaterials_All() throws InvalidReferenceException, UnsupportedCategoryException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_All");

        BiomaterialSearchCriteria bisc = new BiomaterialSearchCriteria();
        SearchResult<Biomaterial> sr = gridClient.searchForBiomaterials(bisc, null);
        assertTrue(sr.getResults().size() < 200);
        assertEquals(148, sr.getResults().size());

        logForSilverCompatibility(TEST_OUTPUT, "test Biomaterial transmission");
        Biomaterial b = null;
        for (Biomaterial n : sr.getResults()) {
            if (n.getId().endsWith(":1")) {
                b = n;
                break;
            }
        }
        assertNotNull(b);
        logForSilverCompatibility(TEST_OUTPUT, "test Biomaterial transfer");
        assertEquals("name", "Cy3 labeled Pr111 reference_8kNewPr111_14v1p4m11", b.getName());
        assertNull("cellType", b.getCellType());
        assertTrue("Characteristics", b.getCharacteristics().isEmpty());
        assertNull("Description", b.getDescription());
        assertNull("DiseaseState", b.getDiseaseState());
        assertEquals("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1", b.getExperiment().getId());
        assertNull("ExternalId", b.getExternalId());
        assertEquals("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Term:152", b.getMaterialType().getTerm().getId());
        assertNull("MaterialType", b.getMaterialType().getUnit());
        assertNull("organism", b.getOrganism());
        assertNull("", b.getTissueSite());
        assertEquals(BiomaterialType.LABELED_EXTRACT, b.getType());
    }

    @Test
    public void testsearchForBiomaterials_Limit() throws InvalidReferenceException, UnsupportedCategoryException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_Limit");

        BiomaterialSearchCriteria bisc = new BiomaterialSearchCriteria();
        SearchResult<Biomaterial> sr = gridClient.searchForBiomaterials(bisc, null);
        assertTrue(sr.isFullResult());
        int all = sr.getResults().size();
        int chunk = all/2 + 10; // somewhere near past the middle
        // first batch
        LimitOffset off = new LimitOffset(chunk, 0);
        sr = gridClient.searchForBiomaterials(bisc, off);
        assertEquals(off.getOffset(), sr.getFirstResultOffset());
        all -= sr.getResults().size();
        List<String> ids = getIds(sr.getResults());
        assertEquals(chunk, ids.size());
        // second batch
        off.setOffset(sr.getResults().size());
        sr = gridClient.searchForBiomaterials(bisc, off);
        all -= sr.getResults().size();
        assertEquals(0, all);

        // check we didnt get the same ones in the second chunk
        ids.removeAll(getIds(sr.getResults()));
        assertEquals(chunk, ids.size());

    }

    private static List<String> getIds(List<? extends AbstractCaArrayEntity> entities) {
        ArrayList<String> ids = new ArrayList<String>(entities.size());
        for (AbstractCaArrayEntity a : entities) {
            ids.add(a.getId());
        }
        return ids;
    }

    @Test(expected = UnsupportedCategoryFault.class)
    public void testsearchForBiomaterials_BadCategory() throws InvalidReferenceException, UnsupportedCategoryException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_BadCategory");
        BiomaterialSearchCriteria bisc = new BiomaterialSearchCriteria();
        CaArrayEntityReference c = new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:"+Category.class.getName()+":1");//CancerSite

        AnnotationCriterion a = new AnnotationCriterion(c, null);
        bisc.getAnnotationCriterions().add(a);
        gridClient.searchForBiomaterials(bisc, null);
        fail("CancerSite not allowed as criterion");

    }

    private static final String URN_CATEGORY = "URN:LSID:caarray.nci.nih.gov:"+Category.class.getName()+":";

    @Test
    public void testsearchForBiomaterials_Category_DiseaseState() throws InvalidReferenceException, UnsupportedCategoryException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_Category_DiseaseState");

        String value = "???";
        int count = 0;
        searchBioByCategory(97L, value, count);
    }

    @Test
    public void testsearchForBiomaterials_Category_CellType() throws InvalidReferenceException, UnsupportedCategoryException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_Category_CellType");
        String value = "low grade prostatic intraepithelial neoplasia";
        int count = 19;
        searchBioByCategory(63L, value, count);
    }

    @Test
    public void testsearchForBiomaterials_Category_MaterialType() throws InvalidReferenceException, UnsupportedCategoryException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_Category_MaterialType");
        String value = "synthetic_RNA";
        int count = 74;
        searchBioByCategory(153L, value, count);
    }

    @Test
    public void testsearchForBiomaterials_Category_OrganismPart() throws InvalidReferenceException, UnsupportedCategoryException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_Category_OrganismPart");//tissue_site
        String value = "???";
        int count = 0;
        searchBioByCategory(178L, value, count);
    }

    private void searchBioByCategory(long categoryId, String value, int count) throws UnsupportedCategoryException, InvalidReferenceException, RemoteException {
        CaArrayEntityReference cat = new CaArrayEntityReference(URN_CATEGORY + categoryId);
        BiomaterialSearchCriteria bisc = new BiomaterialSearchCriteria();
        bisc.getAnnotationCriterions().add(new AnnotationCriterion(cat, value));
        SearchResult<Biomaterial> sr = gridClient.searchForBiomaterials(bisc, null);
        assertEquals(count, sr.getResults().size());
    }

    /////////////////////////////////////////////////

    @Test
    public void testSearchExperimentsByKeyword() throws RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchExperimentsByKeyword");
        KeywordSearchCriteria crit = new KeywordSearchCriteria();
        crit.setKeyword("Affymetrix");
        List<Experiment> experiments = gridClient.searchForExperimentsByKeyword(crit, null).getResults();
        assertEquals(1, experiments.size());
        assertEquals("test2", experiments.get(0).getTitle());

        crit.setKeyword("test1");
        experiments = gridClient.searchForExperimentsByKeyword(crit, null).getResults();
        assertEquals(1, experiments.size());
        assertEquals("test1", experiments.get(0).getTitle());
    }

    /////////////////////////////////////////////

    @Test
    public void testSearchExperiments() throws RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchExperiments");
        ExperimentSearchCriteria crit = new ExperimentSearchCriteria();
        crit.setTitle("test1");
        try {
            logForSilverCompatibility(TEST_OUTPUT, "title");
            List<Experiment> experiments = gridClient.searchForExperiments(crit, null).getResults();
            assertEquals(1, experiments.size());
            assertEquals("test1", experiments.get(0).getTitle());
        } catch (InvalidReferenceFault e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        } catch (UnsupportedCategoryFault e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        }

        crit = new ExperimentSearchCriteria();
        crit.setPublicIdentifier("admin-00001");
        try {
            logForSilverCompatibility(TEST_OUTPUT, "public id");
            List<Experiment> experiments = gridClient.searchForExperiments(crit, null).getResults();
            assertEquals(1, experiments.size());
            assertEquals("test1", experiments.get(0).getTitle());
        } catch (InvalidReferenceFault e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        } catch (UnsupportedCategoryFault e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        }

        crit = new ExperimentSearchCriteria();
        crit.setOrganism(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Organism:2"));// Mus musculus
        crit.setPublicIdentifier("admin-00002");
        try {
            logForSilverCompatibility(TEST_OUTPUT, "title and Organism");
            List<Experiment> experiments = gridClient.searchForExperiments(crit, null).getResults();
            assertEquals(1, experiments.size());
            assertEquals("test2", experiments.get(0).getTitle());
        } catch (InvalidReferenceFault e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        } catch (UnsupportedCategoryFault e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        }
    }

    //////////////////////////

    @Test
    public void testSearchBiomaterialsByKeyword() throws RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchBiomaterialsByKeyword");
        BiomaterialKeywordSearchCriteria crit = new BiomaterialKeywordSearchCriteria();
        crit.setKeyword("8knew");
        List<Biomaterial> bms = gridClient.searchForBiomaterialsByKeyword(crit, null).getResults();
        assertEquals(64, bms.size());

        for (Biomaterial b : bms) {
            assertTrue("unexpected name " + b.getName(), b.getName().toLowerCase().contains("8knew"));
        }
    }

    ///////////////////////////

    @Test
    public void testSearchForBiomaterials() throws InvalidReferenceException, UnsupportedCategoryException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForBiomaterials");
        BiomaterialSearchCriteria crit = new BiomaterialSearchCriteria();
        crit.setExperiment(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
        crit.setTypes(EnumSet.of(BiomaterialType.SOURCE, BiomaterialType.SAMPLE));
        List<Biomaterial> bms = gridClient.searchForBiomaterials(crit, null).getResults();
        assertEquals(74, bms.size());
    }

    @Test
    public void testSearchForHybridizations() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForHybridizations");
        HybridizationSearchCriteria crit = new HybridizationSearchCriteria();
        crit.setExperiment(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
        List<Hybridization> hybs = gridClient.searchForHybridizations(crit, null).getResults();
        assertEquals(19, hybs.size());
        Hybridization h = null;

        logForSilverCompatibility(TEST_OUTPUT, "test Hybridization transmission");
        for (Hybridization x : hybs) {
            if ("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1".equals(x.getId())) {
                h = x;
                break;
            }
        }
        assertNotNull(h);
        assertEquals("ArrayDesign.Name", "Mm-Incyte-v1px_16Bx24Cx23R", h.getArrayDesign().getName());
        assertEquals("experiment.id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1", h.getExperiment().getId());
        assertEquals("factorValues.size", 2, h.getFactorValues().size());
        assertEquals("name", "gov.nih.nci.ncicb.caarray:Hybridization:1015897590131481:1", h.getName());
    }

    /////////////////////////////////

    @Test
    public void testSearchForQuantitationTypes() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForQuantitationTypes");
        QuantitationTypeSearchCriteria crit = new QuantitationTypeSearchCriteria();
        crit.setHybridization(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1"));
        QuantitationType[] types = gridClient.searchForQuantitationTypes(crit);
        assertEquals(38, types.length);
        QuantitationType qt = null;
        for (QuantitationType t : types) {
            if ("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:59".equals(t.getId())) {
                qt = t;
                break;
            }
        }
        assertEquals("% > B532+1SD", qt.getName());
        assertEquals(DataType.INTEGER, qt.getDataType());
    }

    @Test
    public void testSearchForQuantitationTypes_NoHyb() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForQuantitationTypes_NoHyb");

        try {
            QuantitationTypeSearchCriteria crit = new QuantitationTypeSearchCriteria();
            QuantitationType[] types = gridClient.searchForQuantitationTypes(crit);
            logForSilverCompatibility(TEST_OUTPUT, "unexpected validation outcome");
            fail();
        } catch(AxisFault e) {
            assertTrue(e.toString(), e.getMessage().contains(NullPointerException.class.getName()));
            logForSilverCompatibility(TEST_OUTPUT, "null Hyb validation :" + e.getCause());
        }
    }

    @Test
    public void testSearchForQuantitationTypes_All() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForQuantitationTypes_All");
        QuantitationTypeSearchCriteria crit = new QuantitationTypeSearchCriteria();
        crit.setHybridization(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1"));
        QuantitationType[] types = gridClient.searchForQuantitationTypes(crit);
        assertEquals(38, types.length);

    }

    @Test
    public void testSearchForQuantitationTypes_ArrayDataType() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForQuantitationTypes_ArrayDataType");
        QuantitationTypeSearchCriteria crit = new QuantitationTypeSearchCriteria();
        crit.setHybridization(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1"));
        crit.getArrayDataTypes().add(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.ArrayDataType:1"));//Affymetrix CHP (Gene Expression)
        QuantitationType[] types = gridClient.searchForQuantitationTypes(crit);
        if (types != null) {
            assertEquals(0, types.length);
        }

        crit.getArrayDataTypes().clear();
        crit.getArrayDataTypes().add(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.ArrayDataType:4"));//Genepix GPR (Gene Expression)
        types = gridClient.searchForQuantitationTypes(crit);
        assertEquals(38, types.length);
    }

    @Test
    public void testSearchForQuantitationTypes_FileTypeCategories() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForQuantitationTypes_FileTypeCategories");
        QuantitationTypeSearchCriteria crit = new QuantitationTypeSearchCriteria();
        crit.setHybridization(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1"));
        crit.getFileCategories().add(FileCategory.DERIVED_DATA);
        QuantitationType[] types = gridClient.searchForQuantitationTypes(crit);
        assertEquals(38, types.length);
    }


    /*
     * see GF 22409.
     **/
    @Test
    public void testSearchForQuantitationTypes_FileTypes() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForQuantitationTypes_FileTypes");
        QuantitationTypeSearchCriteria crit = new QuantitationTypeSearchCriteria();
        crit.setHybridization(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1"));
        crit.getFileTypes().add(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.FileType:GENEPIX_GPR"));
        QuantitationType[] types = gridClient.searchForQuantitationTypes(crit);
        assertEquals(38, types.length);

        crit.getFileTypes().clear();
        crit.getFileTypes().add(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.FileType:ILLUMINA_IDAT"));
        types = gridClient.searchForQuantitationTypes(crit);
        assertEquals(0, types.length);
    }
    ///////////////////////////////

    @Test
    public void testSearchForFiles() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForFiles");
        FileSearchCriteria crit = new FileSearchCriteria();
        crit.setExperiment(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
        crit.setCategories(EnumSet.of(FileCategory.DERIVED_DATA));
        List<File> files = gridClient.searchForFiles(crit, null).getResults();
        assertEquals(19, files.size());

        logForSilverCompatibility(TEST_OUTPUT, "test File transmission");
        File df = null;
        for (File f : files) {
            if ("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.File:2".equals(f.getId())) {
                df = f;
                break;
            }
        }
        assertNotNull(df);
        assertEquals("fileType.name", "GENEPIX_GPR", df.getMetadata().getFileType().getName());
        assertEquals("CompressedSize", 682885, df.getMetadata().getCompressedSize());
        assertEquals("UncompressedSize", 1771373, df.getMetadata().getUncompressedSize());
    }

    @Test
    public void testSearchForFiles_Experiment() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForFiles_Experiment");
        FileSearchCriteria crit = new FileSearchCriteria();
        crit.setExperiment(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
        List<File> files = gridClient.searchForFiles(crit, null).getResults();
        assertEquals(files.toString(), 19, files.size());
    }

    @Test
    public void testSearchForFiles_ExperimentGraphNodes() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForFiles_ExperimentGraphNodes");
        FileSearchCriteria crit = new FileSearchCriteria();
        crit.getExperimentGraphNodes().add(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1"));//Hybridization
        crit.getExperimentGraphNodes().add(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:74"));//Extract
        crit.getExperimentGraphNodes().add(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:11"));//Labled Extract
        crit.getExperimentGraphNodes().add(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:97"));//Sample
        crit.getExperimentGraphNodes().add(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:112"));//Source

        List<File> files = gridClient.searchForFiles(crit, null).getResults();
        List<String> expectedNames = new ArrayList<String>(Arrays.asList(
                "8kReversNew17_111_4601_m83.gpr",
                "8kNew111_14v1p4m12.gpr",
                "8kNewPr111_14v1p4m13.gpr",
                "8kNew111_17m12701m36.gpr",
                "8kNew111_14_4601_m84.gpr"));
        assertEquals(expectedNames.size(), files.size());

        for (File f : files) {
            assertTrue(f.getMetadata().getName() + " not found ", expectedNames.remove(f.getMetadata().getName()));
        }
        assertTrue(expectedNames.toString(), expectedNames.isEmpty());
    }

    @Test
    public void testSearchForFiles_Extention() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForFiles_Extention");
        FileSearchCriteria crit = new FileSearchCriteria();
        crit.setExtension("gpr");
        List<File> files = gridClient.searchForFiles(crit, null).getResults();
        assertEquals(19, files.size());
        for(File f:files){
            System.out.println("types  "+f.getMetadata().getFileType());
        }
    }

    @Test
    public void testSearchForFiles_Type() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForFiles_Type");
        FileSearchCriteria crit = new FileSearchCriteria();
        crit.getTypes().add(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.FileType:GENEPIX_GPR"));
        List<File> files = gridClient.searchForFiles(crit, null).getResults();
        assertEquals(19, files.size());
    }

    @Test
    public void testSearchForFiles_All() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForFiles_All");
        FileSearchCriteria crit = new FileSearchCriteria();
        List<File> files = gridClient.searchForFiles(crit, null).getResults();
        assertEquals(19, files.size());
    }

    @Test
    public void testSearchForFiles_Limit() throws InvalidReferenceException, RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchForFiles_Limit");
        FileSearchCriteria fsc = new FileSearchCriteria();
        SearchResult<File> sr = gridClient.searchForFiles(fsc, null);
        assertTrue(sr.isFullResult());
        int all = sr.getResults().size();
        int chunk = all/2 + 2; // somewhere near past the middle
        // first batch
        LimitOffset off = new LimitOffset(chunk, 0);
        sr = gridClient.searchForFiles(fsc, off);
        assertEquals(off.getOffset(), sr.getFirstResultOffset());
        all -= sr.getResults().size();
        List<String> ids = getIds(sr.getResults());
        assertEquals(chunk, ids.size());
        // second batch
        off.setOffset(sr.getResults().size());
        sr = gridClient.searchForFiles(fsc, off);
        all -= sr.getResults().size();
        assertEquals(0, all);

        // check we didnt get the same ones in the second chunk
        ids.removeAll(getIds(sr.getResults()));
        assertEquals(chunk, ids.size());
    }

    /////////////////////////////////////

    @Test
    public void testSearchByExample_Null() throws RemoteException {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_Null");
        try {
            logForSilverCompatibility(TEST_OUTPUT, "null criteria");
            SearchResult sr = gridClient.searchByExample(null, null);
            logForSilverCompatibility(TEST_OUTPUT, "unexpected outcome");
            fail();
        } catch(AxisFault e) {
            assertTrue(e.toString(), e.getMessage().contains(NullPointerException.class.getName()));
            logForSilverCompatibility(TEST_OUTPUT, "null Criteria validation :" + e);
        }

        try {
            logForSilverCompatibility(TEST_OUTPUT, "null exanple");
            ExampleSearchCriteria xsc = new ExampleSearchCriteria(null);
            SearchResult sr = gridClient.searchByExample(xsc, null);
            logForSilverCompatibility(TEST_OUTPUT, "unexpected outcome");
            fail();
        } catch(AxisFault e) {
            assertTrue(e.toString(), e.getMessage().contains(NullPointerException.class.getName()));
            logForSilverCompatibility(TEST_OUTPUT, "null example validation :" + e);
        }
    }

    private <T extends AbstractCaArrayEntity> void testExampleProperty(T example, String property, Object value, int count) throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample " + example.getClass().getName()+ "." + property);
        ExampleSearchCriteria xsc = new ExampleSearchCriteria(example);
        if (property != null) {
            PropertyUtils.setProperty(example, property, value);
        }
        SearchResult<T> sr = gridClient.searchByExample(xsc, null);
        if (sr.isFullResult()){
            assertEquals("count for search by "+property, count, sr.getResults().size());
        } else {
            assertTrue("result size " + sr.getResults().size() + " < " + count, sr.getResults().size() < count);
            assertEquals(sr.getMaxAllowedResults(), sr.getResults().size());
        }
        if (property != null) {
            for (T t : sr.getResults()) {
                Object got = PropertyUtils.getProperty(t, property);
                assertTrue(property + " \nexpected :"+value+"\nbut was :"+got, verify(property, value, got));
            }
        } else {
           for (T t : sr.getResults()) {
                System.out.println(t.toString());
            }
        }
    }

    private boolean verify(String prop, Object expected, Object actual) {
        try{
            if (expected == null && actual == null) return true;
            if (expected == null || actual == null) return false;

            if (expected instanceof Collection) {
                for(Object e : (Collection)expected) {
                    if(((Collection)actual).contains(e))
                        return true;
                }
                return false;
            } else if (expected instanceof String) {
                return ((String)expected).equalsIgnoreCase((String)actual);
            } else {
                return expected.equals(actual);
            }
        }catch(Exception e) {
            throw new RuntimeException(prop, e);
        }
    }

    @Test
    public void testSearchByExample_Organism() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_Organism");
        testExampleProperty(new Organism(), null, null, 33);
        testExampleProperty(new Organism(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Organism:1", 1);
        testExampleProperty(new Organism(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Organism:0", 0);
        testExampleProperty(new Organism(), "commonName", "thale cress", 1);
        testExampleProperty(new Organism(), "scientificName", "Arabidopsis thaliana", 1);
    }

    @Test
    public void testSearchByExample_File() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_File");
        testExampleProperty(new File(), null, null, 24);
        testExampleProperty(new File(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.File:1", 1);
        testExampleProperty(new File(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.File:0", 0);

        testExampleProperty(new File(), "compressedSize", 114521L, 1);
        testExampleProperty(new File(), "uncompressedSize", 114521L, 1);
        testExampleProperty(new File(), "name", "8kNew111_17_4601_m82.gpr", 1);
        FileType type = new FileType();
        type.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.FileType:MAGE_TAB_IDF");
        testExampleProperty(new File(), "fileType", type, 1);
    }

    @Test
    public void testSearchByExample_QuantitationType() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_QuantitationType");
        testExampleProperty(new QuantitationType(), null, null, 123);
        testExampleProperty(new QuantitationType(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:1", 1);
        testExampleProperty(new QuantitationType(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:0", 0);
        testExampleProperty(new QuantitationType(), "name", "CHPDetection", 1);
        testExampleProperty(new QuantitationType(), "dataType", DataType.STRING, 3);
    }

    @Test
    public void testSearchByExample_Experiment() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_Experiment");
        testExampleProperty(new Experiment(), null, null, 2);
        testExampleProperty(new Experiment(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1", 1);
        testExampleProperty(new Experiment(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:0", 0);

        testExampleProperty(new Experiment(), "title", "test2", 1);
        testExampleProperty(new Experiment(), "description", "empty experiment", 1);
        testExampleProperty(new Experiment(), "publicIdentifier", "admin-00002", 1);
        ArrayProvider ap = new ArrayProvider("Affymetrix");
        ap.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.array.ArrayProvider:1");
        testExampleProperty(new Experiment(), "arrayProvider", ap, 1);
        AssayType at = new AssayType("Gene Expression");
        at.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.array.AssayType:3");
        testExampleProperty(new Experiment(), "assayTypes", Collections.singleton(at), 1);
        Term t = new Term();
        t.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Term:697");
        testExampleProperty(new Experiment(), "experimentalDesigns", Collections.singleton(t), 1);
        Factor f = new Factor();
        f.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.factor.Factor:1");
        testExampleProperty(new Experiment(), "factors", Collections.singleton(f), 1);
        Organism o = new Organism();
        o.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Organism:2");
        testExampleProperty(new Experiment(), "organism", o, 1);
        Person p = new Person();
        p.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Person:12");
        testExampleProperty(new Experiment(), "contacts", Collections.singleton(p), 1);
        ArrayDesign ad = new ArrayDesign();
        ad.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.array.ArrayDesign:1");
        ad.setName("Mm-Incyte-v1px_16Bx24Cx23R");
        testExampleProperty(new Experiment(), "arrayDesigns", Collections.singleton(ad), 1);
        testExampleProperty(new Experiment(), "normalizationTypes", "TODO", 1);
        testExampleProperty(new Experiment(), "qualityControlTypes", "TODO", 1);
        testExampleProperty(new Experiment(), "replicateTypes", "TODO", 1);
    }

    @Test
    public void testSearchByExample_Person() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_Person");
        testExampleProperty(new Person(), null, null, 4);
        testExampleProperty(new Person(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Person:1", 1);
        testExampleProperty(new Person(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Person:0", 0);

        testExampleProperty(new Person(), "emailAddress", "JEGreen@nih.gov", 2);
        testExampleProperty(new Person(), "firstName", "Alfonso", 1);
        testExampleProperty(new Person(), "lastName", "Administrator", 2);
        testExampleProperty(new Person(), "middleInitials", "E", 1);
    }

    @Test
    public void testSearchByExample_Hybridization() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_Hybridization");
        testExampleProperty(new Hybridization(), null, null, 19);
        testExampleProperty(new Hybridization(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1", 1);
        testExampleProperty(new Hybridization(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:0", 0);

        ArrayDesign ad = new ArrayDesign();
        ad.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.array.ArrayDesign:1");
        ad.setName("Mm-Incyte-v1px_16Bx24Cx23R");
        testExampleProperty(new Hybridization(), "arrayDesign", ad, 19);
        CaArrayEntityReference  e = new CaArrayEntityReference ("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1");
        testExampleProperty(new Hybridization(), "experiment", e, 19);
        FactorValue fv = new FactorValue();
        Factor f = new Factor();
        f.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.factor.Factor:1");
        fv.setFactor(f);
        UserDefinedValue v = new UserDefinedValue();
        Term t = new Term();
        t.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource:1");
        v.setUnit(t);
        v.setValue("Pr111 reference");
        fv.setValue(v);
        testExampleProperty(new Hybridization(), "factorValues", Collections.singleton(fv), 19);
        testExampleProperty(new Hybridization(), "name", "gov.nih.nci.ncicb.caarray:Hybridization:1015897590131481:1", 1);
    }

    @Test
    public void testSearchByExample_Term() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_Term");
        testExampleProperty(new Term(), null, null, 698);
        testExampleProperty(new Term(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Term:1", 1);
        testExampleProperty(new Term(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Term:0", 0);

        testExampleProperty(new Term(), "accession", "MO_562", 1);
        TermSource ts = new TermSource();
        ts.setId("id=URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource:3");
        testExampleProperty(new Term(), "termSource", ts, 10);
        testExampleProperty(new Term(), "url", "http://mged.sourceforge.net/ontologies/MGEDontology.php#silicon", 1);
        testExampleProperty(new Term(), "value", "synthetic_RNA", 1);
    }

    @Test
    public void testSearchByExample_Category() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_Category");
        testExampleProperty(new Category(), null, null, 239);
        testExampleProperty(new Category(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Category:1", 1);
        testExampleProperty(new Category(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Category:0", 0);

        testExampleProperty(new Category(), "accession", "MO_119", 1);
        testExampleProperty(new Category(), "name", "MeasurementType", 1);
        TermSource ts = new TermSource();
        ts.setId("id=URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource:1");
        testExampleProperty(new Category(), "termSource", ts, 233);
        testExampleProperty(new Category(), "url", "http://mged.sourceforge.net/ontologies/MGEDontology.php#InitialTimePoint", 1);
    }

    @Test
    public void testSearchByExample_TermSource() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_TermSource");
        testExampleProperty(new TermSource(), null, null, 8);
        testExampleProperty(new TermSource(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource:1", 1);
        testExampleProperty(new TermSource(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource:0", 0);

        testExampleProperty(new TermSource(), "name", "MO", 1);
        testExampleProperty(new TermSource(), "url", "http://example.com/a", 1);
        testExampleProperty(new TermSource(), "version", "3.2", 1);
    }

    @Test
    public void testSearchByExample_Factor() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_Factor");
        testExampleProperty(new Factor(), null, null, 1);
        testExampleProperty(new Factor(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.factor.Factor:1", 1);
        testExampleProperty(new Factor(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.factor.Factor:0", 0);

        testExampleProperty(new Factor(), "description", null, 1);
        testExampleProperty(new Factor(), "name", "Cell Lines", 1);
        Term t = new Term();
        t.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Term:286");
        testExampleProperty(new Factor(), "type", t, 1);
    }

    @Test
    public void testSearchByExample_ExperimentalContact() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_ExperimentalContact");
        testExampleProperty(new ExperimentalContact(), null, null, 4);
        testExampleProperty(new ExperimentalContact(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experimentExperimentalContact:1", 1);
        testExampleProperty(new ExperimentalContact(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experimentExperimentalContact:0", 0);

        Person p = new Person();
        p.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Person:9");
        testExampleProperty(new ExperimentalContact(), "person", p, 1);
        Term t = new Term();
        t.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Term:286");//???
        testExampleProperty(new ExperimentalContact(), "roles", Collections.singleton(t), 1);
    }

    @Test
    public void testSearchByExample_ArrayDesign() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_ArrayDesign");
        testExampleProperty(new ArrayDesign(), null, null, 1);
        testExampleProperty(new ArrayDesign(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.array.ArrayDesign:1", 1);
        testExampleProperty(new ArrayDesign(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.array.ArrayDesign:0", 0);

        ArrayProvider ap = new ArrayProvider();
        ap.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.array.ArrayProvider:1");
        testExampleProperty(new ArrayDesign(), "arrayProvider", ap, 1);
        AssayType at = new AssayType("Gene Expression");
        at.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.array.AssayType:3");
        testExampleProperty(new ArrayDesign(), "assayTypes", Collections.singleton(at), 1);
        File df = new File();
        df.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.File:2");
        testExampleProperty(new ArrayDesign(), "files", Collections.singleton(df), 1);
        testExampleProperty(new ArrayDesign(), "lsid", "TODO", 0);
        testExampleProperty(new ArrayDesign(), "name", "Mm-Incyte-v1px_16Bx24Cx23R", 1);
        Organism o = new Organism();
        o.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Organism:2");// Mus musculus
        testExampleProperty(new ArrayDesign(), "organism", o, 1);
        Term t = new Term();
        t.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Term:624");//???
        testExampleProperty(new ArrayDesign(), "technologyType", t, 1);
        testExampleProperty(new ArrayDesign(), "version", "1", 1);
    }

    @Test
    public void testSearchByExample_Biomaterial() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_Biomaterial");
        testExampleProperty(new Biomaterial(), null, null, 148);

        testExampleProperty(new Biomaterial(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:1", 1);
        testExampleProperty(new Biomaterial(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:0", 0);

        TermValue tv = new TermValue();
        Term t = new Term();
        t.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Term:683");
        tv.setTerm(t);
        testExampleProperty(new Biomaterial(), "cellType", tv, 19);

        Characteristic c = new Characteristic();
        Category ct = new Category();
        ct.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Category:39");
        c.setCategory(ct);
        testExampleProperty(new Biomaterial(), "characteristics", Collections.singleton(c), 0);

        testExampleProperty(new Biomaterial(), "description", "foo", 0);

        tv = new TermValue();
        tv.setTerm(t);
        tv.setUnit(t);
        testExampleProperty(new Biomaterial(), "diseaseState", tv, 0);

        CaArrayEntityReference r = new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1");
        testExampleProperty(new Biomaterial(), "experiment", r, 148);

        testExampleProperty(new Biomaterial(), "externalId", "foo", 0);

        tv = new TermValue();
        t = new Term();
        t.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Term:152");
        tv.setTerm(t);
        testExampleProperty(new Biomaterial(), "materialType", tv, 1);

        testExampleProperty(new Biomaterial(), "name", "Cy3 labeled Pr111 reference_8kNewPr111_14v1p4m11", 1);

        Organism o = new Organism();
        o.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Organism:2");
        testExampleProperty(new Biomaterial(), "organism", o, 1);

        tv = new TermValue();
        t = new Term();
        t.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Term:682");
        tv.setTerm(t);
        testExampleProperty(new Biomaterial(), "tissueSite", tv, 1);

        testExampleProperty(new Biomaterial(), "type", BiomaterialType.SOURCE, 37);
    }

    @Test
    public void testSearchByExample_ArrayDataType() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_ArrayDataType");
        testExampleProperty(new ArrayDataType(), null, null, 7);
        testExampleProperty(new ArrayDataType(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.ArrayDataType:1", 1);
        testExampleProperty(new ArrayDataType(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.ArrayDataType:0", 0);
        testExampleProperty(new ArrayDataType(), "name", "Illumina CSV (Genotyping)", 1);
        QuantitationType qt = new QuantitationType();
        qt.setId("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:33");
        testExampleProperty(new ArrayDataType(), "quantitationTypes", Collections.singleton(qt), 1);
        testExampleProperty(new ArrayDataType(), "version", "foo", 0);
    }

    @Test
    public void testSearchByExample_AssayType() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_AssayType");
        testExampleProperty(new AssayType(), null, null, 6);
        testExampleProperty(new AssayType(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.array.AssayType:0", 0);
        testExampleProperty(new AssayType(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.array.AssayType:1", 1);
        testExampleProperty(new AssayType(), "name", "Exon", 1);
    }

    @Test
    public void testSearchByExample_ArrayProvider() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testSearchByExample_ArrayProvider");
        testExampleProperty(new ArrayProvider(), null, null, 8);
        testExampleProperty(new ArrayProvider(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.array.ArrayProvider:0", 0);
        testExampleProperty(new ArrayProvider(), "id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.array.ArrayProvider:1", 1);
        testExampleProperty(new ArrayProvider(), "name", "Nimblegen", 1);
    }

    ///////////////////////////////////////

    @Test
    public void testGetAnnotationSet_Null() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testGetAnnotationSet_Null");
        try {
            logForSilverCompatibility(TEST_OUTPUT, "null request");
            AnnotationSet aset = gridClient.getAnnotationSet(null);
            logForSilverCompatibility(TEST_OUTPUT, "unexpected outcome");
            fail();
        } catch(AxisFault e) {
            assertTrue(e.toString(), e.getMessage().contains(NullPointerException.class.getName()));
            logForSilverCompatibility(TEST_OUTPUT, "null request validation :" + e);
        }
    }

    @Test
    public void testGetAnnotationSet_Empty() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testGetAnnotationSet_Empty");
        AnnotationSetRequest r = new AnnotationSetRequest();
        AnnotationSet aset = gridClient.getAnnotationSet(r);
        assertEquals(0, aset.getCategories().size());
        assertEquals(0, aset.getColumns().size());
    }

    @Test
    public void testGetAnnotationSet_Categories() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testGetAnnotationSet_Categories");
        AnnotationSetRequest r = new AnnotationSetRequest();
        String id = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Category:39";
        r.getCategories().add(new CaArrayEntityReference(id));
        r.getCategories().add(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Category:40"));
        AnnotationSet aset = gridClient.getAnnotationSet(r);
        assertEquals(2, aset.getCategories().size());
        assertEquals(0, aset.getColumns().size());
        for (Category c : aset.getCategories()) {
            if (c.getId().equalsIgnoreCase(id)) {
                assertEquals("Individual", c.getName());
                assertEquals("http://mged.sourceforge.net/ontologies/MGEDontology.php#Individual", c.getUrl());
                assertEquals("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource:1", c.getTermSource().getId());
                break;
            }
        }
    }

    @Test
    public void testGetAnnotationSet_ExperimentGraphNodes() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testGetAnnotationSet_ExperimentGraphNodes");
        AnnotationSetRequest r = new AnnotationSetRequest();
        String id = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:39";
        r.getExperimentGraphNodes().add(new CaArrayEntityReference(id));
        r.getExperimentGraphNodes().add(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:3"));
        AnnotationSet aset = gridClient.getAnnotationSet(r);
        assertEquals(0, aset.getCategories().size());
        assertEquals(2, aset.getColumns().size());
        for (AnnotationColumn c : aset.getColumns()) {
            assertTrue(c.getValueSets().isEmpty());
        }
    }

    @Test
    public void testGetAnnotationSet_ExperimentGraphNodes_Category() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testGetAnnotationSet_ExperimentGraphNodes_Category");
        AnnotationSetRequest r = new AnnotationSetRequest();
        String nid = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:39";
        String cid = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Category:153";
        r.getCategories().add(new CaArrayEntityReference(cid));
        r.getExperimentGraphNodes().add(new CaArrayEntityReference(nid));
        AnnotationSet aset = gridClient.getAnnotationSet(r);
        assertEquals(1, aset.getCategories().size());
        assertEquals(1, aset.getColumns().size());
        AnnotationColumn c = aset.getColumns().get(0);
        Biomaterial b =  (Biomaterial) c.getNode();
        assertEquals(nid, b.getId());
        assertEquals(1, c.getValueSets().size());
        Set<AbstractValue> v = c.getValueSets().get(0).getValues();
        assertEquals(1, v.size());
        System.out.println(v.iterator().next());
    }
}
