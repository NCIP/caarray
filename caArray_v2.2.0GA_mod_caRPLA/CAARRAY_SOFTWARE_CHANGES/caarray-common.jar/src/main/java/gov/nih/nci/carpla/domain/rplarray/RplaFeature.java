package gov.nih.nci.carpla.domain.rplarray;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.NotNull;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;

@Entity
@Table(name = "rplafeature")
public class RplaFeature extends AbstractCaArrayObject {

	// ------------------------------------------------------------
	private MeasurementCharacteristic	_dilution;

	@OneToOne
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public MeasurementCharacteristic getDilution () {
		return _dilution;
	}

	public void setDilution ( MeasurementCharacteristic _dilution) {
		this._dilution = _dilution;
	}

	// ------------------------------------------------------------
	private int	_blockColumn;

	@NotNull
	public int getBlockColumn () {
		return _blockColumn;
	}

	public void setBlockColumn ( int column) {
		_blockColumn = column;
	}

	// ------------------------------------------------------------
	private int	_blockRow;

	@NotNull
	public int getBlockRow () {
		return _blockRow;
	}

	public void setBlockRow ( int row) {
		_blockRow = row;
	}

	// ------------------------------------------------------------
	private int	_column;

	@NotNull
	public int getCol () {
		return _column;
	}

	public void setCol ( int column) {
		this._column = column;
	}

	// ------------------------------------------------------------

	private int	_row;

	@NotNull
	public int getRow () {
		return _row;
	}

	public void setRow ( int row) {
		this._row = row;
	}

	// ------------------------------------------------------------

	private RplaSample	_rplaSample;
	
	public  RplaSample getRplaSample () {
		return _rplaSample;
	}

	public void setRplaSample ( RplaSample rplaSample) {
		this._rplaSample = rplaSample;
	}
	
	
	
	
	
	
	
	
	
}
