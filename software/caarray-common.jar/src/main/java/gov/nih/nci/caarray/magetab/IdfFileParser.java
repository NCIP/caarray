/**
 *  The caArray Software License, Version 1.0
 *
 *  Copyright 2004 5AM Solutions. This software was developed in conjunction
 *  with the National Cancer Institute, and so to the extent government
 *  employees are co-authors, any rights in such works shall be subject to
 *  Title 17 of the United States Code, section 105.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the disclaimer of Article 3, below.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  2. Affymetrix Pure Java run time library needs to be downloaded from
 *  (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 *  after agreeing to the licensing terms from the Affymetrix.
 *
 *  3. The end-user documentation included with the redistribution, if any,
 *  must include the following acknowledgment:
 *
 *  "This product includes software developed by 5AM Solutions and the National
 *  Cancer Institute (NCI).
 *
 *  If no such end-user documentation is to be included, this acknowledgment
 *  shall appear in the software itself, wherever such third-party
 *  acknowledgments normally appear.
 *
 *  4. The names "The National Cancer Institute", "NCI", and "5AM Solutions"
 *  must not be used to endorse or promote products derived from this software.
 *
 *  5. This license does not authorize the incorporation of this software into
 *  any proprietary programs. This license does not authorize the recipient to
 *  use any trademarks owned by either NCI or 5AM.
 *
 *  6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 *  EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * Copyright 2007 NCICB. This software was developed in conjunction with the National
 * Cancer Institute, and so to the extent government employees are co-authors, any
 * rights in such works shall be subject to Title 17 of the United States Code,
 * section 105.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the disclaimer of Article 3, below. Redistributions in
 * binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 2. Affymetrix Pure Java run time library needs to be downloaded from
 * (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 * after agreeing to the licensing terms from the Affymetrix.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment:
 *
 * "This product includes software developed by 5AM Solutions (5AM)
 * and the National Cancer Institute (NCI)."
 *
 * If no such end-user documentation is to be included, this acknowledgment shall
 * appear in the software itself, wherever such third-party acknowledgments
 * normally appear.
 *
 * 4. The names "The National Cancer Institute", "NCI", and "5AM" must not be used to
 * endorse or promote products derived from this software.
 *
 * 5. This license does not authorize the incorporation of this software into any
 * proprietary programs. This license does not authorize the recipient to use any
 * trademarks owned by either NCI or 5AM.
 *
 * 6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL
 * CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.data.file.LineSplitter;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import org.apache.commons.collections.map.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;


/**
 * Parses a MAGE-TAB IDF file into a MultiValueMap.
 *
 * @author John Pike
 */
@SuppressWarnings("PMD")
public final class IdfFileParser {

    private final File mageTabFile;
    private MultiValueMap entityMap;
    private IdfDocument document;
    private LineIterator lineIterator;
    private String currentLine;
    private List<String> currentLineContents;

    private IdfFileParser(File argMageTabFile) {
        super();
        if (argMageTabFile == null) {
            throw new IllegalArgumentException("Null argument for mageTabFile.");
        }
        mageTabFile = argMageTabFile;
    }

    /**
     * @param argMageTabFile the file
     * @return MageTabTextFileLoader
     */
    public static IdfFileParser create(File argMageTabFile) {
        return new IdfFileParser(argMageTabFile);
    }

    /**
     * Creates the entity map from an IDF text file.
     *
     * @throws MageTabTextFileLoaderException exception
     */
    private void createEntityMap() throws MageTabTextFileLoaderException {
        if (entityMap == null) {
            loadEntityMap();
        }
    }

    /**
     * @param index row value
     * @return List of Strings
     */
    public List<String> getRow(int index) {
        List<ArrayList<String>> values = (ArrayList<ArrayList<String>>) entityMap.values();
        return values.get(index);
    }

    /**
     * @param rowHeading key
     * @return List of strings
     */
    public List getRow(String rowHeading) {
        return (ArrayList) entityMap.get(rowHeading);
    }


