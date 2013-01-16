//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.upgrade;

import gov.nih.nci.caarray.dao.DaoModule;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.MultiPartBlob;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.SessionTransactionManagerNoOpImpl;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import liquibase.database.Database;
import liquibase.exception.CustomChangeException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * Migrator to find hybridizations with missing arrays, and where possible, create them based on array designs specified
 * in data files or the experiment.
 * 
 * The migrator will use the array design for the experiment, if there is only one. Otherwise, it will extract the array
 * design from one of the data files associated with the hybridization, if present. If none of the data files specify
 * the array design, and the experiment has more than one array design specified, then no action will be taken.
 * 
 * @author Dan Kokotov
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.CyclomaticComplexity" })
public class FixHybridizationsWithMissingArraysMigrator extends AbstractCustomChange {
    private Database database;
    private Set<DataFileHandler> handlers;
    private final Map<Long, File> openFileMap = new HashMap<Long, File>();
    private final FixHybridizationsWithMissingArraysDao dao;
    private Injector injector;
    
    /**
     * Creates a FixHybridizationsWithMissingArraysMigrator that will make updates to the live database.
     */
    public FixHybridizationsWithMissingArraysMigrator() {
        this(new FixHybridizationsWithMissingArraysDao());
    }
    
    /**
     * Creates a FixHybridizationsWithMissingArraysMigrator that uses a custom data access object for
     * database operations.  Intended for use in testing.
     * 
     * @param dao handles the database update operations
     */
    public FixHybridizationsWithMissingArraysMigrator(FixHybridizationsWithMissingArraysDao dao) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute(Database db) throws CustomChangeException {
        try {
            final Connection underlyingConnection = db.getConnection().getUnderlyingConnection();
            execute(underlyingConnection);
        } catch (Exception e) {
            throw new CustomChangeException(e);
        }
    }

    /**
     * Execute the change given a connection.
     * @param connection the connection
     */
    public void execute(Connection connection) {
        setup(connection);
        try {
            List<Long> hybIdsWithoutArray = dao.getHybIdsWithNoArrayOrNoArrayDesign();
            for (Long hid : hybIdsWithoutArray) {
                ensureArrayDesignSetForHyb(hid);
            }
        } catch (Exception e) {
            throw new UnhandledException("Could not fix hybridizations", e);
        }
    }

    private void ensureArrayDesignSetForHyb(Long hid) throws SQLException, IOException, PlatformFileReadException {
        Long adid = getArrayDesignId(hid);
        if (adid != null) {
            setArrayDesignForHyb(hid, adid);
        }
    }

    private Long getArrayDesignId(Long hid) throws SQLException, IOException, PlatformFileReadException {
        Long adid = dao.getUniqueArrayDesignIdFromExperiment(hid);
        if (adid == null) {
            adid = getArrayDesignIdFromFiles(hid);
        }
        return adid;
    }

