//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.security;

import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.property.BasicPropertyAccessor;
import org.hibernate.property.Getter;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.Setter;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

/**
 * Hibernate Post-load event listener that applies attribute security.
 *
 * @author dkokotov
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class SecurityPolicyPostLoadEventListener implements PostLoadEventListener {
    private static final Logger LOG = Logger.getLogger(SecurityPolicyPostLoadEventListener.class);
    private static final long serialVersionUID = -2071964672876972370L;
    private static final PropertyAccessor BASIC_PROPERTY_ACCESSOR = new BasicPropertyAccessor();

    /**
     * {@inheritDoc}
     */
    public void onPostLoad(PostLoadEvent event) {
        if (SecurityUtils.isPrivilegedMode()) {
            return;
        }
        Project project = null;
        Experiment experiment = null;
        if (event.getEntity() instanceof Project) {
            project = (Project) event.getEntity();
            experiment = project.getExperiment();
        } else if (event.getEntity() instanceof Experiment) {
            experiment = ((Experiment) event.getEntity());
            project = experiment.getProject();
        } else {
            // only care about projects and experiments
            return;
        }

        // unfortunately, there is no good way to tell which will be loaded first - experiment
        // or project. and we need both to be loaded because otherwise we cannot both accurately
        // obtain the set of policies and have them be applied to the actual properties. so we
        // need to apply the policies to both project and experiment for when both are loaded,
        // to be sure
        Set<SecurityPolicy> policies = project.getApplicablePolicies(UsernameHolder.getCsmUser());

        if (!policies.isEmpty()) {
            applySecurityPolicies(project, getPersister(event.getSession(), project), policies);
            applySecurityPolicies(experiment, getPersister(event.getSession(), experiment), policies);

            for (Source source : experiment.getSources()) {
                applySecurityPolicies(source, getPersister(event.getSession(), source), policies);
            }
            for (Sample sample : experiment.getSamples()) {
                applySecurityPolicies(sample, getPersister(event.getSession(), sample), policies);
            }
            for (Extract extract : experiment.getExtracts()) {
                applySecurityPolicies(extract, getPersister(event.getSession(), extract), policies);
            }
            for (LabeledExtract le : experiment.getLabeledExtracts()) {
                applySecurityPolicies(le, getPersister(event.getSession(), le), policies);
            }
        }
    }

    private EntityPersister getPersister(SessionImplementor session, Object entity) {
        return session.getPersistenceContext().getEntry(entity).getPersister();
    }

    private void applySecurityPolicies(Object entity, EntityPersister persister, Set<SecurityPolicy> policies) {
        String[] propertyNames = persister.getPropertyNames();
        Type[] types = persister.getPropertyTypes();
        if (!policies.isEmpty()) {
            for (int i = 0; i < propertyNames.length; i++) {
                Getter getter = BASIC_PROPERTY_ACCESSOR.getGetter(entity.getClass(), propertyNames[i]);
                Setter setter = BASIC_PROPERTY_ACCESSOR.getSetter(entity.getClass(), propertyNames[i]);
                boolean disallowed = false;
                for (SecurityPolicy policy : policies) {
                    if (!policy.allowProperty(entity, propertyNames[i])) {
                        clearDisallowedProperty(entity, propertyNames[i], types[i], getter, setter);
                        disallowed = true;
                        continue;
                    }
                }
                if (!disallowed) {
                    applyTransformations(policies, entity, propertyNames[i], getter, setter);
                }
            }
        }
    }

    /**
     * @param entity
     * @param getter
     * @param setter
     */
    private void applyTransformations(Set<SecurityPolicy> policies, Object entity, String propertyName,
            Getter getter, Setter setter) {
        Transformer transformer = getPropertyTransformer(policies, entity, propertyName);
        if (transformer != null) {
            Object originalVal = getter.get(entity);
            Object transformedVal = transformer.transform(originalVal);
            setter.set(entity, transformedVal, (SessionFactoryImplementor) HibernateUtil
                    .getSessionFactory());
        }
        Closure mutator = getPropertyMutator(policies, entity, propertyName);
        if (mutator != null) {
            mutator.execute(getter.get(entity));
        }
    }

    /**
     * Returns a transformer to be applied to the given property on the given entity, given the set
     * of applicable policies. If multiple Transformers are to be applied, then the returned
     * Transformer represents a chaining of those Transformers. If no Transformers are to be applied,
     * returns null
     *
     * @param policies the active policies
     * @param entity the object in question
     * @param propertyName the name of the property on the object
     * @return the Transformer to be applied, or null if none should be applied
     */
    private Transformer getPropertyTransformer(Set<SecurityPolicy> policies, Object entity, String propertyName) {
        AttributePolicy attributePolicy = SecurityPolicy.getAttributePolicy(entity, propertyName);
        if (attributePolicy == null) {
            return null;
        }
        List<Transformer> transformers = new ArrayList<Transformer>();
        for (AttributeTransformer attrTransformer : attributePolicy.transformers()) {
            if (policiesMatch(attrTransformer.policies(), policies)) {
                try {
                    transformers.add(attrTransformer.transformer().newInstance());
                } catch (InstantiationException e) {
                    LOG.warn("Could not instantiate transformer of class " + attrTransformer.transformer().getName(),
                            e);
                } catch (IllegalAccessException e) {
                    LOG.warn("Could not instantiate transformer of class " + attrTransformer.transformer().getName(),
                            e);
                }
            }
        }
        return transformers.isEmpty() ? null : TransformerUtils.chainedTransformer(transformers);
    }

    /**
     * Returns a Closure mutator to be applied to the given property on the given entity, given the set
     * of applicable policies. If multiple Closures are to be applied, then the returned Closure
     * represents a chaining of those Closure. If no Closures are to be applied,
     * returns null
     *
     * @param policies the active policies
     * @param entity the object in question
     * @param propertyName the name of the property on the object
     * @return the Closure to be applied, or null if none should be applied
     */
    private Closure getPropertyMutator(Set<SecurityPolicy> policies, Object entity, String propertyName) {
        AttributePolicy attributePolicy = SecurityPolicy.getAttributePolicy(entity, propertyName);
        if (attributePolicy == null) {
            return null;
        }
        List<Closure> mutators = new ArrayList<Closure>();
        for (AttributeMutator attrMutator : attributePolicy.mutators()) {
            if (policiesMatch(attrMutator.policies(), policies)) {
                try {
                    mutators.add(attrMutator.mutator().newInstance());
                } catch (InstantiationException e) {
                    LOG.warn("Could not instantiate closure of class " + attrMutator.mutator().getName(),
                            e);
                } catch (IllegalAccessException e) {
                    LOG.warn("Could not instantiate closure of class " + attrMutator.mutator().getName(),
                            e);
                }
            }
        }
        return mutators.isEmpty() ? null : ClosureUtils.chainedClosure(mutators);
    }

    private boolean policiesMatch(String[] policyNames, Set<SecurityPolicy> policies) {
        for (SecurityPolicy policy : policies) {
            if (ArrayUtils.contains(policyNames, policy.getName())) {
                return true;
            }
        }
        return false;
    }


    private void clearDisallowedProperty(Object entity, String propertyName, Type propertyType, Getter getter,
            Setter setter) {
        if (propertyType instanceof CollectionType) {
            CollectionType ct = (CollectionType) propertyType;
            Object val = null;
            if (Collection.class.isAssignableFrom(ct.getReturnedClass())
                    || Map.class.isAssignableFrom(ct.getReturnedClass())) {
                val = CaArrayUtils.emptyCollectionOrMapFor(ct.getReturnedClass());
            }
            setter.set(entity, val, (SessionFactoryImplementor) HibernateUtil.getSessionFactory());
        } else if (!getter.getReturnType().isPrimitive()) {
            setter.set(entity, null, (SessionFactoryImplementor) HibernateUtil.getSessionFactory());
        } else {
            LOG.warn("Could not null out primitive property " + propertyName);
        }
    }
}
