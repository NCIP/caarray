package gov.nih.nci.caarray.services.external.v1_0.grid.service;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.data.DataTransferException;
import gov.nih.nci.caarray.services.external.v1_0.data.JavaDataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.data.InconsistentDataSetsException;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchUtils;
import gov.nih.nci.cagrid.wsenum.utils.EnumerateResponseFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;

/**
 * Implementation of the v1.0 of the CaArray grid service
 *
 * @created by Introduce Toolkit version 1.2
 */
public class CaArraySvc_v1_0Impl extends CaArraySvc_v1_0ImplBase {
    final CaArrayServer caArrayServer;
    private SearchUtils searchUtils;
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
            searchUtils = new SearchUtils(caArrayServer.getSearchService());
            dataUtils = new JavaDataApiUtils(caArrayServer.getDataService());
        } catch (ServerConnectionException e) {
            throw new RemoteException("Could not connect to server", e);
        } catch (IOException e) {
            throw new RemoteException("Could not connect to server", e);
        }
    }

  public gov.nih.nci.caarray.external.v1_0.experiment.Experiment[] searchForExperiments(gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria criteria) throws RemoteException {
        try {
            List<Experiment> experiments = caArrayServer.getSearchService().searchForExperiments(criteria, null).getResults();
            return experiments.toArray(new Experiment[experiments.size()]);
        } catch (InvalidReferenceException e) {
            // TODO: use correct exception
            throw new RemoteException("invalid reference", e);
        }
    }

  public gov.nih.nci.caarray.external.v1_0.experiment.Person[] getAllPrincipalInvestigators() throws RemoteException {
        List<Person> pis = caArrayServer.getSearchService().getAllPrincipalInvestigators();
        return pis.toArray(new Person[pis.size()]);
    }

  public gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity getByReference(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference reference) throws RemoteException {
      try {
            return caArrayServer.getSearchService().getByReference(reference);
        } catch (InvalidReferenceException e) {
            // TODO: use correct exception
            throw new RemoteException("invalid reference", e);
        }
    }

  public gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity[] getByReferences(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference[] references) throws RemoteException {
        try {
            List<AbstractCaArrayEntity> entities = caArrayServer.getSearchService().getByReferences(
                    Arrays.asList(references));
            return entities.toArray(new AbstractCaArrayEntity[entities.size()]);
        } catch (InvalidReferenceException e) {
            // TODO: use correct exception
            throw new RemoteException("invalid reference", e);
        }
    }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateExperiments(gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria experimentSearchCriteria) throws RemoteException {
        try {
            return EnumerateResponseFactory.createEnumerationResponse(new SearchEnumIterator<Experiment>(
                    Experiment.class, searchUtils.experimentsByCriteria(experimentSearchCriteria).iterate()));
        } catch (Exception e) {
            throw new RemoteException("Unable to create enumeration", e);
        }
    }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference getFileContentsZipTransfer(gov.nih.nci.caarray.external.v1_0.query.FileDownloadRequest fileDownloadRequest,boolean compressIndividually) throws RemoteException {
      try {
            File file = dataUtils.downloadFileContentsZipToTempFile(fileDownloadRequest, compressIndividually);
            return TransferServiceHelper.createTransferContext(file, null, true);
      } catch (InvalidReferenceException e) {
            // TODO throw more specific exception
            throw new RemoteException("Error retrieving file contents", e);
      } catch (DataTransferException e) {
            // TODO throw more specific exception
            throw new RemoteException("Error retrieving file contents", e);
      } catch (IOException e) {
            // TODO throw more specific exception
            throw new RemoteException("Error retrieving file contents", e);
      }
  }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference[] getFileContentsTransfers(gov.nih.nci.caarray.external.v1_0.query.FileDownloadRequest fileDownloadRequest,boolean compress) throws RemoteException {
      TransferServiceContextReference[] refs = new TransferServiceContextReference[fileDownloadRequest.getFiles().size()];
      int i = 0;
      for (CaArrayEntityReference fileRef : fileDownloadRequest.getFiles()) {
          refs[i++] = getFileContentsTransfer(fileRef, compress);
      }
      return refs;
  }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference getFileContentsTransfer(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference fileRef,boolean compress) throws RemoteException {
      try {
          return stageFileContentsWithRmiStream(caArrayServer, fileRef, compress);
      } catch (IOException e) {
          throw new RemoteException("Error retrieving file contents: " + e);
      }
  }

  private TransferServiceContextReference stageFileContentsWithRmiStream(CaArrayServer server,
            CaArrayEntityReference fileRef, boolean compress) throws IOException {
      try {
          TransferServiceContextReference ref = null;
          File retrievedFile = dataUtils.downloadFileContentsToTempFile(fileRef, compress);
          if (retrievedFile != null) {
              ref = TransferServiceHelper.createTransferContext(retrievedFile, null, true);
          }
          return ref;
      } catch (InvalidReferenceException e) {
          // TODO throw more specific exception
          throw new RemoteException("Error retrieving file contents", e);
      } catch (DataTransferException e) {
          // TODO Auto-generated catch block
          throw new RemoteException("Error retrieving file contents", e);
      }
  }

  public gov.nih.nci.caarray.external.v1_0.data.DataSet getDataSet(gov.nih.nci.caarray.external.v1_0.query.DataSetRequest dataSetRequest) throws RemoteException {
        try {
            return caArrayServer.getDataService().getDataSet(dataSetRequest);
        } catch (InvalidReferenceException e) {
            // TODO: use correct exception
            throw new RemoteException("invalid reference", e);
        } catch (InconsistentDataSetsException e) {
            // TODO: use correct exception
            throw new RemoteException("invalid reference", e);            
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

  public gov.nih.nci.caarray.external.v1_0.experiment.Experiment[] searchForExperimentsByKeyword(gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria criteria) throws RemoteException {
      List<Experiment> experiments = caArrayServer.getSearchService().searchForExperimentsByKeyword(criteria, null).getResults();
      return experiments.toArray(new Experiment[experiments.size()]);
  }

  public gov.nih.nci.caarray.external.v1_0.data.DataFile[] searchForFiles(gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria fileSearchCriteria) throws RemoteException {
      try {
            List<DataFile> files = caArrayServer.getSearchService().searchForFiles(fileSearchCriteria, null).getResults();
            return files.toArray(new DataFile[files.size()]);
        } catch (InvalidReferenceException e) {
            // TODO: use correct exception
            throw new RemoteException("invalid reference", e);
        }
  }

  public gov.nih.nci.caarray.external.v1_0.sample.Biomaterial[] searchForBiomaterialsByKeyword(gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria criteria) throws RemoteException {
      List<Biomaterial> bms = caArrayServer.getSearchService().searchForBiomaterialsByKeyword(criteria, null).getResults();
      return bms.toArray(new Biomaterial[bms.size()]);
  }

  public gov.nih.nci.caarray.external.v1_0.sample.Biomaterial[] searchForBiomaterials(gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria criteria) throws RemoteException {
      try {
            List<Biomaterial> bms = caArrayServer.getSearchService().searchForBiomaterials(criteria, null).getResults();
            return bms.toArray(new Biomaterial[bms.size()]);
        } catch (InvalidReferenceException e) {
            throw new RemoteException("Error retrieving samples: " + e);
        } 
  }

  public gov.nih.nci.caarray.external.v1_0.sample.Hybridization[] searchForHybridizations(gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria criteria) throws RemoteException {
      try {
            List<Hybridization> hybs = caArrayServer.getSearchService().searchForHybridizations(criteria, null).getResults();
            return hybs.toArray(new Hybridization[hybs.size()]);
        } catch (InvalidReferenceException e) {
            throw new RemoteException("Error retrieving hybs: " + e);
        } 
  }

  public gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet getMageTabExport(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference experimentRef) throws RemoteException {
      try {
          MageTabFileSet mageTabSet = caArrayServer.getDataService().exportMageTab(experimentRef);
          return mageTabSet;
      } catch (InvalidReferenceException e) {
          throw new RemoteException("Error exporting magetab: " + e);
      } catch (DataTransferException e) {
          throw new RemoteException("Error exporting magetab: " + e);
    } 
  }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference getMageTabZipTransfer(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference experimentRef,boolean compressIndividually) throws RemoteException {
        OutputStream fos = null;
        InputStream is = null;
        try {
            File file = File.createTempFile("caarray_magetab_zip_transfer", null);
            fos = FileUtils.openOutputStream(file);
            RemoteInputStream ris = caArrayServer.getDataService().streamMageTabZip(experimentRef, compressIndividually);
            is = RemoteInputStreamClient.wrap(ris);
            IOUtils.copy(is, fos);
            return TransferServiceHelper.createTransferContext(file, null, true);
        } catch (InvalidReferenceException e) {
            // TODO throw more specific exception
            throw new RemoteException("Error retrieving file contents", e);
        } catch (DataTransferException e) {
            // TODO throw more specific exception
            throw new RemoteException("Error retrieving file contents", e);
        } catch (IOException e) {
            // TODO throw more specific exception
            throw new RemoteException("Error retrieving file contents", e);
        } finally {
            if (is != null) {
                IOUtils.closeQuietly(is);
            }
            if (fos != null) {
                IOUtils.closeQuietly(fos);
            }
        }
    }

  public gov.nih.nci.caarray.external.v1_0.data.QuantitationType[] searchForQuantitationTypes(gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria criteria) throws RemoteException {
        try {
            List<QuantitationType> types = caArrayServer.getSearchService().searchForQuantitationTypes(criteria);
            return types.toArray(new QuantitationType[types.size()]);
        } catch (InvalidReferenceException e) {
            // TODO: use correct exception
            throw new RemoteException("invalid reference", e);
        }
  }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchByExample(gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria exampleSearchCriteria) throws RemoteException {
        return caArrayServer.getSearchService().searchByExample(
                (ExampleSearchCriteria<AbstractCaArrayEntity>) exampleSearchCriteria, null);
    }
  
  public gov.nih.nci.caarray.external.v1_0.sample.AnnotationSet getAnnotationSet(gov.nih.nci.caarray.external.v1_0.query.AnnotationSetRequest request) throws RemoteException {
      try {
        return caArrayServer.getSearchService().getAnnotationSet(request);
    } catch (InvalidReferenceException e) {
        // TODO use correct exception
        throw new RemoteException("invalid reference", e);
    }
  }

  public gov.nih.nci.caarray.external.v1_0.vocabulary.Term[] getTermsForCategory(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference categoryRef,java.lang.String valuePrefix) throws RemoteException {
      try {
          List<Term> terms = caArrayServer.getSearchService().getTermsForCategory(categoryRef, valuePrefix);
          return terms.toArray(new Term[terms.size()]);
      } catch (InvalidReferenceException e) {
          // TODO use correct exception
          throw new RemoteException("invalid reference", e);
      }
  }

  public gov.nih.nci.caarray.external.v1_0.vocabulary.Category[] getAllCharacteristicCategories(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference experimentRef) throws RemoteException {
      try {
          List<Category> categories = caArrayServer.getSearchService().getAllCharacteristicCategories(experimentRef);
          return categories.toArray(new Category[categories.size()]);
      } catch (InvalidReferenceException e) {
          // TODO use correct exception
          throw new RemoteException("invalid reference", e);
      }
  }

}
