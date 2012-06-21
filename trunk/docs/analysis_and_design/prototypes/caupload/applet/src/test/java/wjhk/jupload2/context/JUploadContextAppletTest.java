//
// $Id$
//
// jupload - A file upload applet.
//
// Copyright 2010 The JUpload Team
//
// Created: 2 avr. 2010
// Creator: etienne_sf
// Last modified: $Date$
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

package wjhk.jupload2.context;

import java.net.URL;

import javax.swing.JApplet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import wjhk.jupload2.JUploadApplet;
import wjhk.jupload2.policies.UploadPolicy;
import wjhk.jupload2.policies.UploadPolicyFactory;
import wjhk.jupload2.testhelpers.UploadPolicyTestHelper;
import wjhk.jupload2.upload.AbstractJUploadTestHelper;

/** */
@SuppressWarnings("serial")
class JUploadAppletTestHelper extends JUploadApplet {
	URL documentBase = null;

	@Override
	public String getParameter(String prop) {
		if (prop.equals(UploadPolicy.PROP_UPLOAD_POLICY)) {
			return UploadPolicyTestHelper.class.getName();
		} else {
			// For test cases, we use the System properties, as we don't have
			// any applet parameter.
			return System.getProperty(prop);
		}
	}

	/** Simulate calls to getDocumentBase() */
	@Override
	public URL getDocumentBase() {
		return this.documentBase;
	}
}

/**
 * This class is just used to catch the getUploadPolicy method. This is
 * necessary (for JUnit tests) to prevent UploadPolicyFactory to create a new
 * UploadPolicy.
 */
class JUploadContextAppletTestHelper extends JUploadContextApplet {

	static UploadPolicy uploadPolicyTestCase = null;

	public JUploadContextAppletTestHelper(JApplet theApplet,
			UploadPolicy uploadPolicy) {
		super(theApplet);
		this.uploadPolicy = uploadPolicy;
	}

	/**
	 * Retrieves the current upload policy used in the unit testing. Override
	 * the default method, which would call the
	 * {@link UploadPolicyFactory#getUploadPolicy(JUploadContext)} method.
	 * 
	 * @return the current upload policy of the running test.
	 */
	@Override
	public UploadPolicy getUploadPolicy() {
		if (this.uploadPolicy == null) {
			this.uploadPolicy = uploadPolicyTestCase;
		}
		return this.uploadPolicy;
	}

	/** Does nothing, avoir real 'sign' test */
	@Override
	void checkAppletIsSigned() {
		// Nothing. Just to avoid the original call to checkAppletIsSigned(),
		// which would throw an error.
	}
}

/**
 * @author etienne_sf
 */
public class JUploadContextAppletTest extends AbstractJUploadTestHelper {

	JUploadAppletTestHelper applet;

	JUploadContextApplet juploadContextApplet;

	String paramName = "A non existing parameter";

	/** */
	@Before
	public void setUp() {
		JUploadContextAppletTestHelper.uploadPolicyTestCase = this.uploadPolicy;
		this.applet = new JUploadAppletTestHelper();
		this.juploadContextApplet = new JUploadContextAppletTestHelper(
				this.applet, this.uploadPolicy);
	}

	/** */
	@Test
	public void testInit() {
		Assert.assertNotNull("Check the applet", this.applet);
		Assert.assertEquals("Check the applet getter", this.applet,
				this.juploadContextApplet.theApplet);
		Assert.assertNull("Check the rootPane: the applet",
				this.juploadContextApplet.frame);
	}

	/** */
	@Test
	public void testGetParameter_String() {
		String defaultParamValue = "A test default value";

		System.clearProperty(this.paramName);
		Assert.assertEquals("A dummy parameter name: should use default",
				defaultParamValue, this.juploadContextApplet.getParameter(
						this.paramName, defaultParamValue));

		String paramValue = "A real value";
		System.setProperty(this.paramName, paramValue);
		Assert.assertEquals("A dummy parameter name: should use given value",
				paramValue, this.juploadContextApplet.getParameter(
						this.paramName, defaultParamValue));
	}

	/** */
	@Test
	public void testGetParameter_Int() {
		int defaultParamValue = -23465;

		System.clearProperty(this.paramName);
		Assert.assertEquals("A dummy parameter name: should use default",
				defaultParamValue, this.juploadContextApplet.getParameter(
						this.paramName, defaultParamValue));

		int paramValue = -7342;
		System.setProperty(this.paramName, Integer.toString(paramValue));
		Assert.assertEquals("A dummy parameter name: should use given value",
				paramValue, this.juploadContextApplet.getParameter(
						this.paramName, defaultParamValue));
	}

	/** */
	@Test
	public void testGetParameter_Float() {
		float defaultParamValue = (float) -23465.2143;

		System.clearProperty(this.paramName);
		Assert.assertEquals("A dummy parameter name: should use default",
				defaultParamValue, this.juploadContextApplet.getParameter(
						this.paramName, defaultParamValue), 0);

		float paramValue = (float) -7342.532;
		System.setProperty(this.paramName, Float.toString(paramValue));
		Assert.assertEquals("A dummy parameter name: should use given value",
				paramValue, this.juploadContextApplet.getParameter(
						this.paramName, defaultParamValue), 0);
	}

	/** */
	@Test
	public void testGetParameter_Long() {
		long defaultParamValue = -23465;

		System.clearProperty(this.paramName);
		Assert.assertEquals("A dummy parameter name: should use default",
				defaultParamValue, this.juploadContextApplet.getParameter(
						this.paramName, defaultParamValue));

		long paramValue = -7342;
		System.setProperty(this.paramName, Long.toString(paramValue));
		Assert.assertEquals("A dummy parameter name: should use given value",
				paramValue, this.juploadContextApplet.getParameter(
						this.paramName, defaultParamValue));
	}

	/** */
	@Test
	public void testGetParameter_Boolean() {
		boolean defaultParamValue = false;

		System.clearProperty(this.paramName);
		Assert.assertEquals("A dummy parameter name: should use default",
				defaultParamValue, this.juploadContextApplet.getParameter(
						this.paramName, defaultParamValue));
		defaultParamValue = true;
		Assert.assertEquals("A dummy parameter name: should use default",
				defaultParamValue, this.juploadContextApplet.getParameter(
						this.paramName, defaultParamValue));

		System.setProperty(this.paramName, "true");
		Assert.assertTrue("A dummy parameter name: should use given value",
				this.juploadContextApplet.getParameter(this.paramName,
						defaultParamValue));
	}

	/** */
	@Test
	public void testJsString() {
		Assert.assertEquals("jsString()", "\\$\\'\\'\\n",
				this.juploadContextApplet.jsString("$\"'\r\n"));
	}
}
