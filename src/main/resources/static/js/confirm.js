// /static/js/confirm.js
(function () {
    document.addEventListener('submit', function (e) {
        const form = e.target;

        // 1) data-confirm 있으면 확인창
        if (form.matches('[data-confirm]')) {
            const msg = form.getAttribute('data-confirm') || '진행하시겠습니까?';
            if (!confirm(msg)) {
                e.preventDefault();
                e.stopImmediatePropagation();
                return;
            }
        }

        // 2) SmartEditor2 쓰는 폼이면 전송 직전에 본문을 textarea(name=body)로 복사
        try {
            const ta = form.querySelector('textarea[name="body"]');  // ← pf 상관없이 찾기
            const id = ta && ta.id;
            if (id && window.oEditors?.getById?.[id]) {
                oEditors.getById[id].exec('UPDATE_CONTENTS_FIELD', []);
            }
        } catch (_) { /* noop */ }
    }, true);

    // (옵션) 링크/버튼 confirm
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
