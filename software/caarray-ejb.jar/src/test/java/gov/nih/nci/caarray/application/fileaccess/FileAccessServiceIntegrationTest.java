//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.fileaccess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import java.io.File;
import java.sql.SQLException;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;

/**
 *
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class FileAccessServiceIntegrationTest extends AbstractServiceIntegrationTest {
    private FileAccessService fileAccessService;

    @Override
    protected Injector createInjector() {
        return InjectorFactory.getInjector();
    }

    @Before
    public void setUp() {
        this.fileAccessService = new FileAccessServiceBean();
        this.injector.injectMembers(this.fileAccessService);
    }

    @Test(expected = org.hibernate.ObjectNotFoundException.class)
    public void testRemove() {
        Transaction tx = this.hibernateHelper.beginTransaction();
        final File file = MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF;
        CaArrayFile caArrayFile = this.fileAccessService.add(file);
        caArrayFile.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);

        final Project p = new Project();
        p.getExperiment().setProject(p);
        p.getExperiment().setTitle("Foo");
        final Organism o = new Organism();
        o.setScientificName("baz");
        p.getExperiment().setOrganism(o);
        final TermSource ts = new TermSource();
        ts.setName("TS");
        ts.setUrl("http://ts");
        o.setTermSource(ts);
        p.getFiles().add(caArrayFile);
        caArrayFile.setProject(p);
        this.hibernateHelper.getCurrentSession().save(p);
        this.hibernateHelper.getCurrentSession().save(caArrayFile);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        caArrayFile = (CaArrayFile) this.hibernateHelper.getCurrentSession().load(CaArrayFile.class,
                caArrayFile.getId());
        final boolean removed = this.fileAccessService.remove(caArrayFile);
        assertTrue(removed);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        caArrayFile = (CaArrayFile) this.hibernateHelper.getCurrentSession().load(CaArrayFile.class,
                caArrayFile.getId());
        caArrayFile.toString();
        tx.commit();
    }

    @Test
    public void testRemoveWithArrayData() throws SQLException {
        Transaction tx = this.hibernateHelper.beginTransaction();
        // SDRF
        final File file = MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF;
        CaArrayFile caArrayFile = this.fileAccessService.add(file);
        caArrayFile.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        // derived data file
        final File file2 = MageTabDataFiles.SPECIFICATION_DERIVED_DATA_EXAMPLE_DATA_FILE;
        CaArrayFile caArrayFile2 = this.fileAccessService.add(file2);
        caArrayFile2.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        DerivedArrayData der = new DerivedArrayData();
        der.setDataFile(caArrayFile2);
        Hybridization hyb = new Hybridization();
        hyb.setName("foo");
        hyb.getDerivedDataCollection().add(der);
        der.getHybridizations().add(hyb);
        final Sample sample = new Sample();
        sample.setName("sample");
        final Extract extract = new Extract();
        extract.setName("extract");
        sample.getExtracts().add(extract);
        final LabeledExtract le = new LabeledExtract();
        le.setName("label");
        extract.getLabeledExtracts().add(le);
        le.getHybridizations().add(hyb);
        hyb.getLabeledExtracts().add(le);
        le.getExtracts().add(extract);
        extract.getSamples().add(sample);

        final Project p = new Project();
        p.getExperiment().setProject(p);
        p.getExperiment().setTitle("Foo");
        final Organism o = new Organism();
        o.setScientificName("baz");
        p.getExperiment().setOrganism(o);
        final TermSource ts = new TermSource();
        ts.setName("TS");
        ts.setUrl("http://ts");
        o.setTermSource(ts);
        p.getFiles().add(caArrayFile);
        p.getFiles().add(caArrayFile2);
        caArrayFile.setProject(p);
        caArrayFile2.setProject(p);

        this.hibernateHelper.getCurrentSession().save(p);

        this.hibernateHelper.getCurrentSession().save(sample);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(extract);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(le);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(sample);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(hyb);
        this.hibernateHelper.getCurrentSession().saveOrUpdate(der);

        this.hibernateHelper.getCurrentSession().save(caArrayFile);
        this.hibernateHelper.getCurrentSession().save(caArrayFile2);
        this.hibernateHelper.getCurrentSession().flush();
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        caArrayFile = (CaArrayFile) this.hibernateHelper.getCurrentSession().load(CaArrayFile.class,
                caArrayFile.getId());
        caArrayFile2 = (CaArrayFile) this.hibernateHelper.getCurrentSession().load(CaArrayFile.class,
                caArrayFile2.getId());
        der = (DerivedArrayData) this.hibernateHelper.getCurrentSession().load(DerivedArrayData.class, der.getId());
        assertEquals(der.getDataFile(), caArrayFile2);
        assertNotNull(caArrayFile.getProject());
        assertNotNull(caArrayFile2.getProject());
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        caArrayFile = (CaArrayFile) this.hibernateHelper.getCurrentSession().load(CaArrayFile.class,
                caArrayFile.getId());
        boolean removed = this.fileAccessService.remove(caArrayFile);
        assertTrue(removed);
        caArrayFile2 = (CaArrayFile) this.hibernateHelper.getCurrentSession().load(CaArrayFile.class,
                caArrayFile2.getId());
        removed = this.fileAccessService.remove(caArrayFile2);
        assertTrue(removed);

        tx.commit();
        tx = this.hibernateHelper.beginTransaction();

        try {
            caArrayFile = (CaArrayFile) this.hibernateHelper.getCurrentSession().load(CaArrayFile.class,
                    caArrayFile.getId());
            fail("file " + caArrayFile + " not deleted");
        } catch (final org.hibernate.ObjectNotFoundException e) {
        }

        try {
            caArrayFile2 = (CaArrayFile) this.hibernateHelper.getCurrentSession().load(CaArrayFile.class,
                    caArrayFile2.getId());
            fail("file " + caArrayFile2 + " not deleted");
        } catch (final org.hibernate.ObjectNotFoundException e) {
        }
        try {
            der = (DerivedArrayData) this.hibernateHelper.getCurrentSession().load(DerivedArrayData.class, der.getId());
            fail("raw array data not deleted " + der);
        } catch (final org.hibernate.ObjectNotFoundException e) {
        }
        hyb = (Hybridization) this.hibernateHelper.getCurrentSession().load(Hybridization.class, hyb.getId());
        assertTrue(hyb.getDerivedDataCollection().isEmpty());
        tx.commit();
    }
}
