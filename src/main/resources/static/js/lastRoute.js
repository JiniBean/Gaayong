const STORAGE_KEY = 'gaayong:lastRoute';

const ALLOWED_PATHS = new Set(['/', '/expense', '/income', '/fixed', '/pay', '/budget', '/account', '/card', '/category']);
const EXCLUDED_PATHS = new Set(['/signin', '/signup']);

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

function tryRestore() {
    if (window.location.pathname !== '/') return false;

    const target = localStorage.getItem(STORAGE_KEY);
    if (!target || !isValidRoute(target)) return false;
    if (target.split('?')[0] === '/') return false;

    window.location.replace(target);
    return true;
}

function initHomeLinks() {
    document.querySelectorAll('a[href="/"]').forEach((link) => {
        link.addEventListener('click', () => {
            localStorage.setItem(STORAGE_KEY, '/');
        });
    });
}

export { saveLastRoute, tryRestore, initHomeLinks };
