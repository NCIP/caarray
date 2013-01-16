//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.nimblegen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.SessionTransactionManagerNoOpImpl;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.test.data.arraydesign.NimblegenArrayDesignFiles;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class NdfHandlerTest {
    private static Injector injector;
    private static CaArrayHibernateHelper hibernateHelper;
    private NdfHandler handler;
    private FileAccessServiceStub fasStub;

    @Before
    public void setup() {

        injector = Guice.createInjector(new CaArrayHibernateHelperModule());
        hibernateHelper = injector.getInstance(CaArrayHibernateHelper.class);
        this.fasStub = new FileAccessServiceStub();

        final SessionTransactionManager stm = new SessionTransactionManagerNoOpImpl();
        final ArrayDao arrayDao = new ArrayDaoStub();
        final SearchDao searchDao = new SearchDaoStub();
        final DataStorageFacade dataStorageFacade = this.fasStub.createStorageFacade();
        this.handler = new NdfHandler(stm, dataStorageFacade, arrayDao, searchDao, hibernateHelper);

        final FileTypeRegistry typeRegistry = new FileTypeRegistryImpl(Collections.<DataFileHandler> emptySet(),
                Sets.<DesignFileHandler> newHashSet(this.handler));
        injector.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(FileTypeRegistry.class).toInstance(typeRegistry);
                requestStaticInjection(CaArrayFile.class);
            }
        });
        this.fasStub.setTypeRegistry(typeRegistry);
    }

    private CaArrayFile getCaArrayFile(File file) {
        final CaArrayFile caArrayFile = this.fasStub.add(file);
        // caArrayFile.setFileType(NdfHandler.NDF_FILE_TYPE);
        return caArrayFile;
    }

    @Test
    public void testMissingHeader() throws PlatformFileReadException {
        final CaArrayFile missingHeaderFile = getCaArrayFile(NimblegenArrayDesignFiles.MISSING_HEADER_NDF);
        try {
            final boolean opened = this.handler.openFiles(Collections.singleton(missingHeaderFile));
            assertTrue(opened);
            final ValidationResult result = new ValidationResult();
            this.handler.validate(result);
            final FileValidationResult fvr = result
                    .getFileValidationResult(NimblegenArrayDesignFiles.MISSING_HEADER_NDF.getName());
            assertNotNull(fvr);
            assertEquals(1, fvr.getMessages().size());
            final ValidationMessage msg = fvr.getMessages().get(0);
            assertEquals(ValidationMessage.Type.ERROR, msg.getType());
            assertTrue(msg.getMessage().startsWith("Could not find column headers in file"));
        } finally {
            this.handler.closeFiles();
        }
    }

    @Test
    public void testMissingHeaderColumns() throws PlatformFileReadException {
        final CaArrayFile missingHeaderFile = getCaArrayFile(NimblegenArrayDesignFiles.MISSING_COLUMNS_NDF);
        try {
            final boolean opened = this.handler.openFiles(Collections.singleton(missingHeaderFile));
            assertTrue(opened);
            final ValidationResult result = new ValidationResult();
            this.handler.validate(result);
            final FileValidationResult fvr = result
                    .getFileValidationResult(NimblegenArrayDesignFiles.MISSING_COLUMNS_NDF.getName());
            assertNotNull(fvr);
            assertEquals(1, fvr.getMessages().size());
            final ValidationMessage msg = fvr.getMessages().get(0);
            assertEquals(ValidationMessage.Type.ERROR, msg.getType());
            assertEquals(1, msg.getLine());
            assertEquals(0, msg.getColumn());
            assertEquals("Invalid column header for Nimblegen NDF. Missing SEQ_ID column", msg.getMessage());
        } finally {
            this.handler.closeFiles();
        }
    }

    @Test
    public void testIncompleteRow() throws PlatformFileReadException {
        final CaArrayFile missingHeaderFile = getCaArrayFile(NimblegenArrayDesignFiles.INCOMPLETE_ROW_NDF);
        try {
            final boolean opened = this.handler.openFiles(Collections.singleton(missingHeaderFile));
            assertTrue(opened);
            final ValidationResult result = new ValidationResult();
            this.handler.validate(result);
            final FileValidationResult fvr = result
                    .getFileValidationResult(NimblegenArrayDesignFiles.INCOMPLETE_ROW_NDF.getName());
            assertNotNull(fvr);
            assertEquals(1, fvr.getMessages().size());
            final ValidationMessage msg = fvr.getMessages().get(0);
            assertEquals(ValidationMessage.Type.ERROR, msg.getType());
            assertEquals(8, msg.getLine());
            assertEquals(0, msg.getColumn());
            assertEquals("Row has incorrect number of columns. There were 11 columns in the row, "
                    + "and 12 columns in the header", msg.getMessage());
        } finally {
            this.handler.closeFiles();
        }
    }

    @Test
    public void testMissingColumnValue() throws PlatformFileReadException {
        final CaArrayFile missingHeaderFile = getCaArrayFile(NimblegenArrayDesignFiles.MISSING_COLUMN_VALUE_NDF);
        try {
            final boolean opened = this.handler.openFiles(Collections.singleton(missingHeaderFile));
            assertTrue(opened);
            final ValidationResult result = new ValidationResult();
            this.handler.validate(result);
            final FileValidationResult fvr = result
                    .getFileValidationResult(NimblegenArrayDesignFiles.MISSING_COLUMN_VALUE_NDF.getName());
            assertNotNull(fvr);
            assertEquals(1, fvr.getMessages().size());
            final ValidationMessage msg = fvr.getMessages().get(0);
            assertEquals(ValidationMessage.Type.ERROR, msg.getType());
            assertEquals(8, msg.getLine());
            assertEquals(2, msg.getColumn());
            assertEquals("Empty value for required column CONTAINER", msg.getMessage());
        } finally {
            this.handler.closeFiles();
        }
    }

    @Test
    public void testInvalidColumnValue() throws PlatformFileReadException {
        final CaArrayFile missingHeaderFile = getCaArrayFile(NimblegenArrayDesignFiles.INVALID_COLUMN_VALUE_NDF);
        try {
            final boolean opened = this.handler.openFiles(Collections.singleton(missingHeaderFile));
            assertTrue(opened);
            final ValidationResult result = new ValidationResult();
            this.handler.validate(result);
            final FileValidationResult fvr = result
                    .getFileValidationResult(NimblegenArrayDesignFiles.INVALID_COLUMN_VALUE_NDF.getName());
            assertNotNull(fvr);
            assertEquals(1, fvr.getMessages().size());
            final ValidationMessage msg = fvr.getMessages().get(0);
            assertEquals(ValidationMessage.Type.ERROR, msg.getType());
            assertEquals(8, msg.getLine());
            assertEquals(12, msg.getColumn());
            assertEquals("Expected integer value but found FOO for required column Y", msg.getMessage());
        } finally {
            this.handler.closeFiles();
        }
    }
}
