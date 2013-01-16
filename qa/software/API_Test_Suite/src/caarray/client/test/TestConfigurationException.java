//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test;

/**
 * @author vaughng
 * Jun 26, 2009
 */
public class TestConfigurationException extends Exception {

	/**
	 * 
	 */
	public TestConfigurationException() {}

	/**
	 * @param arg0
	 */
	public TestConfigurationException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public TestConfigurationException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public TestConfigurationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
