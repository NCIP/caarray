//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

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
