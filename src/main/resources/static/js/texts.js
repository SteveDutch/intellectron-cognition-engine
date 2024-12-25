//document.addEventListener('DOMContentLoaded', () => {
//    const modalTriggers = document.querySelectorAll('.js-modal-trigger');
//    modalTriggers.forEach(trigger => {
//        trigger.addEventListener('click', () => {
//            const textId = trigger.getAttribute('data-text-id');
//            console.log("TextID:", textId);
//            loadTruncatedText(textId);
//        });
//    });
//});
//
//async function loadTruncatedText(textId) {
//    try {
//        const modal = document.querySelector('#modal-example');
//		console.log("sie textID  in der async funcgion ist gleich: " + textId);
//        modal.classList.add('is-active');  // Show loading state
//        
//        const response = await fetch(`http://127.0.0.1:8080/search/text/truncatedText/${textId}`);
//        if (!response.ok) {
//            throw new Error('Network response was not ok');
//        }
//        
//        const data = await response.json();
//        modal.querySelector('.modal-content').innerHTML = data.content;
//    } catch (error) {
//        showError('Failed to load content: ' + error.message);
//    }
//}
//
//function showError(message) {
//    const error = document.createElement('div');
//    error.className = 'notification is-danger';
//    error.innerHTML = `${message} <button class="delete" onclick="this.parentElement.remove()"></button>`;
//    document.querySelector('.modal-content').prepend(error);
//    setTimeout(() => error.remove(), 50000);  // Auto-remove after 50s
//}


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
			            <p><a href="/search/text/${textId}">Mehr anzeigen</a></p>
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