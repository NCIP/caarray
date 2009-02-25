package gov.nih.nci.carpla.rplatab.model.sradfheaders;

import gov.nih.nci.carpla.rplatab.RplaConstants.SradfHeaderType;

import gov.nih.nci.carpla.rplatab.model.sradfheaders.interfaces.HasTermH;
import gov.nih.nci.carpla.rplatab.model.sradfheaders.interfaces.HasTermSourceRefH;
import gov.nih.nci.carpla.rplatab.model.sradfheaders.interfaces.HasUnitH;

public class CharacteristicH extends SradfHeader implements
												HasTermH,
												HasTermSourceRefH,
												HasUnitH {

	private TermSourceRefH	_termsourcerefh	= null;
	private TermH			_termh			= null;
	private UnitH			_unith			= null;

	// private HeaderObj _parent = null ;

	public CharacteristicH() {
		this.headerType = SradfHeaderType.CharacteristicsH;
	}

	public void setTermSourceRefH ( TermSourceRefH termSourceRefH) {

		_termsourcerefh = termSourceRefH;
		// _termsourcerefh.setParent(this);
	}

	public TermH getTermH () {
		return _termh;
	}

	public void setTermH ( TermH termh) {
		_termh = termh;
		// _termh.setParent(this);

	}

	public TermSourceRefH getTermSourcerefH () {
		return _termsourcerefh;
	}

	public UnitH getUnitH () {

		return _unith;
	}

	public void setUnitH ( UnitH unith) {
		_unith = unith;
		// _unith.setParent(this);

	}

	@Override
	public String getDescription () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion () {
		// TODO Auto-generated method stub
		return null;
	}

}
