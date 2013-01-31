//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.model;

import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.carpla.rplatab.sradf.HEADERTYPE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A Protocol is a parameterizable description of a method.
 */


public final class Protocol extends gov.nih.nci.caarray.magetab.Protocol

implements SamplesSectionPrincipal, HasAttribute, HasName {

	public String toString () {
		StringBuffer ret = new StringBuffer();
		ret.append("Protocol (name=");
		ret.append(getName());
		ret.append(" ;params=");
		for (gov.nih.nci.caarray.magetab.Parameter param : getParameters()) {
			ret.append(param.getName() + " ");

		}
		ret.append(")");
		return ret.toString();

	}

}
