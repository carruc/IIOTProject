package model.descriptors.videocamera;

public class VideocameraInfoDescriptor {
    private int roomNumber;
    private String cameraId;

    public VideocameraInfoDescriptor() {
    }

    public VideocameraInfoDescriptor(int roomNumber, String cameraId) {
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
        return "VideocameraInfoDescriptor{" +
                "roomNumber=" + roomNumber +
                ", cameraId='" + cameraId + '\'' +
                '}';
    }
}
