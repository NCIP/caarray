//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;

import java.io.File;

import org.junit.Test;

public class AffymetrixCdfReaderTest extends AbstractCaarrayTest {

    @Test
    public final void testCreate() throws AffymetrixArrayDesignReadException {
        AffymetrixCdfReader reader = AffymetrixCdfReader.create(AffymetrixArrayDesignFiles.TEST3_CDF);
        assertNotNull(reader.getCdfData());
        reader.close();
        assertNull(reader.getCdfData());
        // check that redundant call to close isn't problematic (invoked by finalizer)
        reader.close();
        assertNull(reader.getCdfData());
    }

    @Test(expected = AffymetrixArrayDesignReadException.class)
    public final void testCreateInvalid() throws AffymetrixArrayDesignReadException {
        try {
            AffymetrixCdfReader.create(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CEL);
        } catch (AffymetrixArrayDesignReadException e) {
            assertEquals("Unknown file format.", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateNoFile() throws AffymetrixArrayDesignReadException {
        AffymetrixCdfReader.create(new File(AffymetrixArrayDesignFiles.TEST3_CDF.getAbsolutePath() + ".nofile"));
    }

}
