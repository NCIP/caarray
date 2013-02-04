//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.platforms.AbstractDesignFileHandler;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Illumina BGX array design importer.
 * 
 * @author gax
 */
public class BgxDesignHandler extends AbstractDesignFileHandler {
    private static final Logger LOG = Logger.getLogger(BgxDesignHandler.class);

    /**
     * File Type for BGX array design.
     */
    public static final FileType BGX_FILE_TYPE = new FileType("ILLUMINA_DESIGN_BGX", FileCategory.ARRAY_DESIGN, true,
            "BGX", "BGX.TXT");
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(BGX_FILE_TYPE);

    private CaArrayFile designFile;
    private File fileOnDisk;


    @Inject
    BgxDesignHandler(SessionTransactionManager sessionTransactionManager, DataStorageFacade dataStorageFacade,
            ArrayDao arrayDao, SearchDao searchDao) {
        super(sessionTransactionManager, dataStorageFacade, arrayDao, searchDao);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileType> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean openFiles(Set<CaArrayFile> designFiles) throws PlatformFileReadException {
        if (designFiles == null || designFiles.size() != 1
                || !SUPPORTED_TYPES.contains(designFiles.iterator().next().getFileType())) {
            return false;
        }

        this.designFile = designFiles.iterator().next();
        this.fileOnDisk = getDataStorageFacade().openFile(this.designFile.getDataHandle(), false);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeFiles() {
        getDataStorageFacade().releaseFile(this.designFile.getDataHandle(), false);
        this.fileOnDisk = null;
        this.designFile = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
         return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(ArrayDesign arrayDesign) {
        arrayDesign.setName(FilenameUtils.getBaseName(this.designFile.getName()));
        arrayDesign.setLsidForEntity(IlluminaCsvDesignHandler.LSID_AUTHORITY + ":"
                + IlluminaCsvDesignHandler.LSID_NAMESPACE + ":" + arrayDesign.getName());
        try {
            arrayDesign.setNumberOfFeatures(getNumberOfFeatures());
        } catch (final IOException e) {
            LOG.error(e);
            throw new IllegalStateException("Couldn't read file: ", e);
        }
    }

    private int getNumberOfFeatures() throws IOException {
        final LineCountHandler h = new LineCountHandler();
        processContent(h);
        return h.getCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createDesignDetails(ArrayDesign arrayDesign) {
        try {
            final ArrayDesignDetails details = new ArrayDesignDetails();
            arrayDesign.setDesignDetails(details);
            getArrayDao().save(details);

            final ProbeHandler h = new ProbeHandler(details, getArrayDao(), getSearchDao());
            processContent(h);
            flushAndClearSession();
        } catch (final IOException e) {
            LOG.error(e);
            throw new IllegalStateException("Couldn't read file: ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ValidationResult result) {
        try {
            final FileValidationResult fileResult = result.getOrCreateFileValidationResult(this.designFile.getName());
            this.designFile.setValidationResult(fileResult);

            final BgxValidator h = new BgxValidator(fileResult);
            processContent(h);
        } catch (final IOException e) {
            LOG.error(e);
            throw new IllegalStateException("Couldn't read file: ", e);
        }
    }

    private void processContent(ContentHandler handler) throws IOException {
        final Reader r = openReader();
        try {
            final SectionParser p = new SectionParser(r, '\t');
            p.parse(handler);
        } finally {
            IOUtils.closeQuietly(r);
        }
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private Reader openReader() throws IOException {
        InputStream in = null;
        Reader r;
        try {
            in = new FileInputStream(this.fileOnDisk);
            try {
                in = new GZIPInputStream(in);
            } catch (final IOException e) {
                // not a gzip
            }
            r = new InputStreamReader(in, "UTF8");
            r = new BufferedReader(r);
            return r;
        } catch (final IOException e) {
            IOUtils.closeQuietly(in);
            throw e;
        }
    }

    /**
     * Sections we know about.
     */
    public enum Section {
        /** Heading section. */
        HEADING,
        /** Probes section. */
        PROBES,
        /** Contols section. */
        CONTROLS,
        /** Columns section. */
        COLUMNS;
    }

    /**
     * Column header names we know about.
     */
    public enum Header {
        /**
         * GenBank accession number for the gene.
         */
        ACCESSION,
        /**
         * Decoder identifier.
         */
        ARRAY_ADDRESS_ID,
        /**
         * Chromosome on which the gene is located.
         */
        CHROMOSOME,
        /**
         * Cytogenetic banding region of the chromosome on which the gene associated with the target is.
         */
        CYTOBAND,
        /**
         * Definition field from the GenBank record.
         */
        DEFINITION,
        /**
         * Entrez Gene database identifier for the gene.
         */
        ENTREZ_GENE_ID,
        /**
         * GenBank identifier for the gene.
         */
        GI,
        /**
         * Illuminainternal gene symbol.
         */
        ILMN_GENE,
        /**
         * Probe identifier before BGX annotation files.
         */
        OBSOLETE_PROBE_ID,
        /**
         * Gene Ontology cellular components associated with the gene.
         */
        ONTOLOGY_COMPONENT,
        /**
         * Gene Ontology molecular functions associated with the gene.
         */
        ONTOLOGY_FUNCTION,
        /**
         * Gene Ontology biological processes associated with the gene.
         */
        ONTOLOGY_PROCESS,
        /**
         * Orientation of the probe on the NCBI genome build.
         */
        PROBE_CHR_ORIENTATION,
        /**
         * Genomic position of the probe on the NCBI genome build.
         */
        PROBE_COORDINATES,
        /**
         * Illuminaprobe identifier.
         */
        PROBE_ID,
        /**
         * Sequence of the probe.
         */
        PROBE_SEQUENCE,
        /**
         * Start position of the probe relative to the 5' end of the source transcript sequence.
         */
        PROBE_START,
        /**
         * Information about what the probe is targeting.
         */
        PROBE_TYPE,
        /**
         * NCBI protein accession number.
         */
        PROTEIN_PRODUCT,
        /**
         * Identifier from the NCBI RefSeq database.
         */
        REFSEQ_ID,
        /**
         * Information associated with control probes.
         */
        REPORTER_COMPOSITE_MAP,
        /**
         * Information associated with control probes.
         */
        REPORTER_GROUP_NAME,
        /**
         * Information associated with control probes.
         */
        REPORTER_GROUP_ID,
        /**
         * Internal identifier for the target, useful for custom design array.
         */
        SEARCH_KEY,
        /**
         * Source from which the transcript sequence was obtained.
         */
        SOURCE,
        /**
         * Source's identifier.
         */
        SOURCE_REFERENCE_ID,
        /**
         * Species associated with the gene.
         */
        SPECIES,
        /**
         * Typically, the gene symbol.
         */
        SYMBOL,
        /**
         * Synonyms for the gene (from the GenBank record).
         */
        SYNONYMS,
        /**
         * Illuminainternal transcript identifier.
         */
        TRANSCRIPT,
        /**
         * Identifier from the NCBI UniGene database.
         */
        UNIGENE_ID;
    }
}
