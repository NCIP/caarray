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
package gov.nih.nci.caarray.application.translation.geosoft;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.AbstractContact;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AbstractFactorValue;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 * Helper class to compose the GEO file.
 * Note: this class is intended to reduce the complexity of GeoSoftExporterBean.
 * @see GeoSoftExporterBean
 * @author gax
 * @since 2.3.1
 */
final class GeoSoftFileWriterUtil {
    private static final Map<String, String> MATERIAL_TYPE_MAP;
    static {
        Map<String, String> map = new HashMap<String, String>();
        map.put("cytoplasmic_RNA".toLowerCase(), "cytoplasmic RNA");
        map.put("DNA".toLowerCase(), "genomic DNA");
        map.put("genomic_DNA".toLowerCase(), "genomic DNA");
        map.put("nuclear_RNA".toLowerCase(), "nuclear RNA");
        map.put("polyA_RNA".toLowerCase(), "polyA RNA");
        map.put("protein".toLowerCase(), "protein");
        map.put("RNA".toLowerCase(), "total RNA");
        map.put("total RNA".toLowerCase(), "total RNA");
        map.put("total_RNA".toLowerCase(), "total RNA");
        MATERIAL_TYPE_MAP = Collections.unmodifiableMap(map);
    }

    private static final Predicate<ProtocolApplication> PREDICATE_TREATMENT = new ProtocolPredicate("treatment");
    private static final Predicate<ProtocolApplication> PREDICATE_GROWTH = new ProtocolPredicate("growth");
    private static final Predicate<ProtocolApplication> PREDICATE_EXTRACT = Predicates.or(
            new ProtocolPredicate("extract"), new ProtocolPredicate("extraction"));
    private static final Predicate<ProtocolApplication> PREDICATE_LABELING = new ProtocolPredicate("labeling");
    private static final Predicate<ProtocolApplication> PREDICATE_SCAN = new ProtocolPredicate("scan");
    private static final Predicate<ProtocolApplication> PREDICATE_HYB = new ProtocolPredicate("hybridization");


    private GeoSoftFileWriterUtil() {
        // no-op
    }

    /**
     * Write the GEO SOFT file for an valid experiment.
     * @param
     */
    static void writeSoftFile(Experiment experiment, String permaLinkUrl, PrintWriter out) {
        writeSampleSections(experiment, out);
        writeSeriesSection(experiment, permaLinkUrl, out);
    }

    private static void writeSeriesSection(Experiment experiment, String permaLinkUrl, PrintWriter out) {
        out.print("^SERIES=");
        out.println(experiment.getPublicIdentifier());
        out.print("!Series_title=");
        out.println(experiment.getTitle());
        out.print("!Series_summary=");
        String desc = StringUtils.isNotBlank(experiment.getDescription())
                ?   experiment.getDescription() : experiment.getTitle();
        out.println(desc);
        writeExperimentDesignTypes(experiment, desc, out);
        for (Publication pub : experiment.getPublications()) {
            out.print("!Series_pubmed_id=");
            out.println(pub.getPubMedId());
        }
        for (ExperimentContact c : experiment.getExperimentContacts()) {
            writeExperimentContact(c, out);
        }
        out.print("!Series_web_link=");
        out.println(permaLinkUrl);
        for (Hybridization h : experiment.getHybridizations()) {
            out.print("!Series_sample_id=");
            out.println(h.getName());
        }
    }

