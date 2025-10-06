document.addEventListener('DOMContentLoaded', function() {
  const darkModeToggle = document.getElementById('darkModeToggle');
  const body = document.body;
  
  // Load page-specific dark mode CSS based on current page
  loadPageSpecificDarkModeCSS();
  
  // Check if user previously enabled dark mode
  const isDarkMode = localStorage.getItem('darkMode') === 'enabled';
  
  // Apply dark mode if previously enabled
  if (isDarkMode) {
    enableDarkMode();
  }
  
  // Toggle dark mode on button click
  darkModeToggle.addEventListener('click', () => {
    if (body.classList.contains('theme-dark')) {
      disableDarkMode();
    } else {
      enableDarkMode();
    }
  });
  
  function enableDarkMode() {
    body.classList.add('theme-dark');
    localStorage.setItem('darkMode', 'enabled');
    
    // Change icon to sun when in dark mode
    const iconElement = darkModeToggle.querySelector('i');
    if (iconElement) {
      iconElement.classList.remove('fa-moon');
      iconElement.classList.add('fa-sun');
    }
    
    // Apply any page-specific dark mode enhancements
    applyPageSpecificDarkMode();
  }
  
  function disableDarkMode() {
    body.classList.remove('theme-dark');
    localStorage.setItem('darkMode', 'disabled');
    
    // Change icon to moon when in light mode
    const iconElement = darkModeToggle.querySelector('i');
    if (iconElement) {
      iconElement.classList.remove('fa-sun');
      iconElement.classList.add('fa-moon');
    }
  }
  
  function loadPageSpecificDarkModeCSS() {
    const currentPath = window.location.pathname;
    let cssFile = '';
    
    // Only load if we have a specific file to load
    if (cssFile) {
      // Check if already loaded
      const existingLink = document.querySelector(`link[href="${cssFile}"]`);
      if (!existingLink) {
        const link = document.createElement('link');
        link.rel = 'stylesheet';
        link.type = 'text/css';
        link.href = cssFile;
        document.head.appendChild(link);
      }
    }
  }
  
  function applyPageSpecificDarkMode() {
    // Add glow effect to elements in dark mode based on current page
    const currentPath = window.location.pathname;
    
    if (currentPath === '/' || currentPath.includes('index')) {
      enhanceWelcomePage();
    }
  }
  
  function enhanceWelcomePage() {
    // Add glow effect to info tiles in dark mode
    const infoTiles = document.querySelectorAll('.info-tiles .tile.is-child.box');
    infoTiles.forEach(tile => {
      tile.style.transition = 'all 0.3s ease';
      
      // Add extra hover effect for info tiles
      tile.addEventListener('mouseenter', () => {
        if (body.classList.contains('theme-dark')) {
          tile.style.boxShadow = '0 6px 20px rgba(57, 255, 20, 0.5)';
        }
      });
      
      tile.addEventListener('mouseleave', () => {
        if (body.classList.contains('theme-dark')) {
          tile.style.boxShadow = '0 4px 12px rgba(57, 255, 20, 0.3)';
        }
      });
    });
  }
});