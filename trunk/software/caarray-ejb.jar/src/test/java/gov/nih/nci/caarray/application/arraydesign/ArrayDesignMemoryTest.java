//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import affymetrix.fusion.cdf.FusionCDFData;
import affymetrix.fusion.cdf.FusionCDFProbeGroupInformation;
import affymetrix.fusion.cdf.FusionCDFProbeInformation;
import affymetrix.fusion.cdf.FusionCDFProbeSetInformation;

/**
 * Test class for ArrayDesignService subsystem.
 */
@SuppressWarnings("PMD")
public class ArrayDesignMemoryTest extends AbstractCaarrayTest {

    private ArrayDesignDetails details = new ArrayDesignDetails();
    FusionCDFData fusionCDFData = new FusionCDFData();
    int featureCount = 0;

    @Test
    public void testLoadDesign() {
        fusionCDFData.setFileName(AffymetrixArrayDesignFiles.TEN_K_CDF.getAbsolutePath());
        fusionCDFData.read();
        int numProbeSets = fusionCDFData.getHeader().getNumProbeSets();
        FusionCDFProbeSetInformation probeSetInformation = new FusionCDFProbeSetInformation();
        for (int index = 0; index < numProbeSets; index++) {
            fusionCDFData.getProbeSetInformation(index, probeSetInformation);
            handleProbeSet(probeSetInformation, fusionCDFData.getProbeSetName(index));
        }
        fusionCDFData.clear();
        fusionCDFData = null;
        probeSetInformation.clear();
        probeSetInformation = null;
        System.gc();
        System.out.println("Feature count = " + details.getFeatures().size());
        long memSize = Runtime.getRuntime().totalMemory();
        System.out.println("Mem size = " + memSize);
        System.out.println("Mem size (formatted) = " + FileUtils.byteCountToDisplaySize(memSize));
        System.out.println("Bytes per Feature = " + memSize/details.getFeatures().size());
    }

    private void handleProbeSet(FusionCDFProbeSetInformation probeSetInformation, String probeSetName) {
        int numGroups = probeSetInformation.getNumGroups();
        FusionCDFProbeGroupInformation probeGroupInformation = new FusionCDFProbeGroupInformation();
        for (int index = 0; index < numGroups; index++) {
            probeSetInformation.getGroup(index, probeGroupInformation);
            handleProbeGroup(probeGroupInformation);
        }
    }

    private void handleProbeGroup(FusionCDFProbeGroupInformation probeGroupInformation) {
        int numCells = probeGroupInformation.getNumCells();
        FusionCDFProbeInformation probeInformation = new FusionCDFProbeInformation();
        for (int index = 0; index < numCells; index++) {
            probeGroupInformation.getCell(index, probeInformation);
            handleProbe(probeInformation);
        }
    }

    private void handleProbe(FusionCDFProbeInformation probeInformation) {
        Feature feature = createFeature(probeInformation.getX(), probeInformation.getY());
        details.getFeatures().add(feature);
    }

    private Feature createFeature(int x, int y) {
        Feature feature = new Feature(details);
        feature.setColumn((short) x);
        feature.setRow((short) y);
        featureCount++;
        //featureCreated[x][y] = true;
        return feature;
    }
}
