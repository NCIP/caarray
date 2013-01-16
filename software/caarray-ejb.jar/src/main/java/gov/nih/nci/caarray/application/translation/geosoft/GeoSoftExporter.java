//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.geosoft;

import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * Export valid Experiments to a GEO SOFT format.
 * @author gax
 * @since 2.3.1
 */
public interface GeoSoftExporter {
/**
     * The default JNDI name to use to lookup <code>GeoSoftExporter</code>.
     */
    String JNDI_NAME = "caarray/GeoSoftExporterBean/local";

    /**
     * Verify if an experiment can be exported into GEO SOFT format.
     * @param experiment experiment to validate.
     * @return messages descibing validation errors. An empty list indicates that there were no errors.
     */
    List<String> validateForExport(Experiment experiment);

    /**
     * Determine how the experiment can be pachaged.
     * @param experiment the experiment to package.
     * @return information about the possible packaging formats.
     */
    List<PackagingInfo> getAvailablePackagingInfos(Project experiment);

    /**
     * Export an to a GEO SOFT format, packaged in an archive.
     * If an error occures during the wrriting of the archive stream, an attempt will be made to add a final entry
     * named ERROR.txt that contains detais about the error, and also indicating that the archive is incomplete and/or
     * malformed.
     * @param experiment experiment to export.
     * @param permaLinkUrl permanent URL of the experiment.
     * @param method packaging method or format.
     * @param out stream to write to.
     * @throws IOException if writing to the stream fails.
     */
    void export(Project experiment, String permaLinkUrl, PackagingInfo.PackagingMethod method, OutputStream out)
            throws IOException;

    /**
     * Export an to a GEO SOFT Info file for an experiment.
     * @param project experiment to export.
     * @param permaLinkUrl permanent URL of the experiment.
     * @param out stream to write to.
     * @throws IOException if writing to the stream fails.
     */
    void writeGeoSoftFile(Project project, String permaLinkUrl, PrintWriter out) throws IOException;

}
