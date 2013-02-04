//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.plugins.affymetrix.CdfReader;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;

import java.io.File;

import org.junit.Test;

public class AffymetrixCdfReaderTest extends AbstractCaarrayTest {

    @Test
    public final void testCreate() throws PlatformFileReadException {
        System.out
                .println("----------*-*-*----------" + AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CEL.getAbsolutePath());
        CdfReader reader = new CdfReader(AffymetrixArrayDesignFiles.TEST3_CDF);
        assertNotNull(reader.getCdfData());
        reader.close();
        assertNull(reader.getCdfData());
        // check that redundant call to close isn't problematic (invoked by finalizer)
        reader.close();
        assertNull(reader.getCdfData());
    }

    @Test(expected = PlatformFileReadException.class)
    public final void testCreateInvalid() throws PlatformFileReadException {
        try {
            new CdfReader(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CEL);
        } catch (PlatformFileReadException e) {
            assertEquals("Unknown file format.", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateNoFile() throws PlatformFileReadException {
        new CdfReader(new File(AffymetrixArrayDesignFiles.TEST3_CDF.getAbsolutePath() + ".nofile"));
    }
}
