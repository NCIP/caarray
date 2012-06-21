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
package caarray.client.examples.java;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.AbstractExperimentGraphNode;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationColumn;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationSet;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationValueSet;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.value.AbstractValue;
import gov.nih.nci.caarray.external.v1_0.value.MeasurementValue;
import gov.nih.nci.caarray.external.v1_0.value.TermValue;
import gov.nih.nci.caarray.external.v1_0.value.UserDefinedValue;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.search.JavaSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A client downloading sample annotations for hybridizations using the caArray Java API.
 *
 * @author Rashmi Srinivasa
 */
public class DownloadSampleAnnotationsForHybridizations {
    private static SearchService searchService = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;

    public static void main(String[] args) {
        DownloadSampleAnnotationsForHybridizations downloader = new DownloadSampleAnnotationsForHybridizations();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            searchService = server.getSearchService();
            searchServiceHelper = new JavaSearchApiUtils(searchService);
            System.out.println("Downloading sample annotations for hybridizations from " + EXPERIMENT_TITLE + "...");
            downloader.download();
        } catch (Throwable t) {
            System.out.println("Error while downloading sample annotations for hybridizations.");
            t.printStackTrace();
        }
    }

    private void download() throws InvalidInputException {
        // Select an experiment of interest.
        CaArrayEntityReference experimentRef = selectExperiment();
        if (experimentRef == null) {
            System.err.println("Could not find experiment with the requested title or public identifier.");
            return;
        }

        // Select hybridizations in the experiment.
        List<CaArrayEntityReference> hybridizationRefs = selectHybridizations(experimentRef);
        if (hybridizationRefs == null) {
            System.err.println("Could not find any hybridizations with CHP data in the selected experiment.");
            return;
        }

        // Retrieve the annotations for samples/biomaterials associated with these hybridizations.
        AnnotationSetRequest annotationSetRequest = new AnnotationSetRequest();
        annotationSetRequest.setExperimentGraphNodes(hybridizationRefs);
        List<Category> allCategories = searchService.getAllCharacteristicCategories(experimentRef);
        // Typically, client application will pick categories of interest; we're using all categories here.
        for (Category category : allCategories) {
            annotationSetRequest.getCategories().add(category.getReference());
        }
        AnnotationSet annotationSet = searchService.getAnnotationSet(annotationSetRequest);
        if (annotationSet == null) {
            System.err.println("Retrieved null annotation set.");
            return;
        }
        printAnnotationSet(annotationSet);
    }

    /**
     * Search for experiments and select one.
     */
    private CaArrayEntityReference selectExperiment() throws InvalidInputException {
        // Search for experiment with the given title.
        ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        experimentSearchCriteria.setTitle(EXPERIMENT_TITLE);

        // ... OR Search for experiment with the given public identifier.
        // ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        // experimentSearchCriteria.setPublicIdentifier(EXPERIMENT_PUBLIC_IDENTIFIER);

        List<Experiment> experiments = (searchService.searchForExperiments(experimentSearchCriteria, null)).getResults();
        if (experiments == null || experiments.size() <= 0) {
            return null;
        }

        // Assuming that only one experiment was found, pick the first result.
        // This will always be true for a search by public identifier, but may not be true for a search by title.
        Experiment experiment = experiments.iterator().next();
        return experiment.getReference();
    }

    /**
     * Select all hybridizations in the given experiment that have CHP data.
     */
    private List<CaArrayEntityReference> selectHybridizations(CaArrayEntityReference experimentRef)
            throws InvalidInputException {
        HybridizationSearchCriteria searchCriteria = new HybridizationSearchCriteria();
        searchCriteria.setExperiment(experimentRef);
        List<Hybridization> hybridizations = (searchServiceHelper.hybridizationsByCriteria(searchCriteria)).list();
        if (hybridizations == null || hybridizations.size() <= 0) {
            return null;
        }

        // Get references to the hybridizations.
        List<CaArrayEntityReference> hybridizationRefs = new ArrayList<CaArrayEntityReference>();
        for (Hybridization hybridization : hybridizations) {
            hybridizationRefs.add(hybridization.getReference());
        }

        // Check if the hybridizations have CHP files associated with them.
        if (haveChpFiles(experimentRef, hybridizationRefs)) {
            return hybridizationRefs;
        } else {
            return null;
        }
    }

    private boolean haveChpFiles(CaArrayEntityReference experimentRef, List<CaArrayEntityReference> hybridizationRefs) throws 
            InvalidInputException {
        FileSearchCriteria searchCriteria = new FileSearchCriteria();
        searchCriteria.setExperiment(experimentRef);
        CaArrayEntityReference chpFileTypeRef = getChpFileType();
        HashSet<CaArrayEntityReference> setOfHybridizationRefs = new HashSet<CaArrayEntityReference>(hybridizationRefs);
        searchCriteria.setExperimentGraphNodes(setOfHybridizationRefs);
        searchCriteria.getTypes().add(chpFileTypeRef);
        List<File> dataFiles = (searchServiceHelper.filesByCriteria(searchCriteria)).list();
        if (dataFiles == null || dataFiles.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    private CaArrayEntityReference getChpFileType() throws InvalidInputException {
        ExampleSearchCriteria<FileType> criteria = new ExampleSearchCriteria<FileType>();
        FileType exampleFileType = new FileType();
        exampleFileType.setName("AFFYMETRIX_CHP");
        criteria.setExample(exampleFileType);
        SearchResult<FileType> results = searchService.searchByExample(criteria, null);
        List<FileType> fileTypes = results.getResults();
        FileType chpFileType = fileTypes.iterator().next();
        return chpFileType.getReference();
    }

    private void printAnnotationSet(AnnotationSet annotationSet) {
        // Ordered list of row headers (categories)
        List<Category> categories = annotationSet.getCategories();
        printCategories(categories);
        // List of columns, one per biomaterial/hybridization
        List<AnnotationColumn> annotationColumns = annotationSet.getColumns();
        for (AnnotationColumn annotationColumn : annotationColumns) {
            // Ordered list of annotation values in the column (values are in the same order as row headers/categories)
            printColumnValues(annotationColumn);
        }
    }

    private void printCategories(List<Category> categories) {
        for (Category category : categories) {
            System.out.print(category.getName() + " ");
        }
        System.out.println();
    }

    private void printColumnValues(AnnotationColumn annotationColumn) {
        AbstractExperimentGraphNode node = annotationColumn.getNode();
        System.out.println("Annotation values for biomaterial/hybridization " + node.getName() + ":");

        List<AnnotationValueSet> annotationValues = annotationColumn.getValueSets();
        for (AnnotationValueSet valuesForOneCategory : annotationValues) {
            System.out.print(valuesForOneCategory.getCategory().getName() + " = ");
            // There will usually be only one value, but there may be multiple in some cases.
            // E.g., if a hybridization originated from 2 samples, each with conflicting values for a category, there will be multiple values.
            Set<AbstractValue> values = valuesForOneCategory.getValues();
            for (AbstractValue value : values) {
                if (TermValue.class.equals(value.getClass())) {
                    TermValue termValue = (TermValue) value;
                    System.out.println(termValue.getTerm().getValue() + ",");
                } else if (MeasurementValue.class.equals(value.getClass())) {
                    MeasurementValue measurementValue = (MeasurementValue) value;
                    System.out.print(measurementValue.getMeasurement() + ",");
                } else if (UserDefinedValue.class.equals(value.getClass())) {
                    UserDefinedValue freeTextValue = (UserDefinedValue) value;
                    System.out.print(freeTextValue.getValue() + ",");
                }
            }
            System.out.println();
        }
    }
}
