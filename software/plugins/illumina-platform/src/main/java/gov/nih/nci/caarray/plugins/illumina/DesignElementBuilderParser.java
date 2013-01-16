//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.platforms.DesignElementBuilder;

import java.util.List;

/**
 * Builds a design element list.
 * 
 * @author gax
 * @since 2.4.0
 */
class DesignElementBuilderParser extends AbstractParser {
    private final AbstractHeaderParser<?> header;
    private final DesignElementBuilder builder;

    /**
     * @param header table header info.
     * @param dataSet the dataset to populate with a DesignElementList
     * @param design the design that defines the probes.
     * @param arrayDao dao for prob lookup.
     * @param searchDao dao to evict and reload probes and design element lists.
     */
    DesignElementBuilderParser(AbstractHeaderParser<?> header, DataSet dataSet, ArrayDesign design, ArrayDao arrayDao,
            SearchDao searchDao) {
        this.header = header;
        this.builder = new DesignElementBuilder(dataSet, design, arrayDao, searchDao);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean parse(List<String> row, int lineNum) {
        final String probeId = this.header.parseProbeId(row, lineNum);
        this.builder.addProbe(probeId);
        return true;
    }

    /**
     * to be called when all lines are parsed, to flush remaining batch.
     */
    void finish() {
        this.builder.finish();
    }

    /**
     * @return list of the created design elements.
     */
    public int getElementCount() {
        return this.builder.getElementCount();
    }
}
