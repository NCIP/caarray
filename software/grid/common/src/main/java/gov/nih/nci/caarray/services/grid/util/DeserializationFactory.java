//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.grid.util;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.log4j.Logger;

/**
 * Pass through to the grid deserialization factory, but with the correct API
 * to actually work.
 */
public final class DeserializationFactory extends BaseDeserializerFactory {
    private static final long serialVersionUID = 1;
    private static final Logger LOG = Logger.getLogger(DeserializationFactory.class);
    
    /**
     * Construct new deserialization factory that will return deserializers for
     * given java and XML type.
     * @param javaType the java type
     * @param xmlType the XML type
     */
    public DeserializationFactory(Class<?> javaType, QName xmlType) {
        super(CaArrayDeserializer.class, xmlType, javaType);
        LOG.debug("Initializing CaArrayDeserializerFactory for class:" + javaType + " and QName:" + xmlType);
    }

    /**
     * Return a new new deserialization factory that will return deserializers for
     * given java and XML type.
     * @param clazz the java type
     * @param qname the XML type
     * @return the deserialization factory
     */
    public static DeserializationFactory create(Class<?> clazz, QName qname) {
        return new DeserializationFactory(clazz, qname);
    }
}
