//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.file;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * Tests the FileRetrievalServiceBean
 */
public class FileRetrievalServiceBeanTest extends AbstractServiceTest {
    /**
     * Test method for
     * {@link gov.nih.nci.caarray.services.file.FileRetrievalServiceBean#readFile(gov.nih.nci.caarray.domain.file.CaArrayFile)}
     * .
     */
    @Test
    public void testReadFile() throws IOException {
        final FileAccessServiceStub fasStub = new FileAccessServiceStub();
        Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(FileTypeRegistry.class).toInstance(fasStub.getTypeRegistry());
                requestStaticInjection(CaArrayFile.class);
            }
        });

        final CaArrayFile file = fasStub.add(MageTabDataFiles.GEDP_IDF);

        final SearchDao searchDao = mock(SearchDao.class);
        when(searchDao.query(any(CaArrayFile.class))).thenReturn(Lists.newArrayList(file));

        final FileRetrievalServiceBean bean = new FileRetrievalServiceBean();
        final DataStorageFacade dataStorageFacade = fasStub.createStorageFacade();
        bean.setSearchDao(searchDao);
        bean.setDataStorageFacade(dataStorageFacade);

        final CaArrayFile caArrayFile = new CaArrayFile();
        final byte[] bytes = bean.readFile(caArrayFile);
        final byte[] expectedBytes = FileUtils.readFileToByteArray(MageTabDataFiles.GEDP_IDF);
        assertTrue("retrieved file contents didn't match", Arrays.equals(expectedBytes, bytes));
    }
}
