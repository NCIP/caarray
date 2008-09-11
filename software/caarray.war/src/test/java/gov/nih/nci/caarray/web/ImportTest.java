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
package gov.nih.nci.caarray.web;

import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.file.FileManagementServiceStub;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.action.project.ProjectFilesAction;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

/**
 * @author John Hedden
 *
 */
public class ImportTest extends AbstractCaarrayTest {

    private final ProjectFilesAction action = new ProjectFilesAction();
    private final LocalProjectManagementServiceStub projectServiceStub = new LocalProjectManagementServiceStub();
    private final LocalFileManagementServiceStub fileManagementStub = new LocalFileManagementServiceStub();

    /**
     * setup.
     */
    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, this.projectServiceStub);
        locatorStub.addLookup(FileManagementService.JNDI_NAME, this.fileManagementStub);
        loadTestProject();
    }

    @SuppressWarnings("deprecation")
    private void loadTestProject() {
        final Project project = new Project();;
        CaArrayFile file1 = new CaArrayFile();
        file1.setProject(project);
        file1.setFileStatus(FileStatus.UPLOADED);
        file1.setName("file1.ext");
        file1.setFileType(FileType.AFFYMETRIX_CEL);
        file1.setId(Long.valueOf(1));
        CaArrayFile file2 = new CaArrayFile();
        file2.setFileStatus(FileStatus.UPLOADED);
        file2.setName("file2.ext");
        file2.setFileType(FileType.AFFYMETRIX_CEL);
        file2.setProject(project);
        CaArrayFile file3 = new CaArrayFile();
        file3.setFileStatus(FileStatus.UPLOADED);
        file3.setName("file3.ext");
        file3.setFileType(FileType.AFFYMETRIX_CEL);
        file3.setProject(project);
        project.getFiles().add(file1);
        project.getFiles().add(file2);
        project.getFiles().add(file3);

        this.action.setProject(project);

        MockHttpSession session = new MockHttpSession ();
        session.setAttribute("myProject", project);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        ServletActionContext.setRequest(request);

    }

    /**
     * test import file.
     * @throws Exception Exception
     */
    @Test
    public void testImportFile() throws Exception {
//        MockHttpSession session = new MockHttpSession ();
//        MockHttpServletRequest request = new MockHttpServletRequest();
//
//        Project myProject = action.getProject();
//        assertNotNull(myProject);
//
//        session.setAttribute("myProject", myProject);
//
//        request.setSession(session);
//        request.setParameter("files:1:selected", "files:1:selected");
//        request.setParameter("files:1:notselected", "files:1:notselected");
//        request.setParameter("notfiles:1:selected", "notfiles:1:selected");
//        ServletActionContext.setRequest(request);
//
//        action.importFile();
    }

    /**
     * local stub
     */
    private static class LocalProjectManagementServiceStub extends ProjectManagementServiceStub {
    }

    /**
     * local stub.
     */
    private static class LocalFileManagementServiceStub extends FileManagementServiceStub {
        boolean calledImportFiles;

        @Override
        public void importFiles(Project targetProject, CaArrayFileSet fileSet, DataImportOptions options) {
            super.importFiles(targetProject, fileSet, options);
            this.calledImportFiles = true;
        }
    }

}
