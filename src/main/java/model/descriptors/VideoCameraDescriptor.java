package model.descriptors;


public class VideoCameraDescriptor {
    private String id;
    private int numberOfPeople;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    @Override
    public String toString() {
        return "VideoCameraDescriptor{" +
                "id='" + id + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                '}';
    }
}

