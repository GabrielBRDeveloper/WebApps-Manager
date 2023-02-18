package br.com.nullexcept.webappmanager.app.basewebapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.WebExtension;

import java.util.List;

import br.com.nullexcept.webappmanager.R;
import br.com.nullexcept.webappmanager.app.BaseWebAppActivity;

public class DialogOptions {
    private AlertDialog dialog;
    private static int pluginCount = -1;
    public DialogOptions(BaseWebAppActivity ctx){
        ctx.getRuntime().getWebExtensionController().list().accept(webExtensions -> pluginCount = webExtensions.size());
        ctx.runOnUiThread(()->{
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            View view = ctx.getLayoutInflater().inflate(R.layout.dialog_actions,null, false);

            view.findViewById(R.id.opt_addons_store).setOnClickListener(v -> {
                dialog.dismiss();
                ctx.loadUrl("https://addons.mozilla.org/pt-BR/android/");
            });

            ((TextView)view.findViewById(R.id.desc)).setText("Plugin Count: "+pluginCount);
            view.findViewById(R.id.opt_gecko_settings).setOnClickListener(v -> {
                dialog.dismiss();
                ctx.loadUrl("about:config");
            });
            builder.setView(view);
            dialog = builder.create();
        });
    }

    public AlertDialog getDialog() {
        return dialog;
    }
}
