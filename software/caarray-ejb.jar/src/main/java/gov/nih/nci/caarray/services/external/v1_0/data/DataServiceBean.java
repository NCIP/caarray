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
package gov.nih.nci.caarray.services.external.v1_0.data;

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.DataFileContents;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.DesignElement;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.FileDownloadRequest;
import gov.nih.nci.caarray.services.AuthorizationInterceptor;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;
import gov.nih.nci.caarray.services.external.v1_0.BaseV1_0ExternalService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.ejb.TransactionTimeout;

import com.healthmarketscience.rmiio.RemoteOutputStream;
import com.healthmarketscience.rmiio.RemoteOutputStreamClient;

/**
 * @author dkokotov
 * 
 */
@Stateless(name = "DataServicev1_0")
@PermitAll
@RemoteBinding(jndiBinding = DataService.JNDI_NAME)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionTimeout(DataServiceBean.TIMEOUT_SECONDS)
@Interceptors({ AuthorizationInterceptor.class, HibernateSessionInterceptor.class })
@SuppressWarnings("PMD")
public class DataServiceBean extends BaseV1_0ExternalService implements DataService {
    private static final Logger LOG = Logger.getLogger(DataServiceBean.class);
    static final int TIMEOUT_SECONDS = 1800;

    private final CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    /**
     * {@inheritDoc}
     */
    public DataSet getDataSet(DataSetRequest request) {
        LOG.info("Received data retrieval request");
        checkRequest(request);
        List<gov.nih.nci.caarray.domain.data.DataSet> dataSets = getDataSets(request);
        gov.nih.nci.caarray.domain.data.DataSet mergedDataSet = createMergedDataSet(dataSets, request);
        LOG.info("Retrieved " + mergedDataSet.getHybridizationDataList().size() + " hybridization data elements, "
                + mergedDataSet.getQuantitationTypes().size() + " quant types");
        return toExternalDataSet(mergedDataSet);
    }
    
    private DataSet toExternalDataSet(gov.nih.nci.caarray.domain.data.DataSet dataSet) {
        DataSet externalDataSet = new DataSet();
        mapCollection(dataSet.getQuantitationTypes(), externalDataSet.getQuantitationTypes(), QuantitationType.class);
        mapCollection(dataSet.getDesignElementList().getDesignElements(), externalDataSet.getDesignElements(),
                DesignElement.class);
        mapCollection(dataSet.getHybridizationDataList(), externalDataSet.getDatas(),
                gov.nih.nci.caarray.external.v1_0.data.HybridizationData.class);        
        return externalDataSet;
    }

    private List<gov.nih.nci.caarray.domain.data.DataSet> getDataSets(DataSetRequest request) {
        List<AbstractArrayData> arrayDatas = getArrayDatas(request);
        List<gov.nih.nci.caarray.domain.data.DataSet> dataSets = new ArrayList<gov.nih.nci.caarray.domain.data.DataSet>(
                arrayDatas.size());
        for (AbstractArrayData data : arrayDatas) {
            dataSets.add(getArrayDataService().getData(data, getQuantitationTypes(request)));
        }
        return dataSets;
    }

    private gov.nih.nci.caarray.domain.data.DataSet createMergedDataSet(
            List<gov.nih.nci.caarray.domain.data.DataSet> dataSets, DataSetRequest request) {
        gov.nih.nci.caarray.domain.data.DataSet dataSet = new gov.nih.nci.caarray.domain.data.DataSet();
        dataSet.getQuantitationTypes().addAll(getQuantitationTypes(request));
        addDesignElementList(dataSet, dataSets);
        addHybridizationDatas(dataSet, dataSets, request);
        return dataSet;
    }

    private void addDesignElementList(gov.nih.nci.caarray.domain.data.DataSet dataSet,
            List<gov.nih.nci.caarray.domain.data.DataSet> dataSets) {
        if (dataSets.isEmpty()) {
            dataSet.setDesignElementList(new DesignElementList());
        } else if (allDesignElementListsAreConsistent(dataSets)) {
            dataSet.setDesignElementList(dataSets.get(0).getDesignElementList());
        } else {
            throw new IllegalArgumentException("The DataSet requested data from inconsistent design elemeents");
        }
    }

    private boolean allDesignElementListsAreConsistent(List<gov.nih.nci.caarray.domain.data.DataSet> dataSets) {
        DesignElementList firstList = dataSets.get(0).getDesignElementList();
        for (int i = 1; i < dataSets.size(); i++) {
            DesignElementList nextList = dataSets.get(i).getDesignElementList();
            if (!ListUtils.isEqualList(firstList.getDesignElements(), nextList.getDesignElements())) {
                return false;
            }
        }
        return true;
    }

