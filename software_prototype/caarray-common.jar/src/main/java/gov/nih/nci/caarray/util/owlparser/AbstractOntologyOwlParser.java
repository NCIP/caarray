package gov.nih.nci.caarray.util.owlparser;

import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;

/**
 * Base class for parsing an ontology description in OWL format. Generates calls to event methods
 * which subclasses should implement to actually do appropriate processing of the parsed data.
 * @author dkokotov
 */
public abstract class AbstractOntologyOwlParser {
    private static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
    private static final String OWL_NAMESPACE = "http://www.w3.org/2002/07/owl#";
    private static final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String RDFS_NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#";
    private static final String MGED_NAMESPACE = "http://mged.sourceforge.net/ontologies/MGEDOntology.owl#";

    private static final String ONTOLOGY_ELEMENT = "Ontology";
    private static final String CLASS_ELEMENT = "Class";
    private static final String SUBCLASS_ELEMENT = "subClassOf";
    private static final String VERSION_INFO_ELEMENT = "versionInfo";
    private static final String UNIQUE_IDENTIFIER_ELEMENT = "unique_identifier";
    private static final String TYPE_ELEMENT = "type";
    
    private static final String ID_ATTRIBUTE = "ID";
    private static final String ABOUT_ATTRIBUTE = "about";    
    private static final String BASE_ATTRIBUTE = "base";
    private static final String RESOURCE_ATTRIBUTE = "resource";
    
    private static final String FRAGMENT_SEPARATOR = "#";

    private final String name;
    private String url;
    private final DocumentFactory df = DocumentFactory.getInstance();
    private TermSource termSource;
    private final Map<String, Category> categoryCache = new HashMap<String, Category>();
    private final Set<Term> termCache = new HashSet<Term>();


