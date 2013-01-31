//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author Steve Lustbader
 */
public abstract class AbstractAffymetrixArrayDesignHandler extends AbstractArrayDesignHandler {

    private static final Logger LOG = Logger.getLogger(AbstractAffymetrixArrayDesignHandler.class);

    static final String LSID_AUTHORITY = "Affymetrix.com";
    static final String LSID_NAMESPACE = "PhysicalArrayDesign";
    static final int PROBE_SET_BATCH_SIZE = 25;

    private boolean[][] featureCreated;
    private ProbeGroup probeGroup;

    AbstractAffymetrixArrayDesignHandler(VocabularyService vocabularyService, CaArrayDaoFactory daoFactory,
            Set<CaArrayFile> designFiles) {
        super(vocabularyService, daoFactory, designFiles);
    }

    AbstractAffymetrixArrayDesignHandler(VocabularyService vocabularyService, CaArrayDaoFactory daoFactory,
            CaArrayFile designFile) {
        super(vocabularyService, daoFactory, designFile);
    }

    /**
     * Load any needed file reader(s) for the array design file(s).
     * @throws AffymetrixArrayDesignReadException if an error occurs loading the file reader(s)
     */
    abstract void loadFileReaders() throws AffymetrixArrayDesignReadException;

    /**
     * Close any needed file reader(s) for the array design file(s).
     */
    abstract void closeFileReaders();

    /**
     * Get the array design name for the design being imported.
     * @return array design name
     */
    abstract String getArrayDesignName();

    abstract int getNumRows();

    abstract int getNumCols();

    /**
     * {@inheritDoc}
     */
    @Override
    void load(ArrayDesign arrayDesign) {
        try {
            loadFileReaders();
            String arrayDesignName = getArrayDesignName();
            arrayDesign.setName(arrayDesignName);
            arrayDesign.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE + ":" + arrayDesignName);
            arrayDesign.setNumberOfFeatures(getNumRows() * getNumCols());
        } catch (AffymetrixArrayDesignReadException e) {
            LOG.error("Unexpected failure to read CDF that previously passed validation", e);
            throw new IllegalStateException(e);
        } finally {
            closeFileReaders();
        }
    }

    @Override
    void validate(ValidationResult result) {
        try {
            loadFileReaders();
            for (CaArrayFile designFile : getDesignFiles()) {
                File file = getFile(designFile);
                result.addFile(file, new FileValidationResult(file));
            }

            checkForDuplicateDesign(getArrayDesignName(), result);
            formatSpecificValidate(result);
        } catch (AffymetrixArrayDesignReadException e) {
            LOG.error("We were unable to process an uploaded array design file.  The Affy parser recorded the "
                    + " following error: " + e.getMessage());
            result.addMessage(getFile(), ValidationMessage.Type.ERROR, "Unable to parse the file "
                    + getDesignFile().getName() + ". " + e.getMessage().replaceAll("null", "unknown"));
        } finally {
            closeFileReaders();
        }
    }

    /**
     * Perform any validation needed specific to the exact file format being imported.  Any needed file readers
     * will already be available when this method is called.
     * @param result validation result to populate
     */
    abstract void formatSpecificValidate(ValidationResult result);

    /**
     * Checks if an array design has already been imported.
     * @param arrayDesignName name of array design to check
     * @param result validation results to populate
     */
    void checkForDuplicateDesign(String arrayDesignName, ValidationResult result) {
        ArrayDesign existingDesign =
            getDaoFactory().getArrayDao().getArrayDesign(LSID_AUTHORITY, LSID_NAMESPACE,
                    arrayDesignName);
        if (existingDesign != null) {
            result.addMessage(getFile(), Type.ERROR, "Affymetrix design " + arrayDesignName
                    + " has already been imported");
        }
    }

    @Override
    void createDesignDetails(ArrayDesign arrayDesign) {
        try {
            ArrayDesignDetails designDetails = createDesignDetailsInstance(arrayDesign);
            loadFileReaders();
            initializeProbeGroup(designDetails);
            initializeFeaturesCreated();
            populateDesignDetails(designDetails);
            createProbeSetDesignElementList(arrayDesign);
        } catch (Exception e) {
            LOG.error("Unexpected failure to read array design files that previously passed validation", e);
            arrayDesign.getDesignFileSet().updateStatus(FileStatus.IMPORT_FAILED);
            throw new IllegalStateException(e);
        } finally {
            closeFileReaders();
        }
    }

    private ArrayDesignDetails createDesignDetailsInstance(ArrayDesign arrayDesign) {
        ArrayDesignDetails designDetails = new ArrayDesignDetails();
        arrayDesign.setDesignDetails(designDetails);
        getArrayDao().save(arrayDesign);
        getArrayDao().flushSession();
        return designDetails;
    }

    abstract void populateDesignDetails(ArrayDesignDetails designDetails) throws AffymetrixArrayDesignReadException;

    private void initializeProbeGroup(ArrayDesignDetails designDetails) {
        this.probeGroup = new ProbeGroup(designDetails);
        this.probeGroup.setName(LSID_AUTHORITY + ":" + this.probeGroup.getClass().getSimpleName() + ":All."
                + getArrayDesignName());
        getSearchDao().save(this.probeGroup);
    }


    void initializeFeaturesCreated() {
        featureCreated = new boolean[getNumCols()][getNumRows()];
    }

    Feature createFeature(int x, int y, ArrayDesignDetails details) {
        Feature feature = new Feature(details);
        feature.setColumn((short) x);
        feature.setRow((short) y);
        featureCreated[x][y] = true;
        getArrayDao().save(feature);
        return feature;
    }

    void createMissingFeatures(ArrayDesignDetails designDetails) {
        for (int x = 0; x < featureCreated.length; x++) {
            for (int y = 0; y < featureCreated[x].length; y++) {
                if (!featureCreated[x][y]) {
                    createFeature(x, y, designDetails);
                }
            }
            flushAndClearSession();
            this.probeGroup = getSearchDao().retrieve(ProbeGroup.class, probeGroup.getId());
        }
    }

    abstract void createProbeSetDesignElementList(ArrayDesign arrayDesign) throws AffymetrixArrayDesignReadException;

    /**
     * @return the probeGroup
     */
    public ProbeGroup getProbeGroup() {
        return probeGroup;
    }

    /**
     * @param probeGroup the probeGroup to set
     */
    public void setProbeGroup(ProbeGroup probeGroup) {
        this.probeGroup = probeGroup;
    }

    ArrayDesign getRefreshedDesign(ArrayDesign arrayDesign) {
        return getArrayDao().getArrayDesign(arrayDesign.getId());
    }

}
