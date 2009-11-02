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

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Export valid Experiments to a GEO SOFT format.
 * Note: This class doesn't need to be an EJB.
 *
 * @author gax
 * @since 2.3.1
 */
@Local(GeoSoftExporter.class)
@Stateless
public class GeoSoftExporterBean implements GeoSoftExporter {

    private static final Logger LOG = Logger.getLogger(GeoSoftExporterBean.class);
    private static final String AFFYMETRIX = "Affymetrix";
    // CHECKSTYLE:OFF magic numbers
    private static final long MAX_ZIP_SIZE = 1024L * 1024L * (768L + 1024L); // ~ 1.8GB
    private static final int ONE_KB = 1024;
    // CHECKSTYLE:ON

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<String> validateForExport(Experiment experiment) {

        List<String> errors = new ArrayList<String>();
        checkArrayDesigns(errors, experiment);

        for (Source source : experiment.getSources()) {
            Hybridization hyb = checkSingleChannel(errors, source);
            if (hyb == null) {
                break;
            }
            LabeledExtract labeledExtract = hyb.getLabeledExtracts().iterator().next();
            Extract extract = labeledExtract.getExtracts().iterator().next();
            Sample sample = extract.getSamples().iterator().next();

            checkRawData(errors, hyb);
            checkDerivedDataFileType(errors, hyb);

            checkProtocol(errors, sample.getProtocolApplications(), "extract", "extraction");
            checkProtocol(errors, extract.getProtocolApplications(), "labeling");
            checkProtocol(errors, labeledExtract.getProtocolApplications(), "hybridization");
            checkProtocol(errors, hyb.getProtocolApplications(), "scan");
            checkDataProcessionProtocol(errors, hyb.getRawDataCollection());

            checkCharOrFactorValue(errors, hyb, labeledExtract, extract, sample, source);
            checkLabeledExtract(errors, labeledExtract, extract);
        }

        return errors;
    }

