//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.sradf;

import java.util.Vector;

import gov.nih.nci.carpla.rplatab.RplaConstants;
import gov.nih.nci.carpla.rplatab.RplaConstants.SradfSectionType;
import gov.nih.nci.carpla.rplatab.files.SradfFile;

public class SradfHeaders {

	private SradfFile			_fileholder;

	private SradfSectionHeaders	_samplesSectionHeaders;
	private SradfSectionHeaders	_arraySectionHeaders;
	private SradfSectionHeaders	_arrayDataSectionHeaders;

	public SradfHeaders() {

	}

	public void setSradfFileHolder ( SradfFile sfileholder) {
		_fileholder = sfileholder;

	}

	public void setSectionHeaders ( SradfSectionHeaders samplessectionheaders,
									SradfSectionHeaders arraylocationssectionheaders,
									SradfSectionHeaders arraydatasectionheaders)
	{
		this._samplesSectionHeaders = samplessectionheaders;
		this._arraySectionHeaders = arraylocationssectionheaders;
		this._arrayDataSectionHeaders = arraydatasectionheaders;
	}

	
	
	
	public SradfSectionHeaders getSamplesSectionHeaders(){
		return _samplesSectionHeaders;
		
	}
	
	public SradfSectionHeaders getArraySectionHeaders(){
		return _arraySectionHeaders;
		
	}
	
	public SradfSectionHeaders getArrayDataSectionHeaders(){
		return _arrayDataSectionHeaders;
		
	}
	
	
	
	
	
	
	
	
}
