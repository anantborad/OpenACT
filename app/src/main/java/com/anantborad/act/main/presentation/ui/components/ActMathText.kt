package com.anantborad.act.main.presentation.ui.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ActMathText(
    latex: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                setBackgroundColor(0) // Transparent
            }
        },
        update = { webView ->
            val html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <link rel="stylesheet" href="file:///android_asset/katex/katex.min.css">
                    <script src="file:///android_asset/katex/katex.min.js"></script>
                    <style>
                        body { 
                            margin: 0; 
                            padding: 0; 
                            font-size: 18px;
                            color: #0f1115;
                        }
                        #math { padding: 8px 0; }
                    </style>
                </head>
                <body>
                    <div id="math"></div>
                    <script>
                        katex.render("$latex", document.getElementById("math"), {
                            throwOnError: false,
                            displayMode: false
                        });
                    </script>
                </body>
                </html>
            """.trimIndent()
            webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
        },
        modifier = modifier.fillMaxWidth()
    )
}
