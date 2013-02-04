//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
import gov.nih.nci.caarray.application.project.FileProcessingResult;
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
        String uploadingErrorKey = UploadProjectFilesAction.UPLOADING_ERROR_KEY;
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

        assertTrue(map.get("error").toString(), map.get("error").toString().contains(uploadingErrorKey));
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
    
    @Test
    public void testUploadMessages() throws Exception {
        File file = File.createTempFile("tmp", NON_ZIP_EXTENSION);
        List<File> files = new ArrayList<File>();
        List<String> fileNames = new ArrayList<String>();
        files.add(file);
        fileNames.add(file.getName());
        action.setUpload(files);
        action.setUploadFileName(fileNames);

        // Upload a file and check for success message
        FileProcessingResult result = new FileProcessingResult();
        result.addSuccessfulFile(file.getName());
        when(mockUploadUtils.uploadFiles(any(Project.class), anyListOf(FileWrapper.class))).thenReturn(result);
        assertEquals(ActionSupport.NONE, action.upload());
        List<String> msgs = ActionHelper.getMessages();
        assertEquals(1, msgs.size());
        assertEquals(1 + UploadProjectFilesAction.FILE_UPLOADED_MSG_SUFFIX, msgs.get(0));

        // Upload a second file and check for updated success message
        FileProcessingResult result2 = new FileProcessingResult();
        result2.addSuccessfulFile("file2");
        when(mockUploadUtils.uploadFiles(any(Project.class), anyListOf(FileWrapper.class))).thenReturn(result2);
        assertEquals(ActionSupport.NONE, action.upload());
        msgs = ActionHelper.getMessages();
        assertEquals(1, msgs.size());
        assertEquals(2 + UploadProjectFilesAction.FILE_UPLOADED_MSG_SUFFIX, msgs.get(0));
    }
}
