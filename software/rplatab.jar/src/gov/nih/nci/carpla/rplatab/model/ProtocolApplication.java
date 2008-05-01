package gov.nih.nci.carpla.rplatab.model;

public class ProtocolApplication
								extends
								gov.nih.nci.caarray.magetab.ProtocolApplication
																				implements
																				SectionPrincipal, HasAttribute {

	public String toString () {
		StringBuffer buffie = new StringBuffer();
		buffie.append("ProtocolApplication(");
		buffie.append(((gov.nih.nci.carpla.rplatab.model.Protocol)getProtocol()).toString()+")");
		return buffie.toString();

	}
}