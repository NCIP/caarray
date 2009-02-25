package gov.nih.nci.carpla.rplatab.model.sradfheaders;

import gov.nih.nci.caarray.magetab.sdrf.Provider;
import gov.nih.nci.carpla.rplatab.RplaConstants.SradfHeaderType;

import gov.nih.nci.carpla.rplatab.model.sradfheaders.interfaces.HasCharacteristicsH;
import gov.nih.nci.carpla.rplatab.model.sradfheaders.interfaces.HasCommentH;
import gov.nih.nci.carpla.rplatab.model.sradfheaders.interfaces.HasDescriptionH;
import gov.nih.nci.carpla.rplatab.model.sradfheaders.interfaces.HasMaterialTypeH;
import gov.nih.nci.carpla.rplatab.model.sradfheaders.interfaces.HasProviderH;
import gov.nih.nci.carpla.rplatab.model.sradfheaders.interfaces.PrincipalNodeH;



import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SourceH extends SradfHeader implements
										HasCharacteristicsH,
										HasMaterialTypeH,
										HasProviderH,
										HasDescriptionH,
										HasCommentH,
										PrincipalNodeH {

	public SourceH() {
		this.headerType = SradfHeaderType.SourceH;

	}

	private Vector<CharacteristicH>	_characteristicsh	= new Vector<CharacteristicH>();

	private CommentH				_commenth			= null;

	private DescriptionH			_descriptionh		= null;

	private ProviderH				_providerh			= null;

	MaterialTypeH					_materialTypeh		= null;

	public void addCharacteristicH ( CharacteristicH characteristicsh) {
		_characteristicsh.add(characteristicsh);
		// characteristicsh.setParent(this);

		this.attributeHeaderObjs.add(characteristicsh);

	}

	public MaterialTypeH getMaterialTypeH () {
		return _materialTypeh;
	}

	public void setMaterialTypeH ( MaterialTypeH materialTypeH) {
		_materialTypeh = materialTypeH;
		// _materialTypeh.setParent(this);
		this.attributeHeaderObjs.add(materialTypeH);

	}

	public ProviderH getProviderH () {
		return _providerh;
	}

	public void setProviderH ( ProviderH providerH) {
		_providerh = providerH;
		// _providerh.setParent(this);

		this.attributeHeaderObjs.add(providerH);
	}

	public DescriptionH getDescriptionH () {
		return _descriptionh;
	}

	public void setDescriptionH ( DescriptionH desch) {

		_descriptionh = desch;
		// _descriptionh.setParent(this);
		this.attributeHeaderObjs.add(_descriptionh);
	}

	public CommentH getCommentH () {
		return _commenth;
	}

	public void setCommentH ( CommentH commenth) {
		_commenth = commenth;
		// _commenth.setParent(this);
		this.attributeHeaderObjs.add(_commenth);
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

};
