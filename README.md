# slark
zouooh's http lib

1.Init.
       
        Slark.DEBUG_DATAS = true;
        Slark.DEBUG = true;
        Slark.init(getApplication());
2.Get.      
        
        Slark.with(getApplication()).get("http:xxxxx/xxx?xx=xx").param("a", "b")
                .progress(LogProgress
                .obtain()).response(new TextResponse() {
            @Override
            public void onRequestSuccess(Request request, String s) {
                Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
            }
        }).request();
