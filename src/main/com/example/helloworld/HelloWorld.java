package main.com.example.helloworld;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;


public final class HelloWorld {

  private static boolean getBoolFromCode(Person person){
    return Optional.ofNullable(person.getLiteracy())
            .map(Literacy::getCode)
            .equals(15L);
  }

  private static boolean getBoolFromCodeWithIf(Person person){
    Literacy personLiteracy = person.getLiteracy();

    if (personLiteracy == null){
      return false;
    }

    return personLiteracy.getCode() == 15L;
  }

  public static void main(String[] args) {
    List<Person> list = new ArrayList<>();
    for (int i = 0; i < 10000000; i++) {
      Literacy literacy = null;
      if (i % 2 == 0){
        literacy = new Literacy("Literacy", ThreadLocalRandom.current().nextLong(12, 18));
      }
      if (i % 5 == 0){
        literacy = new Literacy("Literacy", ThreadLocalRandom.current().nextLong(0, 100));
      }
      list.add(new Person("Name_" + i, i, literacy));
    }

    System.out.println("--------------- Starting Optional run -------------------");
    long start = System.nanoTime();
    for (Person person : list){
      getBoolFromCode(person);
    }
    System.out.println("Total time: " + (System.nanoTime() - start));

    System.out.println("--------------------Starting IF run -------------------");
    start = System.nanoTime();
    for (Person person : list){
      getBoolFromCodeWithIf(person);
    }
    System.out.println("Total time: " + (System.nanoTime() - start));
  }
}