    /**
     * @return IdfDocument  the Document
     * @throws MageTabTextFileLoaderException exception
     */
    public IdfDocument getIdfDocument() throws MageTabTextFileLoaderException {

        if (document == null) {
            createEntityMap();

            document = new IdfDocument();
            getTitle();
            getExperimentDate();
            getExperimentDescription();
            getPublicReleaseDate();
            getSdrfFiles();
            addTermSources();
            addProtocols();
            addPersons();
            addPublications();
            addExperimentalFactors();
           //   addComments(document, entity);entityMap
            addQCTerms();
            addReplicateTerms();
            addNormalizationTerms();
            addExperimentalDesigns();
        }
        return document;

    }

    /**
     * @throws MageTabTextFileLoaderException
     */
    private void getSdrfFiles() throws MageTabTextFileLoaderException {
        List<String> sdrfFileList = ((ArrayList<String>) entityMap.getCollection(IdfDocument.SDRF_FILE));
        if (isNotEmpty(sdrfFileList.get(0))) {
            try {
                document.addToSdrfFiles(new URI(sdrfFileList.get(0)));
            } catch (URISyntaxException se) {
                throw new MageTabTextFileLoaderException("error with URI", se);
            }
        }
    }

    /**
     *
     */
    private void getPublicReleaseDate() throws MageTabTextFileLoaderException {
        List<String> pubDateList = ((ArrayList<String>) entityMap.getCollection(IdfDocument.PUBLIC_RELEASE_DATE));
        if (isNotEmpty(pubDateList.get(0))) {
            try {
            document.setPublicReleaseDate((new java.text.SimpleDateFormat("MM/dd/yyyy")).parse(pubDateList.get(0)));
            } catch (java.text.ParseException pe) {
                throw new MageTabTextFileLoaderException("Invalid PubRelease Date", pe);
            }
        }
    }

    /**
     *
     */
    private void getExperimentDescription() {
        List<String> expDescr = ((ArrayList<String>) entityMap.getCollection(IdfDocument.EXPERIMENT_DESCRIPTION));
        if (isNotEmpty(expDescr.get(0))) {
            document.setExperimentDescription(expDescr.get(0));
        }
    }

    /**
     *
     */
    private void getExperimentDate() throws MageTabTextFileLoaderException {
        List<String> expDateList = ((ArrayList<String>) entityMap.getCollection(IdfDocument.EXPERIMENT_DATE));
        if (isNotEmpty(expDateList.get(0))) {
            try {
                document.setExperimentDate((new java.text.SimpleDateFormat("MM/dd/yyyy")).parse(expDateList.get(0)));
            } catch (java.text.ParseException pe) {
                throw new MageTabTextFileLoaderException("Invalid Experiment Date", pe);
            }
        }
    }

    /**
     *
     */
    private void getTitle() {
        List<String> titleList = ((ArrayList<String>) entityMap.getCollection(IdfDocument.INVESTIGATION_TITLE));
        if (isNotEmpty(titleList.get(0))) {
            document.setTitle(titleList.get(0));
        }
    }



    private void addExperimentalDesigns() {
        List<String> expDesigns = (ArrayList<String>) entityMap.getCollection(IdfDocument.EXPERIMENT_DESIGN);

        List<Term> expDesignList = new ArrayList<Term>();

        for (int i = 0; i < expDesigns.size(); i++) {
            boolean canAdd = false;
            if (isNotEmpty(expDesigns.get(i))) {
                canAdd = true;
                Term term = new Term();
                term.setValue(expDesigns.get(i));
                if (canAdd) {
                    expDesignList.add(term);
                }
            }
        }
        document.setExperimentalDesigns(expDesignList);
    }