    private void addHybridizationDatas(gov.nih.nci.caarray.domain.data.DataSet dataSet,
            List<gov.nih.nci.caarray.domain.data.DataSet> dataSets, DataSetRequest request) {
        for (gov.nih.nci.caarray.domain.data.DataSet nextDataSet : dataSets) {
            for (HybridizationData nextHybridizationData : nextDataSet.getHybridizationDataList()) {
                addHybridizationData(dataSet, nextHybridizationData, request);
            }
        }
    }

    private void addHybridizationData(gov.nih.nci.caarray.domain.data.DataSet dataSet,
            HybridizationData copyFromHybridizationData, DataSetRequest request) {
        HybridizationData hybridizationData = new HybridizationData();
        hybridizationData.setHybridization(copyFromHybridizationData.getHybridization());
        dataSet.getHybridizationDataList().add(hybridizationData);
        copyColumns(hybridizationData, copyFromHybridizationData, getQuantitationTypes(request));
        hybridizationData.setDataSet(dataSet);
    }

    private void copyColumns(HybridizationData hybridizationData, HybridizationData copyFromHybridizationData,
            List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes) {
        for (gov.nih.nci.caarray.domain.data.QuantitationType type : quantitationTypes) {
            hybridizationData.getColumns().add(copyFromHybridizationData.getColumn(type));
        }
    }

