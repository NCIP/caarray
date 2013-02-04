//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;

import java.util.List;

import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.data.search.SortCriterion;
import com.google.inject.Inject;

/**
 * Utility class used to generate and retrieve the singleton <code>DesignElementList</code> to be used for all parsed
 * CHP files, for PGF/CLF-based array designs.
 * @author Steve Lustbader
 */
public final class ChpPgfClfDesignElementListUtility extends AbstractChpDesignElementListUtility {
    private static final Logger LOG = Logger.getLogger(ChpPgfClfDesignElementListUtility.class);

    /**
     * Create a new instance.
     * @param design the design to create the <code>DesignElementList</code> for
     * @param arrayDao used to retrieve <code>LogicalProges</code> from design
     */
    @Inject
    ChpPgfClfDesignElementListUtility(ArrayDao arrayDao, 
            SessionTransactionManager sessionTransactionManager) {
        super(arrayDao, sessionTransactionManager);
    }

    /**
     * {@inheritDoc}
     */
    protected void createDesignElementListEntries(DesignElementList designElementList, ArrayDesign design)
            throws PlatformFileReadException {
        PageSortParams<LogicalProbe> batch = new PageSortParams<LogicalProbe>(BATCH_SIZE, 0,
                (SortCriterion<LogicalProbe>) null, false);
        LOG.info("Retrieving " + BATCH_SIZE + " probe names starting with #" + batch.getIndex());
        List<Long> orderedProbeSetIds = getArrayDao().getLogicalProbeIds(design, batch);
        while (!orderedProbeSetIds.isEmpty()) {
            LOG.info("Saving " + BATCH_SIZE + " probe names starting with #" + batch.getIndex());
            getArrayDao().createDesignElementListEntries(designElementList, batch.getIndex(), orderedProbeSetIds);
            batch.setIndex(batch.getIndex() + BATCH_SIZE);
            if (batch.getIndex() % TRANSACTION_SIZE == 0) {
                LOG.info("Committing design element list");
                flushAndCommitTransaction();
            }
            LOG.info("Retrieving " + BATCH_SIZE + " probe names starting with #" + batch.getIndex());
            orderedProbeSetIds = getArrayDao().getLogicalProbeIds(design, batch);
        }
    }
}
