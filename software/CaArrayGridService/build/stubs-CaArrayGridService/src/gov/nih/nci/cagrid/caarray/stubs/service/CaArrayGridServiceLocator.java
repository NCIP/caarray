/**
 * CaArrayGridServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Apr 28, 2006 (12:42:00 EDT) WSDL2Java emitter.
 */

package gov.nih.nci.cagrid.caarray.stubs.service;

public class CaArrayGridServiceLocator extends org.apache.axis.client.Service implements gov.nih.nci.cagrid.caarray.stubs.service.CaArrayGridService {

    public CaArrayGridServiceLocator() {
    }


    public CaArrayGridServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CaArrayGridServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CaArrayGridServicePortTypePort
    private java.lang.String CaArrayGridServicePortTypePort_address = "http://localhost:8080/wsrf/services/";

    public java.lang.String getCaArrayGridServicePortTypePortAddress() {
        return CaArrayGridServicePortTypePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CaArrayGridServicePortTypePortWSDDServiceName = "CaArrayGridServicePortTypePort";

    public java.lang.String getCaArrayGridServicePortTypePortWSDDServiceName() {
        return CaArrayGridServicePortTypePortWSDDServiceName;
    }

    public void setCaArrayGridServicePortTypePortWSDDServiceName(java.lang.String name) {
        CaArrayGridServicePortTypePortWSDDServiceName = name;
    }

    public gov.nih.nci.cagrid.caarray.stubs.CaArrayGridServicePortType getCaArrayGridServicePortTypePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CaArrayGridServicePortTypePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCaArrayGridServicePortTypePort(endpoint);
    }

    public gov.nih.nci.cagrid.caarray.stubs.CaArrayGridServicePortType getCaArrayGridServicePortTypePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            gov.nih.nci.cagrid.caarray.stubs.bindings.CaArrayGridServicePortTypeSOAPBindingStub _stub = new gov.nih.nci.cagrid.caarray.stubs.bindings.CaArrayGridServicePortTypeSOAPBindingStub(portAddress, this);
            _stub.setPortName(getCaArrayGridServicePortTypePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCaArrayGridServicePortTypePortEndpointAddress(java.lang.String address) {
        CaArrayGridServicePortTypePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (gov.nih.nci.cagrid.caarray.stubs.CaArrayGridServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                gov.nih.nci.cagrid.caarray.stubs.bindings.CaArrayGridServicePortTypeSOAPBindingStub _stub = new gov.nih.nci.cagrid.caarray.stubs.bindings.CaArrayGridServicePortTypeSOAPBindingStub(new java.net.URL(CaArrayGridServicePortTypePort_address), this);
                _stub.setPortName(getCaArrayGridServicePortTypePortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("CaArrayGridServicePortTypePort".equals(inputPortName)) {
            return getCaArrayGridServicePortTypePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://caarray.cagrid.nci.nih.gov/CaArrayGridService/service", "CaArrayGridService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://caarray.cagrid.nci.nih.gov/CaArrayGridService/service", "CaArrayGridServicePortTypePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("CaArrayGridServicePortTypePort".equals(portName)) {
            setCaArrayGridServicePortTypePortEndpointAddress(address);
        }
        else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
