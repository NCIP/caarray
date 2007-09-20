package gov.nih.nci.cagrid.caarray;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.cagrid.caarray.encoding.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.cqlresultset.CQLObjectResult;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

@SuppressWarnings("deprecation")
public class LoadCastorMappingTest extends TestCase {

    protected static Log LOG = LogFactory.getLog(LoadCastorMappingTest.class.getName());

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
        f.setPath("zpath");
        f.setType(FileType.AFFYMETRIX_CDF);
        f.setProject(p);
        p.getFiles().add(f);

        Project p2 = roundTrip(p);
        validate(p2);

        Experiment e2 = p2.getExperiment();
        validate(e2);

        assertEquals(publication, e.getPublications().iterator().next());

        CaArrayFile f2 = p.getFiles().iterator().next();
        assertEquals(f, f2);
        assertEquals(FileStatus.IMPORTED, f2.getFileStatus());
        assertEquals(p, f2.getProject()); // Check the back pointer
    }

    public void testProcessor() throws Exception {
        StubProcessor processor = new StubProcessor(); // extends CaArrayCQLQueryProcessor

        // This is the stubbed out 'result' of calling the remote EJB for the processor
        // empty list to start with
        ArrayList<AbstractCaArrayObject> resultList = new ArrayList<AbstractCaArrayObject>();
        processor.setResults(resultList);

        // basic query for project, get back the 0 rows
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        target.setName("gov.nih.nci.caarray.domain.project.Project");
        query.setTarget(target);

        CQLQueryResults results = processor.processQuery(query);
        assertNotNull(results);
        assertNotNull(results.getObjectResult());
        assertEquals(0, results.getObjectResult().length);

        // add a project, verify it comes over
        resultList.add(getProject());
        results = processor.processQuery(query);
        assertNotNull(results);
        assertNotNull(results.getObjectResult());
        assertEquals(1, results.getObjectResult().length);

        CQLObjectResult objResult = results.getObjectResult(0);
        assertEquals("Project", objResult.get_any()[0].getName());

        // test count queries
        QueryModifier queryModifier = new QueryModifier();
        queryModifier.setCountOnly(true);
        query.setQueryModifier(queryModifier);

        results = processor.processQuery(query);
        assertNotNull(results.getCountResult());
        assertEquals(1, results.getCountResult().getCount());

        resultList.add(getExperiment());
        results = processor.processQuery(query);
        assertEquals(2, results.getCountResult().getCount());

        // attribute queries (non-distict and distinct)
        queryModifier.setCountOnly(false);
        queryModifier.setAttributeNames(new String[] {"id", "gridIdentifier"});
        results = processor.processQuery(query);
        assertNotNull(results.getAttributeResult());
        assertEquals(2, results.getAttributeResult().length);
        assertEquals(2, results.getAttributeResult()[0].getAttribute().length);
        assertEquals("id", results.getAttributeResult()[0].getAttribute()[0].getName());
        assertEquals("1", results.getAttributeResult()[0].getAttribute()[0].getValue());
        assertEquals("projectgridid", results.getAttributeResult()[0].getAttribute()[1].getValue());

        queryModifier.setAttributeNames(null);
        queryModifier.setDistinctAttribute("id");
        results = processor.processQuery(query);
        assertNotNull(results.getAttributeResult());
        assertEquals(1, results.getAttributeResult().length);
        assertEquals(1, results.getAttributeResult()[0].getAttribute().length);
        assertEquals("id", results.getAttributeResult()[0].getAttribute()[0].getName());
        assertEquals("1", results.getAttributeResult()[0].getAttribute()[0].getValue());

        resultList.get(1).setId(2L);
        results = processor.processQuery(query);
        assertEquals(2, results.getAttributeResult().length);
        assertEquals(1, results.getAttributeResult()[0].getAttribute().length);
        assertEquals("id", results.getAttributeResult()[0].getAttribute()[0].getName());
        assertEquals("1", results.getAttributeResult()[0].getAttribute()[0].getValue());
        assertEquals("2", results.getAttributeResult()[1].getAttribute()[0].getValue());

    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractCaArrayObject> T roundTrip(T obj) throws Exception {
        byte[] bytes = marshall(obj);

        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        final Reader r = new InputStreamReader(bais);
        final Unmarshaller um = new Unmarshaller(Project.class);
        um.setMapping(Utils.getMapping("gov/nih/nci/cagrid/caarray/xml-mapping.xml"));
        um.setValidation(true);
        final T unmarshalled = (T) um.unmarshal(r);

        return unmarshalled;
    }

    private byte[] marshall(AbstractCaArrayObject obj) throws Exception {
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

    private Project getProject() {
        Project p = new Project();
        p.setId(1L);
        p.setGridIdentifier("projectgridid");
        return p;
    }

    private void validate(Project p) {
        Project orig = getProject();
        assertNotNull(p);
        assertEquals(orig.getGridIdentifier(), p.getGridIdentifier());
        assertEquals(orig.getId(), p.getId());
    }

    private Experiment getExperiment() {
        Experiment e = new Experiment();
        e.setId(1L);
        e.setTitle("experimentTitle");
        e.setDescription("mydescription");
        e.setDateOfExperiment(new Date(54321L));
        e.setPublicReleaseDate(new Date(65432L));
        e.setGridIdentifier("mygridid");
        return e;
    }

    private void validate(Experiment e) {
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
