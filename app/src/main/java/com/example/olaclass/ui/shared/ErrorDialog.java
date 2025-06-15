package com.example.olaclass.ui.shared;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorDialog {
    public static void show(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton("OK", (DialogInterface dialog, int which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }
}
