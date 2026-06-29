const STORAGE_KEY = 'gaayong:lastRoute';
const RESTORE_FLAG = 'gaayong:restoreRoute';
const RESTORED_FLAG = 'gaayong:restored';

const ALLOWED_PATHS = new Set(['/', '/expense', '/income', '/fixed', '/pay', '/budget', '/account', '/card', '/category']);
const EXCLUDED_PATHS = new Set(['/signin', '/signup']);

function isStandalone() {
    return window.matchMedia('(display-mode: standalone)').matches
        || window.navigator.standalone === true;
}

function isValidRoute(path) {
    if (!path || typeof path !== 'string') return false;
    if (!path.startsWith('/') || path.startsWith('//')) return false;
    if (/javascript:/i.test(path)) return false;

    const pathname = path.split('?')[0];
    return ALLOWED_PATHS.has(pathname);
}

function getCurrentRoute() {
    return window.location.pathname + window.location.search;
}

function saveLastRoute() {
    const pathname = window.location.pathname;
    if (EXCLUDED_PATHS.has(pathname)) return;

    const route = getCurrentRoute();
    if (!isValidRoute(route)) return;

    localStorage.setItem(STORAGE_KEY, route);
}

function setRestoreFlag() {
    const last = localStorage.getItem(STORAGE_KEY);
    if (last && isValidRoute(last)) {
        sessionStorage.setItem(RESTORE_FLAG, last);
    }
}

function tryRestore() {
    if (sessionStorage.getItem(RESTORED_FLAG)) return false;

    const pathname = window.location.pathname;
    if (pathname !== '/') return false;

    const params = new URLSearchParams(window.location.search);
    const isPwaLaunch = params.get('source') === 'pwa' && isStandalone();
    const loginRestore = sessionStorage.getItem(RESTORE_FLAG);

    let target = null;
    if (isPwaLaunch) {
        target = localStorage.getItem(STORAGE_KEY);
    } else if (loginRestore) {
        target = loginRestore;
    }

    if (!target || !isValidRoute(target)) {
        if (isPwaLaunch && params.has('source')) {
            history.replaceState(null, '', '/');
        }
        return false;
    }

    const targetPath = target.split('?')[0];
    if (targetPath === '/') {
        sessionStorage.removeItem(RESTORE_FLAG);
        if (isPwaLaunch && params.has('source')) {
            history.replaceState(null, '', '/');
        }
        return false;
    }

    sessionStorage.setItem(RESTORED_FLAG, '1');
    sessionStorage.removeItem(RESTORE_FLAG);
    window.location.replace(target);
    return true;
}

export { saveLastRoute, tryRestore, setRestoreFlag };
