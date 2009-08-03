package gov.nih.nci.caarray.services.external.v1_0.grid.service;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.IncorrectEntityTypeException;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.NoEntityMatchingReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.UnsupportedCategoryException;
import gov.nih.nci.caarray.services.external.v1_0.data.DataTransferException;
import gov.nih.nci.caarray.services.external.v1_0.data.InconsistentDataSetsException;
import gov.nih.nci.caarray.services.external.v1_0.data.JavaDataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InconsistentDataSetsFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InvalidReferenceFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.UnsupportedCategoryFault;
import gov.nih.nci.caarray.services.external.v1_0.search.JavaSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;
import gov.nih.nci.cagrid.wsenum.utils.EnumerateResponseFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.oasis.wsrf.faults.BaseFaultType;
import org.oasis.wsrf.faults.BaseFaultTypeDescription;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;

/**
 * Implementation of the v1.0 of the CaArray grid service
 *
 * @created by Introduce Toolkit version 1.2
 */
public class CaArraySvc_v1_0Impl extends CaArraySvc_v1_0ImplBase {
    final CaArrayServer caArrayServer;
    private SearchApiUtils searchUtils;
    private JavaDataApiUtils dataUtils;

    public CaArraySvc_v1_0Impl() throws RemoteException {
        super();

        try {
            String jndiUrl = getJndiUrl();
            if (jndiUrl == null) {
                throw new RemoteException("Could not connect to server: invalid JNDI configuration");
            }

            caArrayServer = new CaArrayServer(jndiUrl);
            caArrayServer.connect();
            searchUtils = new JavaSearchApiUtils(caArrayServer.getSearchService());
            dataUtils = new JavaDataApiUtils(caArrayServer.getDataService());
        } catch (ServerConnectionException e) {
            throw new RemoteException("Could not connect to server", e);
        } catch (IOException e) {
            throw new RemoteException("Could not connect to server", e);
        }
    }

