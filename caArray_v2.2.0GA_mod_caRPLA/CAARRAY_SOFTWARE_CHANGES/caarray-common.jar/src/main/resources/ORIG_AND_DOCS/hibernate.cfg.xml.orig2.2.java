<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
    "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
    "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>

        <!-- @DATASOURCE_CONFIG_START@
        <property name="hibernate.connection.datasource">java:jdbc/CaArrayDataSource</property>
        <property name="hibernate.current_session_context_class">managed</property>
        <property name="hibernate.transaction.factory_class">org.hibernate.transaction.CMTTransactionFactory</property>
        <property name="hibernate.transaction.manager_lookup_class">org.hibernate.transaction.JBossTransactionManagerLookup</property>
        <property name="hibernate.dialect">gov.nih.nci.caarray.util.CaarrayInnoDBDialect</property>
        @DATASOURCE_CONFIG_END@ -->

        <!-- @HIBERNATE_CONFIG_START@
        <property name="hibernate.connection.driver_class">com.mysql.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://@SERVERNAME@/@DBNAME@</property>
        <property name="hibernate.connection.username">@DBUSER@</property>
        <property name="hibernate.connection.password">@DBPASSWD@</property>
        <property name="hibernate.dialect">gov.nih.nci.caarray.util.CaarrayInnoDBDialect</property>
        @HIBERNATE_CONFIG_END@ -->

        <!-- mapping files -->
        <mapping class="edu.georgetown.pir.AdditionalOrganismName" />
        <mapping class="edu.georgetown.pir.Organism" />
        <mapping class="gov.nih.nci.caarray.domain.BlobHolder" />
        <mapping class="gov.nih.nci.caarray.domain.array.AbstractDesignElement"/>
        <mapping class="gov.nih.nci.caarray.domain.array.AbstractProbe"/>
        <mapping class="gov.nih.nci.caarray.domain.array.Array"/>
        <mapping class="gov.nih.nci.caarray.domain.array.ArrayGroup"/>
        <mapping class="gov.nih.nci.caarray.domain.array.ArrayDesign"/>
        <mapping class="gov.nih.nci.caarray.domain.array.ArrayDesignDetails"/>
        <mapping class="gov.nih.nci.caarray.domain.array.ExonProbeAnnotation"/>
        <mapping class="gov.nih.nci.caarray.domain.array.ExpressionProbeAnnotation"/>
        <mapping class="gov.nih.nci.caarray.domain.array.Feature"/>
        <mapping class="gov.nih.nci.caarray.domain.array.Gene"/>
        <mapping class="gov.nih.nci.caarray.domain.array.LogicalProbe"/>
        <mapping class="gov.nih.nci.caarray.domain.array.PhysicalProbe"/>
        <mapping class="gov.nih.nci.caarray.domain.array.ProbeGroup"/>
        <mapping class="gov.nih.nci.caarray.domain.array.SNPProbeAnnotation"/>
        <mapping class="gov.nih.nci.caarray.domain.contact.Address"/>
        <mapping class="gov.nih.nci.caarray.domain.contact.Organization"/>
        <mapping class="gov.nih.nci.caarray.domain.contact.Person"/>
        <mapping class="gov.nih.nci.caarray.domain.data.ArrayDataType"/>
        <mapping class="gov.nih.nci.caarray.domain.data.BooleanColumn"/>
        <mapping class="gov.nih.nci.caarray.domain.data.DataSet"/>
        <mapping class="gov.nih.nci.caarray.domain.data.DesignElementList"/>
        <mapping class="gov.nih.nci.caarray.domain.data.DerivedArrayData"/>
        <mapping class="gov.nih.nci.caarray.domain.data.DoubleColumn"/>
        <mapping class="gov.nih.nci.caarray.domain.data.FloatColumn"/>
        <mapping class="gov.nih.nci.caarray.domain.data.HybridizationData"/>
        <mapping class="gov.nih.nci.caarray.domain.data.Image"/>
        <mapping class="gov.nih.nci.caarray.domain.data.IntegerColumn"/>
        <mapping class="gov.nih.nci.caarray.domain.data.LongColumn"/>
        <mapping class="gov.nih.nci.caarray.domain.data.QuantitationType"/>
        <mapping class="gov.nih.nci.caarray.domain.data.RawArrayData"/>
        <mapping class="gov.nih.nci.caarray.domain.data.ShortColumn"/>
        <mapping class="gov.nih.nci.caarray.domain.data.StringColumn"/>
        <mapping class="gov.nih.nci.caarray.domain.file.CaArrayFile"/>
        <mapping class="gov.nih.nci.caarray.domain.hybridization.Hybridization"/>
        <mapping class="gov.nih.nci.caarray.domain.permissions.CollaboratorGroup"/>
        <mapping class="gov.nih.nci.caarray.domain.permissions.AccessProfile"/>
        <mapping class="gov.nih.nci.caarray.domain.project.Factor"/>
        <mapping class="gov.nih.nci.caarray.domain.project.FactorValue"/>
        <mapping class="gov.nih.nci.caarray.domain.project.Experiment"/>
        <mapping class="gov.nih.nci.caarray.domain.project.ExperimentContact"/>
        <mapping class="gov.nih.nci.caarray.domain.project.Project"/>
        <mapping class="gov.nih.nci.caarray.domain.project.PaymentMechanism"/>
        <mapping class="gov.nih.nci.caarray.domain.protocol.Parameter"/>
        <mapping class="gov.nih.nci.caarray.domain.protocol.ParameterValue"/>
        <mapping class="gov.nih.nci.caarray.domain.protocol.Protocol"/>
        <mapping class="gov.nih.nci.caarray.domain.protocol.ProtocolApplication"/>
        <mapping class="gov.nih.nci.caarray.domain.publication.Publication"/>
        <mapping class="gov.nih.nci.caarray.domain.sample.Extract"/>
        <mapping class="gov.nih.nci.caarray.domain.sample.LabeledExtract"/>
        <mapping class="gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic"/>
        <mapping class="gov.nih.nci.caarray.domain.sample.Sample"/>
        <mapping class="gov.nih.nci.caarray.domain.sample.Source"/>
        <mapping class="gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic"/>
        <mapping class="gov.nih.nci.caarray.domain.data.Image"/>
        <mapping class="gov.nih.nci.caarray.domain.data.RawArrayData"/>
        <mapping class="gov.nih.nci.caarray.domain.data.DerivedArrayData"/>
        <mapping class="gov.nih.nci.caarray.domain.hybridization.Hybridization"/>
        <mapping class="gov.nih.nci.caarray.domain.vocabulary.Category"/>
        <mapping class="gov.nih.nci.caarray.domain.vocabulary.Term"/>
        <mapping class="gov.nih.nci.caarray.domain.vocabulary.TermSource"/>
        <mapping class="gov.nih.nci.caarray.domain.register.RegistrationRequest"/>
        <mapping class="gov.nih.nci.caarray.domain.country.Country"/>
        <mapping class="gov.nih.nci.caarray.domain.state.State"/>
        <mapping class="gov.nih.nci.caarray.validation.ValidationMessage"/>
        <mapping class="gov.nih.nci.caarray.validation.FileValidationResult"/>
        <mapping class="gov.nih.nci.cabio.domain.ArrayReporter"/>
        <mapping class="gov.nih.nci.cabio.domain.ExonArrayReporter"/>
        <mapping class="gov.nih.nci.cabio.domain.ExpressionArrayReporter"/>
        <mapping class="gov.nih.nci.cabio.domain.SNPArrayReporter"/>
        <mapping class="gov.nih.nci.cabio.domain.TranscriptArrayReporter"/>

        <!-- configure the security post-load listener -->
        <event type="post-load">
            <listener class="org.hibernate.event.def.DefaultPostLoadEventListener"/>
            <listener class="gov.nih.nci.caarray.security.SecurityPolicyPostLoadEventListener"/>
        </event>
    </session-factory>
</hibernate-configuration>