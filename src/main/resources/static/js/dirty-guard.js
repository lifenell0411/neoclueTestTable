(function () {
    let dirty = false;     // 사용자가 뭔가 수정했는지 여부
    let submitting = false; // 폼 제출 중(이때는 나가기 경고 띄우지 않음)

    function markDirty(){ dirty = true; }

    // 기본 입력들 감지
    document.addEventListener('input', function(e){
        const t = e.target;
        if (t.matches('#title, textarea#body, input[type="file"]')) markDirty();
    }, true);

    // 폼 제출 시 한번은 나가기 경고 끄기
    document.addEventListener('submit', function(){
        submitting = true;
        // 혹시 SPA가 아니니, 안전하게 1초 뒤 풀어둠
        setTimeout(()=>{ submitting = false; }, 1000);
    }, true);

    // SmartEditor2(iframe) 내부 입력도 dirty 처리 — se2 준비 신호에 맞춰 바인딩
    window.addEventListener('se2:ready', function(){
        try {
            const iframe = document.querySelector('#editor-slot iframe');
            const doc = iframe && (iframe.contentDocument || iframe.contentWindow?.document);
            if (doc) {
                doc.addEventListener('input', markDirty, true);
                doc.addEventListener('keyup', markDirty, true);
            }
        } catch (_) {}
    });
    document.addEventListener('click', (e)=>{
        const a = e.target.closest('[data-bypass-unload="true"],[data-skip-leave-warning]');
        if (a) {
            window.__dirtyGuard && (window.__dirtyGuard.isBypass = true);
        }
    }, true);

    // 나가기 경고: 수정했고, 지금 제출 중이 아닐 때만
    window.addEventListener('beforeunload', function (e) {
        if (!dirty || submitting) return;
        e.preventDefault();
        e.returnValue = ''; // 크롬 등 표준 동작
    });
})();
