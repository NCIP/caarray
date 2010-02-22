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

package gov.nih.nci.caarray.application.arraydata.illumina;

import gov.nih.nci.caarray.application.arraydata.IlluminaGenotypingProcessedMatrixHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * processor that can compile the header into an internal model for fast row processing.
 * @author gax
 * @since 3.4.0
 * @see IlluminaGenotypingProcessedMatrixHandler
 */
public class DefaultHeaderProcessor implements IlluminaGenotypingProcessedMatrixHandler.HeaderProcessor {
    private static final Logger LOG = Logger.getLogger(IlluminaGenotypingProcessedMatrixHandler.class);
    private int period; // hyb block width
    private boolean compositColNames;
    private HybBlock[] hybBlocks;
    private int rowWidth;

    /**
     * @return number of columns in a block.
     */
    protected int getPeriod() {
        return period;
    }

    /**
     * @return parsed hybs.
     */
    public HybBlock[] getHybBlocks() {
        return hybBlocks;
    }

    int getRowWidth() {
        return rowWidth;
    }

    /**
     * {@inheritDoc}
     */
    public boolean parseHeader(List<String> row, int lineNum) {
        List<String> uppercaseHeader = new ArrayList<String>(row.size());
        for (String h : row) {
            uppercaseHeader.add(StringUtils.upperCase(h));
        }
        rowWidth = row.size();
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
        hybBlocks = new HybBlock[hybCount];
        int col = 1; // 0th col is the probe id.
        for (int h = 0; h < hybCount; h++, col += period) {
            buildHybBlock(col, row, lineNum, h);
        }
        return true;
    }

    private void buildHybBlock(int blockStart, List<String> row, int lineNum, int block) {
        int col = blockStart;
        HybBlock hb = new HybBlock();
        hb.startCol = col;
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
            if (hdr != null) {
                hb.qTypes[c] = hdr.getQType();
                hb.qTypeIndexes[hdr.getQType().ordinal()] = col;
            }
            hb.qTypeColNames[c] = localName;
            hb.colNameIndexes[c] = col;
        }
        hybBlocks[block] = hb;
        checkBlockColPattern(block, hb, lineNum, col);
    }

    private void checkBlockColPattern(int block, HybBlock hb, int lineNum, int col) {
        if (block > 0) {
            for (int i = 1; i < hb.qTypeColNames.length; i++) {
                if (!hybBlocks[0].qTypeColNames[i].equalsIgnoreCase(hb.qTypeColNames[i])) {
                    error("Column name " + hb.qTypeColNames[i] + " breaks pattern", lineNum, col + i + 1);
                }
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
            error("Missing IlmnID, ID_REF, or ID in first column, first line. (Found " + row.get(0) + ")", lineNum, 1);
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
     * {@inheritDoc}
     */
    protected void error(String msg, int line, int col) {
        LOG.error(msg);
    }

    /**
     * {@inheritDoc}
     */
    protected void warn(String msg, int line, int col) {
        LOG.warn(msg);
    }

    /**
     * {@inheritDoc}
     */
    protected void info(String msg, int line, int col) {
        LOG.info(msg);
    }

    /**
     * Set of consecutive columns of a hyb's data.
     */
    public final class HybBlock {

        private int startCol;
        private final String[] qTypeColNames = new String[period];
        private final int[] colNameIndexes = new int[period];
        private final IlluminaGenotypingProcessedMatrixQuantitationType[] qTypes
                = new IlluminaGenotypingProcessedMatrixQuantitationType[period];
        private final int[] qTypeIndexes = new int[IlluminaGenotypingProcessedMatrixQuantitationType.values().length];

        HybBlock() {
            Arrays.fill(qTypeIndexes, -1);
        }

        /**
         * @param qTCol QuantitationType of interest.
         * @param row the data row.
         * @return the value in the QType's column.
         */
        public String getValue(IlluminaGenotypingProcessedMatrixQuantitationType qTCol, List<String> row) {
            int i = qTypeIndexes[qTCol.ordinal()];
            return i == -1 ? null : row.get(i);
        }

        /**
         * @return all the column names.
         */
        public String[] getQTypeColNames() {
            return qTypeColNames;
        }

        /**
         * @return all identified (known) qTypes in a block.
         */
        public IlluminaGenotypingProcessedMatrixQuantitationType[] getQTypes() {
            return qTypes;
        }

        /**
         * @return index of the first column in a block.
         */
        public int getStartCol() {
            return startCol;
        }
    }

    /**
     * Expected column headers in the tabular TSV file.
     */
    public static enum Header {
        /**
         * {@value}.
         */
        ILMNID,
        /**
         * {@value}.
         */
        ID_REF,
        /**
         * {@value}.
         */
        ID,
        /**
         * {@value}. Implicite, could be anything or sample name
         */
        ALLELE,
        /**
         * {@value}.
         */
        GC_SCORE,
        /**
         * {@value}.
         */
        THETA,
        /**
         * {@value}.
         */
        R,
        /**
         * {@value}.
         */
        B_ALLELE_FREQ,
        /**
         * {@value}.
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
