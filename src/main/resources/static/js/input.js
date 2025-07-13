console.log("Vanilla JS wurde aufgerufen!");

let submitBtn = document.getElementById("submit");
let zettel = {};
let note = {};
let author = {};

submitBtn.addEventListener("click", function (event) {
	// suppress HTML sending form
	event.preventDefault();
	console.log("hooray! submit was clicked");
	prepareZettel();
});

// The reference and tag functionality has been moved to enter-zettel.js

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
	author.authorFirstName = document.getElementById("authorFirstName").value;
	author.authorFamilyName = document.getElementById("authorFamilyName").value;

	let tekst = { title:"", text: "", textDate: "", source: "" };
	zettel.tekst = tekst;
	tekst.title = document.getElementById("texttitle").value;
	tekst.text = document.getElementById("tekst").value;
	tekst.textDate = document.getElementById("timestamp").value;
	tekst.source = document.getElementById("source").value;

	// Collect references from all reference-group divs
    const collectedReferences = [];
    const referenceGroups = document.querySelectorAll('.reference-group');
    
    referenceGroups.forEach(group => {
        const searchInput = group.querySelector('input[name="zettelSearchInput"]');
        const targetZettelId = searchInput.dataset.zettelId;

        // Only include the reference if a Zettel has been selected
        if (targetZettelId) {
            const typeInput = group.querySelector('select[name="referenceType"]');
            const noteInput = group.querySelector('input[name="connectionNote"]');
            
            collectedReferences.push({
                type: typeInput.value,
                targetZettelId: parseInt(targetZettelId),
                connectionNote: noteInput.value || null
            });
        }
    });

	zettel.references = collectedReferences;

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
  
