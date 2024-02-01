package message;

import model.descriptors.videocamera.VideocameraDescriptor;

public class VideocameraDataMessage extends GenericMessage {
    private int numberOfPeople;

    public VideocameraDataMessage(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public VideocameraDataMessage(VideocameraDescriptor videocameraDescriptor) {
        this(videocameraDescriptor.getNumberOfPeople());
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    @Override
    public String toString() {
        return "VideocameraDataMessage{" + "numberOfPeople=" + numberOfPeople + '}';
    }
}
