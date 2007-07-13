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
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.domain.vocabulary.Term;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author John Pike
 *
 */
public class IdfDocument extends AbstractMageTabDocument {

    private String title;
    private Date experimentDate;
    private String experimentDescription;
    private List<TermSource> termSources;
    private Date publicReleaseDate;
    private List<URI> sdrfFiles;
    private List<Term> experimentalDesigns;
    private List<ExperimentalFactor> experimentalFactors;
    private List<Person> persons;
    private List<Semantic> qualityControls;
    private List<Semantic> replicates;
    private List<Semantic> normalizations;
    private List<Publication> publications;
    private List<Comment> comments;
    private List<Protocol> protocols;

    /**
     *
     */
    public static final String INVESTIGATION_TITLE = "Investigation Title".toUpperCase();
    /**
     *
     */
    public static final String EXPERIMENT_DESIGN = "Experimental Design".toUpperCase();
    /**
     *
     */
    public static final String EXPERIMENT_FACTOR_NAME = "Experimental Factor Name".toUpperCase();
    /**
     *
     */
    public static final String EXPERIMENT_FACTOR_TYPE = "Experimental Factor Type".toUpperCase();
    /**
     *
     */
    public static final String EXPERIMENT_FACTOR_TERM_SRC = "Experimental Factor Term Source REF".toUpperCase();

    /**
     *
     */
    public static final String PERSON_LAST_NAME = "Person Last Name".toUpperCase();
    /**
     *
     */
    public static final String PERSON_FIRST_NAME = "Person First Name".toUpperCase();
    /**
     *
     */
    public static final String PERSON_MID_INITIAL = "Person Mid Initials".toUpperCase();
    /**
     *
     */
    public static final String PERSON_EMAIL = "Person Email".toUpperCase();
    /**
     *
     */
    public static final String PERSON_PHONE = "Person Phone".toUpperCase();
    /**
     *
     */
    public static final String PERSON_FAX = "Person Fax".toUpperCase();
    /**
     *
     */
    public static final String PERSON_ADDRESS = "Person Address".toUpperCase();
    /**
     *
     */
    public static final String PERSON_AFFILIATION = "Person Affiliation".toUpperCase();
    /**
     *
     */
    public static final String PERSON_ROLES = "Person Roles".toUpperCase();
    /**
     *
     */
    public static final String PERSON_ROLES_TERM_SRC = "Person Roles Term Source REF".toUpperCase();

    /**
     *
     */
    public static final String QUALITY_CONTROL_TYPE = "Quality Control Type".toUpperCase();
    /**
     *
     */
    public static final String QUALITY_CONTROL_TERM_SRC = "Quality Control Term Source REF".toUpperCase();
    /**
     *
     */
    public static final String REPLICATE_TYPE = "Replicate Type".toUpperCase();
    /**
     *
     */
    public static final String REPLICATE_TERM_SRC = "Replicate Term Source REF".toUpperCase();
    /**
     *
     */
    public static final String NORMALIZATION_TYPE = "Normalization Type".toUpperCase();
    /**
     *
     */
    public static final String NORMALIZATION_TERM_SRC = "Normalization Term Source REF".toUpperCase();
    /**
     *
     */
    public static final String EXPERIMENT_DATE = "Date of Experiment".toUpperCase();
    /**
     *
     */
    public static final String PUBLIC_RELEASE_DATE = "Public Release Date".toUpperCase();

    /**
     *
     */
    public static final String PUBMED_ID = "PubMed ID".toUpperCase();
    /**
     *
     */
    public static final String PUBLICATION_DOI = "Publication DOI".toUpperCase();
    /**
     *
     */
    public static final String PUBLICATION_AUTHOR_LIST = "Publication Author List".toUpperCase();
    /**
     *
     */
    public static final String PUBLICATION_TITLE = "Publication Title".toUpperCase();
    /**
     *
     */
    public static final String PUBLICATION_STATUS = "Publication Status".toUpperCase();
    /**
     *
     */
    public static final String PUBLICATION_STATUS_TERM_SRC = "Publication Status Term Source REF".toUpperCase();
    /**
     *
     */
    public static final String EXPERIMENT_DESCRIPTION = "Experiment Description".toUpperCase();

    /**
     *
     */
    public static final String PROTOCOL_NAME = "Protocol Name".toUpperCase();
    /**
     *
     */
    public static final String PROTOCOL_TYPE = "Protocol Type".toUpperCase();
    /**
     *
     */
    public static final String PROTOCOL_DESCRIPTION = "Protocol Description".toUpperCase();
    /**
     *
     */
    public static final String PROTOCOL_PARAMETERS = "Protocol Parameters".toUpperCase();
    /**
     *
     */
    public static final String PROTOCOL_TERM_SRC = "Protocol Term Source REF".toUpperCase();
    /**
     *
     */
    public static final String PROTOCOL_HARDWARE = "Protocol Hardware".toUpperCase();
    /**
     *
     */
    public static final String PROTOCOL_SOFTWARE = "Protocol Software".toUpperCase();
    /**
     *
     */
    public static final String PROTOCOL_CONTACT = "Protocol Contact".toUpperCase();
    /**
     *
     */
    public static final String SDRF_FILE = "SDRF File".toUpperCase();
    /**
     *
     */
    public static final String TERM_SOURCE_NAME = "Term Source Name".toUpperCase();
    /**
     *
     */
    public static final String TERM_SOURCE_FILE = "Term Source File".toUpperCase();
    /**
     *
     */
    public static final String TERM_SOURCE_VERSION = "Term Source Version".toUpperCase();