    private Long getArrayDesignIdFromFiles(Long hid) throws SQLException, IOException, PlatformFileReadException {
        List<Long> dataFileIds = dao.getImportedDataFileIdsFromHybId(hid);
        for (Long fileId : dataFileIds) {
            Long adid = getArrayDesignFromFile(fileId);
            if (adid != null) {
                return adid;
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private Long getArrayDesignFromFile(Long fileId) throws SQLException, IOException {
        FileType ft = dao.getFileType(fileId);

        if (!ft.isArrayData()) {
            return null;
        }

        CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setId(fileId);
        DataFileHandler handler = null;
        try {
            handler = getHandler(caArrayFile);
            return findArrayDesignFromFile(handler).getId();
        } catch (PlatformFileReadException e) {
            throw new IllegalArgumentException("Error reading file " + caArrayFile.getName(), e);
        } finally {
            if (handler != null) {
                handler.closeFiles();
            }
        }
    }
    
    private ArrayDesign findArrayDesignFromFile(DataFileHandler handler) throws PlatformFileReadException {
        List<LSID> designLsids = handler.getReferencedArrayDesignCandidateIds();
        return dao.getFirstArrayDesignFromLsidList(designLsids);
    }

    private void setArrayDesignForHyb(Long hid, Long adid) throws SQLException {
        Long aid = dao.getArrayFromHybridizationId(hid);
        if (aid != null) {
            dao.setArrayDesignForArray(aid, adid);
        } else {
            dao.setArrayAndDesign(hid, adid);
        }
    }

    /**
     * Find the appropriate data handler for the given data file, and initialize it.
     * 
     * @param caArrayFile the data file to be processed
     * @return the DataFileHandler instance capable of processing that file. That handler will have been initialized
     *         with this file.
     */
    private DataFileHandler getHandler(CaArrayFile caArrayFile) {
        for (DataFileHandler handler : this.handlers) {
            try {
                if (handler.openFile(caArrayFile)) {
                    return handler;
                }
            } catch (PlatformFileReadException e) {
                handler.closeFiles();
                throw new IllegalArgumentException("Error reading file " + caArrayFile.getName(), e);
            }
        }
        throw new IllegalArgumentException("Unsupported type " + caArrayFile.getFileType());
    }

    private void setup(Connection connection) {
        this.injector = createInjector();
        dao.setConnection(connection);
        createHibernateHelper(connection);
        this.openFileMap.clear();
        this.handlers = getHandlers();
    }

    private Injector createInjector() {
        return Guice.createInjector(new DaoModule(), new PlatformModule(), getLocalModule());
    }

    private AbstractModule getLocalModule() {
        final AbstractModule localModule = new AbstractModule() {
            @Override
            protected void configure() {
                // TODO: may need to bind a local implementation of storage in place of this: bind(FileManager.class).toInstance(createFileManager());               
                bind(SessionTransactionManager.class).toInstance(new SessionTransactionManagerNoOpImpl());
                bind(CaArrayHibernateHelper.class).toInstance(new SingleConnectionHibernateHelper());
            }
        };
        return localModule;
    }
    
// TODO: remove this when appropriate local implementation of storage is created to replace this.  
// Until then, keeping it here temporarily as a reference. - A Sy 2011-03-30    
//    private FileManager createFileManager() {
//        return new FileManager() {
//            public File openFile(CaArrayFile caArrayFile) {
//                try {
//                    File file = getFile(caArrayFile.getId());
//                    openFileMap.put(caArrayFile.getId(), file);
//                    return file;
//                } catch (SQLException e) {
//                    throw new IllegalStateException("Could not open the file " + caArrayFile);
//                } catch (IOException e) {
//                    throw new IllegalStateException("Could not open the file " + caArrayFile);
//                }
//            }
//
//            public void closeFile(CaArrayFile caArrayFile) {
//                File file = openFileMap.get(caArrayFile.getId());
//                FileUtils.deleteQuietly(file);                            
//            }
//        };
//    }
    
    private File getFile(Long fileId) throws SQLException, IOException {
        MultiPartBlob mpb = dao.getFileBlob(fileId);

        File f = File.createTempFile("datafile", null);
        InputStream is = mpb.readUncompressedContents();
        FileOutputStream fos = FileUtils.openOutputStream(f);
        IOUtils.copy(is, fos);
        IOUtils.closeQuietly(is);
        IOUtils.closeQuietly(fos);

        return f;
    }
    
    private SingleConnectionHibernateHelper createHibernateHelper(Connection connection) {
        SingleConnectionHibernateHelper hibernateHelper = (SingleConnectionHibernateHelper) injector
                .getInstance(CaArrayHibernateHelper.class);
        
        hibernateHelper.initialize(connection);
        
        return hibernateHelper;
    }
    
    @SuppressWarnings("unchecked")
    private Set<DataFileHandler> getHandlers() {
        return (Set<DataFileHandler>) injector.getInstance(Key.get(TypeLiteral.get(Types
                .setOf(DataFileHandler.class))));
    }
}

