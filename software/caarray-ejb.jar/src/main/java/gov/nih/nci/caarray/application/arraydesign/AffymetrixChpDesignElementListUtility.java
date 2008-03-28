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
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import affymetrix.fusion.cdf.FusionCDFData;

/**
 * Utility class used to generate and retrieve the singleton <code>DesignElementList</code> to be used for all parsed
 * CHP files.
 */
public final class AffymetrixChpDesignElementListUtility {
    private static final Logger LOG = Logger.getLogger(AffymetrixChpDesignElementListUtility.class);
    
    private static final String LSID_AUTHORITY = "Affymetrix.com";
    private static final String LSID_NAMESPACE_ELEMENT_LIST = "DesignElementList";
    private static final String LSID_OBJECT_ID_ELEMENT_LIST_PREFIX = "LogicalProbes";
    
    private static final int BATCH_SIZE = 500;

    private AffymetrixChpDesignElementListUtility() {
        super();
    }

    /**
     * Creates the singleton <code>DesignElementList</code> for CHP files associated with the given
     * Affymetrix design. 
     * 
     * @param design the design to create the <code>DesignElementList</code> for
     * @param arrayDao used to retrieve <code>LogicalProges</code> from design
     * @throws AffymetrixCdfReadException if the CDF file associated with the design couldn't be read.
     */
    public static void createDesignElementList(ArrayDesign design, ArrayDao arrayDao)
    throws AffymetrixCdfReadException {
        checkDesign(design);
        List<String> probeSetNames = getProbeSetNames(design);
        DesignElementList designElementList = new DesignElementList();
        designElementList.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE_ELEMENT_LIST
                + ":" + getDesignElementListObjectId(design));
        designElementList.setDesignElementTypeEnum(DesignElementType.LOGICAL_PROBE);
        arrayDao.save(designElementList);
        List<Long> orderedProbeSetIds = new ArrayList<Long>(BATCH_SIZE);
        for (int i = 0; i < probeSetNames.size(); i += BATCH_SIZE) {
            LOG.info("Retrieving " + BATCH_SIZE + " probe names starting with #" + i);
            List<String> probeSetNamesBatch = probeSetNames.subList(i, Math.min(probeSetNames.size(), i + BATCH_SIZE));
            Map<String, Long> nameToIdMap = arrayDao.getLogicalProbeNamesToIds(design, probeSetNamesBatch);
            orderedProbeSetIds.clear();
            for (int j = i; j < i + probeSetNamesBatch.size(); j++) {
                String probeSetName = probeSetNames.get(j);
                orderedProbeSetIds.add(nameToIdMap.get(probeSetName));
            }
            arrayDao.createDesignElementListEntries(designElementList, i, orderedProbeSetIds);
            LOG.info("Saving " + BATCH_SIZE + " probe names starting with #" + i);
        }
    }

    private static List<String> getProbeSetNames(ArrayDesign design) throws AffymetrixCdfReadException {
        AffymetrixCdfReader reader = null;
        try {
            File cdfFile = TemporaryFileCacheLocator.getTemporaryFileCache().getFile(design.getDesignFile());
            reader = AffymetrixCdfReader.create(cdfFile);
            FusionCDFData fusionCDFData = reader.getCdfData();
            int numProbeSets = fusionCDFData.getHeader().getNumProbeSets();
            List<String> probeSetNames = new ArrayList<String>(numProbeSets);
            for (int index = 0; index < numProbeSets; index++) {
                probeSetNames.add(fusionCDFData.getProbeSetName(index));
            }
            return probeSetNames;
        } finally {
            close(reader);
        }
    }

    /**
     * Returns the existing <code>DesignElementList</code> for CHP files associated with the given
     * Affymetrix design, or null if none exists.
     *
     * @param design the design to retrieve the <code>DesignElementList</code> for
     * @param arrayDesignService service instance used to retrieve the list
     * @return the corresponding list or null.
     */
    public static DesignElementList getDesignElementList(ArrayDesign design, ArrayDesignService arrayDesignService) {
        return arrayDesignService.getDesignElementList(LSID_AUTHORITY, LSID_NAMESPACE_ELEMENT_LIST,
                getDesignElementListObjectId(design));
    }

    private static void checkDesign(ArrayDesign design) {
        if (!LSID_AUTHORITY.equals(design.getLsidAuthority())) {
            throw new IllegalArgumentException("ArrayDesign must be an Affymetrix design");
        }
    }

    private static String getDesignElementListObjectId(ArrayDesign design) {
        return LSID_OBJECT_ID_ELEMENT_LIST_PREFIX + "." + design.getLsidObjectId();
    }

    private static void close(AffymetrixCdfReader reader) {
        if (reader != null) {
            reader.close();
        }
    }
}
