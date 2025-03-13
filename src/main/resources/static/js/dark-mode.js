document.addEventListener('DOMContentLoaded', () => {
  const darkModeToggle = document.getElementById('darkModeToggle');
  const icon = darkModeToggle.querySelector('i');
  
  // Check for saved theme preference or system preference
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
  const savedTheme = localStorage.getItem('theme');
  
  // Set initial theme
  if (savedTheme === 'dark' || (!savedTheme && prefersDark)) {
    document.documentElement.classList.add('theme-dark');
    icon.classList.remove('fa-moon');
    icon.classList.add('fa-sun');
  }
  
  // Toggle theme
  darkModeToggle.addEventListener('click', () => {
    document.documentElement.classList.toggle('theme-dark');
    
    // Toggle icon
    if (document.documentElement.classList.contains('theme-dark')) {
      icon.classList.remove('fa-moon');
      icon.classList.add('fa-sun');
      localStorage.setItem('theme', 'dark');
    } else {
      icon.classList.remove('fa-sun');
      icon.classList.add('fa-moon');
      localStorage.setItem('theme', 'light');
    }
  });
});