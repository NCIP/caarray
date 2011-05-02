/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray2-trunk
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caarray2-trunk Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caarray2-trunk Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray2-trunk Software; (ii) distribute and 
 * have distributed to and by third parties the caarray2-trunk Software and any 
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

