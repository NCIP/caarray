package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.data.file.TabDelimitedFile;
import gov.nih.nci.caarray.magetab.MageTabTextFileLoaderException;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Bill Mason
 * 
 */
@SuppressWarnings("PMD") // has a Cyclomatic Complexity of 4 
public final class SdrfFileParser {

    private static final Log LOG = LogFactory.getLog(SdrfFileParser.class);

    private TabDelimitedFile fileUtil;
    private List<String> currentLineContents;
    private AbstractMap<Integer, SdrfColumn> headerList;
    private AbstractMap<String, AbstractSdrfEntry> dataList;
    private AbstractMap<String, AbstractSdrfEntry> row = null;
    private List<LinkedHashMap<String, AbstractSdrfEntry>> sdrfDocument;
    private AbstractSdrfEntry nodeElement = null;
    private AbstractSdrfEntry termElement = null;
    private boolean linked = false;

    // private AbstractSdrfEntry attributeElement = null;

    /**
     * 
     * @param document document to parse
     * @return SdrfDocument
     * @throws MageTabTextFileLoaderException exception
     */
    public SdrfDocument parseSdrfDocument(SdrfDocument document) throws MageTabTextFileLoaderException {
        fileUtil = document.getFileUtil();
        sdrfDocument = new ArrayList<LinkedHashMap<String, AbstractSdrfEntry>>();
        handleData();
        return null;
    }

    private void handleData() throws MageTabTextFileLoaderException {
        try {
            getHeader();
            getValues();
            if (!linked) {
                sdrfDocument.add((LinkedHashMap<String, AbstractSdrfEntry>) row);
            }
        } catch (InstantiationException e) {
            LOG.error("Unexpected exception in handleData()", e);
        } catch (IllegalAccessException e) {
            LOG.error("Unexpected exception in handleData()", e);
        }

    }

    private void getHeader() throws MageTabTextFileLoaderException {
        headerList = new LinkedHashMap<Integer, SdrfColumn>();
        currentLineContents = fileUtil.readLine();
        if ((currentLineContents) != null) {
            Iterator iter = currentLineContents.iterator();
            int i = 0;
            if (iter.hasNext()) {
                while (iter.hasNext()) {
                    String value = (String) iter.next();
                    SdrfColumn column = new SdrfColumn(value);
                    // Exception happens if no column match with Enum class
                    headerList.put(i++, column);
                }
            }
        }
    }


    private void getValues() throws MageTabTextFileLoaderException, InstantiationException, IllegalAccessException {
        dataList = new LinkedHashMap<String, AbstractSdrfEntry>();
        Class<? extends AbstractSdrfEntry> currentObject;
        String key;
        int columnPosition;
        while ((currentLineContents = fileUtil.readLine()) != null) {
            if (row != null) {
                if (!linked) {
                    sdrfDocument.add((LinkedHashMap<String, AbstractSdrfEntry>) row);
                    nodeElement = null;
                    termElement = null;
                }
            }
            linked = false;
            row = new LinkedHashMap<String, AbstractSdrfEntry>();

            Iterator<String> iter = currentLineContents.iterator();
            columnPosition = -1;
            key = null;
            currentObject = null;
            if (iter.hasNext()) {
                while (iter.hasNext()) {
                    String value = iter.next();
                    try {
                        key = getObjectKey(headerList.get(++columnPosition).getTheClass(), value);
                        currentObject = headerList.get(columnPosition).getTheClass();
                    } catch (Exception ex) {
                        // Continuing for now since we are only doing the values before Hybridization
                        LOG.debug("Did not find : \n column name - " + headerList.get(columnPosition).getHeader()
                                + " \n columnPosition = " + columnPosition);
                    }
                    // check if the object is already created
                    LinkedHashMap<String, AbstractSdrfEntry> hasObject = hasObject(key);

                    if (hasObject != null) {
                        System.out.println("found a node " + key);
                        linked = true;
                        link(hasObject.get(key));
                    } else if (dataList.containsKey(key)) {
                        link(dataList.get(key));
                    } else {
                        // add to list
                        if (currentObject != null) {
                            AbstractSdrfEntry newInstance = currentObject.newInstance();
                            newInstance.setValue(value);
                            newInstance.setColumn(headerList.get(columnPosition));
                            dataList.put(key, newInstance);
                            link(newInstance);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param object
     */

    private void link(AbstractSdrfEntry currentElement) {

        if (nodeElement != null) {
            if (currentElement instanceof AbstractNode) {
                // link node to node
                currentElement.link(nodeElement);
                nodeElement = currentElement;
            } else if (currentElement instanceof AbstractTerm) {
                currentElement.link(nodeElement);
                termElement = currentElement;
            } else if (currentElement instanceof AbstractAttribute) {
                currentElement.link(termElement);
            } else {
                LOG.debug("Found something that does not fit " + currentElement.getClass().getName());
            }
        }
        if (currentElement instanceof AbstractNode) {
            row.put(getObjectKey(currentElement), currentElement);
            nodeElement = currentElement;
        }

    }

    /**
     * 
     */
    private String getObjectKey(Class<? extends AbstractSdrfEntry> c, String value) {
        return c.getName() + "_" + value;
    }

    private String getObjectKey(AbstractSdrfEntry element) {

        return element.getClass().getName() + "_" + element.getValue();
    }

    private LinkedHashMap<String, AbstractSdrfEntry> hasObject(String key) {

        ListIterator<LinkedHashMap<String, AbstractSdrfEntry>> itr = sdrfDocument.listIterator();
        while (itr.hasNext()) {
            LinkedHashMap<String, AbstractSdrfEntry> element = itr.next();
            if (element.containsKey(key)) {
                return element;
            }
        }

        return null;
    }

    /**
     * 
     * @return SdrfFileParser the parser
     */
    public static SdrfFileParser create() {
        return new SdrfFileParser();
    }


 }
