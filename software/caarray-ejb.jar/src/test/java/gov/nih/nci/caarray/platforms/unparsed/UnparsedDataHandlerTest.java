//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.platforms.unparsed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.io.File;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * 
 * @author gax
 */
public class UnparsedDataHandlerTest {
    private FileAccessServiceStub fasStub;
    private UnparsedDataHandler handler;
    private FallbackUnparsedDataHandler fallbackHandler;

    @Before
    public void setup() {
        this.fasStub = new FileAccessServiceStub();

        final DataStorageFacade dataStorageFacade = this.fasStub.createStorageFacade();
        this.handler = new UnparsedDataHandler(dataStorageFacade);
        this.fallbackHandler = new FallbackUnparsedDataHandler(dataStorageFacade);

        final FileTypeRegistry typeRegistry = new FileTypeRegistryImpl(Sets.<DataFileHandler> newHashSet(this.handler),
                Collections.<DesignFileHandler> emptySet());
        Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(FileTypeRegistry.class).toInstance(typeRegistry);
                requestStaticInjection(CaArrayFile.class);
            }
        });
        this.fasStub.setTypeRegistry(typeRegistry);
    }

    @Test
    public void testGetHybridizationNames() throws PlatformFileReadException {
        final CaArrayFile f = this.fasStub.add(new File("foo.gff"));
        assertTrue(this.handler.openFile(f));
        assertEquals(1, this.handler.getHybridizationNames().size());
        assertEquals("foo", this.handler.getHybridizationNames().get(0));
    }

    @Test
    public void testFallbackUnparsedDataHandler() throws PlatformFileReadException {
        final CaArrayFile f = this.fasStub.add(new File("foo.cel"));
        assertTrue(this.fallbackHandler.openFile(f));
    }

    @Test
    public void testFallbackUnparsedDataHandlerValidation() throws PlatformFileReadException {
        final ArrayDesign ad = new ArrayDesign();
        ad.setName("foo");
        final FileValidationResult results = new FileValidationResult();
        this.fallbackHandler.validate(null, results, ad);
        assertEquals("Not parsed because array design foo is not parsed", results.getMessages().get(0).getMessage());

    }

}
