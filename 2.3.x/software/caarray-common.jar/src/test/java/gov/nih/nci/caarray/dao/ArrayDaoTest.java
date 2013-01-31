//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Array DAO.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD")
public class ArrayDaoTest extends AbstractDaoTest {
    private static final Logger LOG = Logger.getLogger(ArrayDaoTest.class);

    private static Organization DUMMY_ORGANIZATION = new Organization();
    private static Organization DUMMY_ORGANIZATION2 = new Organization();
    private static Organization DUMMY_ORGANIZATION3 = new Organization();
    private static ArrayDesign DUMMY_ARRAYDESIGN_1 = new ArrayDesign();
    private static ArrayDesign DUMMY_ARRAYDESIGN_2 = new ArrayDesign();
    private static ArrayDesign DUMMY_ARRAYDESIGN_3 = new ArrayDesign();
    private static AssayType DUMMY_ASSAY_TYPE1;
    private static AssayType DUMMY_ASSAY_TYPE2;
    private static int uniqueInt = 0;

    private static final ArrayDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getArrayDao();

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setUp() {
        // Initialize all the dummy objects needed for the tests.
        initializeArrayDesigns();
        Transaction tx = null;
        // Save dummy objects to database.
        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(DUMMY_ARRAYDESIGN_1);
            DAO_OBJECT.save(DUMMY_ARRAYDESIGN_2);
            DAO_OBJECT.save(DUMMY_ARRAYDESIGN_3);
            DAO_OBJECT.save(DUMMY_ORGANIZATION3);
            DAO_OBJECT.save(DUMMY_ASSAY_TYPE1);
            DAO_OBJECT.save(DUMMY_ASSAY_TYPE2);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            LOG.error("Error setting up test data.", e);
            throw e;
        }
    }

    /**
     * Initialize the dummy <code>ArrayDesign</code> objects.
     */
    private static void initializeArrayDesigns() {
        TermSource ts = new TermSource();
        ts.setName("TS 1");
        Category cat = new Category();
        cat.setName("catName");
        cat.setSource(ts);

        Term term = new Term();
        term.setValue("testval");
        term.setCategory(cat);
        term.setSource(ts);

        Organism organism = new Organism();
        organism.setScientificName("Homo sapiens");
        organism.setTermSource(ts);

        DUMMY_ORGANIZATION = new Organization();
        DUMMY_ORGANIZATION.setName("DummyOrganization");
        DUMMY_ORGANIZATION2 = new Organization();
        DUMMY_ORGANIZATION2.setName("DummyOrganization2");
        DUMMY_ORGANIZATION3 = new Organization();
        DUMMY_ORGANIZATION3.setName("DummyOrganization3");
        DUMMY_ARRAYDESIGN_1 = new ArrayDesign();
        DUMMY_ARRAYDESIGN_1.setName("DummyTestArrayDesign1");
        DUMMY_ARRAYDESIGN_1.setVersion("2.0");
        DUMMY_ARRAYDESIGN_1.setProvider(DUMMY_ORGANIZATION);
        DUMMY_ARRAYDESIGN_1.setLsidForEntity("authority:namespace:objectId");
        DUMMY_ASSAY_TYPE1 = new AssayType("Gene Expression");
        DUMMY_ASSAY_TYPE2 = new AssayType("SNP");
        SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE1);
        DUMMY_ARRAYDESIGN_1.setAssayTypes(assayTypes);
        CaArrayFile file = new CaArrayFile();
        file.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        DUMMY_ARRAYDESIGN_1.addDesignFile(file);
        DUMMY_ARRAYDESIGN_1.setTechnologyType(term);
        DUMMY_ARRAYDESIGN_1.setOrganism(organism);
        DUMMY_ARRAYDESIGN_2 = new ArrayDesign();
        DUMMY_ARRAYDESIGN_2.setName("DummyTestArrayDesign2");
        DUMMY_ARRAYDESIGN_2.setVersion("2.0");
        DUMMY_ARRAYDESIGN_2.setProvider(DUMMY_ORGANIZATION2);
        CaArrayFile file2 = new CaArrayFile();
        file2.setFileStatus(FileStatus.IMPORTING);
        DUMMY_ARRAYDESIGN_2.addDesignFile(file2);
        assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE1);
        assayTypes.add(DUMMY_ASSAY_TYPE2);
        DUMMY_ARRAYDESIGN_2.setAssayTypes(assayTypes);
        DUMMY_ARRAYDESIGN_2.setTechnologyType(term);
        DUMMY_ARRAYDESIGN_2.setOrganism(organism);
        DUMMY_ARRAYDESIGN_3 = new ArrayDesign();
        DUMMY_ARRAYDESIGN_3.setName("DummyTestArrayDesign3");
        DUMMY_ARRAYDESIGN_3.setVersion("2.0");
        DUMMY_ARRAYDESIGN_3.setProvider(DUMMY_ORGANIZATION2);
        CaArrayFile file3 = new CaArrayFile();
        file3.setFileStatus(FileStatus.IMPORTED);
        CaArrayFile file4 = new CaArrayFile();
        file4.setFileStatus(FileStatus.IMPORTED);
        DUMMY_ARRAYDESIGN_3.addDesignFile(file3);
        DUMMY_ARRAYDESIGN_3.addDesignFile(file4);
        assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE1);
        DUMMY_ARRAYDESIGN_3.setAssayTypes(assayTypes);
        DUMMY_ARRAYDESIGN_3.setTechnologyType(term);
        DUMMY_ARRAYDESIGN_3.setOrganism(organism);
    }

    @Test
    public void testGetArrayDesign() throws Exception {
        Transaction tx = null;
        HibernateUtil.enableFilters(false);
        try {
            tx = HibernateUtil.beginTransaction();
            ArrayDesign retrievedArrayDesign = DAO_OBJECT.getArrayDesign(DUMMY_ARRAYDESIGN_1.getId());
            assertEquals(DUMMY_ARRAYDESIGN_1, retrievedArrayDesign);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }

    @Test
    public void testChangeArrayDesignFile() throws Exception {
        Transaction tx = null;
        HibernateUtil.enableFilters(false);
        try {
            tx = HibernateUtil.beginTransaction();
            ArrayDesign retrievedArrayDesign = DAO_OBJECT.getArrayDesign(DUMMY_ARRAYDESIGN_1.getId());
            CaArrayFile file2 = new CaArrayFile();
            file2.setName("newfile");
            file2.setFileStatus(FileStatus.IMPORTING);
            retrievedArrayDesign.getDesignFiles().clear();
            retrievedArrayDesign.addDesignFile(file2);
            DAO_OBJECT.save(retrievedArrayDesign);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            retrievedArrayDesign = DAO_OBJECT.getArrayDesign(DUMMY_ARRAYDESIGN_1.getId());
            assertEquals("newfile", retrievedArrayDesign.getFirstDesignFile().getName());
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }

    @Test
    public void testGetArrayDesignByLsid() throws Exception {
        Transaction tx = null;
        HibernateUtil.enableFilters(false);
        try {
            tx = HibernateUtil.beginTransaction();
            ArrayDesign retrievedArrayDesign = DAO_OBJECT.getArrayDesign(DUMMY_ARRAYDESIGN_1.getLsidAuthority(),
                    DUMMY_ARRAYDESIGN_1.getLsidNamespace(), DUMMY_ARRAYDESIGN_1.getLsidObjectId());
            assertEquals(DUMMY_ARRAYDESIGN_1, retrievedArrayDesign);
            assertEquals(DUMMY_ARRAYDESIGN_1.getDesignFileSet().getFiles().size(),
                    retrievedArrayDesign.getDesignFileSet().getFiles().size());
            assertEquals(DUMMY_ARRAYDESIGN_1.getFirstDesignFile(), retrievedArrayDesign.getFirstDesignFile());
            tx.commit();
            tx = HibernateUtil.beginTransaction();
            retrievedArrayDesign = DAO_OBJECT.getArrayDesign(DUMMY_ARRAYDESIGN_1.getLsidAuthority(),
                    DUMMY_ARRAYDESIGN_1.getLsidNamespace(), "incorrectObjectId");
            assertNull(retrievedArrayDesign);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }

    /**
     * Tests retrieving array design providers and array designs by provider
     */
    @Test
    public void testArrayDesignProviders() throws Exception {
        Transaction tx = null;
        HibernateUtil.enableFilters(false);
        try {
            tx = HibernateUtil.beginTransaction();
            List<Organization> providers = DAO_OBJECT.getArrayDesignProviders();
            List<ArrayDesign> org1Designs = DAO_OBJECT.getArrayDesigns(DUMMY_ORGANIZATION, null, false);
            List<ArrayDesign> org2Designs = DAO_OBJECT.getArrayDesigns(DUMMY_ORGANIZATION2, null, false);
            List<ArrayDesign> org3Designs = DAO_OBJECT.getArrayDesigns(DUMMY_ORGANIZATION3, null, false);
            List<ArrayDesign> org2ImportedDesigns = DAO_OBJECT.getArrayDesigns(DUMMY_ORGANIZATION2, null, true);
            assertNotNull(providers);
            assertEquals(2, providers.size());
            assertNotNull(org1Designs);
            assertEquals(1, org1Designs.size());
            assertNotNull(org2Designs);
            assertEquals(2, org2Designs.size());
            assertNotNull(org3Designs);
            assertEquals(0, org3Designs.size());
            assertNotNull(org2ImportedDesigns);
            assertEquals(1, org2ImportedDesigns.size());
            assertEquals(DUMMY_ARRAYDESIGN_3, org2ImportedDesigns.get(0));
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }

    /**
     * Tests retrieving array design providers and array designs by provider
     */
    @Test
    public void testArrayDesignsByProviderAndAssayType() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
            assayTypes.add(DUMMY_ASSAY_TYPE1);
            List<ArrayDesign> org1Designs = DAO_OBJECT.getArrayDesigns(DUMMY_ORGANIZATION, assayTypes, false);
            @SuppressWarnings("unused")
            List<ArrayDesign> org2Designs = DAO_OBJECT.getArrayDesigns(DUMMY_ORGANIZATION, assayTypes, true);
            List<ArrayDesign> assayType1Designs = DAO_OBJECT.getArrayDesigns(null, assayTypes, false);
            List<ArrayDesign> org2assayType1Designs = DAO_OBJECT.getArrayDesigns(DUMMY_ORGANIZATION2, assayTypes, false);
            assayTypes.clear();
            assayTypes.add(DUMMY_ASSAY_TYPE2);
            List<ArrayDesign> assayType2Designs = DAO_OBJECT.getArrayDesigns(null, assayTypes, false);
            List<ArrayDesign> org2assayType2Designs = DAO_OBJECT.getArrayDesigns(DUMMY_ORGANIZATION2, assayTypes, false);
            List<ArrayDesign> org1assayType2Designs = DAO_OBJECT.getArrayDesigns(DUMMY_ORGANIZATION, assayTypes, false);
            assertNotNull(org1Designs);
            assertNotNull(assayType1Designs);
            assertEquals(3, assayType1Designs.size());
            assertNotNull(assayType2Designs);
            assertEquals(1, assayType2Designs.size());
            assertNotNull(org2assayType1Designs);
            assertEquals(2, org2assayType1Designs.size());
            assertNotNull(org2assayType2Designs);
            assertEquals(1, org2assayType2Designs.size());
            assertNotNull(org1assayType2Designs);
            assertEquals(0, org1assayType2Designs.size());
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of arraydesign: " + e.getMessage());
        }
    }

    @Test
    public void testGetRawArrayData() {
        // normally arraydata would be associated with samples, but in this it is not
        // so disable the security filters
        HibernateUtil.enableFilters(false);
        Transaction tx = null;
        CaArrayFile file = new CaArrayFile();
        RawArrayData rawData = new RawArrayData();
        rawData.setDataFile(file);
        rawData.setName("test" + System.currentTimeMillis());
        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(rawData);
            RawArrayData retrievedArrayData = DAO_OBJECT.getRawArrayData(file);
            assertEquals(rawData, retrievedArrayData);
            tx.commit();
            tx = HibernateUtil.beginTransaction();
            retrievedArrayData = DAO_OBJECT.getRawArrayData(file);
            assertEquals(rawData, retrievedArrayData);
            file = new CaArrayFile();
            DAO_OBJECT.save(file);
            retrievedArrayData = DAO_OBJECT.getRawArrayData(file);
            assertNull(retrievedArrayData);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of array data: " + e.getMessage());
        }
    }

    @Test
    public void testGetDerivedArrayData() {
        // normally arraydata would be associated with samples, but in this it is not
        // so disable the security filters
        HibernateUtil.enableFilters(false);
        Transaction tx = null;
        CaArrayFile file = new CaArrayFile();
        DerivedArrayData derivedArrayData = new DerivedArrayData();
        derivedArrayData.setDataFile(file);
        derivedArrayData.setName("test" + System.currentTimeMillis());
        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(derivedArrayData);
            tx.commit();
            tx = HibernateUtil.beginTransaction();
            DerivedArrayData retrievedArrayData = DAO_OBJECT.getDerivedArrayData(file);
            assertEquals(derivedArrayData, retrievedArrayData);
            file = new CaArrayFile();
            DAO_OBJECT.save(file);
            retrievedArrayData = DAO_OBJECT.getDerivedArrayData(file);
            assertNull(retrievedArrayData);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of array data: " + e.getMessage());
        }
    }

    @Test
    public void testGetArrayDataType() {
        Transaction tx = null;
        ArrayDataTypeDescriptor testDescriptor = createTestArrayDataTypeDescriptor();
        ArrayDataType arrayDataType = new ArrayDataType();
        arrayDataType.setName(testDescriptor.getName());
        arrayDataType.setVersion(testDescriptor.getVersion());
        try {
            tx = HibernateUtil.beginTransaction();
            QuantitationType quantitationType1 = createTestQuantitationType(createTestQuantitationTypeDescriptor());
            QuantitationType quantitationType2 = createTestQuantitationType(createTestQuantitationTypeDescriptor());
            DAO_OBJECT.save(quantitationType1);
            DAO_OBJECT.save(quantitationType2);
            assertNotSame(quantitationType1, quantitationType2);
            arrayDataType.getQuantitationTypes().add(quantitationType1);
            arrayDataType.getQuantitationTypes().add(quantitationType2);
            DAO_OBJECT.save(arrayDataType);
            tx.commit();
            tx = HibernateUtil.beginTransaction();
            HibernateUtil.getCurrentSession().clear();
            ArrayDataType retrievedArrayDataType = DAO_OBJECT.getArrayDataType(testDescriptor);
            assertEquals(arrayDataType, retrievedArrayDataType);
            testDescriptor = new ArrayDataTypeDescriptor() {

                public String getName() {
                    return "not in db";
                }

                public List<QuantitationTypeDescriptor> getQuantitationTypes() {
                    return null;
                }

                public String getVersion() {
                    return null;
                }

                public boolean isEquivalent(ArrayDataType type) {
                    return false;
                }

            };
            retrievedArrayDataType = DAO_OBJECT.getArrayDataType(testDescriptor);
            assertNull(retrievedArrayDataType);
            retrievedArrayDataType = DAO_OBJECT.getArrayDataType(null);
            assertNull(retrievedArrayDataType);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of arraydesign: " + e.getMessage());
        }
    }

    @Test
    public void testIsArrayDesignLocked() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            assertFalse(DAO_OBJECT.isArrayDesignLocked(1L));
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during isArrayDesignLocked: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteArrayDesignDetails() throws Exception {
        Transaction tx = null;
        HibernateUtil.enableFilters(false);
        try {
            tx = HibernateUtil.beginTransaction();
            ArrayDesign retrievedArrayDesign = DAO_OBJECT.getArrayDesign(DUMMY_ARRAYDESIGN_2.getId());
            CaArrayFile file2 = new CaArrayFile();
            file2.setName("newfile");
            file2.setFileStatus(FileStatus.IMPORTED);
            retrievedArrayDesign.getDesignFiles().clear();
            retrievedArrayDesign.addDesignFile(file2);
            ArrayDesignDetails designDetails = new ArrayDesignDetails();
            retrievedArrayDesign.setDesignDetails(designDetails);
            DAO_OBJECT.save(retrievedArrayDesign);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            retrievedArrayDesign = DAO_OBJECT.getArrayDesign(DUMMY_ARRAYDESIGN_2.getId());
            assertNotNull(retrievedArrayDesign.getDesignDetails());
            DAO_OBJECT.deleteArrayDesignDetails(retrievedArrayDesign);
            tx.commit();
            assertNull(retrievedArrayDesign.getDesignDetails());
        } catch (Exception e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }

    private ArrayDataTypeDescriptor createTestArrayDataTypeDescriptor() {
        return new ArrayDataTypeDescriptor() {
            private final String name = "name" + System.currentTimeMillis() + ":" + uniqueInt++;

            public String getName() {
                return name;
            }

            public String getVersion() {
                return "version";
            }

            public List<QuantitationTypeDescriptor> getQuantitationTypes() {
                return null;
            }

            public boolean isEquivalent(ArrayDataType arrayDataType) {
                return false;
            }
        };
    }

    @Test
    public void testGetQuantitationType() {
        Transaction tx = null;
        QuantitationTypeDescriptor testDescriptor = createTestQuantitationTypeDescriptor();
        QuantitationType quantitationType = createTestQuantitationType(testDescriptor);
        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(quantitationType);
            tx.commit();
            tx = HibernateUtil.beginTransaction();
            QuantitationType retrievedQuantitationType = DAO_OBJECT.getQuantitationType(testDescriptor);
            assertEquals(quantitationType, retrievedQuantitationType);
            testDescriptor = new QuantitationTypeDescriptor() {

                public DataType getDataType() {
                    return DataType.BOOLEAN;
                }

                public String getName() {
                    return "not in db";
                }

            };
            retrievedQuantitationType = DAO_OBJECT.getQuantitationType(testDescriptor);
            assertNull(retrievedQuantitationType);
            retrievedQuantitationType = DAO_OBJECT.getQuantitationType(null);
            assertNull(retrievedQuantitationType);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of arraydesign: " + e.getMessage());
        }
    }

    private QuantitationTypeDescriptor createTestQuantitationTypeDescriptor() {
        return new QuantitationTypeDescriptor() {
            private final String name = "name" + System.currentTimeMillis() + ":" + uniqueInt++;

            public String getName() {
                return name;
            }

            public DataType getDataType() {
                return DataType.FLOAT;
            }
        };
    }

    private QuantitationType createTestQuantitationType(QuantitationTypeDescriptor descriptor) {
        QuantitationType quantitationType = new QuantitationType();
        quantitationType.setName(descriptor.getName());
        quantitationType.setTypeClass(descriptor.getDataType().getTypeClass());
        return quantitationType;
    }
}
