
        // Modal-Close-Button Event Listener
        const closeButton = modal.querySelector('.modal-close');
        const modalBackground = modal.querySelector('.modal-background');
        
        const closeModal = () => {
            modal.classList.remove('is-active');
        };

        closeButton.addEventListener('click', closeModal);
        modalBackground.addEventListener('click', closeModal);
      