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
