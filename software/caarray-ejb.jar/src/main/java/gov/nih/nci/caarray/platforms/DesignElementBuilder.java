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

package gov.nih.nci.caarray.platforms;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementReference;
import gov.nih.nci.caarray.domain.data.DesignElementType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a design element list in batches. Currently this is hardcoded to build a list of physical probes,
 * but approach could be extended to other element types.
 * 
 * @author gax, dkokotov
 * @since 2.4.0
 */
public class DesignElementBuilder {
    private static final int DEFAULT_BATCH_SIZE = 1000;
    
    private final ArrayDao arrayDao;
    private final SearchDao searchDao;
    private final List<BatchElement> batch;
    private final List<DesignElementReference> referencesBatch;
    private final ArrayDesign design;
    private final DesignElementList designElementList;
    private int elementCount = 0;
    private final int batchSize;
    private final Map<String, PhysicalProbe> lookup;
    
    /**
     * A single element in the batch.
     */
    private interface BatchElement {
        void addToList(List<String> names);
        AbstractDesignElement lookupDesignElement(Map<String, PhysicalProbe> lookup);
    }

    /**
     * A batch element referred to by a single name.
     */
    private class SimpleBatchElement implements BatchElement {
        private final String name;
        
        public SimpleBatchElement(String name) {
            this.name = name;
        }
        
        public void addToList(List<String> names) {
            names.add(name);            
        }

        public AbstractDesignElement lookupDesignElement(Map<String, PhysicalProbe> map) {           
            return map.get(name);
        }
    }

    /**
     * A batch element having a name and an alternate name.
     */
    private class BatchElementWithAlternateName implements BatchElement {
        private final String name;
        private final String alternateName;
        
        public BatchElementWithAlternateName(String name, String alternateName) {
            this.name = name;
            this.alternateName = alternateName;
        }
        
        public void addToList(List<String> names) {
            names.add(name);
            names.add(alternateName);
        }

        public AbstractDesignElement lookupDesignElement(Map<String, PhysicalProbe> map) {           
            PhysicalProbe probe = map.get(name);
            if (null == probe) {
                probe = map.get(alternateName);
            }
            return probe;
        }
    }

    /**
     * Creates a DesignElementBuilder with a default batch size.
     * 
     * @param dataSet the dataset to populate with a {@link DesignElementList}
     * @param design the design that defines the probes.
     * @param arrayDao an ArrayDao to use
     * @param searchDao an searchDao to use
     */
    public DesignElementBuilder(DataSet dataSet, ArrayDesign design, ArrayDao arrayDao, SearchDao searchDao) {
        this(dataSet, design, arrayDao, searchDao, DEFAULT_BATCH_SIZE);
    }

    /**
     * Creates a DesignElementBuilder with specified batch size.
     * 
     * @param dataSet the dataset to populate with a {@link DesignElementList}
     * @param design the design that defines the probes.
     * @param arrayDao an ArrayDao to use
     * @param searchDao an searchDao to use
     * @param batchSize how many elements should be in a processing batch.
     */
    public DesignElementBuilder(DataSet dataSet, ArrayDesign design, ArrayDao arrayDao, SearchDao searchDao,
            int batchSize) {
        this.arrayDao = arrayDao;
        this.searchDao = searchDao;
        this.design = design;
        designElementList = new DesignElementList();
        designElementList.setDesignElementTypeEnum(DesignElementType.PHYSICAL_PROBE);
        this.batchSize = batchSize; 
        arrayDao.save(designElementList);
        dataSet.setDesignElementList(designElementList);
        lookup = new HashMap<String, PhysicalProbe>(batchSize);
        referencesBatch = new ArrayList<DesignElementReference>(batchSize);
        batch = new ArrayList<BatchElement>(batchSize);
    }

    /**
     * {@inheritDoc}
     */
    public boolean addProbe(String probeName) {
        batch.add(new SimpleBatchElement(probeName));
        return probeAdded();
    }

    /**
     * {@inheritDoc}
     */
    public boolean addProbe(String probeName, String alternateProbeName) {
        if (probeName.equals(alternateProbeName)) {
            return addProbe(probeName);
        }
        batch.add(new BatchElementWithAlternateName(probeName, alternateProbeName));
        return probeAdded();
    }

    private boolean probeAdded() {
        elementCount++;
        if (batch.size() == batchSize) {
            processBatch();
        }
        return true;
    }

    private void processBatch() {        
        for (PhysicalProbe p : getPhysicalProbes()) {
            lookup.put(p.getName(), p);
        }
        int eltIndex = elementCount - batch.size();
        for (BatchElement element : batch) {
            DesignElementReference eltReference = new DesignElementReference();
            eltReference.setDesignElementList(designElementList);
            eltReference.setIndex(eltIndex);
            eltReference.setDesignElement(element.lookupDesignElement(lookup));
            arrayDao.save(eltReference);
            referencesBatch.add(eltReference);
            eltIndex++;
        }
        batch.clear();
        
        searchDao.flushSession();
        for (PhysicalProbe p : lookup.values()) {
            searchDao.evictObject(p);
        }
        lookup.clear();
        
        for (DesignElementReference ref : referencesBatch) {
            searchDao.evictObject(ref);                
        }
        referencesBatch.clear();
        // need to flush again because of seeming hibernate bug so the persistence context does not
        // hold on to memory anymore
        searchDao.flushSession();
    }

    private List<PhysicalProbe> getPhysicalProbes() {
        List<String> names = new ArrayList<String>(batchSize * 2);
        for (BatchElement batchElement : batch) {
            batchElement.addToList(names);
        }
        return arrayDao.getPhysicalProbeByNames(design, names);
    }

    /**
     * to be called when all lines are parsed, to flush remaining batch.
     */
    public void finish() {
        processBatch();
    }

    /**
     * @return list of the created design elements.
     */
    public int getElementCount() {
        return elementCount;
    }
}
