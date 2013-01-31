//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.model;

import gov.nih.nci.carpla.rplatab.sradf.HEADERTYPE;

import java.util.ArrayList;
import java.util.List;

/**
 * A biological sample taken from a source.
 */
public final class Sample extends AbstractBioMaterial implements

SamplesSectionPrincipal, HasCharacteristics, HasComment, HasAttribute {

	private List<Comment>	_comments	= new ArrayList<Comment>();

	private String			_name;

	public void addComment ( Comment comment) {
		_comments.add(comment);
	}

	public String getName () {

		return _name;
	}

	public void setName ( String bioSampleName) {
		_name = bioSampleName;

	}

	public String toString () {
		StringBuffer ret = new StringBuffer();
		ret.append("Sample (name=");
		ret.append(getName() + ")");
		return ret.toString();

	}

}
