console.log('Vanilla JS wurde aufgerufen!');

let submitBtn = document.getElementById('submit');
let zettel ={};
let note = {};

submitBtn.addEventListener('click', function(event) {
	// suppress HTML sending form 
    event.preventDefault();
	console.log('hooray! submit was clicked');
	prepareZettel();
	
});

function prepareZettel() {
	console.log('juhu, funct. prepareZettel wurde aufgerufen');
	zettel.zettel = document.getElementById('title').value;
	zettel.note = document.getElementById('tekst').value;
	zettel.tekst = document.getElementById('tekst').value;
	// zettel.tags = document.getElementsByName('tagInput').value;
	var inputs = document.getElementsByName('tagInput');

	var values = Array.from(inputs).map(input => input.value);
	zettel.tags = values;
	console.log('als JSON:  ' + JSON.stringify(zettel));
	

    zettelToJava();
}

function zettelToJava() {
	fetch(`http://127.0.0.1:8080/input`, {
		method: 'POST',
		headers: {
			'Access-Control-Allow-Origin': 'http://127.0.0.1:8080',
			/*	 nicht nötig, da fetch & a-c-a-o jetzt übereinstimmen,
			die abweichung localhost zu 127.0.0.1 war schon zuviel ;)
			but seems to cause trouble if missing at other computers or 
			maybe esp. at macs */
			 mode: 'no-cors', 
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(zettel),
	});
	console.log('als JSON gesendet:  ' + JSON.stringify(zettel));
	
}
