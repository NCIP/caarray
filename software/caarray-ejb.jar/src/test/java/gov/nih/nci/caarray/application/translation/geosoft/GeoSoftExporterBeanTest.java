package gov.nih.nci.caarray.application.translation.geosoft;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.AbstractHibernateTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessUtils;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.UserDefinedFactorValue;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.UserDefinedCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @author gax
 */
public class GeoSoftExporterBeanTest extends AbstractHibernateTest {
    protected static FileType AFFYMETRIX_CHP = new FileType("AFFYMETRIX_CHP", FileCategory.DERIVED_DATA, true, "CHP");

    private static final String TEST_DATA = "Test data";

    private GeoSoftExporterBean bean;
    VocabularyServiceStub vocab;
    Transaction tx;
    private FileAccessServiceStub fasStub;

    private File rawFile;
    private File derivedFile;
    private File supplementalFile;

    public GeoSoftExporterBeanTest() {
        super(false);
    }

    @Before
    public void setUp() throws IOException {
        this.bean = new GeoSoftExporterBean();
        this.vocab = new VocabularyServiceStub();
        this.tx = this.hibernateHelper.beginTransaction();

        final DataFileHandler chpHandler = mock(DataFileHandler.class);
        when(chpHandler.getSupportedTypes()).thenReturn(Sets.newHashSet(AFFYMETRIX_CHP));
        final FileTypeRegistry typeRegistry = new FileTypeRegistryImpl(Sets.newHashSet(chpHandler),
                Sets.<DesignFileHandler> newHashSet());
        this.fasStub = new FileAccessServiceStub(typeRegistry);
        this.bean.setFileAccessHelper(new FileAccessUtils(this.fasStub.createStorageFacade()));

        createFiles();
    }

    private void createFiles() throws IOException {
        this.rawFile = writeTempFile("raw-data", ".data");
        this.derivedFile = writeTempFile("raw-data", ".data");
        this.supplementalFile = writeTempFile("raw-data", ".data");
    }

    private File writeTempFile(String name, String suffix) throws IOException {
        final File file = File.createTempFile(name, suffix);
        FileUtils.writeByteArrayToFile(file, TEST_DATA.getBytes());
        return file;
    }

    @After
    public void closeTx() {
        FileUtils.deleteQuietly(this.rawFile);
        FileUtils.deleteQuietly(this.derivedFile);
        FileUtils.deleteQuietly(this.supplementalFile);

        this.tx.rollback();
    }

    Project makeGoodProject() throws Exception {
        final Experiment e = makeGoodExperiment();
        return e.getProject();
    }

