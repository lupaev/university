### University Management

#### Описание

University Management System — это приложение для управления группами и студентами в университете. Оно позволяет 
добавлять, редактировать и удалять группы и студентов, а также просматривать списки студентов в каждой группе с 
поддержкой пагинации.

#### Стек технологий

* Backend: Java 17, Spring Boot 3, Spring Data JPA, Hibernate
* Frontend: Vaadin
* База данных: MySQL
* Миграции базы данных: Liquibase
* Сборка: Gradle

#### Установка и запуск

1. Клонирование репозитория:
```bash
git clone http://github.com/lupaev/university.git
cd university
```

2. Запуск с помощью Docker Compose:
```bash
docker compose up -d
```

3. Доступ к приложению:
   Перейдите на страницу http://localhost:8081 в вашем браузере.


4. Остановка приложения и повторный запуск
```bash
docker-compose stop && docker-compose up -d
```