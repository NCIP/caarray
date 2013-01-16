//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.platforms.unparsed;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * Test of unparsed design handler
 * 
 * @author dkokotov
 */
public class UnparsedDesignHandlerTest {
    private UnparsedArrayDesignFileHandler handler;

    @Before
    public void setup() {
        this.handler = new UnparsedArrayDesignFileHandler();

        final FileTypeRegistry typeRegistry = new FileTypeRegistryImpl(Collections.<DataFileHandler> emptySet(),
                Sets.<DesignFileHandler> newHashSet(this.handler));
        Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(FileTypeRegistry.class).toInstance(typeRegistry);
                requestStaticInjection(CaArrayFile.class);
            }
        });
    }

    @After
    public void cleanup() {
        this.handler.closeFiles();
    }

    @Test
    public void testLoadAgilentCsv() {
        testLoadDesign(UnparsedArrayDesignFileHandler.AGILENT_CSV,
                new LSID("Agilent.com", "PhysicalArrayDesign", "Foo"));
    }

    @Test
    public void testLoadImageneTpl() {
        testLoadDesign(UnparsedArrayDesignFileHandler.IMAGENE_TPL, new LSID("caarray.nci.nih.gov", "domain", "Foo"));
    }

    @Test
    public void testLoadUcfSpot() {
        testLoadDesign(UnparsedArrayDesignFileHandler.UCSF_SPOT_SPT, new LSID("caarray.nci.nih.gov", "domain", "Foo"));
    }

    @Test
    public void testLoadAdf() {
        testLoadDesign(UnparsedArrayDesignFileHandler.MAGE_TAB_ADF, new LSID("caarray.nci.nih.gov", "domain", "Foo"));
    }

    private void testLoadDesign(FileType type, LSID expectedLsid) {
        final CaArrayFile designFile = getCaArrayFile("Foo", type);
        final ArrayDesign arrayDesign = new ArrayDesign();
        this.handler.openFiles(Collections.singleton(designFile));
        this.handler.load(arrayDesign);
        assertEquals(expectedLsid.getAuthority(), arrayDesign.getLsidAuthority());
        assertEquals(expectedLsid.getNamespace(), arrayDesign.getLsidNamespace());
        assertEquals(expectedLsid.getObjectId(), arrayDesign.getLsidObjectId());
    }

    private CaArrayFile getCaArrayFile(String name, final FileType type) {
        final CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setName(name);
        caArrayFile.setFileStatus(FileStatus.UPLOADED);
        caArrayFile.setFileType(type);
        return caArrayFile;
    }
}
