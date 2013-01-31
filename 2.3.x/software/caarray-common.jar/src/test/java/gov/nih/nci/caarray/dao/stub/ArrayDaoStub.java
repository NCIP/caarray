//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AssayType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

public class ArrayDaoStub extends AbstractDaoStub implements ArrayDao {

    /**
     * {@inheritDoc}
     */
    public ArrayDesign getArrayDesign(long id) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<ArrayDesign> getArrayDesigns() {
        return new ArrayList<ArrayDesign>();
    }

    /**
     * {@inheritDoc}
     */
    public List<Organization> getArrayDesignProviders() {
        return new ArrayList<Organization>();
    }

    /**
     * {@inheritDoc}
     */
    public List<ArrayDesign> getArrayDesigns(Organization provider, Set<AssayType> assayTypes, boolean importedOnly) {
        return new ArrayList<ArrayDesign>();
    }

    /**
     * {@inheritDoc}
     */
    public ArrayDataType getArrayDataType(ArrayDataTypeDescriptor descriptor) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractArrayData getArrayData(long id) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Hybridization getHybridization(Long id) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public DerivedArrayData getDerivedArrayData(CaArrayFile file) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public RawArrayData getRawArrayData(CaArrayFile file) {
        return null;
    }

    public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        return null;
    }

    public boolean isArrayDesignLocked(Long id) {
        return false;
    }

    public DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        return null;
    }

    public void createDesignElementListEntry(DesignElementList designElementList, int elementIndex, Long logicalProbeId) {
        // empty method
    }

    public Long getLogicalProbeId(ArrayDesign design, String name) {
        return null;
    }

    public void createDesignElementListEntries(DesignElementList designElementList, int startIndex,
            List<Long> logicalProbeIds) {
        // empty method
    }

    public Map<String, Long> getLogicalProbeNamesToIds(ArrayDesign design, List<String> names) {
        return null;
    }

    public void deleteArrayDesignDetails(ArrayDesign design) {
        // empty method
    }

    public List<Long> getLogicalProbeIds(ArrayDesign design, PageSortParams<LogicalProbe> params) {
        return null;
    }

    public void createFeatures(int rows, int cols, ArrayDesignDetails designDetails) {
        // empty method
    }

    public Long getFirstFeatureId(ArrayDesignDetails designDetails) {
        return NumberUtils.LONG_ONE;
    }

    public List<ArrayDesign> getArrayDesigns(ArrayDesignDetails arrayDesignDetails) {
        return new ArrayList<ArrayDesign>();
    }

}
