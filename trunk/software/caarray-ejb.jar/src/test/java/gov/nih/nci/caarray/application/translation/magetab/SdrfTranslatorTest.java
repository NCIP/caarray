//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.MageTabParser;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author gax
 */
public class SdrfTranslatorTest {

    private MageTabTranslator translator;
    private MageTabTranslatorTest helper = new MageTabTranslatorTest();

    @Before
    public void setupTranslator() {
        helper.setupTranslator();
        translator = helper.translator;
    }

    private MageTabDocumentSet getDocumentSet(MageTabFileSet mtfs) {
        try {
            MageTabDocumentSet mtds = MageTabParser.INSTANCE.parse(mtfs);
            return mtds;
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }
    

    @Test
    public void testNormal_1() {
        MageTabDocumentSet ds = getDocumentSet(TestMageTabSets.NORMAL_1_INPUT_SET);
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(ds);
        CaArrayTranslationResult result = this.translator.translate(ds, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        Hybridization hyb = experiment.getHybridizations().iterator().next();
        checkPaCollection(hyb.getProtocolApplications(), "Scan");
        for (RawArrayData rad : hyb.getRawDataCollection()) {
            if (rad.getName().equals("test1.CEL")) {
                checkPaCollection(rad.getProtocolApplications());
            } else if(rad.getName().equals("test2.CEL")) {
                checkPaCollection(rad.getProtocolApplications(), "TRANPRTCL10656");
            } else {
                fail(rad.getName());
            }
        }        
        DerivedArrayData dad = hyb.getDerivedDataCollection().iterator().next();
        checkPaCollection(dad.getProtocolApplications());

    }

    @Test
    public void testNormal_2() {
        MageTabDocumentSet ds = getDocumentSet(TestMageTabSets.NORMAL_2_INPUT_SET);
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(ds);
        CaArrayTranslationResult result = this.translator
                .translate(ds, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        Hybridization hyb = experiment.getHybridizations().iterator().next();
        checkPaCollection(hyb.getProtocolApplications(), "Scan");
        RawArrayData rad = hyb.getRawDataCollection().iterator().next();
        checkPaCollection(rad.getProtocolApplications(), "TRANPRTCL10656");
        DerivedArrayData dad = hyb.getDerivedDataCollection().iterator().next();
        checkPaCollection(dad.getProtocolApplications());
    }

    // test normalization protocols end up in raw data.
    @Test
    public void testNormal_3() {
        MageTabDocumentSet ds = getDocumentSet(TestMageTabSets.NORMAL_3_INPUT_SET);
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(ds);
        CaArrayTranslationResult result = this.translator
                .translate(ds, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        Hybridization hyb = experiment.getHybridizations().iterator().next();
        RawArrayData rad = hyb.getRawDataCollection().iterator().next();
        checkPaCollection(rad.getProtocolApplications(), "TRANPRTCL10656", "derived-data-protocol");
        DerivedArrayData dad = hyb.getDerivedDataCollection().iterator().next();
        checkPaCollection(dad.getProtocolApplications());
    }

    // test multiple normalization protocols ending up in raw data.
    @Test
    public void testMultiNormalization() {
        MageTabDocumentSet ds = getDocumentSet(TestMageTabSets.MULTI_NORMALIZATION_1_INPUT_SET);
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(ds);
        CaArrayTranslationResult result = this.translator.translate(ds, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        Hybridization hyb = experiment.getHybridizations().iterator().next();
        RawArrayData rad = hyb.getRawDataCollection().iterator().next();
        checkPaCollection(rad.getProtocolApplications(), "TRANPRTCL10656", "derived-data-protocol-1", "derived-data-protocol-2");
        DerivedArrayData dad = hyb.getDerivedDataCollection().iterator().next();
        checkPaCollection(dad.getProtocolApplications());
    }

    // test multiple derived data with protocols ending up in derived data.
    @Test
    public void testMultiDerived() {
        MageTabDocumentSet ds = getDocumentSet(TestMageTabSets.MULTI_DERIVED_1_INPUT_SET);
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(ds);
        CaArrayTranslationResult result = this.translator.translate(ds, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        Hybridization hyb = experiment.getHybridizations().iterator().next();
        checkPaCollection(hyb.getProtocolApplications(), "Scan");
        RawArrayData rad = hyb.getRawDataCollection().iterator().next();
        checkPaCollection(rad.getProtocolApplications(), "derived-data-protocol-1");
        for (DerivedArrayData d : hyb.getDerivedDataCollection()) {
            if (d.getName().equals("test.data")) {
                checkPaCollection(d.getProtocolApplications(), "derived-data-protocol-2");
            } else if (d.getName().equals("test2.data")) {
                checkPaCollection(d.getProtocolApplications());
            }
        }
    }

    // test multiple derived data with no scan or normalizations ending up in derived data.
    @Test
    public void testNoScanNoNorm() {
        MageTabDocumentSet ds = getDocumentSet(TestMageTabSets.MULTI_NO_SCAN_1_INPUT_SET);
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(ds);
        CaArrayTranslationResult result = this.translator.translate(ds, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        Hybridization hyb = experiment.getHybridizations().iterator().next();
        checkPaCollection(hyb.getProtocolApplications(), "Scan");
        RawArrayData rad = hyb.getRawDataCollection().iterator().next();
        checkPaCollection(rad.getProtocolApplications(), "derived-data-protocol-1");
        for (DerivedArrayData d : hyb.getDerivedDataCollection()) {
            if (d.getName().equals("test.data")) {
                checkPaCollection(d.getProtocolApplications(), "derived-data-protocol-2");
            } else if (d.getName().equals("test2.data")) {
                checkPaCollection(d.getProtocolApplications());
            } else {
                fail(d.getName());
            }
        }
    }

    // test multiple derived data with no scan , multiple normalizations.
    @Test
    public void testNoScanWithNorm() {
        MageTabDocumentSet ds = getDocumentSet(TestMageTabSets.MULTI_NO_SCAN_2_INPUT_SET);
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(ds);
        CaArrayTranslationResult result = this.translator.translate(ds, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        Hybridization hyb = experiment.getHybridizations().iterator().next();
        checkPaCollection(hyb.getProtocolApplications(), "Scan");
        RawArrayData rad = hyb.getRawDataCollection().iterator().next();
        checkPaCollection(rad.getProtocolApplications(), "normalization-protocol-1", "normalization-protocol-2",
                "derived-data-protocol-1", "derived-data-protocol-2");
        for (DerivedArrayData d : hyb.getDerivedDataCollection()) {
            if (d.getName().equals("test.data")) {
                checkPaCollection(d.getProtocolApplications());
            } else if (d.getName().equals("test2.data")) {
                checkPaCollection(d.getProtocolApplications());
            } else {
                fail(d.getName());
            }
        }
    }

/*
 * revisit this test when looking at GF 24439
    // test multiple derived data with no scan or raw data, multiple normalizations.

    @Test
    public void testNoScanNoRawWithNorm() {
        MageTabDocumentSet ds = getDocumentSet(TestMageTabSets.MULTI_NO_SCAN_3_INPUT_SET");
dump(ds.getSdrfDocuments().iterator().next().getAllHybridizations().iterator().next(), "");
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(ds);
        CaArrayTranslationResult result = this.translator.translate(ds, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        Hybridization hyb = experiment.getHybridizations().iterator().next();
        assertTrue(hyb.getRawDataCollection().isEmpty());
        checkPaCollection(hyb.getProtocolApplications(), "Scan", "normalization-protocol-1", "normalization-protocol-2", "derived-data-protocol-1", "derived-data-protocol-2");
        for (DerivedArrayData d : hyb.getDerivedDataCollection()) {
            if (d.getName().equals("test.data")) {
                checkPaCollection(d.getProtocolApplications());
            } else if (d.getName().equals("test2.data")) {
                checkPaCollection(d.getProtocolApplications());
            } else {
                fail(d.getName());
            }
        }
    }
*/
    @Test
    public void testNoDerivedData() {
        MageTabDocumentSet ds = getDocumentSet(TestMageTabSets.NO_DERIVED_1_INPUT_SET);
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(ds);
        CaArrayTranslationResult result = this.translator
                .translate(ds, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        Hybridization hyb = experiment.getHybridizations().iterator().next();
        checkPaCollection(hyb.getProtocolApplications(), "Scan");
        RawArrayData rad = hyb.getRawDataCollection().iterator().next();
        checkPaCollection(rad.getProtocolApplications(), "TRANPRTCL10656");
        assertTrue(hyb.getDerivedDataCollection().isEmpty());
    }

    @Test
    public void testNoNormNoDerivedData() {
        MageTabDocumentSet ds = getDocumentSet(TestMageTabSets.NO_DERIVED_2_INPUT_SET);
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(ds);
        CaArrayTranslationResult result = this.translator
                .translate(ds, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        Hybridization hyb = experiment.getHybridizations().iterator().next();
        checkPaCollection(hyb.getProtocolApplications(), "Scan");
        RawArrayData rad = hyb.getRawDataCollection().iterator().next();
        checkPaCollection(rad.getProtocolApplications(), "TRANPRTCL10656");
        assertTrue(hyb.getDerivedDataCollection().isEmpty());
    }


    private void dumpProtocols(Collection<ProtocolApplication> pas) {
        for (ProtocolApplication pa : pas) {
            System.out.println(pa.getProtocol().getName());
        }
    }

    private void checkPaCollection(Collection<ProtocolApplication> protocolApplications, String... pNames) {
        StringBuilder sb = new StringBuilder();
        for (String pn : pNames){
            boolean found = false;
            sb.setLength(0);
            sb.append(pn).append(" not found in ");
            for (ProtocolApplication pa : protocolApplications) {
                found |= pa.getProtocol().getName().equals(pn);
                sb.append(pa.getProtocol().getName()).append(", ");
            }
            assertTrue(sb.toString(), found);
        }
        assertEquals("count of " + sb, pNames.length, protocolApplications.size());
    }

    private void dump(AbstractSampleDataRelationshipNode node, String tab) {
        System.out.println(tab + " -> " + toString(node));
        dumpProtocols(node.getProtocolApplications(), tab);
        for(AbstractSampleDataRelationshipNode x : node.getPredecessors()) {
            System.out.println(tab + "\t <- "+toString(x));
        }
        for(AbstractSampleDataRelationshipNode x : node.getSuccessors()) {
            dump(x, tab + "\t");
        }
    }
    
    private String toString(AbstractSampleDataRelationshipNode node) {
        return node.getName() + "("+node.getNodeType()+"-"+System.identityHashCode(node)+")";
    }

    private void dumpProtocols(List<gov.nih.nci.caarray.magetab.ProtocolApplication> protocolApplications, String tab) {
        for (gov.nih.nci.caarray.magetab.ProtocolApplication pa : protocolApplications) {
            System.out.println(tab+"     prot = " + pa.getProtocol().getName());
        }
    }


}
