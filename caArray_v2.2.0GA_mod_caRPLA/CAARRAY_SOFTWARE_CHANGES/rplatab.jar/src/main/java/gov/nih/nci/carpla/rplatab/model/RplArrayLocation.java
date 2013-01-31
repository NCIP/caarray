//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.model;

;

public class RplArrayLocation  implements ArraySectionPrincipal{

	private int	_blockColumn	= -1;

	private int	_blockRow		= -1;

	private int	_column			= -1;

	private int	_row			= -1;

	public int getBlockColumn () {
		return _blockColumn;
	}

	public void setBlockColumn ( int column) {
		_blockColumn = column;
	}

	public int getBlockRow () {
		return _blockRow;
	}

	public void setBlockRow ( int row) {
		_blockRow = row;
	}

	public int getColumn () {
		return _column;
	}

	public void setColumn ( int _column) {
		this._column = _column;
	}

	public int getRow () {
		return _row;
	}

	public void setRow ( int _row) {
		this._row = _row;
	}

	
	
		public String toString(){
			StringBuffer ret = new StringBuffer();
			ret.append("RplArrayLocation (bc=");
			ret.append(getBlockColumn()+",");
			ret.append("br=" + getBlockRow()+",");
			ret.append("c=" + getColumn()+",");
			ret.append("r=" + getRow()+",");
			ret.append(")");
			return ret.toString();
			
		}
		
	}
	
	

