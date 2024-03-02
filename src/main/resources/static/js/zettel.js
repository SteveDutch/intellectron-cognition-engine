document.getElementById('change').addEventListener('click', function () {
    console.log('change clicked!');
    let zettel = document.getElementById("zettel");
    const zettelData = zettel.innerHTML;
    zettel.innerHTML = '<form action="/input" method="post" th:object="${ZettelDtoRecord}">' + zettelData + '</form>';
   });