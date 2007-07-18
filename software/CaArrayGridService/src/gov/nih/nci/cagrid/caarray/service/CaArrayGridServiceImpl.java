package gov.nih.nci.cagrid.caarray.service;

import java.util.Properties;
import java.util.List;
import java.rmi.RemoteException;
import javax.naming.Context;
import javax.naming.InitialContext;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.application.CaArraySearchService;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.0
 * 
 */
public class CaArrayGridServiceImpl extends CaArrayGridServiceImplBase {
	Context context;
	CaArraySearchService searchBean;

	public CaArrayGridServiceImpl() throws RemoteException {
		super();
		Properties jndiProp = new Properties();
		// TODO We should put the following jndi properties in a file which the user can configure
		// with the right IP address for the JNDI server.
		jndiProp.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
		jndiProp.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
		jndiProp.put("java.naming.provider.url","jnp://156.40.192.42:1099");

		try {
			context = new InitialContext(jndiProp);
			searchBean = (CaArraySearchService) context.lookup(CaArraySearchService.JNDI_NAME);
		} catch (Exception e) {
			throw new RemoteException("Could not obtain caArray search bean instance", e);
		}
	}
	
	public gov.nih.nci.caarray.domain.AbstractCaArrayEntity[] search(gov.nih.nci.caarray.domain.AbstractCaArrayEntity abstractCaArrayEntity) throws RemoteException {
		List<AbstractCaArrayEntity> retrievedEntities = null;
		try {
				retrievedEntities = searchBean.search(abstractCaArrayEntity);
				return (AbstractCaArrayEntity[]) retrievedEntities.toArray(new AbstractCaArrayEntity[0]);
		} catch (Exception e) {
			throw new RemoteException("Error searching by example: ", e);
		}
	}

}

