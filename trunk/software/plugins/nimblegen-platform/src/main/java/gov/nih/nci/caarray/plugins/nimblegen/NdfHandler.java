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
package gov.nih.nci.caarray.plugins.nimblegen;

import gov.nih.nci.caarray.application.util.Utils;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.platforms.AbstractDesignFileHandler;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollableResults;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Implementation of NDF parser.
 * 
 * @author Jim McCusker
 */
@SuppressWarnings("PMD.TooManyMethods")
public class NdfHandler extends AbstractDesignFileHandler {
    private static final String LSID_AUTHORITY = "nimblegen.com";
    private static final String LSID_NAMESPACE = "PhysicalArrayDesign";
    private static final Logger LOG = Logger.getLogger(NdfHandler.class);

    private static final String TEMP_TABLE_NAME = "NGImporterTempTable";
    private static final String CREATE_TEMP_TABLE_STMT = "CREATE TEMPORARY TABLE " + TEMP_TABLE_NAME
    + " ( PROBE_ID varchar(100), " + "SEQ_ID varchar(100), " + "CONTAINER varchar(100), " + "X int, "
    + "Y int, " + "INDEX(SEQ_ID));";

    private static final String Y = "Y";
    private static final String X = "X";
    private static final String PROBE_ID = "PROBE_ID";
    private static final String CONTAINER2 = "CONTAINER";
    private static final String SEQ_ID = "SEQ_ID";

    private static final Map<String, DataType> NDF_REQUIRED_COLUMNS = new HashMap<String, DataType>();
    static {
        NDF_REQUIRED_COLUMNS.put(CONTAINER2, DataType.STRING);
        NDF_REQUIRED_COLUMNS.put(SEQ_ID, DataType.STRING);
        NDF_REQUIRED_COLUMNS.put(PROBE_ID, DataType.STRING);
        NDF_REQUIRED_COLUMNS.put(X, DataType.INTEGER);
        NDF_REQUIRED_COLUMNS.put(Y, DataType.INTEGER);
    }

    /**
     * File Type for NDF array design.
     */
    public static final FileType NDF_FILE_TYPE = new FileType("NIMBLEGEN_NDF", FileCategory.ARRAY_DESIGN, true, "NDF");
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(NDF_FILE_TYPE);

    private CaArrayFile designFile;
    private File fileOnDisk;
    private DelimitedFileReader reader;
    @Inject
    private final CaArrayHibernateHelper hibernateHelper;

