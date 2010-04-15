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
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.util.HibernateUtil;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author gax
 */
public class ProbeHandlerTest extends AbstractServiceTest {

    private Transaction transaction;

    @Before
    public  void setUp() throws Exception {
        HibernateUtil.setFiltersEnabled(false);
    }

    @Test
    public void testMapping() {
        this.transaction = HibernateUtil.beginTransaction();

        ArrayDesignDetails d = new ArrayDesignDetails();
        HibernateUtil.getCurrentSession().save(d);
        HibernateUtil.getCurrentSession().flush();

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
            assertEquals("d", ann.getGene().getGenbankAccession());
            assertEquals("e", ann.getGene().getEntrezgeneID());
            assertEquals("f", ann.getGene().getUnigeneclusterID());

        }
        assertTrue(instance.getCount() == 2);
        this.transaction.rollback();
    }

    

}