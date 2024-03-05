document.getElementById('change').addEventListener('click', function () {
    console.log('change clicked!');
    let zettel = document.getElementById("zettel");
    const zettelData = zettel.innerHTML;
    zettel.innerHTML = '<form action="/input" method="post" th:object="${ZettelDtoRecord}">' + zettelData + '</form>';
   });

   //get the modal
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
