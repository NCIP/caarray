package gov.nih.nci.carpla.rplatab.sradf;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SradfHeader {

	public SradfHeader(int col, String htype) {
		_column = col;
		_value = htype;

	}

	public SradfHeader(int col, String htype, String term) {
		_column = col;
		_value = htype;
		_term = term;
	}

	private int					_column;
	private String				_value;
	private String				_term			= null;
	private List<SradfHeader>	_annotheaders	= new ArrayList<SradfHeader>();

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
