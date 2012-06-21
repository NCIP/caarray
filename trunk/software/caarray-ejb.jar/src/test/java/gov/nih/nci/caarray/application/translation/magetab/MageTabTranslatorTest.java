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
package gov.nih.nci.caarray.application.translation.magetab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.MeasurementFactorValue;
import gov.nih.nci.caarray.domain.project.TermBasedFactorValue;
import gov.nih.nci.caarray.domain.project.UserDefinedFactorValue;
import gov.nih.nci.caarray.domain.protocol.MeasurementParameterValue;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.protocol.TermBasedParameterValue;
import gov.nih.nci.caarray.domain.protocol.UserDefinedParameterValue;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.sample.UserDefinedCharacteristic;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.MageTabParser;
import gov.nih.nci.caarray.magetab.MageTabParsingException;
import gov.nih.nci.caarray.magetab.Protocol;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.idf.Investigation;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.magetab.sdrf.AbstractBioMaterial;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Test for MAGE tab translator
 */
@SuppressWarnings("PMD")
public class MageTabTranslatorTest extends AbstractServiceTest {
    protected static FileType AFFYMETRIX_CHP = new FileType("AFFYMETRIX_CHP", FileCategory.DERIVED_DATA, true, "CHP");
    protected static FileType AFFYMETRIX_CEL = new FileType("AFFYMETRIX_CEL", FileCategory.RAW_DATA, true, "CEL");
    protected static FileType GENEPIX_GPR = new FileType("GENEPIX_GPR", FileCategory.DERIVED_DATA, true, "GPR");

    MageTabTranslator translator;
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    private final VocabularyServiceStub vocabularyServiceStub = new VocabularyServiceStub();
    private FileAccessServiceStub fileAccessServiceStub;

    protected FileTypeRegistry typeRegistry;
    protected Injector injector;

    /**
     * Subclasses can override this to configure a custom injector, e.g. by overriding some modules with stubbed out
     * functionality.
     * 
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected Injector createInjector() {
        System.out.println("Creating injector");

        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(FileTypeRegistry.class).toInstance(MageTabTranslatorTest.this.typeRegistry);
                requestStaticInjection(CaArrayFile.class);
                requestStaticInjection(TestMageTabSets.class);
            }
        });
    }

    /**
     * Prepares the translator implementation, stubbing out dependencies.
     */
    @Before
    public void setupTranslator() {
        final DataFileHandler dataHandler = mock(DataFileHandler.class);
        when(dataHandler.getSupportedTypes()).thenReturn(Sets.newHashSet(AFFYMETRIX_CHP, AFFYMETRIX_CEL, GENEPIX_GPR));

        this.typeRegistry = new FileTypeRegistryImpl(Sets.<DataFileHandler> newHashSet(dataHandler),
                Collections.<DesignFileHandler> emptySet());

        this.injector = createInjector();

        final MageTabTranslatorBean mageTabTranslatorBean = new MageTabTranslatorBean();
        mageTabTranslatorBean.setDaoFactory(this.daoFactoryStub);
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        this.fileAccessServiceStub = new FileAccessServiceStub();
        locatorStub.addLookup(VocabularyService.JNDI_NAME, this.vocabularyServiceStub);
        this.translator = mageTabTranslatorBean;
    }

    @Test
    public void testDefect27959() throws InvalidDataException, MageTabParsingException {
        final MageTabFileSet mageTabSet = TestMageTabSets.DEFECT_27959;
        for (final FileRef f : mageTabSet.getAllFiles()) {
            this.fileAccessServiceStub.add(f.getAsFile());
        }
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(mageTabSet);
        assertTrue(docSet.getValidationResult().isValid());
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(mageTabSet);
        final CaArrayFile dataFile = fileSet.getFile(MageTabDataFiles.DEFECT_27959_DERIVED_DATA_FILE);
        dataFile.setFileType(GENEPIX_GPR);
        final ValidationResult result = this.translator.validate(docSet, fileSet);
        assertFalse(result.isValid());
        final FileValidationResult fileResult = result
        .getFileValidationResult(MageTabDataFiles.DEFECT_27959_DERIVED_DATA_FILE.getName());
        assertNotNull(fileResult);
        assertFalse(result.isValid());
        assertEquals(1, fileResult.getMessages().size());
        assertTrue(fileResult.getMessages().get(0).getMessage().startsWith("This file is not correctly referenced"));
    }

    @Test
    public void testDefect13164_positive() throws InvalidDataException, MageTabParsingException {
        final MageTabFileSet mageTabSet = TestMageTabSets.DEFECT_13164_GOOD;
        for (final FileRef f : mageTabSet.getAllFiles()) {
            this.fileAccessServiceStub.add(f.getAsFile());
        }
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(mageTabSet);
        assertTrue(docSet.getValidationResult().isValid());
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(mageTabSet);
        final ValidationResult result = this.translator.validate(docSet, fileSet);
        assertTrue(result.isValid());
    }

