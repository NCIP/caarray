//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.upgrade;

import gov.nih.nci.caarray.application.ApplicationModule;
import gov.nih.nci.caarray.application.JtaSessionTransactionManager;
import gov.nih.nci.caarray.application.file.FileModule;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.DaoModule;
import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.SearchDaoUnsupportedOperationImpl;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.SessionTransactionManagerNoOpImpl;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.services.ServicesModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import liquibase.database.Database;
import liquibase.exception.CustomChangeException;

import org.apache.commons.lang.UnhandledException;
import org.hibernate.Transaction;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * @author jscott
 *
 */
public class FixIlluminaGenotypingCsvDesignProbeNamesMigrator extends AbstractCustomChange {
    private static final String ILLUMINA_DESIGN_CSV_TYPE_NAME = "ILLUMINA_DESIGN_CSV";
    
    /**
     * {@inheritDoc}
     */
    protected void doExecute(Database database) throws CustomChangeException {
        try {
            Connection connection = database.getConnection().getUnderlyingConnection();       
            execute(connection);
        } catch (Exception e) {
            throw new CustomChangeException(e);
        }
    }

    /**
     * @param connection the JDBC connection to use
     */
    public void execute(Connection connection) {
        final Injector injector = createInjector();

        final SingleConnectionHibernateHelper hibernateHelper = createHibernateHelper(connection, injector);

        final FileDao fileDao = injector.getInstance(FileDao.class);
        final Set<DesignFileHandler> handlers = getAllDesignHandlers(fileDao);

        final Transaction transaction = hibernateHelper.beginTransaction();
        try {
            final ArrayDao arrayDao = injector.getInstance(ArrayDao.class);

            final List<Long> arrayDesignIds = getArrayDesignIds(hibernateHelper, arrayDao);
            hibernateHelper.getCurrentSession().clear();

            fixArrayDesigns(handlers, arrayDao, arrayDesignIds);

            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            throw new UnhandledException(e);
        }
    }

    private void fixArrayDesigns(Set<DesignFileHandler> handlers, ArrayDao arrayDao, List<Long> arrayDesignIds)
            throws PlatformFileReadException {

        for (final long arrayDesignId : arrayDesignIds) {
            final ArrayDesign originalArrayDesign = arrayDao.getArrayDesign(arrayDesignId);

            final CaArrayFile arrayDesignFile = originalArrayDesign.getFirstDesignFile();
            final ArrayDesign reparsedArrayDesign = getNewArrayDesign(handlers, arrayDesignFile);

            renameProbesUsingReparsedProbeNames(originalArrayDesign, reparsedArrayDesign);
              
            arrayDao.save(originalArrayDesign);
            arrayDao.flushSession();
            
            // Detach the array design
            arrayDao.clearSession();
        }
    }

    private void renameProbesUsingReparsedProbeNames(ArrayDesign originalArrayDesign, ArrayDesign reparsedArrayDesign) {
        final SortedSet<PhysicalProbe> originalProbes = getSortedProbeList(originalArrayDesign);
        final SortedSet<PhysicalProbe> reparsedProbes = getSortedProbeList(reparsedArrayDesign);

        if (originalProbes.size() != reparsedProbes.size()) {
            throw new IllegalStateException("probe set sizes differ");
        }

        final Iterator<PhysicalProbe> reparsedProbeIterator = reparsedProbes.iterator();
        for (final PhysicalProbe originalProbe : originalProbes) {
            final String reparsedName = reparsedProbeIterator.next().getName();
            originalProbe.setName(reparsedName);
        }
    }

    private SortedSet<PhysicalProbe> getSortedProbeList(ArrayDesign orrayDesign) {
        final Comparator<PhysicalProbe> comparator = getPhysicalProbeIdComparator();

        final SortedSet<PhysicalProbe> probes = new TreeSet<PhysicalProbe>(comparator);
        probes.addAll(orrayDesign.getDesignDetails().getProbes());
        return probes;
    }

