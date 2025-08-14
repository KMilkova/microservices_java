# User Management & Notification System

Система состоит из двух микросервисов: AUTH и NOTIFICATION. AUTH отвечает за регистрацию, авторизацию и управление пользователями (CRUD). NOTIFICATION отправляет уведомления на email при создании, изменении или удалении пользователей. Проект использует Java, Spring Boot, Spring Security, Hibernate, Spring Data JPA, Flyway, MySQL, Docker. JWT используется для аутентификации, роли USER и ADMIN определяют права доступа. USER может изменять только свой аккаунт, ADMIN имеет полный доступ.

Для запуска проекта необходимо установить Docker и Docker Compose. 
Клонируйте репозиторий и создайте в корне проекта файл .env на основе примера нижу. 
В .env необходимо указать свои данные подключения к базе и параметры SMTP для отправки почты. Пример значений:

# Настройки MySQL
DB_URL=jdbc:mysql://mysql-db:3306/users?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=root_password

# Настройки почты (SMTP)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_email_password

После подготовки .env выполните команду:

docker-compose up --build

Это соберет и запустит все сервисы. AUTH-сервис будет доступен по адресу http://localhost:8080, NOTIFICATION-сервис — по адресу http://localhost:8081. 
MySQL будет доступна на localhost:3307 с учетными данными из .env.

Для остановки сервисов используйте:

docker-compose down

Чтобы удалить данные базы, используйте:

docker-compose down -v

Все миграции базы данных выполняются автоматически через Flyway при старте AUTH-сервиса. В базе будут созданы все необходимые таблицы и добавлены тестовые данные. 
AUTH-сервис реализует REST CRUD для пользователей с разделением ролей USER и ADMIN, а NOTIFICATION-сервис отправляет уведомления на почту при создании, изменении или удалении пользователей.
Перед запуском необходимо создать файл .env в корне проекта и указать в нём значения:


Настройка и запуск:

application.yml для AUTH:

spring:
  datasource:
    url: jdbc:mysql://mysql:3306/users?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: Kmill192001@
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: validate      
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
flyway:
  enabled: true
  locations: classpath:db/migration
  baseline-on-migrate: true
logging:
  level:
    org.springframework: DEBUG
token:
  signing:
    key: 53A73E5F1C4E0A2D3B5F2D784E6A1B423D6F247D1F6E5C3A596D635A75327855
server:
  port: 8080

application.yml для NOTIFICATION:

spring:
  mail:
    host: ${SPRING_MAIL_HOST}
    port: ${SPRING_MAIL_PORT}
    username: ${SPRING_MAIL_USERNAME_FILE}
    password: ${SPRING_MAIL_PASSWORD_FILE}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
logging:
  level:
    org.springframework.mail: DEBUG

Пример Flyway миграции для AUTH:

CREATE TABLE user (
id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
username NVARCHAR(50) NOT NULL,
password NVARCHAR(255) NOT NULL,
email NVARCHAR(50),
firstName NVARCHAR(50),
lastName NVARCHAR(50),
role NVARCHAR(10) NOT NULL
);

INSERT INTO user (username, password, email, firstName, lastName, role) VALUES
('admin', '$2a$10$7QKf2x1bRZ0Q6MvSgk1vOe2LxvZpQWm3zH3q1mO2gJk9PqfZ0aB5K', 'admin@example.com', 'Admin', 'User', 'ROLE_ADMIN'),
('user1', '$2a$10$u1kKzj9QvY2s4HfZg7mR6e4tXh6vCjT1pM8bD3qW2sLxN9fJ0hK1O', 'user1@example.com', 'John', 'Doe', 'ROLE_USER'),
('user2', '$2a$10$Zg5HkP1vS6nF2qT9eR8bXj0cYh3uM7lD5vN1sQ2fK8wL0pH6tJ3G', 'user2@example.com', 'Jane', 'Smith', 'ROLE_USER'),
('user3', '$2a$10$F3jK9vL2bN5qT7rY8mH0cXj1sP6wV2eM4zD9fR3gK1nB8oH5tL0Q', 'user3@example.com', 'Mike', 'Johnson', 'ROLE_USER'),
('user4', '$2a$10$W0pH5tL0Q3jK9vL2bN5qT7rY8mH0cXj1sP6wV2eM4zD9fR3gK1nB8o', 'user4@example.com', 'Emily', 'Davis', 'ROLE_USER'),
('user5', '$2a$10$K8wL0pH6tJ3GW0pH5tL0Q3jK9vL2bN5qT7rY8mH0cXj1sP6wV2eM4', 'user5@example.com', 'David', 'Wilson', 'ROLE_USER');

Flyway автоматически создаёт таблицы и вставляет тестовые данные при старте AUTH-сервиса.

Сборка проекта:

mvn clean install

Сборка и запуск через Docker Compose:

docker-compose up --build

REST API AUTH:

POST /api/auth/sign-up — регистрация нового пользователя  
POST /api/auth/sign-in — авторизация и получение JWT  
GET /api/users/all — получить всех пользователей (ADMIN)  
GET /api/users/info/{id} — получить пользователя по ID  
PUT /api/users/update/{id} — обновить данные пользователя  
DELETE /api/users/delete/{id} — удалить пользователя

REST API NOTIFICATION:

POST /api/notifications/user-event - отправка письма

Пример запроса с JWT:

curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/api/users/info/1

Взаимодействие микросервисов:

AUTH отправляет события (создание/изменение/удаление пользователя) в NOTIFICATION через REST. NOTIFICATION рассылает письма всем ADMIN пользователям.

Логирование:

Все операции CRUD и авторизации логируются через SLF4J/Logback с MDC для идентификации сервиса.

Принципы:

Код соблюдает SOLID, роли разделены в Spring Security, миграции схемы управляются Flyway, 
Docker Compose обеспечивает развёртку всей системы.
