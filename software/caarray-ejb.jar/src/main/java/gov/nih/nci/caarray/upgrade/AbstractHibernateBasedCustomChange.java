/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
import gov.nih.nci.caarray.application.file.FileModule;
import gov.nih.nci.caarray.application.util.UtilModule;
import gov.nih.nci.caarray.dao.DaoModule;
import gov.nih.nci.caarray.magetab.MageTabModule;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.services.ServicesModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import java.sql.Connection;

import liquibase.database.Database;
import liquibase.exception.CustomChangeException;

import org.hibernate.Transaction;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Base class for custom Liquibase migrators that use hibernate persistence.
 * @author dharley
 *
 */
public abstract class AbstractHibernateBasedCustomChange extends AbstractCustomChange {
    
    private Injector defaultInjector;
    private static final PlatformModule PLATFORM_MODULE = new PlatformModule();
    
    /**
     * {@inheritDoc}
     */
    public void doExecute(final Database database) throws CustomChangeException {
        SingleConnectionHibernateHelper hibernateHelper =
            createHibernateHelper(database.getConnection().getUnderlyingConnection());
        Transaction transaction = hibernateHelper.beginTransaction();
        try {
            doHibernateExecute(hibernateHelper);
            transaction.commit();
        } catch (Exception exception) {
            transaction.rollback();
            throw new CustomChangeException(exception);
        }
    }
    
    /**
     * Executes the update work using the specified connection.  Clients do not need to handle the transaction as it
     * will be managed by calling execute(Database) method.
     * @param singleConnectionHibernateHelper the SingleConnectionHibernateHelper to use.
     */
    protected abstract void doHibernateExecute(final SingleConnectionHibernateHelper singleConnectionHibernateHelper);
    
    /**
     * Gets the default list of Guice Modules (ArrayDataModule, ServicesModule, ApplicationModule).
     * @return the default list of Guice Modules.
     */
    protected Module[] getGuiceModules() {
        return new Module[] {
                new DaoModule(), 
                new ServicesModule(),
                new FileModule(),
                new ApplicationModule(),
                new MageTabModule(),
                new UtilModule(),
                PLATFORM_MODULE,
            };
    }
    
    /**
     * Creates the guice injector.
     * @return the guice injector.
     */
    protected Injector getInjector() {
        if (null == defaultInjector) {
            Module localModule = new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CaArrayHibernateHelper.class).toInstance(new SingleConnectionHibernateHelper());             
                }   
            };
            final Module[] defaultModules = getGuiceModules();
            final Module[] allModules = new Module[defaultModules.length + 1];
            System.arraycopy(defaultModules, 0, allModules, 0, defaultModules.length);
            allModules[allModules.length - 1] = localModule;
            defaultInjector = Guice.createInjector(allModules);
        }
        return defaultInjector;
    }
    
    /**
     * Creates a hibernate helper instance using the default guice injector.
     * @param connection the connection to use
     * @return SingleConnectionHibernateHelper
     */
    protected SingleConnectionHibernateHelper createHibernateHelper(final Connection connection) {
        return createHibernateHelper(connection, getInjector());
    }

    /**
     * Creates a hibernate helper instance.
     * @param connection the connection to use.
     * @param injector the injector to use.
     * @return hibernate helper instance.
     */
    protected SingleConnectionHibernateHelper createHibernateHelper(final Connection connection,
            final Injector injector) {
        SingleConnectionHibernateHelper hibernateHelper = (SingleConnectionHibernateHelper) injector
                .getInstance(CaArrayHibernateHelper.class);
        hibernateHelper.initialize(connection);
        return hibernateHelper;
    }
}