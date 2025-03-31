document.addEventListener('DOMContentLoaded', function() {
    // Get all tables with the class 'sortable-table'
    const tables = document.querySelectorAll('.sortable-table');
    
    tables.forEach(table => {
        const headers = table.querySelectorAll('thead th');
        
        headers.forEach((header, index) => {
            // Skip the Tags column (index 5)
            if (index === 5) return;
            
            header.style.cursor = 'pointer';
            header.addEventListener('click', () => {
                sortTable(table, index);
            });
        });
    });
});

function sortTable(table, column) {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    const header = table.querySelector(`thead th:nth-child(${column + 1})`);
    
    // Toggle sort direction
    const isAscending = !header.classList.contains('asc');
    header.classList.remove('asc', 'desc');
    header.classList.add(isAscending ? 'asc' : 'desc');
    
    // Remove sort classes from other headers
    table.querySelectorAll('thead th').forEach(h => {
        if (h !== header) {
            h.classList.remove('asc', 'desc');
        }
    });
    
    // Sort rows
    rows.sort((a, b) => {
        const aValue = a.cells[column].textContent.trim();
        const bValue = b.cells[column].textContent.trim();
        
        // Handle numeric values
        if (!isNaN(aValue) && !isNaN(bValue)) {
            return isAscending ? 
                Number(aValue) - Number(bValue) : 
                Number(bValue) - Number(aValue);
        }
        
        // Handle text values
        return isAscending ? 
            aValue.localeCompare(bValue) : 
            bValue.localeCompare(aValue);
    });
    
    // Reorder rows
    rows.forEach(row => tbody.appendChild(row));
} 