# WeatherApp — Temperature delle Città Italiane

Una semplice e intuitiva web app per visualizzare le temperature medie giornaliere delle ultime due settimane in varie città italiane, grazie ai dati forniti da [Open-Meteo.com](https://open-meteo.com/).

Realizzata con Java (Spring Boot) e JavaScript, l’app è facilmente eseguibile tramite Docker.

## Funzionalità Principali

- Visualizzazione delle temperature medie degli ultimi 14 giorni
- Selezione tra diverse città italiane
- Grafico a barre chiaro e interattivo

## Avvio dell’Applicazione

### Requisiti
- Docker Desktop installato e attivo

### 1. Clona il Progetto

```bash
git clone https://github.com/leoagnese/weatherapp.git
```

### 2. Entra nella Cartella del Progetto

```bash
cd weatherapp
```

### 3. Avvia l’App con Docker Compose

```bash
docker-compose up --build
```

> Al primo avvio potrebbe richiedere alcuni minuti per scaricare tutte le dipendenze.

## Accesso all’Applicazione

Una volta avviata, apri il browser su:

```
http://localhost:8080
```

## Arresto dell’Applicazione

### Per interrompere l'app:
Nel terminale premi:

```
Ctrl + C
```

(su macOS: `Cmd + C`)

### Per rimuovere i container:

```bash
docker-compose down
```
