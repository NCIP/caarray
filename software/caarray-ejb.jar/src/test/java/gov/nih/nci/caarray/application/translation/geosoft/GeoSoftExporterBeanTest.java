
package gov.nih.nci.caarray.application.translation.geosoft;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gax
 */
public class GeoSoftExporterBeanTest {

    private GeoSoftExporterBean bean;
    VocabularyServiceStub vocab;
    
    @Before
    public void setUp() {
        bean = new GeoSoftExporterBean();
        vocab  = new VocabularyServiceStub();
    }

    Project makeGoodProject() throws Exception {
        Project p = new Project();
        Experiment e = makeGoodExperiment();
        p.setExperiment(e);
        return p;
    }

    Experiment makeGoodExperiment() throws Exception {
        TermSource src = vocab.getSource("MO", "1.3.1.1");
        Experiment experiment = new Experiment();
        experiment.getExperimentDesignTypes().add(vocab.getTerm(src, "test-design-type1"));
        experiment.getExperimentDesignTypes().add(vocab.getTerm(src, "test-design-type2"));
        experiment.setPublicIdentifier("test-exp-id");
        experiment.setTitle("test-title");
        Publication pub = new Publication();
        pub.setPubMedId("test-pub");
        experiment.getPublications().add(pub);
        ExperimentContact ec = new ExperimentContact();
        Person per = new Person();
        per.setFirstName("fff");
        per.setMiddleInitials("mmm");
        per.setLastName("lll");
        ec.setPerson(per);
        experiment.getExperimentContacts().add(ec);
        ArrayDesign ad = new ArrayDesign();
        experiment.getArrayDesigns().add(ad);
        ad.setName("test-ad");
        ad.setGeoAccession("test-ga");
        Organization o = new Organization();
        ad.setProvider(o);
        o.setName("Affymetrix");
        Source source = new Source();
        source.getProviders().add(o);
        source.setName("test-source");
        experiment.getSources().add(source);
        Sample sample = new Sample();
        sample.setName("test-sample");
        Organism ozm = new Organism();
        ozm.setScientificName("test Organizm");
        sample.setOrganism(ozm);
        source.getSamples().add(sample);
        sample.getSources().add(source);
        Extract extract = new Extract();
        extract.setName("test-extract");
        sample.getExtracts().add(extract);
        extract.getSamples().add(sample);
        
        LabeledExtract lb = new LabeledExtract();
        lb.setLabel(vocab.getTerm(src, "label"));
        lb.setMaterialType(vocab.getTerm(src, "test-mat"));
        extract.getLabeledExtracts().add(lb);
        lb.getExtracts().add(extract);
        Hybridization h = new Hybridization();
        Array a = new Array();
        a.setDesign(ad);
        h.setArray(a);
        h.setName("test-hyb");
        experiment.getHybridizations().add(h);
        ProtocolApplication pa = new ProtocolApplication();
        Protocol p = vocab.getProtocol("some extract", src);
        Term type = vocab.getTerm(src, "extract");
        p.setDescription("extract desc");
        p.setType(type);
        pa.setProtocol(p);
        sample.getProtocolApplications().add(pa);
        pa = new ProtocolApplication();
        type = vocab.getTerm(src, "labeling");
        p = vocab.getProtocol("some label", src);
        p.setDescription("labeling desc");
        p.setType(type);
        pa.setProtocol(p);
        extract.getProtocolApplications().add(pa);
        pa = new ProtocolApplication();
        p = vocab.getProtocol("some hybridization", src);
        p.setDescription("hybridization desc");
        type = vocab.getTerm(src, "hybridization");        
        p.setType(type);
        pa.setProtocol(p);
        lb.getProtocolApplications().add(pa);
        pa = new ProtocolApplication();
        p = vocab.getProtocol("some scan", src);
        p.setDescription("scan desc");
        type = vocab.getTerm(src, "scan");        
        p.setType(type);
        pa.setProtocol(p);
        h.getProtocolApplications().add(pa);
        pa = new ProtocolApplication();
        p = vocab.getProtocol("some treatment", src);
        p.setDescription("treatment desc");
        type = vocab.getTerm(src, "treatment");        
        p.setType(type);
        pa.setProtocol(p);
        h.getProtocolApplications().add(pa);
        pa = new ProtocolApplication();
        p = vocab.getProtocol("another treatment", src);
        p.setDescription("another treatment desc");
        type = vocab.getTerm(src, "treatment");
        p.setType(type);
        pa.setProtocol(p);
        h.getProtocolApplications().add(pa);
        pa = new ProtocolApplication();
        p = vocab.getProtocol("some growth", src);
        p.setDescription("growth desc");
        type = vocab.getTerm(src, "growth");        
        p.setType(type);
        pa.setProtocol(p);
        h.getProtocolApplications().add(pa);

        
        RawArrayData rawData = new RawArrayData();
        pa = new ProtocolApplication();
        p = vocab.getProtocol("data processing", src);
        p.setDescription("data proc desc");
        pa.setProtocol(p);
        rawData.getProtocolApplications().add(pa);
        CaArrayFile rawFile = new CaArrayFile();
        File data = new File(GeoSoftExporterBeanTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        data = new File(data, GeoSoftExporterBeanTest.class.getName().replace(".", "/") + ".class");
        rawFile.setCompressedSize(1024);
        rawFile.setUncompressedSize(1024*2);
        rawFile.setName("raw_file.data");
        rawFile.writeContents(new FileInputStream(data));
        rawData.setDataFile(rawFile);
        h.getRawDataCollection().add(rawData);
        DerivedArrayData d = new DerivedArrayData();
        CaArrayFile derFile = new CaArrayFile();
        derFile.setFileType(FileType.AFFYMETRIX_CHP);
        derFile.setName("derived_file.data");
        derFile.setCompressedSize(1024);
        derFile.setUncompressedSize(1024*2);
        derFile.writeContents(new FileInputStream(data));
        d.setDataFile(derFile);
        h.getDerivedDataCollection().add(d);

        Category ca = vocab.getCategory(src, "test-cat");
        UserDefinedCharacteristic cha = new UserDefinedCharacteristic(ca, "test-val", type);
        source.getCharacteristics().add(cha);
        UserDefinedFactorValue fv = new UserDefinedFactorValue("test-value", type);
        Factor fact = new Factor();
        fact.setName("test-factor");
        fv.setFactor(fact);
        h.getFactorValues().add(fv);
        lb.setTissueSite(vocab.getTerm(src, "some tissue site"));
        extract.setDiseaseState(vocab.getTerm(src, "some disease state"));
        source.setCellType(vocab.getTerm(src, "some cell type"));
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
        Experiment experiment = makeGoodExperiment();
        List<String> result = bean.validateForExport(experiment);
        assertTrue(result.toString(), result.isEmpty());
    }

    @Test
    public void testBadArrayDesign() throws Exception {
        Experiment experiment = makeGoodExperiment();
        ArrayDesign ad = experiment.getArrayDesigns().iterator().next();
        ad.getProvider().setName("foo");
        ad.setGeoAccession(null);
        List<String> result = bean.validateForExport(experiment);
        assertEquals(2, result.size());
        assertEquals("Affymetrix is not the provider for array design test-ad", result.get(0));
        assertEquals("Array design test-ad has no GEO accession", result.get(1));
    }

    @Test
    public void testBadSingleChannel() throws Exception {
        Experiment experiment = makeGoodExperiment();
        Source source = new Source();
        source.setName("another-source");
        experiment.getSources().add(source);
        List<String> result = bean.validateForExport(experiment);
        assertEquals(1, result.size());
        assertEquals("Not a single-channel experiemnt (Source another-source must have one sample but has 0)", result.get(0));

        experiment = makeGoodExperiment();
        experiment.getSources().iterator().next().getSamples().iterator().next().getExtracts().clear();
        result = bean.validateForExport(experiment);
        assertEquals(1, result.size());
        assertEquals("Not a single-channel experiemnt (Sample test-sample must have one extract but has 0)", result.get(0));
    }

    @Test
    public void testNoRawData() throws Exception {
        Experiment experiment = makeGoodExperiment();
        experiment.getSources()
                .iterator().next().getSamples()
                .iterator().next().getExtracts()
                .iterator().next().getLabeledExtracts()
                .iterator().next().getHybridizations()
                .iterator().next().getRawDataCollection().clear();
        List<String> result = bean.validateForExport(experiment);
        assertEquals(1, result.size());
        assertEquals("Hybridization test-hyb must have at least one raw data file", result.get(0));
    }

    @Test
    public void testNoDerivedData() throws Exception {
        Experiment experiment = makeGoodExperiment();
        experiment.getSources()
                .iterator().next().getSamples()
                .iterator().next().getExtracts()
                .iterator().next().getLabeledExtracts()
                .iterator().next().getHybridizations()
                .iterator().next().getDerivedDataCollection().clear();
        List<String> result = bean.validateForExport(experiment);
        assertEquals(1, result.size());
        assertEquals("Hybridization test-hyb must have a derived data file of type AFFYMETRIX_CHP", result.get(0));
    }

    @Test
    public void testNoProtocol() throws Exception {
        Experiment experiment = makeGoodExperiment();
        experiment.getSources()
                .iterator().next().getSamples()
                .iterator().next().getExtracts()
                .iterator().next().getProtocolApplications().clear();
                
        List<String> result = bean.validateForExport(experiment);
        assertEquals(1, result.size());
        assertEquals("Missing protocol (one of [labeling] needed)", result.get(0));
    }

    @Test
    public void testNoCharacteristic() throws Exception {
        Experiment experiment = makeGoodExperiment();
        Source source = experiment.getSources()
                .iterator().next();
        
        Sample sample = source.getSamples().iterator().next();
        Extract e = sample.getExtracts().iterator().next();
        LabeledExtract le = e.getLabeledExtracts().iterator().next();

        source.getCharacteristics().clear();
        le.getHybridizations().iterator().next().getFactorValues().clear();
        le.setTissueSite(null);
        e.setDiseaseState(null);
        source.setCellType(null);
        sample.setExternalId(null);
        

        List<String> result = bean.validateForExport(experiment);
        assertEquals(1, result.size());
        assertEquals("Hybridization test-hyb and associated biomaterials must have at least one characteristic or factor value", result.get(0));
    }

    @Test
    public void testGetPackageingInfo() throws Exception {
        Project p = makeGoodProject();
        Experiment experiment = p.getExperiment();
        List<Packaginginfo> infos = bean.getAvailablePackagingInfos(p);
        assertEquals(2, infos.size());
        for (Packaginginfo pi : infos) {
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
        Hybridization h = experiment.getHybridizations().iterator().next();
        CaArrayFile f = h.getRawDataCollection().iterator().next().getDataFile();
        f.setCompressedSize(Integer.MAX_VALUE);
        infos = bean.getAvailablePackagingInfos(p);
        assertEquals(1, infos.size());
        assertEquals("test-exp-id.tgz", infos.get(0).getName());
        assertEquals(Packaginginfo.PackagingMethod.TGZ, infos.get(0).getMethod());
    }

    @Test
    public void testExportAsZip() throws Exception {
        Project p = makeGoodProject();
        Experiment experiment = p.getExperiment();
        List<Packaginginfo> infos = bean.getAvailablePackagingInfos(p);
        Packaginginfo zipPi = Iterables.find(infos, new Predicate<Packaginginfo>() {
            public boolean apply(Packaginginfo t) {
                return t.getMethod() == Packaginginfo.PackagingMethod.ZIP;
            }
        });
        File f = File.createTempFile("test", zipPi.getName());
        FileOutputStream fos = new FileOutputStream(f);
        bean.export(p, "http://example.com/my_experiemnt", Packaginginfo.PackagingMethod.ZIP, fos);
        fos.close();
        ZipFile zf = new ZipFile(f);
        Enumeration<ZipArchiveEntry> en = zf.getEntries();
        Set<String> entries = new HashSet<String>();
        entries.addAll(java.util.Arrays.asList("test-exp-id.soft.txt", "raw_file.data", "derived_file.data"));
        while (en.hasMoreElements()) {
            entries.remove(en.nextElement().getName());
        }
        assertTrue(entries.toString(), entries.isEmpty());


        fos = new FileOutputStream(f);
        bean.export(p, "http://example.com/my_experiemnt", Packaginginfo.PackagingMethod.TGZ, fos);
        fos.close();
        GzipCompressorInputStream in = new GzipCompressorInputStream(new FileInputStream(f));
        TarArchiveInputStream tar = new TarArchiveInputStream(in);
        TarArchiveEntry e = tar.getNextTarEntry();
        entries.addAll(java.util.Arrays.asList("test-exp-id.soft.txt", "raw_file.data", "derived_file.data", "README.txt"));
        while (e != null) {
            entries.remove(e.getName());
            e = tar.getNextTarEntry();
        }
        assertTrue(entries.toString(), entries.isEmpty());

    }

}