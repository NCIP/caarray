//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessUtils;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.AbstractDownloadTest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ArrayDesignActionMockitoBasedTest extends AbstractDownloadTest {

    private ArrayDesignAction arrayDesignAction;
    private static final String ZIP_FRAGMENT = "test-ARRAY-1893";
    private static final String ZIP_NAME = ZIP_FRAGMENT + ".zip";
    private static final String ARRAY_DESIGN_NAME = "array_design.xml";

    @Test
    public void testSave() throws Exception {
        final ArrayDesign design = new ArrayDesign();
        this.arrayDesignAction.setArrayDesign(design);
        final List<String> zipFileName = new ArrayList<String>();
        zipFileName.add(ZIP_NAME);
        this.arrayDesignAction.setUploadFileName(zipFileName);
        final List<String> fileTypes = new ArrayList<String>();
        fileTypes.add("AGILENT_XML");
        this.arrayDesignAction.setFileFormatType(fileTypes);
        final List<File> fileList = new ArrayList<File>();
        fileList.add(new File(ZIP_NAME));
        this.arrayDesignAction.setUpload(fileList);
        String result = this.arrayDesignAction.saveMeta();
        assertEquals("metaValid", result);
        result = this.arrayDesignAction.save();
        assertEquals("importComplete", result);
    }

    @Override
    @Before
    public void init() {
        super.init();
        setupLocatorStub();
        setupActionWithInjector();
    }

    @SuppressWarnings("unchecked")
    private void setupActionWithInjector() {
        final FileAccessUtils fileAccessUtils = mock(FileAccessUtils.class);
        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                List<String> filenamesList = (List<String>) invocation.getArguments()[1];
                filenamesList.clear();
                filenamesList.add(ARRAY_DESIGN_NAME);
                return null;
            }
        }).when(fileAccessUtils).unzipFiles(anyList(), anyList());
        
        Set<DataFileHandler> datafh = Collections.emptySet();
        Set<DesignFileHandler> designfh = Collections.emptySet();
        final FileTypeRegistry fileTypeRegistry = new FileTypeRegistryImpl(datafh, designfh) {
            @Override
            public FileType getTypeByName(String name) {
                FileType result = new FileType();
                result.setCategory(FileCategory.ARRAY_DESIGN);
                return result;
            }
        };
        
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(FileAccessUtils.class).toInstance(fileAccessUtils);
                bind(FileTypeRegistry.class).toInstance(fileTypeRegistry);
            }
        });
        arrayDesignAction = new ArrayDesignAction(injector);
    }

    private void setupLocatorStub() {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();

        final ArrayDesignService arrayDesignService = mock(ArrayDesignService.class);
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, arrayDesignService);

        final VocabularyService vocabularyService = mock(VocabularyService.class);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, vocabularyService);

        final FileAccessService fileAccessService = mock(FileAccessService.class);
        when(fileAccessService.add(isA(File.class), anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return mock(CaArrayFile.class);
            }
        });
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessService);

        final FileManagementService fileManagementService = mock(FileManagementService.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final ArrayDesign arrayDesign = (ArrayDesign)invocation.getArguments()[0];
                if (ZIP_FRAGMENT.equals(arrayDesign.getName())) {
                    throw new Exception("foo");
                }
                return null;
            }
        }).when(fileManagementService).importArrayDesignDetails(isA(ArrayDesign.class));
        locatorStub.addLookup(FileManagementService.JNDI_NAME, fileManagementService);
    }
}
