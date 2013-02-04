//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.translation.geosoft;

import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.application.fileaccess.FileAccessUtils;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.injection.InjectionInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Export valid Experiments to a GEO SOFT format. Note: This class doesn't need to be an EJB.
 *
 * @author gax
 * @since 2.3.1
 */
@Local(GeoSoftExporter.class)
@Stateless
@Interceptors({ExceptionLoggingInterceptor.class, InjectionInterceptor.class })
public class GeoSoftExporterBean implements GeoSoftExporter {
    private static final Logger LOG = Logger.getLogger(GeoSoftExporterBean.class);
    private static final String AFFYMETRIX_PROVIDER = "Affymetrix";
    static final String AFFYMETRIX_CHP_TYPE_NAME = "AFFYMETRIX_CHP";

    // CHECKSTYLE:OFF magic numbers
    private static final int ONE_KB = 1024;

    // CHECKSTYLE:ON

    private FileAccessUtils fileAccessHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<String> validateForExport(Experiment experiment) {

        final List<String> errors = new ArrayList<String>();
        final boolean stop = checkArrayDesigns(errors, experiment);
        if (stop) {
            return errors;
        }

        final Set<String> protocolErrors = new HashSet<String>();
        for (final Hybridization hyb : experiment.getHybridizations()) {
            final Set<Source> sources = new HashSet<Source>();
            final Set<Sample> samples = new HashSet<Sample>();
            final Set<Extract> extracts = new HashSet<Extract>();
            final Set<LabeledExtract> labeledExtracts = new HashSet<LabeledExtract>();
            GeoSoftFileWriterUtil.collectBioMaterials(hyb, sources, samples, extracts, labeledExtracts);

            checkRawData(errors, hyb);
            checkDerivedDataFileType(errors, hyb);

            checkBioProtocol(protocolErrors, samples, "nucleic_acid_extraction");
            checkBioProtocol(protocolErrors, extracts, "labeling");
            checkBioProtocol(protocolErrors, labeledExtracts, "hybridization");
            checkProtocol(protocolErrors, hyb.getProtocolApplications(), "scan", "image_acquisition");
            checkDataProcessingProtocol(protocolErrors, hyb.getRawDataCollection());
            checkCharOrFactorValue(errors, hyb, labeledExtracts, extracts, samples, sources);
            checkLabeledExtract(errors, labeledExtracts, extracts);

        }
        errors.addAll(protocolErrors);

        return errors;
    }

