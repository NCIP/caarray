package gov.nih.nci.caarray.services.external.v1_0.grid.service;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.cagrid.wsenum.utils.EnumerateResponseFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataDescriptor;

import com.healthmarketscience.rmiio.RemoteOutputStreamServer;
import com.healthmarketscience.rmiio.SimpleRemoteOutputStream;

/**
 * Implementation of the v1.1 of the CaArray grid service
 *
 * @created by Introduce Toolkit version 1.2
 */
public class CaArraySvc_v1_0Impl extends CaArraySvc_v1_0ImplBase {
    final CaArrayServer caArrayServer;

    public CaArraySvc_v1_0Impl() throws RemoteException {
        super();

        try {
            String jndiUrl = getJndiUrl();
            if (jndiUrl == null) {
                throw new RemoteException("Could not connect to server: invalid JNDI configuration");
            }

            caArrayServer = new CaArrayServer(jndiUrl);
            caArrayServer.connect();
        } catch (ServerConnectionException e) {
            throw new RemoteException("Could not connect to server", e);
        } catch (IOException e) {
            throw new RemoteException("Could not connect to server", e);
        }
    }

  public gov.nih.nci.caarray.external.v1_0.experiment.Organism[] getAllOrganisms() throws RemoteException {
        List<Organism> organisms = caArrayServer.getSearchService().getAllOrganisms(null);
        return organisms.toArray(new Organism[organisms.size()]);
    }

  public gov.nih.nci.caarray.external.v1_0.experiment.Experiment[] searchForExperiments(gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria criteria) throws RemoteException {
        List<Experiment> experiments = caArrayServer.getSearchService().searchForExperiments(criteria, null);
        return experiments.toArray(new Experiment[experiments.size()]);
    }

  public gov.nih.nci.caarray.external.v1_0.experiment.Person[] getAllPrincipalInvestigators() throws RemoteException {
        List<Person> pis = caArrayServer.getSearchService().getAllPrincipalInvestigators(null);
        return pis.toArray(new Person[pis.size()]);
    }

  public gov.nih.nci.caarray.external.v1_0.data.FileType[] getAllFileTypes() throws RemoteException {
        List<FileType> fileTypes = caArrayServer.getSearchService().getAllFileTypes(null);
        return fileTypes.toArray(new FileType[fileTypes.size()]);
    }

  public gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity getByReference(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference reference) throws RemoteException {
        return caArrayServer.getSearchService().getByReference(reference);
    }