    private void checkArrayDesigns(List<String> errors, Experiment experiment) {
        for (ArrayDesign ad : experiment.getArrayDesigns()) {
            //* The array provider should be Affymetrix.
            if (!AFFYMETRIX.equals(ad.getProvider().getName())) {
                errors.add(AFFYMETRIX + " is not the provider for array design " + ad.getName());
            }
            //* All array designs associated with the experiment must be ones for which the System has the GEO
            //  accession.
            if (StringUtils.isBlank(ad.getGeoAccession())) {
                errors.add("Array design " + ad.getName() + " has no GEO accession");
            }
        }
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void checkCharOrFactorValue(List<String> errors, Hybridization hyb, LabeledExtract labeledExtract,
            Extract extract, Sample sample, Source source) {
        //* There must be at least 1 characteristic or factor value that is present in every
        //  biomaterial-hybridization chain.
        if (hyb.getFactorValues().isEmpty()
                && !hasCharacteristics(labeledExtract)
                && !hasCharacteristics(extract)
                && !hasCharacteristics(sample)
                && !hasCharacteristics(source)) {
            errors.add("Hybridization " + hyb.getName()
                    + " and associated biomaterials must have at least one characteristic or factor value");
        }
    }

    private static boolean hasCharacteristics(AbstractBioMaterial bio) {
        return !bio.getCharacteristics().isEmpty()
                || bio.getTissueSite() != null
                || bio.getDiseaseState() != null
                || bio.getCellType() != null
                || StringUtils.isNotBlank(bio.getExternalId());
    }

    private void checkDerivedDataFileType(List<String> errors, Hybridization hyb) {
        //* Every hybridization must have a derived data file of type AFFYMETRIX_CHP.
        for (DerivedArrayData dad : hyb.getDerivedDataCollection()) {
            if (FileType.AFFYMETRIX_CHP == dad.getDataFile().getFileType()) {
                return;
            }
        }
        errors.add("Hybridization " + hyb.getName() + " must have a derived data file of type "
                        + FileType.AFFYMETRIX_CHP);
    }

    private void checkLabeledExtract(List<String> errors, LabeledExtract labeledExtract, Extract extract) {
        //* For every chain, the Material Type of the extract or labeled extract must be present.
        if (labeledExtract.getMaterialType() == null && extract.getMaterialType() == null) {
            errors.add("Material Type not set on Labeled Extract " + labeledExtract.getName() + " or Extract "
                    + extract.getName());
        }
        if (labeledExtract.getLabel() == null) {
            errors.add("Labeled Extract " + labeledExtract.getName() + " must have a label");
        }
    }

    private void checkRawData(List<String> errors, Hybridization hyb) {
        //* Every hybridization must have at least one raw data file.
        if (hyb.getRawDataCollection().isEmpty()) {
            errors.add("Hybridization " + hyb.getName() + " must have at least one raw data file");
        }
    }

    /**
     *
     * @return the end of the link.
     */
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private Object checkSinglelink(List<String> errors, Object start, String... properties) {
        try {
            Object o = start;
            for (String p : properties) {
                Set s = (Set) PropertyUtils.getProperty(o, p);
                if (s.size() != 1) {
                    errors.add("Not a single-channel experiemnt (" + o.getClass().getSimpleName()
                          + " " + getName(o) + " must have one " + StringUtils.chop(p) + " but has " + s.size() + ")");
                    return null;
                }
                o = s.iterator().next();
            }
            return o;
        } catch (Exception ex) {
            LOG.error(ex);
            throw new RuntimeException(ex);
        }
    }

    private static String getName(Object o) {
        try {
            return (String) PropertyUtils.getProperty(o, "name");
        } catch (Exception ex) {
            return String.valueOf(o);
        }
    }

    /**
     * @return the Hybridization if the link can be verified.
     */
    private Hybridization checkSingleChannel(List<String> errors, Source source) {
        //* It must be a single-channel experiment, i.e., there must be one-to-one links at every point of the
        //  Source-Sample-Ext-LE-Hyb chain.
        Hybridization hyb = (Hybridization) checkSinglelink(errors, source,
                "samples", "extracts", "labeledExtracts", "hybridizations");
        if (hyb != null) {
            Object s = checkSinglelink(errors, hyb, "labeledExtracts", "extracts", "samples", "sources");
            if (s == null) {
                return null;
            }
        }
        return hyb;
    }

    private void checkProtocol(List<String> errors, List<ProtocolApplication> protocolApplications,
            String... protocols) {
        for (ProtocolApplication pa : protocolApplications) {
            String pType = pa.getProtocol().getType().getValue();
            for (String p : protocols) {
                if (pType.equalsIgnoreCase(p)) {
                    return;
                }
            }
        }
        errors.add("Missing protocol (one of " + Arrays.asList(protocols) + " needed)");
    }

    private void checkDataProcessionProtocol(List<String> errors, Set<RawArrayData> rawDataCollection) {
        for (RawArrayData rad : rawDataCollection) {
            if (rad.getProtocolApplications().isEmpty()) {
                errors.add("Missing data procession protocol for raw array data " + rad.getName());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Packaginginfo> getAvailablePackagingInfos(Project project) {
        List<Packaginginfo> infos = new ArrayList<Packaginginfo>();
        Experiment experiment = project.getExperiment();
        String name = experiment.getPublicIdentifier() + Packaginginfo.PackagingMethod.TGZ.getExtension();
        Packaginginfo.PackagingMethod method = Packaginginfo.PackagingMethod.TGZ;
        infos.add(new Packaginginfo(name, method));

        long size = getEstimatedPackageSize(experiment);
        if (size < MAX_ZIP_SIZE) {
            name = experiment.getPublicIdentifier() + Packaginginfo.PackagingMethod.ZIP.getExtension();
            method = Packaginginfo.PackagingMethod.ZIP;
            infos.add(new Packaginginfo(name, method));
        }
        return infos;
    }

     /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void export(Project project, String permaLinkUrl, Packaginginfo.PackagingMethod method,
            OutputStream out) throws IOException {

        Experiment experiment = project.getExperiment();
        boolean addReadMe = method == Packaginginfo.PackagingMethod.TGZ;
        ArchiveOutputStream arOut;

        switch(method) {
            case ZIP:
                List<Packaginginfo> infos = getAvailablePackagingInfos(project);
                boolean zip = false;
                for (Packaginginfo pi : infos) {
                    zip |= pi.getMethod() == Packaginginfo.PackagingMethod.ZIP;
                }
                if (!zip) {
                    throw new IllegalArgumentException("experiment files are too large for a standard ZIP package");
                }
                arOut = new ZipArchiveOutputStream(out);
                break;
            case TGZ:
                GzipCompressorOutputStream gz = new GzipCompressorOutputStream(out);
                arOut = new TarArchiveOutputStream(gz);
                break;
            default:
                throw new UnsupportedOperationException(method.name());
        }
        try {
            exportArchive(experiment, permaLinkUrl, addReadMe, arOut);
        } finally {
            IOUtils.closeQuietly(arOut);
        }
    }

    private void exportArchive(Experiment experiment, String permaLinkUrl, boolean addReadme, ArchiveOutputStream ar)
            throws IOException {
        if (!validateForExport(experiment).isEmpty()) {
            throw new IllegalArgumentException("experiment not valid for export");
        }
        try {
            generateSoftFile(experiment, permaLinkUrl, ar);
            addDataFiles(experiment, ar);
            if (addReadme) {
                addReadmeFile(ar);
            }
        } catch (Exception err) {
            LOG.error("failed to create archive", err);
            handleException(err, ar);
        }
    }

    private void generateSoftFile(Experiment experiment, String permaLinkUrl, ArchiveOutputStream zout)
            throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(bout);
        GeoSoftFileWriterUtil.writeSoftFile(experiment, permaLinkUrl, out);
        out.close();

        ArchiveEntry ae = createArchiveEntry(zout, experiment.getPublicIdentifier() + ".soft.txt", bout.size());
        zout.putArchiveEntry(ae);        
        bout.writeTo(zout);
        zout.closeArchiveEntry();
    }

    
    private long getEstimatedPackageSize(Experiment experiment) {
        long size = 0;
        for (Hybridization h : experiment.getHybridizations()) {
            for (RawArrayData rad : h.getRawDataCollection()) {
                size += rad.getDataFile().getCompressedSize();
            }
            for (DerivedArrayData dad : h.getDerivedDataCollection()) {
                size += dad.getDataFile().getCompressedSize();
            }
        }
        // estimate GEO SOFT file size and zip entry overhead.
        size += experiment.getHybridizations().size() * ONE_KB;
        return size;
    }

    private void addDataFiles(Experiment experiment, ArchiveOutputStream zout) throws IOException {
        for (Hybridization h : experiment.getHybridizations()) {
            addDataFiles(h.getRawDataCollection(), zout);
            addDataFiles(h.getDerivedDataCollection(), zout);
        }
    }

    private void addDataFiles(Set<? extends AbstractArrayData> dataCollection, ArchiveOutputStream zout)
            throws IOException {
        for (AbstractArrayData aad : dataCollection) {
            CaArrayFile f = aad.getDataFile();
            ArchiveEntry ae = createArchiveEntry(zout, f.getName(), f.getUncompressedSize());
            zout.putArchiveEntry(ae);
            InputStream is = aad.getDataFile().readContents();
            IOUtils.copy(is, zout);
            is.close();
            zout.closeArchiveEntry();
        }
    }

    /**
     * This method creates an ArchiveEntry w/o the need of a File required by
     * ArchiveOutputStream.createArchiveEntry(File inputFile, String entryName).
     */
    private static ArchiveEntry createArchiveEntry(ArchiveOutputStream aos, String name, long size) {
        if (aos instanceof TarArchiveOutputStream) {
            TarArchiveEntry te = new TarArchiveEntry(name);
            te.setSize(size);
            return te;
        } else if (aos instanceof ZipArchiveOutputStream) {
            ZipArchiveEntry ze = new ZipArchiveEntry(name);
            ze.setSize(size);
            return ze;
        }
        throw new UnsupportedOperationException("unsupported archive " + aos.getClass());
    }

    private void addReadmeFile(ArchiveOutputStream ar) throws IOException {
        InputStream is = GeoSoftExporterBean.class.getResourceAsStream("README.txt");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(is, baos);
        is.close();

        ArchiveEntry ae = createArchiveEntry(ar, "README.txt", baos.size());
        ar.putArchiveEntry(ae);
        baos.writeTo(ar);
        ar.closeArchiveEntry();
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private void handleException(Exception err, ArchiveOutputStream ar) throws IOException {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            PrintWriter out = new PrintWriter(bout);
            out.println("An error occured during the creation of this archive.");
            out.println("It may be malformed, and unacceptable by GEO SOFT");
            out.println();
            err.printStackTrace(out);
            out.close();

            ArchiveEntry ae = createArchiveEntry(ar, "ERROR.txt", bout.size());
            ar.putArchiveEntry(ae);
            bout.writeTo(ar);
            ar.closeArchiveEntry();
            ar.finish();
        } catch (Exception e) {
            LOG.error(e);
        } finally {
            if (err instanceof IOException) {
                throw (IOException) err;
            } else if (err instanceof RuntimeException) {
                throw (RuntimeException) err;
            } else {
                throw new RuntimeException("failed to create archive", err);
            }
        }
    }
}
