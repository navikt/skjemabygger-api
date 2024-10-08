forms-api
================

Skjemadefinisjoner, oversettelser og mottaksadresser.

# Komme i gang

`mvn clean install`

Du må ha Docker kjørende for å kjøre testene og for å starte applikasjonen.

Hvis du bruker colima kan det være nødvendig å sette følgende env-variabler for at testcontainers skal fungere:

      export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
      export DOCKER_HOST="unix://${HOME}/.colima/docker.sock"

For flere detaljer, se
[Customizing Docker host detection](https://java.testcontainers.org/features/configuration/#customizing-docker-host-detection)
i dokumentasjonen til [Testcontainers](https://java.testcontainers.org/).

## Kjøre opp applikasjonen lokalt

Det finnes ulike muligheter for hvordan man kan starte applikasjonen lokalt.

### 1. Spring Boot med profil `local`

Kjøre `no/nav/forms/FormsApiApplication.kt` med profil `local`, f.eks. i IntelliJ.

Dette er den raskeste måten, men med profil `local` brukes en inmemory database, så data mistes ved restart.

### 2. Docker compose up

`docker compose up --build`

Her må man kjøre `mvn install` og kjøre kommandoen over på nytt for å få med endringer. Fordelen er at man får
testet docker image for applikasjonen.

### 3. Spring Boot med profil `docker`

Dette er en hybrid måte hvor applikasjonen kobler seg til database som kjører i en docker container. Data i databasen
overlever restart av applikasjonen.

Starte databasen med docker compose:

`docker compose up db`

Deretter kjøres `no/nav/forms/FormsApiApplication.kt` med profil `docker` og `DATABASE_PORT=5442`.

## URLer lokalt

Swagger url: http://localhost:8082/swagger-ui/index.html

OpenAPI descriptions: http://localhost:8082/v3/api-docs

---

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #team-fyllut-sendinn
