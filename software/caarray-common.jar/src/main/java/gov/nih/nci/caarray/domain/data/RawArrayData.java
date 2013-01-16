//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.data;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * Represents the raw image quantitation extracted from an image. In most cases, this can be thought of
 * as representing the values from a single data file, as an example for each Affymetrix CEL file there
 * will be a corresponding <code>RawArrayData</code> instance.
 */
@Entity
@DiscriminatorValue(RawArrayData.DISCRIMINATOR)
public class RawArrayData extends AbstractArrayData {
    private static final long serialVersionUID = 1234567890L;

    /** the Hibernate discriminator for this ArrayData subclass. */
    public static final String DISCRIMINATOR = "RAW";

    private Set<Image> sourceImages = new HashSet<Image>();

    /**
     * @return source images
     */
    @OneToMany(mappedBy = "rawArrayData")
    @Cascade(CascadeType.DELETE)
    public Set<Image> getSourceImages() {
        return sourceImages;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setSourceImages(Set<Image> sourceImages) {
        this.sourceImages = sourceImages;
    }
}
