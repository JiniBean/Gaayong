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
        //결제여부 변경 버튼 클릭한 경우 수정모드 열지 않음
        if(e.target.classList.contains('pmtMod') || e.target.closest('.pmtMod')) {
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

    // -------------- 결제 변경 ----------------------
    window.addEventListener("click", function(e) {
        if(!e.target.classList.contains('pmtMod')) return;
        e.stopPropagation();
        e.stopImmediatePropagation();
        const form = document.createElement('form');
        form.action = '/fixed?m=mod';
        form.method = 'POST';

        const idInput = document.createElement('input');
        idInput.type = 'hidden';
        idInput.name = 'id';
        idInput.value = e.target.dataset.id;
        form.appendChild(idInput);

        const fixed = document.createElement('input');
        fixed.type = 'hidden';
        fixed.name = 'fixedId';
        fixed.value = e.target.dataset.id;
        form.appendChild(fixed);

        const paidInput = document.createElement('input');
        paidInput.type = 'hidden';
        paidInput.name = 'isPaid';
        paidInput.value = (e.target.dataset.paid === 'true') ? null : 'on';
        console.log(paidInput.value)
        form.appendChild(paidInput);

        const nameInput = document.createElement('input');
        nameInput.type = 'hidden';
        nameInput.name = 'name';
        nameInput.value = e.target.dataset.name;
        form.appendChild(nameInput);

        const ddInput = document.createElement('input');
        ddInput.type = 'hidden';
        ddInput.name = 'dd';
        ddInput.value = e.target.dataset.dd? e.target.dataset.dd : null;
        form.appendChild(ddInput);

        const ctgIdInput = document.createElement('input');
        ctgIdInput.type = 'hidden';
        ctgIdInput.name = 'ctgId';
        ctgIdInput.value = e.target.dataset.ctgid;
        form.appendChild(ctgIdInput);

        const amtInput = document.createElement('input');
        amtInput.type = 'hidden';
        amtInput.name = 'amt';
        amtInput.value = e.target.dataset.amt;
        form.appendChild(amtInput);

        const acctIdInput = document.createElement('input');
        acctIdInput.type = 'hidden';
        acctIdInput.name = 'acctId';
        acctIdInput.value = e.target.dataset.acctid? e.target.dataset.acctid : null;
        form.appendChild(acctIdInput);

        const cardIdInput = document.createElement('input');
        cardIdInput.type = 'hidden';
        cardIdInput.name = 'cardId';
        cardIdInput.value = e.target.dataset.cardid ? e.target.dataset.cardid : null;
        form.appendChild(cardIdInput);

        const typeInput = document.createElement('input');
        typeInput.type = 'hidden';
        typeInput.name = 'type';
        typeInput.value = 'FIX';
        form.appendChild(typeInput);

        document.body.appendChild(form);
        form.submit();
    });

    // -------------- 결제수단 ----------------------

    const pmtType = document.querySelectorAll('select[name=pmtType]');

    pmtType.forEach(i => {
        i.onchange = pmtToggle;
        pmtToggle();

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

});