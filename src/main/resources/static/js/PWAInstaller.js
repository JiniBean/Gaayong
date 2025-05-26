// 설치 프롬프트를 저장할 변수
let deferredPrompt;

// PWA 설치 관련 기능을 별도의 클래스로 분
export default class PWAInstaller {
    constructor(btn) {
        this.btn = btn;
        this.prompt = null;
        this.init();
        this.checkInstallState();
    }

    init() {
        window.addEventListener('beforeinstallprompt', (e) => {
            e.preventDefault();
            this.prompt = e;
            this.show();
        });

        if (this.btn) {
            this.btn.addEventListener('click', () => this.install());
        }

        window.addEventListener('appinstalled', () => this.hide());
    }

    checkInstallState() {
        // PWA가 이미 설치되어 있는지 확인
        if (window.matchMedia('(display-mode: standalone)').matches || 
            window.navigator.standalone === true) {
            this.hide();
        }
    }

    show() {
        if (this.btn) this.btn.style.display = 'flex';
    }

    async install() {
        if (!this.prompt) return;
        this.prompt.prompt();
        const { outcome } = await this.prompt.userChoice;
        if (outcome === 'accepted') {
            console.log('PWA가 설치되었습니다');
            this.hide();
        }
        this.prompt = null;
    }

    hide() {
        if (this.btn) this.btn.style.display = 'none';
        this.prompt = null;
    }
}