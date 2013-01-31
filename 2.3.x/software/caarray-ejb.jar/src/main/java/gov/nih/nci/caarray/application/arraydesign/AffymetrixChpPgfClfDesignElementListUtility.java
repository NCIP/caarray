//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.data.DesignElementList;

import java.util.List;

import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Utility class used to generate and retrieve the singleton <code>DesignElementList</code> to be used for all parsed
 * CHP files, for PGF/CLF-based array designs.
 * @author Steve Lustbader
 */
public final class AffymetrixChpPgfClfDesignElementListUtility extends AbstractAffymetrixChpDesignElementListUtility {
    private static final Logger LOG = Logger.getLogger(AffymetrixChpPgfClfDesignElementListUtility.class);

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
        new AffymetrixChpPgfClfDesignElementListUtility(design, arrayDao).createDesignElementList();
    }

    /**
     * Create a new instance.
     * @param design the design to create the <code>DesignElementList</code> for
     * @param arrayDao used to retrieve <code>LogicalProges</code> from design
     */
    private AffymetrixChpPgfClfDesignElementListUtility(ArrayDesign design, ArrayDao arrayDao) {
        super(design, arrayDao);
    }

    void createDesignElementListEntries(DesignElementList designElementList) throws AffymetrixArrayDesignReadException {
        PageSortParams<LogicalProbe> batch = new PageSortParams<LogicalProbe>(BATCH_SIZE, 0, null, false);
        LOG.info("Retrieving " + BATCH_SIZE + " probe names starting with #" + batch.getIndex());
        List<Long> orderedProbeSetIds = getArrayDao().getLogicalProbeIds(getDesign(), batch);
        while (!orderedProbeSetIds.isEmpty()) {
            LOG.info("Saving " + BATCH_SIZE + " probe names starting with #" + batch.getIndex());
            getArrayDao().createDesignElementListEntries(designElementList, batch.getIndex(), orderedProbeSetIds);
            batch.setIndex(batch.getIndex() + BATCH_SIZE);
            LOG.info("Retrieving " + BATCH_SIZE + " probe names starting with #" + batch.getIndex());
            orderedProbeSetIds = getArrayDao().getLogicalProbeIds(getDesign(), batch);
        }
    }

}
