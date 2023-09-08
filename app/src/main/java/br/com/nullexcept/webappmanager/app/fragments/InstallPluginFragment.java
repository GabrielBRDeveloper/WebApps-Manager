package br.com.nullexcept.webappmanager.app.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.WebExtension;

import java.util.ArrayList;

import br.com.nullexcept.webappmanager.R;

public class InstallPluginFragment extends Fragment {
    public WebExtension plugin;
    public GeckoResult<AllowOrDeny> result;
    private int resultCode = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_extension, container, false);

        StringBuilder description = new StringBuilder();
        description.append("<b>Author: </b>").append(plugin.metaData.creatorName).append("<br>");
        description.append("<b>Description: </b><br>");
        description.append("<p> ").append(plugin.metaData.description).append("</p><br>");
        description.append("<b> Permissions: </b><br><br>");

        for (String perm: plugin.metaData.permissions){
            description.append("&nbsp;&nbsp;&nbsp;<b>·</b>&nbsp;").append(perm.toUpperCase()).append("<br>");
        }

        Spanned spanned = Html.fromHtml(description.toString());
        ((TextView)v.findViewById(R.id.title)).setText(plugin.metaData.name);
        ((TextView)v.findViewById(R.id.version)).setText(plugin.metaData.version);
        ((TextView)v.findViewById(R.id.desc)).setText(spanned);


        v.findViewById(R.id.install).setOnClickListener((view)->{
            resultCode = 0;
            Toast.makeText(getContext(), String.format("%s installed!", plugin.metaData.name), Toast.LENGTH_LONG).show();
            destroy();
        });

        v.findViewById(R.id.cancel).setOnClickListener((view)-> destroy());

        plugin.metaData.icon.getBitmap(128).then((x)->{
            Bitmap bit = x;
            getActivity().runOnUiThread(()->{
                ((ImageView)v.findViewById(R.id.icon)).setImageBitmap(bit);
            });
            return null;
        });

        return v;
    }

    private void destroy(){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
        result.complete(resultCode == 0 ? AllowOrDeny.ALLOW : AllowOrDeny.DENY);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
