package main.com.example.helloworld;

public class Person {
    String name;
    int age;
    Literacy literacy;

    public Person(String name, int age, Literacy literacy){
        this.name = name;
        this.age = age;
        this.literacy = literacy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Literacy getLiteracy() {
        return literacy;
    }

    public void setLiteracy(Literacy literacy) {
        this.literacy = literacy;
    }
}
