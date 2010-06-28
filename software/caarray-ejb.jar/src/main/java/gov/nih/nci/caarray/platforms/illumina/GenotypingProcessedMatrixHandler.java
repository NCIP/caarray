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

package gov.nih.nci.caarray.platforms.illumina;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.DefaultValueParser;
import gov.nih.nci.caarray.platforms.FileManager;
import gov.nih.nci.caarray.platforms.ValueParser;
import gov.nih.nci.caarray.platforms.spi.AbstractDataFileHandler;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;
import com.google.inject.Inject;

/**
 * Illumina Genotyping Processed Matrix importer.
 * 
 * @since 2.4.0
 * @author gax
 */
final class GenotypingProcessedMatrixHandler extends AbstractDataFileHandler {
    private static final Logger LOG = Logger.getLogger(GenotypingProcessedMatrixHandler.class);
    private static final long ONE_MINUTE = 1000L * 60L;
    
    private final ValueParser valueParser = new DefaultValueParser();
    private final ArrayDao arrayDao;
    private final SearchDao searchDao;
    
    /**
     * 
     */
    @Inject
    GenotypingProcessedMatrixHandler(FileManager fileManager, ArrayDao arrayDao, SearchDao searchDao) {
        super(fileManager);
        this.arrayDao = arrayDao;
        this.searchDao = searchDao;
    }

    /**
     * {@inheritDoc}
     */
    public boolean parsesData() {
        return true;
    }
    
    @Override
    protected boolean acceptFileType(FileType type) {
        return FileType.ILLUMINA_GENOTYPING_PROCESSED_MATRIX_TXT == type;
    }
    /**
     * {@inheritDoc}
     */
    public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        DefaultHeaderProcessor proc = new DefaultHeaderProcessor();
        processFile(proc, null, getFile());
        List<IlluminaGenotypingProcessedMatrixQuantitationType> l = proc.getLoaders().get(0).getQTypes();
        return l.toArray(new IlluminaGenotypingProcessedMatrixQuantitationType[l.size()]);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getHybridizationNames() {
        DefaultHeaderProcessor proc = new DefaultHeaderProcessor();
        processFile(proc, null, getFile());
        return proc.getHybNames();
    }

    /**
     * {@inheritDoc}
     */
    public List<LSID> getReferencedArrayDesignCandidateIds() {
        // cannot determine from file
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public void validate(MageTabDocumentSet mTabSet, final FileValidationResult result, ArrayDesign design) {
        ValidatingHeaderParser headerProc = new ValidatingHeaderParser(result, mTabSet);
        HybDataValidator<IlluminaGenotypingProcessedMatrixQuantitationType> proc
                = new HybDataValidator<IlluminaGenotypingProcessedMatrixQuantitationType>(headerProc, result, design,
                arrayDao);
        processFile(headerProc, proc, getFile());
        proc.finish();
    }
    
    /**
     * {@inheritDoc}
     */        
    public boolean requiresMageTab() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design) {
        // pass 1: load design element and count row.
        DefaultHeaderProcessor header = new DefaultHeaderProcessor();
        DesignElementBuilder designElementProc = new DesignElementBuilder(header, dataSet, design, arrayDao, searchDao);
        processFile(header, designElementProc, getFile());
        designElementProc.finish();
        dataSet.prepareColumns(types, designElementProc.getElementCount());
        LOG.info("Pass 1/2 loaded " + designElementProc.getElementCount() + " design elements.");
        // pass 2: fill columns.
        header = new DefaultHeaderProcessor();
        HybDataBuilder<IlluminaGenotypingProcessedMatrixQuantitationType> loader
                = new HybDataBuilder<IlluminaGenotypingProcessedMatrixQuantitationType>(dataSet, header, valueParser);
        processFile(header, loader, getFile());
        LOG.info("Pass 2/2 loaded data.");
    }

