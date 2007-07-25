package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.data.file.TabDelimitedFile;
import gov.nih.nci.caarray.magetab.MageTabTextFileLoaderException;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Bill Mason
 * 
 */
@SuppressWarnings("PMD")
// has a Cyclomatic Complexity of 4
public final class SdrfFileParser {

    private static final Log LOG = LogFactory.getLog(SdrfFileParser.class);

    private TabDelimitedFile fileUtil;
    private List<String> currentLineContents;
    private List<SdrfColumn> headerList;
    private AbstractMap<String, AbstractSdrfEntry> dataList;
    private List<AbstractSdrfEntry> row = null;
    private List<AbstractSdrfEntry> rows;
    private AbstractSdrfEntry nodeElement = null;
    private AbstractSdrfEntry termElement = null;
    private AbstractSdrfEntry theRow = null;
    private boolean linked = false;

    /**
     * 
     * @param document document to parse
     * @throws MageTabTextFileLoaderException exception
     */
    public void parseSdrfDocument(SdrfDocument document) throws MageTabTextFileLoaderException {
        fileUtil = document.getFileUtil();
        rows = new ArrayList<AbstractSdrfEntry>();
        handleData();
        document.setHeaders(headerList);
        document.setRows(rows);
    }

    private void handleData() throws MageTabTextFileLoaderException {
        try {
            getHeader();
            getValues();
            rows.add((AbstractSdrfEntry) row.get(0));
        } catch (InstantiationException e) {
            LOG.error("Unexpected exception in handleData()", e);
        } catch (IllegalAccessException e) {
            LOG.error("Unexpected exception in handleData()", e);
        }

    }

    private void getHeader() throws MageTabTextFileLoaderException {
        headerList = new ArrayList<SdrfColumn>();
        currentLineContents = fileUtil.readLine();
        if ((currentLineContents) != null) {
            Iterator iter = currentLineContents.iterator();
            if (iter.hasNext()) {
                while (iter.hasNext()) {
                    String value = (String) iter.next();
                    SdrfColumn column = new SdrfColumn(value);
                    // Exception happens if no column match with Enum class
                    headerList.add(column);
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
            if (row != null && !linked) {
                rows.add((AbstractSdrfEntry) row.get(0));
            }
            nodeElement = null;
            termElement = null;
            linked = false;
            row = new ArrayList<AbstractSdrfEntry>();

            Iterator<String> iter = currentLineContents.iterator();
            columnPosition = -1;
            key = null;
            currentObject = null;
            if (iter.hasNext()) {
                while (iter.hasNext()) {
                    String value = iter.next();
                    try {
                        key = headerList.get(++columnPosition).getTheClass().getName() + "_" + value;
                        currentObject = headerList.get(columnPosition).getTheClass();
                    } catch (Exception ex) {
                        // Continuing for now since we are only doing the values before Hybridization
                        LOG.debug("Did not find : \n column name - " + headerList.get(columnPosition).getHeader()
                                + " \n columnPosition = " + columnPosition);
                        continue;
                    }
                    // see if the object can be reused
                    if (dataList.containsKey(key)) {
                        if (dataList.get(key) instanceof AbstractNode) {
                            linked = true;
                        }
                        LOG.debug("found " + key);
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
                if (currentElement instanceof Label) {
                    termElement = currentElement;

                }
                currentElement.link(nodeElement);
                termElement = currentElement;
            } else if (currentElement instanceof AbstractAttribute) {
                currentElement.link(termElement);
            } else {
                LOG.debug("Found something that does not fit " + currentElement.getClass().getName());
            }
        }
        if (currentElement instanceof AbstractNode) {
            row.add(currentElement);
            nodeElement = currentElement;
        }

    }

    /**
     * 
     * @return SdrfFileParser the parser
     */
    public static SdrfFileParser create() {
        return new SdrfFileParser();
    }

}
