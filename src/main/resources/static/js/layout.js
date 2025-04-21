
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
})

