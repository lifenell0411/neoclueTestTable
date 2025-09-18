// /js/beforeunload-killer.js
(function(){
    function apply(win){
        try{
            // 1) 이후 등록될 beforeunload 리스너 무시
            var _add = win.addEventListener.bind(win);
            win.addEventListener = function(type, listener, options){
                if (type === 'beforeunload') return; // 등록 자체 무시
                return _add(type, listener, options);
            };

            // 2) onbeforeunload 직접 할당 봉인
            try{
                Object.defineProperty(win, 'onbeforeunload', {
                    configurable: true,
                    get(){ return null; },
                    set(_){ /* block */ }
                });
            }catch(_){
                try { win.onbeforeunload = null; } catch(__){}
            }

            // 3) 혹시 남았을지 모를 핸들러 전파 차단(캡처 단계)
            _add('beforeunload', function(e){
                // 절대 preventDefault/returnValue 금지!
                e.stopImmediatePropagation();
            }, true);

            // 4) jQuery로 붙은 핸들러 off
            if (win.jQuery){
                try { win.jQuery(win).off('beforeunload'); win.jQuery(win.document).off('beforeunload'); } catch(_){}
            }

            // 디버그
            if (win.console && win.location) {
                win.console.debug('[beforeunload-killer] patched:', win.location.href);
            }
        }catch(e){
            try { win.console && win.console.debug('[beforeunload-killer] error', e); } catch(_){}
        }
    }

    // 상위창에 즉시 적용
    apply(window);

    // 에디터 iframe(과 이후 동적 생성분)에도 적용
    function patchIframes(){
        document.querySelectorAll('iframe').forEach(function(ifr){
            try{
                var w = ifr.contentWindow || (ifr.contentDocument && ifr.contentDocument.defaultView);
                if (w && !w.__beforeunloadKillerApplied){
                    apply(w);
                    w.__beforeunloadKillerApplied = true;
                }
            }catch(_){}
        });
    }
    // 초반 폴링 + 변이감시 (모달 열릴 때 등)
    var tries = 0, t = setInterval(function(){
        patchIframes();
        if (++tries > 80) clearInterval(t);
    }, 120);

    var mo = new MutationObserver(function(){ patchIframes(); });
    mo.observe(document.documentElement, { childList:true, subtree:true });
})();
