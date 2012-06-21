/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
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
package gov.nih.nci.caarray.web.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

import com.google.common.collect.Lists;
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
    private static final String UPLOAD_SIZE_HEADER = "x-file-size";
    private static final String UPLOAD_NAME_HEADER = "x-file-name";
    private static final String UPLOAD_SIZE_FIELD = "chunkedFileSize";
    private static final String UPLOAD_NAME_FIELD = "chunkedFileName";

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
                    handleFormField(servletRequest, item);
                } else {
                    handleFileUpload(item);
                }
            }
            handleChunkedUploadHeaders(servletRequest);
        } catch (FileUploadException e) {
            if (monitor != null) {
                monitor.abort();
            }
            LOG.warn("Error processing upload", e);
            errors.add(e.getMessage());
        }
    }

    private void handleFormField(HttpServletRequest servletRequest, FileItem item)
            throws UnsupportedEncodingException {
        LOG.debug("Item is a normal form field");
        List<String> values = params.get(item.getFieldName());
        if (values == null) {
            values = new ArrayList<String>();
            params.put(item.getFieldName(), values);
        }
        String charset = servletRequest.getCharacterEncoding();
        values.add(charset != null ? item.getString(charset) : item.getString());
    }
    
    private void handleFileUpload(FileItem item) {
        LOG.debug("Item is a file upload");
        List<FileItem> values = files.get(item.getFieldName());
        if (values == null) {
            values = new ArrayList<FileItem>();
            files.put(item.getFieldName(), values);
        }
        values.add(item);
    }
    
    private void handleChunkedUploadHeaders(HttpServletRequest servletRequest) {
        String fileSize = servletRequest.getHeader(UPLOAD_SIZE_HEADER);
        if (fileSize != null) {
            params.put(UPLOAD_SIZE_FIELD, Lists.newArrayList(fileSize));
        }
        String fileName = servletRequest.getHeader(UPLOAD_NAME_HEADER);
        if (fileName != null) {
            params.put(UPLOAD_NAME_FIELD, Lists.newArrayList(fileName));
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
