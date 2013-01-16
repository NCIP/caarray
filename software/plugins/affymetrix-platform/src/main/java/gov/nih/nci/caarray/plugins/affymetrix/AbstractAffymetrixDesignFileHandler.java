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
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.platforms.AbstractDesignFileHandler;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import org.apache.log4j.Logger;

/**
 * @author Steve Lustbader
 */
abstract class AbstractAffymetrixDesignFileHandler extends AbstractDesignFileHandler {
    private static final Logger LOG = Logger.getLogger(AbstractAffymetrixDesignFileHandler.class);

    static final String LSID_AUTHORITY = "Affymetrix.com";
    static final String LSID_NAMESPACE = "PhysicalArrayDesign";
    static final int PROBE_SET_BATCH_SIZE = 1000;

    private final AbstractChpDesignElementListUtility designElementListUtility;

    private boolean[][] featureCreated;
    private ProbeGroup probeGroup;

    AbstractAffymetrixDesignFileHandler(SessionTransactionManager sessionTransactionManager,
            DataStorageFacade dataStorageFacade, ArrayDao arrayDao, SearchDao searchDao,
            AbstractChpDesignElementListUtility designElementListUtility) {
        super(sessionTransactionManager, dataStorageFacade, arrayDao, searchDao);
        this.designElementListUtility = designElementListUtility;
    }

    /**
     * Get the array design name for the design being imported.
     * 
     * @return array design name
     */
    protected abstract String getArrayDesignName();

    protected abstract int getNumRows();

    protected abstract int getNumCols();

    abstract void populateDesignDetails(ArrayDesignDetails designDetails) throws PlatformFileReadException;

    abstract void createProbeSetDesignElementList(ArrayDesign arrayDesign) throws PlatformFileReadException;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void load(ArrayDesign arrayDesign) {
        final String arrayDesignName = getArrayDesignName();
        arrayDesign.setName(arrayDesignName);
        arrayDesign.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE + ":" + arrayDesignName);
        arrayDesign.setNumberOfFeatures(getNumRows() * getNumCols());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createDesignDetails(ArrayDesign arrayDesign) {
        try {
            final ArrayDesignDetails designDetails = createDesignDetailsInstance(arrayDesign);
            initializeProbeGroup(designDetails);
            initializeFeaturesCreated();
            populateDesignDetails(designDetails);
            createProbeSetDesignElementList(arrayDesign);
        } catch (final Exception e) {
            LOG.error("Unexpected failure to read array design files that previously passed validation", e);
            arrayDesign.getDesignFileSet().updateStatus(FileStatus.IMPORT_FAILED);
            throw new IllegalStateException(e);
        }
    }

    private ArrayDesignDetails createDesignDetailsInstance(ArrayDesign arrayDesign) {
        final ArrayDesignDetails designDetails = new ArrayDesignDetails();
        arrayDesign.setDesignDetails(designDetails);
        getArrayDao().save(arrayDesign);
        getSessionTransactionManager().flushSession();
        return designDetails;
    }

    private void initializeProbeGroup(ArrayDesignDetails designDetails) {
        this.probeGroup = new ProbeGroup(designDetails);
        this.probeGroup.setName(LSID_AUTHORITY + ":" + this.probeGroup.getClass().getSimpleName() + ":All."
                + getArrayDesignName());
        getSearchDao().save(this.probeGroup);
    }

    void initializeFeaturesCreated() {
        this.featureCreated = new boolean[getNumCols()][getNumRows()];
    }

    Feature createFeature(int x, int y, ArrayDesignDetails details) {
        final Feature feature = new Feature(details);
        feature.setColumn((short) x);
        feature.setRow((short) y);
        this.featureCreated[x][y] = true;
        getArrayDao().save(feature);
        return feature;
    }

    /**
     * Fill in any missing features in this array design.
     * 
     * @param designDetails the ArrayDesignDetails for the array design.
     */
    protected void createMissingFeatures(ArrayDesignDetails designDetails) {
        for (int x = 0; x < this.featureCreated.length; x++) {
            for (int y = 0; y < this.featureCreated[x].length; y++) {
                if (!this.featureCreated[x][y]) {
                    createFeature(x, y, designDetails);
                }
            }
            flushAndClearSession();
            this.probeGroup = getSearchDao().retrieve(ProbeGroup.class, this.probeGroup.getId());
        }
    }

    protected void checkForDuplicateDesign(String arrayDesignName, FileValidationResult result) {
        final ArrayDesign existingDesign = getArrayDao()
                .getArrayDesign(LSID_AUTHORITY, LSID_NAMESPACE, arrayDesignName);
        if (existingDesign != null) {
            result.addMessage(Type.ERROR, "Affymetrix design " + arrayDesignName + " has already been imported");
        }
    }

    /**
     * @return the probeGroup
     */
    protected ProbeGroup getProbeGroup() {
        return this.probeGroup;
    }

    /**
     * @param probeGroup the probeGroup to set
     */
    protected void setProbeGroup(ProbeGroup probeGroup) {
        this.probeGroup = probeGroup;
    }

    protected ArrayDesign getRefreshedDesign(ArrayDesign arrayDesign) {
        return getArrayDao().getArrayDesign(arrayDesign.getId());
    }

    /**
     * @return the designElementListUtility
     */
    protected AbstractChpDesignElementListUtility getDesignElementListUtility() {
        return this.designElementListUtility;
    }
}
