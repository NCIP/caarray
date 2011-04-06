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
package gov.nih.nci.caarray.plugins.visualizationTab.action;

import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.translation.gct.GctExporter;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.AbstractProbe;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.plugins.affymetrix.AffymetrixExpressionChpQuantitationType;
import gov.nih.nci.caarray.services.data.DataRetrievalService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.visualization.datasource.Capabilities;
import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.QueryPair;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.base.ResponseStatus;
import com.google.visualization.datasource.base.StatusType;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableCell;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.NumberValue;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.AbstractColumn;
import com.google.visualization.datasource.query.Query;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;

/**
 * Action implementing the samples tab.
 * 
 * @author Dan Kokotov
 */
public class ProjectVisualizationAction implements Preparable {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(ProjectVisualizationAction.class);

    private static final String SAMPLE_NAME = "sample_name";

    private static final ColumnDescription[] QUANT_COLUMNS = new ColumnDescription[] { new ColumnDescription(
            SAMPLE_NAME, ValueType.TEXT, "Name of sample"), };

    private static final int MAX_ZOOM_LEVEL = 5;
    private static final int GROUPS_PER_WINDOW = 50;
    private static final int MAX_PROBE_INDEX_RANGE = 500;

    private Project project = new Project();
    private List<Hybridization> hybridizations = new ArrayList<Hybridization>();
    private String dataset;
    private int zoomLevel;
    private int startProbeIndex;
    private int endProbeIndex;

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() {
        Project retrieved = null;
        if (this.project.getId() != null) {
            retrieved = ServiceLocatorFactory.getGenericDataService().getPersistentObject(Project.class,
                    this.project.getId());
        } else if (this.project.getExperiment().getPublicIdentifier() != null) {
            retrieved = ServiceLocatorFactory.getProjectManagementService().getProjectByPublicId(
                    this.project.getExperiment().getPublicIdentifier());
        }
        if (retrieved != null) {
            this.project = retrieved;
        }
    }

    /**
     * {@inheritDoc}
     */
    @SkipValidation
    public String load() {
        return "success";
    }

