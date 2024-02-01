console.log('Vanilla JS wurde aufgerufen!');

let submitBtn = document.getElementById('submit');

submitBtn.addEventListener('click', function(event) {
	// suppress HTML sending form 
    event.preventDefault();
	console.log('hooray! submit was clicked');
	prepareZettel();
	
});

function prepareZettel() {
	console.log('juhu, funct. prepareZettel wurde aufgerufen');
	//zettel.zettelTopic = getElementById('title');
    zettelToJava();
}

function zettelToJava() {
	console.log('Zettel =   ' + zettel);
	fetch(`http://127.0.0.1:8080/channel`, {
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
}
