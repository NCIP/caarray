package gov.nih.nci.caarray.platforms.illumina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.ExpressionProbeAnnotation;
import gov.nih.nci.caarray.domain.array.Gene;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.staticinjection.CaArrayEjbStaticInjectionModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 *
 * @author gax
 */
public class ProbeHandlerTest extends AbstractServiceTest {
    private static Injector injector;
    private static CaArrayHibernateHelper hibernateHelper; 

    private Transaction transaction;
    
    /**
     * post-construct lifecycle method; intializes the Guice injector that will provide dependencies. 
     */
    @BeforeClass
    public static void init() {
        injector = createInjector();
        hibernateHelper = injector.getInstance(CaArrayHibernateHelper.class);
    }
    
    /**
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected static Injector createInjector() {
        return Guice.createInjector(new CaArrayEjbStaticInjectionModule(), new CaArrayHibernateHelperModule());
    }
    
    @Before
    public  void setUp() throws Exception {
        hibernateHelper.setFiltersEnabled(false);
    }

    @Test
    public void testMapping() {
        this.transaction = hibernateHelper.beginTransaction();

        ArrayDesignDetails d = new ArrayDesignDetails();
        hibernateHelper.getCurrentSession().save(d);
        hibernateHelper.getCurrentSession().flush();

        ProbeHandler instance = new ProbeHandler(d, new ArrayDaoStub(), new SearchDaoStub());
        instance.startSection("Probes", 1);
        String[] header = {"Probe_Id", "Symbol", "Definition", "Accession", "ENTREZ_GENE_ID", "UNIGENE_ID"};
        instance.parseFirstRow(header, 2);
        String[] row = {"mmm", "b", "c", "b", "e", "f"};
        instance.parseRow(row, 3);
        instance.endSection("Probes", 4);
        instance.startSection("Controls", 5);
        instance.parseFirstRow(header, 6);
        row[0] = "ccc";
        instance.parseRow(row, 7);
        instance.endSection("Controls", 8);
        assertFalse(d.getProbes().size() == 2);
        for (PhysicalProbe p : d.getProbes()) {
            if (p.getName().equals("mmm")) {
                assertEquals("Main", p.getProbeGroup().getName());
            } else if (p.getName().equals("ccc")) {
                assertEquals("Control", p.getProbeGroup().getName());
            } else {
                fail(p.getName());
            }
            ExpressionProbeAnnotation ann = (ExpressionProbeAnnotation) p.getAnnotation();
            assertEquals("b", ann.getGene().getSymbol());
            assertEquals("c", ann.getGene().getFullName());
            assertEquals("d", ann.getGene().getAccessionNumbers(Gene.GENBANK).get(0));
            assertEquals("e", ann.getGene().getAccessionNumbers(Gene.ENTREZ_GENE).get(0));
            assertEquals("f", ann.getGene().getAccessionNumbers(Gene.UNIGENE).get(0));

        }
        assertTrue(instance.getCount() == 2);
        this.transaction.rollback();
    }

    

}