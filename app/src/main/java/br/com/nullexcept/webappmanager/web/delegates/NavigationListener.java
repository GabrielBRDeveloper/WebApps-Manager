package br.com.nullexcept.webappmanager.web.delegates;

import android.annotation.SuppressLint;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.WebExtension;
import org.mozilla.geckoview.WebExtensionController;

import java.util.List;

import br.com.nullexcept.webappmanager.R;
import br.com.nullexcept.webappmanager.web.WebSession;

public class NavigationListener implements GeckoSession.NavigationDelegate {
    private WebSession session;
    public NavigationListener(WebSession session){
        this.session = session;
    }
    @Nullable
    @Override
    public GeckoResult<GeckoSession> onNewSession(@NonNull GeckoSession current, @NonNull String uri) {
        session.loadUri(uri);
        session.log("Requested new session!!");
        return null;
    }

    @SuppressLint("WrongThread")
    @Nullable
    @Override
    public GeckoResult<AllowOrDeny> onLoadRequest(@NonNull GeckoSession session, @NonNull LoadRequest request) {
        String url = request.uri+"";
        if (url.startsWith("https://addons.mozilla.org/") && url.endsWith(".xpi")){
            this.session.log("REQUIRES INSTALL NEW PLUGIN");
            this.session.context().getRuntime().getWebExtensionController().install(url).accept(webExtension -> {
                NavigationListener.this.session.context().getRuntime().getWebExtensionController().enable(webExtension, WebExtensionController.EnableSource.USER);
            });
        }
        return GeckoSession.NavigationDelegate.super.onLoadRequest(session, request);
    }

    @Override
    public void onLocationChange(@NonNull GeckoSession current, @Nullable String url, @NonNull List<GeckoSession.PermissionDelegate.ContentPermission> perms) {
        GeckoSession.NavigationDelegate.super.onLocationChange(current, url, perms);
        session.context().runOnUiThread(()->{
            ((TextView)session.context().findViewById(R.id.url)).setText(url);
        });
    }
}
