//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.owlparser;

import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * OWL Parser that writes out sql statements createing the term source, categories and terms from the
 * OWL data.
 * @author dkokotov
 */
public class SqlOntologyOwlParser extends AbstractOntologyOwlParser {
    private static final String UNCHECKED = "unchecked";

    private static final String CATEGORY_TABLE = "category";
    private static final String CATEGORY_PARENTS_TABLE = "category_parents";
    private static final String TERM_SOURCE_TABLE = "term_source";
    private static final String TERM_TABLE = "term";
    private static final String TERM_CATEGORIES_TABLE = "term_categories";

    private static final String ID_COLUMN = "id";
    private static final String NAME_COLUMN = "name";
    private static final String VALUE_COLUMN = "value";
    private static final String URL_COLUMN = "url";
    private static final String ACCESSION_COLUMN = "accession";
    private static final String VERSION_COLUMN = "version";
    private static final String TERM_SOURCE_REF_COLUMN = "source";
    private static final String CATEGORY_REF_COLUMN = "category_id";
    private static final String PARENT_CATEGORY_REF_COLUMN = "parent_category_id";
    private static final String TERM_REF_COLUMN = "term_id";

    private final File sqlFile;
    private Writer sqlFileWriter;

    /**
     * Create a new parser that will write out to the given file.
     * @param name an explicit name for the term source whose ontology is being parsed
     * @param url an explicit url for the term source whose ontology is being parsed
     * @param sqlFile file to write out to
     */
    public SqlOntologyOwlParser(String name, String url, File sqlFile) {
        super(name, url);
        this.sqlFile = sqlFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void startProcessing() throws ParseException {
        try {
            sqlFileWriter = new FileWriter(sqlFile);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void finishProcessing() {
        if (sqlFileWriter != null) {
            try {
                sqlFileWriter.close();
            } catch (IOException e) { // NOPMD
                // nothing to do
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings(UNCHECKED)
    protected void processCategory(Category category) throws ParseException {
        writeSqlLine(CATEGORY_TABLE, new String[] {TERM_SOURCE_TABLE }, new String[] {NAME_COLUMN, URL_COLUMN,
                ACCESSION_COLUMN, TERM_SOURCE_REF_COLUMN }, new String[] {sqlString(category.getName()),
                sqlString(category.getUrl()), sqlString(category.getAccession()), ID_COLUMN }, ArrayUtils
                .toMap(new String[][] {{"name", sqlString(getTermSource().getName()) } }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings(UNCHECKED)
    protected void processCategorySubclass(Category category, Category parentCategory) throws ParseException {
        writeSqlLine(CATEGORY_PARENTS_TABLE, new String[] {CATEGORY_TABLE + " c1", CATEGORY_TABLE + " c2" },
                new String[] {CATEGORY_REF_COLUMN, PARENT_CATEGORY_REF_COLUMN }, new String[] {"c1." + ID_COLUMN,
                        "c2." + ID_COLUMN }, ArrayUtils.toMap(new String[][] {
                        {"c1." + NAME_COLUMN, sqlString(category.getName()) },
                        {"c2." + NAME_COLUMN, sqlString(parentCategory.getName()) } }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings(UNCHECKED)
    protected void processTerm(Term t) throws ParseException {
        writeSqlLine(TERM_TABLE, new String[] {TERM_SOURCE_TABLE }, new String[] {VALUE_COLUMN, URL_COLUMN,
                ACCESSION_COLUMN, TERM_SOURCE_REF_COLUMN }, new String[] {sqlString(t.getValue()),
                sqlString(t.getUrl()), sqlString(t.getAccession()), ID_COLUMN }, ArrayUtils.toMap(new String[][] {{
                "name", sqlString(getTermSource().getName()) } }));
        for (Category c : t.getCategories()) {
            writeSqlLine(TERM_CATEGORIES_TABLE, new String[] {TERM_TABLE + " t", CATEGORY_TABLE + " c" },
                    new String[] {TERM_REF_COLUMN, CATEGORY_REF_COLUMN }, new String[] {"t." + ID_COLUMN,
                            "c." + ID_COLUMN }, ArrayUtils.toMap(new String[][] {
                            {"t." + VALUE_COLUMN, sqlString(t.getValue()) },
                            {"c." + NAME_COLUMN, sqlString(c.getName()) } }));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processTermSource(TermSource ts) throws ParseException {
        writeSqlLine(TERM_SOURCE_TABLE, new String[] {NAME_COLUMN, URL_COLUMN, VERSION_COLUMN }, new String[] {
                sqlString(ts.getName()), sqlString(ts.getUrl()), sqlString(ts.getVersion()) });
    }

    @SuppressWarnings(UNCHECKED)
    private void writeSqlLine(String toTable, String[] fromTables, String[] columns,
            String[] selectEntries, Map<String, String> whereEntries) throws ParseException {
        StringBuilder sqlLine = new StringBuilder("insert into ").append(toTable);
        sqlLine.append(" (").append(StringUtils.join(columns, ",")).append(")");
        sqlLine.append(" select ").append(StringUtils.join(selectEntries, ", "));
        sqlLine.append(" from ").append(StringUtils.join(fromTables, ", "));
        sqlLine.append(" where ");
        sqlLine.append(StringUtils.join(CollectionUtils.transformedCollection(whereEntries.entrySet(),
                new Transformer() {
                    public Object transform(Object o) {
                        Map.Entry<String, String> whereEntry = (Map.Entry<String, String>) o;
                        return new StringBuilder(whereEntry.getKey()).append(" = ").append(whereEntry.getValue())
                                .toString();
                    }
                }), " and "));
        sqlLine.append(";\n");
        try {
            sqlFileWriter.write(sqlLine.toString());
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    private void writeSqlLine(String table, String[] columns, String[] values) throws ParseException {
        StringBuilder sqlLine = new StringBuilder("insert into ").append(table);
        sqlLine.append(" (").append(StringUtils.join(columns, ",")).append(")");
        sqlLine.append(" values (").append(StringUtils.join(values, ", ")).append(");\n");
        try {
            sqlFileWriter.write(sqlLine.toString());
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    private String sqlString(String value) {
        if (value == null) {
            return "null";
        }
        return "'" + StringEscapeUtils.escapeSql(value) + "'";
    }
}
