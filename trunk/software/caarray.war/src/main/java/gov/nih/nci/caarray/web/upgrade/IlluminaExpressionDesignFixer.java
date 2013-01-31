//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ExpressionProbeAnnotation;
import gov.nih.nci.caarray.domain.array.Gene;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.util.io.DelimitedFileReader;
import gov.nih.nci.caarray.util.io.DelimitedFileReaderFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Fixes missing annotation for existing Illumina designs.
 */
public final class IlluminaExpressionDesignFixer extends AbstractMigrator implements Migrator {

    private static final Logger LOG = Logger.getLogger(IlluminaExpressionDesignFixer.class);

    private static final int SEARCH_KEY_HEADER_INDEX = 0;
    private static final int TARGET_HEADER_INDEX = 1;
    private static final int SYMBOL_HEADER_INDEX = 6;
    private static final int DEFINITION_HEADER_INDEX = 10;
    private static final int GENE_EXPRESSION_INDEX = 3;

    private static final int HEADERS_SIZE = 13;

    /**
     * {@inheritDoc}
     */
    public void migrate() throws MigrationStepFailedException {
        List<Long> designIds = getIlluminaExpressionDesignIds();
        for (Long designId : designIds) {
            fix(getDesign(designId));
        }
    }

    private ArrayDesign getDesign(Long designId) {
        return getDesignService().getArrayDesign(designId);
    }

    private void fix(ArrayDesign design) throws MigrationStepFailedException {
        LOG.info("Fixing Illumina design: " + design.getName());
        DelimitedFileReader reader = null;
        Map<String, List<LogicalProbe>> nameToProbeMap = getProbeMap(design);
        ArrayDao arrayDao = getDaoFactory().getArrayDao();
        try {
            reader = getReader(design);
            positionAtAnnotation(reader);
            while (reader.hasNextLine()) {
                List<String> values = reader.nextLine();
                fixLogicalProbe(values, nameToProbeMap);
            }
            arrayDao.flushSession();
            arrayDao.clearSession();
        } catch (IOException e) {
            throw new MigrationStepFailedException("Couldn't read Illumina CSV file.", e);
        } finally {
            close(reader);
        }
    }

    private void positionAtAnnotation(DelimitedFileReader reader) throws IOException {
        boolean isHeader = false;
        while (!isHeader && reader.hasNextLine()) {
            isHeader = isHeaderLine(reader.nextLine());
        }
    }

    private boolean isHeaderLine(List<String> values) {
        return HEADERS_SIZE == values.size()
        && "Search_key".equals(values.get(SEARCH_KEY_HEADER_INDEX))
        && "Target".equals(values.get(TARGET_HEADER_INDEX));
    }

    private void fixLogicalProbe(List<String> values, Map<String, List<LogicalProbe>> nameToProbeMap) {
        String target = values.get(TARGET_HEADER_INDEX);
        for (LogicalProbe probe : nameToProbeMap.get(target)) {
            ExpressionProbeAnnotation annotation = new ExpressionProbeAnnotation();
            annotation.setGene(new Gene());
            annotation.getGene().setSymbol(values.get(SYMBOL_HEADER_INDEX));
            annotation.getGene().setFullName(values.get(DEFINITION_HEADER_INDEX));
            probe.setAnnotation(annotation);
        }
    }

    private Map<String, List<LogicalProbe>> getProbeMap(ArrayDesign design) {
        Map<String, List<LogicalProbe>> nameToProbeMap =
            new HashMap<String, List<LogicalProbe>>(design.getDesignDetails().getLogicalProbes().size());
        for (LogicalProbe probe : design.getDesignDetails().getLogicalProbes()) {
            if (!nameToProbeMap.containsKey(probe.getName())) {
                nameToProbeMap.put(probe.getName(), new ArrayList<LogicalProbe>(1));
            }
            nameToProbeMap.get(probe.getName()).add(probe);
        }
        return nameToProbeMap;
    }

    private DelimitedFileReader getReader(ArrayDesign design) throws MigrationStepFailedException {
        File csvFile = TemporaryFileCacheLocator.getTemporaryFileCache().getFile(design.getFirstDesignFile());
        try {
            return DelimitedFileReaderFactory.INSTANCE.getCsvReader(csvFile);
        } catch (IOException e) {
            throw new MigrationStepFailedException("Couldn't read Illumina CSV file " + csvFile.getName(), e);
        }

    }

    private void close(DelimitedFileReader reader) {
        if (reader != null) {
            reader.close();
        }
    }

    private List<Long> getIlluminaExpressionDesignIds() {
        List<ArrayDesign> allDesigns = getDesignService().getArrayDesigns();
        List<Long> illuminaDesignIds = new ArrayList<Long>();
        for (ArrayDesign design : allDesigns) {
            if (isIlluminaExpressionDesign(design)) {
                illuminaDesignIds.add(design.getId());
            }
        }
        return illuminaDesignIds;
    }

    private boolean isIlluminaExpressionDesign(ArrayDesign design) {
        boolean containsGeneExpression = false;
        for (AssayType currType : design.getAssayTypes()) {
            if (currType.getId() == GENE_EXPRESSION_INDEX) {
                containsGeneExpression = true;
            }
        }
        return design.getFirstDesignFile() != null
                && FileType.ILLUMINA_DESIGN_CSV.equals(design.getFirstDesignFile().getFileType())
                && containsGeneExpression;
    }

}
