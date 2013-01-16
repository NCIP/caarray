//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.cagrid.caarray.service.globus.resource;

import javax.xml.namespace.QName;


public interface ResourceConstants {
	public static final String SERVICE_NS = "http://caarray.cagrid.nci.nih.gov/CaArraySvc";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "CaArraySvcKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "CaArraySvcResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName SERVICEMETADATA_Value_RP = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
	public static final QName DOMAINMODEL_Value_RP = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice", "DomainModel");
	
}
