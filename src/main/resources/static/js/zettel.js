let submitBtn = document.getElementById("submit");
let zettel = {};
let note = {};
let author = {};
//save modal
const saveModal = document.getElementById("saveModal");
const saveModalMessage = document.getElementById("saveModalMessage");
const saveModalClose = document.getElementById("saveModalClose");
// get ID of the zettel to be updated
let zettelId = parseInt(document.getElementById("zettelId").textContent);

// Object to store initial form data
let initialFormData = {};

// --- Function to capture current form data ---
function captureCurrentFormData() {
    let data = {};
    data.topic = document.getElementById("title").value;
    data.note = document.getElementById("notiz").value; // Direct string

    // Tags as array of strings
    data.tags = Array.from(document.getElementsByName("tags")).map(input => input.value);

    // Author
    // Assumption: Only one author with index 0
    data.authorFirstName = document.getElementById("authorFirstName0") ? document.getElementById("authorFirstName0").value : '';
    data.authorFamilyName = document.getElementById("authorFamilyName0") ? document.getElementById("authorFamilyName0").value : '';

    // Tekst
    data.tekstTitle = document.getElementById("texttitle").value;
    data.tekstText = document.getElementById("tekst").value;
    data.tekstSource = document.getElementById("source").value;
    // Date: Use current input value, fallback to displayed text only if necessary
    const textDateInput = document.getElementById("textDate").value;
    if (textDateInput !== "") {
        data.tekstTextDate = textDateInput;
    } else {
         const timestampElement = document.getElementById("timestamp");
         data.tekstTextDate = timestampElement ? timestampElement.textContent : null; // Or ''
    }

    // References as array of strings
    data.references = Array.from(document.getElementsByName("references")).map(input => input.value);

    return data;
}

// --- Function to compare two objects (simplified) ---
// Compares if simple values or array contents have changed
function detectChanges(initialData, currentData) {
    // Compare simple string values (trimmed)
    if (initialData.topic.trim() !== currentData.topic.trim()) return true;
    if (initialData.note.trim() !== currentData.note.trim()) return true;
    if (initialData.authorFirstName.trim() !== currentData.authorFirstName.trim()) return true;
    if (initialData.authorFamilyName.trim() !== currentData.authorFamilyName.trim()) return true;
    if (initialData.tekstTitle.trim() !== currentData.tekstTitle.trim()) return true;
    if (initialData.tekstText.trim() !== currentData.tekstText.trim()) return true;
    if (initialData.tekstSource.trim() !== currentData.tekstSource.trim()) return true;

    // Compare date (simple string comparison here)
    if (initialData.tekstTextDate !== currentData.tekstTextDate) return true;

    // Compare arrays (length and content, ignoring order)
    const tagsChanged = initialData.tags.length !== currentData.tags.length ||
                       JSON.stringify([...initialData.tags].sort()) !== JSON.stringify([...currentData.tags].sort());
    if (tagsChanged) return true;

    const refsChanged = initialData.references.length !== currentData.references.length ||
                       JSON.stringify([...initialData.references].sort()) !== JSON.stringify([...currentData.references].sort());
    if (refsChanged) return true;

    // If no differences found so far
    return false;
}

// --- EventListener to store initial data after load ---
document.addEventListener('DOMContentLoaded', () => {
    initialFormData = captureCurrentFormData();
    console.log("Initial form data captured:", initialFormData);
});

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
    zettel.tekst = document.getElementById("tekst").value;
    // an array for tags:
    let inputs = document.getElementsByName("tags");
    let values = Array.from(inputs).map((input) => input.value);
    zettel.tags = values;

    let author = { authorFirstName: "", authorFamilyName: "" };
    zettel.author = author;
    // XXX 0 is hardcoded, should be dynamic ... but since there is only one author, it's ok & necessary
    // I'm using 1:1 instead of coded many:many for simplicity reasons
    author.authorFirstName = document.getElementById("authorFirstName0").value;
    if (author.authorFirstName === "") {
        alert(" author's first name is missing");
        return false;
    }
    author.authorFamilyName = document.getElementById("authorFamilyName0").value;
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

    // an array for references:
    let referenceInputs = document.getElementsByName("references");
    let referenceValues = Array.from(referenceInputs).map((input) => input.value);
    zettel.references = referenceValues;

    // get ID of the zettel to be updated
    // zettel.zettelId = parseInt(document.getElementById("zettelId").textContent);

    console.log("zettelId: " + zettelId);

    console.log("als JSON:  " + JSON.stringify(zettel));

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
        body: JSON.stringify(zettel, author, 4),
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