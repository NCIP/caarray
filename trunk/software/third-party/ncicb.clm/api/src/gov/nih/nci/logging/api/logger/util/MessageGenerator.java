package gov.nih.nci.logging.api.logger.util;

/**
 * <!-- LICENSE_TEXT_START -->
 * 
 * <!-- LICENSE_TEXT_END -->
 */

import gov.nih.nci.logging.api.util.StringUtils;

import java.io.*;

import org.hibernate.type.Type;

/**
 * This class generates the specifically formatted message for logging. 
 * 
 * @author Ekagra Software Technologes Limited ('Ekagra')
 * 
 */
public class MessageGenerator {

	/**
	 * This method generates the string message for update operation
	 * 
	 * @param obj --
	 *            Object to be audited
	 * @param id --
	 *            Serializable id of the object
	 * @param currentState --
	 *            current states of the object after the operation
	 * @param previousState --
	 *            previous states of the object before the operation
	 * @param propertyNames
	 *            --names of the object states
	 * @param types --
	 *            Hibernate types of the object states
	 * @param operation --
	 *            the name of the operation being performed
	 * @return Returns the string type of the message
	 */
	public static String generateStringMessage(Object obj, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types, String operation,
			String comment, String objectID) {

		String message = null;
		if (("update").equalsIgnoreCase(operation)) {
			StringBuffer messagePreviousState = null;
			StringBuffer messageCurrentState = null;
			try {

				messagePreviousState = new StringBuffer();
				messageCurrentState = new StringBuffer();
				messagePreviousState
						.append("&"
								+ ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION
								+ "=["
								+ ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION_NAME
								+ "=" + operation.toUpperCase() + ";"
								+ ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION_COMMENT 
								+ "=" + comment + "]");
				
				
				
				messagePreviousState
						.append("&"
								+ ApplicationConstants.OBJECT_STATE_MESSAGE_OBJECT
								+ "=["
								+ ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION_OBJECT_NAME
								+ "="
								+ obj.getClass().getName()
								+ ";"
								+ ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION_OBJECT_IDENTIFIER_ATTRIBUTE_VALUE
								+ "=" + StringUtils.initString(objectID) + "]");

				if (previousState != null) {
					messagePreviousState.append("&"
							+ ApplicationConstants.OBJECT_STATE_MESSAGE_PREVIOUS_ATTRIBUTES
							+ "=[");
				}
				//System.out.println("currentstate is "				+ obj.getClass().getName() + currentState);
				for (int index = 0; index < currentState.length; index++) {
					if (index == 0) {
						if (previousState != null) {
							messagePreviousState.append(String
									.valueOf(propertyNames[index])
									+ "="
									+ String.valueOf(previousState[index]));
						} else {
							//System.out.println("Prev State was null");
						}
						if (currentState != null) {
							messageCurrentState
									.append(String
											.valueOf(propertyNames[index])
											+ "="
											+ String
													.valueOf(currentState[index]));
						} else {
							//System.out.println("Curr State was null");
						}
					} else {
						if (previousState != null) {
							messagePreviousState.append(";"
									+ String.valueOf(propertyNames[index])
									+ "="
									+ String.valueOf(previousState[index]));
						} else {
							//System.out.println("Prev State was null");
						}
						if (currentState != null) {
							messageCurrentState
									.append(";"
											+ String
													.valueOf(propertyNames[index])
											+ "="
											+ String
													.valueOf(currentState[index]));
						} else {
							//System.out.println("Curr State was null");
						}
					}

				}
			} catch (SecurityException e) {
				System.out.println(e);
			}
			if (previousState != null) {
				messagePreviousState.append("]&"
						+ ApplicationConstants.OBJECT_STATE_MESSAGE_CURRENT_ATTRIBUTES
						+ "=[");
			} else
				messagePreviousState.append("&"
						+ ApplicationConstants.OBJECT_STATE_MESSAGE_CURRENT_ATTRIBUTES
						+ "=[");
			messagePreviousState.append(messageCurrentState.toString());
			message = messagePreviousState.toString();
		}
		// for other operations including save, delete
		else {
			StringBuffer messageState = null;

			try {
				
				
				
				messageState = new StringBuffer();
				
				messageState.append("&"
						+ ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION
						+ "=["
						+ ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION_NAME
						+ "=" + operation.toUpperCase() + ";"
						+ ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION_COMMENT 
						+ "=" + comment + "]");
				messageState.append("&"
						+ ApplicationConstants.OBJECT_STATE_MESSAGE_OBJECT
						+ "=["
						+ ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION_OBJECT_NAME
						+ "="
						+ obj.getClass().getName()
						+ ";"
						+ ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION_OBJECT_IDENTIFIER_ATTRIBUTE_VALUE
						+ "=" + StringUtils.initString(objectID) + "]");
				messageState.append("&"
						+ ApplicationConstants.OBJECT_STATE_MESSAGE_ATTRIBUTES
						+ "=[");
				
				/*messageState.append("&operation=[name="
						+ operation.toUpperCase() + ";" + "comment=" + comment
						+ "]");
				messageState.append("&object=[name=" + obj.getClass().getName()
						+ ";" + "ID=" + StringUtils.initString(objectID) + "]");
				messageState.append("&attributes=[");*/
				for (int index = 0; index < currentState.length; index++) {
					if (index == 0) {
						if (currentState != null) {
							messageState
									.append(String
											.valueOf(propertyNames[index])
											+ "="
											+ String
													.valueOf(currentState[index]));
						} else {
							//System.out.println("State was null");
						}
					} else {
						if (currentState != null) {
							messageState
									.append(";"
											+ String
													.valueOf(propertyNames[index])
											+ "="
											+ String
													.valueOf(currentState[index]));
						} else {
							//System.out.println("State was null");
						}
					}
				}
				messageState.append("]");
			} catch (SecurityException e) {
				System.out.println(e);
			}
			message = messageState.toString();

		}
		return message;
	}