    @Test
    public void testDefect13164_negative() throws InvalidDataException, MageTabParsingException {
        final MageTabFileSet mageTabSet = TestMageTabSets.DEFECT_13164_BAD;
        for (final FileRef f : mageTabSet.getAllFiles()) {
            this.fileAccessServiceStub.add(f.getAsFile());
        }
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(mageTabSet);
        assertTrue(docSet.getValidationResult().isValid());
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(mageTabSet);
        final ValidationResult result = this.translator.validate(docSet, fileSet);
        assertFalse(result.isValid());
        final FileValidationResult fileResult = result.getFileValidationResult(
                MageTabDataFiles.DEFECT_13164_SDRF.getName());
        assertNotNull(fileResult);
        assertFalse(result.isValid());
        assertEquals(1, fileResult.getMessages().size());
        assertTrue(fileResult.getMessages().get(0).getMessage().indexOf("or the Term Source should be omitted") >= 0);
    }

    @Test
    public void testDefect17200() throws InvalidDataException, MageTabParsingException {
        MageTabFileSet mageTabSet = TestMageTabSets.DEFECT_17200;
        for (final FileRef f : mageTabSet.getAllFiles()) {
            this.fileAccessServiceStub.add(f.getAsFile());
        }
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(mageTabSet);
        assertTrue(docSet.getValidationResult().isValid());
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.DEFECT_17200);
        ValidationResult result = this.translator.validate(docSet, fileSet);
        assertFalse(result.isValid());
        FileValidationResult fileResult = result.getFileValidationResult(MageTabDataFiles.DEFECT_17200_GPR.getName());
        assertNotNull(fileResult);
        assertFalse(result.isValid());
        assertEquals(1, fileResult.getMessages().size());
        assertTrue(fileResult.getMessages().get(0).getMessage().startsWith("This file is not correctly referenced"));
        fileResult = result.getFileValidationResult(GenepixArrayDataFiles.GPR_3_0_6.getName());
        assertNotNull(fileResult);
        assertFalse(result.isValid());
        assertEquals(1, fileResult.getMessages().size());
        assertTrue(fileResult.getMessages().get(0).getMessage().startsWith("This data file is not referenced from "));

