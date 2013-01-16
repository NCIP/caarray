//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.magetab.sdrf.Comment;

import java.util.List;

/**
 * Interface to be implemented by classes representing SDRF columns which can be followed by a Comment column. 
 * Any column representing a node or edge in the sample-data relationship graph can be modified with a
 * number of Comment columns which represent vendor-specific data in a free text format. 
 * 
 * @author Bill Mason
 */
public interface Commentable {
    /**
     * 
     * @return the list of the Comments modifying the column.
     */
    List<Comment> getComments();
}
