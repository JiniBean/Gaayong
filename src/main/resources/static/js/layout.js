window.addEventListener("load", function (){
    //전체 메뉴 토글
    const drawerBtn = document.querySelector(".drawer-btn");
    const drawerAside = document.querySelector('.drawer');
    const removeIcon = drawerAside.querySelector(".drawer-close");

    drawerBtn.onclick = drawerToggle;
    removeIcon.onclick = drawerToggle;
    function drawerToggle(){
        drawerAside.classList.toggle('active');
    }

    // -------------- 숫자 포맷팅 ----------------------
    const amountInputs = document.querySelectorAll('input[name="amt"]');
    amountInputs.forEach(i => {
        i.addEventListener("focus", deFormatAmount);
        i.addEventListener("blur", formatAmount);
        i.addEventListener("input", formatNumber);

    })

    function formatAmount(e) {
        e.target.value = Number(e.target.value).toLocaleString();
    }
    function deFormatAmount(e) {
        e.target.value = e.target.value.replace(/,/g, '');
    }
    function formatNumber(e) {
        e.target.value = e.target.value.replace(/[^0-9]/g, '');
    }

    const form = document.querySelectorAll('form');
    form.forEach(i => {
        i.addEventListener("submit", function(e){
            e.preventDefault()
            const amtInput = e.target.querySelector('input[name="amt"]');
            if(amtInput) amtInput.value = amtInput.value.replace(/,/g, '');
            i.submit();
        });
    });


    const theme = document.querySelector('.theme');
    theme.onclick = function () {
        const body = document.querySelector('body');
        body.classList.add('dark');
    }
})