	/**
	 * 
	 * This method does the actual work of generating a xml file
	 * 
	 * @param entityName
	 * @param id
	 * @param State
	 * @param propertyNames
	 * @param types
	 * @param operation
	 * @param out
	 * @throws IOException
	 */
	public static void convert(String entityName, Serializable id,
			Object[] State, String[] propertyNames, Type[] types,
			String operation, String comment, String objectID, OutputStream out)
			throws IOException {
		// TODO operation, Object ID, OBjectName.
		Writer wout = new OutputStreamWriter(out, "UTF8");
		wout.write("<?xml version=\"1.0\"?>\r\n");
		wout.write("<" + entityName + ">\r\n");
		wout.write("<operation>\r\n");
		wout.write(operation);
		wout.write("</operation>\r\n");
		for (int index = 0; index < State.length; index++) {
			wout.write("  <" + propertyNames[index] + ">\r\n");
			wout.write(State[index].toString());
			wout.write("</" + propertyNames[index] + ">\r\n");
		}
		wout.write("</" + entityName + ">\r\n");
		wout.flush();
	}

	/**
	 * This method generates xml file for update operation
	 * 
	 * @param obj --
	 *            Object to be audited
	 * @param id --
	 *            Serializable id of the object
	 * @param currentState --
	 *            current states of the object after the operation
	 * @param previousState --
	 *            previous states of the object before the operation
	 * @param propertyNames
	 *            --names of the object states
	 * @param types --
	 *            Hibernate types of the object states
	 * @param operation --
	 *            the name of the operation being performed
	 * 
	 */

	public static void generateXMLMessage(Object obj, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types, String operation,
			String comment, String objectID, String outfileName) {
		String entityName = obj.getClass().getName();
		OutputStream out = null;

		try {
			out = new FileOutputStream(outfileName);
			if (operation.equalsIgnoreCase("update")) {
				convert(entityName, id, currentState, previousState,
						propertyNames, types, operation, comment, objectID, out);
			} else
				convert(entityName, id, currentState, propertyNames, types,
						operation, comment, objectID, out);
		} catch (IOException e) {
			System.err.println(e);
		}

	}

	/**
	 * This method does the actual work of generating xml file for update
	 * operation
	 * 
	 * @param entityName -
	 *            object name to be audited
	 * @param id --
	 *            serializable id of the object
	 * @param currentState --
	 *            current state after the operation
	 * @param previousState --
	 *            previous state before the operation
	 * @param propertyNames -
	 *            string values of the states names
	 * @param types -
	 *            hibernate types of the states
	 * @param operation -
	 *            operation being performed
	 * @param out --
	 *            xml file name
	 * @throws IOException
	 */
	public static void convert(String entityName, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types, String operation,
			String comment, String objectID, OutputStream out)
			throws IOException {

		// TODO operation, Object ID, OBjectName.
		Writer wout = new OutputStreamWriter(out, "UTF8");
		wout.write("<?xml version=\"1.0\"?>\r\n");
		wout.write("<" + entityName + ">\r\n");
		wout.write("<operation>\r\n");
		wout.write(operation);
		wout.write("</operation>\r\n");

		for (int index = 0; index < currentState.length; index++) {
			wout.write("<" + propertyNames[index] + ">\r\n");
			if (previousState != null) {
				wout.write("<previousState>\r\n");
				wout.write(String.valueOf(previousState[index]));
				wout.write("</previousState>\r\n");
			}
			wout.write("<currentState>\r\n");
			wout.write(currentState[index].toString());
			wout.write("</currentState>\r\n");
			wout.write("</" + propertyNames[index] + ">\r\n");
		}
		wout.write("</" + entityName + ">\r\n");
		wout.flush();
	}

	/**
	 * This method cleans the string
	 * 
	 * @param s
	 * @return - returns a clean string
	 */
	public static String escapeText(String s) {

		if (s.indexOf('&') != -1 || s.indexOf('<') != -1
				|| s.indexOf('>') != -1) {
			StringBuffer result = new StringBuffer(s.length() + 4);
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c == '&')
					result.append("&amp;");
				else if (c == '<')
					result.append("&lt;");
				else if (c == '>')
					result.append("&gt;");
				else
					result.append(c);
			}
			return result.toString();
		} else {
			return s;
		}

	}

}