    private void addNormalizationTerms() {
        List<String> types = (ArrayList<String>) entityMap.getCollection(IdfDocument.NORMALIZATION_TYPE);
        List<String> typeSrcList = (ArrayList<String>) entityMap.getCollection(IdfDocument.NORMALIZATION_TERM_SRC);

        List<Semantic> normalizationList = new ArrayList<Semantic>();

        for (int i = 0; i < types.size(); i++) {
            boolean canAdd = false;
            if (isNotEmpty(types.get(i))) {
                canAdd = true;
                Semantic type = new Semantic();
                Term term = new Term();
                term.setValue(types.get(i));
                if (isNotEmpty(typeSrcList.get(i))) {
                    type.setSource(getSourceRef(typeSrcList.get(i)));
                }
                if (canAdd) {
                    normalizationList.add(type);
                }
            }
        }
        document.setNormalizations(normalizationList);
    }


    private void addReplicateTerms() {
        List<String> types = (ArrayList<String>) entityMap.getCollection(IdfDocument.REPLICATE_TYPE);
        List<String> typeSrcList = (ArrayList<String>) entityMap.getCollection(IdfDocument.REPLICATE_TERM_SRC);

        List<Semantic> replicateList = new ArrayList<Semantic>();

        for (int i = 0; i < types.size(); i++) {
            boolean canAdd = false;
            if (isNotEmpty(types.get(i))) {
                canAdd = true;
                Semantic type = new Semantic();
                Term term = new Term();
                term.setValue(types.get(i));
                if (isNotEmpty(typeSrcList.get(i))) {
                    type.setSource(getSourceRef(typeSrcList.get(i)));
                }
                if (canAdd) {
                    replicateList.add(type);
                }
            }
        }
        document.setReplicates(replicateList);
    }


    private void addQCTerms() {
        List<String> types = (ArrayList<String>) entityMap.getCollection(IdfDocument.QUALITY_CONTROL_TYPE);
        List<String> typeSrcList = (ArrayList<String>) entityMap.getCollection(IdfDocument.QUALITY_CONTROL_TERM_SRC);

        List<Semantic> qcList = new ArrayList<Semantic>();

        for (int i = 0; i < types.size(); i++) {
            boolean canAdd = false;
            if (isNotEmpty(types.get(i))) {
                canAdd = true;
                Semantic type = new Semantic();
                Term term = new Term();
                term.setValue(types.get(i));
                if (isNotEmpty(typeSrcList.get(i))) {
                    type.setSource(getSourceRef(typeSrcList.get(i)));
                }
                if (canAdd) {
                    qcList.add(type);
                }
            }
        }
        document.setQualityControls(qcList);
    }

    private void addExperimentalFactors() throws MageTabTextFileLoaderException {

        List<String> factors = (ArrayList<String>) entityMap.getCollection(IdfDocument.EXPERIMENT_FACTOR_NAME);
        List<String> types = (ArrayList<String>) entityMap.getCollection(IdfDocument.EXPERIMENT_FACTOR_TYPE);
        List<String> typeSrcList = (ArrayList<String>) entityMap.getCollection(IdfDocument.EXPERIMENT_FACTOR_TERM_SRC);
        List<ExperimentalFactor> factorList = new ArrayList<ExperimentalFactor>();
        for (int i = 0; i < factors.size(); i++) {
            ExperimentalFactor factor = new ExperimentalFactor();
            boolean canAdd = false;
            if (isNotEmpty(factors.get(i))) {
                canAdd = true;
                factor.setName(factors.get(i));
            }
            if (isNotEmpty(types.get(i))) {
                Semantic type = new Semantic();
                Term term = new Term();
                term.setValue(types.get(i));
                type.setType(term);
                if (isNotEmpty(typeSrcList.get(i))) {
                    type.setSource(getSourceRef(typeSrcList.get(i)));
                }
                factor.setType(type);
            }
            if (canAdd) {
                factorList.add(factor);
            }
        }
        document.setExperimentalFactors(factorList);

    }
    @SuppressWarnings("PMD")
    private void addPublications() throws MageTabTextFileLoaderException {

        List<String> titles = (ArrayList<String>) entityMap.getCollection(IdfDocument.PUBLICATION_TITLE);
        List<String> authors = (ArrayList<String>) entityMap.getCollection(IdfDocument.PUBLICATION_AUTHOR_LIST);
        List<String> dois = (ArrayList<String>) entityMap.getCollection(IdfDocument.PUBLICATION_DOI);
        List<String> statusS = (ArrayList<String>) entityMap.getCollection(IdfDocument.PUBLICATION_STATUS);
        List<String> statusSrcList = (ArrayList<String>)
                entityMap.getCollection(IdfDocument.PUBLICATION_STATUS_TERM_SRC);
        List<String> pubmedList = (ArrayList<String>) entityMap.getCollection(IdfDocument.PUBMED_ID);
        List<Publication> publicationList = new ArrayList<Publication>();
        for (int i = 0; i < titles.size(); i++) {
            boolean canAdd = false;
            Publication publication = new Publication();
            if (isNotEmpty(titles.get(i))) {
                canAdd = true;
                publication.setTitle(titles.get(i));
            }
            if (isNotEmpty(authors.get(i))) {
                canAdd = true;
                publication.setAuthorList(authors.get(i));
            }
            if (isNotEmpty(dois.get(i))) {
                publication.setDoi(dois.get(i));
            }
            if (isNotEmpty(pubmedList.get(i))) {
                publication.setPubmedId(Integer.parseInt(pubmedList.get(i)));
            }
            if (isNotEmpty(statusS.get(i))) {
                Semantic status = new Semantic();
                Term term = new Term();
                term.setValue(statusS.get(i));
                status.setType(term);
                if (isNotEmpty(statusSrcList.get(i))) {
                    status.setSource(getSourceRef(statusSrcList.get(i)));
                }
                publication.setStatus(status);
            }
            if (canAdd) {
                publicationList.add(publication);
            }
        }
        document.setPublications(publicationList);
    }

