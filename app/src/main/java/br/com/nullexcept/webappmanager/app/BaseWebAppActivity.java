package br.com.nullexcept.webappmanager.app;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoRuntimeSettings;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;
import org.mozilla.geckoview.WebExtension;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import br.com.nullexcept.webappmanager.R;
import br.com.nullexcept.webappmanager.app.basewebapp.DialogOptions;
import br.com.nullexcept.webappmanager.app.fragments.InstallPluginFragment;
import br.com.nullexcept.webappmanager.config.Config;
import br.com.nullexcept.webappmanager.web.WebSession;

public class BaseWebAppActivity extends AppCompatActivity  {
    Config config;
    public static BaseWebAppActivity CURRENT_CONTEXT;
    public static Config             CURRENT_CONFIG;
    public static GeckoSession session;
    public static GeckoRuntime runtime;
    public Handler handler;
    private final ArrayList<String> extensions = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
        config = new Config(this, getClass().getName());
        config.load();
        CURRENT_CONFIG = config;
        CURRENT_CONTEXT = this;


        if (!config.enable){
            Toast.makeText(this, R.string.not_exists, Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }
            return;
        }

        setTitle(config.name);
        setContentView(R.layout.webapp_acitivity);

        if (session == null){
            GeckoRuntimeSettings.Builder settings = new GeckoRuntimeSettings.Builder();
            settings = settings.arguments(new String[]{
                    "--profile", config.getProfileDir().getAbsolutePath(),
                    "-purgecaches"
            });

            session = new WebSession(this);
            GeckoRuntimeSettings build = settings.build();

            build.setAboutConfigEnabled(true);

            runtime = GeckoRuntime.create(this, build);

            session.open(runtime);
            loadUrl(config.url);
        } else {
            ((WebSession)session).resumeUI();
        }

        ((GeckoView)findViewById(R.id.webview)).setSession(session);
        log(session);

        findViewById(R.id.options).setOnClickListener(v -> {
            new DialogOptions(this).getDialog().show();
        });

        if (!config.action_bar){
            findViewById(R.id.header).setVisibility(View.INVISIBLE);
            findViewById(R.id.content).setPadding(0,0,0,0);
        }

        setTaskDescription(new ActivityManager.TaskDescription(config.name));
        if (new File(config.getSaveDir(), "icon.png").exists()){
            loadIcon();
        }

        new Thread(()->{
            File extensions = new File(config.getProfileDir(), "/plugins");
            extensions.mkdirs();
            File[] list = extensions.listFiles();

            for (File file : list){
                String uri = file.toURI().toString();
                runOnUiThread(()-> runtime.getWebExtensionController().install(uri));
            }
        }).start();

        refreshExtensions();
    }

    private void loadIcon() {
        new Thread(()->{
            try {
                Bitmap icon = BitmapFactory.decodeFile(new File(config.getSaveDir(), "icon.png").getAbsolutePath());
                if (icon.getWidth() > 1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        setTaskDescription(new ActivityManager.TaskDescription(config.name, icon));
                    }
                }
            }catch (Exception e){}
        }).start();
    }


    public GeckoRuntime getRuntime() {
        return runtime;
    }

    public GeckoSession getSession() {
        return session;
    }

    public void error(Object... items){
        for (Object obj: items){
            _log(2, obj);
        }
    }

    public void log(Object... items){
        for (Object obj: items){
            _log(1, obj);
        }
    }

    private void _log(int level, Object obj){
        if (obj instanceof Throwable){
            try {
                Throwable th = (Throwable) obj;
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream stream = new PrintStream(out);
                th.printStackTrace(stream);
                stream.flush();
                out.close();
                obj = out.toString("utf8");
            } catch (Exception e){}
        }
        switch (level){
            case 0: Log.i(getClass().getSimpleName(), obj+""); break;
            case 1: Log.d(getClass().getSimpleName(), obj+""); break;
            case 2: Log.e(getClass().getSimpleName(), obj+""); break;
        }
    }


    private long LAST_BACK = 0;
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getFragments().size() > 0){
            super.onBackPressed();
            return;
        }
        if (System.currentTimeMillis()-LAST_BACK < 500){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else finish();
            Process.killProcess(Process.myPid());
            super.onBackPressed();

        } else {
            if (session != null){
                session.goBack();
            }
        }
        LAST_BACK = System.currentTimeMillis();
    }

    public void loadUrl(String url) {
        session.loadUri(url);
    }

    public URL parseURL(String url){
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void installExtension(WebExtension extension, GeckoResult<AllowOrDeny> result) {
        if (!extension.location.startsWith("file:")){
            result.complete(AllowOrDeny.DENY);
            return;
        }

        File file = new File(parseURL(extension.location).getPath()).getAbsoluteFile();
        File pluginFolder = new File(config.getProfileDir(), "/plugins").getAbsoluteFile();

        if (file.equals(pluginFolder.getParentFile())){
            Toast.makeText(this, "INVALID PLUGIN PATH", Toast.LENGTH_LONG).show();;
            result.complete(AllowOrDeny.DENY);
            return;
        }


        InstallPluginFragment fragment = new InstallPluginFragment();
        fragment.plugin = extension;
        fragment.result = result;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    protected void refreshExtensions(){
        runtime.getWebExtensionController().list().then((ls)->{
            extensions.clear();
            for (WebExtension extension : ls){
                log(String.format(
                        "----------------------\n"
                                +"Name: %s\n"
                                +"Version: %s\n"
                                +"Enable: %s\n"
                                +"---------------------", extension.metaData.name, extension.metaData.version, extension.metaData.enabled));
            }
            return null;
        });
    }
}
