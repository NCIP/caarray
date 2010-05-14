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
package gov.nih.nci.caarray.platforms.nimblegen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.platforms.FileManager;
import gov.nih.nci.caarray.platforms.MockFileManager;
import gov.nih.nci.caarray.platforms.MockSessionTransactionManager;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.test.data.arraydesign.NimblegenArrayDesignFiles;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class NdfHandlerTest {
    private NdfHandler handler;
    private FileAccessServiceStub fasStub;        
    
    @Before
    public void setup() {
        SessionTransactionManager stm = new MockSessionTransactionManager();
        fasStub = new FileAccessServiceStub();        
        FileManager fm = new MockFileManager(fasStub);
        ArrayDao arrayDao = new ArrayDaoStub();
        SearchDao searchDao = new SearchDaoStub();
        handler = new NdfHandler(stm, fm, arrayDao, searchDao);
    }
    
    @Test
    public void testMissingHeader() throws PlatformFileReadException {
        CaArrayFile missingHeaderFile = fasStub.add(NimblegenArrayDesignFiles.MISSING_HEADER_NDF); 
        try {
            handler.openFiles(Collections.singleton(missingHeaderFile));
            ValidationResult result = new ValidationResult();
            handler.validate(result);
            FileValidationResult fvr = result.getFileValidationResult(NimblegenArrayDesignFiles.MISSING_HEADER_NDF);
            assertNotNull(fvr);
            assertEquals(1, fvr.getMessages().size());
            ValidationMessage msg = fvr.getMessages().get(0);
            assertEquals(ValidationMessage.Type.ERROR, msg.getType());
            assertTrue(msg.getMessage().startsWith("Could not find column headers in file"));
        } finally {
            handler.closeFiles();
        }
    }
    
    @Test
    public void testMissingHeaderColumns() throws PlatformFileReadException {
        CaArrayFile missingHeaderFile = fasStub.add(NimblegenArrayDesignFiles.MISSING_COLUMNS_NDF); 
        try {
            handler.openFiles(Collections.singleton(missingHeaderFile));
            ValidationResult result = new ValidationResult();
            handler.validate(result);
            FileValidationResult fvr = result.getFileValidationResult(NimblegenArrayDesignFiles.MISSING_COLUMNS_NDF);
            assertNotNull(fvr);
            assertEquals(1, fvr.getMessages().size());
            ValidationMessage msg = fvr.getMessages().get(0);
            assertEquals(ValidationMessage.Type.ERROR, msg.getType());
            assertEquals(1, msg.getLine());
            assertEquals(0, msg.getColumn());
            assertEquals("Invalid column header for Nimblegen NDF. Missing SEQ_ID column", msg.getMessage());
        } finally {
            handler.closeFiles();
        }
    }
    
    @Test
    public void testIncompleteRow() throws PlatformFileReadException {
        CaArrayFile missingHeaderFile = fasStub.add(NimblegenArrayDesignFiles.INCOMPLETE_ROW_NDF); 
        try {
            handler.openFiles(Collections.singleton(missingHeaderFile));
            ValidationResult result = new ValidationResult();
            handler.validate(result);
            FileValidationResult fvr = result.getFileValidationResult(NimblegenArrayDesignFiles.INCOMPLETE_ROW_NDF);
            assertNotNull(fvr);
            assertEquals(1, fvr.getMessages().size());
            ValidationMessage msg = fvr.getMessages().get(0);
            assertEquals(ValidationMessage.Type.ERROR, msg.getType());
            assertEquals(8, msg.getLine());
            assertEquals(0, msg.getColumn());
            assertEquals("Row has incorrect number of columns. There were 11 columns in the row, "
                    + "and 12 columns in the header", msg.getMessage());
        } finally {
            handler.closeFiles();
        }
    }
    
    @Test
    public void testMissingColumnValue() throws PlatformFileReadException {
        CaArrayFile missingHeaderFile = fasStub.add(NimblegenArrayDesignFiles.MISSING_COLUMN_VALUE_NDF); 
        try {
            handler.openFiles(Collections.singleton(missingHeaderFile));
            ValidationResult result = new ValidationResult();
            handler.validate(result);
            FileValidationResult fvr = result.getFileValidationResult(NimblegenArrayDesignFiles.MISSING_COLUMN_VALUE_NDF);
            assertNotNull(fvr);
            assertEquals(1, fvr.getMessages().size());
            ValidationMessage msg = fvr.getMessages().get(0);
            assertEquals(ValidationMessage.Type.ERROR, msg.getType());
            assertEquals(8, msg.getLine());
            assertEquals(2, msg.getColumn());
            assertEquals("Empty value for required column CONTAINER", msg.getMessage());
        } finally {
            handler.closeFiles();
        }
    }
    
    @Test
    public void testInvalidColumnValue() throws PlatformFileReadException {
        CaArrayFile missingHeaderFile = fasStub.add(NimblegenArrayDesignFiles.INVALID_COLUMN_VALUE_NDF); 
        try {
            handler.openFiles(Collections.singleton(missingHeaderFile));
            ValidationResult result = new ValidationResult();
            handler.validate(result);
            FileValidationResult fvr = result.getFileValidationResult(NimblegenArrayDesignFiles.INVALID_COLUMN_VALUE_NDF);
            assertNotNull(fvr);
            assertEquals(1, fvr.getMessages().size());
            ValidationMessage msg = fvr.getMessages().get(0);
            assertEquals(ValidationMessage.Type.ERROR, msg.getType());
            assertEquals(8, msg.getLine());
            assertEquals(12, msg.getColumn());
            assertEquals("Expected integer value but found FOO for required column Y", msg.getMessage());
        } finally {
            handler.closeFiles();
        }
    }
}
