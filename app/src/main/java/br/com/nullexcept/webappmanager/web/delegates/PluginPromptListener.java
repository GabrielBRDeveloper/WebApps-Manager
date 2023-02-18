package br.com.nullexcept.webappmanager.web.delegates;

import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.WebExtension;
import org.mozilla.geckoview.WebExtensionController;

import br.com.nullexcept.webappmanager.R;
import br.com.nullexcept.webappmanager.web.WebSession;

public class PluginPromptListener implements WebExtensionController.PromptDelegate {
    private WebSession session;
    public PluginPromptListener(WebSession session){
        this.session = session;
    }

    @Nullable
    @Override
    public GeckoResult<AllowOrDeny> onInstallPrompt(@NonNull WebExtension extension) {
        GeckoResult result =  new GeckoResult<>();
        session.log("REQUIRES INSTALL ADDON");

        session.context().runOnUiThread(()->{
            AlertDialog.Builder builder = new AlertDialog.Builder(session.context());
            View view = session.context().getLayoutInflater().inflate(R.layout.dialog_install_plugin, null, false);
            builder.setOnCancelListener(dialog -> {
                result.complete(AllowOrDeny.DENY);
            });
            ((TextView)view.findViewById(R.id.title)).setText(extension.metaData.name);
            ((TextView)view.findViewById(R.id.desc)).setText("From: "+extension.location+"\n\n"+extension.metaData.description);

            builder.setView(view);
            AlertDialog dialog = builder.create();
            view.findViewById(R.id.cancel).setOnClickListener(v -> {dialog.cancel();});
            view.findViewById(R.id.install).setOnClickListener(v -> {
                result.complete(AllowOrDeny.DENY);
                dialog.dismiss();
            });
            dialog.show();
        });

        return result;
    }
}
