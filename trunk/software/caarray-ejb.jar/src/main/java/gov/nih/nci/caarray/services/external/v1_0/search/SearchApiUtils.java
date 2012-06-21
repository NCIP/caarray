package gov.nih.nci.caarray.services.external.v1_0.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;

/**
 * SearchApiUtils is a helper interface for working with the caArray Search API. It makes it easier to perform a variety
 * of common tasks.
 * 
 * There are implementations for the Java and Grid API's. Using this interface in client code will make it easier to
 * migrate from one flavor of the API to another in the future.
 * 
 * @author dkokotov
 */
public interface SearchApiUtils {
    /**
     * Returns a Search instance encapsulating a search for experiments by criteria. 
     * @param criteria the search criteria.
     * @return the Search instance.
     */
    Search<Experiment> experimentsByCriteria(ExperimentSearchCriteria criteria);

    /**
     * Returns a Search instance encapsulating a search for experiments by keyword. 
     * @param criteria the search criteria.
     * @return the Search instance.
     */
    Search<Experiment> experimentsByKeyword(KeywordSearchCriteria criteria);

    /**
     * Returns a Search instance encapsulating a search for biomaterials by criteria. 
     * @param criteria the search criteria.
     * @return the Search instance.
     */
    Search<Biomaterial> biomaterialsByCriteria(BiomaterialSearchCriteria criteria);

    /**
     * Returns a Search instance encapsulating a search for experiments by keyword. 
     * @param criteria the search criteria.
     * @return the Search instance.
     */
    Search<Biomaterial> biomaterialsByKeyword(BiomaterialKeywordSearchCriteria criteria);

    /**
     * Returns a Search instance encapsulating a search for files by criteria. 
     * @param criteria the search criteria.
     * @return the Search instance.
     */
    Search<File> filesByCriteria(FileSearchCriteria criteria);

    /**
     * Returns a Search instance encapsulating a search for hybridizations by criteria. 
     * @param criteria the search criteria.
     * @return the Search instance.
     */
    Search<Hybridization> hybridizationsByCriteria(HybridizationSearchCriteria criteria);

    /**
     * Returns a Search instance encapsulating a search by example. 
     * @param criteria the search criteria.
     * @param <T> the type of the example entity.
     * @return the Search instance.
     */
    <T extends AbstractCaArrayEntity> Search<T> byExample(ExampleSearchCriteria<T> criteria);

    /**
     * Wrapper for exceptions that may be thrown by invoking the actual service API methods. This is needed
     * because these exceptions may be thrown from methods implementing generic APIs (e.g. Iterator.next()) which
     * do not allow for checked exceptions. In that case, the original exception will be wrapped in this
     * class (which is extends RuntimeException), and can be retrieved by calling getCause().
     * 
     * @author dkokotov
     */
    public static final class WrapperException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        /**
         * @param t the wrapped exception
         */
        public WrapperException(Throwable t) {
            super(t);
        }        
    }
}