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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;

/**
 * @author Winston Cheng
 */
@SuppressWarnings("PMD")
public class ProjectSourcesActionTest extends AbstractDownloadTest {
    private final ProjectSourcesAction action = new ProjectSourcesAction();
    private static Source DUMMY_SOURCE;
    private static int NUM_SOURCES = 2;

    @Before
    @SuppressWarnings("deprecation")
    public void setUp() throws Exception {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new ProjectManagementServiceStub());
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        DUMMY_SOURCE = new Source();
        DUMMY_SOURCE.setId(1L);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testPrepare() throws Exception {
        // no current source id
        this.action.prepare();
        assertNull(this.action.getCurrentSource().getId());

        // valid current source id
        Source source = new Source();
        source.setId(1L);
        this.action.setCurrentSource(source);
        this.action.prepare();
        assertEquals(DUMMY_SOURCE, this.action.getCurrentSource());

        // invalid current source id
        source = new Source();
        source.setId(2L);
        this.action.setCurrentSource(source);
        try {
            this.action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (final PermissionDeniedException pde) {
        }
    }

    @Test
    public void testCopy() {
        this.action.setCurrentSource(DUMMY_SOURCE);
        assertEquals("list", this.action.copy());
    }

    @Test
    public void testDelete() {
        assertEquals("list", this.action.delete());
        DUMMY_SOURCE.getSamples().add(new Sample());
        this.action.setCurrentSource(DUMMY_SOURCE);
        assertEquals("list", this.action.delete());
        assertTrue(ActionHelper.getMessages().contains("experiment.annotations.cantdelete"));
    }

    @Test
    public void testDownload() throws Exception {
        assertEquals("noSourceData", this.action.download());

        final CaArrayFile rawFile = this.fasStub.add(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        final CaArrayFile derivedFile = this.fasStub.add(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);

        final Project p = new Project();
        p.getExperiment().setPublicIdentifier("test");
        final Source so = new Source();
        final Sample s1 = new Sample();
        so.getSamples().add(s1);
        final Sample s2 = new Sample();
        so.getSamples().add(s2);
        final Extract e = new Extract();
        s1.getExtracts().add(e);
        s2.getExtracts().add(e);
        final LabeledExtract le = new LabeledExtract();
        e.getLabeledExtracts().add(le);
        final Hybridization h = new Hybridization();
        le.getHybridizations().add(h);
        final RawArrayData raw = new RawArrayData();
        h.addArrayData(raw);
        final DerivedArrayData derived = new DerivedArrayData();
        h.getDerivedDataCollection().add(derived);
        raw.setDataFile(rawFile);
        derived.setDataFile(derivedFile);

        this.action.setCurrentSource(so);
        this.action.setProject(p);
        final List<CaArrayFile> files = new ArrayList<CaArrayFile>(so.getAllDataFiles());
        Collections.sort(files, DownloadHelper.CAARRAYFILE_NAME_COMPARATOR_INSTANCE);
        assertEquals(2, files.size());
        assertEquals("missing_term_source.idf", files.get(0).getName());
        assertEquals("missing_term_source.sdrf", files.get(1).getName());

        this.action.download();
        assertEquals("application/zip", this.mockResponse.getContentType());
        assertEquals("filename=\"caArray_test_files.zip\"", this.mockResponse.getHeader("Content-disposition"));

        final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(
                this.mockResponse.getContentAsByteArray()));
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
    public void testLoad() {
        final Project p = new Project();
        final Experiment e = new Experiment();
        p.setExperiment(e);
        for (int i = 0; i < NUM_SOURCES; i++) {
            e.getSources().add(new Source());
        }
        this.action.setProject(p);
        assertEquals("list", this.action.load());
        assertEquals(NUM_SOURCES, this.action.getPagedItems().getFullListSize());
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(Source.class) && entityId.equals(1L)) {
                return (T) DUMMY_SOURCE;
            }
            return null;
        }

        @Override
        public int collectionSize(Collection<? extends PersistentObject> collection) {
            return collection.size();
        }

    }
}
