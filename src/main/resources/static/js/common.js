// Aktuelle URL abrufen
const url = window.location.href;

// URL in Teile zerlegen
const urlParts = new URL(url);

// Hostnamen extrahieren
const searchTerm = urlParts.search;

// Hostnamen in das HTML-Element einfügen
document.getElementById('searchTerm').textContent = searchTerm;