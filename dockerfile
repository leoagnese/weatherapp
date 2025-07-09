# Usa un'immagine base per la compilazione (OpenJDK con Maven)
FROM maven:3-openjdk-17 AS build

# Imposta la directory di lavoro all'interno del container
WORKDIR /app

# Copia il file pom.xml e scarica le dipendenze per velocizzare le build successive
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia il resto del codice sorgente
COPY src ./src

# Compila l'applicazione Spring Boot
RUN mvn clean install -DskipTests

# Usa un'immagine base più leggera per il runtime (OpenJDK JRE)
FROM openjdk:17-slim

# Crea un gruppo e un utente non-root
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

# Imposta la directory di lavoro
WORKDIR /app

# Copia il JAR compilato dalla fase di build
COPY --from=build /app/target/weatherapp-0.0.1-SNAPSHOT.jar app.jar

# Cambia il proprietario del JAR all'utente non-root
RUN chown appuser:appgroup app.jar

# Cambia utente per l'esecuzione dell'applicazione
USER appuser

# Espone la porta su cui l'applicazione Spring Boot sarà in ascolto
EXPOSE 8080

# Comando per eseguire l'applicazione
ENTRYPOINT ["java", "-jar", "app.jar"]
