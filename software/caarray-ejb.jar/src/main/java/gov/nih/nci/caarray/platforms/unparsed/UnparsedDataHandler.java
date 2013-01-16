//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.platforms.unparsed;

import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.AbstractDataFileHandler;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Handler for unparsed data formats.
 */
public class UnparsedDataHandler extends AbstractDataFileHandler {
    /**
     * FileType for Illumina IDAT files.
     */
    public static final FileType FILE_TYPE_ILLUMINA_IDAT = new FileType("ILLUMINA_IDAT", FileCategory.RAW_DATA, false,
            "IDAT");
    /**
     * FileType for Affymetrix DAT files.
     */
    public static final FileType FILE_TYPE_AFFYMETRIX_DAT = new FileType("AFFYMETRIX_DAT", FileCategory.RAW_DATA,
            false, "DAT");
    /**
     * FileType for Affymetrix EXP files.
     */
    public static final FileType FILE_TYPE_AFFYMETRIX_EXP = new FileType("AFFYMETRIX_EXP", FileCategory.DERIVED_DATA,
            false, "EXP");
    /**
     * FileType for Affymetrix TXT files.
     */
    public static final FileType FILE_TYPE_AFFYMETRIX_TXT = new FileType("AFFYMETRIX_TXT", FileCategory.DERIVED_DATA,
            false);
    /**
     * FileType for Affymetrix RPT files.
     */
    public static final FileType FILE_TYPE_AFFYMETRIX_RPT = new FileType("AFFYMETRIX_RPT", FileCategory.DERIVED_DATA,
            false, "RPT");
    /**
     * FileType for Imagene TIF files.
     */
    public static final FileType FILE_TYPE_IMAGENE_TIF = new FileType("IMAGENE_TIF", FileCategory.RAW_DATA, false);
    /**
     * FileType for GEO SOFT files.
     */
    public static final FileType FILE_TYPE_GEO_SOFT = new FileType("GEO_SOFT", FileCategory.RAW_DATA, false);
    /**
     * FileType for GEO GSM files.
     */
    public static final FileType FILE_TYPE_GEO_GSM = new FileType("GEO_GSM", FileCategory.RAW_DATA, false);
    /**
     * FileType for SCANARRAY CSV files.
     */
    public static final FileType FILE_TYPE_SCANARRAY_CSV = new FileType("SCANARRAY_CSV", FileCategory.RAW_DATA, false);
    /**
     * FileType for Illumina RAW TXT files.
     */
    public static final FileType FILE_TYPE_ILLUMINA_RAW_TXT = new FileType("ILLUMINA_RAW_TXT", FileCategory.RAW_DATA,
            false);
    /**
     * FileType for Illumina DERIVED TXT files.
     */
    public static final FileType FILE_TYPE_ILLUMINA_DERIVED_TXT = new FileType("ILLUMINA_DERIVED_TXT",
            FileCategory.DERIVED_DATA, false);
    /**
     * FileType for Imagene TXT files.
     */
    public static final FileType FILE_TYPE_IMAGENE_TXT = new FileType("IMAGENE_TXT", FileCategory.DERIVED_DATA, false);
    /**
     * FileType for Agilent DERIVED TXT files.
     */
    public static final FileType FILE_TYPE_AGILENT_DERIVED_TXT = new FileType("AGILENT_DERIVED_TXT",
            FileCategory.DERIVED_DATA, false);
    /**
     * FileType for Nimblegen GFF files.
     */
    public static final FileType FILE_TYPE_NIMBLEGEN_GFF = new FileType("NIMBLEGEN_GFF", FileCategory.DERIVED_DATA,
            false, "GFF");
    /**
     * FileType for Nimblegen Derived TXT files.
     */
    public static final FileType FILE_TYPE_NIMBLEGEN_DERIVED_TXT = new FileType("NIMBLEGEN_DERIVED_TXT",
            FileCategory.DERIVED_DATA, false);
    /**
     * FileType for Nimblegen Raw TXT files.
     */
    public static final FileType FILE_TYPE_NIMBLEGEN_RAW_TXT = new FileType("NIMBLEGEN_RAW_TXT", FileCategory.RAW_DATA,
            false);
    /**
     * FileType for Agilent TSV files.
     */
    public static final FileType FILE_TYPE_AGILENT_TSV = new FileType("AGILENT_TSV", FileCategory.RAW_DATA, false,
            "TSV");
    /**
     * FileType for MAGE-TAB base DATA MATRIX files.
     */
    public static final FileType FILE_TYPE_MAGE_TAB_DATA_MATRIX = new FileType("MAGE_TAB_DATA_MATRIX",
            FileCategory.RAW_DATA, false, true, "DATA");

    private static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(FILE_TYPE_ILLUMINA_IDAT,
            FILE_TYPE_AFFYMETRIX_DAT, FILE_TYPE_AFFYMETRIX_EXP, FILE_TYPE_AFFYMETRIX_TXT, FILE_TYPE_AFFYMETRIX_RPT,
            FILE_TYPE_IMAGENE_TIF, FILE_TYPE_GEO_SOFT, FILE_TYPE_GEO_GSM, FILE_TYPE_SCANARRAY_CSV,
            FILE_TYPE_ILLUMINA_RAW_TXT, FILE_TYPE_ILLUMINA_DERIVED_TXT, FILE_TYPE_IMAGENE_TXT,
            FILE_TYPE_AGILENT_DERIVED_TXT, FILE_TYPE_NIMBLEGEN_GFF, FILE_TYPE_NIMBLEGEN_DERIVED_TXT,
            FILE_TYPE_NIMBLEGEN_RAW_TXT, FILE_TYPE_AGILENT_TSV, FILE_TYPE_MAGE_TAB_DATA_MATRIX);

    /**
     * Default constructor.
     * 
     * @param dataStorageFacade data storage facade to use for retrieving file data
     */
    @Inject
    UnparsedDataHandler(DataStorageFacade dataStorageFacade) {
        super(dataStorageFacade);
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
        return UnsupportedDataFormatDescriptor.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        return new QuantitationTypeDescriptor[] {};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design) {
        // no-op, data parsing not supported for the current type
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(MageTabDocumentSet mTabSet, FileValidationResult result, ArrayDesign design) {
        // no-op, data parsing not supported for the current type
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresMageTab() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LSID> getReferencedArrayDesignCandidateIds() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
        return false;
    }
}
