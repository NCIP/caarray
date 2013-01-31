//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.dao.SampleDao;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;

import java.util.Collections;
import java.util.List;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 *
 */
public class SampleDaoStub extends AbstractDaoStub implements SampleDao {

    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.dao.SampleDao#countSamplesByCharacteristicCategory(gov.nih.nci.caarray.domain.vocabulary.Category, java.lang.String)
     */
    public int countSamplesByCharacteristicCategory(Category c, String keyword) {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.dao.SampleDao#countSourcesByCharacteristicCategory(gov.nih.nci.caarray.domain.vocabulary.Category, java.lang.String)
     */
    public int countSourcesByCharacteristicCategory(Category c, String keyword) {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.dao.SampleDao#searchByCategory(com.fiveamsolutions.nci.commons.data.search.PageSortParams, java.lang.String, gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory[])
     */
    public <T extends AbstractBioMaterial> List<T> searchByCategory(PageSortParams<T> params, String keyword,
            BiomaterialSearchCategory... categories) {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.dao.SampleDao#searchCount(java.lang.String, gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory[])
     */
    public int searchCount(String keyword, BiomaterialSearchCategory... categories) {
        // TODO Auto-generated method stub
        return 0;
    }



    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.dao.SampleDao#searchSamplesByCharacteristicCategory(gov.nih.nci.caarray.domain.vocabulary.Category, java.lang.String)
     */
    public List<Sample> searchSamplesByCharacteristicCategory(Category c, String keyword) {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.dao.SampleDao#searchSourcesByCharacteristicCategory(gov.nih.nci.caarray.domain.vocabulary.Category, java.lang.String)
     */
    public List<Source> searchSourcesByCharacteristicCategory(Category c, String keyword) {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.dao.SampleDao#searchSourcesByCharacteristicCategory(gov.nih.nci.caarray.domain.vocabulary.Category, java.lang.String)
     */
    public List<Organism> searchForOrganismNames(String keyword) {
        return Collections.EMPTY_LIST;
    }

}