    Experiment makeGoodExperiment() throws Exception {
        final Project prj = new Project();
        final Experiment experiment = new Experiment();
        prj.setExperiment(experiment);
        final Method setter = Experiment.class.getDeclaredMethod("setProject", Project.class);
        setter.setAccessible(true);
        setter.invoke(experiment, prj);

        final TermSource src = this.vocab.getSource("MO", "1.3.1.1");

        // to ensure consistent order
        experiment.setExperimentDesignTypes(new LinkedHashSet<Term>());
        experiment.getExperimentDesignTypes().add(this.vocab.getTerm(src, "test-design-type1"));
        experiment.getExperimentDesignTypes().add(this.vocab.getTerm(src, "test-design-type2"));
        experiment.setPublicIdentifier("test-exp-id");
        experiment.setTitle("test-title");
        final Publication pub = new Publication();
        pub.setPubMedId("test-pub");
        experiment.getPublications().add(pub);
        final ExperimentContact ec = new ExperimentContact();
        final Person per = new Person();
        per.setFirstName("fff");
        per.setMiddleInitials("mmm");
        per.setLastName("lll");
        ec.setPerson(per);
        experiment.getExperimentContacts().add(ec);
        final ArrayDesign ad = new ArrayDesign();
        experiment.getArrayDesigns().add(ad);
        ad.setName("test-ad");
        ad.setGeoAccession("test-ga");
        final Organization o = new Organization();
        ad.setProvider(o);
        o.setName("Affymetrix");
        final Source source = new Source();
        source.setId(1L);
        source.getProviders().add(o);
        source.setName("test-source");
        experiment.getSources().add(source);
        Sample sample = new Sample();
        sample.setId(2L);
        sample.setName("test-sample1");
        Organism ozm = new Organism();
        ozm.setId(1L);
        ozm.setScientificName("test Organizm 1");
        sample.setOrganism(ozm);
        source.getSamples().add(sample);
        sample.getSources().add(source);
        final Extract extract = new Extract();
        extract.setId(3L);
        extract.setName("test-extract");
        sample.getExtracts().add(extract);
        extract.getSamples().add(sample);

        sample = new Sample();
        sample.setId(4L);
        sample.setName("test-sample2");
        ozm = new Organism();
        ozm.setId(2L);
        ozm.setScientificName("test Organizm 2");
        sample.setOrganism(ozm);
        source.getSamples().add(sample);
        sample.getSources().add(source);
        sample.getExtracts().add(extract);
        extract.getSamples().add(sample);

        final LabeledExtract lb = new LabeledExtract();
        lb.setId(5L);
        lb.setLabel(this.vocab.getTerm(src, "label"));
        final Term mt = this.vocab.getTerm(src, "test-mat");
        mt.setId(1L);
        mt.setValue("MT val");
        lb.setMaterialType(mt);
        extract.getLabeledExtracts().add(lb);
        lb.getExtracts().add(extract);
        final Hybridization h = new Hybridization();
        h.setId(6L);
        final Array a = new Array();
        a.setDesign(ad);
        h.setArray(a);
        h.setName("test-hyb");
        experiment.getHybridizations().add(h);
        ProtocolApplication pa = new ProtocolApplication();
        pa.setId(7L);
        Protocol p = this.vocab.getProtocol("some extract", src);
        Term type = this.vocab.getTerm(src, "nucleic_acid_extraction");
        p.setDescription("extract desc");
        p.setType(type);
        pa.setProtocol(p);
        sample.getProtocolApplications().add(pa);
        pa = new ProtocolApplication();
        pa.setId(8L);
        type = this.vocab.getTerm(src, "labeling");
        p = this.vocab.getProtocol("some label", src);
        p.setDescription("labeling desc");
        p.setType(type);
        pa.setProtocol(p);
        extract.getProtocolApplications().add(pa);
        pa = new ProtocolApplication();
        pa.setId(9L);
        p = this.vocab.getProtocol("some hybridization", src);
        p.setDescription("hybridization desc");
        type = this.vocab.getTerm(src, "hybridization");
        p.setType(type);
        pa.setProtocol(p);
        lb.getProtocolApplications().add(pa);
        pa = new ProtocolApplication();
        pa.setId(10L);
        p = this.vocab.getProtocol("some scan", src);
        p.setDescription("scan desc");
        type = this.vocab.getTerm(src, "scan");
        p.setType(type);
        pa.setProtocol(p);
        h.getProtocolApplications().add(pa);
        pa = new ProtocolApplication();
        pa.setId(11L);
        p = this.vocab.getProtocol("some treatment", src);
        p.setDescription("treatment desc");
        type = this.vocab.getTerm(src, "treatment");
        p.setType(type);
        pa.setProtocol(p);
        h.getProtocolApplications().add(pa);
        pa = new ProtocolApplication();
        pa.setId(12L);
        p = this.vocab.getProtocol("another treatment", src);
        p.setDescription("another treatment desc");
        type = this.vocab.getTerm(src, "treatment");
        p.setType(type);
        pa.setProtocol(p);
        h.getProtocolApplications().add(pa);
        pa = new ProtocolApplication();
        pa.setId(13L);
        p = this.vocab.getProtocol("some growth", src);
        p.setDescription("growth desc");
        type = this.vocab.getTerm(src, "growth");
        p.setType(type);
        pa.setProtocol(p);
        h.getProtocolApplications().add(pa);

        final RawArrayData rawData = new RawArrayData();
        pa = new ProtocolApplication();
        pa.setId(14L);
        rawData.setName("raw-array-data");
        rawData.getHybridizations().add(h);
        p = this.vocab.getProtocol("data processing", src);
        p.setDescription("data proc desc");
        pa.setProtocol(p);
        rawData.getProtocolApplications().add(pa);
        final CaArrayFile rawCaArrayFile = this.fasStub.add(this.rawFile);
        rawCaArrayFile.setUncompressedSize(TEST_DATA.getBytes().length);
        rawCaArrayFile.setName("raw_file.data");
        rawCaArrayFile.setFileStatus(FileStatus.IMPORTED);
        rawData.setDataFile(rawCaArrayFile);
        h.getRawDataCollection().add(rawData);
        final DerivedArrayData d = new DerivedArrayData();
        final CaArrayFile derCaArrayFile = this.fasStub.add(this.derivedFile);
        derCaArrayFile.setFileType(AFFYMETRIX_CHP);
        derCaArrayFile.setName("derived_file.data");
        derCaArrayFile.setCompressedSize(1024);
        derCaArrayFile.setUncompressedSize(TEST_DATA.getBytes().length);
        derCaArrayFile.setFileStatus(FileStatus.IMPORTED);
        d.setDataFile(derCaArrayFile);
        h.getDerivedDataCollection().add(d);

        final CaArrayFile suppCaArrayFile = this.fasStub.add(this.supplementalFile);
        suppCaArrayFile.setName("supplimental.data");
        suppCaArrayFile.setCompressedSize(1024);
        suppCaArrayFile.setUncompressedSize(TEST_DATA.getBytes().length);
        suppCaArrayFile.setFileStatus(FileStatus.IMPORTED);
        final Field supplementalFilesField = Project.class.getDeclaredField("supplementalFiles");
        supplementalFilesField.setAccessible(true);
        final SortedSet<CaArrayFile> supplementalFiles = (SortedSet<CaArrayFile>) supplementalFilesField.get(prj);
        supplementalFiles.add(suppCaArrayFile);

        final Category ca = this.vocab.getCategory(src, "test-cat");
        final UserDefinedCharacteristic cha = new UserDefinedCharacteristic(ca, "test-val", type);
        source.getCharacteristics().add(cha);
        final UserDefinedFactorValue fv = new UserDefinedFactorValue("test-value", type);
        final Factor fact = new Factor();
        fact.setName("test-factor");
        fv.setFactor(fact);
        h.getFactorValues().add(fv);
        lb.setTissueSite(this.vocab.getTerm(src, "some tissue site"));
        extract.setDiseaseState(this.vocab.getTerm(src, "some disease state"));
        source.setCellType(this.vocab.getTerm(src, "some cell type"));
        sample.setExternalId("test external id");

        lb.getHybridizations().add(h);
        h.getLabeledExtracts().add(lb);
        return experiment;
    }

