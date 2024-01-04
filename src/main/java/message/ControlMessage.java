package message;

public class ControlMessage<T> {

    private long timestamp;
    private String type;
    private T data;

    public ControlMessage() {

    }

    public ControlMessage(String type, T data) {
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.data = data;
    }

    public ControlMessage(long timestamp, String type, T data) {
        this.timestamp = timestamp;
        this.type = type;
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ControlMessage{" + "timestamp=" + timestamp + ", type='" + type + '\'' + ", data=" + data + '}';
    }
}
