//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.cagrid.caarray.util;

import gov.nih.nci.cagrid.encoding.EncodingUtils;
import gov.nih.nci.cagrid.encoding.SDKDeserializer;

import java.io.StringReader;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;

/**
 * This is identical to the SDK Serializer but turns off validation. unfortunately, it has to copy the whole deserialize
 * method to do so. Also, the original method produces a DOM from the element prior to unmarshalling it. This is sometimes 
 * inefficient, and we use the unmarshal variant that can read directly from a String.
 * 
 * @author dkokotov
 */
public class CaArrayDeserializer extends SDKDeserializer {
    private static final Logger LOG = Logger.getLogger(CaArrayDeserializer.class);

    public CaArrayDeserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }


    public void onEndElement(String namespace, String localName, DeserializationContext context) {
        long startTime=System.currentTimeMillis();
        Unmarshaller unmarshall = new Unmarshaller(javaType);

        try {
            Mapping mapping = EncodingUtils.getMapping(context.getMessageContext());
            if (mapping != null) {
                unmarshall.setMapping(mapping);
            } else {
                LOG.error("Castor mapping was null!  Using default mapping.");
            }
            unmarshall.setValidation(false);
        } catch (MappingException e) {
            LOG.error("Problem establishing castor mapping!  Using default mapping.", e);
        }

        MessageElement msgElem = context.getCurElement();
        String asStr = null;
        try {
            asStr = msgElem.getAsString();
        } catch (Exception e) {
            LOG.error("Problem extracting message type! Result will be null!", e);
        }
        if (asStr != null) {
            try {
                StringReader reader = new StringReader(asStr);
                value = unmarshall.unmarshal(reader);
                reader.close();
            } catch (MarshalException e) {
                LOG.error("Problem with castor marshalling!", e);
            } catch (ValidationException e) {
                LOG.error("XML does not match schema!", e);
            }
        }
        long duration=System.currentTimeMillis()- startTime;
        LOG.debug("Total time to deserialize("+localName+"):"+duration+" ms.");
    }

}
