package gov.nih.nci.carpla.rplatab.rplaidf;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

public class RplaIdfHelper {

	private Hashtable<String, Vector<String>>	_map;

	public RplaIdfHelper() {
		_map = new Hashtable<String, Vector<String>>();

	}

	public void addColumnString ( String rowHeader, String value) {
		//System.out.println(rowHeader + "=" + value);
		Vector<String> strings = _map.get(rowHeader);
		if (strings == null) {
			strings = new Vector<String>();
			_map.put(rowHeader, strings);
		}
		strings.add(value);
	}

	public Vector<String> getColumnStrings ( String key) {
		if (_map.get(key) == null) {
			return new Vector<String>();
		}
		return _map.get(key);

	}

}
