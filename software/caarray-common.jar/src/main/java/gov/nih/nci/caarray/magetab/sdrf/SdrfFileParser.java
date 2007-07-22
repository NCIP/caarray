package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.data.file.TabDelimitedFile;
import gov.nih.nci.caarray.magetab.MageTabTextFileLoaderException;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Bill Mason
 * 
 */
@SuppressWarnings("PMD")
public final class SdrfFileParser {
    
    private static final Log LOG = LogFactory.getLog(SdrfFileParser.class);
    
    private TabDelimitedFile fileUtil;
    private List<String> currentLineContents;
    private AbstractMap<Integer, SdrfColumn> headerList;
    private AbstractMap<String, AbstractSdrfEntry> dataList;
    private final List<AbstractSdrfEntry> documentList = new LinkedList<AbstractSdrfEntry>();
    private AbstractSdrfEntry nodeElement = null;
    private AbstractSdrfEntry termElement = null;
//    private AbstractSdrfEntry attributeElement = null;


    /**
     * 
     * @param document document to parse
     * @return SdrfDocument
     * @throws MageTabTextFileLoaderException exception
     */
    public SdrfDocument parseSdrfDocument(SdrfDocument document) throws MageTabTextFileLoaderException {
        fileUtil = document.getFileUtil();
        handleData();
        return null;
    }

    private void handleData() throws MageTabTextFileLoaderException {
        try {
            getHeader();
            getValues();
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
    
    @SuppressWarnings("PMD")
    private void getValues() throws MageTabTextFileLoaderException, InstantiationException, IllegalAccessException {
        dataList = new LinkedHashMap<String, AbstractSdrfEntry>();
        while ((currentLineContents = fileUtil.readLine()) != null) {
            Iterator<String> iter = currentLineContents.iterator();
            int columnPosition = -1;
            String key = null;
            Class<? extends AbstractSdrfEntry> object = null;
            if (iter.hasNext()) {
                while (iter.hasNext()) {
                    String value = iter.next();
                    // Class<? extends AbstractSdrfEntry> object = getObject(columnPosition++, value);
                    try {
                        object = headerList.get(++columnPosition).getTheClass();
                        key = object.getName() + "_" + value;
                    } catch (Exception ex) {
                        // Continuing for now since we are only doing the values before Hybridization
                        System.out
                                .println("Did not find : \n column name - "
                                        + headerList.get(columnPosition).getHeader() + " \n columnPosition = "
                                        + columnPosition);
                    }
                    // check if the object is already created
                    if (dataList.containsKey(key)) {
                        link(dataList.get(key));
                    } else {
                        // add to list
                        if (object != null) {
                            AbstractSdrfEntry newInstance = object.newInstance();
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
    @SuppressWarnings("PMD")
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
                //attributeElement = currentElement;
            } else {
                System.out.println("Found something that does not fit " + currentElement.getClass().getName());
            }
        }
        if (currentElement instanceof AbstractNode) {
            documentList.add(currentElement);
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
