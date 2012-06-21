
package gov.nih.nci.logging.webapp.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

/** 
 */
public class LoginForm extends ValidatorForm {

	/** loginID property */
	private String loginID;


	/** password property */
	private String password;

	
	/** application property */
	private String application;



	

	/** 
	 * Returns the password.
	 * @return String
	 */
	public String getPassword() {
		return password;
	}

	/** 
	 * Set the password.
	 * @param password The password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	

	/** 
	 * Returns the application.
	 * @return String
	 */
	public String getApplication() {
		return application;
	}

	/** 
	 * Set the application.
	 * @param application The application to set
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	
	/**
	 * @return Returns the loginID.
	 */
	public String getLoginID()
	{
		return loginID;
	}

	
	/**
	 * @param loginID The loginID to set.
	 */
	public void setLoginID(String loginID)
	{
		this.loginID = loginID;
	}

}

