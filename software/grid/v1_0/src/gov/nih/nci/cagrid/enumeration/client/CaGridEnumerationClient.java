//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.cagrid.enumeration.client;

import gov.nih.nci.cagrid.enumeration.common.CaGridEnumerationI;

import java.rmi.RemoteException;

import org.apache.axis.client.Stub;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.gsi.GlobusCredential;

/**
 * This class is autogenerated, DO NOT EDIT GENERATED GRID SERVICE ACCESS METHODS.
 *
 * This client is generated automatically by Introduce to provide a clean unwrapped API to the
 * service.
 *
 * On construction the class instance will contact the remote service and retrieve it's security
 * metadata description which it will use to configure the Stub specifically for each method call.
 * 
 * @created by Introduce Toolkit version 1.4
 */
public class CaGridEnumerationClient extends CaGridEnumerationClientBase implements CaGridEnumerationI {	

	public CaGridEnumerationClient(String url) throws MalformedURIException, RemoteException {
		this(url,null);	
	}

	public CaGridEnumerationClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	}
	
	public CaGridEnumerationClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
	   	this(epr,null);
	}
	
	public CaGridEnumerationClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
	}

	public static void usage(){
		System.out.println(CaGridEnumerationClient.class.getName() + " -url <service url>");
	}
	
	public static void main(String [] args){
	    System.out.println("Running the Grid Service Client");
		try{
		if(!(args.length < 2)){
			if(args[0].equals("-url")){
			  CaGridEnumerationClient client = new CaGridEnumerationClient(args[1]);
			  // place client calls here if you want to use this main as a
			  // test....
			} else {
				usage();
				System.exit(1);
			}
		} else {
			usage();
			System.exit(1);
		}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

  public org.xmlsoap.schemas.ws._2004._09.enumeration.PullResponse pullOp(org.xmlsoap.schemas.ws._2004._09.enumeration.Pull params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"pullOp");
    return portType.pullOp(params);
    }
  }

  public org.xmlsoap.schemas.ws._2004._09.enumeration.RenewResponse renewOp(org.xmlsoap.schemas.ws._2004._09.enumeration.Renew params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"renewOp");
    return portType.renewOp(params);
    }
  }

  public org.xmlsoap.schemas.ws._2004._09.enumeration.GetStatusResponse getStatusOp(org.xmlsoap.schemas.ws._2004._09.enumeration.GetStatus params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getStatusOp");
    return portType.getStatusOp(params);
    }
  }

  public void releaseOp(org.xmlsoap.schemas.ws._2004._09.enumeration.Release params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"releaseOp");
    portType.releaseOp(params);
    }
  }

}
