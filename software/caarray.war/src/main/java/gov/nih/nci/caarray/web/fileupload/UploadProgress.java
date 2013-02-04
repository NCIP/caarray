//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.fileupload;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

/**
 * Action class for retrieving current progress of an upload and returning it as JSON data.
 * @author kokotovd
 */
public class UploadProgress {
    private static final Logger LOG = Logger.getLogger(UploadProgress.class);

    /**
     * {@inheritDoc}
     */
    public String execute() {
        ProgressMonitor monitor = MonitoredMultiPartRequest.getProgressMonitor(ServletActionContext.getRequest());
        JSONObject json = new JSONObject();
        if (monitor != null) {
            json.accumulate("itemNumber", monitor.getItemNumber());
            json.accumulate("bytesSent", monitor.getBytesRead());
            json.accumulate("bytesTotal", monitor.getBytesLength());
            json.accumulate("percentComplete", monitor.percentComplete());
            if (!monitor.isStillProcessing() || monitor.isAborted()) {
                json.accumulate("aborted", true);
            } else {
                json.accumulate("aborted", false);
            }
        } else {
            json.accumulate("itemNumber", -1);
            json.accumulate("bytesSent", "0");
            json.accumulate("bytesTotal", "0");
            json.accumulate("percentComplete", "0");
            json.accumulate("aborted", false);
        }
        try {
            ServletActionContext.getResponse().getWriter().write(json.toString());
        } catch (IOException e) {
            LOG.warn("Could not write JSON progress report: " + json.toString());
        }
        return null;
    }
}