    /**
     * {@inheritDoc}
     */
    @SkipValidation
    public String openInIgv() throws IOException {
        Socket socket = null;
        BufferedReader in = null;
        final HttpServletResponse response = ServletActionContext.getResponse();
        final PrintWriter pw = response.getWriter();
        try {
            socket = new Socket("127.0.0.1", 60151);
            final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("new");
            String igvResp = in.readLine();
            LOG.debug("genome socket response: " + igvResp);

            out.println("genome hg16");
            igvResp = in.readLine();
            LOG.debug("genome socket response: " + igvResp);

            StringBuilder url = new StringBuilder("/ajax/project/tab/Visualization/gct.action?project.id=")
                    .append(getProject().getId());
            for (final Hybridization h : this.hybridizations) {
                url.append("&hybridizations.id=" + h.getId());
            }
            // hack to make IGV think it's a GCT file
            url.append("&type=gct");
            LOG.debug("Load GCT url: " + getAbsoluteLink(url.toString()));
            out.println("load " + getAbsoluteLink(url.toString()));
            igvResp = in.readLine();
            LOG.debug("load socket response: " + igvResp);

            url = new StringBuilder("/ajax/project/tab/Visualization/sampleInfo.action?project.id=")
                    .append(getProject().getId());
            for (final Hybridization h : this.hybridizations) {
                url.append("&hybridizations.id=" + h.getId());
            }
            LOG.debug("Load sampleInfo url: " + getAbsoluteLink(url.toString()));
            out.println("load " + getAbsoluteLink(url.toString()));
            igvResp = in.readLine();
            LOG.debug("load socket response: " + igvResp);

            pw.println("Successfully loaded data in IGV");
        } catch (final Exception e) {
            LOG.error("Error talking to IGV", e);
            pw.println("Error loading data in IGV");
        } finally {
            if (in != null) {
                IOUtils.closeQuietly(in);
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (final IOException e) {
                    // no biggie
                }
            }
            pw.flush();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SkipValidation
    public String gct() throws Exception {
        try {
            final GctExporter exporter = ServiceLocatorFactory.getGctExporter();
            final HttpServletResponse response = ServletActionContext.getResponse();
            final String fileName = getExperiment().getPublicIdentifier() + ".gct";
            response.setContentType("text/plain; charset=iso-8859-1");
            response.addHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
            exporter.export(getHybridizations(), AffymetrixExpressionChpQuantitationType.CHP_SIGNAL,
                    response.getWriter());
            // response.addHeader("Content-Length", String.valueOf(bytes.length));
            response.getWriter().flush();
            return null;
        } catch (final IOException e) {
            LOG.error(e);
            throw e;
        } catch (final RuntimeException e) {
            LOG.error(e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @SkipValidation
    public String sampleInfo() throws Exception {
        final HttpServletResponse response = ServletActionContext.getResponse();
        final PrintWriter pw = response.getWriter();
        final ProjectManagementService pms = ServiceLocatorFactory.getProjectManagementService();
        final List<Category> categories = pms.getAllCharacteristicCategories(getProject().getExperiment());

        pw.print("TRACK_ID");
        for (final Category cat : categories) {
            pw.print(String.format("\t%s", cat.getName()));
        }
        pw.println();

        final Set<Hybridization> hybs = new HashSet<Hybridization>(getHybridizations());
        for (Hybridization h : hybs) {
            final GenericDataService gds = ServiceLocatorFactory.getGenericDataService();
            h = gds.getPersistentObject(Hybridization.class, h.getId());
            pw.print(h.getName());
            for (final Category cat : categories) {
                final Set<AbstractCharacteristic> chars = h.getCharacteristicsRecursively(cat);
                final String charValue = StringUtils.join(
                        Iterators.transform(chars.iterator(), new Function<AbstractCharacteristic, String>() {
                            @Override
                            public String apply(AbstractCharacteristic c) {
                                return c.getDisplayValue();
                            }
                        }), ", ");
                pw.print(String.format("\t%s", charValue));
            }
            pw.println();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SkipValidation
    public String data() {
        DataSourceRequest dsRequest = null;
        final HttpServletRequest req = ServletActionContext.getRequest();
        final HttpServletResponse resp = ServletActionContext.getResponse();
        try {
            try {
                // Extract the request parameters.
                dsRequest = new DataSourceRequest(req);

                // Split the query.
                final QueryPair query = DataSourceHelper.splitQuery(dsRequest.getQuery(), Capabilities.SELECT);

                // Generate the data table.
                final DataTable data = generateMyDataTable(query.getDataSourceQuery());

                // Apply the completion query to the data table.
                final DataTable newData = DataSourceHelper.applyQuery(query.getCompletionQuery(), data,
                        dsRequest.getUserLocale());

                DataSourceHelper.setServletResponse(newData, dsRequest, resp);
            } catch (final RuntimeException rte) {
                LOG.error("A runtime exception has occured", rte);
                final ResponseStatus status = new ResponseStatus(StatusType.ERROR, ReasonType.INTERNAL_ERROR,
                        rte.getMessage());
                if (dsRequest == null) {
                    dsRequest = DataSourceRequest.getDefaultDataSourceRequest(req);
                }
                DataSourceHelper.setServletErrorResponse(status, dsRequest, resp);
            } catch (final DataSourceException e) {
                if (dsRequest != null) {
                    DataSourceHelper.setServletErrorResponse(e, dsRequest, resp);
                } else {
                    DataSourceHelper.setServletErrorResponse(e, req, resp);
                }
            }
            return null;
        } catch (final IOException e) {
            throw new IllegalStateException("Error writing response stream", e);
        }
    }

    private DataTable generateMyDataTable(Query query) throws DataSourceException {
        if ("heatmap".equals(this.dataset)) {
            return generateHeatmapTable(query);
        } else {
            return generateCharacteristicsTable(query);
        }
    }

    private DataTable generateCharacteristicsTable(Query query) throws DataSourceException {
        final DataTable data = new DataTable();

        final ProjectManagementService pms = ServiceLocatorFactory.getProjectManagementService();
        final List<Category> categories = pms.getAllCharacteristicCategories(getProject().getExperiment());

        final List<ColumnDescription> requiredColumns = getRequiredCharacteristicsColumns(query, categories);
        data.addColumns(requiredColumns);

        final Set<Hybridization> hybs = new HashSet<Hybridization>(getHybridizations());
        for (Hybridization h : hybs) {
            final GenericDataService gds = ServiceLocatorFactory.getGenericDataService();
            h = gds.getPersistentObject(Hybridization.class, h.getId());
            final TableRow row = new TableRow();
            for (final ColumnDescription selectionColumn : requiredColumns) {
                final String columnName = selectionColumn.getId();
                if (columnName.equals(SAMPLE_NAME)) {
                    row.addCell(h.getName());
                } else {
                    final Category cat = findCategoryByName(categories, selectionColumn.getId());
                    if (cat == null) {
                        LOG.warn("Could not find category " + selectionColumn.getId());
                        continue;
                    }
                    final Set<AbstractCharacteristic> chars = h.getCharacteristicsRecursively(cat);
                    final String charValue = StringUtils.join(
                            Iterators.transform(chars.iterator(), new Function<AbstractCharacteristic, String>() {
                                @Override
                                public String apply(AbstractCharacteristic c) {
                                    return c.getDisplayValue();
                                }
                            }), ", ");

                    row.addCell(new TableCell(new NumberValue(charValue.hashCode()), charValue));
                }
            }
            data.addRow(row);
        }

        return data;
    }

    private DataTable generateHeatmapTable(Query query) throws DataSourceException {
        final DataRetrievalService drs = ServiceLocatorFactory.getDataRetrievalService();
        final DataTable data = new DataTable();

        final DataRetrievalRequest drr = new DataRetrievalRequest();
        drr.getHybridizations().addAll(this.hybridizations);
        drr.getQuantitationTypes().add(getQuantitationType(AffymetrixExpressionChpQuantitationType.CHP_SIGNAL));
        final DataSet ds = drs.getDataSet(drr);

        final List<AbstractDesignElement> elements = ds.getDesignElementList().getDesignElements();
        final double multiplier = elements.size() / (double) MAX_PROBE_INDEX_RANGE;
        final int window = (int) Math.floor((this.endProbeIndex - this.startProbeIndex) * multiplier); // getProbeWindowSize(elements.size());
        this.startProbeIndex = (int) Math.floor(this.startProbeIndex * multiplier);
        final int groupSize = window / GROUPS_PER_WINDOW;

        final List<ColumnDescription> requiredColumns = getRequiredHeatmapColumns(query, elements, window, groupSize);
        data.addColumns(requiredColumns);

        for (final HybridizationData hd : ds.getHybridizationDataList()) {
            final TableRow row = new TableRow();
            final FloatColumn column = (FloatColumn) hd.getColumns().get(0);
            for (final ColumnDescription selectionColumn : requiredColumns) {
                final int probeIndex = Integer.parseInt(selectionColumn.getCustomProperty("startIndex"));
                if (probeIndex < 0) {
                    LOG.error("Bad column " + selectionColumn.getId());
                    continue;
                }
                row.addCell(getHeatmapCell(column, probeIndex, groupSize));
            }
            data.addRow(row);
        }

        return data;
    }

    private TableCell getHeatmapCell(FloatColumn signalColumn, int startIndex, int groupSize) {
        double sum = 0;
        final StringBuilder labelSuffix = new StringBuilder(" (average of");
        for (int i = 0; i < groupSize; i++) {
            final int index = Math.min(startIndex + i, signalColumn.getValues().length - 1);
            final double val = signalColumn.getValues()[index];
            sum += val;
            if (i < 10) {
                labelSuffix.append(String.format(" %.2f", val));
            } else if (i == 10) {
                labelSuffix.append(String.format(" and %d more values", groupSize - 10));
            }
        }
        labelSuffix.append(")");

        return new TableCell(new NumberValue(sum / groupSize), String.format("%.2f %s", sum / groupSize,
                groupSize > 1 ? labelSuffix : ""));
    }

    private Category findCategoryByName(final List<Category> categories, final String id) {
        return CaArrayUtils.find(categories, new Predicate<Category>() {
            @Override
            public boolean apply(Category cat) {
                return cat.getName().equals(id);
            }
        });
    }

    private int findProbeByName(List<AbstractDesignElement> elements, String probeName) {
        for (int i = 0; i < elements.size(); i++) {
            final AbstractProbe probe = (AbstractProbe) elements.get(i);
            if (probe.getName().equals(probeName)) {
                return i;
            }
        }
        return -1;
    }

    private QuantitationType getQuantitationType(QuantitationTypeDescriptor qtd) {
        final CaArraySearchService searchService = ServiceLocatorFactory.getCaArraySearchService();
        final QuantitationType qt = new QuantitationType();
        qt.setName(qtd.getName());
        qt.setTypeClass(qtd.getDataType().getTypeClass());
        return searchService.search(qt).get(0);
    }

    private List<ColumnDescription> getRequiredCharacteristicsColumns(Query query, List<Category> categories) {
        // Required columns
        final List<ColumnDescription> requiredColumns = Lists.newArrayList();
        for (final ColumnDescription column : QUANT_COLUMNS) {
            if (isColumnRequested(query, column.getId())) {
                requiredColumns.add(column);
            }
        }
        for (final Category category : categories) {
            if (isColumnRequested(query, category.getName())) {
                requiredColumns.add(new ColumnDescription(category.getName(), ValueType.NUMBER, getColumnLabel(category
                        .getName())));
            }
        }
        return requiredColumns;
    }

    private List<ColumnDescription> getRequiredHeatmapColumns(Query query, List<AbstractDesignElement> elements,
            int window, int groupSize) {
        final List<ColumnDescription> requiredColumns = Lists.newArrayList();
        for (int i = this.startProbeIndex; i < Math.min(this.startProbeIndex + window, elements.size() - groupSize); i += groupSize) {
            final int endIndex = Math.min(i + groupSize, elements.size());
            final ColumnDescription cd = createHeatmapColumn(elements.subList(i, endIndex), i);
            cd.setCustomProperty("startIndex", String.valueOf(i));
            requiredColumns.add(cd);
        }
        return requiredColumns;
    }

    private ColumnDescription createHeatmapColumn(List<AbstractDesignElement> subList, int startIndex) {
        final AbstractProbe firstProbe = (AbstractProbe) subList.get(0);
        final AbstractProbe lastProbe = (AbstractProbe) subList.get(subList.size() - 1);
        final String columnId = "column" + startIndex;
        final String columnName = (firstProbe == lastProbe) ? StringUtils.substring(firstProbe.getName(), 0, 20)
                : StringUtils.substring(firstProbe.getName(), 0, 10) + ".."
                        + StringUtils.substring(lastProbe.getName(), 0, 10);
        final String columnDescription = (firstProbe == lastProbe) ? firstProbe.getName() : firstProbe.getName()
                + " - " + lastProbe.getName() + " (" + subList.size() + " probes)";
        final ColumnDescription cd = new ColumnDescription(columnId, ValueType.NUMBER, columnName);
        cd.setCustomProperty("description", columnDescription);
        return cd;
    }

    private static String getColumnLabel(String init) {
        return StringUtils.rightPad(init, 20, "\u00a0").substring(0, 20);
    }

    private boolean isColumnRequested(Query query, String columnName) {
        // If the query is empty return true.
        if (!query.hasSelection()) {
            return true;
        }

        final List<AbstractColumn> columns = query.getSelection().getColumns();
        for (final AbstractColumn column : columns) {
            if (column.getId().equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }

    private int getProbeWindowSize(int totalProbes) {
        final double zoomStep = Math.pow(totalProbes / GROUPS_PER_WINDOW, 1.0d / (MAX_ZOOM_LEVEL - 1));
        return (int) Math.floor(totalProbes / Math.pow(zoomStep, this.zoomLevel - 1));
    }

    public List<Hybridization> getHybridizations() {
        return this.hybridizations;
    }

    public void setHybridizations(List<Hybridization> hybridizations) {
        this.hybridizations = hybridizations;
    }

    public String getDataset() {
        return this.dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public int getZoomLevel() {
        return this.zoomLevel;
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public int getStartProbeIndex() {
        return this.startProbeIndex;
    }

    public void setStartProbeIndex(int startProbeIndex) {
        this.startProbeIndex = startProbeIndex;
    }

    public int getEndProbeIndex() {
        return this.endProbeIndex;
    }

    public void setEndProbeIndex(int endProbeIndex) {
        this.endProbeIndex = endProbeIndex;
    }

    /**
     * code inspired by outputUrl.tag.
     * 
     * @return the current project's permanent URL.
     */
    protected String getAbsoluteLink(String relativeUrl) {
        final HttpServletRequest request = ServletActionContext.getRequest();
        final StringBuffer sb = new StringBuffer();
        final String scheme = request.getScheme();
        final String host = request.getServerName();
        final int port = request.getServerPort();
        sb.append(scheme).append("://").append(host);
        // CHECKSTYLE:OFF port 80 and 443 are not magic numbers
        if (("http".equalsIgnoreCase(scheme) && port != 80) || ("https".equalsIgnoreCase(scheme) && port != 443)) {
            sb.append(":").append(port);
        }
        // CHECKSTYLE:ON
        sb.append(request.getContextPath());
        sb.append(relativeUrl);
        return sb.toString();
    }

    /**
     * @return the project
     */
    @CustomValidator(type = "hibernate")
    public Project getProject() {
        return this.project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Convenience method for getting the experiment of the current project.
     * 
     * @return the project's experiment
     */
    protected Experiment getExperiment() {
        return getProject().getExperiment();
    }
}
