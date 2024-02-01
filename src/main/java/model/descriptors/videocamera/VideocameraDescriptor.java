package model.descriptors.videocamera;


public class VideocameraDescriptor {
    private int numberOfPeople;

    public VideocameraDescriptor(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public VideocameraDescriptor() {
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    @Override
    public String toString() {
        return "VideocameraDescriptor{" +
                "numberOfPeople=" + numberOfPeople +
                '}';
    }
}

