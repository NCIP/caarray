//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.AbstractCaarrayTest;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * @author Winston Cheng
 *
 */
public class ClassBasedMigrationStepTest extends AbstractCaarrayTest {
    private DocumentBuilder db;

    @Before
    public void setUp() {
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (Exception asdf) {
            fail("Could not get document builder");
        }
    }

    @Test
    public void testExecute() {
        try {
            Element el = createElement("<migrator-class>classNotFound</migrator-class>");
            ClassBasedMigrationStep step = new ClassBasedMigrationStep(el);
            step.execute();
            fail("Should throw ClassNotFoundException");
        } catch (MigrationStepFailedException msfe) {
            assertEquals(ClassNotFoundException.class, msfe.getCause().getClass());
        }

        try {
            Element el = createElement("<migrator-class>gov.nih.nci.caarray.web.upgrade.Migration</migrator-class>");
            ClassBasedMigrationStep step = new ClassBasedMigrationStep(el);
            step.execute();
            fail("Should throw InstantiationException");
        } catch (MigrationStepFailedException msfe) {
            assertEquals(InstantiationException.class, msfe.getCause().getClass());
        }

        try {
            Element el = createElement("<migrator-class>gov.nih.nci.caarray.web.upgrade.UpgradeManager</migrator-class>");
            ClassBasedMigrationStep step = new ClassBasedMigrationStep(el);
            step.execute();
            fail("Should throw IllegalAccessException");
        } catch (MigrationStepFailedException msfe) {
            assertEquals(IllegalAccessException.class, msfe.getCause().getClass());
        }

        try {
            Element el = createElement("<migrator-class>gov.nih.nci.caarray.web.upgrade.ApplicationUpgradeListener</migrator-class>");
            ClassBasedMigrationStep step = new ClassBasedMigrationStep(el);
            step.execute();
            fail("Should throw MigrationStepFailedException");
        } catch (MigrationStepFailedException msfe) {
            // expected exception
        }

        try {
            Element el = createElement("<migrator-class>gov.nih.nci.caarray.web.upgrade.TestMigrator</migrator-class>");
            ClassBasedMigrationStep step = new ClassBasedMigrationStep(el);
            TestMigrator.setMigrated(false);
            step.execute();
            assertTrue(TestMigrator.isMigrated());
        } catch (MigrationStepFailedException msfe) {
            fail(msfe.toString());
        }

    }

    private Element createElement(String xml) {
        try {
            Document dom = db.parse(new InputSource(new StringReader(xml)));
            return (Element)dom.getFirstChild();
        } catch (Exception e) {
            return null;
        }
    }
}
