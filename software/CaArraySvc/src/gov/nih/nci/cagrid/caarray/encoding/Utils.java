package gov.nih.nci.cagrid.caarray.encoding;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class Utils {

	public static String toXML(Object beanObject) throws Exception {

		StringWriter w = new StringWriter();
		Mapping mapping = getMapping("gov/nih/nci/cagrid/caarray/xml-mapping.xml");

		org.exolab.castor.xml.Marshaller marshaller = null;

		marshaller = new org.exolab.castor.xml.Marshaller(w);

		marshaller.setMapping(mapping);
//		 marshaller.setSuppressXSIType(true);

		/** Disabled to improve performance * */
		marshaller.setMarshalAsDocument(true);
		marshaller.setValidation(false);

		System.out.println("About to marshall...");
		marshaller.marshal(beanObject);
		System.out.println("...done");

		return w.getBuffer().toString();
	}

	public static Object fromXML(String xml) throws Exception {

		Mapping mapping = getMapping("gov/nih/nci/cagrid/caarray/xml-mapping.xml");
		Unmarshaller unmarshaller = new Unmarshaller();
		unmarshaller.setMapping(mapping);
		unmarshaller.setWhitespacePreserve(true);
		return unmarshaller.unmarshal(new InputSource(new StringReader(xml)));
	}

	public static Object fromXML(Element el) throws Exception {

		Mapping mapping = getMapping("gov/nih/nci/cagrid/caarray/xml-mapping.xml");
		Unmarshaller unmarshaller = new Unmarshaller();
		unmarshaller.setMapping(mapping);
		unmarshaller.setWhitespacePreserve(true);
		return unmarshaller.unmarshal(el);
	}

	public static Mapping getMapping(String resource) {
		EntityResolver resolver = new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId) {
				if (publicId
						.equals("-//EXOLAB/Castor Object Mapping DTD Version 1.0//EN")) {
					InputStream in = Thread.currentThread()
							.getContextClassLoader().getResourceAsStream(
									"mapping.dtd");
					return new InputSource(in);
				}
				return null;
			}
		};
		org.xml.sax.InputSource mappIS = new org.xml.sax.InputSource(Thread
				.currentThread().getContextClassLoader().getResourceAsStream(
						resource));
		Mapping mapping = new Mapping();
		mapping.setEntityResolver(resolver);
		mapping.loadMapping(mappIS);
		return mapping;
	}

}
