// Aktuelle URL abrufen
const url = window.location.href;

function getValueAfterEquals(url) {
    const equalsIndex = url.lastIndexOf('=');
    if (equalsIndex !== -1 && equalsIndex < url.length - 1) {
        return url.substring(equalsIndex + 1);
    }
}
// Suchbegriff in das HTML-Element einfügen
let searchTerm = document.getElementById('searchTerm');
if (searchTerm) {
    searchTerm.textContent = getValueAfterEquals(url);
} else {
    console.log("Element with ID 'searchTerm' not found. Probably no result page shown");
}
// document.getElementById('searchTerm').textContent = getValueAfterEquals(url);
 
	function getValueBeforeEquals(url) {
		try {
			const pathname = new URL(url).pathname;
			const segments = pathname.split('/').filter(Boolean);
			if (segments[1]) {
				return segments[1];
			}
		} catch (e) {
			console.error('Error while parsing URL:', e);
		}
	}
	
		let searchedVia = document.getElementById('searchedVia');
		if (searchedVia) {
			searchedVia.textContent = getValueBeforeEquals(url);
		} else {
			console.log("Element with ID 'searchedVia' not found. Probably no result page shown");
		}

document.getElementById('addTagButton')?.addEventListener('click', function () {
    let tagsContainer = document.getElementById('tagsContainer');
    let newDiv = document.createElement('div');
    let newInput = document.createElement('input');
    newInput.type = 'text';
    newInput.name = 'tags'; // Name attribute to bind the input to an ArrayList
    let newRemoveButton = document.createElement('button');
	newRemoveButton.classList.add('button', 'is-small', 'is-warning');
    newRemoveButton.type = 'button';
    newRemoveButton.textContent = 'del';
    newRemoveButton.onclick = function () {
        this.parentNode.remove();
    }
    tagsContainer.appendChild(newDiv);
    newDiv.appendChild(newInput);
    newDiv.appendChild(newRemoveButton);
});


document.getElementById('addReferenceButton')?.addEventListener('click', function () { 
    let signatureContainer = document.getElementById('signatureContainer');
    let newDiv = document.createElement('div');
    let newReferenceInput = document.createElement('input');
    newReferenceInput.type = 'number';
    newReferenceInput.min = '100000000000';
    newReferenceInput.max = '999999999999';
    newReferenceInput.pattern = '[0-9]{12}';
    newReferenceInput.setAttribute('required', 'required');
    newReferenceInput.name = 'references'; // Name attribute to bind the input to an ArrayList
    let newRemoveRefButton = document.createElement('button');
    newRemoveRefButton.type = 'button';
	newRemoveRefButton.classList.add('button', 'is-small', 'is-warning');
    newRemoveRefButton.textContent = 'del';
    newRemoveRefButton.onclick = function () {
        this.parentNode.remove();
    }
    signatureContainer.appendChild(newDiv);
    newDiv.appendChild(newReferenceInput);
    newDiv.appendChild(newRemoveRefButton);
});


async function loadTruncatedText(textId) {
    const modal = document.querySelector('#modal-example');
    const modalContent = modal.querySelector('.modal-content .box');
    
    try {
        modal.classList.add('is-active');
        modalContent.innerHTML = '<div class="loader"></div>'; // Bulma loader
        
        const response = await fetch(`http://127.0.0.1:8080/search/text/truncatedText/${textId}`);
        
        if (response.status === 404) {
            throw new Error('Text nicht gefunden');
        }
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        
        // Verwende das bestehende HTML-Layout
        modalContent.innerHTML = `
            <h2 class="title">${data.title}</h2>
            <p>${data.content}</p>
			            <p><a href="/search/text/${textId}"  target="_blank" rel="noopener noreferrer">Mehr anzeigen</a></p>
						</div>
								<button class="modal-close is-large" aria-label="close"></button>
							</div>
        `;

        // Modal-Close-Button Event Listener
        const closeButton = modal.querySelector('.modal-close');
        const modalBackground = modal.querySelector('.modal-background');
        
        const closeModal = () => {
            modal.classList.remove('is-active');
        };

        closeButton.addEventListener('click', closeModal);
        modalBackground.addEventListener('click', closeModal);
        
    } catch (error) {
        modalContent.innerHTML = `
            <div class="notification is-danger">
                <button class="delete"></button>
                Error: ${error.message}
            </div>`;
    }
}

// Event Listener für die Preview-Buttons
document.addEventListener('DOMContentLoaded', () => {
    const previewButtons = document.querySelectorAll('.text-preview');
    previewButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            const textId = e.target.dataset.textId;
            loadTruncatedText(textId);
        });
    });
});