    private void checkRequest(DataSetRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("DataRetrievalRequest was null");
        } else if (request.getHybridizations().isEmpty() && request.getDataFiles().isEmpty()) {
            throw new IllegalArgumentException("DataRetrievalRequest didn't specify any Hybridizations or Files");
        } else if (request.getQuantitationTypes().isEmpty()) {
            throw new IllegalArgumentException("DataRetrievalRequest didn't specify QuantitationTypes");
        }
    }

    private List<AbstractArrayData> getArrayDatas(DataSetRequest request) {
        List<Hybridization> hybridizations = getHybridizations(request);
        List<CaArrayFile> files = getFiles(request);
        List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes = 
            getQuantitationTypes(request);
        List<AbstractArrayData> arrayDatas = new ArrayList<AbstractArrayData>(hybridizations.size());

        for (Hybridization hybridization : hybridizations) {
            addArrayDatas(arrayDatas, hybridization, quantitationTypes);
        }
        for (CaArrayFile dataFile : files) {
            addArrayDatas(arrayDatas, dataFile, quantitationTypes);
        }
        return arrayDatas;
    }

    private void addArrayDatas(List<AbstractArrayData> arrayDatas, Hybridization hybridization,
            List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes) {
        for (RawArrayData rawArrayData : hybridization.getRawDataCollection()) {
            if (shouldAddData(arrayDatas, rawArrayData, quantitationTypes)) {
                arrayDatas.add(rawArrayData);
            }
        }
        for (DerivedArrayData derivedArrayData : hybridization.getDerivedDataCollection()) {
            if (shouldAddData(arrayDatas, derivedArrayData, quantitationTypes)) {
                arrayDatas.add(derivedArrayData);
            }
        }
    }

    private void addArrayDatas(List<AbstractArrayData> arrayDatas, CaArrayFile dataFile,
            List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes) {
        if (dataFile.getFileType().isDerivedArrayData()) {
            AbstractArrayData arrayData = daoFactory.getArrayDao().getDerivedArrayData(dataFile);
            if (arrayData != null && shouldAddData(arrayDatas, arrayData, quantitationTypes)) {
                arrayDatas.add(arrayData);
            }
        }
        if (dataFile.getFileType().isRawArrayData()) {
            AbstractArrayData arrayData = daoFactory.getArrayDao().getRawArrayData(dataFile);
            if (arrayData != null && shouldAddData(arrayDatas, arrayData, quantitationTypes)) {
                arrayDatas.add(arrayData);
            }
        }
    }

    private boolean shouldAddData(List<AbstractArrayData> arrayDatas, AbstractArrayData arrayData,
            List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes) {
        return arrayData != null && !arrayDatas.contains(arrayData) && containsAllTypes(arrayData, quantitationTypes);
    }

    private boolean containsAllTypes(AbstractArrayData arrayData,
            List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes) {
        return arrayData.getDataSet() != null
                && arrayData.getDataSet().getQuantitationTypes().containsAll(quantitationTypes);
    }

    private List<Hybridization> getHybridizations(DataSetRequest request) {
        List<Hybridization> hybridizations = new ArrayList<Hybridization>(request.getHybridizations().size());
        for (CaArrayEntityReference hybRef : request.getHybridizations()) {
            hybridizations.add((Hybridization) getByLsid(hybRef.getLsid()));
        }        
        return hybridizations;
    }

    private List<CaArrayFile> getFiles(DataSetRequest request) {
        List<CaArrayFile> files = new ArrayList<CaArrayFile>(request.getDataFiles().size());
        for (CaArrayEntityReference fileRef : request.getDataFiles()) {
            files.add((CaArrayFile) getByLsid(fileRef.getLsid()));
        }
        return files;
    }

    private List<gov.nih.nci.caarray.domain.data.QuantitationType> getQuantitationTypes(DataSetRequest request) {
        List<gov.nih.nci.caarray.domain.data.QuantitationType> types = 
            new ArrayList<gov.nih.nci.caarray.domain.data.QuantitationType>(request.getDataFiles().size());
        for (CaArrayEntityReference qtRef : request.getQuantitationTypes()) {
            types.add((gov.nih.nci.caarray.domain.data.QuantitationType) getByLsid(qtRef.getLsid()));
        }
        return types;
    }

    /**
     * {@inheritDoc}
     */
    public List<DataFileContents> getFileContents(FileDownloadRequest fileDownloadRequest, boolean compress)
            throws FileSizeTooBigException {
        List<DataFileContents> fileContentsList = new ArrayList<DataFileContents>();
        long fileSize = 0;
        for (CaArrayEntityReference fileRef : fileDownloadRequest.getFiles()) {
            DataFileContents contents = getFileContents(fileRef, compress);
            if (contents == null) {
                continue;
            }
            fileSize += compress ? contents.getFileMetadata().getCompressedSize() : contents.getFileMetadata()
                    .getUncompressedSize();
            if (fileSize > DataService.MAX_DIRECT_RETRIEVAL_FILE_SIZE) {
                throw new FileSizeTooBigException(fileSize, DataService.MAX_DIRECT_RETRIEVAL_FILE_SIZE);
            }
            fileContentsList.add(contents);
        }
        return fileContentsList;
    }
    
    /**
     * {@inheritDoc}
     */
    public DataFileContents getFileContents(CaArrayEntityReference fileRef, boolean compress)
            throws FileSizeTooBigException {        
        CaArrayFile file = (CaArrayFile) getByLsid(fileRef.getLsid());
        long fileSize = compress ? file.getCompressedSize() : file.getUncompressedSize();
        if (fileSize > DataService.MAX_DIRECT_RETRIEVAL_FILE_SIZE) {
            throw new FileSizeTooBigException(fileSize, DataService.MAX_DIRECT_RETRIEVAL_FILE_SIZE);
        }
        DataFileContents contents = new DataFileContents();
        DataFile fileMetadata = new DataFile();
        fileMetadata.setCompressedSize(file.getCompressedSize());
        fileMetadata.setUncompressedSize(file.getUncompressedSize());
        fileMetadata.setName(file.getName());
        fileMetadata.setLsid(file.getLsid());
        contents.setFileMetadata(fileMetadata);
        contents.setCompressed(compress);
        try {
            contents.setContents(IOUtils.toByteArray(compress ? file.readCompressedContents() : file.readContents()));
        } catch (IOException e) {
            LOG.warn("Could not read file contents", e);
        }
        return contents;
    }
    
    /**
     * {@inheritDoc}
     */
    public DataFile streamFileContents(CaArrayEntityReference fileRef, boolean compress, RemoteOutputStream out) {
        CaArrayFile file = (CaArrayFile) getByLsid(fileRef.getLsid());
        DataFile fileMetadata = new DataFile();
        fileMetadata.setCompressedSize(file.getCompressedSize());
        fileMetadata.setUncompressedSize(file.getUncompressedSize());
        fileMetadata.setName(file.getName());
        fileMetadata.setLsid(file.getLsid());
        try {
            OutputStream osWrap = RemoteOutputStreamClient.wrap(out);
            IOUtils.copy(compress ? file.readCompressedContents() : file.readContents(), osWrap);
            osWrap.flush();
        } catch (IOException e) {
            LOG.warn("Could not write file contents to remote output stream", e);
        }
        return fileMetadata;
    }
}
