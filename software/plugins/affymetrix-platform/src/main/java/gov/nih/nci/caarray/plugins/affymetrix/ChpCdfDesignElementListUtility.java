//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import affymetrix.fusion.cdf.FusionCDFData;

import com.google.inject.Inject;

/**
 * Utility class used to generate and retrieve the singleton <code>DesignElementList</code> to be used for all parsed
 * CHP files, for CDF-based array designs.
 * 
 * @author Steve Lustbader
 */
final class ChpCdfDesignElementListUtility extends AbstractChpDesignElementListUtility {
    private static final Logger LOG = Logger.getLogger(ChpCdfDesignElementListUtility.class);

    private final DataStorageFacade dataStorageFacade;

    /**
     * Create a new instance.
     * 
     * @param design the design to create the <code>DesignElementList</code> for
     * @param arrayDao used to retrieve <code>LogicalProges</code> from design
     */
    @Inject
    ChpCdfDesignElementListUtility(ArrayDao arrayDao, SessionTransactionManager sessionTransactionManager,
            DataStorageFacade dataStorageFacade) {
        super(arrayDao, sessionTransactionManager);
        this.dataStorageFacade = dataStorageFacade;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createDesignElementListEntries(DesignElementList designElementList, ArrayDesign design)
            throws PlatformFileReadException {
        final List<String> probeSetNames = getProbeSetNames(design);
        final List<Long> orderedProbeSetIds = new ArrayList<Long>(BATCH_SIZE);
        for (int i = 0; i < probeSetNames.size(); i += BATCH_SIZE) {
            LOG.info("Retrieving " + BATCH_SIZE + " probe names starting with #" + i);
            final List<String> probeSetNamesBatch = probeSetNames.subList(i,
                    Math.min(probeSetNames.size(), i + BATCH_SIZE));
            final Map<String, Long> nameToIdMap = getArrayDao().getLogicalProbeNamesToIds(design, probeSetNamesBatch);
            orderedProbeSetIds.clear();
            for (int j = i; j < i + probeSetNamesBatch.size(); j++) {
                final String probeSetName = probeSetNames.get(j);
                orderedProbeSetIds.add(nameToIdMap.get(probeSetName));
            }
            getArrayDao().createDesignElementListEntries(designElementList, i, orderedProbeSetIds);
            LOG.info("Saving " + BATCH_SIZE + " probe names starting with #" + i);
        }
    }

    private List<String> getProbeSetNames(ArrayDesign design) throws PlatformFileReadException {
        CdfReader reader = null;
        File cdfFile = null;
        try {
            cdfFile = this.dataStorageFacade.openFile(design.getDesignFiles().iterator().next().getDataHandle(), false);
            reader = new CdfReader(cdfFile);
            final FusionCDFData fusionCDFData = reader.getCdfData();
            final int numProbeSets = fusionCDFData.getHeader().getNumProbeSets();
            final List<String> probeSetNames = new ArrayList<String>(numProbeSets);
            for (int index = 0; index < numProbeSets; index++) {
                probeSetNames.add(fusionCDFData.getProbeSetName(index));
            }
            return probeSetNames;
        } finally {
            if (cdfFile != null) {
                this.dataStorageFacade.releaseFile(design.getDesignFiles().iterator().next().getDataHandle(), false);
            }
            if (reader != null) {
                reader.close();
            }
        }
    }
}
