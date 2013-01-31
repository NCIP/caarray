//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.cabio.domain;

import gov.nih.nci.caarray.domain.array.AbstractProbeAnnotation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.BatchSize;

/**
 *
 */
@Entity
@DiscriminatorValue("EXPRESSIONARRAYREPORTER")
@BatchSize(size = AbstractProbeAnnotation.RELATED_BATCH_SIZE)
public class ExpressionArrayReporter extends TranscriptArrayReporter {

    private static final long serialVersionUID = 1L;

    private String sequenceSource;
    private String sequenceType;
    private String targetDescription;
    private String targetId;
    /**
     * @return the sequenceSource
     */
    public String getSequenceSource() {
        return sequenceSource;
    }
    /**
     * @param sequenceSource the sequenceSource to set
     */
    public void setSequenceSource(String sequenceSource) {
        this.sequenceSource = sequenceSource;
    }
    /**
     * @return the sequenceType
     */
    public String getSequenceType() {
        return sequenceType;
    }
    /**
     * @param sequenceType the sequenceType to set
     */
    public void setSequenceType(String sequenceType) {
        this.sequenceType = sequenceType;
    }
    /**
     * @return the targetDescription
     */
    public String getTargetDescription() {
        return targetDescription;
    }
    /**
     * @param targetDescription the targetDescription to set
     */
    public void setTargetDescription(String targetDescription) {
        this.targetDescription = targetDescription;
    }
    /**
     * @return the targetId
     */
    public String getTargetId() {
        return targetId;
    }
    /**
     * @param targetId the targetId to set
     */
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

}
