//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.rplaidf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class RplaIdfHelper {

	private Hashtable<String, Vector<String>>	_map;

	public RplaIdfHelper() {
		_map = new Hashtable<String, Vector<String>>();

	}

	public void addColumnString ( String rowHeader, String value) {
		
		Vector<String> strings = _map.get(rowHeader);
		if (strings == null) {
			strings = new Vector<String>();
			_map.put(rowHeader, strings);
		}
		strings.add(value);
	}

	public List<String> getColumnStrings ( String key) {
		if (_map.get(key) == null) {
			return new ArrayList<String>();
		}
		return _map.get(key);

	}

}
