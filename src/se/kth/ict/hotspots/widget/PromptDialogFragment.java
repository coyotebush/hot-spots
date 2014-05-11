package se.kth.ict.hotspots.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import se.kth.ict.hotspots.R;

/**
 * Prompts the user for a string.
 */
public class PromptDialogFragment extends DialogFragment {

    public interface PromptDialogListener {
        public void onPromptDialogResult(PromptDialogFragment dialog, String value);
    }

    private int title;
    private int positiveText;
    private String initialValue;
    private PromptDialogListener listener = null;
    private EditText text = null;

    /**
     * Construct the dialog.
     *
     * @param title        resource ID of dialog title
     * @param positiveText resource ID of positive button text
     * @param initialValue initial value of text field
     */
    public PromptDialogFragment(int title, int positiveText, String initialValue) {
        this.title = title;
        this.positiveText = positiveText;
        this.initialValue = initialValue;
    }


    public void setListener(PromptDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title);
        builder.setNegativeButton(R.string.button_cancel, null);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onPromptDialogResult(PromptDialogFragment.this, text.getText().toString());
                }
            }
        });

        text = new EditText(getActivity());
        text.setText(initialValue);
        text.selectAll();
        text.requestFocus();
        builder.setView(text);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }
}
