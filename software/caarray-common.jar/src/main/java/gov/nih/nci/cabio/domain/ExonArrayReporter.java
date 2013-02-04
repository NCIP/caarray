//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.cabio.domain;

import gov.nih.nci.caarray.domain.array.AbstractProbeAnnotation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.BatchSize;

/**
 *
 */
@Entity
@DiscriminatorValue("EXONARRAYREPORTER")
@BatchSize(size = AbstractProbeAnnotation.RELATED_BATCH_SIZE)
public class ExonArrayReporter extends TranscriptArrayReporter {

    private static final long serialVersionUID = 1L;
    private Long probeSelectionRegionId;
    private Long probeCount;
    private String strand;
    /**
     * @return the probeCount
     */
    public Long getProbeCount() {
        return probeCount;
    }
    /**
     * @param probeCount the probeCount to set
     */
    public void setProbeCount(Long probeCount) {
        this.probeCount = probeCount;
    }
    /**
     * @return the probeSelectionRegionId
     */
    public Long getProbeSelectionRegionId() {
        return probeSelectionRegionId;
    }
    /**
     * @param probeSelectionRegionId the probeSelectionRegionId to set
     */
    public void setProbeSelectionRegionId(Long probeSelectionRegionId) {
        this.probeSelectionRegionId = probeSelectionRegionId;
    }
    /**
     * @return the strand
     */
    public String getStrand() {
        return strand;
    }
    /**
     * @param strand the strand to set
     */
    public void setStrand(String strand) {
        this.strand = strand;
    }

}
