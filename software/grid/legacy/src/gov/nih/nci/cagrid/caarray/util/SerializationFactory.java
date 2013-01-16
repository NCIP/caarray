//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.cagrid.caarray.util;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.log4j.Logger;

/**
 * Pass through to the grid serialization factory, but with the correct API
 * to actually work.
 */
public final class SerializationFactory extends BaseSerializerFactory {
    private static final Logger LOG = Logger.getLogger(SerializationFactory.class);
    
    public SerializationFactory(Class javaType, QName xmlType) {
        super(CaArraySerializer.class, xmlType, javaType);
        LOG.debug("Initializing CaArraySerializerFactory for class:" + javaType + " and QName:" + xmlType);
    }

    public static SerializationFactory create(Class<?> clazz, QName qname) {
        return new SerializationFactory(clazz, qname);
    }
}
