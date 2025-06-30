package my;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.IOException;

public class LombokExamples {
    public static void main(String[] args) {

        // @Data автоматически создает конструктор, геттеры, сеттеры, методы equals(), hashCode() и toString()
        Person person = new Person();
        person.setName("Alex");
        person.setAge(22);
        person.setEmail("example@gmail.com");
        Person person2 = person;
        System.out.println(person.getName());
        System.out.println(person.equals(person2));
        System.out.println(person.getAge());
        System.out.println(person);


        //@RequiredArgsConstructor создаёт конструктор со всеми private final полями
        Car car = new Car("BMW", "7");

        //@AllArgsConstructor создаёт конструктор со всеми полями
        Book book = new Book("Viktor Pelevin", "KGBT+", 340, 500);

        //@Builder Автоматически создает внутренний статический класс-строитель с методами, соответствующими полям класса
        House house = House.builder()
                .address("some addres")
                .rooms(5)
                .area(25)
                .price(1000000000)
                .build();


        // @Getter и @Setter создают гетеры и сеттеры для полей над которыми стоят
        Product product = new Product();
        product.setPrice(1000000000);
        product.getName();
//        product.setName("Kolbasa");


        //@Accessors(chain = true) в сочетании с анотацией @Setter над классом, позволила при создании экземпляра вызывать сеттеры по цепочке, а не отдельно
        Configuration configuration = new Configuration()
                .setApiUrl("dsdsdsdsd")
                .setApiSecretToken("****")
                .setTimeout(5);

    }


    @Data
    public static class Person {
        private String name;
        private int age;
        private String email;
    }

    @RequiredArgsConstructor
    public static class Car {
        private final String brand;
        private final String model;
    }

    @AllArgsConstructor
    public static class Book {
        private String title;
        private String author;
        private int pages;
        private int price;
    }

    @Builder
    public static class House {
        private String address;
        private int rooms;
        private int area;
        private int price;
    }

    public static class Product {
        @Getter
        private String name;
        @Setter
        private int price;
    }

    @Accessors(chain = true)
    @Setter
    public static class Configuration {
        public String apiUrl;
        public String apiSecretToken;
        public int timeout;
    }

    //мы можем обмануть компилятор Java, заставив его обрабатывать проверенные исключения как непроверенные
    @SneakyThrows
    public static void throwSneakyIOExceptionUsingLombok() {
        throw new IOException("lombok sneaky");
    }
}
