//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.cagrid.caarray.util;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.log4j.Logger;

/**
 * Pass through to the grid deserialization factory, but with the correct API
 * to actually work.
 */
public final class DeserializationFactory extends BaseDeserializerFactory {
    private static final Logger LOG = Logger.getLogger(DeserializationFactory.class);
    
    public DeserializationFactory(Class javaType, QName xmlType) {
        super(CaArrayDeserializer.class, xmlType, javaType);
        LOG.debug("Initializing CaArrayDeserializerFactory for class:" + javaType + " and QName:" + xmlType);
    }

    public static DeserializationFactory create(Class<?> clazz, QName qname) {
        return new DeserializationFactory(clazz, qname);
    }
}
