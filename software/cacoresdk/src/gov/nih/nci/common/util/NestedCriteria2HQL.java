package gov.nih.nci.common.util;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

public class NestedCriteria2HQL
{

	private NestedCriteria criteria;
	private Configuration cfg;
	private Session session;
	private Query query;
	private Query countQuery;

	private static Logger log = Logger.getLogger(NestedCriteria2HQL.class);

	private List paramList = new ArrayList();

	public NestedCriteria2HQL(NestedCriteria crit, Configuration cfg, Session session)
	{
		this.criteria = crit;
		this.cfg = cfg;
		this.session = session;
	}

	public Query translate() throws Exception
	{
		StringBuffer hql = new StringBuffer();
		String srcAlias = "";
		String destAlias = getAlias(criteria.getTargetObjectName(),1);

		hql.append("from ");
		hql.append(criteria.getTargetObjectName()).append(" ").append(destAlias);;
		hql.append(" where ");

		NestedCriteria temp = criteria;
		Stack closingStack = new Stack();
		while (temp != null)
		{
			srcAlias = getAlias(temp.getSourceName(),1);
			destAlias = getAlias(temp.getTargetObjectName(),1);
			hql.append(destAlias).append(".id in ");
			hql.append("(");
			hql.append("select ").append(destAlias).append(".id ");
			hql.append(" from ").append(temp.getTargetObjectName()).append(" ").append(destAlias);

			//Source and Target will be same in the case of it is the only node in the linked list.
			if (!temp.getSourceName().equals(temp.getTargetObjectName()))
			{
				hql.append(",").append(temp.getSourceName()).append(" ").append(srcAlias);
				hql.append(" where ");
				//Rolename will be null only in case of inheritance
				String roleName = temp.getRoleName() == null?"id":temp.getRoleName();
				if (temp.isTargetCollection())
					hql.append(destAlias).append(" in elements(").append(srcAlias).append(".").append(temp.getRoleName()).append(")");
				else
					hql.append(destAlias).append("=").append(srcAlias).append(".").append(roleName);

				hql.append(" and ");
			} else
			{
				hql.append(" where ");
			}

			//If it is the last node then process the attached object list collection
			if (temp.getInternalNestedCriteria() == null)
			{
				for (Iterator i = temp.getSourceObjectList().iterator(); i.hasNext();)
				{
					Object obj = i.next();
					hql.append(srcAlias).append(".id in ").append("(");
					hql.append(getObjectCriterion(obj, cfg));
					hql.append(")");
					if (i.hasNext())
						hql.append(" or ");
				}
			}

			closingStack.push(")");
			temp = temp.getInternalNestedCriteria();
		}

		while (!closingStack.empty())
			hql.append(closingStack.pop());

		query = prepareQuery(hql);
		log.debug("HQL Query :"+query.getQueryString());
		return query;
	}
	
	private Query prepareQuery(StringBuffer hql)
	{
		query = session.createQuery(hql.toString());
		countQuery = session.createQuery("select count(*) " + hql.toString());

		for (int i=0;i<paramList.size();i++)
		{
			Object valueObj = paramList.get(i);
			if (valueObj instanceof String)
			{
				query.setString(i, (String)valueObj);
				countQuery.setString(i, (String)valueObj);
			}
			else if (valueObj instanceof Long)
			{
				query.setLong(i, ((Long)valueObj).longValue());
				countQuery.setLong(i, ((Long)valueObj).longValue());
			}
			else if (valueObj instanceof Date)
			{
				query.setDate(i,(Date)valueObj);
				countQuery.setDate(i,(Date)valueObj);
			}
			else if (valueObj instanceof Boolean)
			{
				query.setBoolean(i, ((Boolean)valueObj).booleanValue());
				countQuery.setBoolean(i, ((Boolean)valueObj).booleanValue());				
			}
			else if (valueObj instanceof Double)
			{
				query.setDouble(i, ((Double)valueObj).doubleValue());
				countQuery.setDouble(i, ((Double)valueObj).doubleValue());
			}
			else if (valueObj instanceof Float)
			{
				query.setFloat(i, ((Float)valueObj).floatValue());
				countQuery.setFloat(i, ((Float)valueObj).floatValue());
			}
			else if (valueObj instanceof Integer)
			{
				query.setInteger(i, ((Integer)valueObj).intValue());
				countQuery.setInteger(i, ((Integer)valueObj).intValue());
			}
			else
			{
				query.setString(i, valueObj.toString());
				countQuery.setString(i, valueObj.toString());
			}
		}
		return query;
	}