  public gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity[] getByReferences(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference[] references) throws RemoteException {
        List<AbstractCaArrayEntity> entities = caArrayServer.getSearchService().getByReferences(
                Arrays.asList(references));
        return entities.toArray(new AbstractCaArrayEntity[entities.size()]);
    }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateOrganisms() throws RemoteException {
        try {
            return EnumerateResponseFactory.createEnumerationResponse(new OrganismEnumIterator());
        } catch (Exception e) {
            throw new RemoteException("Unable to create enumeration", e);
        }
    }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateExperiments(gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria experimentSearchCriteria) throws RemoteException {
        try {
            return EnumerateResponseFactory.createEnumerationResponse(new ExperimentCriteriaEnumIterator(
                    experimentSearchCriteria));
        } catch (Exception e) {
            throw new RemoteException("Unable to create enumeration", e);
        }
    }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference getFileContentsZipTransfer(gov.nih.nci.caarray.external.v1_0.query.FileDownloadRequest fileDownloadRequest,boolean compressIndividually) throws RemoteException {
      File zipFile = null;
      FileOutputStream fos = null;
      ZipOutputStream zos = null;
      try {
          zipFile = File.createTempFile("caarray_transfer_zip", null);
          fos = FileUtils.openOutputStream(zipFile);
          zos = new ZipOutputStream(fos);
          for (CaArrayEntityReference fileRef : fileDownloadRequest.getFiles()) {
              RetrievedFile retrievedFile = retrieveFileContentsWithRmiStream(caArrayServer, fileRef, true);
              ZipEntry ze = new ZipEntry(retrievedFile.getFileMetadata().getName());
              ze.setMethod(compressIndividually ? ZipEntry.STORED : ZipEntry.DEFLATED);
              ze.setSize(retrievedFile.getFileMetadata().getCompressedSize());
              ze.setCrc(FileUtils.checksumCRC32(retrievedFile.getFile()));
              zos.putNextEntry(ze);
              InputStream is = FileUtils.openInputStream(retrievedFile.getFile());
              if (!compressIndividually) {
                 is = new GZIPInputStream(is);
              }
              IOUtils.copy(is, zos);
              zos.closeEntry();
              zos.flush();
              IOUtils.closeQuietly(is);
              FileUtils.deleteQuietly(retrievedFile.getFile());
          }

          return TransferServiceHelper.createTransferContext(zipFile, null, true);
      } catch (IOException e) {
        throw new RemoteException("Unable to create zip of files");
      } finally {
          if (zos != null) {
              IOUtils.closeQuietly(zos);
          }
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

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerateFileContentTransfers(gov.nih.nci.caarray.external.v1_0.query.FileDownloadRequest fileDownloadRequest,boolean compress) throws RemoteException {
      try {
          return EnumerateResponseFactory.createEnumerationResponse(new FileTransferEnumIterator(fileDownloadRequest, compress));
      } catch (Exception e) {
          throw new RemoteException("Unable to create enumeration", e);
      }
  }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference getFileContentsTransfer(gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference fileRef,boolean compress) throws RemoteException {
      try {
          return stageFileContentsWithRmiStream(caArrayServer, fileRef, compress);
      } catch (IOException e) {
          throw new RemoteException("Error retrieving file contents: " + e);
      }
  }

  static TransferServiceContextReference stageFileContentsWithRmiStream(CaArrayServer server,
            CaArrayEntityReference fileRef, boolean compress) throws IOException {
      TransferServiceContextReference ref = null;
      RetrievedFile retrievedFile = retrieveFileContentsWithRmiStream(server, fileRef, compress);
      if (retrievedFile != null) {
          long fileSize = compress ? retrievedFile.getFileMetadata().getCompressedSize() : retrievedFile
                    .getFileMetadata().getUncompressedSize();
          ref = TransferServiceHelper.createTransferContext(retrievedFile.getFile(), new DataDescriptor(
                  fileSize, retrievedFile.getFileMetadata().getName()), true);
      }
      return ref;

  }

  private static RetrievedFile retrieveFileContentsWithRmiStream(CaArrayServer server,
          CaArrayEntityReference fileRef, boolean compress) throws IOException {
    RemoteOutputStreamServer ostream = null;
    DataFile fileMetadata = null;
    File file = null;
    OutputStream fos = null;
    try {
        file = File.createTempFile("caarray_transfer", null);
        fos = FileUtils.openOutputStream(file);
        ostream = new SimpleRemoteOutputStream(fos);
        fileMetadata = server.getDataService().streamFileContents(fileRef, compress, ostream.export());
    } finally {
        if (ostream != null) {
            ostream.close();
        }
        if (fos != null) {
            IOUtils.closeQuietly(fos);
        }
        if (file != null && fileMetadata == null) {
            file.delete();
        }
    }
    return fileMetadata == null ? null : new RetrievedFile(file, fileMetadata);
  }

  private static final class RetrievedFile {
        private final File file;
        private final DataFile fileMetadata;

        /**
         * @param file
         * @param fileMetadata
         */
        private RetrievedFile(File file, DataFile fileMetadata) {
            this.file = file;
            this.fileMetadata = fileMetadata;
        }

        /**
         * @return the file
         */
        public File getFile() {
            return file;
        }

        /**
         * @return the fileMetadata
         */
        public DataFile getFileMetadata() {
            return fileMetadata;
        }
    }

  public gov.nih.nci.caarray.external.v1_0.data.DataSet getDataSet(gov.nih.nci.caarray.external.v1_0.query.DataSetRequest dataSetRequest) throws RemoteException {
      DataSet dataSet = caArrayServer.getDataService().getDataSet(dataSetRequest);
      return dataSet;
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

  public gov.nih.nci.caarray.external.v1_0.experiment.Experiment[] searchForExperimentsByKeyword(gov.nih.nci.caarray.external.v1_0.query.ExperimentKeywordSearchCriteria criteria) throws RemoteException {
      List<Experiment> experiments = caArrayServer.getSearchService().searchForExperimentsByKeyword(criteria, null);
      return experiments.toArray(new Experiment[experiments.size()]);
  }

  public gov.nih.nci.caarray.external.v1_0.data.DataFile[] searchForFiles(gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria fileSearchCriteria) throws RemoteException {
      List<DataFile> files = caArrayServer.getSearchService().searchForFiles(fileSearchCriteria, null);
      return files.toArray(new DataFile[files.size()]);
  }

  public gov.nih.nci.caarray.external.v1_0.sample.Biomaterial[] searchForBiomaterialsByKeyword(gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria criteria) throws RemoteException {
      List<Biomaterial> bms = caArrayServer.getSearchService().searchForBiomaterialsByKeyword(criteria, null);
      return bms.toArray(new Biomaterial[bms.size()]);
  }

  public gov.nih.nci.caarray.external.v1_0.sample.Biomaterial[] searchForBiomaterials(gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria criteria) throws RemoteException {
      try {
            List<Biomaterial> bms = caArrayServer.getSearchService().searchForBiomaterials(criteria, null);
            return bms.toArray(new Biomaterial[bms.size()]);
        } catch (InvalidReferenceException e) {
            throw new RemoteException("Error retrieving samples: " + e);
        } 
  }

  public gov.nih.nci.caarray.external.v1_0.sample.Hybridization[] searchForHybridizations(gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria criteria) throws RemoteException {
      try {
            List<Hybridization> hybs = caArrayServer.getSearchService().searchForHybridizations(criteria, null);
            return hybs.toArray(new Hybridization[hybs.size()]);
        } catch (InvalidReferenceException e) {
            throw new RemoteException("Error retrieving hybs: " + e);
        } 
  }

}
