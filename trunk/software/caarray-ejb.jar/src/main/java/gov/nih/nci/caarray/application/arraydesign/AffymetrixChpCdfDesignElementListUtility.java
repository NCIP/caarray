//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.DesignElementList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import affymetrix.fusion.cdf.FusionCDFData;

/**
 * Utility class used to generate and retrieve the singleton <code>DesignElementList</code> to be used for all parsed
 * CHP files, for CDF-based array designs.
 * @author Steve Lustbader
 */
public final class AffymetrixChpCdfDesignElementListUtility extends AbstractAffymetrixChpDesignElementListUtility {
    private static final Logger LOG = Logger.getLogger(AffymetrixChpCdfDesignElementListUtility.class);

    /**
     * Creates the singleton <code>DesignElementList</code> for CHP files associated with the given
     * Affymetrix design.
     *
     * @param design the design to create the <code>DesignElementList</code> for
     * @param arrayDao used to retrieve <code>LogicalProges</code> from design
     * @throws AffymetrixArrayDesignReadException if the design file associated with the design couldn't be read.
     */
    public static void createDesignElementList(ArrayDesign design, ArrayDao arrayDao)
            throws AffymetrixArrayDesignReadException {
        new AffymetrixChpCdfDesignElementListUtility(design, arrayDao).createDesignElementList();
    }

    /**
     * Create a new instance.
     * @param design the design to create the <code>DesignElementList</code> for
     * @param arrayDao used to retrieve <code>LogicalProges</code> from design
     */
    private AffymetrixChpCdfDesignElementListUtility(ArrayDesign design, ArrayDao arrayDao) {
        super(design, arrayDao);
    }

    void createDesignElementListEntries(DesignElementList designElementList) throws AffymetrixArrayDesignReadException {
        List<String> probeSetNames = getProbeSetNames();
        List<Long> orderedProbeSetIds = new ArrayList<Long>(BATCH_SIZE);
        for (int i = 0; i < probeSetNames.size(); i += BATCH_SIZE) {
            LOG.info("Retrieving " + BATCH_SIZE + " probe names starting with #" + i);
            List<String> probeSetNamesBatch = probeSetNames.subList(i, Math.min(probeSetNames.size(), i + BATCH_SIZE));
            Map<String, Long> nameToIdMap = getArrayDao().getLogicalProbeNamesToIds(getDesign(), probeSetNamesBatch);
            orderedProbeSetIds.clear();
            for (int j = i; j < i + probeSetNamesBatch.size(); j++) {
                String probeSetName = probeSetNames.get(j);
                orderedProbeSetIds.add(nameToIdMap.get(probeSetName));
            }
            getArrayDao().createDesignElementListEntries(designElementList, i, orderedProbeSetIds);
            LOG.info("Saving " + BATCH_SIZE + " probe names starting with #" + i);
        }
    }

    private List<String> getProbeSetNames() throws AffymetrixArrayDesignReadException {
        AffymetrixCdfReader reader = null;
        try {
            File cdfFile = TemporaryFileCacheLocator.getTemporaryFileCache().getFile(
                    getDesign().getDesignFiles().iterator().next());
            reader = AffymetrixCdfReader.create(cdfFile);
            FusionCDFData fusionCDFData = reader.getCdfData();
            int numProbeSets = fusionCDFData.getHeader().getNumProbeSets();
            List<String> probeSetNames = new ArrayList<String>(numProbeSets);
            for (int index = 0; index < numProbeSets; index++) {
                probeSetNames.add(fusionCDFData.getProbeSetName(index));
            }
            return probeSetNames;
        } finally {
            close(reader);
        }
    }

    private static void close(AffymetrixCdfReader reader) {
        if (reader != null) {
            reader.close();
        }
    }

}
