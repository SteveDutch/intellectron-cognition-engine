document.addEventListener('DOMContentLoaded', () => {
    // Find the main modal element in the DOM
    // Replace '#text-modal' with the actual ID or a unique selector for your modal in texts.html
    const modal = document.querySelector('#modal-example'); 

    // Only proceed if the modal element exists on this page
    if (modal) {
        // Find the close button and background *within* the modal
        const closeButton = modal.querySelector('.modal-close');
        const modalBackground = modal.querySelector('.modal-background');
        
        // Function to close the modal
        const closeModal = () => {
            modal.classList.remove('is-active');
        };

        // Add event listeners only if the elements were found
        if (closeButton) {
            closeButton.addEventListener('click', closeModal);
        }
        if (modalBackground) {
            modalBackground.addEventListener('click', closeModal);
        }

        // --- Add other modal-related logic here ---
        // For example, code to *open* the modal might go here or elsewhere
        // depending on how it's triggered.

    } else {
        // Optional: Log a message if the modal isn't found (useful for debugging)
        // console.log('No modal element found on this page.');
    }
});
      