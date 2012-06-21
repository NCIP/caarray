/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
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
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceStub;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.AbstractBaseStrutsTest;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 *
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ProjectOverviewActionTest extends AbstractBaseStrutsTest {
    private final ProjectOverviewAction action = new ProjectOverviewAction();
    private static final LocalArrayDesignServiceStub arrayDesignServiceStub = new LocalArrayDesignServiceStub();
    private static final LocalGenericDataServiceStub genericDataServiceStub = new LocalGenericDataServiceStub();
    private static final ProjectManagementServiceStub projectManagementServiceStub = new ProjectManagementServiceStub();
    private static final VocabularyServiceStub vocabularyServiceStub = new VocabularyServiceStub();

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, arrayDesignServiceStub);
        locatorStub.addLookup(GenericDataService.JNDI_NAME, genericDataServiceStub);
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, projectManagementServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, vocabularyServiceStub);
    }

    @Test
    public void testPrepare() throws Exception {
        // no manufacturer
        setProjectId(1L);
        this.action.prepare();
        assertEquals(vocabularyServiceStub.getOrganisms().size(), this.action.getOrganisms().size());
        assertEquals(arrayDesignServiceStub.getArrayDesignProviders().size(), this.action.getManufacturers().size());
        assertEquals(0, this.action.getArrayDesigns().size());
        // project with manufacturer
        setProjectId(2L);
        this.action.prepare();
        assertEquals(vocabularyServiceStub.getOrganisms().size(), this.action.getOrganisms().size());
        assertEquals(arrayDesignServiceStub.getArrayDesignProviders().size(), this.action.getManufacturers().size());
        assertEquals(1, this.action.getArrayDesigns().size());
    }


    @Test
    public void testLoad() throws Exception {
        setProjectId(1L);
        assertEquals(Action.INPUT, this.action.load());
        assertEquals(projectManagementServiceStub.getTissueSitesForExperiment(null).size(), this.action.getTissueSites().size());
        assertEquals(projectManagementServiceStub.getDiseaseStatesForExperiment(null).size(), this.action.getDiseaseState().size());
        assertEquals(projectManagementServiceStub.getMaterialTypesForExperiment(null).size(), this.action.getMaterialTypes().size());
        assertEquals(projectManagementServiceStub.getCellTypesForExperiment(null).size(), this.action.getCellTypes().size());
    }

    @Test
    public void testRetrieveArrayDesigns() throws Exception {
        this.action.setManufacturerId(1L);
        SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
        assayTypes.add(new AssayType("Gene Expression"));
        this.action.setAssayTypeValues(assayTypes);
        assertEquals("xmlArrayDesigns", this.action.retrieveArrayDesigns());
        assertEquals(1, this.action.getArrayDesigns().size());
    }

    @SuppressWarnings("deprecation")
    private void setProjectId(Long id) {
        Project proj = new Project() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean hasReadPermission(User user) {
                return (this.getId() == 1l);
            }

            @Override
            public boolean hasWritePermission(User user) {
                return (this.getId() == 1l);
            }

        };
        proj.setId(id);
        proj.getExperiment().setId(id);
        this.action.setProject(proj);
    }

    @SuppressWarnings("deprecation")
    private static class LocalArrayDesignServiceStub extends ArrayDesignServiceStub {
        @Override
        public List<Organization> getAllProviders() {
            List<Organization> providers = new ArrayList<Organization>();
            Organization p1 = new Organization();
            p1.setId(1L);
            providers.add(p1);
            return providers;
        }
        @Override
        public List<Organization> getArrayDesignProviders() {
            List<Organization> providers = new ArrayList<Organization>();
            Organization p1 = new Organization();
            p1.setId(1L);
            providers.add(p1);
            return providers;
        }
        @Override
        public List<ArrayDesign> getImportedArrayDesignsForProvider(Organization provider) {
            if (provider != null && Long.valueOf(1L).equals(provider.getId())) {
                List<ArrayDesign> designs = new ArrayList<ArrayDesign>();
                ArrayDesign d1 = new ArrayDesign();
                d1.setId(1L);
                designs.add(d1);
                return designs;
            }
            return null;
        }
        @Override
        public List<ArrayDesign> getImportedArrayDesigns(Organization provider, Set<AssayType> assayTypes) {
            AssayType DUMMY_ASSAYTYPE_1 = new AssayType("Gene Expression");
            if (provider != null && Long.valueOf(1L).equals(provider.getId()) &&
                    assayTypes.contains(DUMMY_ASSAYTYPE_1)) {
                List<ArrayDesign> designs = new ArrayList<ArrayDesign>();
                ArrayDesign d1 = new ArrayDesign();
                d1.setId(1L);
                SortedSet <AssayType>dummyAssayTypes = new TreeSet<AssayType>();
                dummyAssayTypes.add(DUMMY_ASSAYTYPE_1);
                d1.setAssayTypes(dummyAssayTypes);
                designs.add(d1);
                return designs;
            }
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    private static class LocalGenericDataServiceStub extends GenericDataServiceStub {
        @Override
        @SuppressWarnings("unchecked")
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (Organization.class.equals(entityClass) && Long.valueOf(1L).equals(entityId)) {
                Organization o = new Organization();
                o.setId(1L);
                return (T) o;
            }
            if (Project.class.equals(entityClass)) {
                Project p = new Project();
                p.setId(entityId);
                p.getExperiment().setId(1L);
                if (2L == entityId) {
                    Organization o = new Organization();
                    o.setId(1L);
                    p.getExperiment().setManufacturer(o);
                    SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
                    assayTypes.add(new AssayType("Gene Expression"));
                    p.getExperiment().setAssayTypes(assayTypes);
                }
                return (T) p;                
            }
            
            return null;
        }
    }
}
