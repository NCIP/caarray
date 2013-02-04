//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.array;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Index;

/**
 * Reports on the presence or intensity of a given target probe.
 */
@Entity
public abstract class AbstractProbe extends AbstractDesignElement {

    private AbstractProbeAnnotation annotation;
    private String name;

    /**
     * Constructor.
     */
    public AbstractProbe() {
        super();
    }

    /**
     * @return the annotation
     */
    @OneToOne
    @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.EVICT })
    public AbstractProbeAnnotation getAnnotation() {
        return annotation;
    }

    /**
     * @param annotation the annotation to set
     */
    public void setAnnotation(AbstractProbeAnnotation annotation) {
        this.annotation = annotation;
    }

    /**
     * @return the name
     */
    @Index(name = "ap_name_idx")
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


}
