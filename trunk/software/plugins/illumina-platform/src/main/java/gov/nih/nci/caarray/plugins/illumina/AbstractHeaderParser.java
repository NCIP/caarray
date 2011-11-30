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

import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Parse header structure and check the validity.
 * @param <QT> QuantitationTypeDescriptor
 * @author gax
 * @since 2.4.0
 */
public abstract class AbstractHeaderParser <QT extends Enum<QT> & QuantitationTypeDescriptor> extends AbstractParser {

    private final List<ValueLoader> loaders = new ArrayList<ValueLoader>();
    private int probIdColumn = -1;
    private int rowWidth = -1;
    private final Class<QT> qTypeEnum;

    /**
     * general ctor.
     * @param messages handler to allow diffrent ways of handling of error encounterd in a header.
     * @param qTypeEnum qType ennum type that will be recognized and read from the header.
     */
    protected AbstractHeaderParser(MessageHandler messages, Class<QT> qTypeEnum) {
        super(messages);
        this.qTypeEnum = qTypeEnum;
    }

    /**
     * ctor with {@link MessageHandler.DefaultMessageHandler}.
     * @param qTypeEnum qType ennum type that will be recognized and read from the header.     *
     */
    protected AbstractHeaderParser(Class<QT> qTypeEnum) {
        super();
        this.qTypeEnum = qTypeEnum;
    }

    /**
     * default ctor with {@link MessageHandler.DefaultMessageHandler}, and the generic parameter of the subclass.
     */
    @SuppressWarnings("unchecked")
    protected AbstractHeaderParser() {
        super();
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        this.qTypeEnum = (Class<QT>) parameterizedType.getActualTypeArguments()[0];
    }

    /**
     * {@inheritDoc}
     */
    public boolean parse(List<String> row, int line) {
        rowWidth = row.size();
        boolean ok = parseLoaders(row, line);
        if (ok) {
            if (loaders.isEmpty()) {
                error("No samples found", line, 0);
            }
            if (probIdColumn == -1) {
                error("No Probe id column found", line, 0);
            }
        }
        return ok;
    }

    /**
     * get the Hyb loaders constructed from the header.
     * @return list of hyb loaders.
     */
    public List<ValueLoader> getLoaders() {
        return loaders;
    }

    /**
     * @param probIdColumn index of the column that holds the probe ID.
     */
    protected void setProbIdColumn(int probIdColumn) {
        this.probIdColumn = probIdColumn;
    }

    /**
     * @return number of columns each row has.
     */
    public int getRowWidth() {
        return rowWidth;
    }

    /**
     * Get the list of hyb names collected.
     * @return hyb names.
     */
    public List<String> getHybNames() {
        List<String> hybNames = new ArrayList<String>();
        for (AbstractHeaderParser<QT>.ValueLoader block : loaders) {
            hybNames.add(block.getHybName());
        }
        return hybNames;
    }

    /**
     * This method should at some point call
     * {@link #addValueLoader(java.lang.String),
     * {@link #setProbIdColumn(int)}, and {@link #setRowWidth(int) with the proper values that will allow data rows
     * to be loaded.
     * @param row one row from the matrix file.
     * @param line the line number in the file.
     * @return true if the header is valid and can be used to read the rows.
     */
    protected abstract boolean parseLoaders(List<String> row, int line);

    /**
     * @param the line to get the probe id from.
     * @param line the line number we are processing.
     * @return the id of the probe
     */
    String parseProbeId(List<String> row, int line) {
        return row.get(probIdColumn);
    }

    private void validateSdrfNames(Set<String> sdrfHybNames, int lineNum) {
        for (ValueLoader h : loaders) {
            String hybName = h.getHybName();
            if (sdrfHybNames != null && !sdrfHybNames.contains(hybName)) {
                error("Hybridization " + hybName + " is not referenced in SDRF", lineNum, 0);
            }
        }
    }

