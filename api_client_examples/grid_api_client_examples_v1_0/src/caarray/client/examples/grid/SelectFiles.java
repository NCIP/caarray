/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The test
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This test Software License (the License) is between NCI and You. You (or
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
 * its rights in the test Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the test Software; (ii) distribute and
 * have distributed to and by third parties the test Software and any
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
package caarray.client.examples.grid;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.data.FileTypeCategory;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.GridSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A client selecting files from an experiment using the caArray Grid service API.
 *
 * @author Rashmi Srinivasa
 */
public class SelectFiles {
    private static CaArraySvc_v1_0Client client = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;
    private static final String SAMPLE_NAME_01 = BaseProperties.SAMPLE_NAME_01;
    private static final String SAMPLE_NAME_02 = BaseProperties.SAMPLE_NAME_02;

    public static void main(String[] args) {
        SelectFiles selector = new SelectFiles();
        try {
            client = new CaArraySvc_v1_0Client(BaseProperties.getGridServiceUrl());
            searchServiceHelper = new GridSearchApiUtils(client);
            CaArrayEntityReference experimentRef = selector.searchForExperiment();
            if (experimentRef == null) {
                System.out.println("Could not find experiment with the requested title.");
                return;
            }
            System.out.println("Selecting files in experiment: " + EXPERIMENT_TITLE + "...");
            selector.selectFilesInExperiment(experimentRef);
            System.out.println("Selecting files associated with samples: " + SAMPLE_NAME_01 + ", " + SAMPLE_NAME_02 + "...");
            selector.selectFilesFromSamples(experimentRef);
        } catch (Throwable t) {
            System.err.println("Error while selecting files.");
            t.printStackTrace();
        }
    }

    private void selectFilesInExperiment(CaArrayEntityReference experimentRef) throws RemoteException, InvalidReferenceException {
        List<CaArrayEntityReference> fileRefs = selectRawFiles(experimentRef);
        if (fileRefs == null) {
            System.out.println("Could not find any raw files in the experiment.");
        } else {
            System.out.println("Found " + fileRefs.size() + " raw files in the experiment.");
        }
        fileRefs = selectCelFiles(experimentRef);
        if (fileRefs == null) {
            System.out.println("Could not find any Affymetrix CEL files in the experiment.");
        } else {
            System.out.println("Found " + fileRefs.size() + " Affymetrix CEL files in the experiment.");
        }
        fileRefs = selectChpFiles(experimentRef);
        if (fileRefs == null) {
            System.out.println("Could not find any derived files with extension .CHP in the experiment.");
        } else {
            System.out.println("Found " + fileRefs.size() + " derived files with extension .CHP in the experiment.");
        }
    }

    private void selectFilesFromSamples(CaArrayEntityReference experimentRef) throws RemoteException {
        Set<CaArrayEntityReference> sampleRefs = searchForSamples(experimentRef);
        if (sampleRefs == null || sampleRefs.size() <= 0) {
            System.out.println("Could not find the requested samples.");
            return;
        }
        List<CaArrayEntityReference> fileRefs = selectRawFilesFromSamples(experimentRef, sampleRefs);
        if (fileRefs == null) {
            System.out.println("Could not find any raw files associated with the given samples.");
        } else {
            System.out.println("Found " + fileRefs.size() + " raw files associated with the given samples.");
        }
    }

    /**
     * Search for an experiment based on its title or public identifier.
     */
    private CaArrayEntityReference searchForExperiment() throws RemoteException {
        // Search for experiment with the given title.
        ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        experimentSearchCriteria.setTitle(EXPERIMENT_TITLE);

        // ... OR Search for experiment with the given public identifier.
        // ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        // experimentSearchCriteria.setPublicIdentifier(EXPERIMENT_PUBLIC_IDENTIFIER);

        List<Experiment> experiments = (client.searchForExperiments(experimentSearchCriteria, null)).getResults();
        if (experiments == null || experiments.size() <= 0) {
            return null;
        }

        // Assuming that only one experiment was found, pick the first result.
        // This will always be true for a search by public identifier, but may not be true for a search by title.
        Experiment experiment = experiments.get(0);
        return experiment.getReference();
    }

    /**
     * Search for samples based on name.
     */
    private Set<CaArrayEntityReference> searchForSamples(CaArrayEntityReference experimentRef) throws RemoteException {
        BiomaterialSearchCriteria criteria = new BiomaterialSearchCriteria();
        criteria.setExperiment(experimentRef);
        criteria.getNames().add(SAMPLE_NAME_01);
        criteria.getNames().add(SAMPLE_NAME_02);
        criteria.getTypes().add(BiomaterialType.SAMPLE);
        List<Biomaterial> samples = client.searchForBiomaterials(criteria, null).getResults();
        if (samples == null || samples.size() <= 0) {
            return null;
        }
        Set<CaArrayEntityReference> sampleRefs = new HashSet<CaArrayEntityReference>();
        for (Biomaterial sample : samples) {
            sampleRefs.add(sample.getReference());
        }
        return sampleRefs;
    }

    /**
     * Select all raw data files in the experiment.
     */
    private List<CaArrayEntityReference> selectRawFiles(CaArrayEntityReference experimentRef) throws RemoteException, InvalidReferenceException {
        FileSearchCriteria fileSearchCriteria = new FileSearchCriteria();
        fileSearchCriteria.setExperiment(experimentRef);
        fileSearchCriteria.getCategories().add(FileTypeCategory.RAW);

        List<DataFile> files = searchServiceHelper.filesByCriteria(fileSearchCriteria).list();
        if (files.size() <= 0) {
            return null;
        }

        // Return references to the files.
        List<CaArrayEntityReference> fileRefs = new ArrayList<CaArrayEntityReference>();
        for (DataFile file : files) {
            System.out.print(file.getName() + "  ");
            fileRefs.add(file.getReference());
        }
        return fileRefs;
    }

    /**
     * Select all Affymetrix CEL data files in the experiment.
     */
    private List<CaArrayEntityReference> selectCelFiles(CaArrayEntityReference experimentRef) throws RemoteException, InvalidReferenceException {
        FileSearchCriteria fileSearchCriteria = new FileSearchCriteria();
        fileSearchCriteria.setExperiment(experimentRef);

        //CaArrayEntityReference celFileTypeRef = getCelFileType();
        CaArrayEntityReference celFileTypeRef = new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.FileType:AFFYMETRIX_CEL");
        fileSearchCriteria.getTypes().add(celFileTypeRef);

        List<DataFile> files = searchServiceHelper.filesByCriteria(fileSearchCriteria).list();
        if (files.size() <= 0) {
            return null;
        }

        // Return references to the files.
        List<CaArrayEntityReference> fileRefs = new ArrayList<CaArrayEntityReference>();
        for (DataFile file : files) {
            System.out.print(file.getName() + "  ");
            fileRefs.add(file.getReference());
        }
        return fileRefs;
    }

    private CaArrayEntityReference getCelFileType() throws RemoteException {
        ExampleSearchCriteria<FileType> criteria = new ExampleSearchCriteria<FileType>();
        FileType exampleFileType = new FileType();
        exampleFileType.setName("AFFYMETRIX_CEL");
        criteria.setExample(exampleFileType);
        List<FileType> fileTypes = (client.searchByExample(criteria, null)).getResults();
        FileType celFileType = fileTypes.get(0);
        return celFileType.getReference();
    }

    /**
     * Select all derived data files with extension .CHP in the experiment.
     */
    private List<CaArrayEntityReference> selectChpFiles(CaArrayEntityReference experimentRef) throws RemoteException, InvalidReferenceException {
        FileSearchCriteria fileSearchCriteria = new FileSearchCriteria();
        fileSearchCriteria.setExperiment(experimentRef);

        fileSearchCriteria.getCategories().add(FileTypeCategory.DERIVED);
        fileSearchCriteria.setExtension("CHP");

        List<DataFile> files = searchServiceHelper.filesByCriteria(fileSearchCriteria).list();
        if (files.size() <= 0) {
            return null;
        }

        // Return references to the files.
        List<CaArrayEntityReference> fileRefs = new ArrayList<CaArrayEntityReference>();
        for (DataFile file : files) {
            System.out.print(file.getName() + "  ");
            fileRefs.add(file.getReference());
        }
        return fileRefs;
    }

    /**
     * Select all raw data files associated with the given samples.
     */
    private List<CaArrayEntityReference> selectRawFilesFromSamples(CaArrayEntityReference experimentRef, Set<CaArrayEntityReference> sampleRefs) throws RemoteException {
        FileSearchCriteria fileSearchCriteria = new FileSearchCriteria();
        fileSearchCriteria.setExperiment(experimentRef);
        fileSearchCriteria.setExperimentGraphNodes(sampleRefs);
        fileSearchCriteria.getCategories().add(FileTypeCategory.RAW);

        List<DataFile> files = client.searchForFiles(fileSearchCriteria, null).getResults();
        if (files.size() <= 0) {
            return null;
        }

        // Return references to the files.
        List<CaArrayEntityReference> fileRefs = new ArrayList<CaArrayEntityReference>();
        for (DataFile file : files) {
            System.out.print(file.getName() + "  ");
            fileRefs.add(file.getReference());
        }
        return fileRefs;
    }
}
