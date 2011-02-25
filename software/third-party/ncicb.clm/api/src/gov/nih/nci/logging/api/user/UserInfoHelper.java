package gov.nih.nci.logging.api.user;

import gov.nih.nci.logging.api.logger.util.ThreadVariable;

/**
 * <!-- LICENSE_TEXT_START -->
 * 
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author Ekagra Software Technologes Limited ('Ekagra')
 * 
 * This is a helper class for the userInfo and ThreadLocal variable
 * 
 */
public class UserInfoHelper
{
	public static void setUserInfo(String userName, String sessionID, String organization)
	{
		 UserInfo userInfo = (UserInfo)ThreadVariable.get();
		  if (null == userInfo)
		   userInfo = new UserInfo();
		  if (!(null == userName || userName.trim().length() == 0))
		   userInfo.setUsername(userName);
		  if (!(null == sessionID || sessionID.trim().length() == 0))
		  userInfo.setSessionID(sessionID);
		  if (!(null == organization || organization.trim().length() == 0))
			  userInfo.setOrganization(organization);
		  ThreadVariable.set(userInfo);
	}
	
	public static void setUserInfo(String userName, String sessionID)
	{
		 UserInfo userInfo = (UserInfo)ThreadVariable.get();
		  if (null == userInfo)
		   userInfo = new UserInfo();
		  if (!(null == userName || userName.trim().length() == 0))
		   userInfo.setUsername(userName);
		  if (!(null == sessionID || sessionID.trim().length() == 0))
		  userInfo.setSessionID(sessionID);
		  ThreadVariable.set(userInfo);
	}

	public static void setUserName( String userName)
	{
		 UserInfo userInfo = (UserInfo)ThreadVariable.get();
		  if (null == userInfo)
		   userInfo = new UserInfo();
		  if (!(null == userName || userName.trim().length() == 0)){
			  userInfo.setUsername(userName);
		  }
		  ThreadVariable.set(userInfo);
	}
	public static void setGroupNames( String[] groupNames)
	{
		 UserInfo userInfo = (UserInfo)ThreadVariable.get();
		  if (null == userInfo)
		   userInfo = new UserInfo();
		  
		  userInfo.setGroupNames(groupNames);
		  ThreadVariable.set(userInfo);
		  
	}
	public static void setSessionID( String sessionID)
	{
		 UserInfo userInfo = (UserInfo)ThreadVariable.get();
		  if (null == userInfo)
		   userInfo = new UserInfo();
		  if (!(null == sessionID || sessionID.trim().length() == 0)){
			  userInfo.setSessionID(sessionID);
		  }
		  ThreadVariable.set(userInfo);
	}
	public static void setOrganization( String organization)
	{
		 UserInfo userInfo = (UserInfo)ThreadVariable.get();
		  if (null == userInfo)
		   userInfo = new UserInfo();
		  if (!(null == organization || organization.trim().length() == 0)){
			  userInfo.setUsername(organization);
		  }
		  ThreadVariable.set(userInfo);
	}

	
	public static void setObjectStateChangeComment( String comment)
	{
		 UserInfo userInfo = (UserInfo)ThreadVariable.get();
		  if (null == userInfo)
		   userInfo = new UserInfo();
		  if (!(null == comment || comment.trim().length() == 0)){
			  userInfo.setComment(comment);
		  }
		  ThreadVariable.set(userInfo);
	}
	
	public static void setObjectID( String objectIDKey)
	{
		 UserInfo userInfo = (UserInfo)ThreadVariable.get();
		  if (null == userInfo)
		   userInfo = new UserInfo();
		  if (!(null == objectIDKey|| objectIDKey.trim().length() == 0)){
			  userInfo.setObjectIDKey(objectIDKey);
		  }
		  ThreadVariable.set(userInfo);
	}

}
