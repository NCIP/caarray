/**
 * SearchResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Apr 28, 2006 (12:42:00 EDT) WSDL2Java emitter.
 */

package gov.nih.nci.cagrid.caarray.stubs;

public class SearchResponse  implements java.io.Serializable {
    private gov.nih.nci.caarray.domain.AbstractCaArrayEntity[] abstractCaArrayEntity;

    public SearchResponse() {
    }

    public SearchResponse(
           gov.nih.nci.caarray.domain.AbstractCaArrayEntity[] abstractCaArrayEntity) {
           this.abstractCaArrayEntity = abstractCaArrayEntity;
    }


    /**
     * Gets the abstractCaArrayEntity value for this SearchResponse.
     * 
     * @return abstractCaArrayEntity
     */
    public gov.nih.nci.caarray.domain.AbstractCaArrayEntity[] getAbstractCaArrayEntity() {
        return abstractCaArrayEntity;
    }


    /**
     * Sets the abstractCaArrayEntity value for this SearchResponse.
     * 
     * @param abstractCaArrayEntity
     */
    public void setAbstractCaArrayEntity(gov.nih.nci.caarray.domain.AbstractCaArrayEntity[] abstractCaArrayEntity) {
        this.abstractCaArrayEntity = abstractCaArrayEntity;
    }

    public gov.nih.nci.caarray.domain.AbstractCaArrayEntity getAbstractCaArrayEntity(int i) {
        return this.abstractCaArrayEntity[i];
    }

    public void setAbstractCaArrayEntity(int i, gov.nih.nci.caarray.domain.AbstractCaArrayEntity _value) {
        this.abstractCaArrayEntity[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SearchResponse)) return false;
        SearchResponse other = (SearchResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.abstractCaArrayEntity==null && other.getAbstractCaArrayEntity()==null) || 
             (this.abstractCaArrayEntity!=null &&
              java.util.Arrays.equals(this.abstractCaArrayEntity, other.getAbstractCaArrayEntity())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAbstractCaArrayEntity() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAbstractCaArrayEntity());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAbstractCaArrayEntity(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SearchResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://caarray.cagrid.nci.nih.gov/CaArrayGridService", ">SearchResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("abstractCaArrayEntity");
        elemField.setXmlName(new javax.xml.namespace.QName("gme://caCORE.cabig/3.2/gov.nih.nci.caarray.domain", "AbstractCaArrayEntity"));
        elemField.setXmlType(new javax.xml.namespace.QName("gme://caCORE.cabig/3.2/gov.nih.nci.caarray.domain", "AbstractCaArrayEntity"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