    /**
     *
     */
    public IdfDocument() {
        // TODO Auto-generated constructor stub
    }
    /**
     * @return the experimentDate
     */
    public Date getExperimentDate() {
        return this.experimentDate;
    }
    /**
     * @param experimentDate the experimentDate to set
     */
    public void setExperimentDate(Date experimentDate) {
        this.experimentDate = experimentDate;
    }
    /**
     * @return the experimentDescription
     */
    public String getExperimentDescription() {
        return this.experimentDescription;
    }
    /**
     * @param experimentDescription the experimentDescription to set
     */
    public void setExperimentDescription(String experimentDescription) {
        this.experimentDescription = experimentDescription;
    }
    /**
     * @return the termSources
     */
    public List<TermSource> getTermSources() {
        return this.termSources;
    }
    /**
     * @param termSources the termSources to set
     */
    public void setTermSources(List<TermSource> termSources) {
        this.termSources = termSources;
    }
    /**
     * @param termSource the termSource to set
     */
    public void addToTermSources(TermSource termSource) {
        this.termSources.add(termSource);
    }
    /**
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }
    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @return the comments
     */
    public List<Comment> getComments() {
        return this.comments;
    }
    /**
     * @param comments the comments to set
     */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    /**
     * @return the experimentalDesigns
     */
    public List<Term> getExperimentalDesigns() {
        return this.experimentalDesigns;
    }
    /**
     * @param experimentalDesigns the experimentalDesigns to set
     */
    public void setExperimentalDesigns(List<Term> experimentalDesigns) {
        this.experimentalDesigns = experimentalDesigns;
    }
    /**
     * @return the experimentalFactors
     */
    public List<ExperimentalFactor> getExperimentalFactors() {
        return this.experimentalFactors;
    }
    /**
     * @param experimentalFactors the experimentalFactors to set
     */
    public void setExperimentalFactors(List<ExperimentalFactor> experimentalFactors) {
        this.experimentalFactors = experimentalFactors;
    }
    /**
     * @return the normalizations
     */
    public List<Semantic> getNormalizations() {
        return this.normalizations;
    }
    /**
     * @param normalizations the normalizations to set
     */
    public void setNormalizations(List<Semantic> normalizations) {
        this.normalizations = normalizations;
    }
    /**
     * @return the persons
     */
    public List<Person> getPersons() {
        return this.persons;
    }
    /**
     * @param persons the persons to set
     */
    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
    /**
     * @return the protocols
     */
    public List<Protocol> getProtocols() {
        return this.protocols;
    }
    /**
     * @param protocols the protocols to set
     */
    public void setProtocols(List<Protocol> protocols) {
        this.protocols = protocols;
    }
    /**
     * @return the publications
     */
    public List<Publication> getPublications() {
        return this.publications;
    }
    /**
     * @param publications the publications to set
     */
    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }
    /**
     * @return the publicReleaseDate
     */
    public Date getPublicReleaseDate() {
        return this.publicReleaseDate;
    }
    /**
     * @param publicReleaseDate the publicReleaseDate to set
     */
    public void setPublicReleaseDate(Date publicReleaseDate) {
        this.publicReleaseDate = publicReleaseDate;
    }
    /**
     * @return the qualityControls
     */
    public List<Semantic> getQualityControls() {
        return this.qualityControls;
    }
    /**
     * @param qualityControls the qualityControls to set
     */
    public void setQualityControls(List<Semantic> qualityControls) {
        this.qualityControls = qualityControls;
    }
    /**
     * @return the sdrfFiles
     */
    public List<URI> getSdrfFiles() {
        return this.sdrfFiles;
    }
    /**
     * @param sdrfFiles the sdrfFiles to set
     */
    public void setSdrfFiles(List<URI> sdrfFiles) {
        this.sdrfFiles = sdrfFiles;
    }
    /**
     * @param sdrfFile the sdrfFile to add
     */
    public void addToSdrfFiles(URI sdrfFile) {
        if (this.sdrfFiles == null) {
            sdrfFiles = new ArrayList<URI>();
        }
        this.sdrfFiles.add(sdrfFile);
    }
    /**
     * @return the replicates
     */
    public List<Semantic> getReplicates() {
        return this.replicates;
    }
    /**
     * @param replicates the replicates to set
     */
    public void setReplicates(List<Semantic> replicates) {
        this.replicates = replicates;
    }



}
