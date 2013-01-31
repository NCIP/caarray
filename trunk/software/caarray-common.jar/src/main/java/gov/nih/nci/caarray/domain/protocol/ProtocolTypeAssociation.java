//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.protocol;

import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;

/**
 * @author Winston Cheng
 *
 */
public enum ProtocolTypeAssociation {
    /**
     * Extract protocol type.
     */
    EXTRACT("extract", "MO", Sample.class),
    /**
     * Labeling protocol type.
     */
    LABELING("labeling", "MO", Extract.class),
    /**
     * Hybridization protocol type.
     */
    HYBRIDIZATION("hybridization", "MO", LabeledExtract.class);

    private final String value;
    private final String source;
    private final Class<?> nodeClass;

    ProtocolTypeAssociation(String value, String source, Class<?> nodeClass) {
        this.value = value;
        this.source = source;
        this.nodeClass = nodeClass;
    }

    /**
     * @return protocol type value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return source name
     */
    public String getSource() {
        return source;
    }

    /**
     * @return node class
     */
    public Class<?> getNodeClass() {
        return nodeClass;
    }
}
