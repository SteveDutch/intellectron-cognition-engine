console.log("Vanilla JS wurde aufgerufen!");

let submitBtn = document.getElementById("submit");
let zettel = {};
let note = {};
let author = {};

document.getElementById('addTagButton').addEventListener('click', function () {
    var tagsContainer = document.getElementById('tagsContainer');
    var newDiv = document.createElement('div');
    var newInput = document.createElement('input');
    newInput.type = 'text';
    newInput.name = 'tags'; // Name attribute to bind the input to an ArrayList
    var newRemoveButton = document.createElement('button');
    newRemoveButton.type = 'button';
    newRemoveButton.textContent = 'rydde ut Tag';
    newRemoveButton.onclick = function () {
        this.parentNode.remove();
    }
    tagsContainer.appendChild(newDiv);
    newDiv.appendChild(newInput);
    newDiv.appendChild(newRemoveButton);
});


document.getElementById('addReferenceButton').addEventListener('click', function () { 
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
    newRemoveRefButton.textContent = 'rydde ut referanse';
    newRemoveRefButton.onclick = function () {
        this.parentNode.remove();
    }
    signatureContainer.appendChild(newDiv);
    newDiv.appendChild(newReferenceInput);
    newDiv.appendChild(newRemoveRefButton);
});

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
	let inputs = document.getElementsByName("tagInput");
	let values = Array.from(inputs).map((input) => input.value);
	zettel.tags = values;
	let author = { authorFirstName: "", authorFamilyName: "" };
	zettel.author = author;
	author.authorFirstName = document.getElementById("authorFirstName").value;
	author.authorFamilyName = document.getElementById("authorFamilyName").value;

	let tekst = { title:"", text: "", textDate: "", source: "" };
	zettel.tekst = tekst;
	tekst.title = document.getElementById("texttitle").value;
	tekst.text = document.getElementById("tekst").value;
	tekst.textDate = document.getElementById("timestamp").value;
	tekst.source = document.getElementById("source").value;

	// an array for references:
	let referenceInputs = document.getElementsByName("referenceInput");
	let referenceValues = Array.from(referenceInputs).map((input) => input.value);
	zettel.references = referenceValues;

	console.log("als JSON:  " + JSON.stringify(zettel));

	zettelToJava();
}

function zettelToJava() {
	fetch(`http://127.0.0.1:8080/input`, {
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
		    // Check if the response is a redirect
		    if (response.redirected) {
		        // If it's a redirect, navigate to the new URL
		        window.location.href = response.url;
		    } else {
		        // If it's not a redirect, parse the JSON
		        return response.json();
		    }
		})
	  .then(data => {
		// Handle the successful response
		console.log("Response data:", data);
	  })
	  .catch(error => {
		// Pass the error response to the handleError function
		handleError(error);
	  });;
	console.log("als JSON gesendet:  " + JSON.stringify(zettel, author, 4));

	
}

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
  
