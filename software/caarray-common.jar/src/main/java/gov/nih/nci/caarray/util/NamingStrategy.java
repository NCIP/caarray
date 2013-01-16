//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

import java.util.Locale;

import org.hibernate.cfg.ImprovedNamingStrategy;

/**
 * CaArray Naming Strategy.
 */
public class NamingStrategy extends ImprovedNamingStrategy {

    private static final long serialVersionUID = -2556633166307295370L;

    /**
     * {@inheritDoc}
     */
    @Override
    public String classToTableName(String className) {
        String superStr = super.classToTableName(className);
        return superStr.toLowerCase(Locale.US);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String collectionTableName(String ownerEntity, String ownerEntityTable, String associatedEntity,
            String associatedEntityTable, String propertyName) {
        return super.collectionTableName(ownerEntity, ownerEntityTable, associatedEntity,
                                         associatedEntityTable, propertyName).toLowerCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String columnName(String columnName) {
        String superStr = super.columnName(columnName);
        return superStr.toLowerCase(Locale.US);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName,
            String referencedColumnName) {
        return super.foreignKeyColumnName(propertyName, propertyEntityName, propertyTableName,
                                          referencedColumnName).toLowerCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String joinKeyColumnName(String joinedColumn, String joinedTable) {
        return super.joinKeyColumnName(joinedColumn, joinedTable).toLowerCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
        return super.logicalCollectionColumnName(columnName, propertyName, referencedColumn).toLowerCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String logicalCollectionTableName(String tableName, String ownerEntityTable, String associatedEntityTable,
            String propertyName) {
        return super.logicalCollectionTableName(tableName, ownerEntityTable, associatedEntityTable,
                                                propertyName).toLowerCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String logicalColumnName(String columnName, String propertyName) {
        return super.logicalColumnName(columnName, propertyName).toLowerCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String propertyToColumnName(String propertyName) {
        String superStr =  super.propertyToColumnName(propertyName);
        return superStr.toLowerCase(Locale.US);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String tableName(String tableName) {
        String superStr =  super.tableName(tableName);
        return superStr.toLowerCase(Locale.US);
    }
}
