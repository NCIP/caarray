package gov.nih.nci.logging.webapp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import gov.nih.nci.security.AuthenticationManager;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElementPrivilegeContext;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

public class SecurityManager
{
	private static AuthenticationManager authenticationManager = null;
	private static AuthorizationManager authorizationManager = null;

	
	private static AuthenticationManager getAuthenticationManager() throws Exception
	{
		if (null == authenticationManager)
		{
			authenticationManager = SecurityServiceProvider.getAuthenticationManager(Constants.APPLICATION_CONTEXT_NAME);
		}
		return authenticationManager;
	}
	
	private static AuthorizationManager getAuthorizationManager() throws Exception
	{
		if (null == authorizationManager)
		{
			authorizationManager = SecurityServiceProvider.getAuthorizationManager(Constants.APPLICATION_CONTEXT_NAME);
		}
		return authorizationManager;
	}
	
	public static boolean login(String userName, String password) throws Exception
	{
		return getAuthenticationManager().login(userName,password);		
	}
	
	public static ArrayList getProtectedAtrributeValues(HttpServletRequest request, String attributeName)
	{
		HashMap protectedAttributes = (HashMap)request.getAttribute(Constants.PROTECTED_ATTRIBUTES);
		return (ArrayList)protectedAttributes.get(attributeName);	
	}
	
	public static boolean checkPermission(HttpServletRequest request, String applicationName, String userName, String attributeName, String attributeValue) throws Exception
	{
		if (attributeName.equals(Constants.APPLICATION_NAME_ATTRIBUTE))
			return getAuthorizationManager().checkPermission(userName,attributeName + ":" + attributeValue, Constants.PRIVILEGE);
		else
		{
			HashMap protectedAttributes = (HashMap)request.getAttribute(Constants.PROTECTED_ATTRIBUTES);
			if(protectedAttributes==null) return true;
			
			if (protectedAttributes.containsKey(attributeName))
			{
				return getAuthorizationManager().checkPermission(userName, Constants.APPLICATION_NAME_ATTRIBUTE + applicationName + "&" + attributeName + ":" + attributeValue, Constants.PRIVILEGE);
			}
			else return true;
		}
	}

	public static HashMap loadProtectedAttributes(String loginID)
	{
		HashMap protectedAttributes  = new HashMap();
		
		UserProvisioningManager upm = (UserProvisioningManager)authorizationManager;
		Collection protectionElementPrivilegeContexts = null;
		User user = upm.getUser(loginID);
		if (user == null)
		{
			throw new RuntimeException ("User Name doesnot exist in the Authorization Schema");
		}
		try
		{
			protectionElementPrivilegeContexts = upm.getProtectionElementPrivilegeContextForUser(user.getUserId().toString());
		}
		catch (CSObjectNotFoundException e)
		{
			throw new RuntimeException ("User Name doesnot Exist");
		}
		if ( protectionElementPrivilegeContexts != null && protectionElementPrivilegeContexts.size() != 0 )
		{
			Iterator iterator = protectionElementPrivilegeContexts.iterator();
			String objectId = null;
			while (iterator.hasNext())
			{
				ProtectionElementPrivilegeContext protectionElementPrivilegeContext = (ProtectionElementPrivilegeContext)iterator.next();
				ProtectionElement protectionElement = protectionElementPrivilegeContext.getProtectionElement();
				objectId = protectionElement.getObjectId();					
				String attributePart = null;
				try{
					attributePart = objectId.substring(objectId.indexOf('&'));
				}catch(Exception e){
					continue;
				}
					
				if (attributePart != null & attributePart.length() != 0)
				{
					String attributeName = attributePart.substring(1,attributePart.indexOf(':'));
					String attributeValue = attributePart.substring(attributePart.indexOf(':')+1);
					if (protectedAttributes.containsKey(attributeName))
					{
						ArrayList list = (ArrayList)protectedAttributes.get(attributeName);
						list.add(attributeValue);
						protectedAttributes.put(attributeName,list);
					}
					else
					{
						ArrayList list = new ArrayList();
						list.add(attributeValue);
						protectedAttributes.put(attributeName,list);
					}
				}
			}
		}
		return protectedAttributes;
	}

}
