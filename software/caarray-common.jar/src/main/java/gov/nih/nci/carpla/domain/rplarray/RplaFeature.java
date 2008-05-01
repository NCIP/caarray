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
public class RplaFeature extends AbstractCaArrayObject{

	private MeasurementCharacteristic	_dilution;
	private RplaReporter				_rplaReporter;

	private int							_blockColumn;
	private int							_blockRow;
	private int							_column;
	private int							_row;

	@OneToOne
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public MeasurementCharacteristic getDilution () {
		return _dilution;
	}

	public void setDilution ( MeasurementCharacteristic _dilution) {
		this._dilution = _dilution;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public RplaReporter getRplaReporter () {
		return _rplaReporter;
	}

	public void setRplaReporter ( RplaReporter reporter) {
		_rplaReporter = reporter;
	}

	@NotNull
	public int getBlockColumn () {
		return _blockColumn;
	}

	public void setBlockColumn ( int column) {
		_blockColumn = column;
	}

	@NotNull
	public int getBlockRow () {
		return _blockRow;
	}

	public void setBlockRow ( int row) {
		_blockRow = row;
	}

	@NotNull
	public int getCol () {
		return _column;
	}

	public void setCol ( int column) {
		this._column = column;
	}

	@NotNull
	public int getRow () {
		return _row;
	}

	public void setRow ( int row) {
		this._row = row;
	}

}
