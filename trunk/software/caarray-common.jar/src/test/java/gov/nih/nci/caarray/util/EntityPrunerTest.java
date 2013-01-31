//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Test cases for the entity pruner
 */
public class EntityPrunerTest extends AbstractCaarrayTest {
    @Test
    public void testNullSetter() {
        User u = new User();
        u.setFirstName("");
        u.setLastName(" \t");
        u.setOrganization(" test ");
        CaArrayUtils.blankStringPropsToNull(u);
        assertNull(u.getFirstName());
        assertNull(u.getLastName());
        assertNotNull(u.getOrganization());
    }

    @Test
    public void testMakeLeaf() {
        EntityPruner pruner = new EntityPruner();
        HibernateUtil.beginTransaction();
        B b = new B();
        pruner.makeLeaf(b);

        assertEquals(1L, b.getId().longValue());
        assertNull(b.getA());
        assertNull(b.getOther());
        assertTrue(b.getOtherList().isEmpty());
        assertTrue(b.getOtherSet().isEmpty());
        assertTrue(b.getOtherMap().entrySet().isEmpty());
        assertTrue(b.getOtherCollection().isEmpty());
        assertNotNull(b.getFoo());
        assertNotNull(b.getUser());
        assertNull(b.getUnserializable());
        assertEquals(3, b.getI());
        assertTrue(b.getSortedSet().isEmpty());
    }

    @Test
    public void testMakeChildrenLeaves() {
        EntityPruner pruner = new EntityPruner();
        HibernateUtil.beginTransaction();
        B b = new B();
        pruner.makeChildrenLeaves(b);
        assertTrue(b.fooAccessed || b.iAccessed);

        assertNotNull(b.getUser());
        assertNull(b.getUnserializable());
        assertEquals(1L, b.getId().longValue());
        assertNotNull(b.getA());
        assertNotNull(b.getOther());
        assertFalse(b.getOtherList().isEmpty());
        assertFalse(b.getOtherSet().isEmpty());
        assertFalse(b.getOtherMap().entrySet().isEmpty());
        assertFalse(b.getOtherCollection().isEmpty());

        assertEquals(1L, b.getA().getId().longValue());
        assertNull(b.getA().getA());
        assertNull(b.getOther().getA());
        assertNull(b.getOtherList().iterator().next().getA());
        assertNull(b.getOtherSet().iterator().next().getA());
        assertNull(b.getOtherCollection().iterator().next().getA());
        assertFalse(b.getUsers().isEmpty());
        assertTrue(b.getUnserializables().isEmpty());
    }

    @Test
    public void testMap() {
        EntityPruner pruner = new EntityPruner();
        C c = new C();
        pruner.makeChildrenLeaves(c);
        assertNotNull(c.getMapA());
        A a = c.getMapA().get(c.getMapA().keySet().iterator().next());
        assertNull(a.getA());
    }

    public static class A implements PersistentObject, Comparable<A> {
        private static final long serialVersionUID = 1L;
        private Long id = 1L;
        private A aToo;

        public A() {
            this(true);
        }

        private A(boolean recurse) {
            if (recurse) {
                aToo = new A(false);
            }
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public A getA() {
            return aToo;
        }

        public void setA(A a) {
            this.aToo = a;
        }

        /**
         * {@inheritDoc}
         */
        public int compareTo(A o) {
            return 0;
        }

    }

    public static class B extends A {
        private static final long serialVersionUID = 1L;
        private A other;
        private List<A> otherList = new ArrayList<A>();
        private Set<A> otherSet = new HashSet<A>();
        private Map<Integer, A> otherMap = new HashMap<Integer, A>();
        private Collection<A> otherCollection = new HashSet<A>();
        private String foo = "foo";
        private boolean fooAccessed = false;
        private User user = new User();
        private int i = 3;
        private boolean iAccessed = false;
        private Set<User> users = new HashSet<User>();
        private SortedSet<A> sortedSet = new TreeSet<A>();
        private D unserializable;
        private Set<D> unserializables = new HashSet<D>();


        public Set<User> getUsers() {
            return users;
        }

        public void setUsers(Set<User> users) {
            this.users = users;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        private String getFoo() {
            fooAccessed = true;
            return foo;
        }

        @SuppressWarnings("unused")
        private void setFoo(String foo) {
            this.foo = foo;
        }

        public B() {
            other = new A();
            otherList.add(new A());
            otherSet.add(new A());
            otherMap.put(1, new A());
            otherCollection.add(new A());
            users.add(new User());
            sortedSet.add(new A());
            unserializable = new D();
            unserializables.add(new D());
        }

        public A getOther() {
            return other;
        }

        public void setOther(A other) {
            this.other = other;
        }

        public List<A> getOtherList() {
            return otherList;
        }

        public void setOtherList(List<A> otherList) {
            this.otherList = otherList;
        }

        public Set<A> getOtherSet() {
            return otherSet;
        }

        public void setOtherSet(Set<A> otherSet) {
            this.otherSet = otherSet;
        }

        public Map<Integer, A> getOtherMap() {
            return otherMap;
        }

        public void setOtherMap(Map<Integer, A> otherMap) {
            this.otherMap = otherMap;
        }

        public Collection<A> getOtherCollection() {
            return otherCollection;
        }

        @SuppressWarnings("unused")
        private void setOtherCollection(Collection<A> otherCollection) {
            // private on purpose, to test setAccessable
            this.otherCollection = otherCollection;
        }

        public int getI() {
            iAccessed = true;
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public SortedSet<A> getSortedSet() {
            return sortedSet;
        }

        public void setSortedSet(SortedSet<A> sortedSet) {
            this.sortedSet = sortedSet;
        }

        public D getUnserializable() {
            return unserializable;
        }

        public void setUnserializable(D unserializable) {
            this.unserializable = unserializable;
        }

        public Set<D> getUnserializables() {
            return unserializables;
        }

        public void setUnserializables(Set<D> unserializables) {
            this.unserializables = unserializables;
        }

    }

    public static class C implements PersistentObject {
        private static final long serialVersionUID = 1059753925701216036L;
        private Map<Long, A> mapA = new HashMap<Long, A>();

        public C() {
            mapA.put(1L, new A());
        }
        public Long getId() {
            return 1L;
        }

        public Map<Long, A> getMapA() {
            return mapA;
        }

        public void setMapA(Map<Long, A> mapA) {
            this.mapA = mapA;
        }
    }

    // a non-serializable class
    public static class D {
        private String foo = "bar";

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }
}
