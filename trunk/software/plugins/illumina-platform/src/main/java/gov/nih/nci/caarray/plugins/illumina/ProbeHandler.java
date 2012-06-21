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
