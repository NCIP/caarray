//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.geosoft;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.AbstractContact;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AbstractFactorValue;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplicable;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Helper class to compose the GEO file. Note: this class is intended to reduce the complexity of GeoSoftExporterBean.
 * 
 * @see GeoSoftExporterBean
 * @author gax
 * @since 2.3.1
 */
final class GeoSoftFileWriterUtil {
    private static final Map<String, String> MATERIAL_TYPE_MAP;
    static {
        final Map<String, String> map = new HashMap<String, String>();
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
    private static final Predicate<ProtocolApplication> PREDICATE_EXTRACT = new ProtocolPredicate(
            "nucleic_acid_extraction");
    private static final Predicate<ProtocolApplication> PREDICATE_LABELING = new ProtocolPredicate("labeling");
    private static final Predicate<ProtocolApplication> PREDICATE_SCAN = Predicates.or(new ProtocolPredicate("scan"),
            new ProtocolPredicate("image_acquisition"));
    private static final Predicate<ProtocolApplication> PREDICATE_HYB = new ProtocolPredicate("hybridization");

    /**
     * needed to make the output predicable.
     */
    private static final Comparator<PersistentObject> ENTITY_COMPARATOR = new Comparator<PersistentObject>() {

        @Override
        public int compare(PersistentObject o1, PersistentObject o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

    private GeoSoftFileWriterUtil() {
        // no-op
    }

    /**
     * Write the GEO SOFT file for an valid experiment.
     * 
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
        final String desc =
                StringUtils.isNotBlank(experiment.getDescription()) ? experiment.getDescription() : experiment
                        .getTitle();
        out.println(desc);
        writeExperimentDesignTypes(experiment, desc, out);
        for (final Publication pub : experiment.getPublications()) {
            out.print("!Series_pubmed_id=");
            out.println(pub.getPubMedId());
        }
        for (final ExperimentContact c : experiment.getExperimentContacts()) {
            writeExperimentContact(c, out);
        }
        out.print("!Series_web_link=");
        out.println(permaLinkUrl);
        for (final Hybridization h : experiment.getHybridizations()) {
            out.print("!Series_sample_id=");
            out.println(h.getName());
        }
    }

    private static void writeSampleSections(Experiment experiment, PrintWriter out) {
        for (final Hybridization h : experiment.getHybridizations()) {
            writeSampleSection(h, out);
        }
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private static void writeSampleSection(Hybridization h, PrintWriter out) {
        final Set<Source> sources = new TreeSet<Source>(ENTITY_COMPARATOR);
        final Set<Sample> samples = new TreeSet<Sample>(ENTITY_COMPARATOR);
        final Set<Extract> extracts = new TreeSet<Extract>(ENTITY_COMPARATOR);
        final Set<LabeledExtract> labeledExtracts = new TreeSet<LabeledExtract>(ENTITY_COMPARATOR);
        GeoSoftFileWriterUtil.collectBioMaterials(h, sources, samples, extracts, labeledExtracts);

        out.print("^SAMPLE=");
        out.println(h.getName());
        out.print("!Sample_title=");
        out.println(h.getName());
        writeDescription(out, h, samples);
        writeData(h, out);
        writeSources(out, sources);
        writeOrganism(sources, samples, h.getExperiment(), out);
        final Set<ProtocolApplication> pas = new TreeSet<ProtocolApplication>(ENTITY_COMPARATOR);
        pas.addAll(h.getProtocolApplications());
        collectAllProtocols(labeledExtracts, pas);
        collectAllProtocols(extracts, pas);
        collectAllProtocols(samples, pas);
        collectAllProtocols(sources, pas);

        writeProtocol(pas, "Sample_treatment_protocol", PREDICATE_TREATMENT, out);
        writeProtocol(pas, "Sample_growth_protocol", PREDICATE_GROWTH, out);
        writeProtocol(pas, "Sample_extract_protocol", PREDICATE_EXTRACT, out);
        writeProtocol(getAllProtocols(extracts), "Sample_label_protocol", PREDICATE_LABELING, out);
        writeProtocol(getAllProtocols(labeledExtracts), "Sample_hyb_protocol", PREDICATE_HYB, out);
        writeProtocol(h.getProtocolApplications(), "Sample_scan_protocol", PREDICATE_SCAN, out);
        writeProtocolsForData(h.getRawDataCollection(), "Sample_data_processing", out);

        for (final LabeledExtract labeledExtract : labeledExtracts) {
            out.print("!Sample_label=");
            out.println(labeledExtract.getLabel().getValue());
        }
        writeProviders(sources, out);

        out.print("!Sample_platform_id=");
        out.println(h.getArray().getDesign().getGeoAccession());

        final Set<String> uniqueEntries = new HashSet<String>();
        for (final AbstractFactorValue fv : h.getFactorValues()) {
            writeCharacteristics(fv.getFactor().getName(), fv.getDisplayValue(), out, uniqueEntries);
        }
        writeCharacteristics(labeledExtracts, out, uniqueEntries);
        writeCharacteristics(extracts, out, uniqueEntries);
        writeCharacteristics(samples, out, uniqueEntries);
        writeCharacteristics(sources, out, uniqueEntries);

        writeMaterialTypes(extracts, labeledExtracts, out);
    }

    private static void writeDescription(PrintWriter out, Hybridization h, Set<Sample> samples) {
        out.print("!Sample_description=");
        final String desc = h.getDescription();
        if (StringUtils.isNotBlank(desc)) {
            out.println(desc);
        } else {
            String comma = "";
            for (final Sample s : samples) {
                out.print(comma);
                out.print(s.getName());
                comma = ", ";
            }
            out.println();
        }
    }

    private static void writeSources(PrintWriter out, Set<Source> sources) {
        out.print("!Sample_source_name=");
        String del = "";

        for (final Source source : sources) {
            out.print(del);
            out.print(source.getName());
            del = "; ";
        }
        out.println();
    }

    static void collectBioMaterials(Hybridization hyb, Set<Source> sources, Set<Sample> samples, Set<Extract> extracts,
            Set<LabeledExtract> labeledExtracts) {
        for (final LabeledExtract le : hyb.getLabeledExtracts()) {
            labeledExtracts.add(le);
            for (final Extract ex : le.getExtracts()) {
                extracts.add(ex);
                for (final Sample sa : ex.getSamples()) {
                    samples.add(sa);
                    for (final Source src : sa.getSources()) {
                        sources.add(src);
                    }
                }
            }
        }
    }

    private static void collectAllProtocols(Set<? extends ProtocolApplicable> nodes,
            Collection<ProtocolApplication> dest) {
        for (final ProtocolApplicable pa : nodes) {
            dest.addAll(pa.getProtocolApplications());
        }
    }

    private static Iterable<ProtocolApplication> getAllProtocols(Set<? extends ProtocolApplicable> nodes) {
        final List<ProtocolApplication> l = new ArrayList<ProtocolApplication>();
        collectAllProtocols(nodes, l);
        return l;
    }

    private static void writeProviders(Set<Source> sources, PrintWriter out) {
        final Set<AbstractContact> all = new HashSet<AbstractContact>();
        for (final Source source : sources) {
            all.addAll(source.getProviders());
        }
        for (final AbstractContact c : all) {
            out.print("!Sample_biomaterial_provider=");
            out.println(((Organization) c).getName());
        }
    }

    private static void writeMaterialTypes(Set<Extract> extracts, Set<LabeledExtract> les, PrintWriter out) {
        final Set<Term> all = new TreeSet<Term>(ENTITY_COMPARATOR);
        for (final Extract e : extracts) {
            addIgnoreNull(all, e.getMaterialType());
        }
        if (all.isEmpty()) {
            for (final LabeledExtract e : les) {
                addIgnoreNull(all, e.getMaterialType());
            }
        }
        for (final Term mt : all) {
            out.print("!Sample_molecule=");
            final String m = translateMaterial(mt);
            out.println(m);
        }
    }

    private static void
            writeProtocolsForData(Iterable<? extends AbstractArrayData> data, String label, PrintWriter out) {
        final List<ProtocolApplication> l = new ArrayList<ProtocolApplication>();
        for (final AbstractArrayData d : data) {
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
            final Set<String> uniqueEntries = new HashSet<String>();
            final StringBuilder sb = new StringBuilder();
            for (final ProtocolApplication pa : selected) {
                sb.setLength(0);
                sb.append(pa.getProtocol().getName());
                if (StringUtils.isNotBlank(pa.getProtocol().getDescription())) {
                    sb.append(':').append(pa.getProtocol().getDescription());
                }
                final String entry = sb.toString();
                if (uniqueEntries.add(entry)) {
                    out.print(semi);
                    out.print(entry);
                    semi = "\"; \"";
                }
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

    private static void writeCharacteristics(Set<? extends AbstractBioMaterial> bios, PrintWriter out,
            Set<String> alreadyWritten) {
        for (final AbstractBioMaterial bio : bios) {
            for (final AbstractCharacteristic c : bio.getCharacteristics()) {
                writeCharacteristics(c.getCategory().getName(), c.getDisplayValue(), out, alreadyWritten);
            }
            writeExtraCharacteristics(ExperimentOntologyCategory.ORGANISM_PART, bio.getTissueSite(), out,
                    alreadyWritten);
            writeExtraCharacteristics(ExperimentOntologyCategory.DISEASE_STATE, bio.getDiseaseState(), out,
                    alreadyWritten);
            writeExtraCharacteristics(ExperimentOntologyCategory.CELL_TYPE, bio.getCellType(), out, alreadyWritten);
            writeExtraCharacteristics(ExperimentOntologyCategory.EXTERNAL_ID, bio.getExternalId(), out, alreadyWritten);
        }
    }

    private static void writeExtraCharacteristics(ExperimentOntologyCategory key, Term value, PrintWriter out,
            Set<String> alreadyWritten) {
        writeExtraCharacteristics(key, value == null ? null : value.getValue(), out, alreadyWritten);
    }

    private static void writeExtraCharacteristics(ExperimentOntologyCategory key, String value, PrintWriter out,
            Set<String> alreadyWritten) {
        writeCharacteristics(key.getCategoryName(), value, out, alreadyWritten);
    }

    private static void writeCharacteristics(String key, String value, PrintWriter out, Set<String> alreadyWritten) {
        if (StringUtils.isNotBlank(value)) {
            final String entry = "!Sample_characteristics=" + key + ':' + value;
            if (alreadyWritten.add(entry)) {
                out.println(entry);
            }
        }
    }

    private static void writeOrganism(Set<Source> sources, Set<Sample> samples, Experiment exp, PrintWriter out) {
        final Set<Organism> all = new TreeSet<Organism>(ENTITY_COMPARATOR);
        for (final Source source : sources) {
            addIgnoreNull(all, source.getOrganism());
        }
        for (final Sample sample : samples) {
            addIgnoreNull(all, sample.getOrganism());
        }
        if (all.isEmpty()) {
            all.add(exp.getOrganism());
        }
        for (final Organism o : all) {
            out.print("!Sample_organism=");
            out.println(o.getScientificName());
        }
    }

    private static void writeData(Hybridization h, PrintWriter out) {
        for (final RawArrayData rad : h.getRawDataCollection()) {
            out.print("!Sample_supplementary_file=");
            out.println(rad.getDataFile().getName());
        }
        for (final DerivedArrayData dad : h.getDerivedDataCollection()) {
            if (GeoSoftExporterBean.AFFYMETRIX_CHP_TYPE_NAME.equals(dad.getDataFile().getFileType().getName())) {
                out.print("!Sample_table=");
                out.println(dad.getDataFile().getName());
            } else {
                out.print("!Sample_supplementary_file=");
                out.println(dad.getDataFile().getName());
            }
        }
    }

    private static void writeExperimentContact(ExperimentContact c, PrintWriter out) {
        final Person p = c.getPerson();
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
            for (final Term edt : experiment.getExperimentDesignTypes()) {
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
     * From Apache commons-collection.
     * 
     * Adds an element to the collection unless the element is null.
     * 
     * @param collection the collection to add to, must not be null
     * @param object the object to add, if null it will not be added
     * @return true if the collection changed
     * @throws NullPointerException if the collection is null
     * @since Commons Collections 3.2
     */
    private static <T> boolean addIgnoreNull(Collection<? super T> collection, T object) {
        return (object == null ? false : collection.add(object));
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

        @Override
        public boolean apply(ProtocolApplication pa) {
            return this.value.equals(pa.getProtocol().getType().getValue());
        }
    }

}
