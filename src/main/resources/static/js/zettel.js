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

submitBtn.addEventListener("click", function (event) {
    // suppress HTML sending form
    event.preventDefault();
    
    // Add confirmation dialog
    if (!confirm('Are you sure you want to submit these changes?')) {
        // Show modal for cancelled save
        saveModalMessage.textContent = "Save cancelled";
        saveModal.style.display = "block";
        return; // If user clicks Cancel, stop here
    }
    
    // Show modal for confirmed save
    saveModalMessage.textContent = "Save confirmed";
    saveModal.style.display = "block";
    
    console.log("hooray! submit was clicked");
    prepareZettel();
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
        alert("Bitte geben Sie den Vornamen des Autors ein!");
        return;
    }
    author.authorFamilyName = document.getElementById("authorFamilyName0").value;
    if (author.authorFamilyName === "") {
        alert("Bitte geben Sie den Nachnamen des Autors ein!");
        return;
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

    zettelToJava();
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
            console.log(response);
            throw new Error(`HTTP error! status: ${response.status}`);
        }
             // Check if there's actually content to parse
             const contentType = response.headers.get("content-type");
             if (contentType && contentType.includes("application/json")) {
                 return response.json();
             }
             return response.text() // if you expect text
         })
    .then(data => {
        // Handle the successful response
        console.log(data);
        saveModalMessage.textContent = "Save successful!";
        saveModal.style.display = "block";
    })
    .catch(error => {
        // Simplified error handling
        console.error('Error:', error);
        saveModalMessage.textContent = "Error saving: " + error.message;
        saveModal.style.display = "block";
        handleError(error);
    });
}


   //get the modal for delteting the zettel
const modal = document.getElementById("deleteModal");
//open modal
document.getElementById('openDeleteModal').addEventListener('click', function () {
    modal.style.display = "block";
});

let closeModal = document.getElementById("cancel");
// When the user clicks on cancel, close the modal
closeModal.onclick = function () {
    modal.style.display = "none";
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