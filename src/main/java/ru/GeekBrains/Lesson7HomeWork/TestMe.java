package ru.GeekBrains.Lesson7HomeWork;

public class TestMe {

    @BeforeSuite
    void beforeTest(){
        System.out.println("Выполняется метод с аннотацией @BeforeSuite");
    }

    @Test (priority = 1)
    void testThisMethod1(){
        System.out.println("Выполняется метод 'testThisMethod1' с приоритетом = 1");
    }

    @Test (priority = 8)
    void testThisMethod2(){
        System.out.println("Выполняется метод 'testThisMethod2' с приоритетом = 8");
    }

    @Test
    void testThisMethod3(){
        System.out.println("Выполняется метод 'testThisMethod3' с приоритетом по умолчанию");
    }

    @Test (priority = 3)
    void testThisMethod4(){
        System.out.println("Выполняется метод 'testThisMethod4' с приоритетом = 3");
    }

    @Test (priority = 10)
    void testThisMethod5(){
        System.out.println("Выполняется метод 'testThisMethod5' с приоритетом = 10");
    }

    @Test (priority = 8)
    void testThisMethod6(){
        System.out.println("Выполняется метод 'testThisMethod6' с приоритетом = 8");
    }

    @Test
    void testThisMethod7(){
        System.out.println("Выполняется метод 'testThisMethod7' с приоритетом по умолчанию");
    }

    @AfterSuite
    void afterTest(){
        System.out.println("Выполняется метод с аннотацией @AfterSuite");
    }
}
