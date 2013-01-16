//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.logging.api.logger.util;

/**
 * <!-- LICENSE_TEXT_START -->
 * 
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author Ekagra Software Technologes Limited ('Ekagra')
 * 
 * This class manages a ThreadLocal variable
 * 
 */
public class ThreadVariable
{
	// The next serial number to be assigned

	private static ThreadLocal userInfo = new ThreadLocal();

	public static Object get()
	{
		return (Object) userInfo.get();
	}

	public static void set(Object user)
	{
		userInfo.set(user);
	}

}
