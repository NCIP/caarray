//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import affymetrix.fusion.cdf.FusionCDFData;
import affymetrix.fusion.cdf.FusionCDFProbeGroupInformation;
import affymetrix.fusion.cdf.FusionCDFProbeInformation;
import affymetrix.fusion.cdf.FusionCDFProbeSetInformation;
import affymetrix.fusion.cdf.FusionCDFQCProbeInformation;
import affymetrix.fusion.cdf.FusionCDFQCProbeSetInformation;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Contains logic to read Affymetrix CDF files.
 */
public final class CdfHandler extends AbstractAffymetrixDesignFileHandler {
    /**
     * FileType instance for CDF file type.
     */
    public static final FileType CDF_FILE_TYPE = new FileType("AFFYMETRIX_CDF", FileCategory.ARRAY_DESIGN, true, "CDF");

    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(CDF_FILE_TYPE);

    private CdfReader cdfReader;
    private CaArrayFile cdfFile;
    private File cdfFileOnDisk;

    @Inject
    CdfHandler(SessionTransactionManager sessionTransactionManager, DataStorageFacade dataStorageFacade,
            ArrayDao arrayDao, SearchDao searchDao,
            @Named("cdf") AbstractChpDesignElementListUtility designElementListUtility) {
        super(sessionTransactionManager, dataStorageFacade, arrayDao, searchDao, designElementListUtility);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean openFiles(Set<CaArrayFile> designFiles) throws PlatformFileReadException {
        if (designFiles == null || designFiles.size() != 1
                || !SUPPORTED_TYPES.contains(designFiles.iterator().next().getFileType())) {
            return false;
        }

        this.cdfFile = designFiles.iterator().next();
        this.cdfFileOnDisk = getDataStorageFacade().openFile(this.cdfFile.getDataHandle(), false);
        this.cdfReader = new CdfReader(this.cdfFileOnDisk);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeFiles() {
        if (this.cdfReader != null) {
            this.cdfReader.close();
        }
        this.cdfReader = null;
        getDataStorageFacade().releaseFile(this.cdfFile.getDataHandle(), false);
        this.cdfFileOnDisk = null;
        this.cdfFile = null;
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
    public void validate(ValidationResult result) {
        final FileValidationResult fileResult = new FileValidationResult();
        result.addFile(this.cdfFile.getName(), fileResult);
        this.cdfFile.setValidationResult(fileResult);
        checkForDuplicateDesign(getArrayDesignName(), fileResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getArrayDesignName() {
        // the CDFData just reports the name of the file as the design name. but the name of the physical file
        // may not correspond to the original filename. instead get the name from the CaArrayFile, which will
        // be the original name
        return FilenameUtils.getBaseName(this.cdfFile.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getNumRows() {
        if (this.cdfReader == null) {
            throw new IllegalStateException("Must load CDF Reader before retrieving row count.");
        }
        return this.cdfReader.getCdfData().getHeader().getRows();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getNumCols() {
        if (this.cdfReader == null) {
            throw new IllegalStateException("Must load CDF Reader before retrieving column count.");
        }
        return this.cdfReader.getCdfData().getHeader().getCols();
    }

    @Override
    void createProbeSetDesignElementList(ArrayDesign arrayDesign) throws PlatformFileReadException {
        getDesignElementListUtility().createDesignElementList(getRefreshedDesign(arrayDesign));
        flushAndClearSession();
    }

    @Override
    void populateDesignDetails(ArrayDesignDetails designDetails) {
        handleProbeSets(designDetails);
        handleQCProbeSets(designDetails);
        createMissingFeatures(designDetails);
    }

    private void handleProbeSets(ArrayDesignDetails designDetails) {
        final FusionCDFData fusionCDFData = this.cdfReader.getCdfData();
        final int numProbeSets = fusionCDFData.getHeader().getNumProbeSets();
        final FusionCDFProbeSetInformation probeSetInformation = new FusionCDFProbeSetInformation();
        for (int index = 0; index < numProbeSets; index++) {
            fusionCDFData.getProbeSetInformation(index, probeSetInformation);
            handleProbeSet(probeSetInformation, fusionCDFData.getProbeSetName(index), designDetails);
            if (index % PROBE_SET_BATCH_SIZE == 0) {
                flushAndClearSession();
                setProbeGroup(getSearchDao().retrieve(ProbeGroup.class, getProbeGroup().getId()));
            }
        }
    }

    private void handleProbeSet(FusionCDFProbeSetInformation probeSetInformation, String probeSetName,
            ArrayDesignDetails designDetails) {
        final LogicalProbe logicalProbe = new LogicalProbe(designDetails);
        logicalProbe.setName(probeSetName);
        getArrayDao().save(logicalProbe);
        final int numLists = probeSetInformation.getNumLists();
        for (int listIndex = 0; listIndex < numLists; listIndex++) {
            final PhysicalProbe probe = new PhysicalProbe(designDetails, getProbeGroup());
            probe.setName(probeSetName + ".ProbePair" + listIndex);
            getArrayDao().save(probe);
        }
        final int numGroups = probeSetInformation.getNumGroups();
        final FusionCDFProbeGroupInformation probeGroupInformation = new FusionCDFProbeGroupInformation();
        for (int index = 0; index < numGroups; index++) {
            probeSetInformation.getGroup(index, probeGroupInformation);
            handleProbeGroup(probeGroupInformation, designDetails);
        }
    }

    private void
            handleProbeGroup(FusionCDFProbeGroupInformation probeGroupInformation, ArrayDesignDetails designDetails) {
        final int numCells = probeGroupInformation.getNumCells();
        final FusionCDFProbeInformation probeInformation = new FusionCDFProbeInformation();
        for (int index = 0; index < numCells; index++) {
            probeGroupInformation.getCell(index, probeInformation);
            handleProbe(probeInformation, designDetails);
        }
    }

    private void handleProbe(FusionCDFProbeInformation probeInformation, ArrayDesignDetails designDetails) {
        createFeature(probeInformation.getX(), probeInformation.getY(), designDetails);
    }

    private void handleQCProbeSets(ArrayDesignDetails designDetails) {
        final FusionCDFData fusionCDFData = this.cdfReader.getCdfData();
        final int numQCProbeSets = fusionCDFData.getHeader().getNumQCProbeSets();
        final FusionCDFQCProbeSetInformation qcProbeSetInformation = new FusionCDFQCProbeSetInformation();
        for (int index = 0; index < numQCProbeSets; index++) {
            fusionCDFData.getQCProbeSetInformation(index, qcProbeSetInformation);
            handleQCProbeSet(qcProbeSetInformation, designDetails);
            if (index % PROBE_SET_BATCH_SIZE == 0) {
                flushAndClearSession();
                setProbeGroup(getSearchDao().retrieve(ProbeGroup.class, getProbeGroup().getId()));
            }
        }
    }

    private void
            handleQCProbeSet(FusionCDFQCProbeSetInformation qcProbeSetInformation, ArrayDesignDetails designDetails) {
        final int numCells = qcProbeSetInformation.getNumCells();
        final FusionCDFQCProbeInformation qcProbeInformation = new FusionCDFQCProbeInformation();
        for (int index = 0; index < numCells; index++) {
            qcProbeSetInformation.getCell(index, qcProbeInformation);
            handleQCProbe(qcProbeInformation, designDetails);
        }
    }

    private void handleQCProbe(FusionCDFQCProbeInformation qcProbeInformation, ArrayDesignDetails designDetails) {
        createFeature(qcProbeInformation.getX(), qcProbeInformation.getY(), designDetails);
    }

}
