package wjhk.jupload2.upload;

import java.util.concurrent.BlockingQueue;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.policies.UploadPolicy;

/**
 * @author etienne_sf
 * 
 */
public class PacketConstructionThread extends Thread {

    /** The current upload policy. */
    UploadPolicy uploadPolicy = null;

    /** The thread which globally manages the upload */
    FileUploadManagerThread fileUploadManagerThread = null;

    /**
     * The queue where each prepared file are stored, for further processing.
     * This class picks files here, and post them to the packetQueue.
     */
    BlockingQueue<UploadFileData> preparedFileQueue = null;

    /**
     * The queue where each prepared file will be stored, for further processing
     */
    BlockingQueue<UploadFilePacket> packetQueue = null;

    /**
     * The packet this instance is working on.
     */
    UploadFilePacket packetInProgress = null;

    /**
     * Indicates when the last file has been received. The last file is the
     * poisonned
     */
    boolean lastFileReceived = false;

    PacketConstructionThread(BlockingQueue<UploadFileData> preparedFileQueue,
            BlockingQueue<UploadFilePacket> packetQueue,
            FileUploadManagerThread fileUploadManagerThread,
            UploadPolicy uploadPolicy) {
        // A thread name is very useful, when debugging...
        super("PacketConstructionThread");

        this.preparedFileQueue = preparedFileQueue;
        this.packetQueue = packetQueue;
        this.fileUploadManagerThread = fileUploadManagerThread;
        this.uploadPolicy = uploadPolicy;

        // Let's construct the first packet...
        this.packetInProgress = new UploadFilePacket(this.uploadPolicy);
    }

    /**
     * The actual command to generate packets.
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    final public void run() {
        this.uploadPolicy.displayDebug("Start of PacketConstructionThread", 80);
        try { // catch (JUploadException e)

            // We loop, and wait for the 'poisonned' UploadFileData to finish.
            try {
                while (!lastFileReceived
                        && !this.fileUploadManagerThread.isUploadFinished()) {
                    UploadFileData ufd = preparedFileQueue.take();
                    receiveNewFile(ufd);
                }

                this.uploadPolicy
                        .displayDebug(
                                "PacketConstructionThread: end of loop, the thread is about to finish",
                                30);

                // We may have some file left to send...
                if (this.packetInProgress.size() > 0) {
                    this.uploadPolicy
                            .displayDebug(
                                    "Last file received: the current packet is not empty, we send it",
                                    30);
                    sendCurrentPacket();
                }
            } catch (InterruptedException e) {
                this.uploadPolicy
                        .displayWarn("packetConstructionThread received InterruptedException, exiting");
            }

            // In standard mode, we should have no more file to manage. The
            // following test is meaningful only if the FilePreparationThread
            // has been finished before (otherwise, other files could enter the
            // queue after this test)
            if (!this.preparedFileQueue.isEmpty()) {
                if (!this.fileUploadManagerThread.isUploadFinished()) {
                    // Hum, hum. This should not happen.
                    this.uploadPolicy
                            .displayWarn("The preparedFileQueue is not empty, at the end of "
                                    + this.getClass().getName());
                }
                // This can happen, if we are interrupted while working. Let's
                // empty this.
                this.uploadPolicy
                        .displayDebug(
                                "The PacketConstructionThread is about to finish, but the preparedFileQueue is not empty. Let's clear it.",
                                30);
                while (!this.preparedFileQueue.isEmpty()) {
                    this.preparedFileQueue.poll();
                }
            }
        } catch (JUploadException e) {
            this.fileUploadManagerThread.setUploadException(e);
        } finally {
            // To properly finish the job, we send a 'poisonned' packet, so that
            // the FileUploadThread knows it's finished.
            try {
                this.packetQueue.put(new UploadFilePacketPoisonned(
                        this.uploadPolicy));
            } catch (InterruptedException e) {
                this.uploadPolicy
                        .displayWarn("packetConstructionThread received InterruptedException (while checking if packetQueue is empty), exiting");
            }
        }
        this.uploadPolicy.displayDebug("End of PacketConstructionThread", 80);
    }

    /**
     * Called when a new file is received
     * 
     * @param uploadFileData
     * @throws JUploadException
     * @throws InterruptedException
     */
    private void receiveNewFile(UploadFileData uploadFileData)
            throws JUploadException, InterruptedException {
        // Are we finished ?
        if (uploadFileData.isPoisonned()) {
            lastFileReceived = true;
            this.uploadPolicy
                    .displayDebug(
                            "Poisonned UploadFileData received, PacketContructionThread will exit normally",
                            50);
        } else {
            // We try to add the file to the current packet. If it doesn't work,
            // this packet is probably full. We sent it, and add the packet to
            // the new one.
            if (!this.packetInProgress.add(uploadFileData)) {
                // The packet was refused. We send the current one, and retry.
                this.uploadPolicy
                        .displayDebug(
                                "The file can't be added to the current packet. Let's send this packet first.",
                                80);
                sendCurrentPacket();
                if (!this.packetInProgress.add(uploadFileData)) {
                    throw new JUploadException(
                            "Could not add file to packet! (filename: "
                                    + uploadFileData.getFileName() + ")");
                }
            }

            // If the current packet is finished, we send it immediatly.
            if (this.packetInProgress.isFull()) {
                sendCurrentPacket();
            }
        }
    }

    private void sendCurrentPacket() throws InterruptedException {
        if (this.packetInProgress == null) {
            throw new java.lang.AssertionError(
                    this.getClass().getName()
                            + ".sendCurrentPacket(): this.packetInProgress may not be null");
        } else if (this.packetInProgress.size() == 0) {
            throw new java.lang.AssertionError(
                    this.getClass().getName()
                            + ".sendCurrentPacket(): this.packetInProgress.size() may not be 0");
        }
        // If a packet is ready, we post it into the relevant queue.
        this.packetQueue.put(packetInProgress);

        // And we start a new one.
        this.packetInProgress = new UploadFilePacket(this.uploadPolicy);
    }
}