/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-ejb-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-ejb-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-ejb-jar Software and any
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
package gov.nih.nci.caarray.application.permissions;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Test cases for service.
 */
@SuppressWarnings("PMD")
public class PermissionsManagementServiceIntegrationTest extends AbstractServiceIntegrationTest {
    private static final String TEST = "test";
    private PermissionsManagementService permissionsManagementService;
    private final GenericDataServiceStub genericDataServiceStub = new GenericDataServiceStub();

    @Before
    public void setup() {
        this.permissionsManagementService = createPermissionsManagementService(this.genericDataServiceStub);
    }

    private PermissionsManagementService createPermissionsManagementService(GenericDataService genericDataServiceStub) {
        final CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;
        final PermissionsManagementServiceBean bean = new PermissionsManagementServiceBean();
        bean.setHibernateHelper(this.hibernateHelper);
        bean.setCollaboratorGroupDao(daoFactory.getCollaboratorGroupDao());
        bean.setSearchDao(daoFactory.getSearchDao());

        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, genericDataServiceStub);
        final MysqlDataSource ds = new MysqlDataSource();
        final Configuration config = this.hibernateHelper.getConfiguration();
        ds.setUrl(config.getProperty("hibernate.connection.url"));
        ds.setUser(config.getProperty("hibernate.connection.username"));
        ds.setPassword(config.getProperty("hibernate.connection.password"));
        locatorStub.addLookup("java:jdbc/CaArrayDataSource", ds);
        bean.setGenericDataService(genericDataServiceStub);

        return bean;
    }

    @Test
    public void testChangeOwner() throws Exception {
        CaArrayUsernameHolder.setUser(STANDARD_USER);
        Transaction tx = this.hibernateHelper.beginTransaction();
        final CollaboratorGroup created = this.permissionsManagementService.create(TEST);
        tx.commit();
        assertEquals(STANDARD_USER, created.getOwner().getLoginName());
        tx = this.hibernateHelper.beginTransaction();
        List<CollaboratorGroup> groups =
                this.permissionsManagementService.getCollaboratorGroupsForOwner(created.getOwner().getUserId());
        tx.commit();
        assertEquals(1, groups.size());

        CaArrayUsernameHolder.setUser("systemadministrator");
        final User caarrayuser = SecurityUtils.getAuthorizationManager().getUser("caarrayuser");
        tx = this.hibernateHelper.beginTransaction();
        groups = this.permissionsManagementService.getCollaboratorGroupsForOwner(caarrayuser.getUserId());
        assertEquals(0, groups.size());
        this.permissionsManagementService.changeOwner(created.getId(), "caarrayuser");
        tx.commit();
        tx = this.hibernateHelper.beginTransaction();
        groups = this.permissionsManagementService.getCollaboratorGroupsForOwner(caarrayuser.getUserId());
        tx.commit();
        assertEquals(1, groups.size());
    }
}
