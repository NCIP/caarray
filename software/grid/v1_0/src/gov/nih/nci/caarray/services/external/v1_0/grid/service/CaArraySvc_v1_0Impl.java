//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.external.v1_0.grid.service;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileStreamableContents;
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
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.NoEntityMatchingReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.UnsupportedCategoryException;
import gov.nih.nci.caarray.services.external.v1_0.data.DataTransferException;
import gov.nih.nci.caarray.services.external.v1_0.data.InconsistentDataSetsException;
import gov.nih.nci.caarray.services.external.v1_0.data.JavaDataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InconsistentDataSetsFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InvalidInputFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InvalidReferenceFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.UnsupportedCategoryFault;
import gov.nih.nci.caarray.services.external.v1_0.search.JavaSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;
import gov.nih.nci.cagrid.wsenum.utils.EnumerateResponseFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataDescriptor;
import org.oasis.wsrf.faults.BaseFaultType;
import org.oasis.wsrf.faults.BaseFaultTypeDescription;

/**
 * Implementation of the v1.0 of the CaArray grid service
 *
 * @created by Introduce Toolkit version 1.2
 */
public class CaArraySvc_v1_0Impl extends CaArraySvc_v1_0ImplBase {
    private CaArrayServer caArrayServer;
    private SearchApiUtils searchUtils;
    private JavaDataApiUtils dataUtils;

     public CaArraySvc_v1_0Impl() throws RemoteException {

    }

    private synchronized CaArrayServer getCaArrayServer() throws RemoteException {
        if (caArrayServer == null) {
            try {
                String jndiUrl = getJndiUrl();
                if (jndiUrl == null) {
                    throw new RemoteException("Could not connect to server: invalid JNDI configuration");
                }

                CaArrayServer s = new CaArrayServer(jndiUrl);
                s.connect();
                caArrayServer = s;
            } catch (IOException e) {
                throw new RemoteException("Could not connect to server", e);
            } catch (ServerConnectionException e) {
                throw new RemoteException("Could not connect to server", e);
            }
        }
        return caArrayServer;
    }

    private synchronized SearchApiUtils getSearchUtils() throws RemoteException {
        if (searchUtils == null) {
            searchUtils = new JavaSearchApiUtils(getCaArrayServer().getSearchService());
        }
        return searchUtils;
    }

