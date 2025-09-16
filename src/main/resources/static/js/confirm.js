

(function () {
    // 중복 포함되더라도 한 번만 바인딩
    if (window.__confirmBound) return;
    window.__confirmBound = true;

    // 폼 submit에 data-confirm 있으면 확인
    document.addEventListener('submit', function (e) {
        const form = e.target;
        if (!form.matches('[data-confirm]')) return;
        const msg = form.getAttribute('data-confirm') || '진행하시겠습니까?';
        if (!confirm(msg)) {
            e.preventDefault();
            e.stopImmediatePropagation();
        }
    }, true); // 캡처 단계

    // 링크/버튼에도 data-confirm 있으면 확인
    document.addEventListener('click', function (e) {
        const el = e.target.closest('a[data-confirm], button[data-confirm]');
        if (!el) return;
        const msg = el.getAttribute('data-confirm') || '진행하시겠습니까?';
        if (!confirm(msg)) {
            e.preventDefault();
            e.stopImmediatePropagation();
        }
    }, true);
})();