    @Inject
    NdfHandler(SessionTransactionManager sessionTransactionManager, DataStorageFacade dataStorageFacade,
            ArrayDao arrayDao, SearchDao searchDao, CaArrayHibernateHelper hibernateHelper) {
        super(sessionTransactionManager, dataStorageFacade, arrayDao, searchDao);
        this.hibernateHelper = hibernateHelper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean openFiles(Set<CaArrayFile> designFiles) throws PlatformFileReadException {
        if (designFiles == null || designFiles.size() != 1
                || !SUPPORTED_TYPES.contains(designFiles.iterator().next().getFileType())) {
            return false;
        }

        this.designFile = designFiles.iterator().next();
        this.fileOnDisk = getDataStorageFacade().openFile(this.designFile.getDataHandle(), false);
        try {
            this.reader = new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(this.fileOnDisk);
            return true;
        } catch (final IOException e) {
            throw new PlatformFileReadException(this.fileOnDisk, "Could not open reader for file "
                    + this.designFile.getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileType> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeFiles() {
        if (this.reader != null) {
            this.reader.close();
        }
        getDataStorageFacade().releaseFile(this.designFile.getDataHandle(), false);
        this.reader = null;
        this.fileOnDisk = null;
        this.designFile = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
         return true;
    }

    private ScrollableResults loadRows(File file) throws IOException {
        this.hibernateHelper.getCurrentSession().createSQLQuery(CREATE_TEMP_TABLE_STMT).executeUpdate();
        final String filePath = file.getAbsolutePath().replace('\\', '/');
        final String loadQuery = "load data local infile '" + filePath + "' into table " + TEMP_TABLE_NAME
        + " fields terminated by '\t' ignore 1 lines "
        + "(@c1,CONTAINER,@c3,@c4,SEQ_ID,@c6,@c7,@c8,@c9,@c10,@c11,@c12,PROBE_ID,@c14,@c15,X,Y);";

        final SQLQuery q = this.hibernateHelper.getCurrentSession().createSQLQuery(loadQuery);
        q.executeUpdate();
        return getProbes();
    }

    ScrollableResults getProbes() {
        final SQLQuery q = this.hibernateHelper.getCurrentSession().createSQLQuery(
                "select * from " + TEMP_TABLE_NAME + " order by SEQ_ID asc");
        return q.scroll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createDesignDetails(ArrayDesign arrayDesign) throws PlatformFileReadException {
        final Map<String, LogicalProbe> logicalProbes = new HashMap<String, LogicalProbe>();
        int count = 0;
        try {
            final ArrayDesignDetails details = new ArrayDesignDetails();
            arrayDesign.setDesignDetails(details);
            getArrayDao().save(arrayDesign);
            getArrayDao().save(details);

            final ScrollableResults results = loadRows(this.fileOnDisk);
            count = loadProbes(details, logicalProbes, results);
            arrayDesign.setNumberOfFeatures(count);
        } catch (final IOException e) {
            LOG.error("Error processing line " + count);
            throw new PlatformFileReadException(this.fileOnDisk, "Couldn't read file: ", e);
        } finally {
            this.hibernateHelper.getCurrentSession().createSQLQuery("DROP TABLE " + TEMP_TABLE_NAME + ";")
            .executeUpdate();
        }
    }

    private int loadProbes(ArrayDesignDetails details, Map<String, LogicalProbe> logicalProbes,
            ScrollableResults results) throws IOException {
        int count = 0;
        results.beforeFirst();
        String lastSeqId = null;
        while (results.next()) {
            final Object[] values = results.get();
            final Map<String, Object> vals = new HashMap<String, Object>();
            vals.put(PROBE_ID, values[0]);
            vals.put(SEQ_ID, values[1]);
            vals.put(CONTAINER2, values[2]);
            vals.put(X, values[3]);
            vals.put(Y, values[4]);

            if (lastSeqId != null && !vals.get(SEQ_ID).equals(lastSeqId)) {
                logicalProbes.clear();
                flushAndClearSession();
            }
            lastSeqId = (String) vals.get(SEQ_ID);

            final PhysicalProbe p = createPhysicalProbe(details, vals, logicalProbes);
            getArrayDao().save(p);
            ++count;
        }
        return count;
    }

    private LogicalProbe getLogicalProbe(String feature, ArrayDesignDetails details,
            Map<String, LogicalProbe> logicalProbes) {
        if (!logicalProbes.containsKey(feature)) {
            final LogicalProbe p = new LogicalProbe(details);
            p.setName(feature);
            logicalProbes.put(feature, p);
            getArrayDao().save(p);
            return p;
        } else {
            return logicalProbes.get(feature);
        }
    }

    private PhysicalProbe createPhysicalProbe(ArrayDesignDetails details, Map<String, Object> values,
            Map<String, LogicalProbe> logicalProbes) {
        final String sequenceId = (String) values.get(SEQ_ID);
        final String container = (String) values.get(CONTAINER2);
        final String probeId = (String) values.get(PROBE_ID);
        final LogicalProbe lp = getLogicalProbe(sequenceId, details, logicalProbes);
        final PhysicalProbe p = new PhysicalProbe(details, null);
        lp.addProbe(p);
        p.setName(container + "|" + sequenceId + "|" + probeId);

        final Feature f = new Feature(details);
        f.setColumn(((Integer) values.get(X)).shortValue());
        f.setRow(((Integer) values.get(Y)).shortValue());

        p.getFeatures().add(f);

        return p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(ArrayDesign arrayDesign) {
        arrayDesign.setName(FilenameUtils.getBaseName(this.designFile.getName()));
        arrayDesign.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE + ":" + arrayDesign.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ValidationResult result) throws PlatformFileReadException {
        final FileValidationResult fileResult = result.getOrCreateFileValidationResult(this.designFile.getName());
        this.designFile.setValidationResult(fileResult);
        try {
            this.reader.reset();
            if (!this.reader.hasNextLine()) {
                fileResult.addMessage(ValidationMessage.Type.ERROR, "File was empty");
            }
            final Map<String, Integer> headers = getHeaders();
            validateHeader(headers, fileResult);
            if (fileResult.isValid()) {
                validateValues(headers, fileResult);
            }
        } catch (final IOException e) {
            throw new PlatformFileReadException(this.fileOnDisk, "Unable to read file", e);
        }
    }

    private void validateHeader(Map<String, Integer> headers, FileValidationResult result) throws IOException {
        if (headers == null) {
            result.addMessage(ValidationMessage.Type.ERROR, "Could not find column headers in file. Header must "
                    + " contain at least the following column headings: " + NDF_REQUIRED_COLUMNS.keySet());
        } else {
            final Set<String> missing = new HashSet<String>(NDF_REQUIRED_COLUMNS.keySet());
            missing.removeAll(headers.keySet());

            for (final String col : missing) {
                result.addMessage(ValidationMessage.Type.ERROR, "Invalid column header for Nimblegen NDF. Missing "
                        + col + " column", this.reader.getCurrentLineNumber(), 0);
            }
        }
    }

    private void validateValues(Map<String, Integer> headers, FileValidationResult result) throws IOException {
        while (this.reader.hasNextLine()) {
            final List<String> values = this.reader.nextLine();
            validateValuesRow(values, headers, result);
        }
    }

    private void validateValuesRow(List<String> values, Map<String, Integer> header, FileValidationResult result) {
        if (values.size() != header.size()) {
            result.addMessage(ValidationMessage.Type.ERROR,
                    "Row has incorrect number of columns. There were " + values.size() + " columns in the row, and "
                    + header.size() + " columns in the header", this.reader.getCurrentLineNumber(), 0);
            return;
        }

        for (final String column : NDF_REQUIRED_COLUMNS.keySet()) {
            final int columnIndex = header.get(column);
            final String value = values.get(columnIndex);
            final DataType columnType = NDF_REQUIRED_COLUMNS.get(column);
            if (StringUtils.isBlank(value)) {
                result.addMessage(ValidationMessage.Type.ERROR, "Empty value for required column " + column,
                        this.reader.getCurrentLineNumber(), columnIndex + 1);
            } else if (columnType == DataType.INTEGER && !Utils.isInteger(value)) {
                result.addMessage(ValidationMessage.Type.ERROR, "Expected integer value but found " + value
                        + " for required column " + column, this.reader.getCurrentLineNumber(), columnIndex + 1);
            }
        }
    }

    private boolean isHeaderLine(List<String> values) {
        // assume that a line that contains any of expected header columns is the header
        return !Sets.intersection(ImmutableSet.copyOf(values), NDF_REQUIRED_COLUMNS.keySet()).isEmpty();
    }

    /**
     * @return mapping of header columns to their positions, or null if no header line found
     */
    private Map<String, Integer> getHeaders() throws IOException {
        while (this.reader.hasNextLine()) {
            final List<String> values = this.reader.nextLine();
            if (values.isEmpty()) {
                // allow blank lines at start
                continue;
            }
            if (isHeaderLine(values)) {
                int index = 0;
                final Map<String, Integer> result = new HashMap<String, Integer>();
                for (final String value : values) {
                    result.put(value.toUpperCase(Locale.getDefault()), index++);
                }
                return result;
            } else {
                // once we've hit a non-blank line, if it's not the header, then there is no header
                return null;
            }
        }
        return null;
    }
}