    @SuppressWarnings("PMD")
    private void addPersons() throws MageTabTextFileLoaderException {
        List<String> firstNames = (ArrayList<String>) entityMap.getCollection(IdfDocument.PERSON_FIRST_NAME);
        List<String> lastNames = (ArrayList<String>) entityMap.getCollection(IdfDocument.PERSON_LAST_NAME);
        List<String> middleInits = (ArrayList<String>) entityMap.getCollection(IdfDocument.PERSON_MID_INITIAL);
        List<String> phones = (ArrayList<String>) entityMap.getCollection(IdfDocument.PERSON_PHONE);
        List<String> faxS = (ArrayList<String>) entityMap.getCollection(IdfDocument.PERSON_FAX);
        List<String> addresses = (ArrayList<String>) entityMap.getCollection(IdfDocument.PERSON_ADDRESS);
        List<String> affiliations = (ArrayList<String>) entityMap.getCollection(IdfDocument.PERSON_AFFILIATION);
        List<String> emails = (ArrayList<String>) entityMap.getCollection(IdfDocument.PERSON_EMAIL);
        List<String> rolesSrcList = (ArrayList<String>) entityMap.getCollection(IdfDocument.PERSON_ROLES_TERM_SRC);
        List<String> roles = (ArrayList<String>) entityMap.getCollection(IdfDocument.PERSON_ROLES);

        List<Person> personList = new ArrayList<Person>();

        for (int i = 0; i < lastNames.size(); i++) {
            boolean canAdd = false;
            Person person = new Person();
            if (isNotEmpty(lastNames.get(i))) {
                canAdd = true;
                person.setLastName(lastNames.get(i));
            }
            if (isNotEmpty(firstNames.get(i))) {
                canAdd = true;
                person.setFirstName(firstNames.get(i));
            }
            if (isNotEmpty(middleInits.get(i))) {
                person.setMiddleInit(middleInits.get(i));
            }
            if (isNotEmpty(phones.get(i))) {
                person.setPhone(phones.get(i));
            }
            if (isNotEmpty(faxS.get(i))) {
                person.setFax(faxS.get(i));
            }
            if (isNotEmpty(addresses.get(i))) {
                person.setAddress(addresses.get(i));
            }
            if (isNotEmpty(affiliations.get(i))) {
                person.setAffiliation(affiliations.get(i));
            }
            if (isNotEmpty(emails.get(i))) {
                person.setEmail(emails.get(i));
            }
            if (isNotEmpty(roles.get(i))) {
                Semantic role = new Semantic();
                Term term = new Term();
                term.setValue(roles.get(i));
                role.setType(term);
                if (isNotEmpty(rolesSrcList.get(i))) {
                    role.setSource(getSourceRef(rolesSrcList.get(i)));
                }
                person.setRole(role);
            }
            if (canAdd) {
                personList.add(person);
            }
        }
        document.setPersons(personList);

    }

