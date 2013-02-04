//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.ExpressionProbeAnnotation;
import gov.nih.nci.caarray.domain.array.Gene;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.plugins.illumina.BgxDesignHandler.Header;

import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * Create Probes for rows in a Probes or Controls section.
 * @author gax
 */
class ProbeHandler extends LineCountHandler {
    private static final int LOGICAL_PROBE_BATCH_SIZE = 5;
    
    private int[] colIndex;
    private ArrayDesignDetails details;
    private ProbeGroup group;
    private final ArrayDao arrayDao;
    private final SearchDao searchDao;
    private boolean visitedProbes;
    private boolean visitedControls;


    /**
     * @param details the array design details to add the probes and groups to.
     * @param dao DAO used by the importer.
     */
    public ProbeHandler(ArrayDesignDetails details, ArrayDao arrayDao, SearchDao searchDao) {
        this.details = details;
        this.arrayDao = arrayDao;
        this.searchDao = searchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean startSection(String sectionName, int lineNumber) {
        String groupName;
        try {
            switch (BgxDesignHandler.Section.valueOf(sectionName.toUpperCase(Locale.getDefault()))) {
                case PROBES :
                    groupName = "Main"; 
                    visitedProbes = true;
                    break;
                case CONTROLS:
                    groupName = "Control"; 
                    visitedControls = true;
                    break;
                default:
                    return false;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        group = new ProbeGroup(details);
        group.setName(groupName);
        arrayDao.save(group);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean endSection(String sectionName, int lineNumber) {
        return !(visitedProbes && visitedControls);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.EmptyCatchBlock")
    public void parseFirstRow(String[] values, int lineNumber) {
        colIndex = new int[BgxDesignHandler.Header.values().length];
        Arrays.fill(colIndex, -1);
        for (int i = 0; i < values.length; i++) {
            String col = values[i].toUpperCase(Locale.getDefault());
            try {
                BgxDesignHandler.Header h = BgxDesignHandler.Header.valueOf(col);
                colIndex[h.ordinal()] = i;
            } catch (IllegalArgumentException e) {
                // unknown column
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void parseRow(String[] values, int lineNumber) {
        super.parseRow(values, lineNumber);
        PhysicalProbe p = createProbe(values);
        arrayDao.save(p);
        if (getCount() % LOGICAL_PROBE_BATCH_SIZE == 0) {
            flushAndClear();
        }
    }

    private PhysicalProbe createProbe(String[] line) {
        PhysicalProbe probe = new PhysicalProbe(details, group);
        String probeId = getValue(BgxDesignHandler.Header.PROBE_ID, line);
        probe.setName(probeId);
        ExpressionProbeAnnotation annotation = new ExpressionProbeAnnotation();
        annotation.setGene(new Gene());
        annotation.getGene().setSymbol(getValue(BgxDesignHandler.Header.SYMBOL, line));
        annotation.getGene().setFullName(getValue(BgxDesignHandler.Header.DEFINITION, line));
        
        addAccession(annotation, Gene.GENBANK, BgxDesignHandler.Header.ACCESSION, line);
        addAccession(annotation, Gene.ENTREZ_GENE, BgxDesignHandler.Header.ENTREZ_GENE_ID, line);
        addAccession(annotation, Gene.UNIGENE, BgxDesignHandler.Header.UNIGENE_ID, line);
        
        probe.setAnnotation(annotation);
        return probe;
    }

    private void addAccession(ExpressionProbeAnnotation annotation, String databaseName, Header header,
            String[] line) {
        final String accessionNumber = getValue(header, line);
        if (!StringUtils.isEmpty(accessionNumber)) {
            annotation.getGene().addAccessionNumber(databaseName, accessionNumber);
        }
    }

    private String getValue(BgxDesignHandler.Header column, String[] line) {
        int idx = colIndex[column.ordinal()];
        return idx == -1 ? null : line[idx];
    }

    private void flushAndClear() {
        arrayDao.flushSession();
        arrayDao.clearSession();
        details = searchDao.retrieve(ArrayDesignDetails.class, details.getId());
        group = searchDao.retrieve(ProbeGroup.class, group.getId());
    }


}
