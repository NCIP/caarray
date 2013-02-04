//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.idf;

import gov.nih.nci.caarray.magetab.EntryHeading;

import java.io.Serializable;

/**
 * A single row in an IDF document.
 */
class IdfRow implements Serializable {

    private final EntryHeading heading;
    private final IdfRowType type;

    IdfRow(EntryHeading heading, IdfRowType type) {
        this.heading = heading;
        this.type = type;
    }

    private static final long serialVersionUID = 1L;

    EntryHeading getHeading() {
        return heading;
    }

    IdfRowType getType() {
        return type;
    }

}
