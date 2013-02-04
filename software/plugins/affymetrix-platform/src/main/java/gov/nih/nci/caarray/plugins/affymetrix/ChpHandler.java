//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.AbstractDataFileHandler;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import affymetrix.fusion.chp.FusionCHPData;
import affymetrix.fusion.chp.FusionCHPDataReg;
import affymetrix.fusion.chp.FusionCHPHeader;
import affymetrix.fusion.chp.FusionCHPLegacyData;
import affymetrix.fusion.chp.FusionCHPMultiDataData;
import affymetrix.fusion.chp.FusionCHPQuantificationData;
import affymetrix.fusion.chp.FusionCHPQuantificationDetectionData;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Array data handler for all versions and types of the Affymetrix CHP file format.
 */
public class ChpHandler extends AbstractDataFileHandler {
    private static final String LSID_AUTHORITY = "Affymetrix.com";
    private static final String LSID_NAMESPACE_DESIGN = "PhysicalArrayDesign";

    /**
     * FileType instance for CHP file type.
     */
    public static final FileType CHP_FILE_TYPE = new FileType("AFFYMETRIX_CHP", FileCategory.DERIVED_DATA, true, "CHP",
            "CNCHP");
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(CHP_FILE_TYPE);

    private final AbstractChpDesignElementListUtility chpDesignElementListUtility;

    @Inject
    ChpHandler(DataStorageFacade dataStorageFacade,
            @Named("cdf") AbstractChpDesignElementListUtility chpDesignElementListUtility) {
        super(dataStorageFacade);
        this.chpDesignElementListUtility = chpDesignElementListUtility;

        FusionCHPLegacyData.registerReader();
        FusionCHPQuantificationData.registerReader();
        FusionCHPQuantificationDetectionData.registerReader();
        FusionCHPMultiDataData.registerReader();
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
    public ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
        final AbstractCHPData<?> chpData = getChpData(getFile());
        return chpData.getArrayDataTypeDescriptor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        final AbstractCHPData<?> chpData = getChpData(getFile());
        return chpData.getQuantitationTypeDescriptors();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadData(final DataSet dataSet, final List<QuantitationType> types, ArrayDesign design) {
        final Set<QuantitationType> typeSet = new HashSet<QuantitationType>();
        typeSet.addAll(types);
        if (dataSet.getDesignElementList() == null) {
            getDesignElementList(dataSet, design);
        }
        final AbstractCHPData<?> chpData = getChpData(getFile());
        dataSet.prepareColumns(types, chpData.getNumProbeSets());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        chpData.loadData(typeSet, hybridizationData);
    }

    private void getDesignElementList(final DataSet dataSet, ArrayDesign design) {
        final DesignElementList probeList = this.chpDesignElementListUtility.getDesignElementList(design);
        dataSet.setDesignElementList(probeList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(final MageTabDocumentSet mTabSet, final FileValidationResult result, ArrayDesign design) {
        final AbstractCHPData<?> chpData = getChpData(getFile());
        if (chpData == null) {
            result.addMessage(Type.ERROR, "Couldn't read Affymetrix CHP file: " + getFile().getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresMageTab() {
        // TODO Auto-generated method stub
        return false;
    }

    private AbstractCHPData<?> getChpData(final File file) {
        AbstractCHPData<?> chpData = null;
        final FusionCHPData data = FusionCHPDataReg.read(file.getAbsolutePath());

        if (data instanceof FusionCHPLegacyData) {
            chpData = getChpLegacyData(data);
        } else if (data instanceof FusionCHPQuantificationData) {
            chpData = getChpExpressionSignalData(data);
        } else if (data instanceof FusionCHPQuantificationDetectionData) {
            chpData = getChpExpressionDABGSignalData(data);
        } else if (data instanceof FusionCHPMultiDataData) {
            chpData = getChpMultiDataData(data);
        }

        if (chpData == null) {
            throw new IllegalArgumentException("Unsupported Affymetrix CHP type");
        }

        return chpData;
    }

    private AbstractCHPData<?> getChpLegacyData(final FusionCHPData fusionCHPData) {
        final FusionCHPLegacyData fusionCHPLegacyData = FusionCHPLegacyData.fromBase(fusionCHPData);
        final int assayType = fusionCHPLegacyData.getHeader().getAssayType();
        switch (assayType) {
        case FusionCHPHeader.EXPRESSION_ASSAY:
            return new CHPExpressionMAS5Data(fusionCHPLegacyData);
        case FusionCHPHeader.GENOTYPING_ASSAY:
            return new CHPSNPData(fusionCHPLegacyData);
        default:
            return null;
        }
    }

    private AbstractCHPData<?> getChpExpressionSignalData(final FusionCHPData fusionCHPData) {
        final FusionCHPQuantificationData fusionCHPQuantificationData =
                FusionCHPQuantificationData.fromBase(fusionCHPData);
        return new CHPExpressionSignalData(fusionCHPQuantificationData);
    }

    private AbstractCHPData<?> getChpExpressionDABGSignalData(final FusionCHPData fusionCHPData) {
        final FusionCHPQuantificationDetectionData fusionCHPQuantificationData =
                FusionCHPQuantificationDetectionData.fromBase(fusionCHPData);
        return new CHPExpressionSignalDetectionData(fusionCHPQuantificationData);
    }

    private AbstractCHPData<?> getChpMultiDataData(final FusionCHPData fusionCHPData) {
        final FusionCHPMultiDataData fusionCHPMultiDataData = FusionCHPMultiDataData.fromBase(fusionCHPData);

        final String algorithm = fusionCHPMultiDataData.getAlgName();

        final boolean algorithmIsBRLMM = algorithm.startsWith("brlmm");
        final boolean algorithmIsBirdseed = algorithm.startsWith("birdseed");
        final boolean algorithmIsAxiomGT = algorithm.startsWith("axiomgt");
        final boolean algorithmIsCN4 = "CN4".equals(algorithm);
        final boolean algorithmIsCN5 = "Analyzer Server".equals(algorithm);

        if (algorithmIsBRLMM) {
            return new CHPSnpBrlmmData(fusionCHPMultiDataData);
        } else if (algorithmIsBirdseed) {
            return new CHPSnpBirdseedData(fusionCHPMultiDataData);
        } else if (algorithmIsAxiomGT) {
            return new CHPSnpAxiomGTData(fusionCHPMultiDataData);
        } else if (algorithmIsCN4) {
            return new CnchpData(fusionCHPMultiDataData, CnchpData.CN4_TYPE_MAP);
        } else if (algorithmIsCN5) {
            return new CnchpData(fusionCHPMultiDataData, CnchpData.CN5_TYPE_MAP);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LSID> getReferencedArrayDesignCandidateIds() {
        final String lsidObjectId = getChpData(getFile()).getChipType();
        return Collections.singletonList(new LSID(LSID_AUTHORITY, LSID_NAMESPACE_DESIGN, lsidObjectId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
        return true;
    }
}
