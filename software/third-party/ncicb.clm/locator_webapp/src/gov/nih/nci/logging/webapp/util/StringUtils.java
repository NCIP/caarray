//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.logging.webapp.util;

public class StringUtils {

	
	

	/**
	 * returns true of the string is blank or null
	 * @param str
	 * @return
	 */
	public static boolean isBlankOrNull(final String str) {
		boolean test = false;
		if (str == null) {
			test = true;
		} else {
			if (str.equals("")) {
				test = true;
			}
		}
		return test;
	}

	/**
	 * initializes a string. 
	 * @param str
	 * @return
	 */
	public static String initString(final String str) {
		String test = "";
		if (str != null) {
			test = str.trim();
		}
		return test;
	}

}
