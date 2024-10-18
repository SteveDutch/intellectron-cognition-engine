let submitBtn = document.getElementById("submit");
let zettel = {};
let note = {};
let author = {};
// get ID of the zettel to be updated
let zettelId = parseInt(document.getElementById("zettelId").textContent);

/* document.getElementById('addTagButton').addEventListener('click', function () {
    var tagsContainer = document.getElementById('tagsContainer');
    var newDiv = document.createElement('div');
    var newInput = document.createElement('input');
    newInput.type = 'text';
    newInput.name = 'tags'; // Name attribute to bind the input to an ArrayList
    var newRemoveButton = document.createElement('button');
    newRemoveButton.type = 'button';
	newRemoveButton.className = 'button is-small is-warning';
    newRemoveButton.textContent = 'rydde ut Tag';
    newRemoveButton.onclick = function () {
        this.parentNode.remove();
    }
    tagsContainer.appendChild(newDiv);
    newDiv.appendChild(newInput);
    newDiv.appendChild(newRemoveButton);
}); */

/* document.getElementById('addReferenceButton').addEventListener('click', function () { 
    var signatureContainer = document.getElementById('signatureContainer');
    var newDiv = document.createElement('div');
    var newReferenceInput = document.createElement('input');
    newReferenceInput.type = 'number';
    newReferenceInput.min = '100000000000';
    newReferenceInput.max = '999999999999';
    newReferenceInput.pattern = '[0-9]{12}';
    newReferenceInput.setAttribute('required', 'required');
    newReferenceInput.name = 'references'; // Name attribute to bind the input to an ArrayList
    var newRemoveRefButton = document.createElement('button');
    newRemoveRefButton.type = 'button';
	newRemoveRefButton.className = 'button is-small is-warning';
    newRemoveRefButton.textContent = 'rydde ut referanse';
    newRemoveRefButton.onclick = function () {
        this.parentNode.remove();
    }
    signatureContainer.appendChild(newDiv);
    newDiv.appendChild(newReferenceInput);
    newDiv.appendChild(newRemoveRefButton);
}); */

submitBtn.addEventListener("click", function (event) {
    // suppress HTML sending form
    event.preventDefault();
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
            mode: "no-cors",
            "Content-Type": "application/json",
        },
        body: JSON.stringify(zettel, author, 4),

    }).then(response => {
		if (!response.ok) {
		  throw response;
		}
		return response.json();
	  })
	  .then(data => {
		// Handle the successful response
		console.log(data);
	  })
	  .catch(error => {
		// Pass the error response to the handleError function
		handleError(error);
	  });;

    
    console.log("als JSON gesendet:  " + JSON.stringify(zettel, author, 4));
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
}

document.getElementById('delete').addEventListener('click', function () {
    deleteZettel();
});

function handleError(error) {
    error.json().then(data => {
        // Assuming the server sends a JSON response with a 'message' field
        const message = data.message;
        console.error('Error:', message);
        // Display the message on the page
        // For example, you could set the text content of an element with id 'errorMessage' to the message
       // document.alarm('errorMessage').textContent = message;
	   alert(message);
    }).catch(err => {
        console.error('Error parsing error response:', err);
    });
}