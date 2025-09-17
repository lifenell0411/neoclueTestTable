// /static/js/beforeunload-killer.js
(function () {
    function disableBeforeUnloadOnce() {
        try { window.onbeforeunload = null; } catch(_) {}
        try {
            if (window.jQuery) {
                jQuery(window).off('beforeunload');
                jQuery(document).off('beforeunload');
            }
        } catch(_) {}

        // SmartEditor2 iframe 쪽도 같이 끈다
        var iframe = document.querySelector('#editor-slot iframe') ||
            document.querySelector('iframe[src*="SmartEditor2Skin"]');
        if (iframe && iframe.contentWindow) {
            try { iframe.contentWindow.onbeforeunload = null; } catch(_) {}
        }
    }

    // 제출 직전에 여러 번 호출해서 확실히 무력화
    function disableBeforeUnloadBurst() {
        disableBeforeUnloadOnce();
        setTimeout(disableBeforeUnloadOnce, 0);
        setTimeout(disableBeforeUnloadOnce, 50);
        setTimeout(disableBeforeUnloadOnce, 150);
        setTimeout(disableBeforeUnloadOnce, 300);
    }

    // data-block-unload-on-submit 달린 폼만 대상
    document.addEventListener('submit', function (e) {
        var form = e.target;
        if (!form.matches('[data-block-unload-on-submit]')) return;

        // 네이티브 leave 팝업/에디터 훅 제거
        disableBeforeUnloadBurst();
        // 리다이렉트 전후로도 한 번 더
        window.addEventListener('pagehide', disableBeforeUnloadBurst, { once: true, capture: true });
    }, true);
})();