        mageTabSet = new MageTabFileSet();
        mageTabSet.addNativeData(new JavaIOFileRef(MageTabDataFiles.DEFECT_17200_GPR));
        fileSet = new CaArrayFileSet();
        fileSet.add(this.fileAccessServiceStub.add(MageTabDataFiles.DEFECT_17200_GPR));
        docSet = MageTabParser.INSTANCE.parse(mageTabSet);
        assertTrue(docSet.getValidationResult().isValid());
        result = this.translator.validate(docSet, fileSet);
        assertTrue(result.isValid());
    }

    /**
     * See @see <a href="https://gforge.nci.nih.gov/tracker/?func=detail&aid=27306&group_id=305&atid=1344">Defect
     * 27306</a>
     * 
     * @throws InvalidDataException
     * @throws MageTabParsingException
     */
    @Test
    public void testThatSecondDerivedFileGetsLinkedToFirstDerivedFile() throws InvalidDataException,
    MageTabParsingException {
        // Prepare the input data

        ((MageTabTranslatorBean) this.translator).setDaoFactory(new LocalDaoFactoryStub2());

        final MageTabFileSet mageTabSet = TestMageTabSets.DEFECT_27306_INPUT_SET;
        for (final FileRef f : mageTabSet.getAllFiles()) {
            this.fileAccessServiceStub.add(f.getAsFile());
        }

        final MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(mageTabSet);
        final CaArrayFileSet caArrayFileSet = TestMageTabSets.getFileSet(mageTabSet);

        // Call the method under test

        final CaArrayTranslationResult translationResult = this.translator.translate(docSet, caArrayFileSet);

        // Check the results

        final Experiment experiment = translationResult.getInvestigations().iterator().next();
        final Hybridization hybridization = experiment.getHybridizations().iterator().next();

        final RawArrayData rawData = hybridization.getRawDataCollection().iterator().next();

        DerivedArrayData level2Data = null;
        DerivedArrayData level3Data = null;
        for (final DerivedArrayData derivedData : hybridization.getDerivedDataCollection()) {
            final String name = derivedData.getName();
            if (name.contains("level2")) {
                level2Data = derivedData;
            } else if (name.contains("level3")) {
                level3Data = derivedData;
            }
        }

        final AbstractArrayData level2Parent = level2Data.getDerivedFromArrayDataCollection().iterator().next();
        assertEquals(rawData, level2Parent);

        final AbstractArrayData level3Parent = level3Data.getDerivedFromArrayDataCollection().iterator().next();
        assertEquals(level2Data, level3Parent);
    }

    @Test
    public void testSpecificationDocuments() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE
        .parse(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);
        final CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        final Experiment experiment = result.getInvestigations().iterator().next();
        assertNotNull(experiment.getDescription());
        assertTrue(experiment.getDescription().startsWith("&lt;&gt;Gene expression of TK6"));
        assertEquals(8, experiment.getExperimentContacts().size());
        assertEquals(1, experiment.getExperimentDesignTypes().size());
        assertEquals("genetic_modification_design", experiment.getExperimentDesignTypes().iterator().next().getValue());
        assertEquals(6, experiment.getSources().size());
        assertEquals(6, experiment.getSamples().size());
        assertEquals(6, experiment.getExtracts().size());
        assertEquals(6, experiment.getLabeledExtracts().size());
        assertEquals(6, experiment.getHybridizations().size());
        for (final Hybridization hyb : experiment.getHybridizations()) {
            assertNotNull(hyb.getArray().getDesign());
            assertEquals(6, hyb.getDerivedDataCollection().iterator().next().getDerivedFromArrayDataCollection().size());
        }
    }

    @Test
    public void testSpecificationTermCaseSensitivityDocuments() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets
        .getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_CASE_SENSITIVITY_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE
        .parse(TestMageTabSets.MAGE_TAB_SPECIFICATION_CASE_SENSITIVITY_INPUT_SET);
        final CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        final Collection<Term> terms = result.getTerms();
        @SuppressWarnings("unchecked")
        final Collection<Term> matchingTerms = CollectionUtils.select(terms, new Predicate() {
            public boolean evaluate(Object o) {
                final Term t = (Term) o;
                return t.getValue().equalsIgnoreCase("fresh_sample");
            }
        });
        assertTrue(matchingTerms.size() >= 1);
        final Term oneMatch = matchingTerms.iterator().next();
        for (final Term eachMatch : matchingTerms) {
            assertTrue(oneMatch == eachMatch);
        }
    }

    @Test
    public void testSpecificationDocumentsNoExpDesc() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets
        .getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_EXP_DESC_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE
        .parse(TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_EXP_DESC_INPUT_SET);
        final CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        final Experiment experiment = result.getInvestigations().iterator().next();
        assertNull(experiment.getDescription());
        assertEquals(8, experiment.getExperimentContacts().size());
        assertEquals(1, experiment.getExperimentDesignTypes().size());
        assertEquals("genetic_modification_design", experiment.getExperimentDesignTypes().iterator().next().getValue());
        assertEquals(6, experiment.getSources().size());
        assertEquals(6, experiment.getSamples().size());
        assertEquals(6, experiment.getExtracts().size());
        assertEquals(6, experiment.getLabeledExtracts().size());
        assertEquals(6, experiment.getHybridizations().size());
        for (final Hybridization hyb : experiment.getHybridizations()) {
            assertNotNull(hyb.getArray().getDesign());
        }
    }

    @Test
    public void testSpecificationDocumentsNoArrayDesignRef() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets
        .getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_ARRAY_DESIGN_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE
        .parse(TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_ARRAY_DESIGN_INPUT_SET);
        final CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        final Experiment experiment = result.getInvestigations().iterator().next();
        assertNotNull(experiment.getDescription());
        assertTrue(experiment.getDescription().startsWith("&lt;&gt;Gene expression of TK6"));
        assertFalse(experiment.getDescription().contains("<"));
        assertEquals(8, experiment.getExperimentContacts().size());
        assertEquals(1, experiment.getExperimentDesignTypes().size());
        assertEquals("genetic_modification_design", experiment.getExperimentDesignTypes().iterator().next().getValue());
        assertEquals(6, experiment.getSources().size());
        assertEquals(6, experiment.getSamples().size());
        assertEquals(6, experiment.getExtracts().size());
        assertEquals(6, experiment.getLabeledExtracts().size());
        assertEquals(6, experiment.getHybridizations().size());
        for (final Hybridization hyb : experiment.getHybridizations()) {
            assertNull(hyb.getArray().getDesign().getName());
        }
    }

    @Test
    public void testSpecificationDocumentsWithDerivedData() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.DERIVED_DATA_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.DERIVED_DATA_INPUT_SET);
        final CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        final Experiment experiment = result.getInvestigations().iterator().next();
        assertNotNull(experiment.getDescription());
        assertTrue(experiment.getDescription().startsWith("Gene expression of TK6"));
        assertEquals(8, experiment.getExperimentContacts().size());
        assertEquals(1, experiment.getExperimentDesignTypes().size());
        assertEquals("genetic_modification_design", experiment.getExperimentDesignTypes().iterator().next().getValue());
        assertEquals(1, experiment.getSources().size());
        assertEquals(1, experiment.getSamples().size());
        assertEquals(1, experiment.getExtracts().size());
        assertEquals(1, experiment.getLabeledExtracts().size());
        assertEquals(1, experiment.getHybridizations().size());
        final Hybridization hyb = experiment.getHybridizations().iterator().next();
        assertNotNull(hyb.getArray().getDesign());
        assertEquals(2, hyb.getDerivedDataCollection().size());
        final Iterator<DerivedArrayData> derivedArrayDataIterator = hyb.getDerivedDataCollection().iterator();
        final DerivedArrayData derivedData = derivedArrayDataIterator.next();
        final DerivedArrayData derivedData2 = derivedArrayDataIterator.next();
        assertEquals(1, derivedData.getDerivedFromArrayDataCollection().size());
        assertEquals(1, derivedData2.getDerivedFromArrayDataCollection().size());
        final AbstractArrayData derivedFrom = derivedData.getDerivedFromArrayDataCollection().iterator().next();
        final AbstractArrayData derivedFrom2 = derivedData2.getDerivedFromArrayDataCollection().iterator().next();

        // not sure which order the derived from objects are returned - one derived data is derived from
        // raw data and the other is derived from the other derived data, but we don't know which derived data
        // is first
        if (derivedFrom instanceof RawArrayData) {
            assertEquals(hyb.getRawDataCollection().iterator().next(), derivedFrom);
            assertEquals(derivedData, derivedFrom2);
        } else {
            assertEquals(hyb.getRawDataCollection().iterator().next(), derivedFrom2);
            assertEquals(derivedData2, derivedFrom);
        }

    }

    @Test
    public void testTcgaBroadDocuments() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.TCGA_BROAD_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.TCGA_BROAD_INPUT_SET);
        final CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);

        final Set<Term> terms = new HashSet<Term>();
        terms.addAll(result.getTerms());
        assertEquals(10, terms.size());

        assertEquals(1, result.getInvestigations().size());
        final Experiment investigation = result.getInvestigations().iterator().next();
        final IdfDocument idf = docSet.getIdfDocuments().iterator().next();
        assertEquals(idf.getInvestigation().getTitle(), investigation.getTitle());

        checkTcgaBroadBioMaterials(investigation);
        checkTcgaBroadHybridizations(investigation);
    }

    private void checkTcgaBroadHybridizations(Experiment investigation) {
        assertEquals(1, investigation.getArrayDesigns().size());
        final ArrayDesign arrayDesign = investigation.getArrayDesigns().iterator().next();
        for (final LabeledExtract labeledExtract : investigation.getLabeledExtracts()) {
            final Hybridization hybridization = labeledExtract.getHybridizations().iterator().next();
            assertEquals(arrayDesign, hybridization.getArray().getDesign());
            final RawArrayData celData = hybridization.getRawDataCollection().iterator().next();
            assertEquals(celData.getDataFile().getName(), celData.getName());
        }
        final Set<Hybridization> hybridizations = investigation.getHybridizations();
        assertEquals(26, hybridizations.size());
        checkHybridizationsHaveRawDataAndFiles(hybridizations);
    }

    private void checkTcgaBroadBioMaterials(Experiment investigation) {
        assertEquals(26, investigation.getSources().size());
        assertEquals(26, investigation.getSamples().size());
        assertEquals(26, investigation.getExtracts().size());
        assertEquals(26, investigation.getLabeledExtracts().size());
    }

    @Test
    public void testGskTestDocuments() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.GSK_TEST_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.GSK_TEST_INPUT_SET);
        final CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        final Experiment experiment = result.getInvestigations().iterator().next();
        assertEquals(6, experiment.getSources().size());
        assertEquals(6, experiment.getSamples().size());
        assertEquals(6, experiment.getExtracts().size());
        assertEquals(6, experiment.getLabeledExtracts().size());
        assertEquals(6, experiment.getHybridizations().size());
    }

    @Test
    public void testTranslateRawArrayDataWithoutDerivedData() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.PERFORMANCE_TEST_10_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.PERFORMANCE_TEST_10_INPUT_SET);
        final CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        assertEquals(1, result.getInvestigations().size());
        final Experiment investigation = result.getInvestigations().iterator().next();
        assertEquals(10, investigation.getHybridizations().size());
        checkHybridizationsHaveRawDataAndFiles(investigation.getHybridizations());
    }

    @Test
    public void testTranslateDataMatrixCopyNumberData() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.GOOD_DATA_MATRIX_COPY_NUMBER_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.GOOD_DATA_MATRIX_COPY_NUMBER_INPUT_SET);
        final CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        assertEquals(1, result.getInvestigations().size());
        final Experiment investigation = result.getInvestigations().iterator().next();
        final Set<Hybridization> hybridizations = investigation.getHybridizations();
        assertEquals(3, hybridizations.size());
        for (final Hybridization hybridization : hybridizations) {
            assertTrue(hybridization.getRawDataCollection().isEmpty());
            assertFalse(hybridization.getDerivedDataCollection().isEmpty());
            assertNotNull(hybridization.getDerivedDataCollection().iterator().next().getDataFile());
        }
    }

    @Test
    public void testTranslatePersonsWithNullAffiliation() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE
        .parse(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);
        docSet.getIdfDocuments().iterator().next().getInvestigation().getPersons().iterator().next()
        .setAffiliation(null);
        final CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        final Experiment experiment = result.getInvestigations().iterator().next();
        for (final ExperimentContact contact : experiment.getExperimentContacts()) {
            for (final Organization organization : contact.getContact().getAffiliations()) {
                assertNotNull(organization);
            }
        }
    }

    @Test
    public void testTranslateBioMaterialSourceDescriptions() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE
        .parse(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);
        final SdrfDocument sdrfDocument = docSet.getSdrfDocuments().iterator().next();
        addDescriptionToBioMaterials(sdrfDocument);
        final CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        final Experiment experiment = result.getInvestigations().iterator().next();
        checkDescription(experiment.getSources(), "Source description");
        checkDescription(experiment.getSamples(), "Sample description");
        checkDescription(experiment.getExtracts(), "Extract description");
        checkDescription(experiment.getLabeledExtracts(), "LabeledExtract description");
    }

    private void checkDescription(Set<? extends gov.nih.nci.caarray.domain.sample.AbstractBioMaterial> materials,
            String description) {
        for (final gov.nih.nci.caarray.domain.sample.AbstractBioMaterial material : materials) {
            assertEquals(description, material.getDescription());
        }
    }

    private void addDescriptionToBioMaterials(SdrfDocument sdrfDocument) {
        addDescriptionToBioMaterials(sdrfDocument.getAllSources(), "Source description");
        addDescriptionToBioMaterials(sdrfDocument.getAllSamples(), "Sample description");
        addDescriptionToBioMaterials(sdrfDocument.getAllExtracts(), "Extract description");
        addDescriptionToBioMaterials(sdrfDocument.getAllLabeledExtracts(), "LabeledExtract description");
    }

    private void addDescriptionToBioMaterials(List<? extends AbstractBioMaterial> materials, String description) {
        for (final AbstractBioMaterial material : materials) {
            material.setDescription(description);
        }
    }

    private void checkHybridizationsHaveRawDataAndFiles(Set<Hybridization> hybridizations) {
        for (final Hybridization hybridization : hybridizations) {
            assertFalse(hybridization.getRawDataCollection().isEmpty());
            assertNotNull(hybridization.getRawDataCollection().iterator().next().getDataFile());
        }
    }

    @Test
    public void testTranslateValid_Feature13141() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.VALID_FEATURE_13141_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.VALID_FEATURE_13141_INPUT_SET);
        final ValidationResult vResult = this.translator.validate(docSet, fileSet);
        assertTrue(vResult.getMessages().isEmpty());
        final CaArrayTranslationResult tResult = this.translator.translate(docSet, fileSet);
        assertEquals(1, tResult.getInvestigations().size());
        final Experiment e = tResult.getInvestigations().iterator().next();
        assertEquals(6, e.getSamples().size());
        for (final Sample s : e.getSamples()) {
            assertNotNull(s.getExternalId());
        }
        boolean wasFound = false;
        for (final Source source : e.getSources()) {
            if (!(StringUtils.isEmpty(source.getDescription()))) {
                assertEquals(2000, source.getDescription().length());
                wasFound = true;
            }
        }
        assertTrue("the 2000 char source description was not found.", wasFound);
        final Set<Publication> publicationsSet = e.getPublications();
        assertEquals(1, publicationsSet.size());
        final Publication publication = publicationsSet.iterator().next();
        assertEquals(2000, publication.getAuthors().length());
    }

    @Test
    public void testTranslateInvalid_Feature13141() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.INVALID_FEATURE_13141_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.INVALID_FEATURE_13141_INPUT_SET);
        final ValidationResult vResult = this.translator.validate(docSet, fileSet);
        assertFalse(vResult.getMessages().isEmpty());
        assertEquals(3, vResult.getMessages(ValidationMessage.Type.WARNING).size());
        final String string = "[ExternalSampleId] value '%s"
            + "' is referenced multiple times (ExternalSampleId must be unique). "
            + "Existing value will be reused.";
        assertEquals(String.format(string, "345"), vResult.getMessages(ValidationMessage.Type.WARNING).get(0)
                .getMessage());
        assertEquals(String.format(string, "234"), vResult.getMessages(ValidationMessage.Type.WARNING).get(1)
                .getMessage());
        assertEquals(String.format(string, "123"), vResult.getMessages(ValidationMessage.Type.WARNING).get(2)
                .getMessage());
        final CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);

        assertEquals(1, result.getInvestigations().size());
        final Experiment e = result.getInvestigations().iterator().next();
        assertEquals(6, e.getSamples().size());
        for (final Sample s : e.getSamples()) {
            assertNotNull(s.getExternalId());
        }
    }

    @Test
    public void testValidateInvalidDuplicateTermSources() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets
        .getFileSet(TestMageTabSets.INVALID_DUPLICATE_TERM_SOURCES_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE
        .parse(TestMageTabSets.INVALID_DUPLICATE_TERM_SOURCES_INPUT_SET);
        final ValidationResult validationResult = this.translator.validate(docSet, fileSet);
        assertFalse(validationResult.getMessages().isEmpty());
        assertEquals(7, validationResult.getMessages(ValidationMessage.Type.ERROR).size());
        final String prototypeString = "Redundant term source named '%s'. Term sources cannot have "
            + "the same URL unless they have different versions, even if " + "their names are different.";
        assertEquals(String.format(prototypeString, "LM_3"), validationResult.getMessages(ValidationMessage.Type.ERROR)
                .get(0).getMessage());
        assertEquals(String.format(prototypeString, "LM_2"), validationResult.getMessages(ValidationMessage.Type.ERROR)
                .get(1).getMessage());
        assertEquals(String.format(prototypeString, "LM_1"), validationResult.getMessages(ValidationMessage.Type.ERROR)
                .get(2).getMessage());
        assertEquals(String.format(prototypeString, "LM"), validationResult.getMessages(ValidationMessage.Type.ERROR)
                .get(3).getMessage());
        assertEquals(String.format(prototypeString, "FOO2"), validationResult.getMessages(ValidationMessage.Type.ERROR)
                .get(4).getMessage());
        assertEquals(String.format(prototypeString, "FOO"), validationResult.getMessages(ValidationMessage.Type.ERROR)
                .get(5).getMessage());
        assertEquals("Duplicate term source name 'FOO'.", validationResult.getMessages(ValidationMessage.Type.ERROR)
                .get(6).getMessage());
    }

    @Test
    public void testValidateExperimentDescriptionLength() throws Exception {
        final MageTabFileSet validMageTabFileSet = new MageTabFileSet();
        validMageTabFileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.VALID_DESCRIPTION_LENGTH_IDF));
        final MageTabDocumentSet validDocSet = MageTabParser.INSTANCE.parse(validMageTabFileSet);
        final ValidationResult goodValidationResult = this.translator.validate(validDocSet,
                TestMageTabSets.getFileSet(validMageTabFileSet));
        assertTrue(goodValidationResult.getMessages().isEmpty());

        final MageTabFileSet invalidMageTabFileSet = new MageTabFileSet();
        invalidMageTabFileSet.addIdf(new JavaIOFileRef(MageTabDataFiles.INVALID_DESCRIPTION_LENGTH_IDF));
        final MageTabDocumentSet invalidDocSet = MageTabParser.INSTANCE.parse(invalidMageTabFileSet);
        final ValidationResult badValidationResult = this.translator.validate(invalidDocSet,
                TestMageTabSets.getFileSet(invalidMageTabFileSet));
        assertFalse(badValidationResult.getMessages().isEmpty());
        assertEquals(1, badValidationResult.getMessages(ValidationMessage.Type.ERROR).size());
        assertEquals("The experiment description length of 2001 is greater than the allowed length of 2000.",
                badValidationResult.getMessages(ValidationMessage.Type.ERROR).get(0).getMessage());
    }

    @Test
    public void testExtendedFactorValues() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE
        .parse(TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET);
        final CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        final Experiment experiment = result.getInvestigations().iterator().next();
        assertEquals(3, experiment.getFactors().size());
        assertEquals(3, experiment.getSamples().size());
        assertEquals(3, experiment.getHybridizations().size());
        verifyExtendedFactorValuesSampleChars(experiment.getSampleByName("Sample A"), "123", 5f, null, "years",
                "tissue", "Name for: http://mged.sourceforge.net/ontologies/MGEDontology.php");
        verifyExtendedFactorValuesSampleChars(experiment.getSampleByName("Sample B"), "234", null, "a lot", "months",
                "tissue", "Name for: http://mged.sourceforge.net/ontologies/MGEDontology.php");
        verifyExtendedFactorValuesSampleChars(experiment.getSampleByName("Sample C"), "345", 2.2f, null, "days",
                "unknown", null);
        verifyExtendedFactorValuesSampleParams(experiment.getSampleByName("Sample A"), "foo", 4f, null, "kg",
                "planting", "Name for: http://mged.sourceforge.net/ontologies/MGEDontology.php");
        verifyExtendedFactorValuesSampleParams(experiment.getSampleByName("Sample B"), "baz", null, "4", null,
                "planting", "Name for: http://mged.sourceforge.net/ontologies/MGEDontology.php");
        verifyExtendedFactorValuesSampleParams(experiment.getSampleByName("Sample C"), "foo", null, "less", "mg",
                "nothing", null);
        verifyExtendedFactorValuesHyb(experiment.getHybridizationByName("Hyb A"), "123", 5f, null, "years", "tissue",
        "Name for: http://mged.sourceforge.net/ontologies/MGEDontology.php");
        verifyExtendedFactorValuesHyb(experiment.getHybridizationByName("Hyb B"), "234", null, "a lot", "months",
                "tissue", "Name for: http://mged.sourceforge.net/ontologies/MGEDontology.php");
        verifyExtendedFactorValuesHyb(experiment.getHybridizationByName("Hyb C"), "345", null, "2.2", null, "unknown",
                null);
    }

    @Test
    public void testRenamingTermSources() throws Exception {
        final CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.RENAMING_TERM_SOURCES_INPUT_SET);
        final MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.RENAMING_TERM_SOURCES_INPUT_SET);
        final CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        boolean mgedAliasTermSourceWasFound = false;
        for (final Term term : result.getTerms()) {
            final TermSource termSource = term.getSource();
            System.out.println("termSource.getName() =" + termSource.getName() + "=");
            if (termSource.getName().equals("Name for: http://mged.sourceforge.net/ontologies/MGEDontology.php")) {
                mgedAliasTermSourceWasFound = true;
            }
        }
        assertTrue("The MGED alias TermSource name was not found.", mgedAliasTermSourceWasFound);
    }

    private void verifyExtendedFactorValuesHyb(Hybridization hyb, String fv1Value, Float fv2Num, String fv2String,
            String fv2Unit, String fv3Value, String fv3ts) {
        assertEquals(3, hyb.getFactorValues().size());
        final UserDefinedFactorValue fv1 = (UserDefinedFactorValue) hyb.getFactorValue("ExternalSampleId");
        assertEquals(fv1Value, fv1.getValue());
        assertNull(fv1.getUnit());
        if (fv2Num != null) {
            final MeasurementFactorValue fv2 = (MeasurementFactorValue) hyb.getFactorValue("Age");
            assertEquals(fv2Num, fv2.getValue());
            assertEquals(fv2Unit, fv2Unit == null ? fv2.getUnit() : fv2.getUnit().getValue());
            if (fv2Unit != null) {
                assertEquals("Name for: http://mged.sourceforge.net/ontologies/MGEDontology.php", fv2.getUnit()
                        .getSource().getName());
            }
        } else {
            final UserDefinedFactorValue fv2 = (UserDefinedFactorValue) hyb.getFactorValue("Age");
            assertEquals(fv2String, fv2.getValue());
            assertEquals(fv2Unit, fv2Unit == null ? fv2.getUnit() : fv2.getUnit().getValue());
            if (fv2Unit != null) {
                assertEquals("Name for: http://mged.sourceforge.net/ontologies/MGEDontology.php", fv2.getUnit()
                        .getSource().getName());
            }
        }
        if (fv3ts != null) {
            final TermBasedFactorValue fv3 = (TermBasedFactorValue) hyb.getFactorValue("MaterialType");
            assertEquals(fv3Value, fv3.getTerm().getValue());
            assertEquals(fv3ts, fv3.getTerm().getSource().getName());
            assertNull(fv3.getUnit());
        } else {
            final UserDefinedFactorValue fv3 = (UserDefinedFactorValue) hyb.getFactorValue("MaterialType");
            assertEquals(fv3Value, fv3.getValue());
            assertNull(fv3.getUnit());
        }
    }

    private void verifyExtendedFactorValuesSampleChars(Sample s, String c1Value, Float c2Num, String c2String,
            String c2Unit, String c3Value, String c3ts) {
        assertEquals(2, s.getCharacteristics().size());
        assertEquals(c1Value, s.getExternalId());
        if (c2Num != null) {
            final MeasurementCharacteristic c2 = (MeasurementCharacteristic) s.getCharacteristic("Age");
            assertEquals(c2Num, c2.getValue());
            assertEquals(c2Unit, c2.getUnit().getValue());
            assertEquals("Name for: http://mged.sourceforge.net/ontologies/MGEDontology.php", c2.getUnit().getSource()
                    .getName());
        } else {
            final UserDefinedCharacteristic c2 = (UserDefinedCharacteristic) s.getCharacteristic("Age");
            assertEquals(c2String, c2.getValue());
            assertEquals(c2Unit, c2.getUnit().getValue());
            assertEquals("Name for: http://mged.sourceforge.net/ontologies/MGEDontology.php", c2.getUnit().getSource()
                    .getName());
        }
        if (c3ts != null) {
            final TermBasedCharacteristic c3 = (TermBasedCharacteristic) s.getCharacteristic("MaterialType");
            assertEquals(c3Value, c3.getTerm().getValue());
            assertEquals(c3ts, c3.getTerm().getSource().getName());
            assertNull(c3.getUnit());
        } else {
            final UserDefinedCharacteristic c3 = (UserDefinedCharacteristic) s.getCharacteristic("MaterialType");
            assertEquals(c3Value, c3.getValue());
            assertNull(c3.getUnit());
        }
    }

    private void verifyExtendedFactorValuesSampleParams(Sample s, String pv1Value, Float pv2Num, String pv2String,
            String pv2Unit, String pv3Value, String pv3ts) {
        assertEquals(1, s.getProtocolApplications().size());
        final ProtocolApplication pa = s.getProtocolApplications().get(0);
        assertEquals("Dan1", pa.getProtocol().getName());
        assertEquals(3, pa.getValues().size());
        final UserDefinedParameterValue pv1 = (UserDefinedParameterValue) pa.getValue("p1");
        assertNotNull(pv1);
        assertEquals(pv1Value, pv1.getValue());
        assertNull(pv1.getUnit());
        if (pv2Num != null) {
            final MeasurementParameterValue pv2 = (MeasurementParameterValue) pa.getValue("p3");
            assertEquals(pv2Num, pv2.getValue());
            assertEquals(pv2Unit, pv2Unit == null ? pv2.getUnit() : pv2.getUnit().getValue());
            if (pv2Unit != null) {
                assertEquals("Name for: http://mged.sourceforge.net/ontologies/MGEDontology.php", pv2.getUnit()
                        .getSource().getName());
            }
        } else {
            final UserDefinedParameterValue pv2 = (UserDefinedParameterValue) pa.getValue("p3");
            assertEquals(pv2String, pv2.getValue());
            assertEquals(pv2Unit, pv2Unit == null ? pv2.getUnit() : pv2.getUnit().getValue());
            if (pv2Unit != null) {
                assertEquals("Name for: http://mged.sourceforge.net/ontologies/MGEDontology.php", pv2.getUnit()
                        .getSource().getName());
            }
        }
        if (pv3ts != null) {
            final TermBasedParameterValue pv3 = (TermBasedParameterValue) pa.getValue("p2");
            assertEquals(pv3Value, pv3.getTerm().getValue());
            assertEquals(pv3ts, pv3.getTerm().getSource().getName());
            assertNull(pv3.getUnit());
        } else {
            final UserDefinedParameterValue pv3 = (UserDefinedParameterValue) pa.getValue("p2");
            assertEquals(pv3Value, pv3.getValue());
            assertNull(pv3.getUnit());
        }
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {
        /**
         * {@inheritDoc}
         */
        @Override
        public ArrayDao getArrayDao() {
            return new LocalArrayDaoStub();
        }

    }

    private static class LocalDaoFactoryStub2 extends DaoFactoryStub {
        /**
         * {@inheritDoc}
         */
        @Override
        public ArrayDao getArrayDao() {
            return new LocalArrayDaoStub2();
        }

    }

    private static class LocalArrayDaoStub extends ArrayDaoStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> List<T> queryEntityByExample(ExampleSearchCriteria<T> criteria,
                Order... orders) {
            final T entityToMatch = criteria.getExample();
            if (entityToMatch instanceof ArrayDesign) {
                final ArrayDesign arrayDesign = (ArrayDesign) entityToMatch;
                if ("URN:LSID:Affymetrix.com:PhysicalArrayDesign:Test3".equals(arrayDesign.getLsid())) {
                    final ArrayList list = new ArrayList();
                    list.add(arrayDesign);
                    return list;
                }
            }
            return super.queryEntityByExample(criteria, orders);
        }
    }

    private static class LocalArrayDaoStub2 extends ArrayDaoStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> List<T> queryEntityByExample(ExampleSearchCriteria<T> criteria,
                Order... orders) {
            final T entityToMatch = criteria.getExample();
            if (entityToMatch instanceof ArrayDesign) {
                final ArrayDesign arrayDesign = (ArrayDesign) entityToMatch;
                if ("URN:LSID:Affymetrix.com:PhysicalArrayDesign:HT_HG-U133A".equals(arrayDesign.getLsid())) {
                    final ArrayList list = new ArrayList();
                    list.add(arrayDesign);
                    return list;
                }
            }
            return super.queryEntityByExample(criteria, orders);
        }
    }
}
