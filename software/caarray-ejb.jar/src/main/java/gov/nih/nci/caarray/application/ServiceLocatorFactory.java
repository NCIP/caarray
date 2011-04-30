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
package gov.nih.nci.caarray.application;

import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.audit.AuditLogService;
import gov.nih.nci.caarray.application.browse.BrowseService;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.jobqueue.JobQueueService;
import gov.nih.nci.caarray.application.permissions.PermissionsManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.registration.RegistrationService;
import gov.nih.nci.caarray.application.translation.geosoft.GeoSoftExporter;
import gov.nih.nci.caarray.application.translation.magetab.MageTabExporter;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.services.data.DataRetrievalService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;

import com.google.inject.Provider;

/**
 * Provides access to a <code>ServiceLocator</code>.
 */
public final class ServiceLocatorFactory {

    private static ServiceLocator locator = new ServiceLocatorImplementation();

    private ServiceLocatorFactory() {

    }

    /**
     * Returns a <code>ServiceLocator</code> instance.
     * 
     * @return the locator
     */
    public static ServiceLocator getLocator() {
        return locator;
    }

    /**
     * Allows registration of a <code>ServiceLocator</code> instance; should only be used in test code to replace the
     * actual locator with a test stub.
     * 
     * @param locator the locator to set
     */
    public static void setLocator(ServiceLocator locator) {
        ServiceLocatorFactory.locator = locator;
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static ProjectManagementService getProjectManagementService() {
        return (ProjectManagementService) getLocator().lookup(ProjectManagementService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static JobQueueService getJobQueueService() {
        return (JobQueueService) getLocator().lookup(JobQueueService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static PermissionsManagementService getPermissionsManagementService() {
        return (PermissionsManagementService) getLocator().lookup(PermissionsManagementService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static FileAccessService getFileAccessService() {
        return (FileAccessService) getLocator().lookup(FileAccessService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static FileManagementService getFileManagementService() {
        return (FileManagementService) getLocator().lookup(FileManagementService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static VocabularyService getVocabularyService() {
        return (VocabularyService) getLocator().lookup(VocabularyService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static GenericDataService getGenericDataService() {
        return (GenericDataService) getLocator().lookup(GenericDataService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static ArrayDesignService getArrayDesignService() {
        return (ArrayDesignService) getLocator().lookup(ArrayDesignService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static ArrayDataService getArrayDataService() {
        return (ArrayDataService) getLocator().lookup(ArrayDataService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static RegistrationService getRegistrationService() {
        return (RegistrationService) getLocator().lookup(RegistrationService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static BrowseService getBrowseService() {
        return (BrowseService) getLocator().lookup(BrowseService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service.
     * 
     * @return the service
     */
    public static AuditLogService getAuditLogService() {
        return (AuditLogService) getLocator().lookup(AuditLogService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the MAGE-TAB Exporter singleton service.
     * 
     * @return the MAGE-TAB Exporter service
     */
    public static MageTabExporter getMageTabExporter() {
        return (MageTabExporter) getLocator().lookup(MageTabExporter.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the GEO SOFT Exporter singleton service.
     * 
     * @return the GEO SOFT Exporter service
     */
    public static GeoSoftExporter getGeoSoftExporter() {
        return (GeoSoftExporter) getLocator().lookup(GeoSoftExporter.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the MAGE-TAB Translator singleton service.
     * 
     * @return the MAGE-TAB Exporter service
     */
    public static MageTabTranslator getMageTabTranslator() {
        return (MageTabTranslator) getLocator().lookup(MageTabTranslator.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the DataRetrievalService singleton service.
     * 
     * @return the DataRetrievalService
     */
    public static DataRetrievalService getDataRetrievalService() {
        return (DataRetrievalService) getLocator().lookup(DataRetrievalService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the DataRetrievalService singleton service.
     * 
     * @return the DataRetrievalService
     */
    public static CaArraySearchService getCaArraySearchService() {
        return (CaArraySearchService) getLocator().lookup(CaArraySearchService.JNDI_NAME);
    }

    /**
     * Create a Guice provider that will provide instances of given service by looking them up in this locator using the
     * given JNDI name.
     * 
     * @param <T> service type
     * @param service the service for which a provider will be created
     * @param serviceJndiName the JNDI name by which to lookup the service
     * @return the guice provider
     */
    public static <T> Provider<T> serviceProvider(Class<T> service, final String serviceJndiName) {
        return new Provider<T>() {
            @Override
            @SuppressWarnings("unchecked")
            public T get() {
                return (T) getLocator().lookup(serviceJndiName);
            }
        };
    }
}
