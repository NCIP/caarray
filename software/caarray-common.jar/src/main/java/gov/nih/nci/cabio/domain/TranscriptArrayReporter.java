//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.cabio.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 */
@Entity
@DiscriminatorValue("TRANSCRIPTREPORTER")
public class TranscriptArrayReporter extends ArrayReporter {

    private static final long serialVersionUID = 1L;

}
