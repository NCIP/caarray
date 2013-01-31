//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.validation.ValidationResult;

import org.apache.log4j.Logger;

import affymetrix.fusion.cdf.FusionCDFData;
import affymetrix.fusion.cdf.FusionCDFProbeGroupInformation;
import affymetrix.fusion.cdf.FusionCDFProbeInformation;
import affymetrix.fusion.cdf.FusionCDFProbeSetInformation;
import affymetrix.fusion.cdf.FusionCDFQCProbeInformation;
import affymetrix.fusion.cdf.FusionCDFQCProbeSetInformation;

/**
 * Contains logic to read Affymetrix CDF files.
 */
class AffymetrixCdfHandler extends AbstractAffymetrixArrayDesignHandler {

    private static final Logger LOG = Logger.getLogger(AffymetrixCdfHandler.class);

    private AffymetrixCdfReader cdfReader;

    AffymetrixCdfHandler(VocabularyService vocabularyService, CaArrayDaoFactory daoFactory, CaArrayFile designFile) {
        super(vocabularyService, daoFactory, designFile);
    }

    @Override
    protected void formatSpecificValidate(ValidationResult result) {
        // nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void closeFileReaders() {
        if (cdfReader != null) {
            cdfReader.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getArrayDesignName() {
        if (cdfReader == null) {
            throw new IllegalStateException("Must load CDF Reader before retrieving array design name.");
        }
        return cdfReader.getCdfData().getChipType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getNumRows() {
        if (cdfReader == null) {
            throw new IllegalStateException("Must load CDF Reader before retrieving row count.");
        }
        return cdfReader.getCdfData().getHeader().getRows();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getNumCols() {
        if (cdfReader == null) {
            throw new IllegalStateException("Must load CDF Reader before retrieving column count.");
        }
        return cdfReader.getCdfData().getHeader().getCols();
    }

    void createProbeSetDesignElementList(ArrayDesign arrayDesign) throws AffymetrixArrayDesignReadException {
        AffymetrixChpCdfDesignElementListUtility
                .createDesignElementList(getRefreshedDesign(arrayDesign), getArrayDao());
        flushAndClearSession();
    }

    void populateDesignDetails(ArrayDesignDetails designDetails) {
        handleProbeSets(designDetails);
        handleQCProbeSets(designDetails);
        createMissingFeatures(designDetails);
    }

    void handleProbeSets(ArrayDesignDetails designDetails) {
        FusionCDFData fusionCDFData = cdfReader.getCdfData();
        int numProbeSets = fusionCDFData.getHeader().getNumProbeSets();
        FusionCDFProbeSetInformation probeSetInformation = new FusionCDFProbeSetInformation();
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
        LogicalProbe logicalProbe = new LogicalProbe(designDetails);
        logicalProbe.setName(probeSetName);
        getArrayDao().save(logicalProbe);
        int numLists = probeSetInformation.getNumLists();
        for (int listIndex = 0; listIndex < numLists; listIndex++) {
            PhysicalProbe probe = new PhysicalProbe(designDetails, getProbeGroup());
            probe.setName(probeSetName + ".ProbePair" + listIndex);
            getArrayDao().save(probe);
        }
        int numGroups = probeSetInformation.getNumGroups();
        FusionCDFProbeGroupInformation probeGroupInformation = new FusionCDFProbeGroupInformation();
        for (int index = 0; index < numGroups; index++) {
            probeSetInformation.getGroup(index, probeGroupInformation);
            handleProbeGroup(probeGroupInformation, designDetails);
        }
    }

    private void handleProbeGroup(FusionCDFProbeGroupInformation probeGroupInformation,
            ArrayDesignDetails designDetails) {
        int numCells = probeGroupInformation.getNumCells();
        FusionCDFProbeInformation probeInformation = new FusionCDFProbeInformation();
        for (int index = 0; index < numCells; index++) {
            probeGroupInformation.getCell(index, probeInformation);
            handleProbe(probeInformation, designDetails);
        }
    }

    private void handleProbe(FusionCDFProbeInformation probeInformation, ArrayDesignDetails designDetails) {
        createFeature(probeInformation.getX(), probeInformation.getY(), designDetails);
    }

    void handleQCProbeSets(ArrayDesignDetails designDetails) {
        FusionCDFData fusionCDFData = cdfReader.getCdfData();
        int numQCProbeSets = fusionCDFData.getHeader().getNumQCProbeSets();
        FusionCDFQCProbeSetInformation qcProbeSetInformation = new FusionCDFQCProbeSetInformation();
        for (int index = 0; index < numQCProbeSets; index++) {
            fusionCDFData.getQCProbeSetInformation(index, qcProbeSetInformation);
            handleQCProbeSet(qcProbeSetInformation, designDetails);
            if (index % PROBE_SET_BATCH_SIZE == 0) {
                flushAndClearSession();
                setProbeGroup(getSearchDao().retrieve(ProbeGroup.class, getProbeGroup().getId()));
            }
        }
    }

    private void handleQCProbeSet(FusionCDFQCProbeSetInformation qcProbeSetInformation,
            ArrayDesignDetails designDetails) {
        int numCells = qcProbeSetInformation.getNumCells();
        FusionCDFQCProbeInformation qcProbeInformation = new FusionCDFQCProbeInformation();
        for (int index = 0; index < numCells; index++) {
            qcProbeSetInformation.getCell(index, qcProbeInformation);
            handleQCProbe(qcProbeInformation, designDetails);
        }
    }

    private void handleQCProbe(FusionCDFQCProbeInformation qcProbeInformation, ArrayDesignDetails designDetails) {
        createFeature(qcProbeInformation.getX(), qcProbeInformation.getY(), designDetails);
    }

    @Override
    protected void loadFileReaders() throws AffymetrixArrayDesignReadException {
        cdfReader = AffymetrixCdfReader.create(getFile());
    }

    @Override
    Logger getLog() {
        return LOG;
    }

}
