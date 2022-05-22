package com.geekym.face_recognition_engage.HomeFragments.Tools.PDFs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.geekym.face_recognition_engage.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class pdfViewer_Activity extends AppCompatActivity {

    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        String filename = getIntent().getStringExtra("filename");
        String fileurl = getIntent().getStringExtra("fileurl");

        final ProgressDialog pDialog = new ProgressDialog(pdfViewer_Activity.this);
        pDialog.setTitle("Course PDF");
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        webView=findViewById(R.id.pdf_view);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pDialog.dismiss();
            }
        });
        StringBuilder url = new StringBuilder();
        String pdf = fileurl;
        try {
            url.append(URLEncoder.encode(pdf,"UTF-8"));
            webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url="+url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}