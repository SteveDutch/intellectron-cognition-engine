/**
 * Instant Reference Search Module - made by genAI
 * Provides instant search functionality for Zettel references using Mustache templates
 */

class InstantRefSearch {
    constructor(inputElement, dropdownElement) {
        this.input = inputElement;
        this.dropdown = dropdownElement;
        this.resultsContainer = dropdownElement.querySelector('.dropdown-content');
        this.searchTimeout = null;
        this.selectedZettel = null;
        this.template = document.getElementById('zettel-search-template').innerHTML;
        
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
        
        // Focus event
        this.input.addEventListener('focus', () => {
            if (this.input.value.length >= 3) {
                this.performSearch(this.input.value);
            }
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
            const response = await fetch(`/api/search/zettel?query=${encodeURIComponent(query)}`);
            
            if (!response.ok) {
                throw new Error('Netzwerkfehler');
            }
            
            const zettels = await response.json();
            this.displayResults(zettels);
            
        } catch (error) {
            console.error('Search error:', error);
            this.displayError('Netzwerkfehler im PullDown');
        }
    }
    
    displayResults(zettels) {
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
        const rendered = Mustache.render(this.template, templateData);
        this.resultsContainer.innerHTML = rendered;
        
        this.showResults();
    }
    
    displayError(errorMessage) {
        const templateData = {
            error: errorMessage
        };
        
        const rendered = Mustache.render(this.template, templateData);
        this.resultsContainer.innerHTML = rendered;
        
        this.showResults();
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

// Auto-initialize all instant search dropdowns when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    const searchDropdowns = document.querySelectorAll('.instant-ref-search-dropdown');
    
    searchDropdowns.forEach(dropdown => {
        const input = dropdown.querySelector('.instant-ref-search-input');
        if (input) {
            new InstantRefSearch(input, dropdown);
        }
    });
});

// Export for manual initialization if needed
window.InstantRefSearch = InstantRefSearch; 