/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
