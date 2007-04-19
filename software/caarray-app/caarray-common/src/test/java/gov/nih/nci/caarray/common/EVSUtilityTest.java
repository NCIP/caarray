/**
 *  The caArray Software License, Version 1.0
 *
 *  Copyright 2004 5AM Solutions. This software was developed in conjunction
 *  with the National Cancer Institute, and so to the extent government
 *  employees are co-authors, any rights in such works shall be subject to
 *  Title 17 of the United States Code, section 105.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the disclaimer of Article 3, below.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  2. Affymetrix Pure Java run time library needs to be downloaded from
 *  (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 *  after agreeing to the licensing terms from the Affymetrix.
 *
 *  3. The end-user documentation included with the redistribution, if any,
 *  must include the following acknowledgment:
 *
 *  "This product includes software developed by 5AM Solutions and the National
 *  Cancer Institute (NCI).
 *
 *  If no such end-user documentation is to be included, this acknowledgment
 *  shall appear in the software itself, wherever such third-party
 *  acknowledgments normally appear.
 *
 *  4. The names "The National Cancer Institute", "NCI", and "5AM Solutions"
 *  must not be used to endorse or promote products derived from this software.
 *
 *  5. This license does not authorize the incorporation of this software into
 *  any proprietary programs. This license does not authorize the recipient to
 *  use any trademarks owned by either NCI or 5AM.
 *
 *  6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 *  EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.common;

import gov.nih.nci.caarray.application.vocabulary.EVSUtility;
import gov.nih.nci.caarray.vocabulary.Term;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author John Pike
 *
 */
public class EVSUtilityTest extends TestCase {
    /**
     * various private uses
     */
    private static final int NUMBER_THREE = 3;
    /**
     * Create the test case.
     *
     * @param testName name of the test case
     */
    public EVSUtilityTest(final String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(EVSUtilityTest.class);
    }


    /**
    * Tests basic search in EVS.
    */
    public final void testSearchEVS() {
        EVSUtility evs = new EVSUtility();
        List<Term> results = evs.getConcepts("ProtocolType");
        List<String> termNames = new ArrayList<String>();
        for (Iterator<Term> i = results.iterator(); i.hasNext();) {
            Term aTerm = i.next();
            termNames.add(aTerm.getName());
        }


        assertTrue(!termNames.isEmpty());
        assertTrue(termNames.size() == NUMBER_THREE);
        assertTrue(termNames.contains("DataTransformationProtocolType"));
        assertTrue(termNames.contains("ExperimentalProtocolType"));
        assertTrue(termNames.contains("HigherLevelAnalysisProtocolType"));
    }

    /**
     * Tests basic search in EVS, where search should return no results.
     */
    public final void testSearchEVSNoResults() {
        EVSUtility evs = new EVSUtility();
        List<Term> results = evs.getConcepts("Foo");
        List<String> termNames = new ArrayList<String>();
        for (Iterator<Term> i = results.iterator(); i.hasNext();) {
            Term aTerm = i.next();
            termNames.add(aTerm.getName());
        }


        assertTrue(termNames.isEmpty());

    }







}
