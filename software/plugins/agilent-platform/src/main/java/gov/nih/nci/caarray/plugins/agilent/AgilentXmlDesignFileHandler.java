//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.platforms.AbstractDesignFileHandler;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * @author jscott
 * 
 */
public class AgilentXmlDesignFileHandler extends AbstractDesignFileHandler implements FeatureCountListener {
    static final String LSID_AUTHORITY = "Agilent.com";
    static final String LSID_NAMESPACE = "PhysicalArrayDesign";

    /**
     * File Type for AGILENT_XML array design.
     */
    public static final FileType XML_FILE_TYPE = new FileType("AGILENT_XML", FileCategory.ARRAY_DESIGN, true);
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(XML_FILE_TYPE);

    private final VocabularyDao vocabularyDao;
    private CaArrayFile designFile;
    private File fileOnDisk;
    private ArrayDesignDetails arrayDesignDetails;
    private Reader inputReader = null;
    private AgilentGELMTokenizer tokenizer = null;
    private int featureCount = 0;

    /**
     * @param sessionTransactionManager the SessionTransactionManager to use
     * @param dataStorageFacade data storage facade to use
     * @param arrayDao the ArrayDao to use
     * @param searchDao the SearchDao to use
     * @param vocabularyDao the VocabularyDao to use
     */
    @Inject
    protected AgilentXmlDesignFileHandler(SessionTransactionManager sessionTransactionManager,
            DataStorageFacade dataStorageFacade, ArrayDao arrayDao, SearchDao searchDao, VocabularyDao vocabularyDao) {
        super(sessionTransactionManager, dataStorageFacade, arrayDao, searchDao);
        this.vocabularyDao = vocabularyDao;
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
    public boolean openFiles(Set<CaArrayFile> designFiles) throws PlatformFileReadException {
        if (designFiles == null || designFiles.size() != 1) {
            return false;
        }

        this.designFile = designFiles.iterator().next();

        if (!SUPPORTED_TYPES.contains(this.designFile.getFileType())) {
            return false;
        }

        try {
            this.fileOnDisk = getDataStorageFacade().openFile(this.designFile.getDataHandle(), false);
            this.inputReader = new FileReader(this.fileOnDisk);
            this.tokenizer = new AgilentGELMTokenizer(this.inputReader);

            return true;
        } catch (final Exception e) {
            closeFiles();

            throw new PlatformFileReadException(this.fileOnDisk, "Could not open reader for file "
                    + this.designFile.getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ValidationResult result) throws PlatformFileReadException {
        try {
            final FileValidationResult fileResult = result.getOrCreateFileValidationResult(this.designFile.getName());
            this.designFile.setValidationResult(fileResult);

            final AgilentGELMParser parser = new AgilentGELMParser(this.tokenizer);

            if (!parser.validate()) {
                fileResult.addMessage(ValidationMessage.Type.ERROR, "Validation failed.");
            }
        } catch (final Exception e) {
            throw new PlatformFileReadException(this.fileOnDisk, "Unexpected error while validating "
                    + this.designFile.getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(ArrayDesign arrayDesign) throws PlatformFileReadException {
        try {
            arrayDesign.setName(FilenameUtils.getBaseName(this.designFile.getName()));
            arrayDesign.setLsidForEntity(String.format("%s:%s:%s", LSID_AUTHORITY, LSID_NAMESPACE,
                    arrayDesign.getName()));
            getArrayDao().save(arrayDesign);
        } catch (final Exception e) {
            throw new PlatformFileReadException(this.fileOnDisk, "Unexpected error while loading "
                    + this.designFile.getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createDesignDetails(ArrayDesign arrayDesign) throws PlatformFileReadException {
        try {
            arrayDesignDetails = new ArrayDesignDetails();
            getArrayDao().save(arrayDesignDetails);
            arrayDesign.setDesignDetails(arrayDesignDetails);
            getArrayDao().save(arrayDesign);
            parseArrayDesign(arrayDesign);
            arrayDesign.setNumberOfFeatures(Integer.valueOf(featureCount));
            getArrayDao().save(arrayDesign);
            flushAndClearSession();
        } catch (Exception e) {
            throw new PlatformFileReadException(this.fileOnDisk, "Unexpected error while creating design details: "
                    + designFile.getName() + ": " + e.getMessage(), e);
        }
    }

    private void parseArrayDesign(ArrayDesign arrayDesign) throws PlatformFileReadException {
        boolean parseResult;

        try {
            AgilentGELMParser parser = new AgilentGELMParser(tokenizer);
            parser.addFeatureCountListener(this);

            ArrayDesignBuilderImpl builder = new ArrayDesignBuilderImpl(arrayDesignDetails, vocabularyDao,
                    getArrayDao(), getSearchDao());
            parseResult = parser.parse(builder);

        } catch (final Exception e) {
            throw new PlatformFileReadException(this.fileOnDisk,
                    "Could not parse file " + this.designFile.getName(), e);
        }

        if (!parseResult) {
            throw new PlatformFileReadException(this.fileOnDisk, "Could not load file " + this.designFile.getName());
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void closeFiles() {
        try {
            closeTokenizer();
            closeReader();
            getDataStorageFacade().releaseFile(this.designFile.getDataHandle(), false);
            this.fileOnDisk = null;
            this.designFile = null;
        } catch (final PlatformFileReadException e) {
            // Closing anyway, ignore the exception and move on
            return;
        }
    }

    private void closeReader() throws PlatformFileReadException {
        try {
            if (null != this.inputReader) {
                this.inputReader.close();
                this.inputReader = null;
            }
        } catch (final IOException e) {
            throw new PlatformFileReadException(this.fileOnDisk,
                    "Could not close file " + this.designFile.getName(), e);
        }
    }

    private void closeTokenizer() throws PlatformFileReadException {
        if (null != this.tokenizer) {
            this.tokenizer.close();
            this.tokenizer = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
         return true;
    }

    /**
     * Increment feature count. 
     */
    public void incrementFeatureCount() {
        featureCount++;
    }
}
