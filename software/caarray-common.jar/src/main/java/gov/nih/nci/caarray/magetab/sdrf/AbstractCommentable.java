//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.Commentable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * An entity within an SDRF document -- may be a bio material, hybridization, or
 * data object.
 */
public abstract class AbstractCommentable implements Commentable, Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Comment> comments = new ArrayList<Comment>();

    /**
     * {@inheritDoc}
     */
    public List<Comment> getComments() {
        return comments;
    }
}
