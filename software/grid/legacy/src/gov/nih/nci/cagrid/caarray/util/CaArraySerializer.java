//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.cagrid.caarray.util;

import gov.nih.nci.cagrid.encoding.AxisContentHandler;
import gov.nih.nci.cagrid.encoding.EncodingUtils;
import gov.nih.nci.cagrid.encoding.SDKSerializer;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.log4j.Logger;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.Attributes;

/**
 * This is identical to the SDK Serializer but turns off validation. unfortunately, it has to copy the whole serialize
 * method to do so.
 * 
 * @author dkokotov
 */
public class CaArraySerializer extends SDKSerializer {
    private static final Logger LOG = Logger.getLogger(CaArraySerializer.class);

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
            throws IOException {
        long startTime = System.currentTimeMillis();

        AxisContentHandler hand = new AxisContentHandler(context);
        Marshaller marshaller = new Marshaller(hand);

        try {
            Mapping mapping = EncodingUtils.getMapping(context.getMessageContext());
            marshaller.setMapping(mapping);
            marshaller.setValidation(false);
        } catch (MappingException e) {
            LOG.error("Problem establishing castor mapping!  Using default mapping.", e);
        }
        try {
            marshaller.marshal(value);
        } catch (MarshalException e) {
            LOG.error("Problem using castor marshalling.", e);
            throw new IOException("Problem using castor marshalling." + e.getMessage());
        } catch (ValidationException e) {
            LOG.error("Problem validating castor marshalling; message doesn't comply with the associated XML schema.",
                    e);
            throw new IOException(
                    "Problem validating castor marshalling; message doesn't comply with the associated XML schema."
                            + e.getMessage());
        }
        long duration = System.currentTimeMillis() - startTime;
        LOG.debug("Total time to serialize(" + name.getLocalPart() + "):" + duration + " ms.");
    }

}
