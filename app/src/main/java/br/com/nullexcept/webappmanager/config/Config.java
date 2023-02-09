package br.com.nullexcept.webappmanager.config;

import android.content.SharedPreferences;

import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class Config {
    private final String base;

    public String url = "";
    public String name = "";
    public String user_agent = GeckoSession.getDefaultUserAgent();
    public boolean action_bar = false;
    public boolean enable = false;
    public boolean redirects = false;

    public Config(String base){
        this.base = base;
    }


    public String getBase() {
        return base;
    }

    public void load(SharedPreferences prefs){
        enable = prefs.getBoolean(base, enable);

        name         = prefs.getString(base+"/name", name);
        url          = prefs.getString(base+"/url", url);
        user_agent   = prefs.getString(base+"/user_agent", user_agent);

        action_bar   = prefs.getBoolean(base+"/action_bar", action_bar);
        redirects    = prefs.getBoolean(base+"/redirects", redirects);
    }

    public void save(SharedPreferences prefs){
        prefs.edit()
                .putString(base+"/name", name)
                .putString(base+"/user_agent", user_agent)
                .putString(base+"/url", url)
                .putBoolean(base+"/redirects", redirects)
                .putBoolean(base+"/action_bar", action_bar)
                .putBoolean(base, enable)
                .apply();
    }


}
