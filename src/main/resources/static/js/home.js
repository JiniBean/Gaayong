import PWAInstaller from "/js/PWAInstaller.js";

window.addEventListener("load", function () {
    // -------------- 서비스워커 ----------------------
    const installBtn = document.querySelector("#install");
    const pwa = new PWAInstaller(installBtn);

    // service worker 지원여부
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.register('/service-worker.js')
            .then(r => console.log('ServiceWorker registration successful'))
            .catch(e => console.error('ServiceWorker registration failed', e));
    }
});