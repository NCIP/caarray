//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.sradf;

import gov.nih.nci.carpla.rplatab.RplaConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SradfSectionHeaders {

	private RplaConstants.SradfSectionType	_sectionType;
	private List<SradfHeader>				_principalHeaders;
	private int								_totalnumcolumns;

	// private List<HEADERTYPE> _principalHeaderTypes;

	public SradfSectionHeaders() {

		_principalHeaders = new ArrayList<SradfHeader>();
		_totalnumcolumns = 0;
		// _principalHeaderTypes = new ArrayList<HEADERTYPE>();
	}

	public List<SradfHeader> getHeaders () {
		return _principalHeaders;
	}

	public void setTotalNumberOfColumns ( int totalcolnum) {
		_totalnumcolumns = totalcolnum;
	}

	public int getTotalNumberOfColumns () {
		return _totalnumcolumns;
	}

	public void setSectionType ( RplaConstants.SradfSectionType sectionType) {
		_sectionType = sectionType;
	}
	
	public RplaConstants.SradfSectionType getSectionType () {
		return _sectionType ;
	}
	
	
	
	

}
