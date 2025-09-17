
    // 네임스페이스
    window.__dirtyGuard = window.__dirtyGuard || {};
    (function(G){
    if (G.enabled) return;          // 중복 방지
    G.enabled = true;
    G.isDirty = false;
    G.isBypass = false;

    G._handler = function(e){
    if (!G.isDirty || G.isBypass) return;
    e.preventDefault();
    e.returnValue = '';
    return '';
};

    window.addEventListener('beforeunload', G._handler);

    // input/textarea 변경 시 더티 ON
    document.addEventListener('input', (e)=>{
    if (e.target.closest('form')) G.isDirty = true;
});

    // SE2 훅 (ID 맞게 수정)
    if (window.oEditors?.getById?.["body"]) {
    oEditors.getById["body"].exec("ADD_APP_EVENT", ["onChangeContents", ()=> G.isDirty = true]);
}

    // 의도된 이동/제출
    document.addEventListener('submit', (e)=>{
    if (e.target.matches('form')) {
    G.isBypass = true;
    G.isDirty = false;
}
}, true);

    document.addEventListener('click', (e)=>{
    const a = e.target.closest('[data-bypass-unload="true"]');
    if (a) G.isBypass = true;
}, true);

    // bfcache 복원 시 초기화
    window.addEventListener('pageshow', (e)=>{
    if (e.persisted) {
    G.isDirty = false;
    G.isBypass = false;
}
});

    // 외부에서 강제 리셋 필요할 때
    G.reset = function(){
    G.isDirty = false;
    G.isBypass = false;
};
})(window.__dirtyGuard);
