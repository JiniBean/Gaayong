window.addEventListener("load", function () {

    const addBtn = document.querySelector(".addBtn");
    const cnclBtns = document.querySelectorAll(".cnclBtn");
    const addCard = document.querySelector(".add");

    addBtn.onclick = function () {
        addCard.classList.toggle("d:none");
    };

    cnclBtns.forEach(btn => {
        btn.onclick = function() {
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

    const cardList = document.querySelectorAll(".list");
    cardList.forEach(i => i.onclick = modToggle);

    function modToggle(e) {
        if (e.target.classList.contains('delBtn') || e.target.closest('.delBtn')) {
            return;
        }
        if (e.target.classList.contains('pmtMod') || e.target.closest('.pmtMod')) {
            return;
        }
        const card = e.target.closest('.list');
        const modDiv = e.target.closest('.modDiv');
        const modCard = modDiv.querySelector('.mod');

        card.classList.add('d:none');
        modCard.classList.remove('d:none');
    }

    window.addEventListener("click", function(e) {
        if (!e.target.classList.contains('delBtn')) return;
        e.stopPropagation();
        e.stopImmediatePropagation();
        if (confirm('삭제하시겠습니까?')) {
            const params = new URLSearchParams(window.location.search);
            const form = document.createElement('form');
            form.action = '/pay?m=del';
            form.method = 'POST';

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = e.target.dataset.id;
            form.appendChild(idInput);

            ['y', 'm', 'c'].forEach(key => {
                const value = params.get(key);
                if (value) {
                    const input = document.createElement('input');
                    input.type = 'hidden';
                    input.name = key === 'm' ? 'month' : key;
                    input.value = value;
                    form.appendChild(input);
                }
            });

            document.body.appendChild(form);
            form.submit();
        }
    });

    window.addEventListener("click", function(e) {
        if (!e.target.classList.contains('pmtMod')) return;
        e.stopPropagation();
        e.stopImmediatePropagation();

        const params = new URLSearchParams(window.location.search);
        const form = document.createElement('form');
        form.action = '/pay?m=mod';
        form.method = 'POST';

        const fields = {
            id: e.target.dataset.id,
            name: e.target.dataset.name,
            dd: e.target.dataset.dd,
            ctgId: e.target.dataset.ctgid,
            amt: e.target.dataset.amt
        };

        Object.entries(fields).forEach(([name, value]) => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = name;
            input.value = value ?? '';
            form.appendChild(input);
        });

        if (e.target.dataset.paid !== 'true') {
            const paidInput = document.createElement('input');
            paidInput.type = 'hidden';
            paidInput.name = 'isPaid';
            paidInput.value = 'on';
            form.appendChild(paidInput);
        }

        ['y', 'm', 'c'].forEach(key => {
            const value = params.get(key);
            if (value) {
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = key === 'm' ? 'month' : key;
                input.value = value;
                form.appendChild(input);
            }
        });

        document.body.appendChild(form);
        form.submit();
    });

    const prev = document.querySelector(".prev");
    const next = document.querySelector(".next");
    const params = new URLSearchParams(window.location.search);
    const month = params.get('m') ? parseInt(params.get('m')) : new Date().getMonth() + 1;
    const year = params.get('y') ? parseInt(params.get('y')) : new Date().getFullYear();
    const category = params.get('c');

    const prevMonth = month === 1 ? 12 : month - 1;
    const prevYear = month === 1 ? year - 1 : year;
    const nextMonth = month === 12 ? 1 : month + 1;
    const nextYear = month === 12 ? year + 1 : year;

    const categoryQuery = category ? `&c=${category}` : '';
    prev.href = `/pay?m=${prevMonth}&y=${prevYear}${categoryQuery}`;
    next.href = `/pay?m=${nextMonth}&y=${nextYear}${categoryQuery}`;

    const ctgBtns = document.querySelector('.ctg-btn');
    const leftBtn = document.querySelector('.scroll-left');
    const rightBtn = document.querySelector('.scroll-right');

    if (ctgBtns && leftBtn && rightBtn) {
        const scrollAmount = ctgBtns.offsetWidth * 0.9;
        leftBtn.addEventListener('click', () => {
            ctgBtns.scrollLeft -= scrollAmount;
        });
        rightBtn.addEventListener('click', () => {
            ctgBtns.scrollLeft += scrollAmount;
        });
    }

    if (ctgBtns) {
        const selectedBtn = ctgBtns.querySelector('.btn-color\\:main-2');
        if (selectedBtn) {
            const containerWidth = ctgBtns.offsetWidth;
            const scrollWidth = ctgBtns.scrollWidth;
            const buttonLeft = selectedBtn.offsetLeft;
            const buttonWidth = selectedBtn.offsetWidth;

            let scrollPosition = buttonLeft - (containerWidth / 2) + (buttonWidth / 2);
            scrollPosition = Math.max(0, scrollPosition);
            scrollPosition = Math.min(scrollWidth - containerWidth, scrollPosition);

            ctgBtns.scrollLeft = scrollPosition;
        }
    }
});
