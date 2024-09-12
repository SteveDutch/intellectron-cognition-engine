        // Aktuelle URL abrufen
        const url = window.location.href;

        // URL in Teile zerlegen
        const urlParts = new URL(url);

        // Hostnamen extrahieren
        const searchTerm = urlParts.search;

        // Hostnamen in das HTML-Element einfügen
        document.getElementById('searchTerm').textContent = searchTerm;


document.addEventListener('DOMContentLoaded', () => {
    // Functions to open and close a modal
    const modalTriggers = document.querySelectorAll('.js-modal-trigger');
    
    modalTriggers.forEach((trigger) => {
        trigger.addEventListener('click', () => {
            const modalId = trigger.getAttribute('data-target');
            const zettelId = trigger.getAttribute('data-zettel-id');
            // const modal = document.getElementById(modalId);
            console.log('Zettel ID:', zettelId); // Add this line for debugging 

            if (zettelId) {
                const modal = document.getElementById(modalId);
                if (modal) {
                    openModal(modal, zettelId);
                } else {
                    console.error(`Modal with id "${modalId}" not found`);
                }
            } else {
                console.error('Zettel ID is undefined');
            }
        });
    });

    function openModal(modal, zettelId) {
      modal.classList.add('is-active');
      document.body.classList.add('is-clipped');
      
      console.log('Zettel ID (zumZwoten):', zettelId); // Add this line for debugging 
      fetch(`/modal-content/${zettelId}`)
          .then(response => response.json())
          .then(data => {
              const modalContent = modal.querySelector('.modal-content');
              if (modalContent) {
                  const renderedContent = Mustache.render(document.getElementById('modal-template').innerHTML, data);
                  modalContent.innerHTML = renderedContent;
              }
          })
          .catch(error => console.error('Error fetching modal content:', error));
  }
  
    function closeModal($el) {
      $el.classList.remove('is-active');
    }
  
    function closeAllModals() {
      (document.querySelectorAll('.modal') || []).forEach(($modal) => {
        closeModal($modal);
      });
    }
  
    // Add a click event on buttons to open a specific modal WAR DOPPELT
    // (document.querySelectorAll('.js-modal-trigger') || []).forEach(($trigger) => {
    //   const modal = $trigger.dataset.target;
    //   const $target = document.getElementById(modal);
  
    //   $trigger.addEventListener('click', () => {
    //     openModal($target);
    //   });
    // });
  
    // Add a click event on various child elements to close the parent modal
    (document.querySelectorAll('.modal-background, .modal-close, .modal-card-head .delete, .modal-card-foot .button') || []).forEach(($close) => {
      const $target = $close.closest('.modal');
  
      $close.addEventListener('click', () => {
        closeModal($target);
      });
    });
  
    // Add a keyboard event to close all modals
    document.addEventListener('keydown', (event) => {
      if(event.key === "Escape") {
        closeAllModals();
      }
    });
  });
  document.querySelector('.js-modal-trigger').addEventListener('click', function() {
	var modalTemplate = document.getElementById('modal-template').innerHTML;
	// var modalContent = Mustache.render(modalTemplate, data);
      fetch('/modal-content/' + zettelId)
          .then(response => response.json())
          .then(data => {
              // Populate modal with data
              var modalTemplate = document.getElementById('modal-template').innerHTML;
              var modalContent = Mustache.render(modal, data);
              document.querySelector('#modal-js-example .modal-content').innerHTML = modalContent;
              // Open the modal
          });
  });