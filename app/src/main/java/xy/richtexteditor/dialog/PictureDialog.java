package xy.richtexteditor.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import xy.richtexteditor.R;

/**
 * Created by Xieying on 2018/4/5
 * Function:
 */
public class PictureDialog extends AppCompatDialogFragment {
    private static final String LOG_TAG = PictureDialog.class.getSimpleName();

    private long imageId;

    private CharSequence[] items;

    private OnDialogClickListener mListener;

    public static PictureDialog createPictureDialog(Long imageId) {
        PictureDialog pictureDialog = new PictureDialog();
        pictureDialog.setImageId(imageId);
        return pictureDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null)
                            switch (which) {
                                case 0:
                                    mListener.delete(imageId);
                                    break;
                                case 1:
                                    mListener.reload(imageId);
                                    break;
                            }

                    }
                })
                .setTitle(R.string.operate).create();

    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }


    public void setItems(CharSequence[] items) {
        this.items = items;
    }

    public void setOnDialogClickListener(OnDialogClickListener mListener) {
        this.mListener = mListener;
    }

    public interface OnDialogClickListener {

        void delete(Long id);

        void reload(Long id);
    }
}
