package gov.nih.nci.caarray.domain.file;

import static org.junit.Assert.*;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import org.junit.Test;

public class CaArrayFileSetTest {

    @Test
    public void testGetFile() {
        CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
        CaArrayFile caArrayFile2 = new CaArrayFile();
        caArrayFile2.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF.getName());
        CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(caArrayFile);
        fileSet.add(caArrayFile2);
        fileSet.add(new CaArrayFile());
        assertEquals(caArrayFile, fileSet.getFile(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF));
        assertEquals(caArrayFile2, fileSet.getFile(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF));
        assertNull(fileSet.getFile(MageTabDataFiles.SPECIFICATION_ERROR_EXAMPLE_IDF));
    }

}
