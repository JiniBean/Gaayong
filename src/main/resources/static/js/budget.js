window.addEventListener("load", function () {

    // -------------- 추가 ----------------------
    const addBtn = document.querySelector(".addBtn");
    const cnclBtn = document.querySelector(".cnclBtn");
    const addCard = document.querySelector(".add");

    addBtn.onclick = function () {
        addCard.classList.toggle("d:none");
    };

    cnclBtn.onclick = function () {
        addCard.classList.add("d:none");
    }

    // -------------- 수정 ----------------------
    const cardList = document.querySelectorAll(".list");
    cardList.forEach(i => i.onclick = modToggle)

    function modToggle(e) {
        // 삭제 버튼을 클릭한 경우 수정 모드를 열지 않음
        if(e.target.classList.contains('delBtn') || e.target.closest('.delBtn')) {
            return;
        }
        
        const card = e.target.closest('.list');
        const modDiv = e.target.closest('.modDiv');
        const modCard = modDiv.querySelector('.mod');
        const cnclBtn = modDiv.querySelector('.cnclBtn');

        card.classList.add('d:none');
        modCard.classList.remove('d:none');

        cnclBtn.onclick = function () {
            const amtInput = modCard.querySelector('input[name="amt"]');
            amtInput.value = amtInput.dataset.amt
            card.classList.remove('d:none');
            modCard.classList.add('d:none');
        };
    }

    // -------------- 삭제 ----------------------
    window.addEventListener("click", function(e) {
        if(!e.target.classList.contains('delBtn')) return;
        e.stopPropagation();
        e.stopImmediatePropagation();
        if(confirm('삭제하시겠습니까?')) {
            const id = e.target.dataset.id;
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/budget?m=del';

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