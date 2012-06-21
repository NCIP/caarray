package gov.nih.nci.logging.api.domain;

import java.io.Serializable;

/**
 * ObjectAttribute is used in case of Object State logs to represent the previous
 * and current values for a particular attribute of the object.
 *   
 * <br><br>Example: 
 * 		Object 'Customer' has attributes firstName, lastName, street, zip.  
 * 		<br>ObjectAttribute objectAttribute = new ObjectAttribute(); 
 * 		<br>objectAttribute.setAttributeName("street");
 * 		<br> objectAttribute.setPreviousValue("SomeStreet St.");
 * 		<br>objectAttribute.setPreviousValue("SomeOtherStreet St.");
 * 
 * 
 * @version 1.0
 * 
 */
public class ObjectAttribute implements Serializable {
	/**
	 * Primary key of ObjectAttribute object. 
	 * 
	 */
	private Long id;
	/**
	 * The name  of the Object's attribute whose values are being logged. The
	 * attributeName  is populated by the  ObjectStateInterceptor.
	 */
	private String attributeName;
	/**
	 * This value would be present only on case of update operations and stores the
	 * value of the attribute before the update operation.
	 */
	private String previousValue;
	/**
	 * The current value of the attribute. This value would be present when a new
	 * object is created or existing object is deleted. In case of update operation it
	 * store the value of the attribute after the update.
	 */
	private String currentValue;
	
	
	/**
	 * @return Returns the attributeName.
	 */
	public String getAttributeName() {
		return attributeName;
	}
	/**
	 * @param attributeName The attributeName to set.
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	/**
	 * @return Returns the currentValue.
	 */
	public String getCurrentValue() {
		return currentValue;
	}
	/**
	 * @param currentValue The currentValue to set.
	 */
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
	/**
	 * @return Returns the id.
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return Returns the previousValue.
	 */
	public String getPreviousValue() {
		return previousValue;
	}
	/**
	 * @param previousValue The previousValue to set.
	 */
	public void setPreviousValue(String previousValue) {
		this.previousValue = previousValue;
	}




}
