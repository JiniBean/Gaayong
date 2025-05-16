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

    // -------------- 다크모드 ----------------------
    const body = document.querySelector('body');
    const themePC = document.querySelector('.theme-pc');
    const theme = document.querySelector('.theme');
    const isLogin = JSON.parse(theme.dataset.theme);
    const lightBtn = theme.querySelector('.light-mode');
    const darkBtn = theme.querySelector('.dark-mode');

    //로그인 정보 없을 때
    const localMode= localStorage.getItem('theme') || null;
    if (localMode) themeToggle(localMode);

    themePC.onclick = () => setTheme(body.classList.contains('light') ? 'dark' : 'light');
    lightBtn.onclick = ()=> setTheme('light');
    darkBtn.onclick = ()=> setTheme('dark');

    function setTheme(mode){
        isLogin ? localStorage.removeItem('theme') : localStorage.setItem('theme', mode);

        if(!isLogin) {
            themeToggle(mode);
            return;
        }

        fetch(`/api/user/${mode}`, {
            method: 'GET',
            credentials: 'same-origin'
        })
            .then(response => {
                if (response.ok) themeToggle(mode);
            })
            .catch(error => console.error('오류:', error));


    }

    function themeToggle(mode){
        body.classList.toggle('light', mode === 'light');
        theme.classList.toggle('bd-color:base-2', mode === 'light');
        lightBtn.classList.toggle('icon-color:sub-1', mode === 'light');
        lightBtn.classList.toggle('bg-color:base-3', mode === 'light');
        lightBtn.classList.toggle('bg-color:base-2', mode === 'light');
        darkBtn.classList.toggle('icon-color:base-4', mode === 'light');

        body.classList.toggle('dark', mode === 'dark');
        theme.classList.toggle('bd-color:base-10', mode === 'dark');
        lightBtn.classList.toggle('icon-color:base-5', mode === 'dark');
        darkBtn.classList.toggle('bg-color:base-10', mode === 'dark');
        darkBtn.classList.toggle('icon-color:base-2', mode === 'dark');

    }
})

