package com.anantborad.act.main.presentation.ui.components

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ActMathText(
    latex: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    fontSize: Int = 16
) {
    // Start with a minimum height to avoid initial jump
    var webViewHeight by remember { mutableStateOf(48.dp) }
    
    val textColorHtml = remember(textColor) {
        String.format("#%06X", (0xFFFFFF and textColor.toArgb()))
    }
    
    // Normalize LaTeX: handle both single and double escaped delimiters from JSON data
    val normalizedLatex = remember(latex) {
        latex.replace("\\\\(", "\\(")
             .replace("\\\\)", "\\)")
             .replace("\\\\[", "\\[")
             .replace("\\\\]", "\\]")
    }
    
    // Escape for JavaScript string literal
    val escapedLatex = remember(normalizedLatex) {
        normalizedLatex
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "")
    }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        view?.loadUrl("javascript:MathInterface.onHeightChanged(document.documentElement.scrollHeight);")
                    }
                }
                settings.javaScriptEnabled = true
                setBackgroundColor(0) // Transparent
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                
                // Prevent interaction to behave like a standard Text label
                setOnLongClickListener { true }
                isLongClickable = false

                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun onHeightChanged(height: Float) {
                        val density = context.resources.displayMetrics.density
                        // Increased buffer to 8dp to prevent clipping of radicals/tall symbols
                        val heightDp = (height / density).dp + 8.dp
                        post {
                            if (Math.abs(webViewHeight.value - heightDp.value) > 1f) {
                                webViewHeight = heightDp
                            }
                        }
                    }
                }, "MathInterface")
            }
        },
        update = { webView ->
            val contentKey = "${escapedLatex}_${textColorHtml}_${fontSize}"
            if (webView.tag != contentKey) {
                webView.tag = contentKey
                val html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                        <link rel="stylesheet" href="file:///android_asset/katex/katex.min.css">
                        <script src="file:///android_asset/katex/katex.min.js"></script>
                        <script src="file:///android_asset/katex/contrib/auto-render.min.js"></script>
                        <style>
                            * {
                                -webkit-user-select: none;
                                user-select: none;
                                box-sizing: border-box;
                            }
                            body { 
                                margin: 0; 
                                padding: 0; 
                                font-size: ${fontSize}px;
                                color: $textColorHtml;
                                font-family: 'Inter', -apple-system, sans-serif;
                                line-height: 1.5;
                                overflow: visible;
                            }
                            #math-container { 
                                width: 100%;
                                word-wrap: break-word;
                                overflow-wrap: break-word;
                                overflow: visible;
                            }
                            .katex { 
                                font-size: 1.1em !important; 
                                vertical-align: middle;
                                white-space: nowrap;
                            }
                            .katex-display {
                                margin: 0.8em 0;
                                overflow-x: auto;
                                overflow-y: hidden;
                                padding: 4px 0;
                            }
                        </style>
                    </head>
                    <body>
                        <div id="math-container"></div>
                        <script>
                            (function() {
                                const container = document.getElementById("math-container");
                                container.textContent = "$escapedLatex";
                                
                                renderMathInElement(container, {
                                    delimiters: [
                                        {left: "$$", right: "$$", display: true},
                                        {left: "\\(", right: "\\)", display: false},
                                        {left: "\\[", right: "\\]", display: true},
                                        {left: "\\begin{equation}", right: "\\end{equation}", display: true}
                                    ],
                                    throwOnError: false
                                });
                                
                                const updateHeight = () => {
                                    MathInterface.onHeightChanged(document.documentElement.scrollHeight);
                                };
                                
                                updateHeight();
                                setTimeout(updateHeight, 50);
                                setTimeout(updateHeight, 200);
                                setTimeout(updateHeight, 500);
                            })();
                        </script>
                    </body>
                    </html>
                """.trimIndent()
                webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 32.dp)
            .height(webViewHeight)
    )
}
