const CACHE_NAME = 'gaayong-v1';
const urlsToCache = [
    // 스타일
    '/css/inc/root.css',
    '/css/inc/reset.css',
    '/css/inc/style.css',
    '/css/inc/button.css',
    '/css/inc/drawer.css',
    '/css/inc/form.css',
    '/css/inc/icon.css',
    '/css/inc/layout.css',
    '/css/inc/utill.css',
    '/css/budget.css',
    '/css/category.css',
    '/css/fixed.css',
    '/css/home.css',
    '/css/sign.css',

    // 아이콘
    '/icons/icon-192x192.svg',
    '/icons/icon-512x512.svg',
    '/icons/maskable-icon.svg',
    '/icons/favicon.svg',
    '/icons/add.svg',
    '/icons/menu.svg',
    '/icons/moon.svg',
    '/icons/moon-fill.svg',
    '/icons/sun.svg',
    '/icons/trash.svg',
    '/icons/signin.svg',
    '/icons/signout.svg',
    '/icons/search.svg',
    '/icons/calender.svg',
    '/icons/caret-down.svg',
    '/icons/caret-left.svg',
    '/icons/caret-right.svg',
    '/icons/toggle-left.svg',
    '/icons/toggle-right.svg',
    '/icons/x.svg',

    // 기본 파일
    '/manifest.json',
    '/',
    '/home',
    '/signin',
    '/signup',
    '/budget',
    '/category',
    '/expense',
    '/income',
    '/fixed',
    '/account'
];

// 설치 단계에서 캐시를 생성하고 필요한 파일들을 캐시에 추가
self.addEventListener('install', event => {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => {
                console.log('Caching app shell');
                return cache.addAll(urlsToCache);
            })
    );
});

// fetch 이벤트 핸들러 개선 - 네트워크 우선 전략 적용
self.addEventListener('fetch', event => {
    // HTTPS 요청으로 강제 변환
    const secureUrl = event.request.url.replace('http://', 'https://');
    const secureRequest = new Request(secureUrl, {
        method: event.request.method,
        headers: event.request.headers,
        mode: event.request.mode,
        credentials: event.request.credentials,
        redirect: event.request.redirect
    });

    event.respondWith(
        caches.match(secureRequest)
            .then(response => {
                if (response) {
                    return response;
                }
                return fetch(secureRequest)
                    .then(networkResponse => {
                        if (networkResponse.ok) {
                            const responseClone = networkResponse.clone();
                            caches.open(CACHE_NAME).then(cache => {
                                cache.put(secureRequest, responseClone);
                            });
                        }
                        return networkResponse;
                    });
            })
            .catch(() => {
                // 오프라인 폴백 처리
                return new Response('오프라인 상태입니다.', {
                    status: 503,
                    statusText: 'Service Unavailable'
                });
            })
    );
});

// 활성화 단계에서 오래된 캐시를 삭제
self.addEventListener('activate', event => {
    const cacheWhitelist = [CACHE_NAME];
    event.waitUntil(
        caches.keys()
            .then(cacheNames => {
                return Promise.all(
                    cacheNames.map(cacheName => {
                        if (!cacheWhitelist.includes(cacheName)) {
                            return caches.delete(cacheName);
                        }
                    })
                );
            })
    );
});
