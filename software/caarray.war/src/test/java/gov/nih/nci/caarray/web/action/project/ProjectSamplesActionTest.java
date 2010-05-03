/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
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
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.FileDaoTest;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.AbstractDownloadTest;
import gov.nih.nci.caarray.web.helper.DownloadHelper;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 *
 */
public class ProjectSamplesActionTest extends AbstractDownloadTest {
    private final ProjectSamplesAction action = new ProjectSamplesAction();

    private static Sample DUMMY_SAMPLE = new Sample();
    private static Source DUMMY_SOURCE = new Source();

    @Before
    @SuppressWarnings("deprecation")
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new LocalProjectManagementService());
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        DUMMY_SAMPLE = new Sample();
        DUMMY_SAMPLE.setId(1L);
        DUMMY_SAMPLE.setName("Dummy Sample");
        DUMMY_SOURCE = new Source();
        DUMMY_SOURCE.setName("Dummy Source");
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testPrepare() throws Exception {
        // no current sample id
        action.prepare();
        assertNull(action.getCurrentSample().getId());

        // valid current sample id
        Sample sample = new Sample();
        sample.setId(1L);
        sample.setName("sample1");
        action.setCurrentSample(sample);
        action.prepare();
        assertEquals(DUMMY_SAMPLE, action.getCurrentSample());

        //valid sample external id
        sample = new Sample();
        sample.setName("sample2");
        sample.setExternalId("abc");
        action.setCurrentSample(sample);
        action.prepare();
        assertEquals("abc", action.getCurrentSample().getExternalId());

        // invalid current sample id
        sample = new Sample();
        sample.setId(2L);
        sample.setName("sample3");
        action.setCurrentSample(sample);
        try {
            action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (PermissionDeniedException pde) {
            assertTrue(pde.getMessage().endsWith("id 2"));
        }
    }

    @Test
    public void testPrepareUsingExternalSampleId() throws Exception {
        // no current sample id
        action.prepare();
        assertNull(action.getCurrentSample().getId());

        //valid sample external id
        Sample sample = new Sample();
        sample.setName("sample4");
        sample.setExternalId("abc");
        action.setCurrentSample(sample);
        action.prepare();
        assertEquals("abc", action.getCurrentSample().getExternalId());

        // invalid current sample id
        sample = new Sample();
        sample.setName("sample5");
        sample.setExternalId("def");
        action.setCurrentSample(sample);
        try {
            action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (PermissionDeniedException pde) {
            assertTrue(pde.getMessage().endsWith("external id def"));
        }
    }

    @Test
    public void testDownload() throws Exception {
        assertEquals("noSampleData", action.download());

        FileAccessServiceStub fas = new FileAccessServiceStub();
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fas));
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        fas.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);

        Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        Sample s = new Sample();
        Extract e = new Extract();
        s.getExtracts().add(e);
        LabeledExtract le = new LabeledExtract();
        e.getLabeledExtracts().add(le);
        Hybridization h = new Hybridization();
        le.getHybridizations().add(h);
        RawArrayData raw = new RawArrayData();
        h.addArrayData(raw);
        DerivedArrayData derived = new DerivedArrayData();
        h.getDerivedDataCollection().add(derived);
        CaArrayFile rawFile = new CaArrayFile();
        FileDaoTest.writeContents(rawFile, "");
        rawFile.setName("missing_term_source.idf");
        raw.setDataFile(rawFile);
        CaArrayFile derivedFile = new CaArrayFile();
        FileDaoTest.writeContents(derivedFile, "");
        derivedFile.setName("missing_term_source.sdrf");
        derived.setDataFile(derivedFile);

        action.setCurrentSample(s);
        action.setProject(p);
        List<CaArrayFile> files = new ArrayList<CaArrayFile>(s.getAllDataFiles());
        Collections.sort(files, DownloadHelper.CAARRAYFILE_NAME_COMPARATOR_INSTANCE);
        assertEquals(2, files.size());
        assertEquals("missing_term_source.idf", files.get(0).getName());
        assertEquals("missing_term_source.sdrf", files.get(1).getName());

        action.download();
        assertEquals("application/zip", mockResponse.getContentType());
        assertEquals("filename=\"caArray_test_files.zip\"", mockResponse.getHeader("Content-disposition"));

        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(mockResponse.getContentAsByteArray()));
        ZipEntry ze = zis.getNextEntry();
        assertNotNull(ze);
        assertEquals("missing_term_source.idf", ze.getName());
        ze = zis.getNextEntry();
        assertNotNull(ze);
        assertEquals("missing_term_source.sdrf", ze.getName());
        assertNull(zis.getNextEntry());
        IOUtils.closeQuietly(zis);
    }

    @Test
    public void testCopy() {
        action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals("list", action.copy());
    }

    @Test
    public void testSearchForAssociationValues() {
        assertEquals("associationValues", action.searchForAssociationValues());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSave() {
        // save new
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.created"));

        // update associations
        Source toAdd = new Source();
        toAdd.setName("source1_to_add");
        List<Source> addList = new ArrayList<Source>();
        addList.add(toAdd);
        action.setItemsToAssociate(addList);
        Source toRemove = new Source();
        toRemove.setName("source_to_remove");
        toRemove.getSamples().add(DUMMY_SAMPLE);
        List<Source> removeList = new ArrayList<Source>();
        removeList.add(toRemove);
        action.setItemsToRemove(removeList);

        // protocols
        ProtocolApplication pa = new ProtocolApplication();
        Protocol p = new Protocol();
        p.setId(1L);
        p.setName("protocol1");
        pa.setProtocol(p);
        DUMMY_SAMPLE.addProtocolApplication(pa);

        Protocol p2 = new Protocol();
        p2.setId(2L);
        p2.setName("protocol2");
        List<Protocol> protocols = new ArrayList<Protocol>();
        protocols.add(p2);
        action.setSelectedProtocols(protocols);

        // update existing sample
        action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertTrue(toAdd.getSamples().contains(DUMMY_SAMPLE));
        assertFalse(toRemove.getSamples().contains(DUMMY_SAMPLE));
        assertEquals(p2, DUMMY_SAMPLE.getProtocolApplications().get(0).getProtocol());

        // add another protocol application
        Protocol p3 = new Protocol();
        p3.setId(3L);
        p3.setName("protocol3");
        action.getSelectedProtocols().add(p3);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertEquals(2, DUMMY_SAMPLE.getProtocolApplications().size());
        assertTrue(DUMMY_SAMPLE.getProtocolApplications().get(0).getProtocol().equals(p2)
                || DUMMY_SAMPLE.getProtocolApplications().get(0).getProtocol().equals(p3));
        assertTrue(DUMMY_SAMPLE.getProtocolApplications().get(1).getProtocol().equals(p2)
                || DUMMY_SAMPLE.getProtocolApplications().get(1).getProtocol().equals(p3));

        // take away a PA
        action.getSelectedProtocols().remove(p2);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));
        assertEquals(1, DUMMY_SAMPLE.getProtocolApplications().size());
        assertEquals(p3, DUMMY_SAMPLE.getProtocolApplications().get(0).getProtocol());

    }

    @Test
    public void testSaveDuplicate() {

        action.getProject().getExperiment().getSamples().add(DUMMY_SAMPLE);

        // add a sample, with duplicate name
        Sample sam = new Sample();
        sam.setName(DUMMY_SAMPLE.getName());
        sam.getSources().add(DUMMY_SOURCE);

        // try to save dup.
        action.setCurrentSample(sam);
        assertEquals(Action.INPUT, action.save());
        assertEquals(1, action.getFieldErrors().size());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testEditSampleRenameAsDuplicate() {

        action.getProject().getExperiment().getSamples().add(DUMMY_SAMPLE);

        // add another sample, with valid name
        Sample sam = new Sample();
        sam.setId(2L);
        sam.setName("test_name");
        sam.getSources().add(DUMMY_SOURCE);

        action.getProject().getExperiment().getSamples().add(sam);


        // try to edit sam w/ valid name.
        sam.setName("name_is_valid");
        action.setCurrentSample(sam);
        assertEquals(ProjectTabAction.RELOAD_PROJECT_RESULT, action.save());
        assertTrue(ActionHelper.getMessages().contains("experiment.items.updated"));

        // try to edit sam with invalid name
        sam.setName(DUMMY_SAMPLE.getName());
        action.setCurrentSample(sam);
        assertEquals(Action.INPUT, action.save());
        assertEquals(1, action.getFieldErrors().size());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testEdit() {
        ProtocolApplication pa = new ProtocolApplication();
        Protocol p = new Protocol();
        p.setName("protocol1");
        pa.setProtocol(p);
        DUMMY_SAMPLE.getProtocolApplications().clear();
        DUMMY_SAMPLE.getProtocolApplications().add(pa);
        action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals(Action.INPUT, action.edit());
        assertEquals(1, action.getSelectedProtocols().size());
        assertEquals(p, action.getSelectedProtocols().get(0));

        ProtocolApplication pa2 = new ProtocolApplication();
        Protocol p2 = new Protocol();
        p2.setName("protocol2");
        pa2.setProtocol(p2);
        DUMMY_SAMPLE.getProtocolApplications().add(pa2);
        action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals(Action.INPUT, action.edit());
        assertEquals(2, action.getSelectedProtocols().size());
        assertEquals(p2, action.getSelectedProtocols().get(1));
    }

    public void testDelete() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        DUMMY_SOURCE.getSamples().add(DUMMY_SAMPLE);
        DUMMY_SAMPLE.getSources().add(DUMMY_SOURCE);
        Project p = new Project();
        setProjectForExperiment(p.getExperiment(), p);
        p.getPublicProfile().setSecurityLevel(SecurityLevel.READ_SELECTIVE);
        p.getPublicProfile().getSampleSecurityLevels().put(DUMMY_SAMPLE, SampleSecurityLevel.READ);
        DUMMY_SAMPLE.setExperiment(p.getExperiment());
        action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals("list", action.delete());
        assertFalse(DUMMY_SOURCE.getSamples().contains(DUMMY_SAMPLE));
        assertTrue(p.getPublicProfile().getSampleSecurityLevels().isEmpty());

        DUMMY_SAMPLE.getExtracts().add(new Extract());
        action.setCurrentSample(DUMMY_SAMPLE);
        assertEquals("list", action.delete());
        assertTrue(ActionHelper.getMessages().contains("experiment.annotations.cantdelete"));
    }

    private void setProjectForExperiment(Experiment e, Project p) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method m = e.getClass().getDeclaredMethod("setProject", Project.class);
        m.setAccessible(true);
        m.invoke(e, p);
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(Sample.class) && entityId.equals(1L)) {
                return (T)DUMMY_SAMPLE;
            }
            return null;
        }
    }

    private static class LocalProjectManagementService extends ProjectManagementServiceStub {
        @Override
        public <T extends AbstractBioMaterial> T getBiomaterialByExternalId(Project project, String externalId,
                Class<T> biomaterialClass) {
            T bmByExternalId = super.getBiomaterialByExternalId(project, externalId, biomaterialClass);
            if ("abc".equals(externalId)) {
                return bmByExternalId;
            }
            return null;
        }
    }
}
