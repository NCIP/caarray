package gov.nih.nci.caarray.services.external.v1_0.grid.common;

import javax.xml.namespace.QName;


public interface CaArraySvc_v1_0Constants {
	public static final String SERVICE_NS = "http://grid.v1_0.external.services.caarray.nci.nih.gov/CaArraySvc_v1_0";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "CaArraySvc_v1_0Key");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "CaArraySvc_v1_0ResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName SERVICEMETADATA = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
	public static final QName DOMAINMODEL = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice", "DomainModel");
	
}