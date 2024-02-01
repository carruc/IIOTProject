package message;

import model.descriptors.videocamera.VideocameraInfoDescriptor;

public class VideocameraInfoMessage extends GenericMessage {
    private int roomNumber;
    private String cameraId;

    public VideocameraInfoMessage() {
        super();
    }

    public VideocameraInfoMessage(VideocameraInfoDescriptor videocameraInfoDescriptor) {
        this(videocameraInfoDescriptor.getRoomNumber(), videocameraInfoDescriptor.getCameraId());
    }

    public VideocameraInfoMessage(int roomNumber, String cameraId) {
        this.roomNumber = roomNumber;
        this.cameraId = cameraId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    @Override
    public String toString() {
        return "VideocameraInfoMessage{" + "roomNumber=" + roomNumber + ", cameraId='" + cameraId + '\'' + '}';
    }
}
