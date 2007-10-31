package com.example.tests;

import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;

public class NewTest extends SeleneseTestCase {
	public void testNew() throws Exception {
		selenium.open("/caarray/protected/Project_list.action");
		selenium.type("j_username", "caarrayadmin");
		selenium.type("j_password", "f1rebird05");
		selenium.click("login");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("Experiment Workspace"));
	}
}
