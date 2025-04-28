window.addEventListener("load", function () {

    const addBtn = document.querySelector(".addBtn");
    const cnclBtn = document.querySelector(".cnclBtn");
    const addCard = document.querySelector(".add");

    // -------------- 추가 ----------------------
    addBtn.onclick = function () {
        addCard.classList.toggle("d:none");
    };

    cnclBtn.onclick = function () {
        addCard.classList.add("d:none");
    }

});