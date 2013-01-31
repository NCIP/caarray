//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixArrayDataTypes;
import gov.nih.nci.caarray.application.arraydata.genepix.GenepixArrayDataTypes;
import gov.nih.nci.caarray.application.arraydata.illumina.IlluminaArrayDataTypes;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * Responsible for ensuring all known data types are registered in the system at initialization time.
 */
final class TypeRegistrationManager {

    private final ArrayDao arrayDao;

    TypeRegistrationManager(ArrayDao arrayDao) {
        this.arrayDao = arrayDao;
    }

    public void registerNewTypes() {
        initialize(AffymetrixArrayDataTypes.values());
        initialize(GenepixArrayDataTypes.values());
        initialize(IlluminaArrayDataTypes.values());
        initialize(UnsupportedDataFormatDescriptor.INSTANCE);
    }

    private void initialize(ArrayDataTypeDescriptor[] types) {
        for (ArrayDataTypeDescriptor type : types) {
            initialize(type);
        }
    }

    private void initialize(ArrayDataTypeDescriptor type) {
        ensureQuantitationTypesRegistered(type);
        ensureArrayDataTypeRegistered(type);
    }

    private void ensureArrayDataTypeRegistered(ArrayDataTypeDescriptor type) {
        if (!isRegistered(type)) {
            register(type);
        }
    }

    private boolean isRegistered(ArrayDataTypeDescriptor typeDescriptor) {
        return getType(typeDescriptor) != null;
    }

    private ArrayDataType getType(ArrayDataTypeDescriptor typeDescriptor) {
        return arrayDao.getArrayDataType(typeDescriptor);
    }

    private ArrayDataType createType(ArrayDataTypeDescriptor typeDescriptor) {
        ArrayDataType arrayDataType = new ArrayDataType();
        arrayDataType.setName(typeDescriptor.getName());
        arrayDataType.setVersion(typeDescriptor.getVersion());
        return arrayDataType;
    }

    private void register(ArrayDataTypeDescriptor typeDescriptor) {
        ArrayDataType arrayDataType = createType(typeDescriptor);
        for (QuantitationTypeDescriptor quantitationTypeDescriptor : typeDescriptor.getQuantitationTypes()) {
            arrayDataType.getQuantitationTypes().add(getQuantitationType(quantitationTypeDescriptor));
        }
        arrayDao.save(arrayDataType);
    }

    private void ensureQuantitationTypesRegistered(ArrayDataTypeDescriptor type) {
        for (QuantitationTypeDescriptor quantitationTypeDescriptor : type.getQuantitationTypes()) {
            ensureQuantitationTypeRegistered(quantitationTypeDescriptor);
        }
    }

    private void ensureQuantitationTypeRegistered(QuantitationTypeDescriptor quantitationTypeDescriptor) {
        if (!isRegistered(quantitationTypeDescriptor)) {
            register(quantitationTypeDescriptor);
        }
    }

    private boolean isRegistered(QuantitationTypeDescriptor quantitationTypeDescriptor) {
        return getQuantitationType(quantitationTypeDescriptor) != null;
    }

    private QuantitationType getQuantitationType(QuantitationTypeDescriptor quantitationTypeDescriptor) {
        return arrayDao.getQuantitationType(quantitationTypeDescriptor);
    }

    private void register(QuantitationTypeDescriptor quantitationTypeDescriptor) {
        QuantitationType quantitationType = createQuantitationType(quantitationTypeDescriptor);
        arrayDao.save(quantitationType);
    }

    private QuantitationType createQuantitationType(QuantitationTypeDescriptor quantitationTypeDescriptor) {
        QuantitationType quantitationType = new QuantitationType();
        quantitationType.setName(quantitationTypeDescriptor.getName());
        quantitationType.setTypeClass(quantitationTypeDescriptor.getDataType().getTypeClass());
        return quantitationType;
    }
}
