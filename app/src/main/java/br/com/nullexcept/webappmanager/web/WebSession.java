package br.com.nullexcept.webappmanager.web;

import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.MediaSession;

import br.com.nullexcept.webappmanager.app.BaseWebAppActivity;
import br.com.nullexcept.webappmanager.config.Config;
import br.com.nullexcept.webappmanager.web.delegates.ContentListener;
import br.com.nullexcept.webappmanager.web.delegates.NavigationListener;
import br.com.nullexcept.webappmanager.web.delegates.ProcessListener;
import br.com.nullexcept.webappmanager.web.delegates.PromptListener;

public class WebSession extends GeckoSession {
    private Class current;
    public WebSession(Object current){
        this.current = current.getClass();
        getSettings().setUserAgentOverride(config().user_agent);
        setProgressDelegate(new ProcessListener(this));
        setContentDelegate(new ContentListener(this));
        setNavigationDelegate(new NavigationListener(this));
        setPromptDelegate(new PromptListener(this));
    }

    public void log(Object... items){
        context().log(items);
    }

    public void error(Object... items){
        context().error(items);
    }

    public Config config(){
        try {
            return (Config) current.getField("CURRENT_CONFIG").get(null);
        } catch (Exception e) {}
        return null;
    }

    public BaseWebAppActivity context(){
        try {
            return (BaseWebAppActivity) current.getField("CURRENT_CONTEXT").get(null);
        } catch (Exception e) {}
        return null;
    }
}
