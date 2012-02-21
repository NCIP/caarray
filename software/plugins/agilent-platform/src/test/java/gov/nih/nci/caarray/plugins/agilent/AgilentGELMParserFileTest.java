/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray2-trunk
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caarray2-trunk Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caarray2-trunk Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray2-trunk Software; (ii) distribute and 
 * have distributed to and by third parties the caarray2-trunk Software and any 
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
package gov.nih.nci.caarray.plugins.agilent;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.test.data.arraydesign.AgilentArrayDesignFiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.Order;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Basic tests for AgilentGELMParser.  Exercises the functionality in a (sorta)
 * unit-test way.  Subclasses may add additional files to the list.
 * 
 */
public class AgilentGELMParserFileTest {

    private static ArrayDesignBuilderImpl arrayDesignBuilder;

    @Test
    public void testAcghXmlFile() throws FileNotFoundException {
        testSingleFile(AgilentArrayDesignFiles.TEST_SHORT_ACGH_XML);  
    }

    @Test
    @Ignore(value = "Large file, no extra coverage over expression file #2")
    public void validatesTestGeneExpressionOneXmlFile() throws FileNotFoundException {
        testSingleFile(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_1_XML);       
    }

    @Test
    public void testGeneExpressionTwoXmlFile() throws FileNotFoundException {
        testSingleFile(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_2_XML);       
    }

    @Test
    @Ignore(value = "Large file, no extra coverage over expression file #2")
    public void validatesTestGeneExpressionThreeXmlFile() throws FileNotFoundException {
        testSingleFile(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_3_XML);       
    }

    @Test
    public void testMiRnaOneXmlFile() throws FileNotFoundException {
        testSingleFile(AgilentArrayDesignFiles.TEST_MIRNA_1_XML);       
    }
    
    @Test
    @Ignore(value = "Large file, no extra coverage over mirna #1")
    public void validatesTestMiRnaTwoXmlFile() throws FileNotFoundException {
        testSingleFile(AgilentArrayDesignFiles.TEST_MIRNA_2_XML);       
    }
    
    private void testSingleFile(File file) {
        validate(file);
        parse(file);
    }
    
    private void validate(File file) {
        AgilentGELMParser parser = getParser(file);
        assertTrue(parser.validate());
    }
    
    private void parse(File file) {
        AgilentGELMParser parser = getParser(file);
        assertTrue(parser.parse(arrayDesignBuilder));
    }
    
    private AgilentGELMParser getParser(File file) {
        FileReader reader;
        try {
            reader = new FileReader(file);
            AgilentGELMTokenizer tokenizer = new AgilentGELMTokenizer(reader);
            return new AgilentGELMParser(tokenizer);
        } catch (FileNotFoundException e) {
            fail("Test file not available: " + file);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void setUp() throws Exception {
        final Term millimeterTerm = new Term();
        final TermSource termSource = new TermSource();
        List<TermSource> termSources = Collections.singletonList(termSource);
        
        ArrayDesignDetails arrayDesignDetailsMock = mock(ArrayDesignDetails.class);

        VocabularyDao vocabularyDaoMock = mock(VocabularyDao.class);
        
        when(vocabularyDaoMock.queryEntityByExample((ExampleSearchCriteria<TermSource>)anyObject(), any(Order.class)))
        .thenReturn(termSources);
        
        when(vocabularyDaoMock.getTerm(refEq(termSource), eq("mm"))).thenReturn(millimeterTerm);

        ArrayDao arrayDaoMock = mock(ArrayDao.class);
        
        SearchDao searchDaoMock = mock(SearchDao.class);
        
        arrayDesignBuilder = new ArrayDesignBuilderImpl(arrayDesignDetailsMock, vocabularyDaoMock, arrayDaoMock, searchDaoMock);
    }
}