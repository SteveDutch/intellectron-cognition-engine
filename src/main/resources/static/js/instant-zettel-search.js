/**
 * Instant Zettel Search Module - Consolidated
 * Provides instant search functionality for Zettel references, plus tag and reference management.
 */

class InstantZettelSearch {
    constructor(inputElement, dropdownElement) {
        this.input = inputElement;
        this.dropdown = dropdownElement;
        this.resultsContainer = dropdownElement.querySelector('.dropdown-content');
        this.searchTimeout = null;
        this.selectedZettel = null;
        
        this.init();
    }
    
    init() {
        // Input event with debouncing
        this.input.addEventListener('input', (e) => {
            clearTimeout(this.searchTimeout);
            this.searchTimeout = setTimeout(() => {
                this.performSearch(e.target.value);
            }, 300);
        });
        
        // Focus event - only search if there are 3+ characters AND user is actively typing
        this.input.addEventListener('focus', () => {
            // Don't auto-search on focus - let user type first
            // This prevents unnecessary API calls when just clicking into the field
        });
        
        // Click outside to close
        document.addEventListener('click', (e) => {
            if (!this.input.contains(e.target) && !this.dropdown.contains(e.target)) {
                this.hideResults();
            }
        });
        
        // ESC key to close
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                this.hideResults();
            }
        });
        
        // Click on dropdown items
        this.resultsContainer.addEventListener('click', (e) => {
            const clickableItem = e.target.closest('.is-clickable');
            if (clickableItem) {
                const zettelId = clickableItem.dataset.zettelId;
                const zettelTopic = clickableItem.dataset.zettelTopic;
                this.selectZettel({ 
                    zettelId: zettelId, 
                    topic: zettelTopic 
                });
            }
        });
    }
    
    async performSearch(query) {
        if (query.length < 3) {
            this.hideResults();
            return;
        }
        
        try {
            const url = `/api/search/zettel?query=${encodeURIComponent(query)}`;
            const response = await fetch(url);
            
            if (!response.ok) {
                throw new Error('Netzwerkfehler');
            }
            
            const zettels = await response.json();
            await this.displayResults(zettels);
            
        } catch (error) {
            console.error('Search error:', error);
            await this.displayError('Netzwerkfehler im PullDown');
        }
    }
    
    async displayResults(zettels) {
        try {
            // Load template from external file
            const response = await fetch('/js/zettel-search-template.mustache');
            if (!response.ok) {
                throw new Error('Template not found');
            }
            const template = await response.text();
            
            // Prepare data for Mustache template
            const templateData = {
                zettels: zettels.map(zettel => ({
                    ...zettel,
                    noteTextTruncated: zettel.noteText ? 
                        (zettel.noteText.length > 100 ? 
                            zettel.noteText.substring(0, 100) + '...' : 
                            zettel.noteText) : null
                }))
            };
            
            // Render template
            const rendered = Mustache.render(template, templateData);
            this.resultsContainer.innerHTML = rendered;
            
            this.showResults();
        } catch (error) {
            console.error('Error loading template:', error);
            this.displayError('Template loading error');
        }
    }
    
    async displayError(errorMessage) {
        try {
            // Load template from external file
            const response = await fetch('/js/zettel-search-template.mustache');
            if (!response.ok) {
                throw new Error('Template not found');
            }
            const template = await response.text();
            
            const templateData = {
                error: errorMessage
            };
            
            const rendered = Mustache.render(template, templateData);
            this.resultsContainer.innerHTML = rendered;
            
            this.showResults();
        } catch (error) {
            console.error('Error loading template for error display:', error);
            // Fallback to simple error display
            this.resultsContainer.innerHTML = `<div class="dropdown-item has-text-danger">${errorMessage}</div>`;
            this.showResults();
        }
    }
    
    selectZettel(zettel) {
        this.selectedZettel = zettel;
        this.input.value = `${zettel.topic} (ID: ${zettel.zettelId})`;
        this.input.dataset.zettelId = zettel.zettelId;
        this.hideResults();
        
        // Trigger custom event for other components
        this.input.dispatchEvent(new CustomEvent('zettelSelected', {
            detail: { zettel: zettel }
        }));
    }
    
    showResults() {
        this.dropdown.classList.add('is-active');
    }
    
    hideResults() {
        this.dropdown.classList.remove('is-active');
    }
    
    getSelectedZettelId() {
        return this.selectedZettel ? this.selectedZettel.zettelId : null;
    }
    
    clearSelection() {
        this.selectedZettel = null;
        this.input.value = '';
        this.input.removeAttribute('data-zettel-id');
        this.hideResults();
    }
}

