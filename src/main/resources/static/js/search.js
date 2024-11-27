/**
 * 
 */

/* fetch('/api/tags/findByText?tagText=example')
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text);
            });
        }
        return response.json();
    })
    .then(data => {
        // Verarbeiten Sie die Daten hier
    })
    .catch(error => {
        console.error('Fehler beim Abrufen des Tags:', error.message);
        // Zeigen Sie eine Fehlermeldung im Frontend an
    });
 */

	document.addEventListener('DOMContentLoaded', () => {
	    const totalImages = 8;
	    const randomNumber = Math.floor(Math.random() * totalImages) + 1;
		console.log(randomNumber);
	    document.querySelector("section.search").style.backgroundImage = `url('images/${randomNumber}.jpg')`;
	});