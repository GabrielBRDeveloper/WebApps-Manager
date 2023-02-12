package br.com.nullexcept.webappmanager.config;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Environment;

import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import br.com.nullexcept.webappmanager.util.FileUtils;

public class Config {
    private final String base;

    public String url = "";
    public String name = "";
    public String user_agent = GeckoSession.getDefaultUserAgent();
    public boolean action_bar = false;
    public boolean enable = false;
    public boolean redirects = false;
    public File data_directory;
    public Context ctx;

    public Config(Activity ctx, String base){
        this.base = base;
        this.data_directory = ctx.getFilesDir();
        this.ctx = ctx.getApplication();
    }

    public File getSaveDir(){
        return mkdirs(new File(data_directory, "instances/"+base));
    }

    private File mkdirs(File file){
        file.mkdirs();
        return file;
    }

    public File getProfileDir(){
        return mkdirs(new File(getSaveDir(), "profile"));
    }

    public void deleteAll(){
        ArrayList<File> baseDirectory = new ArrayList<>();
        baseDirectory.addAll(Arrays.asList(ctx.getExternalFilesDirs(null)));
        baseDirectory.add(ctx.getFilesDir());
        baseDirectory.add(this.data_directory);
        for (File file: baseDirectory){
            FileUtils.delete(new File(file, "instances/"+base));
        }
        enable = false;
        save();
    }

    public String getBase() {
        return base;
    }

    public void load(){
        SharedPreferences prefs = ctx.getSharedPreferences("webapps",Context.MODE_PRIVATE);

        enable = prefs.getBoolean(base, enable);

        data_directory = new File(prefs.getString(base+"/datadir", data_directory.getAbsolutePath()));

        name         = prefs.getString(base+"/name", name);
        url          = prefs.getString(base+"/url", url);
        user_agent   = prefs.getString(base+"/user_agent", user_agent);

        action_bar   = prefs.getBoolean(base+"/action_bar", action_bar);
        redirects    = prefs.getBoolean(base+"/redirects", redirects);
    }

    public void save(){
        SharedPreferences prefs = ctx.getSharedPreferences("webapps",Context.MODE_PRIVATE);
        prefs.edit()
                .putString(base+"/datadir", data_directory.getAbsolutePath())
                .putString(base+"/name", name)
                .putString(base+"/user_agent", user_agent)
                .putString(base+"/url", url)
                .putBoolean(base+"/redirects", redirects)
                .putBoolean(base+"/action_bar", action_bar)
                .putBoolean(base, enable)
                .apply();
    }


}
