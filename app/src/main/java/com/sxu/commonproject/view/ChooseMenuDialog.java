package com.sxu.commonproject.view;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sxu.commonproject.R;
import com.sxu.commonproject.app.CommonApplication;

/**
 * Created by juhg on 15/10/20.
 */
public class ChooseMenuDialog extends Dialog {

    private Context context;
    private TextView photoText;
    private TextView chooseText;
    private TextView cancelText;

    // 图片的Uri地址
    private Uri iconUri;
    private final int REQUEST_CODE_TAKE_PHOTO = 1;
    private final int REQUEST_CODE_CHOOSE_IMAGE = 2;

    public ChooseMenuDialog(Context context) {
        super(context);
        this.context = context;
    }

    public ChooseMenuDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_menu_layout);
        photoText = (TextView)findViewById(R.id.photo_text);
        chooseText = (TextView)findViewById(R.id.choose_text);
        cancelText = (TextView)findViewById(R.id.cancel_text);
        CommonApplication.setTypeface(photoText);
        CommonApplication.setTypeface(chooseText);
        CommonApplication.setTypeface(cancelText);

//        photoText.setOnClickListener(this);
//        chooseText.setOnClickListener(this);
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    public TextView getPhotoText() {
        return photoText;
    }

    public TextView getChooseText() {
        return chooseText;
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.photo_text:
//                File file = new File(PathManager.getInstance() + "/temp_icon.jpg");
//                iconUri = Uri.fromFile(file);
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, iconUri);
//                ((Activity)context).startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
//                break;
//            case R.id.choose_text:
//                intent = new Intent(Intent.ACTION_PICK, null);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                ((Activity)context).startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE);
//                break;
//            case R.id.cancel_text:
//                cancel();
//                break;
//            default:
//                break;
//        }
//    }

    /**
     * 裁减图片操作
     * 注意：
     *   1. 由于Android对应用程序的内存有严格限制，为了防止传送大图片出现OOM，在拍照截图的过程中默认传递的都是缩略图，
     *      这样裁减后的图片非常模糊。而采用URi的方式传递的是大图片，需要注意的是，如果需要传递大图片，那么拍照时需要设置
     *      照片的输出路径，也就是在调用相机时传递MediaStore.EXTRA_OUTPUT参数，同时为了防止某些手机默认传递大图片的
     *      问题（如小米4），在传递大图片时将return-data置为false.
     *
     *   2. 对于某些手机或平板，输入Uri和输出Uri不能相等，否则裁减后的图片大小变为0kb,如平板Teclast P80h.
     * @param uri
     */
//    private void startCropImage(Uri uri) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        // 图片处于可裁剪状态
//        intent.putExtra("crop", "true");
//        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("scale", true);
//        // 裁剪后图片的大小
//        //intent.putExtra("outputX", 500);
//        //intent.putExtra("outputY", 500);
//        //intent.putExtra("scale", true);
//        // 以Uri的方式传递照片，如果
//        File cropFile = new File(AppInstances.getPathManager().getImgCacheDir() + "crop_image.jpg");
//        cropImageUri = Uri.fromFile(cropFile);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
//        // 裁剪区域的形状为圆形，默认为矩形
//        //intent.putExtra("circleCrop", true);
//        // 设置输出格式
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        // return-data=true传递的为缩略图，小米手机默认传递大图，所以会导致onActivityResult调用失败
//        intent.putExtra("return-data", false);
//        // 关闭人脸识别
//        intent.putExtra("noFaceDetection", true);
//        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
//    }
}
