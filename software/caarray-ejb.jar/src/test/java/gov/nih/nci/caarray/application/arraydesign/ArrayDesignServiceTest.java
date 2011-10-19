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
 */
package gov.nih.nci.caarray.application.arraydesign;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

/**
 * Test class for ArrayDesignService subsystem.
 */
@SuppressWarnings("PMD")
public class ArrayDesignServiceTest extends AbstractArrayDesignServiceTest {
    @Test
    public void testSaveArrayDesign() throws Exception {
        final File file = new File("dummyfile.txt");
        final ArrayDesign design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        this.arrayDesignService.importDesign(design);
        assertEquals(TEST_DESIGN_NAME, design.getName());
        assertEquals(TEST_LSID.getAuthority(), design.getLsidAuthority());
        assertEquals(TEST_LSID.getNamespace(), design.getLsidNamespace());
        assertEquals(TEST_LSID.getObjectId(), design.getLsidObjectId());
        assertNull(design.getDescription());

        design.setDescription("new description");
        this.arrayDesignService.saveArrayDesign(design);
        final ArrayDesign updatedDesign = this.arrayDesignService.getArrayDesign(design.getId());
        assertEquals(TEST_DESIGN_NAME, design.getName());
        assertEquals(TEST_LSID.getAuthority(), design.getLsidAuthority());
        assertEquals(TEST_LSID.getNamespace(), design.getLsidNamespace());
        assertEquals(TEST_LSID.getObjectId(), design.getLsidObjectId());
        assertEquals("new description", updatedDesign.getDescription());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSaveLockedArrayDesignAllowedFields() throws Exception {
        final File file = new File("dummyfile.txt");
        ArrayDesign design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        // "lock" this design by setting the ID to 2 but can still update description of locked designs
        design.setId(2L);
        this.caArrayDaoFactoryStub.getArrayDao().save(design);
        this.arrayDesignService.importDesign(design);

        design.setDescription("another description");
        design = this.arrayDesignService.saveArrayDesign(design);
        assertEquals(2L, design.getId().longValue());
        final ArrayDesign updatedLockedDesign = this.arrayDesignService.getArrayDesign(design.getId());
        assertEquals(TEST_DESIGN_NAME, design.getName());
        assertEquals(TEST_LSID.getAuthority(), updatedLockedDesign.getLsidAuthority());
        assertEquals(TEST_LSID.getNamespace(), updatedLockedDesign.getLsidNamespace());
        assertEquals(TEST_LSID.getObjectId(), updatedLockedDesign.getLsidObjectId());
        assertEquals("another description", updatedLockedDesign.getDescription());
    }

    @Test(expected = IllegalAccessException.class)
    public void testSaveArrayDesignWhileImporting() throws Exception {
        final File file = new File("dummyfile.txt");
        final ArrayDesign design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        this.arrayDesignService.importDesign(design);

        design.getFirstDesignFile().setFileStatus(FileStatus.IMPORTING);
        design.setName("new name");
        this.arrayDesignService.saveArrayDesign(design);
    }

    @Test(expected = IllegalAccessException.class)
    @SuppressWarnings("deprecation")
    public void testSaveArrayDesignLockedProviderChangeOrganization() throws Exception {
        final File file = new File("dummyfile.txt");
        ArrayDesign design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        // array designs with ID == 2 are considered locked by the test stub
        design.setId(2L);
        this.arrayDesignService.importDesign(design);

        // since the test DB is in memory, we have to instantiate a new copy of this design to alter it
        design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        design.setId(2L);
        design.setProvider(new Organization());
        this.arrayDesignService.saveArrayDesign(design);
    }

    @Test(expected = IllegalAccessException.class)
    @SuppressWarnings("deprecation")
    public void testSaveArrayDesignLockedProviderChangeAssayType() throws Exception {
        final File file = new File("dummyfile.txt");
        ArrayDesign design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        // array designs with ID == 2 are considered locked by the test stub
        design.setId(2L);
        this.arrayDesignService.importDesign(design);

        // since the test DB is in memory, we have to instantiate a new copy of this design to alter it
        design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        design.setId(2L);
        final SortedSet<AssayType> assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE);
        design.setAssayTypes(assayTypes);
        this.arrayDesignService.saveArrayDesign(design);
    }

    @Test(expected = IllegalAccessException.class)
    @SuppressWarnings("deprecation")
    public void testSaveArrayDesignLockedProviderChangeDesignFile() throws Exception {
        final File file = new File("dummyfile.txt");
        ArrayDesign design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        // array designs with ID == 2 are considered locked by the test stub
        design.setId(2L);
        this.arrayDesignService.importDesign(design);

        // since the test DB is in memory, we have to instantiate a new copy of this design to alter it
        design = createDesign(null, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        design.setId(2L);
        design.getDesignFiles().clear();
        design.addDesignFile(new CaArrayFile());
        this.arrayDesignService.saveArrayDesign(design);
    }

    @Test
    public void testValidateDesign_InvalidFileType() {
        final File file = new File("dummyfile.txt");
        CaArrayFile invalidDesignFile = getCaArrayFile(file, TEST_NONDESIGN_TYPE);
        ValidationResult result = this.arrayDesignService.validateDesign(Collections.singleton(invalidDesignFile));
        assertFalse(result.isValid());
        invalidDesignFile = getCaArrayFile(file, null);
        result = this.arrayDesignService.validateDesign(Collections.singleton(invalidDesignFile));
        assertFalse(result.isValid());
    }

    @Test
    public void testDuplicateArrayDesign() {
        final File file = new File("dummyfile.txt");
        final ArrayDesign design = createDesign(DUMMY_ORGANIZATION, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        this.arrayDesignService.importDesign(design);

        final ArrayDesign design2 = createDesign(DUMMY_ORGANIZATION, null, null, getCaArrayFile(file, TEST_DESIGN_TYPE));
        this.arrayDesignService.importDesign(design2);
        final ValidationResult result = this.arrayDesignService.validateDesign(design2);
        assertFalse(result.isValid());
        assertTrue(result.getMessages().iterator().next().getMessage().contains("design already exists with the name"));
    }

    @Test
    public void testOrganizations() {
        assertEquals(0, this.arrayDesignService.getAllProviders().size());
        final Organization o = new Organization();
        o.setName("Foo");
        o.setProvider(true);
        this.caArrayDaoFactoryStub.getSearchDao().save(o);
        assertEquals(1, this.arrayDesignService.getAllProviders().size());
        assertEquals("Foo", this.arrayDesignService.getAllProviders().get(0).getName());
    }
}
