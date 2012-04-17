/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray2 Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray2 Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2 Software; (ii) distribute and
 * have distributed to and by third parties the caArray2 Software and any
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
package gov.nih.nci.caarray.domain.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;

import java.lang.reflect.Method;
import java.util.Date;

import org.junit.Test;

/**
 * Tests for the Sample class.
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD")
public class SampleTest {

    @Test
    @SuppressWarnings("deprecation")
    public void testMerge() throws Exception {
        Project p = new Project();
        Experiment e = new Experiment();
        p.setExperiment(e);
        Method setter = Experiment.class.getDeclaredMethod("setProject", Project.class);
        setter.setAccessible(true);
        setter.invoke(e, p);

        CollaboratorGroup cg1 = new CollaboratorGroup();
        cg1.setId(1L);
        p.addGroupProfile(cg1);

        CollaboratorGroup cg2 = new CollaboratorGroup();
        cg2.setId(2L);
        p.addGroupProfile(cg2);

        CollaboratorGroup cg3 = new CollaboratorGroup();
        cg3.setId(3L);
        p.addGroupProfile(cg3);

        CollaboratorGroup cg4 = new CollaboratorGroup();
        cg4.setId(4L);
        p.addGroupProfile(cg4);

        AccessProfile ap1 = null;
        AccessProfile ap2 = null;
        AccessProfile ap3 = null;
        AccessProfile ap4 = null;
        for (AccessProfile ap : p.getAllAccessProfiles()) {
            if (ap.getGroup() != null && ap.getGroup().equals(cg1)) {
                ap1 = ap;
            } else if (ap.getGroup() != null && ap.getGroup().equals(cg2)) {
                ap2 = ap;
            } else if (ap.getGroup() != null && ap.getGroup().equals(cg3)) {
                ap3 = ap;
            } else if (ap.getGroup() != null && ap.getGroup().equals(cg4)) {
                ap4 = ap;
            }
        }

        ap1.setId(1L);
        ap2.setId(2L);
        ap3.setId(3L);
        ap4.setId(4L);

        Source source1 = new Source();
        source1.setId(1L);
        source1.setName("source1");
        source1.setExperiment(e);
        e.getSources().add(source1);

        Source source2 = new Source();
        source1.setId(2L);
        source1.setName("source2");
        source2.setExperiment(e);
        e.getSources().add(source2);

        Extract extract1 = new Extract();
        extract1.setId(1L);
        extract1.setName("extract1");
        extract1.setExperiment(e);
        e.getExtracts().add(extract1);

        Extract extract2 = new Extract();
        extract2.setId(2L);
        extract2.setName("extract2");
        extract2.setExperiment(e);
        e.getExtracts().add(extract2);

        Sample sample1 = new Sample();
        sample1.setId(1L);
        sample1.setName("sample1");
        sample1.getSources().add(source1);
        source1.getSamples().add(sample1);
        sample1.getExtracts().add(extract1);
        sample1.setExperiment(e);
        e.getSamples().add(sample1);
        ap1.getSampleSecurityLevels().put(sample1, SampleSecurityLevel.READ_WRITE);
        ap2.getSampleSecurityLevels().put(sample1, SampleSecurityLevel.READ_WRITE);
        ap3.getSampleSecurityLevels().put(sample1, SampleSecurityLevel.NONE);
        assertNull(p.getGroupProfiles().get(cg4).getSampleSecurityLevels().get(sample1));

        Sample sample2 = new Sample();
        sample2.setId(2L);
        sample2.setName("sample2");
        sample2.getSources().add(source2);
        source2.getSamples().add(sample2);
        sample2.getExtracts().add(extract2);
        e.getSamples().add(sample2);
        sample2.setExperiment(e);
        ap1.getSampleSecurityLevels().put(sample2, SampleSecurityLevel.READ);
        ap3.getSampleSecurityLevels().put(sample2, SampleSecurityLevel.READ);
        ap4.getSampleSecurityLevels().put(sample2, SampleSecurityLevel.NONE);

        sample1.merge(sample2);

        assertEquals(1, source1.getSamples().size());
        assertEquals(1, source2.getSamples().size());
        assertEquals(2, sample1.getSources().size());
        assertEquals(2, sample1.getExtracts().size());
        assertEquals(SampleSecurityLevel.READ, p.getGroupProfiles().get(cg1).getSampleSecurityLevels().get(sample1));
        assertEquals(SampleSecurityLevel.READ_WRITE, p.getGroupProfiles().get(cg2).getSampleSecurityLevels().get(sample1));
        assertEquals(SampleSecurityLevel.NONE, p.getGroupProfiles().get(cg3).getSampleSecurityLevels().get(sample1));
        assertEquals(SampleSecurityLevel.NONE, p.getGroupProfiles().get(cg4).getSampleSecurityLevels().get(sample1));
    }

    @Test
    public void testPropagateLastModifiedDataTime() {
        Date date = new Date();
        Source src = new Source();
        Sample s = new Sample();
        Extract e = new Extract();
        src.getSamples().add(s);
        s.getSources().add(src);
        s.getExtracts().add(e);
        e.getSamples().add(s);
        s.propagateLastModifiedDataTime(date);
        assertEquals(date, src.getLastModifiedDataTime());
        assertNotSame(date, e.getLastModifiedDataTime());
    }
}