    /**
     * @return true if this experiment's design provider is not Affy (not beed to do more validations).
     */
    private boolean checkArrayDesigns(List<String> errors, Experiment experiment) {
        if (experiment.getArrayDesigns().isEmpty()) {
            errors.add("No (" + AFFYMETRIX_PROVIDER + ") array design specified");
            return true;
        }
        for (final ArrayDesign ad : experiment.getArrayDesigns()) {
            // * The array provider should be Affymetrix.
            if (!AFFYMETRIX_PROVIDER.equals(ad.getProvider().getName())) {
                errors.add(AFFYMETRIX_PROVIDER + " is not the provider for array design " + ad.getName());
            }
            if (!errors.isEmpty()) {
                return true;
            }
            // * All array designs associated with the experiment must be ones for which the System has the GEO
            // accession.
            if (StringUtils.isBlank(ad.getGeoAccession())) {
                errors.add("Array design " + ad.getName() + " has no GEO accession");
            }
        }
        return false;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void checkCharOrFactorValue(List<String> errors, Hybridization hyb, Set<LabeledExtract> labeledExtracts,
            Set<Extract> extracts, Set<Sample> samples, Set<Source> sources) {
        // * There must be at least 1 characteristic or factor value that is present in every
        // biomaterial-hybridization chain.
        if (hyb.getFactorValues().isEmpty() && !hasCharacteristics(labeledExtracts) && !hasCharacteristics(extracts)
                && !hasCharacteristics(samples) && !hasCharacteristics(sources)) {
            errors.add("Hybridization " + hyb.getName()
                    + " and associated biomaterials must have at least one characteristic or factor value");
        }
    }

    @SuppressWarnings("empty-statement")
    private static boolean hasCharacteristics(Set<? extends AbstractBioMaterial> bios) {
        for (final AbstractBioMaterial bio : bios) {
            if (!bio.getCharacteristics().isEmpty() || bio.getTissueSite() != null || bio.getDiseaseState() != null
                    || bio.getCellType() != null || StringUtils.isNotBlank(bio.getExternalId())) {
                return true;
            }
        }
        return false;
    }

    private void checkDerivedDataFileType(List<String> errors, Hybridization hyb) {
        // * Every hybridization must have a derived data file of type AFFYMETRIX_CHP.
        for (final DerivedArrayData dad : hyb.getDerivedDataCollection()) {
            if (AFFYMETRIX_CHP_TYPE_NAME.equals(dad.getDataFile().getFileType().getName())) {
                return;
            }
        }
        errors.add("Hybridization " + hyb.getName() + " must have a derived data file of type "
                + AFFYMETRIX_CHP_TYPE_NAME);
    }

    private void checkLabeledExtract(List<String> errors, Set<LabeledExtract> labeledExtracts, Set<Extract> extracts) {
        // * For every chain, the Material Type of the extract or labeled extract must be present.
        boolean foundMaterialType = false;
        for (final LabeledExtract le : labeledExtracts) {
            if (le.getMaterialType() != null) {
                foundMaterialType = true;
            }
            if (le.getLabel() == null) {
                errors.add("Labeled Extract " + le.getName() + " must have a label");
            }
        }
        for (final Extract e : extracts) {
            if (e.getMaterialType() != null) {
                foundMaterialType = true;
                break;
            }
        }
        if (!foundMaterialType) {
            errors.add("Material Type not set on Labeled Extract or Extract");
        }
    }

    private void checkRawData(List<String> errors, Hybridization hyb) {
        // * Every hybridization must have at least one raw data file.
        if (hyb.getRawDataCollection().isEmpty()) {
            errors.add("Hybridization " + hyb.getName() + " must have at least one Raw Data File");
        }
    }

    private void checkProtocol(Set<String> errors, List<ProtocolApplication> protocolApps, String... protocols) {
        for (final ProtocolApplication pa : protocolApps) {
            final String pType = pa.getProtocol().getType().getValue();
            for (final String p : protocols) {
                if (pType.equalsIgnoreCase(p)) {
                    return;
                }
            }
        }
        errors.add("Missing protocol (one of " + Arrays.asList(protocols) + " needed)");
    }

    private void checkBioProtocol(Set<String> errors, Set<? extends AbstractBioMaterial> bios, String... protocols) {
        final List<ProtocolApplication> all = new ArrayList<ProtocolApplication>();
        for (final AbstractBioMaterial bio : bios) {
            all.addAll(bio.getProtocolApplications());
        }
        checkProtocol(errors, all, protocols);
    }

    private void checkDataProcessingProtocol(Set<String> errors, Set<RawArrayData> rawDataCollection) {
        for (final RawArrayData rad : rawDataCollection) {
            if (rad.getProtocolApplications().isEmpty()) {
                errors.add("Missing data processing protocol");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<PackagingInfo> getAvailablePackagingInfos(Project project) {
        final List<PackagingInfo> infos = new ArrayList<PackagingInfo>();
        final Experiment experiment = project.getExperiment();
        String name = experiment.getPublicIdentifier() + PackagingInfo.PackagingMethod.TGZ.getExtension();
        PackagingInfo.PackagingMethod method = PackagingInfo.PackagingMethod.TGZ;
        infos.add(new PackagingInfo(name, method));

        final long size = getEstimatedPackageSize(experiment);
        if (size < PackagingInfo.MAX_ZIP_SIZE) {
            name = experiment.getPublicIdentifier() + PackagingInfo.PackagingMethod.ZIP.getExtension();
            method = PackagingInfo.PackagingMethod.ZIP;
            infos.add(new PackagingInfo(name, method));
        }
        return infos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void writeGeoSoftFile(Project project, String permaLinkUrl, PrintWriter out) throws IOException {
        if (!validateForExport(project.getExperiment()).isEmpty()) {
            throw new IllegalArgumentException("experiment not valid for export");
        }
        GeoSoftFileWriterUtil.writeSoftFile(project.getExperiment(), permaLinkUrl, out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void export(Project project, String permaLinkUrl, PackagingInfo.PackagingMethod method, OutputStream out)
            throws IOException {

        final OutputStream closeShield = new CloseShieldOutputStream(out);
        final Experiment experiment = project.getExperiment();
        boolean addReadMe = false;
        if (method == PackagingInfo.PackagingMethod.TGZ) {
            addReadMe = true;
        } else {
            ensureZippable(project);
        }

        final ArchiveOutputStream arOut = method.createArchiveOutputStream(closeShield);
        try {
            exportArchive(experiment, permaLinkUrl, addReadMe, arOut);
        } finally {
            // note that the caller's stream is shielded from the close(),
            // but this is the only way to finish and flush the (gzip) stream.
            IOUtils.closeQuietly(arOut);
        }
    }

    private void ensureZippable(Project project) {
        final List<PackagingInfo> infos = getAvailablePackagingInfos(project);
        for (final PackagingInfo pi : infos) {
            if (pi.getMethod() == PackagingInfo.PackagingMethod.ZIP) {
                return;
            }
        }
        throw new IllegalArgumentException("experiment files are too large for a standard ZIP package");

    }

    @SuppressWarnings({"PMD.AvoidInstanceofChecksInCatchClause", "PMD.AvoidThrowingRawExceptionTypes" })
    private void exportArchive(Experiment experiment, String permaLinkUrl, boolean addReadme, ArchiveOutputStream ar)
            throws IOException {
        if (!validateForExport(experiment).isEmpty()) {
            throw new IllegalArgumentException("experiment not valid for export");
        }

        generateSoftFile(experiment, permaLinkUrl, ar);
        addDataFiles(experiment, ar);
        if (addReadme) {
            addReadmeFile(ar);
        }
    }

    private void generateSoftFile(Experiment experiment, String permaLinkUrl, ArchiveOutputStream zout)
            throws IOException {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final Writer w = new OutputStreamWriter(bout, "UTF-8");
        final PrintWriter out = new PrintWriter(w);
        GeoSoftFileWriterUtil.writeSoftFile(experiment, permaLinkUrl, out);
        out.close();

        final ArchiveEntry ae =
                this.fileAccessHelper.createArchiveEntry(zout, experiment.getPublicIdentifier() + ".soft.txt",
                        bout.size());
        zout.putArchiveEntry(ae);
        bout.writeTo(zout);
        zout.closeArchiveEntry();
    }

    private long getEstimatedPackageSize(Experiment experiment) {
        long size = 0;
        for (final Hybridization h : experiment.getHybridizations()) {
            for (final RawArrayData rad : h.getRawDataCollection()) {
                size += rad.getDataFile().getCompressedSize();
            }
            for (final DerivedArrayData dad : h.getDerivedDataCollection()) {
                size += dad.getDataFile().getCompressedSize();
            }
        }
        for (final CaArrayFile f : experiment.getProject().getUserVisibleSupplementalFiles()) {
            size += f.getCompressedSize();
        }

        // estimate GEO SOFT file size and zip entry overhead.
        size += experiment.getHybridizations().size() * ONE_KB;
        return size;
    }

    private void addDataFiles(Experiment experiment, ArchiveOutputStream zout) throws IOException {
        for (final Hybridization h : experiment.getHybridizations()) {
            addDataFiles(h.getRawDataCollection(), zout);
            addDataFiles(h.getDerivedDataCollection(), zout);
        }
        for (final CaArrayFile f : experiment.getProject().getUserVisibleSupplementalFiles()) {
            this.fileAccessHelper.addFileToArchive(f, zout);
        }
    }

    private void addDataFiles(Set<? extends AbstractArrayData> dataCollection, ArchiveOutputStream zout)
            throws IOException {
        for (final AbstractArrayData aad : dataCollection) {
            final CaArrayFile f = aad.getDataFile();
            this.fileAccessHelper.addFileToArchive(f, zout);
        }
    }

    private void addReadmeFile(ArchiveOutputStream ar) throws IOException {
        final InputStream is = GeoSoftExporterBean.class.getResourceAsStream("README.txt");
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(is, baos);
        is.close();

        final ArchiveEntry ae = this.fileAccessHelper.createArchiveEntry(ar, "README.txt", baos.size());
        ar.putArchiveEntry(ae);
        baos.writeTo(ar);
        ar.closeArchiveEntry();
    }

    /**
     * @param fileAccessHelper the fileAccessHelper to set
     */
    @Inject
    public void setFileAccessHelper(FileAccessUtils fileAccessHelper) {
        this.fileAccessHelper = fileAccessHelper;
    }
}
