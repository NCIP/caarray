package wjhk.jupload2.testhelpers;

import wjhk.jupload2.filedata.FileData;
import wjhk.jupload2.policies.UploadPolicy;
import wjhk.jupload2.upload.FileUploadManagerThread;
import wjhk.jupload2.upload.UploadFileData;

/**
 * @author etienne_sf
 */
public class FileUploadDataTestHelper extends UploadFileData {

	/**
	 * @param fileDataParam
	 * @param numOfFileInCurrentRequest
	 * @param fileUploadManagerThreadParam
	 * @param uploadPolicyParam
	 */
	public FileUploadDataTestHelper(FileData fileDataParam,
			int numOfFileInCurrentRequest,
			FileUploadManagerThread fileUploadManagerThreadParam,
			UploadPolicy uploadPolicyParam) {
		super(fileDataParam, numOfFileInCurrentRequest,
				fileUploadManagerThreadParam, uploadPolicyParam);
	}

}
