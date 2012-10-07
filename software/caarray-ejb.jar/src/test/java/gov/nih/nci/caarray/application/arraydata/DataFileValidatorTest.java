/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caArray2 Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caArray2 Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2 Software; (ii) distribute and 
 * have distributed to and by third parties the caArray2 Software and any 
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
package gov.nih.nci.caarray.application.arraydata;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.platforms.unparsed.FallbackUnparsedDataHandler;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Providers;

/**
 * @author wcheng
 *
 */
public class DataFileValidatorTest {
    private static final FileType DUMMY_FILETYPE = new FileType("DUMMY_FILETYPE", FileCategory.RAW_DATA, true);

    private DataFileValidator dataFileValidator;
    private CaArrayFile dataFile;
    @Mock private DataFileHandler mockDataFileHandler;
    @Mock private ArrayDesign mockDesign1;
    @Mock private ArrayDesign mockDesign2;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mockDataFileHandler.openFile(any(CaArrayFile.class))).thenReturn(true);
        when(mockDataFileHandler.getSupportedTypes()).thenReturn(Sets.newHashSet(DUMMY_FILETYPE));
        
        final Module testModule = new AbstractModule() {
            @Override
            protected void configure() {
                FileTypeRegistry typeRegistry = new FileTypeRegistryImpl(Sets.newHashSet(mockDataFileHandler),
                        Collections.<DesignFileHandler> emptySet());
                bind(FileTypeRegistry.class).toInstance(typeRegistry);
                requestStaticInjection(CaArrayFile.class);
                
                bind(ArrayDao.class).toInstance(mock(ArrayDao.class));
                Multibinder<DataFileHandler> dataHandlers = Multibinder.newSetBinder(binder(), DataFileHandler.class);
                dataHandlers.addBinding().toInstance(mockDataFileHandler);
                Provider<FallbackUnparsedDataHandler> fallbackHandler =
                        Providers.of(mock(FallbackUnparsedDataHandler.class));
                bind(FallbackUnparsedDataHandler.class).toProvider(fallbackHandler);
            }
        };
        final Injector injector = Guice.createInjector(testModule);
        this.dataFileValidator = (DataFileValidator) injector.getInstance(DataFileValidator.class);

        Project project = new Project();
        dataFile = new CaArrayFile();
        dataFile.setProject(project);
        dataFile.setFileType(DUMMY_FILETYPE);
    }

    @Test
    public void validateNoExperimentDesigns() {
        dataFileValidator.validate(dataFile, null, false);

        FileValidationResult result = dataFile.getValidationResult();
        assertFalse(result.isValid());
        assertTrue(validateErrorMessages(result, Sets.newHashSet("The array design referenced by this data file could not be found.")));
    }

    @Test
    public void validExperimentDesign() {
        Experiment experiment = dataFile.getProject().getExperiment();
        experiment.setArrayDesigns(Sets.newHashSet(mockDesign1));
        dataFileValidator.validate(dataFile, null, false);

        FileValidationResult result = dataFile.getValidationResult();
        assertTrue(result.isValid());
    }
    
    @Test
    public void validateMultipleExperimentDesigns() {
        Experiment experiment = dataFile.getProject().getExperiment();
        experiment.setArrayDesigns(Sets.newHashSet(mockDesign1, mockDesign2));
        dataFileValidator.validate(dataFile, null, false);

        FileValidationResult result = dataFile.getValidationResult();
        assertFalse(result.isValid());
        assertTrue(validateErrorMessages(result, Sets.newHashSet("This experiment has multiple array designs.")));
    }

    /**
     * Returns true if the validation result contains each given error message fragment.
     * @param result file validation result object
     * @param messageFragments expected error message fragments
     * @return true if all message fragments are found
     */
    private static boolean validateErrorMessages(FileValidationResult result, Set<String> messageFragments) {
        List<ValidationMessage> validationMessages = result.getMessages(ValidationMessage.Type.ERROR);
        for (String fragment : messageFragments) {
            boolean matched = false;
            for (ValidationMessage message : validationMessages) {
                if (message.getMessage().contains(fragment)) {
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return true;
    }
}
