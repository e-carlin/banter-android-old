package com.banter.banter;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Response;
import com.banter.banter.api.API;
import com.banter.banter.api.AccountAPI;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

//TODO: Lots of work to be done in here cleaning things up and doing proper error handling

public class PlaidAddAccountActivity extends AppCompatActivity {
    private final static String TAG= "PlaidAddAccountActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plaid_add_account);

        openPlaidAddAccountWebView();
    }

    private void openPlaidAddAccountWebView() {
        final HashMap<String, String> linkInitializationOptions = getLinkInitializationOptions();
        final Uri linkInitializationUrl = generateLinkInitializationUrl(linkInitializationOptions);
        final WebView plaidLinkWebView = getConfiguredPlaidLinkWebView();
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

                    if (action.equals("connected")) {
                        //Success! We got the account details from Plaid
                        // Now send the account details to our API

                        AccountAPI.addAccount(new JSONObject(linkData),
                                PlaidAddAccountActivity.this,
                                getResponseListener(),
                                getResponseErrorListener()
                        );

                    } else if (action.equals("exit")) {
                        // User exited
                        Log.w(TAG, "User exited the Plaid link workflow");
                        startActivity(new Intent(PlaidAddAccountActivity.this, UserDetailsActivity.class));
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

    private Response.ErrorListener getResponseErrorListener() {
        return error -> {
            Log.e(TAG, "Error sending plaid public token to our api: "+error);
            try {
                if(error.networkResponse != null && error.networkResponse.data != null) {
                    Log.e(TAG, "Error message: " + new String(error.networkResponse.data, "UTF-8"));
                }
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            finally {
                showSendPublicTokenErrorAlerDialog();
            }
        };
    }

    private void showSendPublicTokenErrorAlerDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Error saving your account");
        alertDialog.setMessage("Sorry, our system encountered an error and was unable to save your account. Please try again.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Retry",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(PlaidAddAccountActivity.this, PlaidAddAccountActivity.class));
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        startActivity(new Intent(PlaidAddAccountActivity.this, UserDetailsActivity.class));
                    }
                });
        alertDialog.show();
    }

    private Response.Listener<JSONObject> getResponseListener() {
        return response -> {
            Log.i(TAG, "Response from adding account is: "+response.toString());
            this.startActivity(new Intent(this, UserDetailsActivity.class));
        };
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

    //TODO: These values should all be moved into a config file
    private HashMap<String, String> getLinkInitializationOptions() {
        HashMap<String, String> linkInitializeOptions = new HashMap<>();
        linkInitializeOptions.put("key", BuildConfig.PLAID_PUBLIC_KEY);
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
        HashMap<String,String> linkData = new HashMap<>();
        for(String key : linkUri.getQueryParameterNames()) {
            linkData.put(key, linkUri.getQueryParameter(key));
        }
        return linkData;
    }
}
