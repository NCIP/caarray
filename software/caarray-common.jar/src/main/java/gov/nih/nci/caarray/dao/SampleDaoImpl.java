/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
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
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.search.AnnotationCriterion;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.domain.search.JoinableSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.util.HibernateHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.sample</code> package.
 *
 * @author mshestopalov
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.TooManyMethods", "PMD.ExcessiveClassLength" })
public class SampleDaoImpl extends AbstractCaArrayDaoImpl implements SampleDao {
    private static final String UNCHECKED = "unchecked";
    private static final String FROM_KEYWORD = " FROM ";
    private static final String SELECT_DISTINCT = " SELECT DISTINCT ";
    private static final String KEYWORD_SUB = "keyword";
    private static final String CATEGORY_SUB = "mycat";
    private static final String ORDER_BY = " ORDER BY ";
    private static final String LEFT_JOIN = " LEFT JOIN ";

    /**
     *
     * @param hibernateHelper the CaArrayHibernateHelper dependency
     */
    @Inject
    public SampleDaoImpl(CaArrayHibernateHelper hibernateHelper) {
        super(hibernateHelper);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends AbstractBioMaterial> List<T> searchByCategory(PageSortParams<T> params, String keyword,
            Class<T> biomaterialClass, BiomaterialSearchCategory... categories) {
        Query q = getSearchQuery(false, params, keyword, singletonClass(biomaterialClass), categories);
        q.setFirstResult(params.getIndex());
        if (params.getPageSize() > 0) {
            q.setMaxResults(params.getPageSize());
        }
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<AbstractBioMaterial> searchByCategory(PageSortParams<AbstractBioMaterial> params,
            String keyword, Set<Class<? extends AbstractBioMaterial>> biomaterialClasses,
            BiomaterialSearchCategory... categories) {
        Query q = getSearchQuery(false, params, keyword, biomaterialClasses, categories);
        q.setFirstResult(params.getIndex());
        if (params.getPageSize() > 0) {
            q.setMaxResults(params.getPageSize());
        }
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Source> searchSourcesByCharacteristicCategory(PageSortParams<Source> params, Category c,
            String keyword) {

        StringBuffer sb = new StringBuffer();

        sb.append(SELECT_DISTINCT + "s");
        sb.append(generateSourcesByCharacteristicCategoryClause(false, params, keyword));

        Query q = getCurrentSession().createQuery(sb.toString());
        q.setString(CATEGORY_SUB, c.getName());

        if (keyword != null && !keyword.equals("")) {
            q.setString(KEYWORD_SUB, "%" + keyword + "%");
        }

        q.setFirstResult(params.getIndex());
        if (params.getPageSize() > 0) {
            q.setMaxResults(params.getPageSize());
        }
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    public int countSourcesByCharacteristicCategory(Category c, String keyword) {
        StringBuffer sb = new StringBuffer();

        sb.append("SELECT count(DISTINCT s)");
        sb.append(generateSourcesByCharacteristicCategoryClause(true, null, keyword));

        Query q = getCurrentSession().createQuery(sb.toString());
        q.setString(CATEGORY_SUB, c.getName());
        if (!StringUtils.isEmpty(keyword)) {
            q.setString(KEYWORD_SUB, "%" + keyword + "%");
        }

        return ((Number) q.uniqueResult()).intValue();
    }

    /**
     * {@inheritDoc}
     */
    public int countSamplesByCharacteristicCategory(Category c, String keyword) {
        Query q = createQueryForSamplesByCharacteristicCategory(true, "SELECT count(DISTINCT s)", null, c,
                keyword);

        if (q == null) {
            return 0;
        }

        return ((Number) q.uniqueResult()).intValue();

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Sample> searchSamplesByCharacteristicCategory(PageSortParams<Sample> params,
            Category category, String keyword) {
        Query q = createQueryForSamplesByCharacteristicCategory(false, SELECT_DISTINCT + "s", params,
                category, keyword);

        if (q == null) {
            return Collections.emptyList();
        }

        q.setFirstResult(params.getIndex());
        if (params.getPageSize() > 0) {
            q.setMaxResults(params.getPageSize());
        }
        return q.list();
    }

    private Query createQueryForSamplesByCharacteristicCategory(boolean count, String selectClause,
            PageSortParams<Sample> params, Category c, String keyword) {
        Query returnVal = null;
        if (!StringUtils.isEmpty(keyword)) {
            List<TermBasedCharacteristic> tbsList = findCharacteristics(c.getName(), keyword);
            if (!tbsList.isEmpty()) {
                Map<String, List<? extends Serializable>> idBlocks = Maps.newHashMap();
                StringBuffer sb = new StringBuffer(selectClause);
                sb.append(generateSamplesByCharacteristicCategoryClause(count, tbsList, params, idBlocks));
                returnVal = getCurrentSession().createQuery(sb.toString());
                HibernateHelper.bindInClauseParameters(returnVal, idBlocks);
            }
        } else {
            StringBuffer sb = new StringBuffer(selectClause);
            sb.append(generateSamplesByCharacteristicCategoryClause(count, null, params, null));
            returnVal = getCurrentSession().createQuery(sb.toString());
            returnVal.setString(CATEGORY_SUB, c.getName());
        }

        return returnVal;
    }

    /**
     * Returns a list of TermBasedCharacterics with given category name and whose values
     * match the given keyword (using case-insensitive anywhere search).
     */
    @SuppressWarnings("unchecked")
    private List<TermBasedCharacteristic> findCharacteristics(String categoryName, String keyword) {
        String queryStr = "SELECT DISTINCT tbs from "
                + TermBasedCharacteristic.class.getName()
                + " tbs left join tbs.term t left join tbs.category c WHERE c.name = :category AND t.value like :"
                + KEYWORD_SUB;
        Query q = getCurrentSession().createQuery(queryStr);
        q.setString(KEYWORD_SUB, "%" + keyword + "%");
        q.setString("category", categoryName);
        return q.list();
    }

    private String generateSamplesByCharacteristicCategoryClause(boolean count,
            List<? extends Serializable> tbsList, PageSortParams<Sample> params,
            Map<String, List<? extends Serializable>> idBlocks) {

        // need to break this up into blocks of 500 to get around bug
        // http://opensource.atlassian.com/projects/hibernate/browse/HHH-2166
        @SuppressWarnings("deprecation")
        JoinableSortCriterion<Sample> sortCrit = params != null ? (JoinableSortCriterion<Sample>) params
                .getSortCriterion() : null;
        StringBuffer sb = new StringBuffer(FROM_KEYWORD).append(Sample.class.getName()).append(" s ");
        sb.append(getJoinClause(count, sortCrit, "s.characteristics chr", "chr.category cat",
                "s.sources src", "src.characteristics schr", "schr.category scat"));

        if (tbsList != null) {
            sb.append(" WHERE " + HibernateHelper.buildInClause(tbsList, "chr", idBlocks) + " OR "
                    + HibernateHelper.buildInClause(tbsList, "schr", idBlocks));
        } else {
            sb.append(" WHERE cat.name = :" + CATEGORY_SUB + " OR scat.name = :" + CATEGORY_SUB);
        }

        if (!count && sortCrit != null) {
            sb.append(ORDER_BY).append(getOrderByField(sortCrit));
            if (params.isDesc()) {
                sb.append(" desc");
            }
        }

        return sb.toString();
    }

    private String generateSourcesByCharacteristicCategoryClause(boolean count,
            PageSortParams<Source> params, String keyword) {

        @SuppressWarnings("deprecation")
        JoinableSortCriterion<Source> sortCrit = params != null ? (JoinableSortCriterion<Source>) params
                .getSortCriterion() : null;
        StringBuffer sb = new StringBuffer(FROM_KEYWORD).append(Source.class.getName()).append(" s ");
        sb.append(getJoinClause(count, sortCrit, "s.characteristics chr", "chr.category cat"));
        sb.append(", ");
        sb.append(TermBasedCharacteristic.class.getName() + " tbs " + LEFT_JOIN + " tbs.term t "
                + " WHERE cat.name = :" + CATEGORY_SUB + " AND chr.id = tbs.id");

        if (!StringUtils.isEmpty(keyword)) {
            sb.append(" AND t.value like :" + KEYWORD_SUB);
        }

        if (!count && sortCrit != null) {
            sb.append(ORDER_BY).append(getOrderByField(sortCrit));
            if (params.isDesc()) {
                sb.append(" desc");
            }
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Sample> searchSamplesByExperimentAndCategory(String keyword, Experiment e,
            SearchSampleCategory... c) {
        StringBuffer sb = new StringBuffer();

        sb.append(SELECT_DISTINCT + "s");
        sb.append(FROM_KEYWORD + Sample.class.getName() + " s");
        sb.append(getJoinClause(false, null, c));
        sb.append(getWhereClause(singletonClass(Sample.class), c));
        sb.append(" AND s.experiment = :exp" + ORDER_BY + "s.name");
        Query q = getCurrentSession().createQuery(sb.toString());
        q.setEntity("exp", e);
        q.setString(KEYWORD_SUB, "%" + keyword + "%");

        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked" })
    public List<Sample> searchSamplesByExperimentAndArbitraryCharacteristicValue(String keyword,
            Experiment experiment, Category category) {

        String sQueryPattern =
              "select distinct s " 
            + "from @CHARACTERISTIC@ ch " 
            + "     join ch.bioMaterial s " 
            + "where s.class = :cls" 
            + "      and s.experiment = :exp " 
            + "      and ch.category = :cat " 
            + "      and @VALUE@ like :keyw";

        Map<String, String> sQuerySubstitutes = Maps.newHashMap();
        sQuerySubstitutes.put("TermBasedCharacteristic", "ch.term.value");
        sQuerySubstitutes.put("UserDefinedCharacteristic", "ch.value");
        sQuerySubstitutes.put("MeasurementCharacteristic", "ch.value");

        List<Sample> sampleList = Lists.newArrayList();
        for (String materialType : Sets.newHashSet(Sample.DISCRIMINATOR, Source.DISCRIMINATOR)) {
            for (Entry<String, String> substitute : sQuerySubstitutes.entrySet()) {
                String sQuery = sQueryPattern.replaceAll("@CHARACTERISTIC@", substitute.getKey()).replaceAll(
                        "@VALUE@", substitute.getValue());
                Query query = getCurrentSession().createQuery(sQuery);
                query.setString("cls", materialType);
                query.setEntity("exp", experiment);
                query.setEntity("cat", category);
                query.setString("keyw", "%" + keyword + "%");
                List<AbstractBioMaterial> bioMaterialList = (List<AbstractBioMaterial>) query.list();
                for (AbstractBioMaterial bioMaterial : bioMaterialList) {
                    if (bioMaterial instanceof Sample) {
                        sampleList.add((Sample) bioMaterial);
                    } else {
                        sampleList.addAll(((Source) bioMaterial).getSamples());
                    }
                }
            }
        }
        return sampleList;
    }

    /**
     * {@inheritDoc}
     */
    public int searchCount(String keyword, Class<? extends AbstractBioMaterial> biomaterialClass,
            BiomaterialSearchCategory... categories) {
        Query q = getSearchQuery(true, null, keyword, singletonClass(biomaterialClass), categories);
        return ((Number) q.uniqueResult()).intValue();
    }

    private <T extends AbstractBioMaterial> Query getSearchQuery(boolean count, PageSortParams<T> params,
            String keyword, Set<Class<? extends AbstractBioMaterial>> biomaterialSubclasses,
            BiomaterialSearchCategory... categories) {
        @SuppressWarnings("deprecation")
        JoinableSortCriterion<T> sortCrit = params != null ? (JoinableSortCriterion<T>) params
                .getSortCriterion() : null;
        StringBuffer sb = new StringBuffer();
        if (count) {
            sb.append("SELECT COUNT(DISTINCT s)");
        } else {
            sb.append(SELECT_DISTINCT + "s");
        }

        sb.append(generateSearchClause(count, sortCrit, biomaterialSubclasses, categories));

        if (!count && sortCrit != null) {
            sb.append(ORDER_BY).append(getOrderByField(sortCrit));
            if (params.isDesc()) {
                sb.append(" desc");
            }
        }
        Query q = getCurrentSession().createQuery(sb.toString());
        q.setString(KEYWORD_SUB, "%" + StringUtils.defaultString(keyword) + "%");
        if (biomaterialSubclasses.size() > 1) {
            Set<String> discriminators = new HashSet<String>();
            for (Class<? extends AbstractBioMaterial> bmClass : biomaterialSubclasses) {
                discriminators.add(getDiscriminator(bmClass));
            }
            q.setParameterList("klass", discriminators);
        }
        return q;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "PMD" })
    public <T extends AbstractBioMaterial> List<T> searchByCriteria(PageSortParams<T> params,
            BiomaterialSearchCriteria criteria) {
        Criteria c = getCurrentSession().createCriteria(AbstractBioMaterial.class);

        if (criteria.getExperiment() != null) {
            c.add(Restrictions.eq("experiment", criteria.getExperiment()));
        }

        if (!criteria.getNames().isEmpty()) {
            c.add(Restrictions.in("name", criteria.getNames()));
        }

        if (!criteria.getExternalIds().isEmpty()) {
            c.add(Restrictions.in("externalId", criteria.getExternalIds()));
        }

        if (!criteria.getBiomaterialClasses().isEmpty()) {
            // unfortunately due to a hibernate bug we have to explicitly specify discriminators
            // rather than being able to use classnames
            Set<String> discriminators = new HashSet<String>();
            for (Class<? extends AbstractBioMaterial> bmClass : criteria.getBiomaterialClasses()) {
                discriminators.add(getDiscriminator(bmClass));
            }
            c.add(Restrictions.in("class", discriminators));
        }

        if (!criteria.getAnnotationCriterions().isEmpty()) {
            Set<String> diseaseStates = new HashSet<String>();
            Set<String> tissueSites = new HashSet<String>();
            Set<String> cellTypes = new HashSet<String>();
            Set<String> materialTypes = new HashSet<String>();
            for (AnnotationCriterion ac : criteria.getAnnotationCriterions()) {
                if (ac.getCategory().getName()
                        .equals(ExperimentOntologyCategory.DISEASE_STATE.getCategoryName())) {
                    diseaseStates.add(ac.getValue());
                } else if (ac.getCategory().getName()
                        .equals(ExperimentOntologyCategory.CELL_TYPE.getCategoryName())) {
                    cellTypes.add(ac.getValue());
                } else if (ac.getCategory().getName()
                        .equals(ExperimentOntologyCategory.MATERIAL_TYPE.getCategoryName())) {
                    materialTypes.add(ac.getValue());
                } else if (ac.getCategory().getName()
                        .equals(ExperimentOntologyCategory.ORGANISM_PART.getCategoryName())) {
                    tissueSites.add(ac.getValue());
                }
            }

            if (!diseaseStates.isEmpty() || !tissueSites.isEmpty() || !cellTypes.isEmpty()
                    || !materialTypes.isEmpty()) {
                Junction and = Restrictions.conjunction();
                addAnnotationCriterionValues(c, and, diseaseStates, "diseaseState", "ds");
                addAnnotationCriterionValues(c, and, tissueSites, "tissueSite", "ts");
                addAnnotationCriterionValues(c, and, materialTypes, "materialType", "mt");
                addAnnotationCriterionValues(c, and, cellTypes, "cellType", "ct");
                c.add(and);
            }
        }

        c.setFirstResult(params.getIndex());
        if (params.getPageSize() > 0) {
            c.setMaxResults(params.getPageSize());
        }
        c.addOrder(toOrder(params));
        return c.list();
    }

    private static String getDiscriminator(Class<? extends AbstractBioMaterial> bmClass) {
        String errorMsg = "Not a valid biomaterial class (no DISCRIMINATOR field): ";
        try {
            Field discField = bmClass.getDeclaredField("DISCRIMINATOR");
            return (String) discField.get(null);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(errorMsg + bmClass, e);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(errorMsg + bmClass, e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(errorMsg + bmClass, e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(errorMsg + bmClass, e);
        }
    }

    private void addAnnotationCriterionValues(Criteria c, Junction junction, Set<String> values,
            String assocPath, String alias) {
        if (!values.isEmpty()) {
            c.createAlias(assocPath, alias);
            junction.add(Restrictions.in(alias + ".value", values));
        }
    }

    private String generateSearchClause(boolean count,
            JoinableSortCriterion<? extends AbstractBioMaterial> sortCrit,
            Set<Class<? extends AbstractBioMaterial>> biomaterialSubclasses,
            BiomaterialSearchCategory... categories) {
        StringBuilder sb = new StringBuilder();
        Class<? extends AbstractBioMaterial> fromClass = biomaterialSubclasses.size() == 1 ? biomaterialSubclasses
                .iterator().next() : AbstractBioMaterial.class;
        sb.append(FROM_KEYWORD).append(fromClass.getName()).append(" s")
                .append(getJoinClause(count, sortCrit, categories))
                .append(getWhereClause(biomaterialSubclasses, categories));
        return sb.toString();
    }

    private static String getJoinClause(boolean count,
            JoinableSortCriterion<? extends AbstractBioMaterial> sortCrit, String... joinTables) {
        LinkedHashSet<String> joins = new LinkedHashSet<String>(Arrays.asList(joinTables));
        return generateJoinClause(count, sortCrit, joins);
    }

    private static String getJoinClause(boolean count,
            JoinableSortCriterion<? extends AbstractBioMaterial> sortCrit,
            BiomaterialSearchCategory... categories) {
        LinkedHashSet<String> joins = new LinkedHashSet<String>();
        for (BiomaterialSearchCategory category : categories) {
            joins.addAll(Arrays.asList(category.getJoins()));
        }
        return generateJoinClause(count, sortCrit, joins);
    }

    private static String generateJoinClause(boolean count,
            JoinableSortCriterion<? extends AbstractBioMaterial> sortCrit, Set<String> joins) {
        String[] orderByJoins = null;

        if (!count && sortCrit != null) {
            orderByJoins = sortCrit.getJoins();
        }

        Set<String> completeJoinsList = new LinkedHashSet<String>();
        completeJoinsList.addAll(joins);

        if (orderByJoins != null) {
            completeJoinsList.addAll(Arrays.asList(orderByJoins));
        }

        StringBuffer sb = new StringBuffer();
        for (String joinTable : completeJoinsList) {
            sb.append(LEFT_JOIN).append(joinTable.replace("this", "s"));
        }

        return sb.toString();

    }

    private static String getOrderByField(JoinableSortCriterion<? extends AbstractBioMaterial> sortCrit) {
        String searchByField = sortCrit.getOrderField();
        return searchByField.replace("this", "s");
    }

    private Set<Class<? extends AbstractBioMaterial>> singletonClass(
            Class<? extends AbstractBioMaterial> klass) {
        Set<Class<? extends AbstractBioMaterial>> bmClasses = new HashSet<Class<? extends AbstractBioMaterial>>();
        bmClasses.add(klass);
        return bmClasses;
    }

    private String getWhereClause(Set<Class<? extends AbstractBioMaterial>> biomaterialSubclasses,
            BiomaterialSearchCategory... categories) {
        StringBuffer sb = new StringBuffer(" WHERE ");
        if (biomaterialSubclasses.size() > 1) {
            sb.append(" s.class in (:klass) AND ");
        }
        sb.append('(');
        int i = 0;
        for (BiomaterialSearchCategory category : categories) {
            sb.append(i++ == 0 ? "" : " OR ").append('(')
                    .append(category.getWhereClause().replace("this", "s")).append(')');
        }
        sb.append(')');
        return sb.toString();
    }

}
