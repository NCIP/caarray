//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.base.TestProperties;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;


/**
 *
 */
public class TestApiPerformance {

    private static final String TITLE =
        "TCGA Analysis of Gene Expression for Glioblastoma Multiforme Using Affymetrix HT_HG-U133A";

    @Test
    public void testApiPerformance() throws ServerConnectionException {
        CaArrayServer server = new CaArrayServer(TestProperties.getServerHostname(), TestProperties.getServerJndiPort());
        server.connect();
        CaArraySearchService searchService = server.getSearchService();
        Experiment searchExperiment = new Experiment();
        searchExperiment.setTitle(TITLE);
        List<Experiment> matches = searchService.search(searchExperiment);

        QuantitationType searchIntensity = new QuantitationType();
        searchIntensity.setName("CELIntensity");
        QuantitationType intensity = searchService.search(searchIntensity).iterator().next();

        Set<Hybridization> hybridizations = getAllHybridizations(matches);

        DataRetrievalRequest request = new DataRetrievalRequest();
        request.addQuantitationType(intensity);
        request.getHybridizations().addAll(hybridizations);
        long start = System.currentTimeMillis();
        DataSet dataSet = server.getDataRetrievalService().getDataSet(request);
        assertEquals(hybridizations.size(), dataSet.getHybridizationDataList().size());
        assertEquals(1, dataSet.getHybridizationDataList().get(0).getColumns().size());
        assertNotNull(dataSet);
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("Retrieval of DataSet with all hybridizations, in milliseconds: " + time);
        long totalMilliseconds = 0L;

        int index = 0;
        for (Hybridization hybridization : hybridizations) {
            request = new DataRetrievalRequest();
            request.addQuantitationType(intensity);
            request.addHybridization(hybridization);
            start = System.currentTimeMillis();
            dataSet = server.getDataRetrievalService().getDataSet(request);
            assertEquals(1, dataSet.getHybridizationDataList().size());
            assertEquals(1, dataSet.getHybridizationDataList().get(0).getColumns().size());
            assertNotNull(dataSet);
            end = System.currentTimeMillis();
            time = end - start;
            totalMilliseconds += time;
            System.out.println(index + ": Retrieval in milliseconds: " + time);
            index++;
        }
        System.out.println("Total time in milliseconds: " + totalMilliseconds);
    }

    /**
     * @param matches
     * @return
     */
    private Set<Hybridization> getAllHybridizations(List<Experiment> matches) {
        Set<Hybridization> hybridizations = new HashSet<Hybridization>();
        for (Experiment experiment : matches) {
            hybridizations.addAll(getAllHybridizations(experiment));
        }
        return hybridizations;
    }

    private Set<Hybridization> getAllHybridizations(Experiment experiment) {
        Set<Hybridization> hybridizations = new HashSet<Hybridization>();
        hybridizations.addAll(experiment.getHybridizations());
        return hybridizations;
    }
}