    /**
     * Test of validateForExport method, of class GeoSoftExporterBean.
     */
    @Test
    public void testValidateForExport() throws Exception {
        final Experiment experiment = makeGoodExperiment();
        final List<String> result = this.bean.validateForExport(experiment);
        assertTrue(result.toString(), result.isEmpty());
    }

    @Test
    public void testBadArrayDesign() throws Exception {
        Experiment experiment = makeGoodExperiment();
        experiment.getArrayDesigns().clear();
        List<String> result = this.bean.validateForExport(experiment);
        assertEquals(1, result.size());
        assertEquals("No (Affymetrix) array design specified", result.get(0));

        experiment = makeGoodExperiment();
        final ArrayDesign ad = experiment.getArrayDesigns().iterator().next();
        ad.getProvider().setName("foo");
        ad.setGeoAccession(null);
        result = this.bean.validateForExport(experiment);
        assertEquals(1, result.size());
        assertEquals("Affymetrix is not the provider for array design test-ad", result.get(0));
    }

    @Test
    public void testBadSingleChannel() throws Exception {
        final Experiment experiment = makeGoodExperiment();
        final Hybridization h = experiment.getHybridizations().iterator().next();
        final RawArrayData rawData = new RawArrayData();
        final ProtocolApplication pa = new ProtocolApplication();
        rawData.setName("raw-array-data2");
        rawData.getHybridizations().add(h);
        final Protocol p = this.vocab.getProtocol("data processing", null);
        p.setDescription("data proc desc");
        pa.setProtocol(p);
        rawData.getProtocolApplications().add(pa);
        h.getRawDataCollection().add(rawData);
        final List<String> result = this.bean.validateForExport(experiment);
        assertEquals(result.toString(), 0, result.size());
    }

    @Test
    public void testNoRawData() throws Exception {
        final Experiment experiment = makeGoodExperiment();
        experiment.getSources().iterator().next().getSamples().iterator().next().getExtracts().iterator().next()
                .getLabeledExtracts().iterator().next().getHybridizations().iterator().next().getRawDataCollection()
                .clear();
        final List<String> result = this.bean.validateForExport(experiment);
        assertEquals(1, result.size());
        assertEquals("Hybridization test-hyb must have at least one Raw Data File", result.get(0));
    }

    @Test
    public void testNoDerivedData() throws Exception {
        final Experiment experiment = makeGoodExperiment();
        final Source source = experiment.getSources().iterator().next();
        final Sample sample1 = source.getSamples().iterator().next();
        final Extract extract = sample1.getExtracts().iterator().next();
        final LabeledExtract lb = extract.getLabeledExtracts().iterator().next();
        final Hybridization hyb = lb.getHybridizations().iterator().next();
        hyb.getDerivedDataCollection().clear();
        final List<String> result = this.bean.validateForExport(experiment);
        assertEquals(1, result.size());
        assertEquals("Hybridization test-hyb must have a derived data file of type AFFYMETRIX_CHP", result.get(0));
    }

    @Test
    public void testNoProtocol() throws Exception {
        final Experiment experiment = makeGoodExperiment();
        experiment.getSources().iterator().next().getSamples().iterator().next().getExtracts().iterator().next()
                .getProtocolApplications().clear();

        final List<String> result = this.bean.validateForExport(experiment);
        assertEquals(1, result.size());
        assertEquals("Missing protocol (one of [labeling] needed)", result.get(0));
    }

