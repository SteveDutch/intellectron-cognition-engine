let submitBtn = document.getElementById("submit");
let zettel = {};

//save modal
const saveModal = document.getElementById("saveModal");
const saveModalMessage = document.getElementById("saveModalMessage");
const saveModalClose = document.getElementById("saveModalClose");
// get ID of the zettel to be updated
let zettelId = parseInt(document.getElementById("zettelId").textContent);

// Object to store initial form data
let initialFormData = {};

// The reference and tag functionality has been moved to enter-zettel.js

// --- Function to capture current form data ---
function captureCurrentFormData() {
    let data = {};
    data.topic = document.getElementById("title").value;
    data.note = document.getElementById("notiz").value; 

    data.tags = Array.from(document.getElementsByName("tags")).map(input => input.value);
    
    data.authorFirstName = document.getElementById("authorFirstName0") ? document.getElementById("authorFirstName0").value : '';
    data.authorFamilyName = document.getElementById("authorFamilyName0") ? document.getElementById("authorFamilyName0").value : '';

    data.tekstTitle = document.getElementById("texttitle").value;
    data.tekstText = document.getElementById("tekst").value;
    data.tekstSource = document.getElementById("source").value;
    const textDateInput = document.getElementById("textDate").value;
    if (textDateInput !== "") {
        data.tekstTextDate = textDateInput;
    } else {
         const timestampElement = document.getElementById("timestamp");
         data.tekstTextDate = timestampElement ? timestampElement.textContent : null;
    }

    // References - This logic will be handled by prepareZettel during submission
    data.references = Array.from(document.querySelectorAll('.reference-group:not(#reference-template)'))
        .map(group => {
            const searchInput = group.querySelector('input[name="zettelSearchInput"]');
            const targetZettelId = searchInput.dataset.zettelId;
            return targetZettelId ? `ref-${targetZettelId}` : null;
        })
        .filter(Boolean); // Keep it simple for change detection

    return data;
}

// --- Function to detect changes ---
function detectChanges(initialData, currentData) {
    return JSON.stringify(initialData) !== JSON.stringify(currentData);
}

// --- Capture initial form data when page loads ---
document.addEventListener('DOMContentLoaded', function() {
    initialFormData = captureCurrentFormData();
    // The 'addReferenceButton' listener is now in enter-zettel.js
    
    // Listen for zettel selection from instant search - made by genAI
    const searchInput = document.getElementById("zettelSearchInput");
    if (searchInput) {
        searchInput.addEventListener('zettelSelected', function(event) {
            console.log('Zettel selected:', event.detail.zettel);
        });
    }
});

// Close modal functionality
if (saveModalClose) {
    saveModalClose.addEventListener('click', function() {
        saveModal.style.display = 'none';
    });
}

// All reference-handling functions (addReference, displayReference, etc.) are removed from here.

// The getReferenceTypeClass function has been moved to enter-zettel.js

submitBtn.addEventListener("click", function (event) {
    // suppress HTML sending form
    event.preventDefault();

    // --- Check for changes ---
    const currentData = captureCurrentFormData();
    if (!detectChanges(initialFormData, currentData)) {
        // Use English alert
        alert("No changes detected. Submit cancelled.");
        return; // Stop if nothing changed
    }
    // --- End check for changes ---

    // Use English confirmation
    if (!confirm('Are you sure you want to submit these changes?')) {
        console.log("Save cancelled by user.");
        return;
    }

    console.log("hooray! submit was clicked, changes detected and confirmed, preparing data via prepareZettel...");
    // Call the original prepareZettel function.
    // It performs validation and modifies the global 'zettel' object.
    // It returns nothing on success, or exits early on validation failure.
    // We need a way to know if validation failed inside prepareZettel.
    // Modifying prepareZettel slightly to return true/false is cleaner.
    // Alternatively, check if the fetch should proceed based on a flag or re-validation.

    // Let's try modifying prepareZettel to return a boolean
    const preparationSuccess = prepareZettel(); // prepareZettel now returns true/false

    if (preparationSuccess) {
        console.log("Data prepared/validated, sending to server...");
        // zettelToJava now reads the global 'zettel' object again
        zettelToJava();
    } else {
        console.log("Data preparation/validation failed inside prepareZettel.");
        // The alert is already shown inside prepareZettel
    }
});

