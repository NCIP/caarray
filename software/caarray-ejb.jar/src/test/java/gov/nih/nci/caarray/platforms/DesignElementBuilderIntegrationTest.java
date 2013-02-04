//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.platforms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.AbstractHibernateTest;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.DaoModule;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementReference;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.platforms.unparsed.UnparsedArrayDesignFileHandler;
import gov.nih.nci.caarray.staticinjection.CaArrayCommonStaticInjectionModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.util.Collections;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author dkokotov
 * 
 */
public class DesignElementBuilderIntegrationTest extends AbstractHibernateTest {
    /**
     * 
     */
    private static final int NUMBER_OF_PROBES = 700;
    private ArrayDao arrayDao;
    private SearchDao searchDao;

    private ArrayDesign design;

    public DesignElementBuilderIntegrationTest() {
        super(false);
    }

    /**
     * Subclasses can override this to configure a custom injector, e.g. by overriding some modules with stubbed out
     * functionality.
     * 
     * @return a Guice injector from which this will obtain dependencies.
     */
    @Override
    protected Injector createInjector() {
        return Guice.createInjector(new CaArrayCommonStaticInjectionModule(), new CaArrayHibernateHelperModule(),
                new DaoModule(), new AbstractModule() {
                    @Override
                    protected void configure() {
                        final FileTypeRegistry typeRegistry =
                                new FileTypeRegistryImpl(Collections.<DataFileHandler> emptySet(), Sets
                                        .<DesignFileHandler> newHashSet(new UnparsedArrayDesignFileHandler()));
                        bind(FileTypeRegistry.class).toInstance(typeRegistry);
                    }
                });
    }

    @Before
    public void setUp() throws Exception {
        this.arrayDao = this.injector.getInstance(ArrayDao.class);
        this.searchDao = this.injector.getInstance(SearchDao.class);
        this.design = createArrayDesign();
    }

    @Test
    public void testBuilder() {
        Transaction tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().save(this.design.getAssayTypes().iterator().next());
        this.hibernateHelper.getCurrentSession().save(this.design);
        final DataSet ds = new DataSet();
        this.hibernateHelper.getCurrentSession().save(ds);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        final DesignElementBuilder builder =
                new DesignElementBuilder(ds, this.design, this.arrayDao, this.searchDao, 300);
        final int midpoint = NUMBER_OF_PROBES / 2;
        for (int i = 0; i < midpoint; i++) {
            builder.addProbe("PROBE_" + i);
        }
        for (int i = midpoint; i < NUMBER_OF_PROBES; i++) {
            builder.addProbe("dummy probe name", "PROBE_" + i);
        }
        builder.finish();
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        final DesignElementList del =
                (DesignElementList) this.hibernateHelper.getCurrentSession().load(DesignElementList.class,
                        ds.getDesignElementList().getId());
        assertEquals(NUMBER_OF_PROBES, del.getDesignElementReferences().size());
        assertEquals(NUMBER_OF_PROBES, del.getDesignElements().size());
        for (int i = 0; i < NUMBER_OF_PROBES; i++) {
            final DesignElementReference der = del.getDesignElementReferences().get(i);
            final AbstractDesignElement de = del.getDesignElements().get(i);
            final String probeName = "PROBE_" + i;
            assertNotNull(der.getDesignElement());
            assertTrue(der.getDesignElement() instanceof PhysicalProbe);
            assertEquals(probeName, ((PhysicalProbe) der.getDesignElement()).getName());
            assertTrue(de instanceof PhysicalProbe);
            assertEquals(probeName, ((PhysicalProbe) de).getName());
        }
        tx.commit();
    }

    private static ArrayDesign createArrayDesign() {
        final TermSource ts = new TermSource();
        ts.setName("TS 1");
        final Category cat = new Category();
        cat.setName("catName");
        cat.setSource(ts);

        final Term term = new Term();
        term.setValue("testval");
        term.setCategory(cat);
        term.setSource(ts);

        final Organism organism = new Organism();
        organism.setScientificName("Homo sapiens");
        organism.setTermSource(ts);

        final Organization o = new Organization();
        o.setName("DummyOrganization");
        o.setProvider(true);

        final ArrayDesign design = new ArrayDesign();
        design.setName("foo");
        design.setVersion("99");
        design.setGeoAccession("GPL0001");
        design.setProvider(o);
        final AssayType at1 = new AssayType("Gene Expression");
        design.getAssayTypes().add(at1);
        design.setTechnologyType(term);
        design.setOrganism(organism);

        final ArrayDesignDetails detail = new ArrayDesignDetails();
        design.setDesignDetails(detail);
        for (int i = 0; i < NUMBER_OF_PROBES; i++) {
            final PhysicalProbe p = new PhysicalProbe(detail, null);
            p.setName("PROBE_" + i);
            detail.getProbes().add(p);
        }

        final CaArrayFile f = new CaArrayFile();
        f.setFileStatus(FileStatus.IMPORTED);
        f.setFileType(UnparsedArrayDesignFileHandler.AGILENT_CSV);
        f.setDataHandle(CaArrayUtils.makeUriQuietly("test:nowhere"));
        design.addDesignFile(f);
        return design;
    }
}
