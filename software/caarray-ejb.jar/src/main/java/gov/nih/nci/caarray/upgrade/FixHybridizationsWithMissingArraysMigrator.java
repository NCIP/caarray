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