	private String getObjectAttributeCriterion(String sourceAlias, Object obj, Configuration cfg) throws Exception
	{
		StringBuffer whereClause = new StringBuffer();
		HashMap criterionMap = getObjAttrCriterion(obj, cfg);
		if (criterionMap != null)
		{
			Iterator keys = criterionMap.keySet().iterator();
			while (keys.hasNext())
			{
				String key = (String) keys.next();
				Object value = criterionMap.get(key);
				if (!key.equals("id") && (value instanceof String))
				{
					if (criteria.caseSensitivityFlag)
					{
						whereClause.append(sourceAlias + Constant.DOT + key + getOperator(value) + "? ");
						paramList.add(((String) value).replaceAll("\\*", "\\%"));
					} else
					{
						whereClause.append("lower(" + sourceAlias + Constant.DOT + key + ") " + getOperator(value) + "? ");
						paramList.add(((String) value).toLowerCase().replaceAll("\\*", "\\%"));
					}
				} else
				{
					whereClause.append(sourceAlias).append(Constant.DOT).append(key).append(getOperator(value)).append("? ");
					paramList.add(value);
				}
				if (keys.hasNext())
					whereClause.append(" and ");
			}
		}
		return whereClause.toString();
	}

	private HashMap getObjAttrCriterion(Object obj, Configuration cfg) throws Exception
	{
		HashMap criterions = new HashMap();
		String objClassName = obj.getClass().getName();
		PersistentClass pclass = cfg.getClassMapping(objClassName);
		setAttrCriterion(obj, pclass, criterions);

		while (pclass.getSuperclass() != null)
		{
			pclass = pclass.getSuperclass();
			setAttrCriterion(obj, pclass, criterions);
		}

		//String identifier = pclass.getIdentifierProperty().getName();
		//Field idField = pclass.getMappedClass().getDeclaredField(identifier);
		//idField.setAccessible(true);
		//if (idField.get(obj) != null)
			//criterions.put(identifier, idField.get(obj));

		return criterions;
	}

	private void setAttrCriterion(Object obj, PersistentClass pclass, HashMap criterions) throws Exception
	{
		Iterator properties = pclass.getPropertyIterator();
		while (properties.hasNext())
		{
			Property prop = (Property) properties.next();
			if (!prop.getType().isAssociationType())
			{
				Class objClass = obj.getClass();
				String fieldName = prop.getName();
				String getterName = "get" + capitalizeFirstLetter(fieldName);
				Method getterMethod = null;
				while (objClass != null) {
					try {
						log.debug("Checking class: " + objClass.getName() + " for method: " + getterName);
						getterMethod = objClass.getDeclaredMethod(getterName, null);
					} catch (NoSuchMethodException nsme) {
						// Move on and check if it's a method in the superclass.
					}
					if (getterMethod != null) {
						// Found the getter method.
						break;
					}
					objClass = objClass.getSuperclass();
				}
				if (getterMethod == null) {
					log.error("No such method: " + getterName);
					return;
				}

				Object valueOfAttribute = getterMethod.invoke(obj, null);
				if (valueOfAttribute != null)
				{
					if (prop.getType().getName().equals("gov.nih.nci.common.util.StringClobType"))
						criterions.put(fieldName, valueOfAttribute + "*");
					else
						criterions.put(fieldName, valueOfAttribute);
				}
			}
		}
	}

