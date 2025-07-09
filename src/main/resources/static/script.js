document.addEventListener('DOMContentLoaded', () => {
    const citySelect = document.getElementById('city-select');
    const fetchButton = document.getElementById('fetch-button');
    const chartCanvas = document.getElementById('temperature-chart');
    const messageBox = document.getElementById('message-box');
    const loadingSpinner = document.getElementById('loading-spinner');
    let temperatureChart; // Variabile per l'istanza del grafico Chart.js

    // Dati delle città con latitudini e longitudini (hardcoded per semplicità)
    const cities = [
        { name: 'Roma', latitude: 41.90, longitude: 12.48 },
        { name: 'Milano', latitude: 45.46, longitude: 9.19 },
        { name: 'Napoli', latitude: 40.85, longitude: 14.27 },
        { name: 'Firenze', latitude: 43.77, longitude: 11.25 },
        { name: 'Venezia', latitude: 45.44, longitude: 12.33 },
        { name: 'Torino', latitude: 45.07, longitude: 7.69 },
        { name: 'Bologna', latitude: 44.50, longitude: 11.34 },
        { name: 'Genova', latitude: 44.41, longitude: 8.93 },
        { name: 'Palermo', latitude: 38.11, longitude: 13.36 },
        { name: 'Bari', latitude: 41.11, longitude: 16.87 }
    ];

    /**
     * Popola il dropdown delle città.
     */
    function populateCitySelect() {
        cities.forEach(city => {
            const option = document.createElement('option');
            option.value = `${city.latitude},${city.longitude}`;
            option.textContent = city.name;
            citySelect.appendChild(option);
        });
    }

    /**
     * Inizializza il grafico Chart.js con dati vuoti.
     */
    function initializeChart() {
        const ctx = chartCanvas.getContext('2d');
        temperatureChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: [],
                datasets: [{
                    label: 'Temperatura Media Giornaliera (°C)',
                    data: [],
                    backgroundColor: 'rgba(59, 130, 246, 0.7)', // blue-500 con opacità
                    borderColor: 'rgba(59, 130, 246, 1)',
                    borderWidth: 1,
                    borderRadius: 8 // Angoli arrotondati per le barre
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false, // Permette al grafico di adattarsi al contenitore
                plugins: {
                    legend: {
                        display: true,
                        labels: {
                            font: {
                                size: 14,
                                family: 'Inter'
                            },
                            color: '#374151' // gray-700
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return context.dataset.label + ': ' + context.parsed.y + '°C';
                            }
                        },
                        titleFont: {
                            family: 'Inter'
                        },
                        bodyFont: {
                            family: 'Inter'
                        }
                    }
                },
                scales: {
                    x: {
                        title: {
                            display: true,
                            text: 'Data',
                            font: {
                                size: 16,
                                family: 'Inter',
                                weight: 'bold'
                            },
                            color: '#374151'
                        },
                        grid: {
                            display: false // Rimuove le griglie verticali
                        },
                        ticks: {
                            font: {
                                family: 'Inter'
                            },
                            color: '#4b5563', // gray-600
                            // Formatta le date per l'asse X
                            callback: function(value, index, ticks) {
                                const date = new Date(this.getLabelForValue(value));
                                return date.toLocaleDateString('it-IT', { day: '2-digit', month: '2-digit' });
                            }
                        }
                    },
                    y: {
                        title: {
                            display: true,
                            text: 'Temperatura (°C)',
                            font: {
                                size: 16,
                                family: 'Inter',
                                weight: 'bold'
                            },
                            color: '#374151'
                        },
                        beginAtZero: false, // Le temperature possono essere negative
                        grid: {
                            color: '#e5e7eb' // gray-200
                        },
                        ticks: {
                            font: {
                                family: 'Inter'
                            },
                            color: '#4b5563', // gray-600
                            callback: function(value) {
                                return value + '°C';
                            }
                        }
                    }
                },
                animation: {
                    duration: 1000, // Durata dell'animazione in ms
                    easing: 'easeOutQuart' // Tipo di easing
                }
            }
        });
    }

    /**
     * Aggiorna il grafico con i nuovi dati.
     * @param {Array<string>} dates Le etichette delle date.
     * @param {Array<number>} temperatures I valori delle temperature.
     */
    function updateChart(dates, temperatures) {
        if (temperatureChart) {
            temperatureChart.data.labels = dates;
            temperatureChart.data.datasets[0].data = temperatures;
            temperatureChart.update();
        }
    }

    /**
     * Mostra un messaggio all'utente.
     * @param {string} message Il messaggio da visualizzare.
     * @param {boolean} isError Indica se il messaggio è un errore.
     */
    function showMessageBox(message, isError = false) {
        messageBox.textContent = message;
        messageBox.classList.remove('hidden');
        messageBox.classList.add('flex');
        if (isError) {
            messageBox.classList.remove('text-gray-700');
            messageBox.classList.add('text-red-600');
        } else {
            messageBox.classList.remove('text-red-600');
            messageBox.classList.add('text-gray-700');
        }
        chartCanvas.classList.add('hidden'); // Nasconde il canvas quando il messaggio è visibile
        loadingSpinner.classList.add('hidden'); // Nasconde lo spinner
    }

    /**
     * Nasconde il messaggio e mostra il grafico.
     */
    function hideMessageBox() {
        messageBox.classList.add('hidden');
        messageBox.classList.remove('flex');
        chartCanvas.classList.remove('hidden'); // Mostra il canvas
        loadingSpinner.classList.add('hidden'); // Nasconde lo spinner
    }

    /**
     * Mostra lo spinner di caricamento.
     */
    function showLoadingSpinner() {
        loadingSpinner.classList.remove('hidden');
        loadingSpinner.classList.add('flex');
        messageBox.classList.add('hidden'); // Nasconde il messaggio
        chartCanvas.classList.add('hidden'); // Nasconde il canvas
        fetchButton.disabled = true; // Disabilita il pulsante durante il caricamento
    }

    /**
     * Nasconde lo spinner di caricamento.
     */
    function hideLoadingSpinner() {
        loadingSpinner.classList.add('hidden');
        loadingSpinner.classList.remove('flex');
        fetchButton.disabled = false; // Riabilita il pulsante
    }

    // Popola il dropdown all'avvio
    populateCitySelect();
    // Inizializza il grafico (vuoto all'inizio)
    initializeChart();
    // Mostra il messaggio iniziale
    showMessageBox("Seleziona una città e clicca 'Mostra Grafico' per visualizzare i dati.");

    // Aggiungi l'event listener al bottone
    fetchButton.addEventListener('click', async () => {
        const selectedValue = citySelect.value;
        if (!selectedValue) {
            showMessageBox("Per favore, seleziona una città.", true);
            return;
        }

        const [latitude, longitude] = selectedValue.split(',').map(Number);

        showLoadingSpinner(); // Mostra lo spinner di caricamento

        try {
            // Effettua la richiesta al backend Java
            const response = await fetch(`/api/temperatures?latitude=${latitude}&longitude=${longitude}`);
            const data = await response.json(); // Anche in caso di errore HTTP, prova a leggere il JSON

            if (!response.ok) {
                // Se la risposta non è OK (es. 4xx, 5xx), usa il messaggio di errore dal backend se disponibile
                const errorMessage = data.error || `Errore HTTP: ${response.status} - Impossibile recuperare i dati.`;
                throw new Error(errorMessage);
            }

            if (data.dates && data.temperatures && data.dates.length > 0) {
                hideMessageBox(); // Nasconde il messaggio e mostra il grafico
                updateChart(data.dates, data.temperatures);
            } else {
                showMessageBox("Nessun dato di temperatura trovato per la città selezionata.", false);
            }

        } catch (error) {
            console.error('Errore nel recupero dei dati:', error);
            showMessageBox(`Impossibile recuperare i dati: ${error.message}.`, true);
        } finally {
            hideLoadingSpinner(); // Nasconde lo spinner alla fine, indipendentemente dal successo/fallimento
        }
    });

    // Gestione della responsività del canvas
    window.addEventListener('resize', () => {
        if (temperatureChart) {
            temperatureChart.resize();
        }
    });
});
