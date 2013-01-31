//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;

import com.opensymphony.xwork2.inject.Inject;

/**
 * Implementation of the Struts2 Multipart request that allows for progress monitoring. Based on work by Dave Casserly
 * at http://www.davidjc.com/ajaxfileupload/demo!input.action
 *
 * @author kokotovd
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class MonitoredMultiPartRequest implements MultiPartRequest {
    private static final Logger LOG = Logger.getLogger(MonitoredMultiPartRequest.class);

    private static final String UPLOAD_ID_ATTRIBUTE = "__multipart_upload_id";

    private final Map<String, List<FileItem>> files = new HashMap<String, List<FileItem>>();
    private final Map<String, List<String>> params = new HashMap<String, List<String>>();
    private final List<String> errors = new ArrayList<String>();
    private long maxSize;

    /**
     * Sets the maximum size for an upload allowed.
     * @param maxSize the max size for the upload
     */
    @Inject(StrutsConstants.STRUTS_MULTIPART_MAXSIZE)
    public void setMaxSize(String maxSize) {
        this.maxSize = Long.parseLong(maxSize);
    }

    /**
     * Retrieves the identifier for the current upload request.
     * @param request the current HTTP request
     * @return an identifier used to refer to this upload when checking for progress
     */
    public static String getUploadKey(HttpServletRequest request) {
        String uploadId = request.getParameter(UPLOAD_ID_ATTRIBUTE);
        if (uploadId == null) {
            uploadId = StringUtils.defaultString((String) request.getAttribute(UPLOAD_ID_ATTRIBUTE));
        }
        return ProgressMonitor.SESSION_PROGRESS_MONITOR + uploadId;
    }

    /**
     * Returns the progress monitor.
     * @param request the current HTTP request
     * @return the progress monitor
     */
    public static ProgressMonitor getProgressMonitor(HttpServletRequest request) {
        String uploadKey = getUploadKey(request);
        return (ProgressMonitor) ServletActionContext.getRequest().getSession().getAttribute(uploadKey);
    }

    /**
     * Release the progress monitor for the upload associated with the given request.
     *
     * @param request the current HTTP request
     */
    public static void releaseProgressMonitor(HttpServletRequest request) {
        String uploadKey = getUploadKey(request);
        request.getSession().removeAttribute(uploadKey);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked", "PMD.CyclomaticComplexity" })
    public void parse(HttpServletRequest servletRequest, String saveDir) throws IOException {
        DiskFileItemFactory fac = new DiskFileItemFactory();
        fac.setSizeThreshold(0);
        if (saveDir != null) {
            fac.setRepository(new File(saveDir));
        }
        ProgressMonitor monitor = null;
        try {
            ServletFileUpload upload = new ServletFileUpload(fac);
            upload.setSizeMax(maxSize);
            monitor = new ProgressMonitor();
            upload.setProgressListener(monitor);
            String uploadKey = getUploadKey(servletRequest);
            servletRequest.getSession().setAttribute(uploadKey, monitor);
            List<FileItem> items = (List<FileItem>) upload.parseRequest(createRequestContext(servletRequest));
            for (FileItem item : items) {
                LOG.debug((new StringBuilder()).append("Found item ").append(item.getFieldName()).toString());
                if (item.isFormField()) {
                    LOG.debug("Item is a normal form field");
                    List<String> values = params.get(item.getFieldName());
                    if (values == null) {
                        values = new ArrayList<String>();
                        params.put(item.getFieldName(), values);
                    }
                    String charset = servletRequest.getCharacterEncoding();
                    values.add(charset != null ? item.getString(charset) : item.getString());
                } else {
                    LOG.debug("Item is a file upload");
                    List<FileItem> values = files.get(item.getFieldName());
                    if (values == null) {
                        values = new ArrayList<FileItem>();
                        files.put(item.getFieldName(), values);
                    }
                    values.add(item);
                }
            }

        } catch (FileUploadException e) {
            if (monitor != null) {
                monitor.abort();
            }
            LOG.warn("Error processing upload", e);
            errors.add(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public Enumeration<String> getFileParameterNames() {
        return Collections.enumeration(files.keySet());
    }

    private <T> T[] transformFileItemsForField(String fieldName, T[] emptyArray, Transformer t) {
        List<FileItem> items = files.get(fieldName);

        if (items == null) {
            return null;
        }

        List<T> result = new ArrayList<T>(items.size());
        CollectionUtils.collect(items, t, result);
        return result.toArray(emptyArray);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getContentType(String fieldName) {
        return transformFileItemsForField(fieldName, ArrayUtils.EMPTY_STRING_ARRAY, new Transformer() {
            public Object transform(Object o) {
                return ((FileItem) o).getContentType();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public File[] getFile(String fieldName) {
        return transformFileItemsForField(fieldName, new File[0], new Transformer() {
            public Object transform(Object o) {
                return ((DiskFileItem) o).getStoreLocation();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public String[] getFileNames(String fieldName) {
        return transformFileItemsForField(fieldName, ArrayUtils.EMPTY_STRING_ARRAY, new Transformer() {
            public Object transform(Object o) {
                return getCanonicalName(((DiskFileItem) o).getName());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public String[] getFilesystemName(String fieldName) {
        return transformFileItemsForField(fieldName, ArrayUtils.EMPTY_STRING_ARRAY, new Transformer() {
            public Object transform(Object o) {
                return ((DiskFileItem) o).getStoreLocation().getName();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public String getParameter(String name) {
        List<String> v = params.get(name);
        if (v != null && !v.isEmpty()) {
            return v.get(0);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    /**
     * {@inheritDoc}
     */
    public String[] getParameterValues(String name) {
        List<String> v = params.get(name);
        if (v != null && !v.isEmpty()) {
            return v.toArray(new String[v.size()]);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List getErrors() {
        return errors;
    }

    private String getCanonicalName(String filename) {
        int forwardSlash = filename.lastIndexOf('/');
        int backwardSlash = filename.lastIndexOf("\\");
        String canonicalName = filename;
        if (forwardSlash != -1 && forwardSlash > backwardSlash) {
            canonicalName = filename.substring(forwardSlash + 1, filename.length());
        } else if (backwardSlash != -1 && backwardSlash >= forwardSlash) {
            canonicalName = filename.substring(backwardSlash + 1, filename.length());
        }

        return canonicalName;
    }

    private RequestContext createRequestContext(final HttpServletRequest req) {
        return new RequestContext() {
            public String getCharacterEncoding() {
                return req.getCharacterEncoding();
            }

            public String getContentType() {
                return req.getContentType();
            }

            public int getContentLength() {
                return req.getContentLength();
            }

            public InputStream getInputStream() throws IOException {
                return req.getInputStream();
            }
        };
    }
}
