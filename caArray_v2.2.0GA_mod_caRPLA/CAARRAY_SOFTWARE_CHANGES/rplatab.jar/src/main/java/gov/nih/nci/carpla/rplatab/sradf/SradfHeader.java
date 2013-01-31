//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.sradf;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SradfHeader {

	private int					_column;
	private String				_value;
	private String				_term			= null;
	private String				_optionalRefinedTerm			= null;
	
	private List<SradfHeader>	_annotheaders	= new ArrayList<SradfHeader>();

	public SradfHeader(int col, String htype) {
		_column = col;
		_value = htype;

	}

	public SradfHeader(int col, String htype, String term) {
		_column = col;
		_value = htype;
		_term = term;
	}

	public SradfHeader(	int col,
						String htype,
						String term,
						String optionalrefinedterm) {
		_column = col;
		_value = htype;
		_term = term;
		_optionalRefinedTerm = optionalrefinedterm ;
	}

	public String getTerm () {
		return _term;
	}

	public void add ( SradfHeader header) {
		_annotheaders.add(header);
	}

	public int getCol () {
		return _column;
	}

	public String getValue () {
		return _value;
	}

	public List<SradfHeader> getSubHeaders () {
		return _annotheaders;
	}

};
