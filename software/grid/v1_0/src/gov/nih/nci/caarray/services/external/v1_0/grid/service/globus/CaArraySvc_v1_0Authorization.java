package gov.nih.nci.caarray.services.external.v1_0.grid.service.globus;


import java.rmi.RemoteException;
import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import javax.xml.rpc.handler.MessageContext;

import org.globus.gsi.GlobusCredential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.gsi.jaas.JaasGssUtil;
import org.globus.wsrf.impl.security.authentication.Constants;
import org.globus.wsrf.impl.security.authorization.exceptions.AuthorizationException;
import org.globus.wsrf.impl.security.authorization.exceptions.CloseException;
import org.globus.wsrf.impl.security.authorization.exceptions.InitializeException;
import org.globus.wsrf.impl.security.authorization.exceptions.InvalidPolicyException;
import org.globus.wsrf.security.authorization.PDP;
import org.globus.wsrf.security.authorization.PDPConfig;
import org.w3c.dom.Node;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This is a PDP for use with the globus authorization callout.
 * This class will have a authorize<methodName> method for each method on this grid service.
 * The method is responsibe for making any authorization callouts required to satisfy the 
 * authorization requirements placed on each method call.  Each method will either return
 * apon a successful authorization or will throw an exception apon a failed authorization.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class CaArraySvc_v1_0Authorization implements PDP {

	public static final String SERVICE_NAMESPACE = "http://grid.v1_0.external.services.caarray.nci.nih.gov/CaArraySvc_v1_0";
	
	
	public CaArraySvc_v1_0Authorization() {
	}
	
	protected String getServiceNamespace(){
		return SERVICE_NAMESPACE;
	}
	
	public static String getCallerIdentity() {
		String caller = org.globus.wsrf.security.SecurityManager.getManager().getCaller();
		if ((caller == null) || (caller.equals("<anonymous>"))) {
			return null;
		} else {
			return caller;
		}
	}
	
	public static GlobusCredential getInvocationCredential() {
        org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext.getCurrentContext();
        Subject subject = (Subject) ctx.getProperty(Constants.INVOCATION_SUBJECT);
        GlobusGSSCredentialImpl credential = (GlobusGSSCredentialImpl) JaasGssUtil.getCredential(subject);
        return credential.getGlobusCredential();
    }
					
	public static void authorizeGetMultipleResourceProperties() throws RemoteException {
		
		
	}
					
	public static void authorizeGetResourceProperty() throws RemoteException {
		
		
	}
					
	public static void authorizeQueryResourceProperties() throws RemoteException {
		
		
	}
					
	public static void authorizeGetServiceSecurityMetadata() throws RemoteException {
		
		
	}
					
	public static void authorizeGetAllOrganisms() throws RemoteException {
		
		
	}
					
	public static void authorizeSearchForExperiments() throws RemoteException {
		
		
	}
					
	public static void authorizeGetAllPrincipalInvestigators() throws RemoteException {
		
		
	}
					
	public static void authorizeGetAllFileTypes() throws RemoteException {
		
		
	}
					
	public static void authorizeGetByReference() throws RemoteException {
		
		
	}
					
	public static void authorizeGetByReferences() throws RemoteException {
		
		
	}
					
	public static void authorizeEnumerateOrganisms() throws RemoteException {
		
		
	}
					
	public static void authorizeEnumerateExperiments() throws RemoteException {
		
		
	}
					
	public static void authorizeGetFileContentsZipTransfer() throws RemoteException {
		
		
	}
					
	public static void authorizeGetFileContentsTransfers() throws RemoteException {
		
		
	}
					
	public static void authorizeEnumerateFileContentTransfers() throws RemoteException {
		
		
	}
					
	public static void authorizeGetFileContentsTransfer() throws RemoteException {
		
		
	}
					
	public static void authorizeGetDataSet() throws RemoteException {
		
		
	}
					
	public static void authorizeQuery() throws RemoteException {
		
		
	}
					
	public static void authorizeSearchForBiomaterials() throws RemoteException {
		
		
	}
					
	public static void authorizeSearchForHybridizations() throws RemoteException {
		
		
	}
					
	public static void authorizeSearchForExperimentsByKeyword() throws RemoteException {
		
		
	}
					
	public static void authorizeSearchForFiles() throws RemoteException {
		
		
	}
					
	public static void authorizeSearchForBiomaterialsByKeyword() throws RemoteException {
		
		
	}
	
	
	public boolean isPermitted(Subject peerSubject, MessageContext context, QName operation)
		throws AuthorizationException {
		
		if(!operation.getNamespaceURI().equals(getServiceNamespace())){
		  return false;
		}
		if(operation.getLocalPart().equals("getMultipleResourceProperties")){
			try{
				authorizeGetMultipleResourceProperties();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("getResourceProperty")){
			try{
				authorizeGetResourceProperty();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("queryResourceProperties")){
			try{
				authorizeQueryResourceProperties();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("getServiceSecurityMetadata")){
			try{
				authorizeGetServiceSecurityMetadata();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("getAllOrganisms")){
			try{
				authorizeGetAllOrganisms();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("searchForExperiments")){
			try{
				authorizeSearchForExperiments();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("getAllPrincipalInvestigators")){
			try{
				authorizeGetAllPrincipalInvestigators();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("getAllFileTypes")){
			try{
				authorizeGetAllFileTypes();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("getByReference")){
			try{
				authorizeGetByReference();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("getByReferences")){
			try{
				authorizeGetByReferences();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("enumerateOrganisms")){
			try{
				authorizeEnumerateOrganisms();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("enumerateExperiments")){
			try{
				authorizeEnumerateExperiments();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("getFileContentsZipTransfer")){
			try{
				authorizeGetFileContentsZipTransfer();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("getFileContentsTransfers")){
			try{
				authorizeGetFileContentsTransfers();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("enumerateFileContentTransfers")){
			try{
				authorizeEnumerateFileContentTransfers();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("getFileContentsTransfer")){
			try{
				authorizeGetFileContentsTransfer();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("getDataSet")){
			try{
				authorizeGetDataSet();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("query")){
			try{
				authorizeQuery();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("searchForBiomaterials")){
			try{
				authorizeSearchForBiomaterials();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("searchForHybridizations")){
			try{
				authorizeSearchForHybridizations();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("searchForExperimentsByKeyword")){
			try{
				authorizeSearchForExperimentsByKeyword();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("searchForFiles")){
			try{
				authorizeSearchForFiles();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} else if(operation.getLocalPart().equals("searchForBiomaterialsByKeyword")){
			try{
				authorizeSearchForBiomaterialsByKeyword();
				return true;
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
		} 		
		return false;
	}
	

	public Node getPolicy(Node query) throws InvalidPolicyException {
		return null;
	}


	public String[] getPolicyNames() {
		return null;
	}


	public Node setPolicy(Node policy) throws InvalidPolicyException {
		return null;
	}


	public void close() throws CloseException {


	}


	public void initialize(PDPConfig config, String name, String id) throws InitializeException {

	}
	
	
}