    /**
     * Create an ontology parser that will parse an ontology OWL file for a term source with given name and url.
     * If name and/or url are null, they will be taken from the OWL file, otheriwse the given values
     * will be used.
     * @param name an explicit name for the term source whose ontology is being parsed
     * @param url an explicit url for the term source whose ontology is being parsed
     */
    public AbstractOntologyOwlParser(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * Parse an OWL document from given InputStream.
     * @param owlStream InputStream containing the bytes of the OWL document
     * @throws ParseException on error
     */
    public void parse(InputStream owlStream) throws ParseException {
        SAXReader reader = new SAXReader();
        try {
            parse(reader.read(owlStream));
        } catch (DocumentException e) {
            throw new ParseException(e);
        }
    }

    /**
     * Parse an OWL document from given Document.
     * @param owlDoc The OWL document
     * @throws ParseException on error
     */
    public void parse(Document owlDoc) throws ParseException {
        handle(owlDoc.getRootElement());
    }

    @SuppressWarnings("unchecked")
    private void handle(Element rootElement) throws ParseException {
        try {
            startProcessing();
            if (url == null) {                
                url = rootElement.attributeValue(getBaseAttributeName());
            }
            handleOntologyDescription(rootElement.element(getOntologyElementName()));
            List<Element> classElements = rootElement.elements(getClassElementName());
            // two passes: one to create categories, one to resolve subclassing references
            handleClasses(classElements.iterator());
            handleSubclasses(classElements.iterator());
            List<Element> allElements = rootElement.elements();
            Iterator<Element> individualElements = IteratorUtils.filteredIterator(allElements.iterator(),
                    IndividualElementPredicate.INSTANCE);
            handleIndividuals(individualElements);            
        } finally {
            finishProcessing();            
        }
    }
    
    private QName getOntologyElementName() {
        return df.createQName(ONTOLOGY_ELEMENT, OWL_NAMESPACE);        
    }

    private QName getClassElementName() {
        return df.createQName(CLASS_ELEMENT, OWL_NAMESPACE);        
    }
    
    private QName getSubclassElementName() {
        return df.createQName(SUBCLASS_ELEMENT, RDFS_NAMESPACE);        
    }

    private QName getVersionInfoElementName() {
        return df.createQName(VERSION_INFO_ELEMENT, OWL_NAMESPACE);        
    }

    private QName getTypeElementName() {
        return df.createQName(TYPE_ELEMENT, RDF_NAMESPACE);        
    }

    private QName getUniqueIdentifierElementName() {
        return df.createQName(UNIQUE_IDENTIFIER_ELEMENT, MGED_NAMESPACE);        
    }

    private QName getIdAttributeName() {
        return df.createQName(ID_ATTRIBUTE, RDF_NAMESPACE);        
    }

    private QName getAboutAttributeName() {
        return df.createQName(ABOUT_ATTRIBUTE, RDF_NAMESPACE);        
    }

    private QName getBaseAttributeName() {
        return df.createQName(BASE_ATTRIBUTE, XML_NAMESPACE);        
    }

    private QName getResourceAttributeName() {
        return df.createQName(RESOURCE_ATTRIBUTE, RDF_NAMESPACE);        
    }

    private String getIdentifier(Element e) {
        Attribute id = e.attribute(getIdAttributeName());
        if (id != null) {
            return id.getValue();
        } 
        Attribute about = e.attribute(getAboutAttributeName());
        if (about != null) {
            return StringUtils.substringAfter(about.getValue(), FRAGMENT_SEPARATOR);
        }
        Attribute resource = e.attribute(getResourceAttributeName());
        if (resource != null) {
            return StringUtils.substringAfter(resource.getValue(), FRAGMENT_SEPARATOR);
        }
        return null;        
    }
    
    private String getUniqueIdentifier(Element e) {
        Element uniqueId = e.element(getUniqueIdentifierElementName());
        if (uniqueId != null) {
            return StringUtils.trimToNull(uniqueId.getText());
        }
        return null;
    }
    
    private String makeUrl(String fragment) {
        return new StringBuilder(url).append(FRAGMENT_SEPARATOR).append(fragment).toString();
    }

    @SuppressWarnings("unchecked")
    private void handleIndividuals(Iterator<Element> individualElements) throws ParseException {
        while (individualElements.hasNext()) {
            Element e = individualElements.next();
            Term t = new Term();
            t.setValue(getIdentifier(e));
            t.setAccession(getUniqueIdentifier(e));
            t.setUrl(makeUrl(t.getValue()));
            t.setCategory(categoryCache.get(e.getQName().getName()));
            t.setSource(termSource);
            handleAdditionalTypes(t, e.elements(getTypeElementName()).iterator());
            termCache.add(t);
            processTerm(t);
        }        
    }

    private void handleAdditionalTypes(Term t, Iterator<Element> typeElements) throws ParseException {
        while (typeElements.hasNext()) {
            Element e = typeElements.next();
            Category c = categoryCache.get(getIdentifier(e));
            t.getCategories().add(c);
        }        
    }

    @SuppressWarnings("unchecked")
    private void handleSubclasses(Iterator<Element> classElements) throws ParseException {
        while (classElements.hasNext()) {
            Element e = classElements.next();
            Category c = categoryCache.get(getIdentifier(e));
            handleSubclassReferences(c, e.elements(getSubclassElementName()).iterator());
        }
    }

    private void handleSubclassReferences(Category c, Iterator<Element> subclassElements) throws ParseException {
        while (subclassElements.hasNext()) {
            Element e = subclassElements.next();
            // subClassOf elements will either reference the subclass directly with rdf:resource attr
            // or as a nested rdf:Class element. They can also be used to identify constraints
            // and not subclass relationships. so we need to handle both of the first two cases and skip the third
            Element nestedClassElt = e.element(getClassElementName());
            String superClassName = getIdentifier(nestedClassElt != null ? nestedClassElt : e);
            if (superClassName == null) {
                continue;
            }
            Category parent = categoryCache.get(superClassName);
            // ontology may refer to parents outside of the ontology itself - ignore those
            if (parent == null) {
                continue;
            }
            c.getParents().add(parent);
            parent.getChildren().add(c);
            processCategorySubclass(c, parent);
        }
    }

    private void handleClasses(Iterator<Element> classElements) throws ParseException {
        while (classElements.hasNext()) {
            Element e = classElements.next();
            Category c = new Category();
            c.setName(getIdentifier(e));
            c.setAccession(getUniqueIdentifier(e));
            c.setUrl(makeUrl(c.getName()));
            categoryCache.put(c.getName(), c);
            processCategory(c);
        }
    }

    private void handleOntologyDescription(Element ontologyElement) throws ParseException {
        termSource = new TermSource();
        termSource.setName(name);
        termSource.setUrl(url);
        Element versionElement = ontologyElement.element(getVersionInfoElementName());
        if (versionElement != null) {
            termSource.setVersion(StringUtils.trimToNull(versionElement.getText()));
        }
        processTermSource(termSource);
    }    

    /**
     * @return the termSource
     */
    public TermSource getTermSource() {
        return termSource;
    }

    /**
     * @return the categoryCache
     */
    public Map<String, Category> getCategoryCache() {
        return categoryCache;
    }

    /**
     * @return the termCache
     */
    public Set<Term> getTermCache() {
        return termCache;
    }


    /**
     * Called to indicate the start of processing the OWL doc.
     * @throws ParseException on error
     */
    protected abstract void startProcessing() throws ParseException;
    
    /**
     * Called to indicate the end of processing the OWL doc. Subclasses should release any resources
     * acquired during processing. This method will be called even if an error had occurred during
     * the parsing process.
     * @throws ParseException on error
     */
    protected abstract void finishProcessing() throws ParseException;
    
    /**
     * Process the term source parsed from the OWL. This term source corresponds to the ontology being
     * parsed.
     * @param ts the parsed term source
     * @throws ParseException on error
     */
    protected abstract void processTermSource(TermSource ts) throws ParseException;
    
    /**
     * Process acategory parsed from the OWL. The category child-parent linkages have not.
     * yet been established at this point
     * @param category the parsed category
     * @throws ParseException on error
     */
    protected abstract void processCategory(Category category) throws ParseException;
 
    /**
     * Process a child-parent relationships between the two given categories.
     * @param category the child category 
     * @param parentCategory the parent category
     * @throws ParseException on error
     */
    protected abstract void processCategorySubclass(Category category, Category parentCategory) throws ParseException;

    /**
     * Process a term parsed from the OWL. The term includes linkages to all categories it belongs to.
     * @param t the parsed term.
     * @throws ParseException on error
     */
    protected abstract void processTerm(Term t) throws ParseException;

    /**
     * Predicate for elements which correspond to individuals in the ontology.
     */
    private static final class IndividualElementPredicate implements Predicate {
        public static final IndividualElementPredicate INSTANCE = new IndividualElementPredicate();
        
        private IndividualElementPredicate() {
            // intentionally empty
        }
        
        public boolean evaluate(Object o) {
            Element e = (Element) o;
            return MGED_NAMESPACE.equals(e.getNamespaceURI());
        }
    }
}
