package gov.nih.nci.cagrid.caarray.service.globus;

import gov.nih.nci.cagrid.caarray.service.CaArraySvcImpl;

import java.rmi.RemoteException;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements each method in the portType of the service.  Each method call represented
 * in the port type will be then mapped into the unwrapped implementation which the user provides
 * in the CaArraySvcImpl class.  This class handles the boxing and unboxing of each method call
 * so that it can be correclty mapped in the unboxed interface that the developer has designed and 
 * has implemented.  Authorization callbacks are automatically made for each method based
 * on each methods authorization requirements.
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class CaArraySvcProviderImpl{
	
	CaArraySvcImpl impl;
	
	public CaArraySvcProviderImpl() throws RemoteException {
		impl = new CaArraySvcImpl();
	}
	

    public gov.nih.nci.cagrid.caarray.stubs.GetDesignDetailsResponse getDesignDetails(gov.nih.nci.cagrid.caarray.stubs.GetDesignDetailsRequest params) throws RemoteException {
    gov.nih.nci.cagrid.caarray.stubs.GetDesignDetailsResponse boxedResult = new gov.nih.nci.cagrid.caarray.stubs.GetDesignDetailsResponse();
    boxedResult.setArrayDesignDetails(impl.getDesignDetails(params.getArrayDesign().getArrayDesign()));
    return boxedResult;
  }

    public gov.nih.nci.cagrid.caarray.stubs.ReadFileResponse readFile(gov.nih.nci.cagrid.caarray.stubs.ReadFileRequest params) throws RemoteException {
    gov.nih.nci.cagrid.caarray.stubs.ReadFileResponse boxedResult = new gov.nih.nci.cagrid.caarray.stubs.ReadFileResponse();
    boxedResult.setResponse(impl.readFile(params.getCaArrayFile().getCaArrayFile()));
    return boxedResult;
  }

    public gov.nih.nci.cagrid.caarray.stubs.GetDataSetResponse getDataSet(gov.nih.nci.cagrid.caarray.stubs.GetDataSetRequest params) throws RemoteException {
    gov.nih.nci.cagrid.caarray.stubs.GetDataSetResponse boxedResult = new gov.nih.nci.cagrid.caarray.stubs.GetDataSetResponse();
    boxedResult.setDataSet(impl.getDataSet(params.getDataRetrievalRequest().getDataRetrievalRequest()));
    return boxedResult;
  }

    public gov.nih.nci.cagrid.caarray.stubs.CreateFileTransferResponse createFileTransfer(gov.nih.nci.cagrid.caarray.stubs.CreateFileTransferRequest params) throws RemoteException {
    gov.nih.nci.cagrid.caarray.stubs.CreateFileTransferResponse boxedResult = new gov.nih.nci.cagrid.caarray.stubs.CreateFileTransferResponse();
    boxedResult.setTransferServiceContextReference(impl.createFileTransfer(params.getCaArrayFile().getCaArrayFile()));
    return boxedResult;
  }

}