    /**
     * Verifies that the SDRF file, if present, contains all hybs in the matrix file.
     * @param mTabSet MAGE TAB documents set containg the SDRF.
     * @param lineNum the line number in the matrix file we are processing (for better validation messages).
     */
    public void validateSdrfNames(MageTabDocumentSet mTabSet, int lineNum) {
        validateSdrfNames(getHybNames(mTabSet), lineNum);
    }

    private static Set<String> getHybNames(MageTabDocumentSet mTabSet) {
        Set<String> sdrfHybNames = null;
        if (mTabSet != null && mTabSet.getSdrfDocuments() != null && !mTabSet.getSdrfDocuments().isEmpty()) {
            sdrfHybNames = new HashSet<String>();
            for (List<gov.nih.nci.caarray.magetab.sdrf.Hybridization> l : mTabSet.getSdrfHybridizations().values()) {
                for (gov.nih.nci.caarray.magetab.sdrf.Hybridization h : l) {
                    sdrfHybNames.add(h.getName());
                }
            }
        }
        return sdrfHybNames;
    }

    /**
     * Verify that all hybs have the same set of QTypes defined in the file.
     * @param lineNum the line number in the matrix file we are processing (for better validation messages).
     */
    public void validateColumnConsistency(int lineNum) {
        ValueLoader reference = null;
        for (ValueLoader vl : loaders) {
            if (reference == null) {
                reference = vl;
                if (reference.qTypes.isEmpty()) {
                    error("No quantitation type columns found for samples " + reference.getHybName(), lineNum, 0);
                }
                continue;
            }
            List<QT> a = reference.getQTypes();
            List<QT> b = vl.getQTypes();
            if (!a.containsAll(b) || !b.containsAll(a)) {
                error("Inconsistent quantitation type column between samples " + reference.getHybName() + " and "
                        + vl.getHybName(), lineNum, 0);
            }
        }
    }

    /**
     * NOTE: make sure you populate the column mappings for the newly created loader.
     * @param hybName the name of the hyb/sample we will be reading values for.
     * @return the created hyb loader.
     */
    protected ValueLoader addValueLoader(String hybName) {
        ValueLoader loader = new ValueLoader(hybName);
        loaders.add(loader);
        return loader;
    }

    /**
     * A Hyb Data reader that maps QTypes to columns in the data matrix, constructed from the table header.
     */
    public class ValueLoader {
        private int[] qTypeMap;
        private final String hybName;
        private final List<QT> qTypes = new ArrayList<QT>();

        /**
         * @param hybName the name of the hyb/sample we will be reading values for.
         */
        protected ValueLoader(String hybName) {
            this.hybName = hybName;
            qTypeMap = new int[EnumSet.allOf(qTypeEnum).size()];
            Arrays.fill(qTypeMap, -1);
        }

        /**
         * @return the name of the hyb/sample we will be reading values for.
         */
        public String getHybName() {
            return hybName;
        }

        /**
         * @param qType the QType that should be read.
         * @param col index of the column folding the value for the qType.
         * @param line line number that was used to create this mapping.
         */
        public void addMapping(QT qType, int col, int line) {
            if (qTypeMap[qType.ordinal()] == -1) {
                qTypeMap[qType.ordinal()] = col;
                qTypes.add(qType);
            } else {
                warn("QuantitationType " + qType + " already defined in column " + (qTypeMap[qType.ordinal()] + 1),
                        line, col + 1);
            }            
        }

        /**
         * @param qTCol QuantitationType of interest.
         * @param row the data row.
         * @return the value in the QType's column.
         */
        public String getValue(QT qTCol, List<String> row) {
            int i = qTypeMap[qTCol.ordinal()];
            return i == -1 ? null : row.get(i);
        }

        /**
         * @return the qTypes that will be collected by this loader.
         */
        public List<QT> getQTypes() {
            return qTypes;
        }
    }
}
