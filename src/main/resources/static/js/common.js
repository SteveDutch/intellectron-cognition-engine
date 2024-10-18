// Aktuelle URL abrufen
const url = window.location.href;

function getValueAfterEquals(url) {
    const equalsIndex = url.lastIndexOf('=');
    if (equalsIndex !== -1 && equalsIndex < url.length - 1) {
        return url.substring(equalsIndex + 1);
    }
}
// Suchbegriff in das HTML-Element einfügen
let searchTerm = document.getElementById('searchTerm');
if (searchTerm) {
    searchTerm.textContent = getValueAfterEquals(url);
} else {
    console.log("Element with ID 'searchTerm' not found. Probably no result page shown");
}
// document.getElementById('searchTerm').textContent = getValueAfterEquals(url);
 

document.getElementById('addTagButton')?.addEventListener('click', function () {
    let tagsContainer = document.getElementById('tagsContainer');
    let newDiv = document.createElement('div');
    let newInput = document.createElement('input');
    newInput.type = 'text';
    newInput.name = 'tags'; // Name attribute to bind the input to an ArrayList
    let newRemoveButton = document.createElement('button');
	newRemoveButton.classList.add('button', 'is-small', 'is-warning');
    newRemoveButton.type = 'button';
    newRemoveButton.textContent = 'rydde ut Tag';
    newRemoveButton.onclick = function () {
        this.parentNode.remove();
    }
    tagsContainer.appendChild(newDiv);
    newDiv.appendChild(newInput);
    newDiv.appendChild(newRemoveButton);
});


document.getElementById('addReferenceButton')?.addEventListener('click', function () { 
    let signatureContainer = document.getElementById('signatureContainer');
    let newDiv = document.createElement('div');
    let newReferenceInput = document.createElement('input');
    newReferenceInput.type = 'number';
    newReferenceInput.min = '100000000000';
    newReferenceInput.max = '999999999999';
    newReferenceInput.pattern = '[0-9]{12}';
    newReferenceInput.setAttribute('required', 'required');
    newReferenceInput.name = 'references'; // Name attribute to bind the input to an ArrayList
    let newRemoveRefButton = document.createElement('button');
    newRemoveRefButton.type = 'button';
	newRemoveRefButton.classList.add('button', 'is-small', 'is-warning');
    newRemoveRefButton.textContent = 'rydde ut referanse';
    newRemoveRefButton.onclick = function () {
        this.parentNode.remove();
    }
    signatureContainer.appendChild(newDiv);
    newDiv.appendChild(newReferenceInput);
    newDiv.appendChild(newRemoveRefButton);
});