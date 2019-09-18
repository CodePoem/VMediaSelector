package com.vdreamers.vmediaselector.sample.boxing;

import org.junit.Test;

public class BoxingDefaultGlideLoaderTest {

    @Test
    public void displayThumbnail() {
        Student s1 = new Student(1, "ha");
        Student s2 = new Student(2, "ha");
        System.out.println(s1.hashCode());
        System.out.println(s2.hashCode());
        System.out.println(s1.equals(s2));
    }

    class Student{

        int age;
        String name;

        public Student() {
        }

        public Student(int age, String name) {
            this.age = age;
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj){
            Student student = (Student) obj;
            return (student.name) == (this.name);
        }

        @Override
        public int hashCode(){
            return age;
        }
    }
}