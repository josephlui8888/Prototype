package com.example.springbreakprototype2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.example.springbreakprototype2.R;

public class GoodServiceDialogFragment extends DialogFragment {
    NoticeDialogListener listener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Maybe need message? -> add .setMessage(R.string.name_of_string) before .setTitle
        builder.setTitle(R.string.good_or_service).setItems(R.array.good_or_service_array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    listener.onDialogGoodClick(GoodServiceDialogFragment.this);
                } else {
                    listener.onDialogServiceClick(GoodServiceDialogFragment.this);
                }
            }
        });

        AlertDialog dialog = builder.create();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.x=100;
        wmlp.y = 700;
        return dialog;
    }

    public interface NoticeDialogListener {
        public void onDialogGoodClick(DialogFragment dialog);
        public void onDialogServiceClick(DialogFragment dialog);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            //throw new ClassCastException(activity.toString()
                    //+ " must implement NoticeDialogListener");
        }
    }
}
