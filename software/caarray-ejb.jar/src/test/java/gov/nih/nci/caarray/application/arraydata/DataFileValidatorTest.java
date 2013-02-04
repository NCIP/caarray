//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