// Single DOMContentLoaded listener to rule them all
document.addEventListener('DOMContentLoaded', () => {
    // Make the class available on the window object for dynamic initialization
    window.InstantZettelSearch = InstantZettelSearch;

    // --- Tag Functionality ---
    const addTagButton = document.getElementById('addTagButton');
    addTagButton?.addEventListener('click', function () {
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

    // --- Reference Functionality ---

    // Function to initialize search on a given context (document or a new node)
    const initializeSearch = (context) => {
        const searchDropdowns = context.querySelectorAll('.instant-ref-search-dropdown');
        
        searchDropdowns.forEach((dropdown) => {
            const input = dropdown.querySelector('.instant-ref-search-input');
            
            if (input && window.InstantZettelSearch && !input.classList.contains('js-initialized')) {
                try {
                    new window.InstantZettelSearch(input, dropdown);
                    input.classList.add('js-initialized');
                } catch (error) {
                    console.error('Error initializing dropdown:', error);
                }
            }
        });
    };
    
    // Initial search initialization on page load
    initializeSearch(document);
    
    // Load existing reference details on page load
    loadExistingReferenceDetails();
    
    // Function to load details for existing references
    async function loadExistingReferenceDetails() {
        const existingRefInputs = document.querySelectorAll('.instant-ref-search-input[data-zettel-id]');
        
        for (const input of existingRefInputs) {
            const zettelId = input.dataset.zettelId;
            if (zettelId && input.value.startsWith('Loading Zettel')) {
                try {
                    // Fetch zettel details by ID
                    const response = await fetch(`/api/search/zettel?query=id:${zettelId}`);
                    if (response.ok) {
                        const result = await response.json();
                        if (result && result.length > 0) {
                            const zettel = result[0];
                            input.value = `${zettel.topic} (ID: ${zettel.zettelId})`;
                        } else {
                            input.value = `Zettel ${zettelId} (not found)`;
                        }
                    } else {
                        input.value = `Zettel ${zettelId} (error loading)`;
                    }
                } catch (error) {
                    console.error('Error loading zettel details:', error);
                    input.value = `Zettel ${zettelId} (error loading)`;
                }
            }
        }
    }

    // Setup for deleting references
    const setupDeleteButtons = (context) => {
        const deleteButtons = context.querySelectorAll('.delete-reference-btn');
        deleteButtons.forEach(button => {
            if (!button.onclick) {
                button.onclick = function() {
                    this.closest('.reference-group').remove();
                };
            }
        });
    };

    // Initial setup for delete buttons on page load
    setupDeleteButtons(document);

    // Logic to add a new reference form group by cloning the template
    const addReferenceButton = document.getElementById('addReferenceButton') || document.getElementById('add-reference-btn');
    if (addReferenceButton) {
        addReferenceButton.addEventListener('click', function () {
            const referencesContainer = document.getElementById('referencesContainer');
            const templateNode = document.getElementById('reference-template-to-clone');

            if (templateNode && referencesContainer) {
                const newNode = templateNode.cloneNode(true);
                newNode.removeAttribute('id');
                newNode.classList.remove('is-hidden');

                // Reset the state of the cloned input field before initialization
                const newSearchInput = newNode.querySelector('.instant-ref-search-input');
                if (newSearchInput) {
                    newSearchInput.classList.remove('js-initialized');
                    newSearchInput.value = ''; // Clear any previous value from the template
                }

                // Must setup delete button for the new clone
                const deleteButton = newNode.querySelector('.delete-reference-btn');
                if (deleteButton) {
                    deleteButton.onclick = function() {
                        this.closest('.reference-group').remove();
                    };
                }
                
                referencesContainer.appendChild(newNode);

                // Initialize the instant search specifically for the new node
                initializeSearch(newNode);
            } else {
                if (!templateNode) console.error('Reference template node not found! Expected ID: reference-template-to-clone');
                if (!referencesContainer) console.error('References container not found!');
            }
        });
    }
}); 