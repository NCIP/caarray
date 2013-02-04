//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.EntryHeading;

/**
 * A single column in an SDRF file.
 */
public class SdrfColumn {

    private final EntryHeading heading;
    private final SdrfColumnType type;

    /**
     * Create a new SdrfColumn for given heading.
     * @param heading the column header for this column
     */
    public SdrfColumn(EntryHeading heading) {
        this.heading = heading;
        type = SdrfColumnType.get(heading.getTypeName());
    }

    /**
     * @return the type of this column
     */
    public SdrfColumnType getType() {
        return type;
    }

    /**
     * @return the heading for this column
     */
    public EntryHeading getHeading() {
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
