package br.com.nullexcept.webappmanager.web;

import android.widget.TextView;

import androidx.annotation.NonNull;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.MediaSession;
import org.mozilla.geckoview.WebExtension;

import br.com.nullexcept.webappmanager.R;
import br.com.nullexcept.webappmanager.app.BaseWebAppActivity;
import br.com.nullexcept.webappmanager.config.Config;
import br.com.nullexcept.webappmanager.web.delegates.*;
public class WebSession extends GeckoSession {
    private Class current;
    private final PluginPromptListener pluginPromptListener;
    public WebSession(Object current){
        this.current = current.getClass();
        getSettings().setUserAgentOverride(config().user_agent);
        getSettings().setUseTrackingProtection(true);

        setProgressDelegate(new ProcessListener(this));
        setContentDelegate(new ContentListener(this));
        setNavigationDelegate(new NavigationListener(this));
        setPromptDelegate(new PromptListener(this));
        pluginPromptListener = new PluginPromptListener(this);
    }

    public void resumeUI(){
        ((TextView)context().findViewById(R.id.title)).setText(((ContentListener)getContentDelegate()).getTitle());
        ((TextView)context().findViewById(R.id.url)).setText(((NavigationListener)getNavigationDelegate()).getLocation());
    }

    @Override
    public void open(@NonNull GeckoRuntime runtime) {
        super.open(runtime);
        runtime.getWebExtensionController().setPromptDelegate(pluginPromptListener);
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