    private Comparator<PhysicalProbe> getPhysicalProbeIdComparator() {
        final Comparator<PhysicalProbe> comparator = new Comparator<PhysicalProbe>() {
            @Override
            public int compare(PhysicalProbe o1, PhysicalProbe o2) {
                if (o1.getId() < o2.getId()) {
                    return -1;
                } else if (o1.getId() == o2.getId()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        };
        return comparator;
    }

    private SingleConnectionHibernateHelper createHibernateHelper(Connection connection, Injector injector) {
        final SingleConnectionHibernateHelper hibernateHelper = (SingleConnectionHibernateHelper) injector
                .getInstance(CaArrayHibernateHelper.class);
        
        hibernateHelper.initialize(connection);
        
        return hibernateHelper;
    }

    private Injector createInjector() {
        final Module localModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(CaArrayHibernateHelper.class).toInstance(new SingleConnectionHibernateHelper());
                bind(SessionTransactionManager.class).to(JtaSessionTransactionManager.class);
            }
        };
        
        final Module[] modules = new Module[] {
                new DaoModule(), 
                new ServicesModule(),
                new FileModule(),
                new PlatformModule(), 
                new ApplicationModule(),
                localModule,
            };

        return Guice.createInjector(modules);
    }

    /**
     * @param handlers
     * @param arrayDesignFile
     * @return 
     * @throws PlatformFileReadException 
     */
    private ArrayDesign getNewArrayDesign(Set<DesignFileHandler> handlers, CaArrayFile arrayDesignFile)
            throws PlatformFileReadException {
        final Set<CaArrayFile> arrayDesignFileSet = Collections.<CaArrayFile> singleton(arrayDesignFile);
        final ArrayDesign arrayDesign = new ArrayDesign();

        for (final DesignFileHandler handler : handlers) {
            if (handler.openFiles(arrayDesignFileSet)) {
                try {
                    handler.load(arrayDesign);
                } finally {
                    handler.closeFiles();
                }
                
                createDesignDetails(handler, arrayDesignFileSet, arrayDesign);
                
                return arrayDesign;
            }
        }
       
        return null;
    }

    /**
     * @param handler
     * @param arrayDesignFileSet 
     * @param arrayDesign
     * @throws PlatformFileReadException 
     */
    private void createDesignDetails(DesignFileHandler handler, Set<CaArrayFile> arrayDesignFileSet,
            ArrayDesign arrayDesign) throws PlatformFileReadException {
        if (handler.openFiles(arrayDesignFileSet)) {
            try {
                handler.createDesignDetails(arrayDesign);
            } finally {
                handler.closeFiles();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Set<DesignFileHandler> getAllDesignHandlers(final FileDao fileDao) {
        final Module module = new AbstractModule() {
            @Override
            protected void configure() {
                // TODO: need something for file storage to replace this: bind(FileManager.class).toInstance(new FileDaoFileManager(fileDao));
                bind(SessionTransactionManager.class).to(SessionTransactionManagerNoOpImpl.class).asEagerSingleton();

                bind(ArrayDao.class).to(ShellArrayDao.class).asEagerSingleton();

                bind(SearchDao.class).to(SearchDaoUnsupportedOperationImpl.class).asEagerSingleton();
            }
        };
        // TODO: need to figure out how to make this work with OSGi plugins
        final Injector injector = Guice.createInjector(module /** , new IlluminaModule() */
        );
        final Key<?> key = Key.get(TypeLiteral.get(Types.setOf(DesignFileHandler.class)));
        final Set<DesignFileHandler> handlers = (Set<DesignFileHandler>) injector.getInstance(key);

        return handlers;
    }

    private List<Long> getArrayDesignIds(SingleConnectionHibernateHelper hibernateHelper, ArrayDao arrayDao) {
        final List<Long> arrayDesignIds = new ArrayList<Long>();

        final Organization provider = (Organization) hibernateHelper.getCurrentSession()
                .createQuery("FROM " + Organization.class.getName() + " where provider = true and name = 'Illumina'")
                .uniqueResult();

        final AssayType assayType = (AssayType) hibernateHelper.getCurrentSession()
                .createQuery("FROM " + AssayType.class.getName() + " where name = 'SNP'").uniqueResult();

        final Set<AssayType> assayTypes = new HashSet<AssayType>();
        assayTypes.add(assayType);

        final List<ArrayDesign> candidateArrayDesigns = arrayDao.getArrayDesigns(provider, assayTypes, true);
        for (final ArrayDesign candidate : candidateArrayDesigns) {
            if (candidate.getFirstDesignFile().getFileType().getName().equals(ILLUMINA_DESIGN_CSV_TYPE_NAME)) {
                arrayDesignIds.add(candidate.getId());
            }
        }
        
        return arrayDesignIds;
    }
}

