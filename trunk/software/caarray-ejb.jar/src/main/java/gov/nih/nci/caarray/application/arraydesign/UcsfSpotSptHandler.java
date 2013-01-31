//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.validation.ValidationResult;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * Basic implementation of the UCSF Spot SPT array design file handler - only the LSID is set.
 *
 * @author Steve Lustbader
 */
public class UcsfSpotSptHandler extends AbstractArrayDesignHandler {
    private static final String LSID_AUTHORITY = AbstractCaArrayEntity.CAARRAY_LSID_AUTHORITY;
    private static final String LSID_NAMESPACE = AbstractCaArrayEntity.CAARRAY_LSID_NAMESPACE;
    private static final Logger LOG = Logger.getLogger(UcsfSpotSptHandler.class);

    UcsfSpotSptHandler(VocabularyService vocabularyService, CaArrayDaoFactory daoFactory,
            CaArrayFile designFile) {
        super(vocabularyService, daoFactory, designFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void createDesignDetails(ArrayDesign arrayDesign) {
        // no-op for unknown types
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void load(ArrayDesign arrayDesign) {
        arrayDesign.setName(FilenameUtils.getBaseName(getDesignFile().getName()));
        arrayDesign.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE + ":" + arrayDesign.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void validate(ValidationResult result) {
        // no-op for unknown types
    }

    FileStatus getValidatedStatus() {
        return FileStatus.IMPORTED_NOT_PARSED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Logger getLog() {
        return LOG;
    }

}
