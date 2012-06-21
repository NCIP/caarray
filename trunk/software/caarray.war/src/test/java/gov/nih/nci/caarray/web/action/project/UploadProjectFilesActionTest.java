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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.file.InvalidFileException;
import gov.nih.nci.caarray.application.project.FileUploadUtils;
import gov.nih.nci.caarray.application.project.FileWrapper;
import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.web.AbstractBaseStrutsTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.google.common.collect.Lists;
import com.opensymphony.xwork2.ActionSupport;

public class UploadProjectFilesActionTest extends AbstractBaseStrutsTest {

    private static final String ZIP_EXTENSION = ".zip";
    private static final String NON_ZIP_EXTENSION = ".idf";

    @Mock
    private FileUploadUtils mockUploadUtils;
    @Mock
    private FileDao fileDao;
    private UploadProjectFilesAction action = new UploadProjectFilesAction();
    Project project = new Project();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        action.setFileUploadUtils(mockUploadUtils);
        action.setFileDao(fileDao);
        project.getExperiment().setPublicIdentifier("publicId");
        CaArrayFile file = new CaArrayFile();
        file.setFileStatus(FileStatus.UPLOADED);
        file.setName("testfile1.cel");
        file.setProject(project);
        project.getFiles().add(file);
        action.setProject(project);
        ServletActionContext.setRequest(new MockHttpServletRequest());
    }

    @Test
    public void testUpload() throws Exception {
        File file = File.createTempFile("tmp", NON_ZIP_EXTENSION);

        List<File> files = new ArrayList<File>();
        List<String> fileNames = new ArrayList<String>();
        files.add(file);
        fileNames.add(file.getName());
        action.setUpload(files);
        action.setUploadFileName(fileNames);

        MockHttpServletResponse response = new MockHttpServletResponse();
        ServletActionContext.setResponse(response);

        assertEquals(ActionSupport.NONE, action.upload());
        JSONArray jsonResult = JSONArray.fromObject(response.getContentAsString());
        assertEquals(1, jsonResult.size());
        JSONObject map = jsonResult.getJSONObject(0);
        assertEquals(2, map.keySet().size());
        assertTrue(map.get("name").toString().contains(NON_ZIP_EXTENSION));
    }

    @Test
    public void testUpload_InvalidFileExceptionNoUnpack() throws Exception {
        String expectedErrorKey = "errors.uploadingErrorWithZip";
        doInvalidFileExceptionTest(expectedErrorKey, true);
    }

    private void doInvalidFileExceptionTest(String expectedErrorKey, boolean unpackSelected) throws Exception {
        String errorKey = "key";
        String errorMessage = "message";
        String errorUnpackingZipKey = "errors.unpackingErrorWithZip";
        File file = File.createTempFile("tmp", ZIP_EXTENSION);

        List<File> files = Lists.newArrayList(file);
        List<String> uploadFileNames = Lists.newArrayList(file.getName());
        action.setUpload(files);
        action.setUploadFileName(uploadFileNames);

        if (unpackSelected) {
            action.setSelectedFilesToUnpack(Lists.newArrayList(file.getName()));
        }

        when(
                mockUploadUtils.uploadFiles(any(Project.class), anyListOf(FileWrapper.class))).thenThrow(
                new InvalidFileException(file.getName(), errorKey, errorMessage));

        MockHttpServletResponse response = new MockHttpServletResponse();
        ServletActionContext.setResponse(response);

        assertEquals(ActionSupport.NONE, action.upload());
        JSONArray jsonResult = JSONArray.fromObject(response.getContentAsString());
        assertEquals(1, jsonResult.size());
        JSONObject map = jsonResult.getJSONObject(0);
        assertEquals(2, map.keySet().size());
        assertTrue(map.get("name").toString().contains(ZIP_EXTENSION));

        assertTrue(map.get("error").toString(), map.get("error").toString().contains(expectedErrorKey));
        assertTrue(map.get("error").toString(), map.get("error").toString().contains(errorUnpackingZipKey));
    }

    @Test
    public void testPartialUploadCheck() throws Exception {
        action.setChunkedFileName("testfile");
        action.setChunkedFileSize(1234L);
        CaArrayFile file = new CaArrayFile();
        file.setPartialSize(111L);
        when(fileDao.getPartialFile(anyLong(), eq("testfile"), eq(1234L))).thenReturn(file);
        MockHttpServletResponse response = new MockHttpServletResponse();
        ServletActionContext.setResponse(response);
        assertEquals(ActionSupport.NONE, action.partialUploadCheck());
        JSONObject asdf = JSONObject.fromObject(response.getContentAsString());
        assertEquals(111L, asdf.getLong("size"));
    }
    
    @Test
    public void testUpload_InvalidFileExceptionUnpack() throws Exception {
        String expectedErrorKey = "errors.uploadingErrorWithAdding";
        doInvalidFileExceptionTest(expectedErrorKey, false);
    }

    @Test
    public void testUpload_UnexpectedError() throws Exception {
        String errorUnpackingZipKey = "errors.unpackingErrorWithZip";
        File file = File.createTempFile("tmp", ZIP_EXTENSION);

        List<File> files = Lists.newArrayList(file);
        List<String> uploadFileNames = Lists.newArrayList(file.getName());
        action.setUpload(files);
        action.setUploadFileName(uploadFileNames);

        when(
                mockUploadUtils.uploadFiles(any(Project.class), anyListOf(FileWrapper.class))).thenThrow(new IllegalStateException());

        MockHttpServletResponse response = new MockHttpServletResponse();
        ServletActionContext.setResponse(response);

        assertEquals(ActionSupport.NONE, action.upload());
        JSONArray jsonResult = JSONArray.fromObject(response.getContentAsString());
        assertEquals(1, jsonResult.size());
        JSONObject map = jsonResult.getJSONObject(0);
        assertEquals(2, map.keySet().size());
        assertTrue(map.get("name").toString().contains(ZIP_EXTENSION));

        assertTrue(map.get("error").toString(), map.get("error").toString().contains(errorUnpackingZipKey));
    }

    @Test
    public void testUploadInBackground_importing() {
        Project project = mock(Project.class);
        when(project.isImportingData()).thenReturn(true);
        action.setProject(project);

        assertEquals(UploadProjectFilesAction.UPLOAD_IN_BACKGROUND, action.uploadInBackground());
        assertFalse(ActionHelper.getMessages().isEmpty());
        assertTrue(ActionHelper.getMessages().contains(action.getText(UploadProjectFilesAction.IMPORTING_FILES_KEY)));
    }

    @Test
    public void testUploadInBackground() {
        assertEquals(UploadProjectFilesAction.UPLOAD_IN_BACKGROUND, action.uploadInBackground());
        assertNull(ActionHelper.getMessages());
    }
}
