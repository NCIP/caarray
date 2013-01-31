//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.EntryHeading;

/**
 * A single column in an SDRF file.
 */
class SdrfColumn {

    private final EntryHeading heading;
    private final SdrfColumnType type;

    SdrfColumn(EntryHeading heading) {
        this.heading = heading;
        type = SdrfColumnType.get(heading.getTypeName());
    }

    SdrfColumnType getType() {
        return type;
    }

    EntryHeading getHeading() {
        return heading;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return heading.toString();
    }

}
