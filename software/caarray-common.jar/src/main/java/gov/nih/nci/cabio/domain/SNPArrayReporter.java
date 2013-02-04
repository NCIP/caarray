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
@DiscriminatorValue("SNPARRAYREPORTER")
@BatchSize(size = AbstractProbeAnnotation.RELATED_BATCH_SIZE)
public class SNPArrayReporter extends ArrayReporter {

    private static final long serialVersionUID = 1L;

    private String phastConservation;

    /**
     * @return the phastConservation
     */
    public String getPhastConservation() {
        return phastConservation;
    }

    /**
     * @param phastConservation the phastConservation to set
     */
    public void setPhastConservation(String phastConservation) {
        this.phastConservation = phastConservation;
    }

}
