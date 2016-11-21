package com.kiminonawa.mydiary.entries.diary;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.FileManager;
import com.kiminonawa.mydiary.shared.ThemeManager;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryPhotoDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    interface PhotoCallBack {
        void addPhoto(Bitmap bitmap, String fileName);

        void selectPhoto(Uri uri);
    }

    private RelativeLayout RL_diary_photo_dialog;
    private ImageView IV_diary_photo_add_a_photo, IV_diary_photo_select_a_photo;

    /**
     * Camera & select photo
     */
    private static final int REQUEST_START_CAMERA_CODE = 1;
    private static final int REQUEST_SELECT_IMAGE_CODE = 2;

    /**
     * File
     */
    private FileManager fileManager;
    private String tempFileName;

    private PhotoCallBack callBack;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        fileManager = new FileManager(getActivity());
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.dialog_fragment_diary_photo, container);
        RL_diary_photo_dialog = (RelativeLayout) rootView.findViewById(R.id.RL_diary_photo_dialog);
        RL_diary_photo_dialog.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        IV_diary_photo_add_a_photo = (ImageView) rootView.findViewById(R.id.IV_diary_photo_add_a_photo);
        IV_diary_photo_add_a_photo.setOnClickListener(this);
        IV_diary_photo_select_a_photo = (ImageView) rootView.findViewById(R.id.IV_diary_photo_select_a_photo);
        IV_diary_photo_select_a_photo.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_START_CAMERA_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = BitmapFactory.decodeFile(fileManager.getDiaryDir() + tempFileName);
                callBack.addPhoto(bitmap, tempFileName);
            } else {
                Log.e("test", "cancel");
            }
            dismiss();
        } else if (requestCode == REQUEST_SELECT_IMAGE_CODE) {
            if (resultCode == RESULT_OK) {
                callBack.selectPhoto(data.getData());
            } else {
                Log.e("test", "cancel");
            }
            dismiss();
        }
    }

    public void setCallBack(PhotoCallBack callBack) {
        this.callBack = callBack;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_diary_photo_add_a_photo:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                tempFileName = "/" + fileManager.createRandomFileName();
                File tmpFile = new File(fileManager.getDiaryDir(), tempFileName);
                Uri outputFileUri = Uri.fromFile(tmpFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(intent, REQUEST_START_CAMERA_CODE);
                break;
            case R.id.IV_diary_photo_select_a_photo:
                FileManager.startBrowseImageFile(this, REQUEST_SELECT_IMAGE_CODE);
                break;
        }
    }
}