//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.platforms.unparsed.FallbackUnparsedDataHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Base class for array data helper classes that interface with the platform handlers. Contains common functionality.
 * 
 * @author dkokotov
 */
abstract class AbstractArrayDataUtility {
    private static final Logger LOG = Logger.getLogger(AbstractArrayDataUtility.class);

    private final ArrayDao arrayDao;
    private final Set<DataFileHandler> handlers;
    private final Provider<FallbackUnparsedDataHandler> fallbackHandlerProvider;

    /**
     * @param arrayDao
     * @param arrayDesignService
     * @param handlers
     */
    @Inject
    AbstractArrayDataUtility(ArrayDao arrayDao, Set<DataFileHandler> handlers,
            Provider<FallbackUnparsedDataHandler> fallbackHandlerProvider) {
        this.arrayDao = arrayDao;
        this.handlers = new HashSet<DataFileHandler>(handlers);
        this.fallbackHandlerProvider = fallbackHandlerProvider;
    }

    /**
     * Find the appropriate data handler for the given data file, and initialize it.
     * 
     * @param arrayDataFile the data file to be processed
     * @param mTabSet parsed MageTabDocumentSet containing SDRF specifying data file to array design mappings. 
     * May be null if not applicable.
     * @return the DataFileHandler instance capable of processing the arrayDataFile. The handler will
     * have been initialized with arrayDataFile and mTabSet. 
     */
    protected DataFileHandler findAndSetupHandler(CaArrayFile arrayDataFile, MageTabDocumentSet mTabSet) 
            throws PlatformFileReadException {
        DataFileHandler handler = findHandlerAndSetDataFile(arrayDataFile); 
        handler.setMageTabDocumentSet(mTabSet);
        return handler;
    }
    
    /**
     * Find the appropriate data handler for the given data file.
     * 
     * @param arrayDataFile the data file to be processed
     * @return the DataFileHandler instance capable of processing the arrayDataFile. The handler will
     * have been initialized with arrayDataFile. 
     */
    protected DataFileHandler findHandlerAndSetDataFile(CaArrayFile arrayDataFile) 
            throws PlatformFileReadException {
        DataFileHandler handler = getOpeningHandler(arrayDataFile);
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported type " + arrayDataFile.getFileType());
        }
        if (!handler.parsesData()) {
            return handler;
        }
        final ArrayDesign ad = getArrayDesign(arrayDataFile, handler);
        if (ad == null || !ad.isImportedAndParsed()) {
            handler.closeFiles();
            handler = getUnparsedDataHandler(arrayDataFile);
        }
        return handler;
    }
    
    /**
     * Find the array design corresponding to the given data file. 
     * <ol>
     * <li>Tries to determine the design from the arrayDataFile; </li>
     * <li>Else if the data file does not encode a design reference, tries to determine it based on the 
     * designs associated with the experiment, if there is exactly one such design.</li>
     * <li>Else tries to resolve using the data file to array design mapping specified by the SDRF file(s)
     * The SDRF files, if available, are contained in the MageTabDocumentSet referenced by the handlerArg.</li> 
     * </ol>
     * 
     * @param arrayDataFile the data file
     * @param handler the handler for the data file
     * @return the array design corresponding to the file, or null if it is not found or could not be determined
     *         uniquely from the file or experiment.
     * @throws PlatformFileReadException
     * 
     */
    protected ArrayDesign getArrayDesign(CaArrayFile arrayDataFile, DataFileHandler handlerArg)
            throws PlatformFileReadException {
        ArrayDesign ad = findArrayDesignFromFile(handlerArg);
        if (ad == null) {
            ad = findArrayDesignFromExperiment(arrayDataFile.getProject().getExperiment());
        }
        if (ad == null) {
            ad = findArrayDesignViaSdrf(arrayDataFile, handlerArg.getMageTabDocumentSet());
        }
        return ad;
    }

    private ArrayDesign findArrayDesignFromFile(DataFileHandler handler) throws PlatformFileReadException {
        final List<LSID> designLsids = handler.getReferencedArrayDesignCandidateIds();
        for (final LSID lsid : designLsids) {
            final ArrayDesign ad = this.arrayDao.getArrayDesign(lsid.getAuthority(), lsid.getNamespace(),
                    lsid.getObjectId());
            if (ad != null) {
                return ad;
            }
        }
        return null;
    }

    private ArrayDesign findArrayDesignFromExperiment(Experiment experiment) {
        final Set<ArrayDesign> experimentDesigns = experiment.getArrayDesigns();
        if (experimentDesigns.size() == 1) {
            return experimentDesigns.iterator().next();
        }
        return null;
    }

    /** 
     * Iterates through all SDRF files contained in mTabSet, looking for the first array design LSID
     * that corresponds to arrayDataFile. If a match is found, the LSID is used to retrieve 
     * the corresponding ArrayDesign from the persistence layer. 
     * @param arrayDataFile
     * @param mTabSetArg
     * @return first array design found that corresponds to arrayDataFile. null if no match found 
     * or if mTabSetArg is null. 
     */
    private ArrayDesign findArrayDesignViaSdrf(CaArrayFile arrayDataFile, MageTabDocumentSet mTabSetArg) {
        if (mTabSetArg == null) {
            return null;
        }
        String dataFileName = arrayDataFile.getName();
        LOG.info("findArrayDesignFromSdrf for arrayDataFile=[" + dataFileName + "]. ");
        for (SdrfDocument sdrf : mTabSetArg.getSdrfDocuments()) {
            String adLsidName = sdrf.getArrayDesignNameForArrayDataFileName(dataFileName);
            LOG.info(String.format(
                    "findArrayDesignFromSdrf for arrayDataFile=[%s], found matching arrayDesign LSID=[%s]. ", 
                    dataFileName, adLsidName));
            if (adLsidName != null) {
                LSID adLsid = new LSID(adLsidName);
                ArrayDesign ad = arrayDao.getArrayDesign(adLsid.getAuthority(), adLsid.getNamespace(), adLsid
                        .getObjectId());
                if (ad != null) {
                    return ad;
                } else {
                    LOG.warn(String.format(
                            "findArrayDesignFromSdrf for arrayDataFile=[%s], found matching arrayDesign LSID=[%s],"
                            + " but arrayDesign object not found in persistent store. ", 
                            dataFileName, adLsidName));
                }
            }
        }
        
        LOG.info(String.format(
                "findArrayDesignFromSdrf for arrayDataFile=[%s], found no matching arrayDesign. ", 
                dataFileName));
        return null; 
    }
    
    protected ArrayDao getArrayDao() {
        return this.arrayDao;
    }

    private DataFileHandler getUnparsedDataHandler(CaArrayFile caArrayFile) throws PlatformFileReadException {
        final DataFileHandler handler = this.fallbackHandlerProvider.get();
        handler.openFile(caArrayFile);
        return handler;
    }

    private DataFileHandler getOpeningHandler(CaArrayFile caArrayFile) throws PlatformFileReadException {
        for (final DataFileHandler handler : this.handlers) {
            try {
                if (handler.openFile(caArrayFile)) {
                    return handler;
                }
            } catch (final PlatformFileReadException e) {
                handler.closeFiles();
                throw e;
            }
        }
        return null;
    }
}
