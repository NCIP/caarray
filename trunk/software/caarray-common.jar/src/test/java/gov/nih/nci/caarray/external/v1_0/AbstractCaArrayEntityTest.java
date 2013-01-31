//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.external.v1_0;

import gov.nih.nci.caarray.external.v1_0.data.FileMetadata;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gax
 */
public class AbstractCaArrayEntityTest {

    private static final List<Class<? extends AbstractCaArrayEntity>> subclasses = new ArrayList<Class<? extends AbstractCaArrayEntity>>();

    public static <T> List<Class<? extends T>> findLocalSubclasses(Class<T> c) throws ClassNotFoundException, URISyntaxException {
        List<Class<? extends T>> l = new ArrayList<Class<? extends T>>();
        File dir = new File(c.getProtectionDomain().getCodeSource().getLocation().toURI());
        Iterator<File> fi = FileUtils.iterateFiles(dir, new String[]{"class"}, true);
        int prefix = dir.getAbsolutePath().length() + 1;
        int suffix = ".class".length();
        while (fi.hasNext()) {
            File cf = fi.next();
                String fn = cf.getAbsolutePath();
                try{
                String cn = fn.substring(prefix, fn.length() - suffix);
                if (cn.endsWith("<error>")) {
                    continue;
                }
                cn = cn.replace('/', '.');
                System.out.println("cn = "+cn);
                Class tmp = c.getClassLoader().loadClass(cn);
                if ((tmp.getModifiers() & Modifier.ABSTRACT ) != 0) {
                    continue;
                }
                if (c.isAssignableFrom(tmp)) {
                    l.add(tmp);
                    System.out.println("added "+cf.getAbsolutePath()+" as "+cn);
                }
            }catch(Exception e) {
                System.err.println(fn);
                e.printStackTrace();
            }
        }
        return l;
    }

    @BeforeClass
    public static void init() throws URISyntaxException, ClassNotFoundException {
        Class c = AbstractCaArrayEntity.class;
        List<Class<? extends AbstractCaArrayEntity>> l = findLocalSubclasses(c);
        subclasses.addAll(l);
    }
    
    /**
     * Test of equals method, of class AbstractCaArrayEntity.
     */
    @Test
    public void testEqualsHashCode() throws Exception {
        for (Class<? extends AbstractCaArrayEntity> c : subclasses) {
            System.out.println("equals test for "+c);
            AbstractCaArrayEntity a = c.newInstance();
            AbstractCaArrayEntity b = c.newInstance();

            // to same
            assertTrue(c.getName(), a.equals(a));
            assertEquals(a.hashCode(), a.hashCode());

            // both with no id, no props
            assertTrue(c.getName(), a.equals(b));
            assertTrue(c.getName(), b.equals(a));
            assertEquals(a.hashCode(), b.hashCode());

            // one has id
            a.setId("foo");
            assertFalse(c.getName(), a.equals(b));
            assertFalse(c.getName(), b.equals(a));
            assertFalse(a.hashCode() ==  b.hashCode());

            // other has diffrent id
            b.setId("bar");
            assertFalse(c.getName(), a.equals(b));
            assertFalse(c.getName(), b.equals(a));
            assertFalse(a.hashCode() ==  b.hashCode());

            // same id
            b.setId(a.getId());
            assertTrue(c.getName(), a.equals(b));
            assertTrue(c.getName(), b.equals(a));
            assertTrue(a.hashCode() ==  b.hashCode());

            // both with same id, one has some property
            setSomeProperty(a);
            assertTrue(c.getName(), a.equals(b));
            assertTrue(c.getName(), b.equals(a));
            assertTrue(a.hashCode() ==  b.hashCode());

            // diffrent prop, on has no id.
            a.setId(null);
            assertFalse(c.getName(), a.equals(b));
            assertFalse(c.getName(), b.equals(a));
            assertFalse(a.hashCode() ==  b.hashCode());

            // diffrent props, no ids.
            b.setId(null);
            assertFalse(c.getName(), a.equals(b));
            assertFalse(c.getName(), b.equals(a));
            assertFalse(a.hashCode() ==  b.hashCode());

            // same props, no ids.
            setSomeProperty(b);
            assertTrue(c.getName(), a.equals(b));
            assertTrue(c.getName(), b.equals(a));
            assertTrue(a.hashCode() ==  b.hashCode());
        }
    }

    private void setSomeProperty(AbstractCaArrayEntity a) throws Exception {
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(a);
        for (PropertyDescriptor p : pds) {
            if (p.getName().equals("id")) continue;
            if (p.getPropertyType() == String.class) {
                p.getWriteMethod().invoke(a, "some String");
                return;
            }
            
            if (p.getPropertyType() == Person.class) {
                p.getWriteMethod().invoke(a, new Person());
                return;
            }

            if (p.getPropertyType() == FileMetadata.class) {
                p.getWriteMethod().invoke(a, new FileMetadata());
                return;
            }

        }

        throw new UnsupportedOperationException("did know how to set a property on " + a.getClass());
    }
}
