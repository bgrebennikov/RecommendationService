
# Сервис рекомендаций банковских продуктов «Стар» (MVP)

Проект представляет собой минимально жизнеспособный продукт (MVP) рекомендательной системы для банка «Стар». Сервис анализирует исторические данные пользователей, типы используемых ими продуктов и финансовые транзакции для формирования персонализированных маркетинговых предложений через REST API.

## 🛠️ Технологический стек

-   **Java 17**

-   **Spring Boot 4.1.0**

-   **PostgreSQL 15** — реляционная СУБД для хранения динамических правил и их условий

-   **H2 Database** — встраиваемая файловая БД в режиме Read-Only (хранение историй транзакций)

-   **Redis 7** — кэширование правил рекомендаций с авто-сбросом при изменении

-   **Liquibase** — версионирование и автоматическое управление миграциями схемы БД

-   **MapStruct 1.6.3** — генерация мапперов между сущностями БД и DTO

-   **SpringDoc OpenAPI 3.0.3 (Swagger)** — интерактивная документация REST API

-   **Docker & Docker Compose** — контейнеризация и оркестрация

-   **JUnit 5 & Mockito & Testcontainers & `@WebMvcTest`** — модульное, компонентное и интеграционное тестирование


## Архитектура и структура проекта

Приложение спроектировано по слоям (Layered Architecture) с соблюдением принципов SOLID:

```
com.github.bgrebennikov.recommendationservice
├── config
│   ├── PostgresDataSourceConfiguration       # Конфигурация подключения к PostgreSQL
│   ├── RecommendationsDataSourceConfiguration# Конфигурация подключения к H2 (транзакции)
│   └── RedisCacheConfig                      # Конфигурация кэширования в Redis
├── controller
│   ├── RecommendationController              # GET /recommendation/{userId}
│   └── RuleController                        # GET, POST, DELETE /rule
├── data
│   ├── dto
│   │   ├── recommendation                    # DTO для рекомендационных ответов
│   │   └── rule                              # DTO для создания и получения правил
│   ├── persistence
│   │   ├── RuleEntity                        # Сущность правила
│   │   └── RuleQueryEntity                   # Сущность условий правила
│   ├── types                                 # Перечисления (ComparisonOperator, ProductType, TransactionType)
│   └── RuleMapper                            # MapStruct-маппер
├── repository
│   ├── RecommendationRepository              # Доступ к транзакционным данным
│   └── RuleRepository                        # JPA-доступ к правилам (с JOIN FETCH)
├── rule
│   ├── dynamic                               # Динамические правила
│   ├── statics                               # Статические правила
│   ├── RecommendationDynamicRuleSet          # Интерфейс набора динамических правил
│   └── RecommendationRuleSet                 # Интерфейс набора статических правил
└── service
    ├── RecommendationService                 # Логика формирования рекомендаций
    └── RuleService                           # Управление правилами и кэшированием

```

## ⚙️ Переменные окружения (Environment Variables)

Сервис конфигурируется с помощью следующих переменных окружения. Если переменная не задана, используется значение по умолчанию:

Переменная окружения

|Переменная окружения| Значение по умолчанию | Описание |
|--|--|--|
| `DB_HOST` | `localhost` | Хост основной базы данных PostgreSQL |
| `DB_PORT` | `5432` | Порт базы данных PostgreSQL |
| `DB_NAME` | `recommendations` | Имя базы данных PostgreSQL |
| `DB_LOGIN` | `postgres` | Логин пользователя PostgreSQL |
| `DB_PASSWORD` | `postgres` | Пароль пользователя PostgreSQL |
| `REDIS_HOST` | `localhost` | Хост сервера Redis |
| `REDIS_PORT` | `6379` | Порт сервера Redis |
| `REDIS_PASSWORD` | *(пусто)* | Пароль подключения к Redis |
| `REDIS_CACHE_TTL` | `600000` | Время жизни кэша Redis в миллисекундах (по умолчанию 10 минут) |
##  Инструкция по развертыванию

### Предварительные требования

-   **JDK 17** (для локальной сборки и запуска)

-   **Docker** (v20.10+) и **Docker Compose** (v2.x+)

-   Свободные порты на хосте: `8080` (приложение), `5433` / `5432` (PostgreSQL), `6379` (Redis)


### 1. Запуск через Docker Compose (Рекомендуемый)

Сборка приложения и запуск всех сервисов (PostgreSQL + Redis + Spring Boot) одной командой:

```
docker compose up --build -d

```

При запуске Liquibase автоматически накатит миграции из `classpath:db/changelog/db.changelog-master.yaml` на PostgreSQL.

#### Проверка статуса контейнеров:

```
docker compose ps

```

#### Просмотр логов приложения:

```
docker compose logs -f recommendation-service

```

### 2. Локальный запуск (Сборка JAR и запуск на ПК)

Для запуска собранного JAR-файла непосредственно на ПК:

1.  **Запустите необходимые зависимости (PostgreSQL и Redis):**

    ```
    docker compose up postgres-db redis -d
    
    ```

2.  **Соберите JAR-файл приложения с помощью Gradle Wrapper:**

    ```
    ./gradlew bootJar
    
    ```

3.  **Запустите собранный JAR-файл:**

    ```
    java -jar build/libs/recommendation-service-0.0.1-SNAPSHOT.jar
    
    ```


_(Примечание: Убедитесь, что имя JAR-файла в `build/libs/` соответствует вашей версии сборки)._

## 🔗 Полезные эндпоинты

-   **Swagger UI (Документация API):** http://localhost:8080/swagger-ui.html

-   **OpenAPI Spec:** http://localhost:8080/v3/api-docs

-   **Получение рекомендаций:** `GET /recommendation/{userId}`

-   **Управление правилами:**

    -   `GET /rule` — получение всех правил (кэшируется в Redis)

    -   `POST /rule` — создание правила (сбрасывает кэш)

    -   `DELETE /rule/{id}` — удаление правила по UUID (сбрасывает кэш)