window.addEventListener("load", function () {

    // -------------- 추가 ----------------------
    const addBtn = document.querySelector(".addBtn");
    const cnclBtn = document.querySelector(".cnclBtn");
    const addCard = document.querySelector(".add");

    addBtn.onclick = function () {
        addCard.classList.toggle('d:none');
    };

    cnclBtn.onclick = function () {
        addCard.classList.add('d:none');
    }

    // -------------- 수정 ----------------------
    window.addEventListener("click", function (e){
      if(!e.target.classList.contains('modBtn')) return;

      const card = e.target.closest('.card');
      const modDiv = e.target.closest('.modDiv');
      const modCard = modDiv.querySelector('.mod');
      const cnclBtn = modDiv.querySelector('.cnclBtn');

      card.classList.toggle('d:none');
      modCard.classList.toggle('d:none');

      cnclBtn.onclick = function () {
          card.classList.toggle('d:none');
          modCard.classList.toggle('d:none');
      };
    });

    // -------------- 삭제 ----------------------
    window.addEventListener("click", function(e) {
        if(!e.target.classList.contains('delBtn')) return;

        if(confirm('삭제하시겠습니까?')) {
            const id = e.target.dataset.id;
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/category?m=del';

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = id;

            form.appendChild(idInput);
            document.body.appendChild(form);
            form.submit();
        }
    });

});