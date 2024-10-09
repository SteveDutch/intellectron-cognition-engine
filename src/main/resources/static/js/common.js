// Aktuelle URL abrufen
const url = window.location.href;

function getValueAfterEquals(url) {
    const equalsIndex = url.lastIndexOf('=');
    if (equalsIndex !== -1 && equalsIndex < url.length - 1) {
        return url.substring(equalsIndex + 1);
    }
}
// Hostnamen in das HTML-Element einfügen
document.getElementById('searchTerm').textContent = getValueAfterEquals(url);
 

