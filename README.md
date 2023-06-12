# Share it
### Сервис для шеринга вещей. 
Позволяет пользователям сдавать и брать друг у друга в аренду вещи и инструменты.

реализованы возможности создания пользователей, добавления вещей, выставления или снятия возможности брать вещь в аренду, текстовый поиск по названию и описанию вещи, контроль времени аренды, не допускающий возможности сдать одну вещь в аренду нескольким пользователям одновременно в один период времени.

Стек используемых технологий:
- Java 11
- Spring Boot
- Hibernate
- PostgreSQL
- Maven
- Docker


сборка проекта с помощью maven
```
mvn package
```

сборка и запуск контейнеров с помощью docker-compose
```
docker-compose up --build
```
