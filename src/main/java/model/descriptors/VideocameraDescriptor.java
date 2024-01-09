package model.descriptors;


public class VideocameraDescriptor {
    private int numberOfPeople;

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

