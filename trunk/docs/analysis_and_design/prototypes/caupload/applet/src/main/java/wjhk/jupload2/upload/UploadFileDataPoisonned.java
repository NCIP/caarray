package wjhk.jupload2.upload;

/**
 * 
 * This class is a kind of 'End of Queue' object. It is posted by the
 * {@link FilePreparationThread} on the preparedFileQueue, to indicate to the
 * {@link PacketConstructionThread} that the last file was sent.
 * 
 * @author etienne_sf
 * 
 */
public class UploadFileDataPoisonned extends UploadFileData {

    UploadFileDataPoisonned() {
        super(null, -1, null, null);
    }

    /**
     * @return the poisonned status. Returns always true, as this class is only
     *         used to indicate the 'End Of Queue' marker in the
     *         preparedFileQueue.
     * @see UploadFileData
     */
    final public boolean isPoisonned() {
        return true;
    }

}