    @Test
    public void testNoCharacteristic() throws Exception {
        final Experiment experiment = makeGoodExperiment();
        final Source source = experiment.getSources().iterator().next();

        final Iterator<Sample> it = source.getSamples().iterator();
        final Sample sample1 = it.next();
        final Sample sample2 = it.next();
        final Extract e = sample1.getExtracts().iterator().next();
        final LabeledExtract le = e.getLabeledExtracts().iterator().next();

        source.getCharacteristics().clear();
        le.getHybridizations().iterator().next().getFactorValues().clear();
        le.setTissueSite(null);
        e.setDiseaseState(null);
        source.setCellType(null);
        sample1.setExternalId(null);
        sample2.setExternalId(null);

        final List<String> result = this.bean.validateForExport(experiment);
        assertEquals(1, result.size());
        assertEquals(
                "Hybridization test-hyb and associated biomaterials must have at least one characteristic or factor value",
                result.get(0));
    }

    @Test
    public void testGetPackageingInfo() throws Exception {
        Project p = makeGoodProject();
        Experiment experiment = p.getExperiment();
        List<PackagingInfo> infos = this.bean.getAvailablePackagingInfos(p);
        assertEquals(2, infos.size());
        for (final PackagingInfo pi : infos) {
            switch (pi.getMethod()) {
            case TGZ:
                assertEquals("test-exp-id.tgz", pi.getName());
                break;
            case ZIP:
                assertEquals("test-exp-id.zip", pi.getName());
                break;
            default:
                fail("unexpected method " + pi.getMethod());
            }
        }
        p = makeGoodProject();
        experiment = p.getExperiment();
        final Hybridization h = experiment.getHybridizations().iterator().next();
        final CaArrayFile f = h.getRawDataCollection().iterator().next().getDataFile();
        f.setCompressedSize(Integer.MAX_VALUE);
        infos = this.bean.getAvailablePackagingInfos(p);
        assertEquals(1, infos.size());
        assertEquals("test-exp-id.tgz", infos.get(0).getName());
        assertEquals(PackagingInfo.PackagingMethod.TGZ, infos.get(0).getMethod());
    }

    @Test
    public void testExportArchiveZip() throws Exception {
        final Project p = makeGoodProject();
        final List<PackagingInfo> infos = this.bean.getAvailablePackagingInfos(p);
        final PackagingInfo zipPi = Iterables.find(infos, new Predicate<PackagingInfo>() {
            @Override
            public boolean apply(PackagingInfo t) {
                return t.getMethod() == PackagingInfo.PackagingMethod.ZIP;
            }
        });
        final File f = File.createTempFile("test", zipPi.getName());
        final FileOutputStream fos = new FileOutputStream(f);

        this.bean.export(p, "http://example.com/my_experiemnt", PackagingInfo.PackagingMethod.ZIP, fos);
        fos.close();
        final ZipFile zf = new ZipFile(f);
        final Enumeration<ZipArchiveEntry> en = zf.getEntries();
        final Set<String> entries = new HashSet<String>();
        entries.addAll(java.util.Arrays.asList("test-exp-id.soft.txt", "raw_file.data", "derived_file.data",
                "supplimental.data"));
        while (en.hasMoreElements()) {
            final ZipArchiveEntry ze = en.nextElement();
            assertTrue(ze.getName() + " unexpected", entries.remove(ze.getName()));

        }
        assertTrue(entries.toString() + " not found", entries.isEmpty());
    }

    @Test
    public void testExportArchiveTar() throws Exception {

        final Project p = makeGoodProject();
        final List<PackagingInfo> infos = this.bean.getAvailablePackagingInfos(p);
        final PackagingInfo zipPi = Iterables.find(infos, new Predicate<PackagingInfo>() {
            @Override
            public boolean apply(PackagingInfo t) {
                return t.getMethod() == PackagingInfo.PackagingMethod.ZIP;
            }
        });
        final File f = File.createTempFile("test", zipPi.getName());
        final FileOutputStream fos = new FileOutputStream(f);
        this.bean.export(p, "http://example.com/my_experiemnt", PackagingInfo.PackagingMethod.TGZ, fos);
        fos.close();
        final GZIPInputStream in = new GZIPInputStream(new FileInputStream(f));
        final TarArchiveInputStream tar = new TarArchiveInputStream(in);
        final Set<String> entries = new HashSet<String>();
        entries.addAll(java.util.Arrays.asList("test-exp-id.soft.txt", "raw_file.data", "derived_file.data",
                "supplimental.data", "README.txt"));
        TarArchiveEntry e = tar.getNextTarEntry();
        while (e != null) {
            assertTrue(e.getName() + " unexpected", entries.remove(e.getName()));
            e = tar.getNextTarEntry();
        }
        assertTrue(entries.toString() + " not found", entries.isEmpty());
    }

}