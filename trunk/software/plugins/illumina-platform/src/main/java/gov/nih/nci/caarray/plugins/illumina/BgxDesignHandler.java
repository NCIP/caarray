/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
