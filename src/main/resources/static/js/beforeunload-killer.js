// /static/js/beforeunload-killer.js
(function () {
    function arm(win) {
        try {
            // 1) addEventListener로의 미래 등록 차단
            const _add = win.addEventListener;
            win.addEventListener = function (type, listener, options) {
                if (type === 'beforeunload') return; // 등록 거부
                return _add.call(this, type, listener, options);
            };

            // 2) onbeforeunload 속성 할당도 영구 차단
            try {
                Object.defineProperty(win, 'onbeforeunload', {
                    configurable: true,
                    get() { return null; },
                    set(_) { /* block */ }
                });
            } catch (_) {}

            // 3) 이미 걸려있던 것 제거 + jQuery 핸들러 제거
            try { win.onbeforeunload = null; } catch (_) {}
            if (win.jQuery) { try { win.jQuery(win).off('beforeunload'); } catch (_) {} }

            // 4) 혹시 남아있어도 전파만 끊어서 네이티브 팝업 방지(캡처 단계)
            _add.call(win, 'beforeunload', function (e) {
                e.stopImmediatePropagation();
            }, true);

            // 5) 문서 객체에도 동일 적용(일부가 document에 거는 경우가 있음)
            if (win.document) {
                const _addDoc = win.document.addEventListener.bind(win.document);
                win.document.addEventListener = function (type, listener, options) {
                    if (type === 'beforeunload') return;
                    return _addDoc(type, listener, options);
                };
                try {
                    Object.defineProperty(win.document, 'onbeforeunload', {
                        configurable: true,
                        get() { return null; },
                        set(_) { /* block */ }
                    });
                } catch (_) {}
                try { win.document.onbeforeunload = null; } catch (_) {}
                _addDoc('beforeunload', function (e) { e.stopImmediatePropagation(); }, true);
            }
        } catch (e) {
            console.warn('[beforeunload-killer] arm fail:', e);
        }
    }

    // 부모 창 먼저 무장
    arm(window);

    // SmartEditor2 iframe 등장 순간까지 재시도하며 무장
    let tries = 0;
    const timer = setInterval(function () {
        const iframe =
            document.querySelector('#editor-slot iframe') ||
            document.querySelector('iframe[src*="SmartEditor2Skin"]');
        if (iframe && iframe.contentWindow) {
            arm(iframe.contentWindow);
            clearInterval(timer);
        }
        if (++tries > 50) clearInterval(timer); // 10초 정도 시도
    }, 200);

    // load 시점에 한 번 더 보강
    window.addEventListener('load', function () {
        const iframe = document.querySelector('#editor-slot iframe');
        if (iframe && iframe.contentWindow) arm(iframe.contentWindow);
    });

    console.log('[beforeunload-killer] armed');
})();
