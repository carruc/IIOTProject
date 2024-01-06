package message;

import model.descriptors.wristband.PersonDataDescriptor;

public class PersonDataMessage extends GenericMessage{
    private String CF;
    private String name;
    private String lastname;
    private int age;
    private int roomNumber;
    private String wristbandId;

    public PersonDataMessage(){

    }

    public PersonDataMessage(String CF, String name, String lastname, int age, int roomNumber, String wristbandId) {
        this.CF = CF;
        this.name = name;
        this.lastname = lastname;
        this.age = age;
        this.roomNumber = roomNumber;
        this.wristbandId = wristbandId;
    }

    public PersonDataMessage(PersonDataDescriptor personDataDescriptor){
        this(personDataDescriptor.getCF(), personDataDescriptor.getName(), personDataDescriptor.getLastname(),
                personDataDescriptor.getAge(), personDataDescriptor.getRoomNumber(), personDataDescriptor.getWristbandId());
    }

    public String getCF() {
        return CF;
    }

    public void setCF(String CF) {
        this.CF = CF;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getWristbandId() {
        return wristbandId;
    }

    public void setWristbandId(String wristbandId) {
        this.wristbandId = wristbandId;
    }
}
