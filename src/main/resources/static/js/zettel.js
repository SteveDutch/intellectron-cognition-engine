document.getElementById('change').addEventListener('click', function () {
    console.log('change clicked!');
    // Erstellen Sie das neue Formular-Element
    let form = document.createElement('form');

    // Fügen Sie das Formular-Element zum div-Container hinzu
    // TODO funktioniert aber so nicht, vermutlich wie im Ass13!
    document.getElementById('zettel').insertAdjacentHTML('beforebegin','<form>');
   // document.getElementById('zettel').insertAdjacentHTML('afterend','</form>');

    // var tagsContainer = document.getElementById('tagsContainer');
    // var newInput = document.createElement('input');>'
    // newInput.type = 'text';
    // newInput.name = 'tagInput'; // Name attribute to bind the input to an ArrayList
    // tagsContainer.appendChild(newInput);
});