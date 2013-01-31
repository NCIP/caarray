//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import static org.junit.Assert.assertNotNull;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * @author Winston Cheng
 *
 */
public class IlluminaExpressionDesignFixerTest extends AbstractCaarrayTest {
    private IlluminaExpressionDesignFixer migrator;

    private final LocalDaoFactoryStub localDaoFactoryStub = new LocalDaoFactoryStub();
    private final LocalArrayDesignService localArrayDesignService = new LocalArrayDesignService();
    private final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();

    private final ArrayDesign ILLUMINA_DESIGN = new ArrayDesign();

    @Before
    public void setUp() {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        localArrayDesignService.setDaoFactory(localDaoFactoryStub);
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, localArrayDesignService);
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new GenericDataServiceStub());
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fileAccessServiceStub));
        TemporaryFileCacheLocator.resetTemporaryFileCache();

        migrator = new IlluminaExpressionDesignFixer();
        migrator.setDaoFactory(localDaoFactoryStub);

        importIlluminaDesign();
    }

    @Test
    public void testMigrate() throws Exception {
        Set<LogicalProbe> probes = ILLUMINA_DESIGN.getDesignDetails().getLogicalProbes();
        for (LogicalProbe probe : probes) {
            probe.setAnnotation(null);
        }
        migrator.migrate();
        for (LogicalProbe probe : probes) {
            assertNotNull(probe.getAnnotation());
        }
    }

    @SuppressWarnings("deprecation")
    private void importIlluminaDesign() {
        CaArrayFile designFile = this.fileAccessServiceStub.add(IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        designFile.setFileType(FileType.ILLUMINA_DESIGN_CSV);

        ILLUMINA_DESIGN.addDesignFile(designFile);
        SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
        AssayType assayType = new AssayType("Gene Expression");
        assayType.setId(3L);
        assayTypes.add(assayType);
        ILLUMINA_DESIGN.setAssayTypes(assayTypes);
        ILLUMINA_DESIGN.setId(1L);
        ILLUMINA_DESIGN.setName("illumina design");

        localArrayDesignService.importDesign(ILLUMINA_DESIGN);
        localArrayDesignService.importDesignDetails(ILLUMINA_DESIGN);
    }

    private class LocalArrayDesignService extends ArrayDesignServiceBean {
        @Override
        public List<ArrayDesign> getArrayDesigns() {
            List<ArrayDesign> designs =  new ArrayList<ArrayDesign>();
            designs.add(ILLUMINA_DESIGN);
            return designs;
        }
        @Override
        public ArrayDesign getArrayDesign(Long id) {
            if (id != null && id.equals(1L)) {
                return ILLUMINA_DESIGN;
            }
            return null;
        }
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {
        @Override
        public ArrayDao getArrayDao() {
            return new ArrayDaoStub() {
                @Override
                public void save(PersistentObject object) {
                    // manually create reverse association automatically created by database fk relationship
                    if (object instanceof LogicalProbe) {
                        LogicalProbe probe = (LogicalProbe) object;
                        probe.getArrayDesignDetails().getLogicalProbes().add(probe);
                    }
                }

            };
        }

    }
}
