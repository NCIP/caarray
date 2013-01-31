//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.grid.util;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.log4j.Logger;

/**
 * Pass through to the grid serialization factory, but with the correct API
 * to actually work.
 */
public final class SerializationFactory extends BaseSerializerFactory {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(SerializationFactory.class);
    
    /**
     * Construct new serialization factory that will return serializers for
     * given java and XML type.
     * @param javaType the java type
     * @param xmlType the XML type
     */
    public SerializationFactory(Class<?> javaType, QName xmlType) {
        super(CaArraySerializer.class, xmlType, javaType);
        LOG.debug("Initializing CaArraySerializerFactory for class:" + javaType + " and QName:" + xmlType);
    }

    /**
     * Return a new new serialization factory that will return serializers for
     * given java and XML type.
     * @param clazz the java type
     * @param qname the XML type
     * @return the serialization factory
     */
    public static SerializationFactory create(Class<?> clazz, QName qname) {
        return new SerializationFactory(clazz, qname);
    }
}
