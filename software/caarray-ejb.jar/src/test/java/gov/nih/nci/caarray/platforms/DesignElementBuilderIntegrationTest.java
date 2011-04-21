/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
 */package gov.nih.nci.caarray.platforms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.AbstractHibernateTest;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.DaoModule;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementReference;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.plugins.illumina.IlluminaCsvDesignHandler;
import gov.nih.nci.caarray.staticinjection.CaArrayCommonStaticInjectionModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;

import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author dkokotov
 *
 */
public class DesignElementBuilderIntegrationTest extends AbstractHibernateTest {    
    /**
     * 
     */
    private static final int NUMBER_OF_PROBES = 700;
    private ArrayDao arrayDao;
    private SearchDao searchDao;
    
    private ArrayDesign design;

    public DesignElementBuilderIntegrationTest() {
        super(false);
    }
     
    /**
     * Subclasses can override this to configure a custom injector, e.g. by overriding some modules with stubbed out
     * functionality.
     * 
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected Injector createInjector() {
        return Guice.createInjector(new CaArrayCommonStaticInjectionModule(), new CaArrayHibernateHelperModule(),
                new DaoModule());
    }

    @Before
    public void setUp() throws Exception {        
        arrayDao = injector.getInstance(ArrayDao.class);
        searchDao = injector.getInstance(SearchDao.class);
        design = createArrayDesign();
    }
    
    @Test
    public void testBuilder() {
        Transaction tx = hibernateHelper.beginTransaction();
        hibernateHelper.getCurrentSession().save(design);        
        DataSet ds = new DataSet();
        hibernateHelper.getCurrentSession().save(ds);                
        tx.commit();

        tx = hibernateHelper.beginTransaction();
        DesignElementBuilder builder = new DesignElementBuilder(ds, design, arrayDao, searchDao, 300);
        final int midpoint = NUMBER_OF_PROBES / 2;
        for (int i = 0; i < midpoint; i++) {
            builder.addProbe("PROBE_" + i);
        }
        for (int i = midpoint; i < NUMBER_OF_PROBES; i++) {
            builder.addProbe("dummy probe name", "PROBE_" + i);
        }
        builder.finish();
        tx.commit();
        
        tx = hibernateHelper.beginTransaction();
        DesignElementList del = (DesignElementList) hibernateHelper.getCurrentSession().load(DesignElementList.class,
                ds.getDesignElementList().getId());
        assertEquals(NUMBER_OF_PROBES, del.getDesignElementReferences().size());
        assertEquals(NUMBER_OF_PROBES, del.getDesignElements().size());
        for (int i = 0; i < NUMBER_OF_PROBES; i++) {
            DesignElementReference der = del.getDesignElementReferences().get(i);
            AbstractDesignElement de = del.getDesignElements().get(i);
            String probeName = "PROBE_" + i;
            assertNotNull(der.getDesignElement());
            assertTrue(der.getDesignElement() instanceof PhysicalProbe);
            assertEquals(probeName, ((PhysicalProbe) der.getDesignElement()).getName());
            assertTrue(de instanceof PhysicalProbe);
            assertEquals(probeName, ((PhysicalProbe) de).getName());
        }
        tx.commit();        
    }
    
    private static ArrayDesign createArrayDesign() {
        TermSource ts = new TermSource();
        ts.setName("TS 1");
        Category cat = new Category();
        cat.setName("catName");
        cat.setSource(ts);

        Term term = new Term();
        term.setValue("testval");
        term.setCategory(cat);
        term.setSource(ts);

        Organism organism = new Organism();
        organism.setScientificName("Homo sapiens");
        organism.setTermSource(ts);

        Organization o = new Organization();
        o.setName("DummyOrganization");
        o.setProvider(true);
        
        ArrayDesign design = new ArrayDesign();
        design.setName("foo");
        design.setVersion("99");
        design.setGeoAccession("GPL0001");
        design.setProvider(o);
        AssayType at1 = new AssayType("Gene Expression");
        SortedSet<AssayType> assayTypes = new TreeSet<AssayType>();
        assayTypes.add(at1);
        design.setTechnologyType(term);
        design.setOrganism(organism);

        ArrayDesignDetails detail = new ArrayDesignDetails();
        design.setDesignDetails(detail);
        for (int i = 0; i < NUMBER_OF_PROBES; i++) {
            PhysicalProbe p = new PhysicalProbe(detail, null);
            p.setName("PROBE_" + i);
            detail.getProbes().add(p);            
        }
        
        CaArrayFile f = new CaArrayFile();
        f.setFileStatus(FileStatus.IMPORTED);
        f.setFileType(IlluminaCsvDesignHandler.DESIGN_CSV_FILE_TYPE);
        design.addDesignFile(f);
        return design;
    }
}
