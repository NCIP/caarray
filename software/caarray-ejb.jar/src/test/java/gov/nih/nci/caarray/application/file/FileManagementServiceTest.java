/**
 *  The caArray Software License, Version 1.0
 *
 *  Copyright 2004 5AM Solutions. This software was developed in conjunction
 *  with the National Cancer Institute, and so to the extent government
 *  employees are co-authors, any rights in such works shall be subject to
 *  Title 17 of the United States Code, section 105.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the disclaimer of Article 3, below.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  2. Affymetrix Pure Java run time library needs to be downloaded from
 *  (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 *  after agreeing to the licensing terms from the Affymetrix.
 *
 *  3. The end-user documentation included with the redistribution, if any,
 *  must include the following acknowledgment:
 *
 *  "This product includes software developed by 5AM Solutions and the National
 *  Cancer Institute (NCI).
 *
 *  If no such end-user documentation is to be included, this acknowledgment
 *  shall appear in the software itself, wherever such third-party
 *  acknowledgments normally appear.
 *
 *  4. The names "The National Cancer Institute", "NCI", and "5AM Solutions"
 *  must not be used to endorse or promote products derived from this software.
 *
 *  5. This license does not authorize the incorporation of this software into
 *  any proprietary programs. This license does not authorize the recipient to
 *  use any trademarks owned by either NCI or 5AM.
 *
 *  6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 *  EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.application.file;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceStub;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslatorStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class FileManagementServiceTest {

    private FileManagementService fileManagementService;

    @Before
    public void setUp() {
        FileManagementServiceBean fileManagementServiceBean = new FileManagementServiceBean();
        fileManagementServiceBean.setArrayDataService(new LocalArrayDataServiceStub());
        fileManagementServiceBean.setArrayDesignService(new LocalArrayDesignServiceStub());
        fileManagementServiceBean.setMageTabTranslator(new MageTabTranslatorStub());
        fileManagementServiceBean.setDaoFactory(new DaoFactoryStub());
        fileManagementServiceBean.setFileAccessService(new FileAccessServiceStub());
        fileManagementService = fileManagementServiceBean;
    }

    @Test
    public void testValidateFiles() {
        Project project = getTgaBroadTestProject();
        fileManagementService.validateFiles(project.getFileSet());
        for (CaArrayFile file : project.getFiles()) {
            assertEquals(FileStatus.VALIDATED, file.getFileStatus());
        }
    }
    @Test
    public void testImportFiles() {
        Project project = getTgaBroadTestProject();
        fileManagementService.importFiles(project, project.getFileSet());
        for (CaArrayFile file : project.getFiles()) {
            assertEquals(FileStatus.IMPORTED, file.getFileStatus());
        }
    }

    private Project getTgaBroadTestProject() {
        Project project = Project.createNew();
        project.getFiles().addAll(TestMageTabSets.getFileSet(TestMageTabSets.TCGA_BROAD_SET).getFiles());
        assertEquals(29, project.getFiles().size());
        return project;
    }

    private static class LocalArrayDataServiceStub extends ArrayDataServiceStub {

        @Override
        public FileValidationResult validate(CaArrayFile arrayDataFile) {
            arrayDataFile.setFileStatus(FileStatus.VALIDATED);
            return new FileValidationResult(new File(arrayDataFile.getName()));
        }
    }

    private static class LocalArrayDesignServiceStub extends ArrayDesignServiceStub {

        @Override
        public FileValidationResult validateDesign(CaArrayFile designFile) {
            designFile.setFileStatus(FileStatus.VALIDATED);
            return new FileValidationResult(new File(designFile.getName()));
        }

    }

}
