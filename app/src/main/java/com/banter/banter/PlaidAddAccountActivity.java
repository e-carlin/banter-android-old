package com.banter.banter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

import java.util.HashMap;

//TODO: Lots of work to be done in here cleaning things up and doing proper error handling

public class PlaidAddAccountActivity extends AppCompatActivity {

    private final String PLAID_PUBLIC_KEY = "fb846942c3ce8e2945b4b1fd408333";

    WebView addAccountView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plaid_add_account);

        openPlaidAddAccountWebView();
    }

    private void openPlaidAddAccountWebView() {
        //Get link configuration options
        final HashMap<String, String> linkInitializeOptions = getLinkInitializeOptions();
        // Generate the Link initialization URL based off of the configuration options
        final Uri linkInitializationUrl = generateLinkInitializationUrl(linkInitializeOptions);
        //Get configured webView object
        final WebView plaidLinkWebView = getConfiguredPlaidLinkWebView();
        // Initialize Link by loading the Link initiaization URL in the WebView
        plaidLinkWebView.loadUrl(linkInitializationUrl.toString());
        // Override the WebView's handler for redirects
        // Link communicates success and failure (analogous to the web's onSuccess and onExit
        // callbacks) via redirects.
        plaidLinkWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Parse the URL to determine if it's a special Plaid Link redirect or a request
                // for a standard URL (typically a forgotten password or account not setup link).
                // Handle Plaid Link redirects and open traditional pages directly in the  user's
                // preferred browser.
                Uri parsedUri = Uri.parse(url);
                if (parsedUri.getScheme().equals("plaidlink")) {
                    String action = parsedUri.getHost();
                    HashMap<String, String> linkData = parseLinkUriData(parsedUri);
                    JSONObject json = new JSONObject(linkData);

                    if (action.equals("connected")) {
                        //Success! We got the details from Plaid
                        // Now send them to our API

                        System.out.println("***************************************");
                        System.out.println("JSON FROM HASH: "+json);
                        for (String name: linkData.keySet()){

                            String key =name.toString();
                            String value = linkData.get(name).toString();
                            System.out.println(key + " " + value);
                        }
                        System.out.println("***************************************");


                        //TODO: Go to UserDetails activity
                        plaidLinkWebView.loadUrl(linkInitializationUrl.toString());
                    } else if (action.equals("exit")) {
                        // User exited
                        // linkData may contain information about the user's status in the Link flow,
                        // the institution selected, information about any error encountered,
                        // and relevant API request IDs.
                        Log.d("User status in flow: ", linkData.get("status"));
                        // The requet ID keys may or may not exist depending on when the user exited
                        // the Link flow.
                        Log.d("Link request ID: ", linkData.get("link_request_id"));
                        Log.d("API request ID: ", linkData.get("plaid_api_request_id"));

                        // Reload Link in the WebView
                        // You will likely want to transition the view at this point.
                        plaidLinkWebView.loadUrl(linkInitializationUrl.toString());
                    } else {
                        Log.d("Link action detected: ", action);
                    }
                    // Override URL loading
                    return true;
                } else if (parsedUri.getScheme().equals("https") ||
                        parsedUri.getScheme().equals("http")) {
                    // Open in browser - this is most  typically for 'account locked' or
                    // 'forgotten password' redirects
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    // Override URL loading
                    return true;
                } else {
                    // Unknown case - do not override URL loading
                    return false;
                }
            }
        });
    }

    private WebView getConfiguredPlaidLinkWebView() {
        // Modify WebView settings
        // TODO: Determine which of these settings I need or not
        final WebView plaidLinkWebView = (WebView) findViewById(R.id.web_view_add_account);
        WebSettings webSettings = plaidLinkWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        WebView.setWebContentsDebuggingEnabled(true);

        return plaidLinkWebView;
    }

    private HashMap<String, String> getLinkInitializeOptions() {
        HashMap<String, String> linkInitializeOptions = new HashMap<String,String>();
        linkInitializeOptions.put("key", PLAID_PUBLIC_KEY);
        linkInitializeOptions.put("product", "auth");
        linkInitializeOptions.put("apiVersion", "v2"); // set this to "v1" if using the legacy Plaid API
        linkInitializeOptions.put("env", "sandbox");
        linkInitializeOptions.put("clientName", "Test App");
        linkInitializeOptions.put("selectAccount", "true");
        linkInitializeOptions.put("webhook", "http://requestb.in");
        linkInitializeOptions.put("baseUrl", "https://cdn.plaid.com/link/v2/stable/link.html");
        // If initializing Link in PATCH / update mode, also provide the public_token
        // linkInitializeOptions.put("public_token", "PUBLIC_TOKEN")

        return linkInitializeOptions;
    }

    // Generate a Link initialization URL based on a set of configuration options
    private Uri generateLinkInitializationUrl(HashMap<String,String>linkOptions) {
        Uri.Builder builder = Uri.parse(linkOptions.get("baseUrl"))
                .buildUpon()
                .appendQueryParameter("isWebview", "true")
                .appendQueryParameter("isMobile", "true");
        for (String key : linkOptions.keySet()) {
            if (!key.equals("baseUrl")) {
                builder.appendQueryParameter(key, linkOptions.get(key));
            }
        }
        return builder.build();
    }

    // Parse a Link redirect URL querystring into a HashMap for easy manipulation and access
    private HashMap<String,String> parseLinkUriData(Uri linkUri) {
        HashMap<String,String> linkData = new HashMap<String,String>();
        for(String key : linkUri.getQueryParameterNames()) {
            linkData.put(key, linkUri.getQueryParameter(key));
        }
        return linkData;
    }
}
