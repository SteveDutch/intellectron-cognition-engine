// Shared functionality for input.html and zettel.html

// --- Tag Functionality ---
document.getElementById('addTagButton')?.addEventListener('click', function () {
    const tagsContainer = document.getElementById('tagsContainer');
    if (!tagsContainer) return;
    
    const newDiv = document.createElement('div');
    const newInput = document.createElement('input');
    newInput.type = 'text';
    newInput.name = 'tags';
    
    const newRemoveButton = document.createElement('button');
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

// --- Reference Functionality (DOM Cloning) ---
document.addEventListener('DOMContentLoaded', () => {
    
    // Initialize instant search for all existing reference forms loaded from the server
    const initializeSearch = (context) => {
        const searchDropdowns = context.querySelectorAll('.instant-ref-search-dropdown');
        searchDropdowns.forEach(dropdown => {
            const input = dropdown.querySelector('.instant-ref-search-input');
            if (input && window.InstantRefSearch && !input.classList.contains('js-initialized')) {
                new window.InstantRefSearch(input, dropdown);
                input.classList.add('js-initialized');
            }
        });
    };
    
    initializeSearch(document);

    // Logic to add a new reference form group by cloning the template
    document.getElementById('addReferenceButton')?.addEventListener('click', function () {
        const referencesContainer = document.getElementById('referencesContainer');
        const templateNode = document.getElementById('reference-template-to-clone');

        if (templateNode) {
            const newNode = templateNode.cloneNode(true);
            newNode.removeAttribute('id');
            newNode.style.display = '';

            referencesContainer.appendChild(newNode);

            // Initialize the instant search specifically for the new node
            initializeSearch(newNode);
        }
    });
}); 