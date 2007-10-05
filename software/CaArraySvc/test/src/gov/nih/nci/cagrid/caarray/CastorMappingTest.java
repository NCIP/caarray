package gov.nih.nci.cagrid.caarray;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.cagrid.caarray.encoding.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

@SuppressWarnings("deprecation")
public class CastorMappingTest extends TestCase {

    protected static Log LOG = LogFactory.getLog(CastorMappingTest.class.getName());

    public static final String CASTOR_MAPPING_DTD = "mapping.dtd";
    public static final String CASTOR_MAPPING_DTD_ENTITY = "-//EXOLAB/Castor Object Mapping DTD Version 1.0//EN";
    public static final String DEFAULT_XML_MAPPING = "/xml-mapping.xml";
    public static final String CASTOR_MAPPING_PROPERTY = "castorMapping";

    public void testLoadMapping() throws IOException, MappingException {
        assertNotNull(Utils.getMapping("gov/nih/nci/cagrid/caarray/xml-mapping.xml"));
    }

    public void testBasic() throws Exception {
        Project p = getProject();
        Project p2 = roundTrip(p);

        validate(p2);
    }

    public void testAdvanced() throws Exception {
        Experiment e = getExperiment();

        Publication publication = new Publication();
        publication.setId(1L);
        publication.setTitle("title");
        e.getPublications().add(publication);
        publication.setExperiment(e);

        Project p = getProject();
        p.setExperiment(e);

        CaArrayFile f = new CaArrayFile();
        f.setId(1L);
        f.setFileStatus(FileStatus.IMPORTED);
        f.setGridIdentifier("filegridid");
        f.setName("zpath");
        f.setType(FileType.AFFYMETRIX_CDF);
        f.setProject(p);
        p.getFiles().add(f);

        Project p2 = roundTrip(p);
        validate(p2);

        Experiment e2 = p2.getExperiment();
        validate(e2);

        assertEquals(publication, e2.getPublications().iterator().next());

        CaArrayFile f2 = p2.getFiles().iterator().next();
        assertEquals(f, f2);
        assertEquals(FileStatus.IMPORTED, f2.getFileStatus());
        // commenting this out for now, since we're eliminating backpointer references.
        // assertEquals(p, f2.getProject()); // Check the back pointer
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractCaArrayObject> T roundTrip(T obj) throws Exception {
        byte[] bytes = marshall(obj);

        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        final Reader r = new InputStreamReader(bais);
        final Unmarshaller um = new Unmarshaller(Project.class);
        um.setMapping(Utils.getMapping("gov/nih/nci/cagrid/caarray/xml-mapping.xml"));
        um.setValidation(true);
        final T unmarshalled = (T) um.unmarshal(r);

        return unmarshalled;
    }

    private static byte[] marshall(AbstractCaArrayObject obj) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Writer w = new OutputStreamWriter(baos);
        final Marshaller m = new Marshaller(w);
        final Mapping mapping = Utils.getMapping("gov/nih/nci/cagrid/caarray/xml-mapping.xml");
        m.setMapping(mapping);
        m.setValidation(true);
        m.marshal(obj);

        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    static Project getProject() {
        Project p = Project.createNew();
        p.setId(1L);
        p.setGridIdentifier("projectgridid");
        return p;
    }

    private static void validate(Project p) {
        Project orig = getProject();
        assertNotNull(p);
        assertEquals(orig.getGridIdentifier(), p.getGridIdentifier());
        assertEquals(orig.getId(), p.getId());
    }

    static Experiment getExperiment() {
        Experiment e = new Experiment();
        e.setId(1L);
        e.setTitle("experimentTitle");
        e.setDescription("mydescription");
        e.setDateOfExperiment(new Date(54321L));
        e.setPublicReleaseDate(new Date(65432L));
        e.setGridIdentifier("mygridid");
        return e;
    }

    private static void validate(Experiment e) {
        Experiment orig = getExperiment();
        assertNotNull(e);
        assertEquals(orig.getId(), e.getId());
        assertEquals(orig.getTitle(), e.getTitle());
        assertEquals(orig.getDescription(), e.getDescription());
        assertEquals(orig.getDateOfExperiment(), e.getDateOfExperiment());
        assertEquals(orig.getPublicReleaseDate(), e.getPublicReleaseDate());
        assertEquals(orig.getGridIdentifier(), e.getGridIdentifier());
    }
}
