package gov.nih.nci.cagrid.caarray.service;

import java.rmi.RemoteException;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 *
 * @created by Introduce Toolkit version 1.1
 *
 */
public class CaArraySvcImpl extends CaArraySvcImplBase {

    public CaArraySvcImpl() throws RemoteException {
        super();
    }

    public gov.nih.nci.caarray.domain.array.ArrayDesignDetails getDesignDetails(
            gov.nih.nci.caarray.domain.array.ArrayDesign arrayDesign) throws RemoteException {
        System.out.println("here.... getDesignDetails()");
        throw new RemoteException("Not yet implemented");
    }

    public gov.nih.nci.caarray.domain.data.DataSet getDataSet(
            gov.nih.nci.caarray.domain.data.AbstractArrayData abstractArrayData) throws RemoteException {
        System.out.println("here.... getDataSet()");
        throw new RemoteException("Not yet implemented");
    }

    public java.lang.String echo(java.lang.String string) throws RemoteException {
        System.out.println("here.... echo(" + string + ")");
        return string;
    }

}