    private static void writeSampleSections(Experiment experiment, PrintWriter out) {
        for (Hybridization h : experiment.getHybridizations()) {
            writeSampleSection(h, out);
        }
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private static void writeSampleSection(Hybridization h, PrintWriter out) {
        out.print("^SAMPLE=");
        out.println(h.getName());
        out.print("!Sample_title=");
        out.println(h.getName());
        out.print("!Sample_description=");
        String desc = StringUtils.isNotBlank(h.getDescription()) ? h.getDescription() : h.getName();
        out.println(desc);
        writeData(h, out);

        LabeledExtract labeledExtract = h.getLabeledExtracts().iterator().next();
        Extract extract = labeledExtract.getExtracts().iterator().next();
        Sample sample = extract.getSamples().iterator().next();
        Source source = sample.getSources().iterator().next();
        out.print("!Sample_source_name=");
        out.println(source.getName());
        writeOrganism(source, sample, out);
        @SuppressWarnings("unchecked")
        Iterable<ProtocolApplication> pas = Iterables.concat(h.getProtocolApplications(),
                labeledExtract.getProtocolApplications(), extract.getProtocolApplications(),
                sample.getProtocolApplications(), source.getProtocolApplications());
        writeProtocol(pas, "Sample_treatment_protocol", PREDICATE_TREATMENT, out);
        writeProtocol(pas, "Sample_growth_protocol", PREDICATE_GROWTH, out);
        writeProtocol(sample.getProtocolApplications(), "Sample_extract_protocol", PREDICATE_EXTRACT, out);
        writeProtocol(extract.getProtocolApplications(), "Sample_label_protocol", PREDICATE_LABELING, out);
        writeProtocol(labeledExtract.getProtocolApplications(), "Sample_hyb_protocol", PREDICATE_HYB, out);
        writeProtocol(h.getProtocolApplications(), "Sample_scan_protocol", PREDICATE_SCAN, out);
        writeProtocolsForData(h.getRawDataCollection(), "Sample_data_processing", out);

        out.print("!Sample_label=");
        out.println(labeledExtract.getLabel().getValue());

        for (AbstractContact c : source.getProviders()) {
            out.print("!Sample_biomaterial_provider=");
            out.println(((Organization) c).getName());
        }

        out.print("!Sample_platform_id=");
        out.println(h.getArray().getDesign().getGeoAccession());

        for (AbstractFactorValue fv : h.getFactorValues()) {
            writeCharacteristics(fv.getFactor().getName(), fv.getDisplayValue(), out);
        }
        writeCharacteristics(labeledExtract, out);
        writeCharacteristics(extract, out);
        writeCharacteristics(sample, out);
        writeCharacteristics(source, out);

        out.print("!Sample_molecule=");
        Term mt = extract.getMaterialType();
        if (mt == null) {
            mt = labeledExtract.getMaterialType();
        }
        String m = translateMaterial(mt);
        out.println(m);
    }

    private static void writeProtocolsForData(Iterable<? extends AbstractArrayData> data, String label,
            PrintWriter out) {
        List<ProtocolApplication> l = new ArrayList<ProtocolApplication>();
        for (AbstractArrayData d : data) {
            l.addAll(d.getProtocolApplications());
        }
        writeProtocol(l, label, null, out);
    }

    private static void writeProtocol(Iterable<ProtocolApplication> pas, String label,
            Predicate<ProtocolApplication> protocolFilter, PrintWriter out) {

        Iterable<ProtocolApplication> selected = pas;
        if (protocolFilter != null) {
            selected = Iterables.filter(pas, protocolFilter);
        }
        if (selected.iterator().hasNext()) {
            out.print('!');
            out.print(label);
            out.print("=");
            String semi = "\"";
            for (ProtocolApplication pa : selected) {
                out.print(semi);
                semi = "\"; \"";
                out.print(pa.getProtocol().getName());
                out.print(':');
                out.print(pa.getProtocol().getDescription());
            }
            out.println('"');
        }
    }

    private static String translateMaterial(Term mt) {
        String geoMt = MATERIAL_TYPE_MAP.get(mt.getValue().toLowerCase());
        if (geoMt == null) {
            geoMt = "other";
        }
        return geoMt;
    }

    private static void writeCharacteristics(AbstractBioMaterial bio, PrintWriter out) {
        for (AbstractCharacteristic c : bio.getCharacteristics()) {
            writeCharacteristics(c.getCategory().getName(), c.getDisplayValue(), out);
        }
        writeExtraCharacteristics(ExperimentOntologyCategory.ORGANISM_PART, bio.getTissueSite(), out);
        writeExtraCharacteristics(ExperimentOntologyCategory.DISEASE_STATE, bio.getDiseaseState(), out);
        writeExtraCharacteristics(ExperimentOntologyCategory.CELL_TYPE, bio.getCellType(), out);
        writeExtraCharacteristics(ExperimentOntologyCategory.EXTERNAL_ID, bio.getExternalId(), out);
    }

    private static void writeExtraCharacteristics(ExperimentOntologyCategory key, Term value, PrintWriter out) {
        writeExtraCharacteristics(key, value == null ? null : value.getValue(), out);
    }

    private static void writeExtraCharacteristics(ExperimentOntologyCategory key, String value, PrintWriter out) {
        writeCharacteristics(key.getCategoryName(), value, out);
    }
    
    private static void writeCharacteristics(String key, String value, PrintWriter out) {
        if (StringUtils.isNotBlank(value)) {
            out.print("!Sample_characteristics=");
            out.print(key);
            out.print(':');
            out.println(value);
        }
    }

    private static void writeOrganism(Source source, Sample sample, PrintWriter out) {
        out.print("!Sample_organism=");
        Organism o = source.getOrganism();
        if (o == null) {
            o = sample.getOrganism();
        }
        if (o == null) {
            o = source.getExperiment().getOrganism();
        }
        out.println(o.getScientificName());
    }

    private static void writeData(Hybridization h, PrintWriter out) {
        for (RawArrayData rad : h.getRawDataCollection()) {
            out.print("!Sample_supplementary_file=");
            out.println(rad.getDataFile().getName());
        }
        for (DerivedArrayData dad : h.getDerivedDataCollection()) {
            if (dad.getDataFile().getFileType() == FileType.AFFYMETRIX_CHP) {
                out.print("!Sample_table=");
                out.println(dad.getDataFile().getName());
            } else {
                out.print("!Sample_supplementary_file=");
                out.println(dad.getDataFile().getName());
            }
        }
    }

    private static void writeExperimentContact(ExperimentContact c, PrintWriter out) {
        Person p = c.getPerson();
        out.print("!Series_contributor=");
        out.print(p.getFirstName());
        out.print(',');
        if (StringUtils.isNotBlank(p.getMiddleInitials())) {
            out.print(p.getMiddleInitials());
            out.print(",");
        }
        out.println(p.getLastName());
    }

    private static void writeExperimentDesignTypes(Experiment experiment, String desc, PrintWriter out) {
        out.print("!Series_overall_design=");
        if (experiment.getExperimentDesignTypes().isEmpty()) {
            out.println(desc);
        } else {
            String semi = "\"";
            for (Term edt : experiment.getExperimentDesignTypes()) {
                out.print(semi);
                semi = "\"; \"";
                out.print(edt.getValue());
                out.print(" (");
                out.print(edt.getSource().getName());
                out.print(')');
            }
            out.println('"');
        }
    }

    /**
     * Helper to filter out protocol collections by protocol type.
     */
    private static final class ProtocolPredicate implements Predicate<ProtocolApplication> {

        private final String value;

        /**
         * @param value of the protocol type to match.
         */
        ProtocolPredicate(String value) {
            this.value = value;
        }

        public boolean apply(ProtocolApplication pa) {
            return value.equals(pa.getProtocol().getType().getValue());
        }
    }

}