    private void addTermSources() throws MageTabTextFileLoaderException {
        List<String> termSourceNames = (ArrayList<String>) entityMap.getCollection(IdfDocument.TERM_SOURCE_NAME);
        List<String> termSourceFiles = (ArrayList<String>) entityMap.getCollection(IdfDocument.TERM_SOURCE_FILE);
        List<String> versions = (ArrayList<String>) entityMap.getCollection(IdfDocument.TERM_SOURCE_VERSION);
        List<TermSource> termSourceList = new ArrayList<TermSource>();
        try {
            for (int i = 0; i < termSourceNames.size(); i++) {
                boolean canAdd = false;
                TermSource termSource = new TermSource();
                canAdd = setTermSources(termSourceNames, termSourceFiles, versions, i, termSource);
                if (canAdd) {
                    termSourceList.add(termSource);
                }
            }
            document.setTermSources(termSourceList);
        } catch (Exception e) {
            throw new MageTabTextFileLoaderException("exception", e);
        }
    }

    /**
     * @param termSourceNames
     * @param termSourceFiles
     * @param versions
     * @param i
     * @param canAdd
     * @param termSource
     * @return
     * @throws URISyntaxException
     */
    private boolean setTermSources(List<String> termSourceNames, List<String> termSourceFiles,
                List<String> versions, int i, TermSource termSource) throws URISyntaxException {
        boolean canAdd = false;
        if (isNotEmpty(termSourceNames.get(i))) {
            canAdd = true;
            termSource.setName((String) termSourceNames.get(i));
        }
        if (isNotEmpty(termSourceFiles.get(i))) {
            canAdd = true;
            termSource.setFile(new URI((String) termSourceFiles.get(i)));
        }
        if (isNotEmpty(versions.get(i))) {
            canAdd = true;
            termSource.setVersion(versions.get(0));
        }
        return canAdd;
    }

    private void addProtocols() throws MageTabTextFileLoaderException {
        List<Protocol> protocolList = new ArrayList<Protocol>();
        List<String> names = (ArrayList<String>) entityMap.getCollection(IdfDocument.PROTOCOL_NAME);
        List<String> types = (ArrayList<String>) entityMap.getCollection(IdfDocument.PROTOCOL_TYPE);
        List<String> typeSrcList = (ArrayList<String>) entityMap.getCollection(IdfDocument.PROTOCOL_TERM_SRC);
        List<String> descrs = (ArrayList<String>) entityMap.getCollection(IdfDocument.PROTOCOL_DESCRIPTION);
        List<String> params = (ArrayList<String>) entityMap.getCollection(IdfDocument.PROTOCOL_PARAMETERS);
        List<String> hardwares = (ArrayList<String>) entityMap.getCollection(IdfDocument.PROTOCOL_HARDWARE);
        List<String> softwares = (ArrayList<String>) entityMap.getCollection(IdfDocument.PROTOCOL_SOFTWARE);
        List<String> contacts = (ArrayList<String>) entityMap.getCollection(IdfDocument.PROTOCOL_CONTACT);
        for (int i = 0; i < names.size(); i++) {
            boolean canAdd = false;
            Protocol protocol = new Protocol();
            if (isNotEmpty(names.get(i))) {
                canAdd = true;
                protocol.setName(names.get(i));
            }
            setProtocolDescrs(descrs, i, protocol);
            setProtocolHardwares(hardwares, i, protocol);
            setProtocolSoftwares(softwares, i, protocol);
            setProtocolContacts(contacts, i, protocol);
            setProtocolParams(params, i, protocol);
            setProtocolTypes(types, typeSrcList, i, protocol);
            if (canAdd) {
                protocolList.add(protocol);
            }
        }
        document.setProtocols(protocolList);
    }