    public static String getJndiUrl() {
        try {
            final Properties jndiProp = new Properties();
            jndiProp.load(CaArraySvc_v1_0Impl.class.getResourceAsStream("/gov/nih/nci/caarray/services/external/v1_0/grid/jndi.properties"));

            return jndiProp.getProperty("java.naming.provider.url");
        } catch (IOException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForExperiments(gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.UnsupportedCategoryFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
        try {
            return caArrayServer.getSearchService().searchForExperiments(criteria, limitOffset);
        } catch (InvalidReferenceException e) {
            throw toFault(e);
        } catch (UnsupportedCategoryException e) {
            throw toFault(e);
        } 
    }

  public gov.nih.nci.caarray.external.v1_0.experiment.Person[] getAllPrincipalInvestigators() throws RemoteException {
        List<Person> pis = caArrayServer.getSearchService().getAllPrincipalInvestigators();
        return pis.toArray(new Person[pis.size()]);
    }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateExperiments(gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria experimentSearchCriteria) throws RemoteException {
        try {
            return EnumerateResponseFactory.createEnumerationResponse(new SearchEnumIterator<Experiment>(
                    Experiment.class, searchUtils.experimentsByCriteria(experimentSearchCriteria).iterator()));
        } catch (Exception e) {
            throw new RemoteException("Unable to create enumeration", e);
        }
    }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference getFileContentsZipTransfer(gov.nih.nci.caarray.external.v1_0.query.FileDownloadRequest fileDownloadRequest,boolean compressIndividually) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
      try {
            java.io.File file = dataUtils.downloadFileContentsZipToTempFile(fileDownloadRequest, compressIndividually);
            return TransferServiceHelper.createTransferContext(file, null, true);
      } catch (InvalidReferenceException e) {
          throw toFault(e);
      } catch (DataTransferException e) {
          throw mapExceptionToFault(e, DataStagingFault.class);
      } catch (IOException e) {
          throw mapExceptionToFault(e, DataStagingFault.class);
      }
  }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference[] getFileContentsTransfers(gov.nih.nci.caarray.external.v1_0.query.FileDownloadRequest fileDownloadRequest,boolean compress) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault {
      TransferServiceContextReference[] refs = new TransferServiceContextReference[fileDownloadRequest.getFiles().size()];
      int i = 0;
      for (CaArrayEntityReference fileRef : fileDownloadRequest.getFiles()) {
          refs[i++] = getFileContentsTransfer(fileRef, compress);
      }
      return refs;
  }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference getFileContentsTransfer(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference fileRef,boolean compress) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault {
      return stageFileContentsWithRmiStream(caArrayServer, fileRef, compress);
  }

  private TransferServiceContextReference stageFileContentsWithRmiStream(CaArrayServer server,
            CaArrayEntityReference fileRef, boolean compress) throws InvalidReferenceFault, DataStagingFault {
      try {
          TransferServiceContextReference ref = null;
          java.io.File retrievedFile = dataUtils.downloadFileContentsToTempFile(fileRef, compress);
          if (retrievedFile != null) {
              ref = TransferServiceHelper.createTransferContext(retrievedFile, null, true);
          }
          return ref;
      } catch (InvalidReferenceException e) {
          throw toFault(e);
      } catch (DataTransferException e) {
          throw mapExceptionToFault(e, DataStagingFault.class);
      } catch (IOException e) {
          throw mapExceptionToFault(e, DataStagingFault.class);
    }
  }

  public gov.nih.nci.caarray.external.v1_0.data.DataSet getDataSet(gov.nih.nci.caarray.external.v1_0.query.DataSetRequest dataSetRequest) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InconsistentDataSetsFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault {
        try {
            return caArrayServer.getDataService().getDataSet(dataSetRequest);
        } catch (InvalidReferenceException e) {
            throw toFault(e);
        } catch (InconsistentDataSetsException e) {
            throw mapExceptionToFault(e, InconsistentDataSetsFault.class);
        }
  }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForExperimentsByKeyword(gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException {
      return caArrayServer.getSearchService().searchForExperimentsByKeyword(criteria, limitOffset);
   }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForFiles(gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
      try {
          return caArrayServer.getSearchService().searchForFiles(criteria, limitOffset);
        } catch (InvalidReferenceException e) {
            throw toFault(e);
        }
  }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForBiomaterialsByKeyword(gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException {
      return caArrayServer.getSearchService().searchForBiomaterialsByKeyword(criteria, limitOffset);
  }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForBiomaterials(gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.UnsupportedCategoryFault {
      try {
          return caArrayServer.getSearchService().searchForBiomaterials(criteria, limitOffset);
        } catch (InvalidReferenceException e) {
            throw toFault(e);
        } catch (UnsupportedCategoryException e) {
            throw toFault(e);
        }
  }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForHybridizations(gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
      try {
          return caArrayServer.getSearchService().searchForHybridizations(criteria, limitOffset);
        } catch (InvalidReferenceException e) {
            throw toFault(e);
        } 
  }

  public gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet getMageTabExport(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference experimentRef) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault {
      try {
          return caArrayServer.getDataService().exportMageTab(experimentRef);
      } catch (InvalidReferenceException e) {
          throw toFault(e);
      } catch (DataTransferException e) {
          throw mapExceptionToFault(e, DataStagingFault.class);

    } 
  }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference getMageTabZipTransfer(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference experimentRef,boolean compressIndividually) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault {
        OutputStream fos = null;
        InputStream is = null;
        try {
            java.io.File file = java.io.File.createTempFile("caarray_magetab_zip_transfer", null);
            fos = FileUtils.openOutputStream(file);
            RemoteInputStream ris = caArrayServer.getDataService().streamMageTabZip(experimentRef, compressIndividually);
            is = RemoteInputStreamClient.wrap(ris);
            IOUtils.copy(is, fos);
            return TransferServiceHelper.createTransferContext(file, null, true);
        } catch (InvalidReferenceException e) {
            throw toFault(e);
        } catch (DataTransferException e) {
            throw mapExceptionToFault(e, DataStagingFault.class);
        } catch (IOException e) {
            throw mapExceptionToFault(e, DataStagingFault.class);
        } finally {
            if (is != null) {
                IOUtils.closeQuietly(is);
            }
            if (fos != null) {
                IOUtils.closeQuietly(fos);
            }
        }
    }

  public gov.nih.nci.caarray.external.v1_0.data.QuantitationType[] searchForQuantitationTypes(gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria criteria) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
        try {
            List<QuantitationType> types = caArrayServer.getSearchService().searchForQuantitationTypes(criteria);
            return types.toArray(new QuantitationType[types.size()]);
        } catch (InvalidReferenceException e) {
            throw toFault(e);
        }
  }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchByExample(gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException {
      return caArrayServer.getSearchService().searchByExample(criteria, limitOffset);
  }
  
  public gov.nih.nci.caarray.external.v1_0.sample.AnnotationSet getAnnotationSet(gov.nih.nci.caarray.external.v1_0.query.AnnotationSetRequest request) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
      try {
        return caArrayServer.getSearchService().getAnnotationSet(request);
    } catch (InvalidReferenceException e) {
        throw toFault(e);
    }
  }

  public gov.nih.nci.caarray.external.v1_0.vocabulary.Term[] getTermsForCategory(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference categoryRef,java.lang.String valuePrefix) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
      try {
          List<Term> terms = caArrayServer.getSearchService().getTermsForCategory(categoryRef, valuePrefix);
          return terms.toArray(new Term[terms.size()]);
      } catch (InvalidReferenceException e) {
          throw toFault(e);
      }
  }

  public gov.nih.nci.caarray.external.v1_0.vocabulary.Category[] getAllCharacteristicCategories(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference experimentRef) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault {
      try {
          List<Category> categories = caArrayServer.getSearchService().getAllCharacteristicCategories(experimentRef);
          return categories.toArray(new Category[categories.size()]);
      } catch (InvalidReferenceException e) {
          throw toFault(e);
      }
  }
  
  private InvalidReferenceFault toFault(InvalidReferenceException e) {
      InvalidReferenceFault fault;
      if (e instanceof NoEntityMatchingReferenceException) {
          fault = mapExceptionToFault(e, NoEntityMatchingReferenceFault.class);
      } else if (e instanceof IncorrectEntityTypeException) {
          fault = mapExceptionToFault(e, IncorrectEntityTypeFault.class);
      } else {
          fault = mapExceptionToFault(e, InvalidReferenceFault.class);
      }
      fault.setCaArrayEntityReference(e.getReference());
      return fault;
  }

  private UnsupportedCategoryFault toFault(UnsupportedCategoryException e) {
      UnsupportedCategoryFault fault = mapExceptionToFault(e, UnsupportedCategoryFault.class);
      fault.setCaArrayEntityReference(e.getCategory());
      return fault;
  }

  private <T extends BaseFaultType> T mapExceptionToFault(Exception e, Class<T> faultType) {
        try {
            T fault = faultType.newInstance();
            if (e.getMessage() != null) {
                fault.setDescription(new BaseFaultTypeDescription[] { new BaseFaultTypeDescription(e.getMessage()) });
            }
            return fault;
        } catch (InstantiationException e1) {
            throw new IllegalArgumentException("Could not instantiate fault of type " + faultType);
        } catch (IllegalAccessException e1) {
            throw new IllegalArgumentException("Could not instantiate fault of type " + faultType);
        }
    }
  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateExperimentsByKeyword(gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria criteria) throws RemoteException {
      try {
          return EnumerateResponseFactory.createEnumerationResponse(new SearchEnumIterator<Experiment>(
                  Experiment.class, searchUtils.experimentsByKeyword(criteria).iterator()));
      } catch (Exception e) {
          throw new RemoteException("Unable to create enumeration", e);
      }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateBiomaterials(gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria criteria) throws RemoteException {
      try {
          return EnumerateResponseFactory.createEnumerationResponse(new SearchEnumIterator<Biomaterial>(
                  Biomaterial.class, searchUtils.biomaterialsByCriteria(criteria).iterator()));
      } catch (Exception e) {
          throw new RemoteException("Unable to create enumeration", e);
      }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateBiomaterialsByKeyword(gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria criteria) throws RemoteException {
      try {
          return EnumerateResponseFactory.createEnumerationResponse(new SearchEnumIterator<Biomaterial>(
                  Biomaterial.class, searchUtils.biomaterialsByKeyword(criteria).iterator()));
      } catch (Exception e) {
          throw new RemoteException("Unable to create enumeration", e);
      }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateHybridizations(gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria criteria) throws RemoteException {
      try {
          return EnumerateResponseFactory.createEnumerationResponse(new SearchEnumIterator<Hybridization>(
                  Hybridization.class, searchUtils.hybridizationsByCriteria(criteria).iterator()));
      } catch (Exception e) {
          throw new RemoteException("Unable to create enumeration", e);
      }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateFiles(gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria criteria) throws RemoteException {
      try {
          return EnumerateResponseFactory.createEnumerationResponse(new SearchEnumIterator<File>(
                  File.class, searchUtils.filesByCriteria(criteria).iterator()));
      } catch (Exception e) {
          throw new RemoteException("Unable to create enumeration", e);
      }
  }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateByExample(gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria criteria) throws RemoteException {
      try {
          return EnumerateResponseFactory.createEnumerationResponse(new SearchEnumIterator(
                  criteria.getExample().getClass(), searchUtils.byExample(criteria).iterator()));
      } catch (Exception e) {
          throw new RemoteException("Unable to create enumeration", e);
      }
  }

}
