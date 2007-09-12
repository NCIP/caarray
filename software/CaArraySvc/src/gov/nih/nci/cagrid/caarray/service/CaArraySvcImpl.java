package gov.nih.nci.cagrid.caarray.service;

import gov.nih.nci.caarray.services.arraydesign.ArrayDesignDetailsService;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 *
 * @created by Introduce Toolkit version 1.1
 *
 */
public class CaArraySvcImpl extends CaArraySvcImplBase {

    final ArrayDesignDetailsService service;

    public CaArraySvcImpl() throws RemoteException {
        Properties jndiProp = new Properties();
        // TODO We should put the following jndi properties in a file which the user can configure
        // with the right IP address for the JNDI server.
        jndiProp.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
        jndiProp.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
        jndiProp.put("java.naming.provider.url","jnp://165.112.133.35:1099");

        try {
            Context context = new InitialContext(jndiProp);
            service = (ArrayDesignDetailsService) context.lookup(ArrayDesignDetailsService.JNDI_NAME);
        } catch (Exception e) {
            throw new RemoteException("Could not obtain caArray search bean instance", e);
        }
    }

    public gov.nih.nci.caarray.domain.array.ArrayDesignDetails getDesignDetails(
            gov.nih.nci.caarray.domain.array.ArrayDesign arrayDesign) throws RemoteException {
        System.out.println("here.... getDesignDetails()");
        return service.getDesignDetails(arrayDesign);
//        return new ArrayDesignDetails();
//        throw new RemoteException("Not yet implemented");
    }

    public gov.nih.nci.caarray.domain.data.DataSet getDataSet(
            gov.nih.nci.caarray.domain.data.AbstractArrayData abstractArrayData) throws RemoteException {
        System.out.println("here.... getDataSet()");
        throw new RemoteException("Not yet implemented");
    }

    public java.lang.String echo(java.lang.String string) throws RemoteException {
        System.out.println("here.... echo(" + string + ")");
        return string;
    }

}
