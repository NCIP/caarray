//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;

import java.io.File;

import affymetrix.fusion.cdf.FusionCDFData;

/**
 * Provides common Affymetrix CDF file handling functionality.
 */
final class CdfReader {
    private FusionCDFData cdfData;

    CdfReader(File cdfFile) throws PlatformFileReadException {
        if (cdfFile == null || !cdfFile.exists()) {
            throw new IllegalArgumentException("CDF file must exist.");
        }
        
        cdfData = new FusionCDFData();
        cdfData.setFileName(cdfFile.getAbsolutePath());
        
        boolean opened = false;
        try {
            opened = open();
        } catch (RuntimeException e) {
            // some CDF files are not supported by the AffxFusion jar and trying to read the files results in a
            // runtime exception being thrown - see GForge defect 11922 at
            // https://gforge.nci.nih.gov/tracker/?group_id=305&atid=1344&func=detail&aid=11922
            // we catch the exception to handle it more gracefully
            throw new PlatformFileReadException(cdfFile, "Unsupported file type", e);
        }
        if (!opened) {
            PlatformFileReadException readException =
                new PlatformFileReadException(cdfFile, cdfData.getError());
            close();
            throw readException;
        }
    }

    private boolean open() {
        boolean success = cdfData.read();
        if (!success) {
            // This invokes a fileChannel.map call that could possibly fail due to a bug in Java
            // that causes previous memory mapped files to not be released until after GC.  So
            // we force a gc here to ensure that is not the cause of our problems
            System.gc();
            cdfData.clear();
            success = cdfData.read();
        }
        return success;
    }

    /**
     * Closes the reader.
     */
    void close() {
        // See development tracker issue #9735 and dev tracker #10925 for details on why System.gc() used here
        if (cdfData != null) {
            cdfData.clear();
            cdfData = null;
            System.gc();
        }
    }

    /**
     * @return the cdfData
     */
    FusionCDFData getCdfData() {
        return cdfData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
