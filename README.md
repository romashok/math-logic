# Домашние задания по курсу математичесой логики
Описание заданий можно прочитать [здесь](https://github.com/shd/logic2015/blob/master/homework.pdf).
### Зависимости 
* [jdk 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [maven](https://maven.apache.org/)

### Сборка
Выполнить ```mvn package``` . 

### Запуск
Выполнять в корневой папке проекта. Доступно 1-3,5 дз.

```java -jar target/math-logic-1.0-SNAPSHOT-jar-with-dependencies.jar <номер дз> <входной файл> <выходной файл>```

### Тесты 
Код покрыт тестами, преимущественно взятыми [отсюда](https://github.com/shd/logic2014/tree/master/tests).

Запускаются командой ```mvn test``` .
