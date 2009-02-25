package gov.nih.nci.carpla.rplatab.model;

import java.io.Serializable;

public class RplArrayFeature {

	private RplArrayLocation	_location;

	private Sample				_sample;

	private Dilution			_dilution;

	public Dilution getDilution () {
		return _dilution;
	}

	public void setDilution ( Dilution dilution) {
		this._dilution = _dilution;
	}

	public Sample getSample () {
		return _sample;
	}

	public void setSample ( Sample sample) {
		this._sample = sample;
	}

	public RplArrayLocation getRplArrayLocation () {
		return _location;
	}

	public void setRplArrayLocation ( RplArrayLocation rarrayloc) {
		_location = rarrayloc;
	}

}
