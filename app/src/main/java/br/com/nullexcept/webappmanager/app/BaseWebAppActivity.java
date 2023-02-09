package br.com.nullexcept.webappmanager.app;

import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.mozilla.gecko.mozglue.GeckoLoader;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoRuntimeSettings;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import br.com.nullexcept.webappmanager.R;
import br.com.nullexcept.webappmanager.config.Config;

public class BaseWebAppActivity extends AppCompatActivity  {
    Config config;
    public static GeckoSession session;
    public static GeckoRuntime runtime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config = new Config(getClass().getName());
        config.load(getSharedPreferences("webapps",MODE_PRIVATE));


        setTitle(config.name);
        setContentView(R.layout.webapp_acitivity);
        GeckoView view = findViewById(R.id.webview);
        if (runtime == null){
            session = new GeckoSession();

            GeckoRuntimeSettings.Builder settings = new GeckoRuntimeSettings.Builder();
            settings = settings.arguments(new String[]{
                    "profile", getFilesDir().getAbsolutePath(),
                    "--profile", getFilesDir().getAbsolutePath()
            });

            runtime = GeckoRuntime.create(this, settings.build());
            session.open(runtime);
            new Thread(()->{
                try {
                    File dir = new File(getFilesDir().getParent(), "icon.png");
                    dir.getParentFile().mkdirs();
                    if (dir.exists()){
                        loadIcon();
                        return;
                    }
                    String url = config.url.substring(config.url.indexOf("://")+3);
                    if (url.contains("/")){
                        url = url.substring(0, url.indexOf("/"));
                    }
                    URLConnection connection = new URL((config.url.startsWith("https") ? "https" : "http")+"://"+url+"/favicon.ico").openConnection();
                    connection.connect();
                    InputStream down = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(down);
                    dir.delete();
                    if (bitmap.getHeight() < 1 || bitmap.getWidth() < 1)return;
                    FileOutputStream o = new FileOutputStream(dir);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, o);
                    o.close();
                    bitmap.recycle();
                    loadIcon();
                }catch (Exception e){e.printStackTrace();}
            }).start();
            view.setSession(session);

            session.loadUri(config.url);
        }
        view.setSession(session);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTaskDescription(new ActivityManager.TaskDescription(config.name, null, Color.WHITE));
        }
        setTitle(config.name);
    }

    private void loadIcon() {
        File dir = new File(getFilesDir().getParent(), "icon.png");
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setTaskDescription(new ActivityManager.TaskDescription(config.name, BitmapFactory.decodeFile(dir.getAbsolutePath()), Color.WHITE));
            }
        }catch (Exception e){}
    }

    private long oldTime = 0;

    @Override
    public void onBackPressed() {
        session.goBack();
        if(System.currentTimeMillis() - oldTime < 350){
            super.onBackPressed();
        }
        oldTime = System.currentTimeMillis();
    }

    @Override
    public File getFilesDir() {
        File file = new File(super.getFilesDir(), "instances/"+getClass().getName()+"/files");
        file.mkdirs();
        return file;
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        ApplicationInfo info = super.getApplicationInfo();
        info.dataDir = getFilesDir().getAbsolutePath();
        return info;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }
        Process.killProcess(Process.myPid());
        super.onDestroy();
    }

    @Override
    public File getCacheDir() {
        File file = new File(getFilesDir().getParent(), "cache");
        file.mkdirs();
        return file;
    }
}
