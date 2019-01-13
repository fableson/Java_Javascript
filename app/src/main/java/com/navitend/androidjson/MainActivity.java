/*
 *
 * AndroidJSON.java
 * Author: Frank Ableson : fableson@navitend.com
 * December 2018
 */
package com.navitend.androidjson;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {
    private final String tag = "AndroidJSON";
    private WebView browser = null;
    private int flipflop = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final EditText formula = (EditText) this.findViewById(R.id.formula);
        final Button btnSimple = (Button) this.findViewById(R.id.btnSimple);
        final Button btnComplex = (Button) this.findViewById(R.id.btnComplex);
        final Button btnRed = (Button) this.findViewById(R.id.btnRed);
        final Button btnData = (Button) this.findViewById(R.id.btnData);


        btnComplex.setOnClickListener(new Button.OnClickListener()
        {

            public void onClick(View v) {
                Log.i(tag,"onClick Complex");
                // Perform action on click
                try
                {
                    String jsonText = "";

                    if (flipflop == 0)
                    {
                        jsonText = "{ \"operation\" : \"addarray\",\"arguments\" : [1,2,3,4,5,6,7,8,9,10]}";
                        flipflop = 1;
                    } else {
                        jsonText = "{ \"operation\" : \"multarray\",\"arguments\" : [1,2,3,4,5,6,7,8,9,10]}";
                        flipflop = 0;
                    }
                    Log.i(tag,"jsonText is [" + jsonText + "]" );
                    browser.loadUrl("javascript:PerformComplexCalculation(" + jsonText + ");");
                }
                catch (Exception e)
                {
                    Log.e(tag,"Error ..." + e.getMessage());
                }

            }
        });
        btnSimple.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v) {
                Log.i(tag,"onClick Simple");
                // Perform action on click
                try
                {
                    String formulaText =  formula.getText().toString();
                    Log.i(tag,"Formula is [" + formulaText + "]" );
                    browser.loadUrl("javascript:PerformSimpleCalculation(" + formulaText + ");");
                }
                catch (Exception e)
                {
                    Log.e(tag,"Error ..." + e.getMessage());
                }
            }
        });

        btnRed.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v) {
                Log.i(tag,"onClick Red");
                // Perform action on click
                try
                {
                    browser.loadUrl("javascript:(function() { document.getElementsByTagName('body')[0].style.color = 'red';})()");
                }
                catch (Exception e)
                {
                    Log.e(tag,"Error ..." + e.getMessage());
                }

            }
        });

        btnData.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v) {
                Log.i(tag,"onClick Data");
                // Perform action on click
                try
                {
                    String url =  "https://www.navitend.com/ibmdeveloperworks";
                    Log.i(tag,"URL is [" + url + "]" );
                    browser.loadUrl("javascript:getWebContent('" + url + "');");
                }
                catch (Exception e)
                {
                    Log.e(tag,"Error ..." + e.getMessage());
                }
            }
        });


        // connect to our browser so we can manipulate it
        browser = (WebView) findViewById(R.id.calculator);

        // set a webview client to override the default functionality
        browser.setWebViewClient(new wvClient());

        // get settings so we can config our WebView instance
        WebSettings settings = browser.getSettings();

        // JavaScript?  Of course!
        settings.setJavaScriptEnabled(true);

        // clear cache
        browser.clearCache(true);

        // this is necessary for "alert()" to work
        browser.setWebChromeClient(new WebChromeClient());

        // add our custom functionality to the javascript environment
        browser.addJavascriptInterface(new CalculatorHandler(), "calc");

        // uncomment this if you want to use the webview as an invisible calculator!
        //browser.setVisibility(View.INVISIBLE);

        // load a page to get things started
        browser.loadUrl("file:///android_asset/index.html");

        // allows the control to receive focus
        // on some versions of Android the webview doesn't handle input focus properly
        // this seems to make things work with Android 2.1, but not 2.2
        // browser.requestFocusFromTouch();
    }

    final class wvClient extends WebViewClient {
        public void onPageFinished(WebView view, String url) {
            // when our web page is loaded, let's call a function that is contained within the page
            // this is functionally equivalent to placing an onload attribute in the <body> tag
            // whenever the loadUrl method is used, we are essentially "injecting" code into the page when it is prefixed with "javascript:"
            browser.loadUrl("javascript:startup()");
        }
    }

    // Javascript handler
    final class CalculatorHandler
    {

        private int iterations = 0;

        // write to LogCat (Info)
        @JavascriptInterface
        public void Info(String str) {
            iterations++;
            Log.i("Calc",str);
        }

        // write to LogCat (Error)
        @JavascriptInterface
        public void Error(String str) {
            iterations++;
            Log.e("Calc",str);
        }

        // sample to retrieve a custom - written function with the details provided by the Android native application code
        @JavascriptInterface
        public String GetSomeFunction()
        {
            iterations++;
            return "var q = 6;function dynamicFunc(v) { return v + q; }";
        }

        // Kill the app
        @JavascriptInterface
        public void EndApp() {
            iterations++;
            finish();
        }

        @JavascriptInterface
        public void setAnswer(String a)
        {
            iterations++;
            Log.i(tag,"Answer [" + a + "]");
        }

        @JavascriptInterface
        public int getIterations()
        {
            return iterations;
        }

        @JavascriptInterface
        public void SendHistory(String s)
        {
            Log.i("Calc","SendHistory" + s);
            try {
                JSONArray ja = new JSONArray(s);
                for (int i=0;i<ja.length();i++) {
                    Log.i("Calc","History entry #" + (i+1) + " is [" + ja.getString(i) + "]");
                }
            } catch (Exception ee) {
                Log.e("Calc",ee.getMessage());
            }
        }

    }
}
