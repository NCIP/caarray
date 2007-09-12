package gov.nih.nci.cagrid.caarray;
import gov.nih.nci.cagrid.caarray.encoding.Utils;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoadCastorMappingTest extends TestCase {

    protected static Log LOG = LogFactory.getLog(LoadCastorMappingTest.class.getName());

    public static final String CASTOR_MAPPING_DTD = "mapping.dtd";
    public static final String CASTOR_MAPPING_DTD_ENTITY = "-//EXOLAB/Castor Object Mapping DTD Version 1.0//EN";
    public static final String DEFAULT_XML_MAPPING = "/xml-mapping.xml";
    public static final String CASTOR_MAPPING_PROPERTY = "castorMapping";

    public void testLoadMapping() {
        LOG.debug("123");
        assertNotNull(Utils.getMapping("gov/nih/nci/cagrid/caarray/xml-mapping.xml"));
    }
}
