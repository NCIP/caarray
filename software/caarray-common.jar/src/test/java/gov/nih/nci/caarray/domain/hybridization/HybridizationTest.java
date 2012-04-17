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
package gov.nih.nci.caarray.domain.hybridization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.sample.UserDefinedCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for Hybridization class
 * @author dkokotov
 */
@SuppressWarnings("PMD")
public class HybridizationTest {
    @Test
    public void testGetCharacteristicsRecursively() {
        TermSource mged = new TermSource();
        mged.setName(ExperimentOntology.MGED_ONTOLOGY.getOntologyName());
        mged.setVersion(ExperimentOntology.MGED_ONTOLOGY.getVersion());
        
        TermSource caarray = new TermSource();
        caarray.setName(ExperimentOntology.CAARRAY.getOntologyName());
        caarray.setVersion(ExperimentOntology.CAARRAY.getVersion());

        Category ds = new Category(ExperimentOntologyCategory.DISEASE_STATE.getCategoryName(), mged);
        Category mt = new Category(ExperimentOntologyCategory.MATERIAL_TYPE.getCategoryName(), mged);
        Category ct = new Category(ExperimentOntologyCategory.CELL_TYPE.getCategoryName(), mged);
        Category id = new Category(ExperimentOntologyCategory.EXTERNAL_ID.getCategoryName(), caarray);
        Category c2 = new Category("Fake category", caarray);

        Term t1 = new Term();
        t1.setValue("Foo");
        t1.setCategory(ds);

        Term t2 = new Term();
        t2.setValue("Foo2");
        t2.setCategory(mt);

        Term t3 = new Term();
        t3.setValue("Foo3");
        t3.setCategory(ct);

        MeasurementCharacteristic mf = new MeasurementCharacteristic();
        mf.setCategory(c2);
        mf.setValue(1.0f);

        Source so1 = new Source();
        so1.setName("Foo");
        so1.setExternalId("X");
        so1.setMaterialType(t1);
        so1.setDiseaseState(t2);

        Sample sa1 = new Sample();
        sa1.setName("Foo");
        sa1.setExternalId("X2");        
        so1.getSamples().add(sa1);
        sa1.getSources().add(so1);
        sa1.setMaterialType(t2);
        sa1.setDiseaseState(t1);

        Sample sa2 = new Sample();
        sa2.setName("Foo");
        sa2.setExternalId("X3");
        sa2.setDiseaseState(t3);

        Extract ex1 = new Extract();
        ex1.setName("Foo");
        ex1.getSamples().add(sa1);
        sa1.getExtracts().add(ex1);
        ex1.getSamples().add(sa2);
        sa2.getExtracts().add(ex1);

        LabeledExtract le1 = new LabeledExtract();
        le1.setName("Foo");        
        le1.getExtracts().add(ex1);
        ex1.getLabeledExtracts().add(le1);

        LabeledExtract le2 = new LabeledExtract();
        le1.setName("Foo2");        

        Hybridization h1 = new Hybridization();
        h1.setName("foo");
        h1.getLabeledExtracts().add(le1);
        le1.getHybridizations().add(h1);
        h1.getLabeledExtracts().add(le2);
        le2.getHybridizations().add(h1);
        
        le2.getCharacteristics().add(mf);
        
        Set<AbstractCharacteristic> chars = h1.getCharacteristicsRecursively(mt);
        assertEquals(1, chars.size());
        AbstractCharacteristic char1 = chars.iterator().next();
        assertTrue(char1 instanceof TermBasedCharacteristic);
        assertEquals(mt.getName(), char1.getCategory().getName());
        assertEquals(t2, ((TermBasedCharacteristic) char1).getTerm());

        chars = h1.getCharacteristicsRecursively(ds);
        assertEquals(2, chars.size());
        List<String> termNames = new ArrayList<String>();
        Iterator<AbstractCharacteristic> charIt = chars.iterator();
        char1 = charIt.next();
        assertTrue(char1 instanceof TermBasedCharacteristic);
        assertEquals(ds.getName(), char1.getCategory().getName());
        termNames.add(((TermBasedCharacteristic) char1).getTerm().getValue());
        AbstractCharacteristic char2 = charIt.next();
        assertEquals(ds.getName(), char2.getCategory().getName());
        termNames.add(((TermBasedCharacteristic) char2).getTerm().getValue());
        Collections.sort(termNames);
        assertEquals("Foo", termNames.get(0));
        assertEquals("Foo3", termNames.get(1));
        
        chars = h1.getCharacteristicsRecursively(ct);
        assertEquals(0, chars.size());
        
        chars = h1.getCharacteristicsRecursively(id);
        assertEquals(2, chars.size());
        List<String> values = new ArrayList<String>();
        charIt = chars.iterator();
        char1 = charIt.next();
        assertTrue(char1 instanceof UserDefinedCharacteristic);
        assertEquals(id.getName(), char1.getCategory().getName());
        values.add(((UserDefinedCharacteristic) char1).getValue());
        char2 = charIt.next();
        assertEquals(id.getName(), char2.getCategory().getName());
        values.add(((UserDefinedCharacteristic) char2).getValue());
        Collections.sort(values);
        assertEquals("X2", values.get(0));
        assertEquals("X3", values.get(1));

        chars = h1.getCharacteristicsRecursively(c2);
        assertEquals(1, chars.size());
        assertEquals(mf, chars.iterator().next());
    }
    
    @Test
    public void testPropagateLastModifiedDataTime() {
        Date date = new Date();
        Hybridization h = new Hybridization();
        LabeledExtract le = new LabeledExtract();
        h.getLabeledExtracts().add(le);
        le.getHybridizations().add(h);
        h.propagateLastModifiedDataTime(date);
        assertEquals(date, le.getLastModifiedDataTime());
    }
}
