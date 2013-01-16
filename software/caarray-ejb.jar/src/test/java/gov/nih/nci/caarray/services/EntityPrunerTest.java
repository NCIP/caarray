//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.caarray.util.CaArrayUtils;
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

import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Test cases for the entity pruner
 */
public class EntityPrunerTest extends AbstractCaarrayTest {
    private Injector injector;
    private CaArrayHibernateHelper hibernateHelper;
    private FileAccessServiceStub fasStub;

    /**
     * post-construct lifecycle method; intializes the Guice injector that will provide dependencies.
     */
    @Before
    public void init() {
        this.fasStub = new FileAccessServiceStub();
        this.injector = createInjector();
        this.hibernateHelper = this.injector.getInstance(CaArrayHibernateHelper.class);
    }

    /**
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected Injector createInjector() {
        return Guice.createInjector(new CaArrayHibernateHelperModule(), new AbstractModule() {
            @Override
            protected void configure() {
                bind(DataStorageFacade.class).toInstance(EntityPrunerTest.this.fasStub.createStorageFacade());
                requestStaticInjection(gov.nih.nci.caarray.services.EntityPruner.class);
            }
        });
    }

    @Test
    public void testNullSetter() {
        final User u = new User();
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
        final EntityPruner pruner = new EntityPruner();
        this.hibernateHelper.beginTransaction();
        final B b = new B();
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
        final EntityPruner pruner = new EntityPruner();
        this.hibernateHelper.beginTransaction();
        final B b = new B();
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
        final EntityPruner pruner = new EntityPruner();
        final C c = new C();
        pruner.makeChildrenLeaves(c);
        assertNotNull(c.getMapA());
        final A a = c.getMapA().get(c.getMapA().keySet().iterator().next());
        assertNull(a.getA());
    }

    @Test
    public void testDataColumnPruning() {
        IntegerColumn col = new IntegerColumn();
        col.initializeArray(10);
        assertEquals(10, col.getValues().length);
        final EntityPruner pruner = new EntityPruner();
        pruner.makeChildrenLeaves(col);
        assertEquals("need data", 10, col.getValues().length);

        col = new IntegerColumn();
        col.initializeArray(10);
        assertEquals(10, col.getValues().length);
        final HybridizationData data = new HybridizationData();
        data.getColumns().add(col);
        pruner.makeChildrenLeaves(data);
        assertEquals("should be pruned", 0, col.getValues().length);
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
                this.aToo = new A(false);
            }
        }

        @Override
        public Long getId() {
            return this.id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public A getA() {
            return this.aToo;
        }

        public void setA(A a) {
            this.aToo = a;
        }

        /**
         * {@inheritDoc}
         */
        @Override
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
            return this.users;
        }

        public void setUsers(Set<User> users) {
            this.users = users;
        }

        public User getUser() {
            return this.user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        private String getFoo() {
            this.fooAccessed = true;
            return this.foo;
        }

        @SuppressWarnings("unused")
        private void setFoo(String foo) {
            this.foo = foo;
        }

        public B() {
            this.other = new A();
            this.otherList.add(new A());
            this.otherSet.add(new A());
            this.otherMap.put(1, new A());
            this.otherCollection.add(new A());
            this.users.add(new User());
            this.sortedSet.add(new A());
            this.unserializable = new D();
            this.unserializables.add(new D());
        }

        public A getOther() {
            return this.other;
        }

        public void setOther(A other) {
            this.other = other;
        }

        public List<A> getOtherList() {
            return this.otherList;
        }

        public void setOtherList(List<A> otherList) {
            this.otherList = otherList;
        }

        public Set<A> getOtherSet() {
            return this.otherSet;
        }

        public void setOtherSet(Set<A> otherSet) {
            this.otherSet = otherSet;
        }

        public Map<Integer, A> getOtherMap() {
            return this.otherMap;
        }

        public void setOtherMap(Map<Integer, A> otherMap) {
            this.otherMap = otherMap;
        }

        public Collection<A> getOtherCollection() {
            return this.otherCollection;
        }

        @SuppressWarnings("unused")
        private void setOtherCollection(Collection<A> otherCollection) {
            // private on purpose, to test setAccessable
            this.otherCollection = otherCollection;
        }

        public int getI() {
            this.iAccessed = true;
            return this.i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public SortedSet<A> getSortedSet() {
            return this.sortedSet;
        }

        public void setSortedSet(SortedSet<A> sortedSet) {
            this.sortedSet = sortedSet;
        }

        public D getUnserializable() {
            return this.unserializable;
        }

        public void setUnserializable(D unserializable) {
            this.unserializable = unserializable;
        }

        public Set<D> getUnserializables() {
            return this.unserializables;
        }

        public void setUnserializables(Set<D> unserializables) {
            this.unserializables = unserializables;
        }

    }

    public static class C implements PersistentObject {
        private static final long serialVersionUID = 1059753925701216036L;
        private Map<Long, A> mapA = new HashMap<Long, A>();

        public C() {
            this.mapA.put(1L, new A());
        }

        @Override
        public Long getId() {
            return 1L;
        }

        public Map<Long, A> getMapA() {
            return this.mapA;
        }

        public void setMapA(Map<Long, A> mapA) {
            this.mapA = mapA;
        }
    }

    // a non-serializable class
    public static class D {
        private String foo = "bar";

        public String getFoo() {
            return this.foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }
}
