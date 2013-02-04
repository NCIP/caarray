//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.application.util.Utils;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.platforms.ProbeNamesValidator;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates values in the table.
 * 
 * @param <QT> QuantitationTypeDescriptor
 * @author gax
 * @since 2.4.0
 */
public class HybDataValidator<QT extends Enum<QT> & QuantitationTypeDescriptor> extends AbstractParser {
    private static final int MAX_ERROR_MESSAGES = 1000;
    static final int BATCH_SIZE = 1000;

    private int errorCount;
    private final ArrayDesign design;
    private final AbstractHeaderParser<QT> header;
    private final List<String> probeNames = new ArrayList<String>(BATCH_SIZE);
    private int batchLineNumber = 1;
    private final ArrayDao arrayDao;
    private final Set<String> lookup = new HashSet<String>(BATCH_SIZE);
    private int entryCount;

    /**
     * @param header header info.
     * @param result collector for of validation messages.
     * @param design design associated with the data file we are parsing.
     * @param arrayDao dao for probe lookup.
     */
    public HybDataValidator(AbstractHeaderParser<QT> header, FileValidationResult result, ArrayDesign design,
            ArrayDao arrayDao) {
        super(result);
        this.header = header;
        if (design == null) {
            throw new IllegalArgumentException("No array design found in experiment");
        }
        this.design = design;
        this.arrayDao = arrayDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parse(List<String> row, int lineNum) {
        if (row.size() != this.header.getRowWidth()) {
            error("Expected " + this.header.getRowWidth() + " columns, but found " + row.size(), lineNum, 0);
        }
        final String probeName = this.header.parseProbeId(row, lineNum);
        this.probeNames.add(probeName);
        if (this.probeNames.size() == BATCH_SIZE) {
            this.batchLineNumber = lineNum - BATCH_SIZE;
            processBatch();
        }

        checkDataFormats(row, lineNum);
        this.entryCount++;
        // stop processing before we have too many messages to deal with.
        return this.errorCount < MAX_ERROR_MESSAGES;
    }

    private void processBatch() {
        final List<PhysicalProbe> batchProbes = this.arrayDao.getPhysicalProbeByNames(this.design, this.probeNames);
        final ArrayList<PhysicalProbe> tmp = new ArrayList<PhysicalProbe>(batchProbes.size());
        tmp.addAll(batchProbes);
        for (final PhysicalProbe p : tmp) {
            this.lookup.add(p.getName());
            this.arrayDao.evictObject(p);
            this.arrayDao.evictObject(p.getArrayDesignDetails());
            this.arrayDao.evictObject(p.getProbeGroup());
            this.arrayDao.evictObject(p.getAnnotation());

        }
        for (final String n : this.probeNames) {
            if (!this.lookup.contains(n)) {
                error(ProbeNamesValidator.formatErrorMessage(new String[] {n }, this.design), this.batchLineNumber, 0);
            }
            this.batchLineNumber++;
        }
        this.probeNames.clear();
        this.lookup.clear();
    }

    /**
     * to be called when all lines are parsed, to flush remaining batch.
     */
    void finish() {
        if (this.entryCount == 0) {
            error("Not data rows found", 0, 0);
        }
        processBatch();
    }

    private void checkDataFormats(List<String> row, int lineNum) {
        int col = 1;
        for (final AbstractHeaderParser<QT>.ValueLoader h : this.header.getLoaders()) {
            for (final QT qt : h.getQTypes()) {
                if (qt != null) {
                    boolean malformed = false;
                    final String val = h.getValue(qt, row);
                    switch (qt.getDataType()) {
                    case FLOAT:
                        malformed = !Utils.isFloat(val);
                        break;
                    case STRING:
                        break;
                    default:
                        // add a new case:{} for this new type and validated it.
                    }
                    if (malformed) {
                        error("Malformed value " + val + " for Quantitation Type " + qt + " (expected a "
                                + qt.getDataType() + ")", lineNum, col + 1);
                    }
                }
                col++;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void error(String msg, int line, int col) {
        this.errorCount++;
        super.error(msg, line, col);
    }
}
