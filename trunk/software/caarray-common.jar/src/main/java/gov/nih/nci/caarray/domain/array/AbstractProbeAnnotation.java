//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Base class for caArray probe annotations.
 */
@Entity
@Table(name = "probeannotation")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "discriminator",
        discriminatorType = DiscriminatorType.STRING
)
@SuppressWarnings("PMD.AbstractClassWithoutAnyMethod")
public abstract class AbstractProbeAnnotation extends AbstractCaArrayEntity {
    /** 
     * Batch size for all associations for probe annotation subclasses.
     */
    public static final int RELATED_BATCH_SIZE = 500;
}
