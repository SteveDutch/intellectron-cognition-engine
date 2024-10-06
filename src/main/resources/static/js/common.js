// Aktuelle URL abrufen
const url = window.location.href;
//
//// URL in Teile zerlegen
//const urlParts = new URL(url);
//
//// Hostnamen extrahieren
//const searchTerm = urlParts.search;

// Hostnamen in das HTML-Element einfügen
document.getElementById('searchTerm').textContent = getValueAfterEquals(url);


function getValueAfterEquals(url) {
    const equalsIndex = url.lastIndexOf('=');
    if (equalsIndex !== -1 && equalsIndex < url.length - 1) {
        return url.substring(equalsIndex + 1);
    }
}