	private String getObjectCriterion(Object obj, Configuration cfg) throws Exception
	{
		String srcAlias = getAlias(obj.getClass().getName(),1);
		
		StringBuffer hql = new StringBuffer();

		HashMap associationCritMap = getObjAssocCriterion(obj, cfg);

		hql.append("select ");
		hql.append(srcAlias).append(".id ");
		hql.append("from ").append(obj.getClass().getName()).append(" ").append(srcAlias);

		// get association value
		if (associationCritMap != null && associationCritMap.size() > 0)
		{
			Iterator associationKeys = associationCritMap.keySet().iterator();
			int counter = 0;
			while (associationKeys.hasNext())
			{
				String roleName = (String) associationKeys.next();
				Object roleValue = associationCritMap.get(roleName);

				if (roleValue instanceof Collection)
				{
					Object[] objs = ((Collection) roleValue).toArray();
					for (int i = 0; i < objs.length; i++)
					{
						String alias = getAlias(objs[i].getClass().getName(),counter++);
						hql.append(",").append(objs[i].getClass().getName()).append(" ").append(alias);
					}
				} else
				{
					String alias = getAlias(roleValue.getClass().getName(),counter++);
					hql.append(",").append(roleValue.getClass().getName()).append(" ").append(alias);
				}
			}
			hql.append(" where ");
			associationKeys = associationCritMap.keySet().iterator();
			counter = 0;
			while (associationKeys.hasNext())
			{
				String roleName = (String) associationKeys.next();
				Object roleValue = associationCritMap.get(roleName);

				if (roleValue instanceof Collection)
				{
					Object[] objs = ((Collection) roleValue).toArray();
					for (int i = 0; i < objs.length; i++)
					{
						String alias = getAlias(objs[i].getClass().getName(),counter++);
						hql.append(alias).append(" in elements(").append(srcAlias).append(".").append(roleName).append(")");
						hql.append(" and ");
						hql.append(alias).append(".id in (").append(getObjectCriterion(objs[i], cfg)).append(") ");
						if (i < objs.length-1)
							hql.append(" and ");
					}
				} else
				{
					String alias = getAlias(roleValue.getClass().getName(),counter++);
					hql.append(alias).append("=").append(srcAlias).append(".").append(roleName);
					hql.append(" and ");
					hql.append(alias).append(".id in (").append(getObjectCriterion(roleValue, cfg)).append(") ");
				}
				hql.append(" ");
				if (associationKeys.hasNext())
					hql.append(" and ");
			}
		}
		String attributeCriteria = getObjectAttributeCriterion(srcAlias, obj, cfg);
		if (associationCritMap == null || associationCritMap.size() == 0 && attributeCriteria != null && attributeCriteria.trim().length() > 0)
			hql.append(" where ");
		if (associationCritMap != null && associationCritMap.size() > 0 && attributeCriteria != null && attributeCriteria.trim().length() > 0)
			hql.append(" ").append(" and ");
		hql.append(attributeCriteria);

		return hql.toString();
	}

	private void setAssoCriterion(Object obj, PersistentClass pclass, HashMap criterions) throws Exception
	{
		Iterator properties = pclass.getPropertyIterator();
		while (properties.hasNext())
		{
			Property prop = (Property) properties.next();
			if (prop.getType().isAssociationType())
			{
				Class objClass = obj.getClass();
				String fieldName = prop.getName();
				String getterName = "get" + capitalizeFirstLetter(fieldName);
				Method getterMethod = null;
				while (objClass != null) {
					try {
						log.debug("Checking class: " + objClass.getName() + " for method: " + getterName);
						getterMethod = objClass.getDeclaredMethod(getterName, null);
					} catch (NoSuchMethodException nsme) {
						// Move on and check if it's a method in the superclass.
					}
					if (getterMethod != null) {
						// Found the getter method.
						break;
					}
					objClass = objClass.getSuperclass();
				}
				if (getterMethod == null) {
					log.error("No such method: " + getterName);
					return;
				}

				Object valueOfAssociation = getterMethod.invoke(obj, null);
				if (valueOfAssociation != null)
				{
					if ((valueOfAssociation instanceof Collection && ((Collection) valueOfAssociation).size() > 0) ||
							!(valueOfAssociation instanceof Collection))
						criterions.put(fieldName, valueOfAssociation);
				}
			}
		}
	}

	private HashMap getObjAssocCriterion(Object obj, Configuration cfg) throws Exception
	{
		HashMap criterions = new HashMap();
		List propertyList = new ArrayList();
		String objClassName = obj.getClass().getName();

		PersistentClass pclass = cfg.getClassMapping(objClassName);

		setAssoCriterion(obj, pclass, criterions);
		while (pclass.isJoinedSubclass())
		{
			pclass = pclass.getSuperclass();
			setAssoCriterion(obj, pclass, criterions);
		}
		return criterions;
	}

	private String getAlias(String sourceName, int count)
	{
		String alias = sourceName.substring(sourceName.lastIndexOf(Constant.DOT) + 1);
		alias = alias.substring(0, 1).toLowerCase() + alias.substring(1);
		return alias+"_"+count;
	}

	private String getOperator(Object valueObj)
	{
		if (valueObj instanceof java.lang.String)
		{
			String value = (String) valueObj;
			if (value.indexOf('*') >= 0)
			{
				return " like ";
			}
		}
		return "=";
	}

	public Query getCountQuery()
	{
		return countQuery;
	}

	private String capitalizeFirstLetter(String inputString)
	{
		String firstLetter = inputString.substring(0, 1);
		return (firstLetter.toUpperCase() + inputString.substring(1));
	}
}
