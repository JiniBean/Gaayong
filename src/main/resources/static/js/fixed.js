window.addEventListener("load", function () {

    // -------------- 추가 ----------------------
    const addBtn = document.querySelector(".addBtn");
    const cnclBtns = document.querySelectorAll(".cnclBtn");
    const addCard = document.querySelector(".add");

    addBtn.onclick = function () {
        addCard.classList.toggle("d:none");
    };

    cnclBtns.forEach(btn => {
        btn.onclick = function() {
            const form = btn.closest('form');
            const section = btn.closest('section');
            if (section.classList.contains('add')) {
                section.classList.add("d:none");
            } else if (section.classList.contains('mod')) {
                const modDiv = section.closest('.modDiv');
                const list = modDiv.querySelector('.list');
                section.classList.add("d:none");
                list.classList.remove("d:none");
            }
        }
    });

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

        card.classList.add('d:none');
        modCard.classList.remove('d:none');
    }

    // -------------- 삭제 ----------------------
    window.addEventListener("click", function(e) {
        if(!e.target.classList.contains('delBtn')) return;
        e.stopPropagation();
        e.stopImmediatePropagation();
        if(confirm('삭제하시겠습니까?')) {
            const id = e.target.dataset.id;
            const form = document.createElement('form');
            form.action = '/fixed?m=del';
            form.method = 'POST';

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