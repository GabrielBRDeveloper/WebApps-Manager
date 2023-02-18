package br.com.nullexcept.webappmanager.web.delegates;

import org.mozilla.geckoview.GeckoSession;

import br.com.nullexcept.webappmanager.web.WebSession;

public class PromptListener implements GeckoSession.PromptDelegate {
    private WebSession session;
    public PromptListener(WebSession session){
        this.session = session;
    }
}