    /**
     * @param types
     * @param typeSrcList
     * @param i
     * @param protocol
     */
    private void setProtocolTypes(List<String> types, List<String> typeSrcList, int i, Protocol protocol) {
        if (isNotEmpty(types.get(i))) {
            Semantic protocolType = new Semantic();
            Term type = new Term();
            type.setValue(types.get(i));
            protocolType.setType(type);
            if (isNotEmpty(typeSrcList.get(i))) {
                protocolType.setSource(getSourceRef(typeSrcList.get(i)));

            }
            protocol.setType(protocolType);
        }
    }

    /**
     * @param descrs
     * @param i
     * @param protocol
     */
    private void setProtocolDescrs(List<String> descrs, int i, Protocol protocol) {
        if (isNotEmpty(descrs.get(i))) {
            protocol.setDescription(descrs.get(i));
        }
    }

    /**
     * @param hardwares
     * @param i
     * @param protocol
     */
    private void setProtocolHardwares(List<String> hardwares, int i, Protocol protocol) {
        if (isNotEmpty(hardwares.get(i))) {
            protocol.setHardware(hardwares.get(i));
        }
    }

    /**
     * @param softwares
     * @param i
     * @param protocol
     */
    private void setProtocolSoftwares(List<String> softwares, int i, Protocol protocol) {
        if (isNotEmpty(softwares.get(i))) {
            protocol.setSoftware(softwares.get(i));
        }
    }

    /**
     * @param contacts
     * @param i
     * @param protocol
     */
    private void setProtocolContacts(List<String> contacts, int i, Protocol protocol) {
        if (isNotEmpty(contacts.get(i))) {
            protocol.setContact(contacts.get(i));
        }
    }

    /**
     * @param params
     * @param i
     * @param protocol
     */
    private void setProtocolParams(List<String> params, int i, Protocol protocol) {
        if (isNotEmpty(params.get(i))) {
            protocol.setParameters(params.get(i));
        }
    }

    private TermSource getSourceRef(String argSource) {
        List<TermSource> documentSrcs = document.getTermSources();
        Iterator<TermSource> iter = documentSrcs.iterator();
        while (iter.hasNext()) {
            TermSource trmSrc = iter.next();
            if (trmSrc.getName().equals(argSource)) {
                return trmSrc;
            }
        }
        return null;

    }

    private boolean isNotEmpty(String argString) {
        return (argString != null && StringUtils.isNotBlank(argString));
    }


    private void loadEntityMap() throws MageTabTextFileLoaderException {
        entityMap = new MultiValueMap();
        handleFileData();
    }

    private void handleFileData() throws MageTabTextFileLoaderException {
        try {
            lineIterator = FileUtils.lineIterator(mageTabFile, "UTF-8");
            getValues();
        } catch (IOException e) {
            throw new MageTabTextFileLoaderException(
                "Couldn't load array design from TXT file " + mageTabFile.getAbsolutePath(), e);
        }

    }


    private void getValues() throws MageTabTextFileLoaderException {
        while ((currentLineContents = readLine()) != null) {
            Iterator iter = currentLineContents.iterator();
            String key = null;
            if (iter.hasNext()) {
                key = (String) iter.next();
                while (iter.hasNext()) {
                    String value = (String) iter.next();
                    entityMap.put(key.toUpperCase(Locale.ENGLISH), value);
                }
            }
        }
    }


    private List<String> readLine() {
        if (lineIterator.hasNext()) {
            currentLine = lineIterator.nextLine();
            currentLineContents = LineSplitter.TAB_DELIMITED.split(currentLine);
            return currentLineContents;
        } else {
            currentLine = null;
            currentLineContents = null;
            return null;
        }
    }

}