function prepareZettel() {
    console.log("juhu, funct. prepareZettel wurde aufgerufen");
    zettel.zettel = document.getElementById("title").value;
    zettel.note = document.getElementById("notiz").value;
    let inputs = document.getElementsByName("tags");
    let values = Array.from(inputs).map((input) => input.value);
    zettel.tags = values;

    let author = { authorFirstName: "", authorFamilyName: "" };
    zettel.author = author;
    author.authorFirstName = document.getElementById("authorFirstName0").value.trim();
    if (author.authorFirstName === "") {
        alert(" author's first name is missing");
        return false;
    }
    author.authorFamilyName = document.getElementById("authorFamilyName0").value.trim();
    if (author.authorFamilyName === "") {
        alert("  author's family name is missing");
        return false;
    }

    let tekst = {title:"", text: "", textDate: "", source: "" };
    zettel.tekst = tekst;
    tekst.title = document.getElementById("texttitle").value;
    tekst.text = document.getElementById("tekst").value;

    if (document.getElementById("textDate").value !== "") {
    tekst.textDate = document.getElementById("textDate").value;
    } else {
        tekst.textDate = document.getElementById("timestamp").textContent;
    }
    tekst.source = document.getElementById("source").value;

    // Collect references from all visible reference-group divs
    const collectedReferences = [];
    const referenceGroups = document.querySelectorAll('.reference-group:not(#reference-template)');

    referenceGroups.forEach(group => {
        const searchInput = group.querySelector('input[name="zettelSearchInput"]');
        // The data-zettel-id attribute holds the selected Zettel's ID
        const targetZettelId = searchInput.dataset.zettelId; 

        if (targetZettelId) {
            const typeInput = group.querySelector('select[name="referenceType"]');
            const noteInput = group.querySelector('input[name="connectionNote"]');
            
            collectedReferences.push({
                type: typeInput.value,
                targetZettelId: parseInt(targetZettelId, 10),
                connectionNote: noteInput.value || null
            });
        }
        // If no zettelId is selected, we simply skip this group.
    });

    zettel.references = collectedReferences;
    
    console.log("zettelId: " + zettelId);
    console.log("zettel object: " + JSON.stringify(zettel));
    
    return true;
}

function zettelToJava() {
    fetch(`http://127.0.0.1:8080/zettel/${zettelId}`, {
        method: "POST",
        headers: {
            "Access-Control-Allow-Origin": "http://127.0.0.1:8080",
            /*	 nicht nötig, da fetch & a-c-a-o jetzt übereinstimmen,
                  die abweichung localhost zu 127.0.0.1 war schon zuviel ;)
                  but seems to cause trouble if missing at other computers or 
                  maybe esp. at macs */
          //   mode: "no-cors", --> not necessary, since we are using the same origin
            "Content-Type": "application/json",
        },
        body: JSON.stringify(zettel, undefined, 4),
    }).then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                console.log(response);
                 throw new Error(`HTTP error! status: ${response.status}, message: ${text || 'Server provided no details'}`);
            });
        }
        console.log("Request successful, server responded.");
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            return response.json();
        }
        return null;
    })
    .then(data => {
        console.log("Success response data (if any):", data);
        saveModalMessage.textContent = "Save successful! (reload in 1,5 sec)";
        saveModal.style.display = "block";
        console.log("Reloading page in 1.5 seconds...");
        setTimeout(() => {
            window.location.reload();
        }, 1500);
    })
    .catch(error => {
        console.error('Error during fetch:', error);
        saveModalMessage.textContent = "Error saving: " + error.message;
        saveModal.style.display = "block";
    });
}


   //get the modal for delteting the zettel
const modal = document.getElementById("deleteModal");
//open modal
const openDeleteModalButton = document.getElementById('openDeleteModal');
if (openDeleteModalButton) {
    openDeleteModalButton.addEventListener('click', function () {
        modal.style.display = "block";
    });
}

let closeModal = document.getElementById("cancel");
// When the user clicks on cancel, close the modal
if(closeModal) {
    closeModal.onclick = function () {
        modal.style.display = "none";
    }
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function (event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
    if (event.target == saveModal) {
        saveModal.style.display = "none";
    }
}

document.getElementById('delete').addEventListener('click', function () {
    deleteZettel();
});

// Add save modal close functionality
saveModalClose.onclick = function() {
    saveModal.style.display = "none";
}

function handleError(error) {
    // Try to parse error as JSON if possible
    if (error.json) {
        error.json().then(data => {
            // Handle JSON error response
            const message = data.message;
            console.error('Error:', message);
            saveModalMessage.textContent = "Error: " + message;
            saveModal.style.display = "block";
        }).catch(err => {
            // Handle non-JSON error
            console.error('Error parsing error response:', err);
            saveModalMessage.textContent = "Error saving changes";
            saveModal.style.display = "block";
            alert(err.message); // Keep existing alert as fallback
        });
    } else {
        // Handle plain error
        console.error('Error:', error);
        saveModalMessage.textContent = "Error saving changes";
        saveModal.style.display = "block";
        alert(error.message); // Keep existing alert as fallback
    }
}