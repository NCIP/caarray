//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
