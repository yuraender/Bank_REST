# Система управления банковскими картами

Backend-приложение на Java Spring Boot для управления банковскими картами.

## 📋 Функциональность

### 🔐 Аутентификация и авторизация

- Spring Security с JWT-токенами
- Две роли пользователей: `ADMIN` и `USER`
- Защищенные эндпоинты с ролевым доступом

### 👨‍💼 Возможности администратора

- Создание, блокировка, активация и удаление карт
- Управление пользователями
- Просмотр всех карт в системе

### 👤 Возможности пользователя

- Просмотр своих карт с поиском и пагинацией
- Запрос блокировки карты
- Переводы между своими картами
- Просмотр баланса

## 🛠 Технологии

- **Java 17**
- **Spring Boot 3.5.5**
- **Spring Security** + JWT
- **Spring Data JPA**
- **MySQL**
- **Liquibase** для миграций
- **Swagger/OpenAPI** для документации
- **JUnit** и **Mockito** для тестирования
- **Docker Compose**

## 🚀 Запуск приложения

### Предварительные требования

- Java 17 или выше
- Maven 3.6+
- Docker и Docker Compose
- MySQL (если запускается без Docker)

### Способ 1: Запуск с помощью Docker Compose (рекомендуемый)

```bash
git clone <repository-url>
cd Bank_REST
docker-compose up -d
```

## Переменные окружения

| Переменная  | Описание            | Значение по умолчанию                                                   |
|-------------|---------------------|-------------------------------------------------------------------------|
| SERVER_PORT | Порт сервера        | 8080                                                                    |
| DB_URL      | URL базы данных     | jdbc:mysql://localhost:3306/bank?useUnicode=yes&characterEncoding=UTF-8 |
| DB_USERNAME | Имя пользователя БД | bank_user                                                               |
| DB_PASSWORD | Пароль БД           | bank_password                                                           |

Приложение будет доступно по адресу: http://localhost:8080

### Способ 2: Локальный запуск

1. Создайте базу данных MySQL

```sql
CREATE DATABASE bank;
CREATE USER bank_user WITH PASSWORD 'bank_password';
GRANT ALL PRIVILEGES ON DATABASE bank_card_db TO bank_user;
```

2. Настройте подключение к БД в `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bank
spring.datasource.username=user
spring.datasource.password=password
```

3. Соберите и запустите приложение:

```bash
mvn clean package
java -jar target/bankcards-1.0.0.jar
```

## 📊 API Документация

После запуска приложения документация API доступна по адресу:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI спецификация: http://localhost:8080/api-docs
