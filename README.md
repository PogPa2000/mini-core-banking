\# Mini Core Banking



Backend service for a Mini Core Banking system.



\## Tech Stack



\* Java 26

\* Spring Boot 4.1.1

\* Spring Data JPA

\* Hibernate

\* PostgreSQL

\* Flyway

\* Maven



\## Features



\* Customer Management

\* Customer Create / Update / Query

\* Database Migration with Flyway

\* Transaction Management

\* Audit Logging

\* API Logging

\* Sensitive Data Protection



\## Project Structure



```text

src/

├── main/

│   ├── java/

│   │   └── ...

│   └── resources/

│       ├── db/

│       │   └── migration/

│       └── application.yml

└── test/

```



\## Database



The application uses PostgreSQL.



Database schema:



```text

core

```



Flyway migrations are located at:



```text

src/main/resources/db/migration

```



\## Run the Application



\### 1. Clone



```bash

git clone https://github.com/PogPa2000/mini-core-banking.git

cd mini-core-banking

```



\### 2. Configure Database



Update the database configuration in `application.yml`.



\### 3. Run



```bash

./mvnw spring-boot:run

```



On Windows:



```powershell

.\\mvnw.cmd spring-boot:run

```



\## Git Workflow



```bash

git add .

git commit -m "feat: description"

git push

```



