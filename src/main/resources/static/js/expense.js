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
    const list = document.querySelectorAll(".list");
    list.forEach(i => i.onclick = modToggle);

    function modToggle(e) {
        console.log(e.target);
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
            form.action = '/expense?m=del';
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

    // -------------- 결제수단 ----------------------

    const pmtType = document.querySelectorAll('select[name=pmtType]');

    pmtType.forEach(i => {
        i.onchange = pmtToggle;
        i.onload = pmtToggle;

        function pmtToggle() {
            const pmtDiv = i.closest('.pmt');
            const cardList = pmtDiv.querySelector('label:has(select[name=cardId])');
            const cardSelect = pmtDiv.querySelector('select[name=cardId]');
            const acctList = pmtDiv.querySelector('label:has(select[name=acctId])');
            const acctSelect = pmtDiv.querySelector('select[name=acctId]');
            const acctIdx = Math.max(0, Array.from(acctSelect.options).findIndex(i => i.value === acctSelect.dataset.id));
            const cardIdx = Math.max(0, Array.from(cardSelect.options).findIndex(i => i.value === cardSelect.dataset.id));

            if(i.value === 'a') {
                cardSelect.value= null;
                acctSelect.selectedIndex = acctIdx;
                cardList.classList.add('d:none');
                acctList.classList.remove('d:none');
            } else {
                acctSelect.value= null;
                cardSelect.selectedIndex = cardIdx;
                cardList.classList.remove('d:none');
                acctList.classList.add('d:none');
            }
        }
    });


    // -------------- 월 이동 ----------------------
    const prev = document.querySelector(".prev");
    const next = document.querySelector(".next");

    const params = new URLSearchParams(window.location.search);
    const month = params.get('m')? parseInt(params.get('m')) : new Date().getMonth()+1;
    const year = params.get('y')? parseInt(params.get('y')) : new Date().getFullYear();

    const prevMonth = month === 1 ? 12 : month - 1;
    const prevYear = month === 1 ? year - 1 : year;
    const nextMonth = month === 12 ? 1 : month + 1;
    const nextYear = month === 12 ? year + 1 : year;

    prev.href = `/expense?m=${prevMonth}&y=${prevYear}`;
    next.href = `/expense?m=${nextMonth}&y=${nextYear}`;
});