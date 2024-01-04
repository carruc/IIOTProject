package model.descriptors.wristband;

public class PersonDataDescriptor {
    String CF;
    String name;
    String lastname;
    int age;

    public PersonDataDescriptor(String CF, String name, String lastname, int age) {
        this.CF = CF;
        this.name = name;
        this.lastname = lastname;
        this.age = age;
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

    @Override
    public String toString() {
        return "PersonDataDescriptor{" + "CF='" + CF + '\'' + ", name='" + name + '\'' + ", lastname='" + lastname + '\'' + ", age=" + age + '}';
    }
}
