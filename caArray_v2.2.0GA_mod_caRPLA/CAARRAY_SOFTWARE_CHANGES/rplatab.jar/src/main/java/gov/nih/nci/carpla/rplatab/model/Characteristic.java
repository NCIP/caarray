//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.model;

import gov.nih.nci.caarray.magetab.OntologyTerm;

import java.io.Serializable;

// I wouldn't mind extending Charcteristic from the magetab package but it's
// marked final. I think I will change that!

public final class Characteristic
									extends
									gov.nih.nci.caarray.magetab.sdrf.Characteristic
																					implements
																					Serializable,
																					HasAttribute {

}
