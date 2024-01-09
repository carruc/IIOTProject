package message;

public abstract class GenericMessage {
    private long timestamp;

    public GenericMessage(){
        timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
}