    /**
     * {@inheritDoc}
     */
    public ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
        return IlluminaArrayDataTypes.ILLUMINA_GENOTYPING_PROCESSED_MATRIX;
    }

    private static DelimitedFileReader openReader(File dataFile) {
        try {
            return new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(dataFile);
        } catch (IOException e) {
            throw new IllegalStateException("File " + dataFile.getName() + " could not be read", e);
        }
    }

    private void processFile(DefaultHeaderProcessor headerProc, AbstractParser rowProc, File file) {
        DelimitedFileReader r = openReader(file);
        long ticker = System.currentTimeMillis();
        try {
            boolean keepGoing = r.hasNextLine() && headerProc.parse(r.nextLine(), r.getCurrentLineNumber());
            while (rowProc != null && keepGoing && r.hasNextLine()) {
                keepGoing = rowProc.parse(r.nextLine(), r.getCurrentLineNumber());
                long now = System.currentTimeMillis();
                ticker = tick(ticker, "...still processing around line " + r.getCurrentLineNumber()  + " with "
                        + rowProc);
            }
        } catch (IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            r.close();
        }
    }

    // CHECKSTYLE:OFF
    static long tick(long lastTick, String msg) {
        long now = System.currentTimeMillis();
        if (lastTick + ONE_MINUTE <= now) {
            Runtime r = Runtime.getRuntime();
            LOG.info(msg + " free=" + (r.freeMemory() / 1048576) + "/" + (r.totalMemory()/1048576) + "MB");
            return now;
        }
        return lastTick;
    }
 // CHECKSTYLE:ON
    /**
     * matrix header parser with extra validation.
     */
    static class ValidatingHeaderParser extends DefaultHeaderProcessor {
        private final MageTabDocumentSet mTabSet;

        ValidatingHeaderParser(FileValidationResult result, MageTabDocumentSet mTabSet) {
            super(result);
            this.mTabSet = mTabSet;
        }

        @Override
        protected boolean parseLoaders(List<String> line, int lineNum) {
            boolean ok = super.parseLoaders(line, lineNum);
            if (ok) {
                validateSdrfNames(mTabSet, lineNum);
                validateColumnConsistency(lineNum);
            }
            return ok;
        }
    }

    /**
     * matrix header parser.
     */
    static class DefaultHeaderProcessor
            extends AbstractHeaderParser<IlluminaGenotypingProcessedMatrixQuantitationType> {
        private int period; // hyb block width
        private boolean compositColNames;

        public DefaultHeaderProcessor() {
            super();
        }

        protected DefaultHeaderProcessor(FileValidationResult results) {
            super(new MessageHandler.ValidationMessageHander(results),
                    IlluminaGenotypingProcessedMatrixQuantitationType.class);
        }

        /**
         * {@inheritDoc}
         */
        protected boolean parseLoaders(List<String> row, int lineNum) {
            super.setProbIdColumn(row.size());
            super.setProbIdColumn(0);
            
            boolean ok = checkMinRequiredColumns(row, lineNum);
            if (!ok) {
                return false;
            }
            // count how many times the first data column appears in the table.
            final String firstQTColName = row.get(2);
            final int dotIdx = firstQTColName.indexOf('.');
            compositColNames = dotIdx != -1;
            Predicate p = new Predicate() {
                public boolean evaluate(Object object) {
                    return compositColNames
                            ? object.toString().endsWith(firstQTColName.substring(dotIdx))
                            : object.toString().equals(firstQTColName);
                }
            };
            int hybCount = CollectionUtils.countMatches(row, p); // repetitions
            period = (row.size() - 1) / hybCount;
            if ((row.size() - 1) % hybCount != 0) {
                error("Not all hybridizations have the same columns", lineNum, 3);
                return false;
            }

            int col = 1; // 0th col is the probe id.
            for (int h = 0; h < hybCount; h++, col += period) {
                buildHybBlock(col, row, lineNum, h);
            }

            return true;
        }

        private void buildHybBlock(int blockStart, List<String> row, int lineNum, int block) {
            int col = blockStart;

            AbstractHeaderParser<IlluminaGenotypingProcessedMatrixQuantitationType>.ValueLoader loader
                    = addValueLoader(row.get(blockStart));
            for (int c = 0; c < period; c++, col++) {
                String localName = row.get(col);
                if (c != 0 && compositColNames) {
                    localName = localName.substring(localName.indexOf('.') + 1);
                }
                Header hdr = null;
                try {
                    hdr = Header.valueOf(localName.toUpperCase(Locale.getDefault()));
                } catch (IllegalArgumentException e) {
                    if (c == 0) {
                        hdr = Header.ALLELE;
                    } else {
                        warn("Unsupported Column " + localName, lineNum, col + 1);
                    }
                }
                if (hdr != null && hdr.getQType() != null) {
                    loader.addMapping(hdr.getQType(), col, lineNum);
                }
            }
        }

        /**
         * Check if the table has the minimum number of columns : the prob id, at least one hyb, and a data column.
         */
        private boolean checkMinRequiredColumns(List<String> row, int lineNum) {
            if (row.isEmpty()) {
                error("Not a header row", lineNum, 0);
                return false;
            } else if (!Header.isIlmnIdHeaderName(row.get(0))) {
                error("Missing IlmnID, ID_REF, or ID in first column, first line. (Found " + row.get(0) + ")",
                        lineNum, 1);
                return false;
            } else if (row.size() == 1) {
                error("Missing \'Value\' (hybridization/sample name) column", lineNum, 2);
                return false;
            } else if (row.size() == 2) {
                error("Missing Quantitation Type (measurement) column", lineNum, 3);
                return false;
            } else {
                return true;
            }
        }

        /**
         * Expected column headers in the tabular TSV file.
         */
        static enum Header {
            /**
             * ILMNID (probe ID).
             */
            ILMNID,
            /**
             * ID_REF (probe ID).
             */
            ID_REF,
            /**
             * ID (probe ID).
             */
            ID,
            /**
             * Implicite, could be anything or sample name.
             * @see IlluminaGenotypingProcessedMatrixQuantitationType#ALLELE
             */
            ALLELE,
            /**
             * GC_SCORE.
             * @see IlluminaGenotypingProcessedMatrixQuantitationType#GC_SCORE
             */
            GC_SCORE,
            /**
             * THETA.
             * @see IlluminaGenotypingProcessedMatrixQuantitationType#THETA
             */
            THETA,
            /**
             * R.
             * @see IlluminaGenotypingProcessedMatrixQuantitationType#R
             */
            R,
            /**
             * B_ALLELE_FREQ.
             * @see IlluminaGenotypingProcessedMatrixQuantitationType#B_ALLELE_FREQ
             */
            B_ALLELE_FREQ,
            /**
             * LOG_R_RATIO.
             * @see IlluminaGenotypingProcessedMatrixQuantitationType#LOG_R_RATIO
             */
            LOG_R_RATIO;

            private IlluminaGenotypingProcessedMatrixQuantitationType qType;

            private Header() {
                try {
                    this.qType = IlluminaGenotypingProcessedMatrixQuantitationType.valueOf(name());
                } catch (IllegalArgumentException e) {
                    this.qType = null;
                }
            }

            /**
             * @return the QuantitationType for the column.
             */
            public IlluminaGenotypingProcessedMatrixQuantitationType getQType() {
                return qType;
            }

            static boolean isIlmnIdHeaderName(String colName) {
                return ILMNID.name().equalsIgnoreCase(colName)
                        || ID_REF.name().equalsIgnoreCase(colName)
                        || ID.name().equalsIgnoreCase(colName);
            }
        }
    }
}
