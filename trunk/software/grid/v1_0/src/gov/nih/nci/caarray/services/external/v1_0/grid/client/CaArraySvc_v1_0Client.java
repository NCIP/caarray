//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.grid.client;

 import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.services.external.v1_0.grid.common.CaArraySvc_v1_0I;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InvalidReferenceFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;

import java.rmi.RemoteException;

import org.apache.axis.client.Stub;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.gsi.GlobusCredential;

/**
 * This class is autogenerated, DO NOT EDIT GENERATED GRID SERVICE ACCESS METHODS.
 * 
 * This client is generated automatically by Introduce to provide a clean unwrapped API to the service.
 * 
 * On construction the class instance will contact the remote service and retrieve it's security metadata description
 * which it will use to configure the Stub specifically for each method call.
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class CaArraySvc_v1_0Client extends CaArraySvc_v1_0ClientBase implements CaArraySvc_v1_0I {

    public CaArraySvc_v1_0Client(String url) throws MalformedURIException, RemoteException {
        this(url, null);
    }

    public CaArraySvc_v1_0Client(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
        super(url, proxy);
    }

    public CaArraySvc_v1_0Client(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
        this(epr, null);
    }

    public CaArraySvc_v1_0Client(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException,
            RemoteException {
        super(epr, proxy);
    }

    public static void usage() {
        System.out.println(CaArraySvc_v1_0Client.class.getName() + " -url <service url>");
    }

    public static void main(String[] args) {
        System.out.println("Running the Grid Service Client");
        try {
            if (!(args.length < 2)) {
                if (args[0].equals("-url")) {                    
                    CaArraySvc_v1_0Client client = new CaArraySvc_v1_0Client(args[1]);
                    SearchApiUtils searchUtils = new GridSearchApiUtils(client);
                    
                    // get all filetypes using example search
                    System.out.println("FileTypes via example search");
                    SearchResult<FileType> ftExampleResult = client.searchByExample(new ExampleSearchCriteria<FileType>(
                            new FileType()), null);
                    for (FileType ft : ftExampleResult.getResults()) {
                        System.out.println("FT: " + ft);
                    }

                } else {
                    usage();
                    System.exit(1);
                }
            } else {
                usage();
                System.exit(1);
            }
        } catch (NoEntityMatchingReferenceFault e) {
            System.out.println("No entity for reference: " + e.getCaArrayEntityReference());
            e.printStackTrace();
            System.exit(1);            
        } catch (IncorrectEntityTypeFault e) {
            System.out.println("Referenced entity is of incorrect type: " + e.getCaArrayEntityReference());
            e.printStackTrace();
            System.exit(1);            
        } catch (InvalidReferenceFault e) {
            System.out.println("Generic Invalid reference fault, reference: " + e.getCaArrayEntityReference());
            e.printStackTrace();
            System.exit(1);            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

  public org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse getMultipleResourceProperties(org.oasis.wsrf.properties.GetMultipleResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getMultipleResourceProperties");
    return portType.getMultipleResourceProperties(params);
    }
  }

  public org.oasis.wsrf.properties.GetResourcePropertyResponse getResourceProperty(javax.xml.namespace.QName params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getResourceProperty");
    return portType.getResourceProperty(params);
    }
  }

  public org.oasis.wsrf.properties.QueryResourcePropertiesResponse queryResourceProperties(org.oasis.wsrf.properties.QueryResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"queryResourceProperties");
    return portType.queryResourceProperties(params);
    }
  }

  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForExperiments(gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.UnsupportedCategoryFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"searchForExperiments");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForExperimentsRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForExperimentsRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForExperimentsRequestCriteria criteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForExperimentsRequestCriteria();
    criteriaContainer.setExperimentSearchCriteria(criteria);
    params.setCriteria(criteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForExperimentsRequestLimitOffset limitOffsetContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForExperimentsRequestLimitOffset();
    limitOffsetContainer.setLimitOffset(limitOffset);
    params.setLimitOffset(limitOffsetContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForExperimentsResponse boxedResult = portType.searchForExperiments(params);
    return boxedResult.getSearchResult();
    }
  }

  public gov.nih.nci.caarray.external.v1_0.experiment.Person[] getAllPrincipalInvestigators() throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getAllPrincipalInvestigators");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetAllPrincipalInvestigatorsRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetAllPrincipalInvestigatorsRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetAllPrincipalInvestigatorsResponse boxedResult = portType.getAllPrincipalInvestigators(params);
    return boxedResult.getPerson();
    }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateExperiments(gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria experimentSearchCriteria) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"enumerateExperiments");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateExperimentsRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateExperimentsRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateExperimentsRequestExperimentSearchCriteria experimentSearchCriteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateExperimentsRequestExperimentSearchCriteria();
    experimentSearchCriteriaContainer.setExperimentSearchCriteria(experimentSearchCriteria);
    params.setExperimentSearchCriteria(experimentSearchCriteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateExperimentsResponse boxedResult = portType.enumerateExperiments(params);
    return boxedResult.getEnumerationResponseContainer();
    }
  }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference getFileContentsTransfer(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference fileRef,boolean compress) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getFileContentsTransfer");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetFileContentsTransferRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetFileContentsTransferRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetFileContentsTransferRequestFileRef fileRefContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetFileContentsTransferRequestFileRef();
    fileRefContainer.setCaArrayEntityReference(fileRef);
    params.setFileRef(fileRefContainer);
    params.setCompress(compress);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetFileContentsTransferResponse boxedResult = portType.getFileContentsTransfer(params);
    return boxedResult.getTransferServiceContextReference();
    }
  }

  public gov.nih.nci.caarray.external.v1_0.data.DataSet getDataSet(gov.nih.nci.caarray.external.v1_0.query.DataSetRequest dataSetRequest) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InconsistentDataSetsFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getDataSet");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetDataSetRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetDataSetRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetDataSetRequestDataSetRequest dataSetRequestContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetDataSetRequestDataSetRequest();
    dataSetRequestContainer.setDataSetRequest(dataSetRequest);
    params.setDataSetRequest(dataSetRequestContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetDataSetResponse boxedResult = portType.getDataSet(params);
    return boxedResult.getDataSet();
    }
  }

  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForBiomaterials(gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.UnsupportedCategoryFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"searchForBiomaterials");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForBiomaterialsRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForBiomaterialsRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForBiomaterialsRequestCriteria criteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForBiomaterialsRequestCriteria();
    criteriaContainer.setBiomaterialSearchCriteria(criteria);
    params.setCriteria(criteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForBiomaterialsRequestLimitOffset limitOffsetContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForBiomaterialsRequestLimitOffset();
    limitOffsetContainer.setLimitOffset(limitOffset);
    params.setLimitOffset(limitOffsetContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForBiomaterialsResponse boxedResult = portType.searchForBiomaterials(params);
    return boxedResult.getSearchResult();
    }
  }

  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForHybridizations(gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"searchForHybridizations");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForHybridizationsRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForHybridizationsRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForHybridizationsRequestCriteria criteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForHybridizationsRequestCriteria();
    criteriaContainer.setHybridizationSearchCriteria(criteria);
    params.setCriteria(criteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForHybridizationsRequestLimitOffset limitOffsetContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForHybridizationsRequestLimitOffset();
    limitOffsetContainer.setLimitOffset(limitOffset);
    params.setLimitOffset(limitOffsetContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForHybridizationsResponse boxedResult = portType.searchForHybridizations(params);
    return boxedResult.getSearchResult();
    }
  }

  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForExperimentsByKeyword(gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"searchForExperimentsByKeyword");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForExperimentsByKeywordRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForExperimentsByKeywordRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForExperimentsByKeywordRequestCriteria criteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForExperimentsByKeywordRequestCriteria();
    criteriaContainer.setKeywordSearchCriteria(criteria);
    params.setCriteria(criteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForExperimentsByKeywordRequestLimitOffset limitOffsetContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForExperimentsByKeywordRequestLimitOffset();
    limitOffsetContainer.setLimitOffset(limitOffset);
    params.setLimitOffset(limitOffsetContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForExperimentsByKeywordResponse boxedResult = portType.searchForExperimentsByKeyword(params);
    return boxedResult.getSearchResult();
    }
  }

  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForFiles(gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"searchForFiles");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForFilesRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForFilesRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForFilesRequestCriteria criteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForFilesRequestCriteria();
    criteriaContainer.setFileSearchCriteria(criteria);
    params.setCriteria(criteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForFilesRequestLimitOffset limitOffsetContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForFilesRequestLimitOffset();
    limitOffsetContainer.setLimitOffset(limitOffset);
    params.setLimitOffset(limitOffsetContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForFilesResponse boxedResult = portType.searchForFiles(params);
    return boxedResult.getSearchResult();
    }
  }

  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForBiomaterialsByKeyword(gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"searchForBiomaterialsByKeyword");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForBiomaterialsByKeywordRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForBiomaterialsByKeywordRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForBiomaterialsByKeywordRequestCriteria criteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForBiomaterialsByKeywordRequestCriteria();
    criteriaContainer.setBiomaterialKeywordSearchCriteria(criteria);
    params.setCriteria(criteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForBiomaterialsByKeywordRequestLimitOffset limitOffsetContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForBiomaterialsByKeywordRequestLimitOffset();
    limitOffsetContainer.setLimitOffset(limitOffset);
    params.setLimitOffset(limitOffsetContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForBiomaterialsByKeywordResponse boxedResult = portType.searchForBiomaterialsByKeyword(params);
    return boxedResult.getSearchResult();
    }
  }

  public gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet getMageTabExport(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference experimentRef) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getMageTabExport");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetMageTabExportRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetMageTabExportRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetMageTabExportRequestExperimentRef experimentRefContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetMageTabExportRequestExperimentRef();
    experimentRefContainer.setCaArrayEntityReference(experimentRef);
    params.setExperimentRef(experimentRefContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetMageTabExportResponse boxedResult = portType.getMageTabExport(params);
    return boxedResult.getMageTabFileSet();
    }
  }

  public gov.nih.nci.caarray.external.v1_0.data.QuantitationType[] searchForQuantitationTypes(gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria criteria) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"searchForQuantitationTypes");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForQuantitationTypesRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForQuantitationTypesRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForQuantitationTypesRequestCriteria criteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForQuantitationTypesRequestCriteria();
    criteriaContainer.setQuantitationTypeSearchCriteria(criteria);
    params.setCriteria(criteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchForQuantitationTypesResponse boxedResult = portType.searchForQuantitationTypes(params);
    return boxedResult.getQuantitationType();
    }
  }

  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchByExample(gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"searchByExample");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchByExampleRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchByExampleRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchByExampleRequestCriteria criteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchByExampleRequestCriteria();
    criteriaContainer.setExampleSearchCriteria(criteria);
    params.setCriteria(criteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchByExampleRequestLimitOffset limitOffsetContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchByExampleRequestLimitOffset();
    limitOffsetContainer.setLimitOffset(limitOffset);
    params.setLimitOffset(limitOffsetContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.SearchByExampleResponse boxedResult = portType.searchByExample(params);
    return boxedResult.getSearchResult();
    }
  }

  public gov.nih.nci.caarray.external.v1_0.sample.AnnotationSet getAnnotationSet(gov.nih.nci.caarray.external.v1_0.query.AnnotationSetRequest request) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getAnnotationSet");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetAnnotationSetRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetAnnotationSetRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetAnnotationSetRequestRequest requestContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetAnnotationSetRequestRequest();
    requestContainer.setAnnotationSetRequest(request);
    params.setRequest(requestContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetAnnotationSetResponse boxedResult = portType.getAnnotationSet(params);
    return boxedResult.getAnnotationSet();
    }
  }

  public gov.nih.nci.caarray.external.v1_0.vocabulary.Term[] getTermsForCategory(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference categoryRef,java.lang.String valuePrefix) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getTermsForCategory");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetTermsForCategoryRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetTermsForCategoryRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetTermsForCategoryRequestCategoryRef categoryRefContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetTermsForCategoryRequestCategoryRef();
    categoryRefContainer.setCaArrayEntityReference(categoryRef);
    params.setCategoryRef(categoryRefContainer);
    params.setValuePrefix(valuePrefix);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetTermsForCategoryResponse boxedResult = portType.getTermsForCategory(params);
    return boxedResult.getTerm();
    }
  }

  public gov.nih.nci.caarray.external.v1_0.vocabulary.Category[] getAllCharacteristicCategories(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference experimentRef) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getAllCharacteristicCategories");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetAllCharacteristicCategoriesRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetAllCharacteristicCategoriesRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetAllCharacteristicCategoriesRequestExperimentRef experimentRefContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetAllCharacteristicCategoriesRequestExperimentRef();
    experimentRefContainer.setCaArrayEntityReference(experimentRef);
    params.setExperimentRef(experimentRefContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.GetAllCharacteristicCategoriesResponse boxedResult = portType.getAllCharacteristicCategories(params);
    return boxedResult.getCategory();
    }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateExperimentsByKeyword(gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria criteria) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"enumerateExperimentsByKeyword");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateExperimentsByKeywordRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateExperimentsByKeywordRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateExperimentsByKeywordRequestCriteria criteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateExperimentsByKeywordRequestCriteria();
    criteriaContainer.setKeywordSearchCriteria(criteria);
    params.setCriteria(criteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateExperimentsByKeywordResponse boxedResult = portType.enumerateExperimentsByKeyword(params);
    return boxedResult.getEnumerationResponseContainer();
    }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateBiomaterials(gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria criteria) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"enumerateBiomaterials");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateBiomaterialsRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateBiomaterialsRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateBiomaterialsRequestCriteria criteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateBiomaterialsRequestCriteria();
    criteriaContainer.setBiomaterialSearchCriteria(criteria);
    params.setCriteria(criteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateBiomaterialsResponse boxedResult = portType.enumerateBiomaterials(params);
    return boxedResult.getEnumerationResponseContainer();
    }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateBiomaterialsByKeyword(gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria criteria) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"enumerateBiomaterialsByKeyword");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateBiomaterialsByKeywordRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateBiomaterialsByKeywordRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateBiomaterialsByKeywordRequestCriteria criteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateBiomaterialsByKeywordRequestCriteria();
    criteriaContainer.setBiomaterialKeywordSearchCriteria(criteria);
    params.setCriteria(criteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateBiomaterialsByKeywordResponse boxedResult = portType.enumerateBiomaterialsByKeyword(params);
    return boxedResult.getEnumerationResponseContainer();
    }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateHybridizations(gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria criteria) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"enumerateHybridizations");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateHybridizationsRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateHybridizationsRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateHybridizationsRequestCriteria criteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateHybridizationsRequestCriteria();
    criteriaContainer.setHybridizationSearchCriteria(criteria);
    params.setCriteria(criteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateHybridizationsResponse boxedResult = portType.enumerateHybridizations(params);
    return boxedResult.getEnumerationResponseContainer();
    }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateFiles(gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria criteria) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"enumerateFiles");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateFilesRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateFilesRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateFilesRequestCriteria criteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateFilesRequestCriteria();
    criteriaContainer.setFileSearchCriteria(criteria);
    params.setCriteria(criteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateFilesResponse boxedResult = portType.enumerateFiles(params);
    return boxedResult.getEnumerationResponseContainer();
    }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateByExample(gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria criteria) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"enumerateByExample");
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateByExampleRequest params = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateByExampleRequest();
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateByExampleRequestCriteria criteriaContainer = new gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateByExampleRequestCriteria();
    criteriaContainer.setExampleSearchCriteria(criteria);
    params.setCriteria(criteriaContainer);
    gov.nih.nci.caarray.services.external.v1_0.grid.stubs.EnumerateByExampleResponse boxedResult = portType.enumerateByExample(params);
    return boxedResult.getEnumerationResponseContainer();
    }
  }

}