    private synchronized JavaDataApiUtils getDataUtils() throws RemoteException {
        if (dataUtils == null) {
            dataUtils = new JavaDataApiUtils(getCaArrayServer().getDataService());
        }
        return dataUtils;
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
            return getCaArrayServer().getSearchService().searchForExperiments(criteria, limitOffset);
        } catch (InvalidInputException e) {
            throw toFault(e);
        } 
    }

  public gov.nih.nci.caarray.external.v1_0.experiment.Person[] getAllPrincipalInvestigators() throws RemoteException {
        List<Person> pis = getCaArrayServer().getSearchService().getAllPrincipalInvestigators();
        return pis.toArray(new Person[pis.size()]);
    }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateExperiments(gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria experimentSearchCriteria) throws RemoteException {
        try {
            return EnumerateResponseFactory.createEnumerationResponse(new SearchEnumIterator<Experiment>(
                    Experiment.class, getSearchUtils().experimentsByCriteria(experimentSearchCriteria).iterator()));
        } catch (Exception e) {
            throw new RemoteException("Unable to create enumeration", e);
        }
    }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference getFileContentsTransfer(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference fileRef,boolean compress) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault {
      return stageFileContentsWithRmiStream(fileRef, compress);
  }

    private TransferServiceContextReference stageFileContentsWithRmiStream(CaArrayEntityReference fileRef,
            boolean compressed) throws InvalidInputFault, DataStagingFault {
      OutputStream ostream = null;
      try {
          java.io.File tempFile = java.io.File.createTempFile("stagedFile", null);
          FileStreamableContents fsContents = caArrayServer.getDataService().streamFileContents(fileRef, compressed);
          ostream = FileUtils.openOutputStream(tempFile);
          JavaDataApiUtils.readFully(fsContents.getContentStream(), ostream, false);          
            return TransferServiceHelper.createTransferContext(tempFile, new DataDescriptor(false, fsContents
                    .getMetadata().getName()), true);
      } catch (InvalidReferenceException e) {
          throw toFault(e);
      } catch (DataTransferException e) {
          throw mapExceptionToFault(e, DataStagingFault.class);
      } catch (IOException e) {
          throw mapExceptionToFault(e, DataStagingFault.class);
      } finally {
          if (ostream != null) {
              IOUtils.closeQuietly(ostream);
          }
      }
  }

  public gov.nih.nci.caarray.external.v1_0.data.DataSet getDataSet(gov.nih.nci.caarray.external.v1_0.query.DataSetRequest dataSetRequest) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InconsistentDataSetsFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault {
        try {
            return getCaArrayServer().getDataService().getDataSet(dataSetRequest);
        } catch (InvalidInputException e) {
            throw toFault(e);
        } catch (InconsistentDataSetsException e) {
            throw mapExceptionToFault(e, InconsistentDataSetsFault.class);
        }
  }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForExperimentsByKeyword(gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException {
      return getCaArrayServer().getSearchService().searchForExperimentsByKeyword(criteria, limitOffset);
   }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForFiles(gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
      try {
          return getCaArrayServer().getSearchService().searchForFiles(criteria, limitOffset);
      } catch (InvalidInputException e) {
          throw toFault(e);
      }
  }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForBiomaterialsByKeyword(gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException {
      return getCaArrayServer().getSearchService().searchForBiomaterialsByKeyword(criteria, limitOffset);
  }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForBiomaterials(gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.UnsupportedCategoryFault {
      try {
          return getCaArrayServer().getSearchService().searchForBiomaterials(criteria, limitOffset);
        } catch (InvalidInputException e) {
            throw toFault(e);
        }
  }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchForHybridizations(gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
      try {
          return getCaArrayServer().getSearchService().searchForHybridizations(criteria, limitOffset);
        } catch (InvalidInputException e) {
            throw toFault(e);
        } 
  }

  public gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet getMageTabExport(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference experimentRef) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault {
      try {
          return getCaArrayServer().getDataService().exportMageTab(experimentRef);
      } catch (InvalidReferenceException e) {
          throw toFault(e);
      } catch (DataTransferException e) {
          throw mapExceptionToFault(e, DataStagingFault.class);

    } 
  }

  public gov.nih.nci.caarray.external.v1_0.data.QuantitationType[] searchForQuantitationTypes(gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria criteria) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
        try {
            List<QuantitationType> types = getCaArrayServer().getSearchService().searchForQuantitationTypes(criteria);
            return types.toArray(new QuantitationType[types.size()]);
        } catch (InvalidInputException e) {
            throw toFault(e);
        }
  }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.caarray.external.v1_0.query.SearchResult searchByExample(gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria criteria,gov.nih.nci.caarray.external.v1_0.query.LimitOffset limitOffset) throws RemoteException {
      try {
        return getCaArrayServer().getSearchService().searchByExample(criteria, limitOffset);
      } catch (InvalidInputException e) {
          throw toFault(e);
      }
  }
  
  public gov.nih.nci.caarray.external.v1_0.sample.AnnotationSet getAnnotationSet(gov.nih.nci.caarray.external.v1_0.query.AnnotationSetRequest request) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
      try {
        return getCaArrayServer().getSearchService().getAnnotationSet(request);
    } catch (InvalidInputException e) {
        throw toFault(e);
    }
  }

  public gov.nih.nci.caarray.external.v1_0.vocabulary.Term[] getTermsForCategory(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference categoryRef,java.lang.String valuePrefix) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault {
      try {
          List<Term> terms = getCaArrayServer().getSearchService().getTermsForCategory(categoryRef, valuePrefix);
          return terms.toArray(new Term[terms.size()]);
      } catch (InvalidInputException e) {
          throw toFault(e);
      }
  }

  public gov.nih.nci.caarray.external.v1_0.vocabulary.Category[] getAllCharacteristicCategories(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference experimentRef) throws RemoteException, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault, gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault {
      try {
          List<Category> categories = getCaArrayServer().getSearchService().getAllCharacteristicCategories(experimentRef);
          return categories.toArray(new Category[categories.size()]);
      } catch (InvalidInputException e) {
          throw toFault(e);
      }
  }
  
    private InvalidInputFault toFault(InvalidInputException e) {
        InvalidInputFault fault;
        if (e instanceof InvalidReferenceException){
            InvalidReferenceFault tmp;
            if (e instanceof NoEntityMatchingReferenceException) {
                tmp = mapExceptionToFault(e, NoEntityMatchingReferenceFault.class);
            } else if (e instanceof IncorrectEntityTypeException) {
                tmp = mapExceptionToFault(e, IncorrectEntityTypeFault.class);
            } else {
                tmp = mapExceptionToFault(e, InvalidReferenceFault.class);
            }
            tmp.setCaArrayEntityReference(((InvalidReferenceException)e).getReference());
            fault = tmp;
        } else if (e instanceof UnsupportedCategoryException){
            fault = mapExceptionToFault(e, UnsupportedCategoryFault.class);
        } else {
            fault = mapExceptionToFault(e, InvalidInputFault.class);
        }
      
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
                  Experiment.class, getSearchUtils().experimentsByKeyword(criteria).iterator()));
      } catch (Exception e) {
          throw new RemoteException("Unable to create enumeration", e);
      }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateBiomaterials(gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria criteria) throws RemoteException {
      try {
          return EnumerateResponseFactory.createEnumerationResponse(new SearchEnumIterator<Biomaterial>(
                  Biomaterial.class, getSearchUtils().biomaterialsByCriteria(criteria).iterator()));
      } catch (Exception e) {
          throw new RemoteException("Unable to create enumeration", e);
      }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateBiomaterialsByKeyword(gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria criteria) throws RemoteException {
      try {
          return EnumerateResponseFactory.createEnumerationResponse(new SearchEnumIterator<Biomaterial>(
                  Biomaterial.class, getSearchUtils().biomaterialsByKeyword(criteria).iterator()));
      } catch (Exception e) {
          throw new RemoteException("Unable to create enumeration", e);
      }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateHybridizations(gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria criteria) throws RemoteException {
      try {
          return EnumerateResponseFactory.createEnumerationResponse(new SearchEnumIterator<Hybridization>(
                  Hybridization.class, getSearchUtils().hybridizationsByCriteria(criteria).iterator()));
      } catch (Exception e) {
          throw new RemoteException("Unable to create enumeration", e);
      }
  }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateFiles(gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria criteria) throws RemoteException {
      try {
          return EnumerateResponseFactory.createEnumerationResponse(new SearchEnumIterator<File>(
                  File.class, getSearchUtils().filesByCriteria(criteria).iterator()));
      } catch (Exception e) {
          throw new RemoteException("Unable to create enumeration", e);
      }
  }

  @SuppressWarnings("unchecked")
  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateByExample(gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria criteria) throws RemoteException {
      try {
          return EnumerateResponseFactory.createEnumerationResponse(new SearchEnumIterator(
                  criteria.getExample().getClass(), getSearchUtils().byExample(criteria).iterator()));
      } catch (Exception e) {
          throw new RemoteException("Unable to create enumeration", e);
      }
  }

}
