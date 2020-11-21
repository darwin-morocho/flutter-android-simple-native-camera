package app.meedu.take_photo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.gesture.Gesture;
import com.otaliastudios.cameraview.gesture.GestureAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

abstract class FlashMode {
    static final int auto = 0;
    static final int on = 1;
    static final int off = 2;
}

public class CameraActivity extends AppCompatActivity {

    private CameraView cameraView;
    public static int CAMERA_VIEW_REQUEST_CODE = 2010212;
    private ImageView imageView;
    private byte[] bytes;
    private boolean isFrontCamera = false;
    private int flashMode = FlashMode.off;
    private ImageButton flashButton;
    private LinearLayout cameraButtons, previewButtons, progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraView = findViewById(R.id.camera_view);
        imageView = findViewById(R.id.image_preview);
        flashButton = findViewById(R.id.button_switch_flashlight);
        cameraButtons = findViewById(R.id.camera_buttons);
        previewButtons = findViewById(R.id.preview_buttons);
        progressDialog = findViewById(R.id.progress_dialog);
        cameraView.setLifecycleOwner(this);
        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM); // Pinch to zoom!
        cameraView.mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS); // Tap to focus!
        cameraView.mapGesture(Gesture.LONG_TAP, GestureAction.TAKE_PICTURE); // Long tap to shoot!
        cameraView.setFlash(Flash.OFF);
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {

                bytes = result.getData();
                result.toBitmap(bitmap -> {
                    setPreview(bitmap);
                });
            }
        });
    }


    public void takePicture(View v) {
        progressDialog.setVisibility(View.VISIBLE);
        this.cameraView.takePicture();
    }

    public void switchCamera(View v) {
        this.isFrontCamera = !this.isFrontCamera;
        this.cameraView.setFacing(this.isFrontCamera ? Facing.FRONT : Facing.BACK);
    }

    public void onCancel(View v) {
        imageView.setVisibility(View.GONE);
        cameraButtons.setVisibility(View.VISIBLE);
        previewButtons.setVisibility(View.GONE);
        bytes = null;
    }


    public void switchFlashMode(View v) {
        this.flashMode++;
        if (this.flashMode > 2) {
            this.flashMode = 0;
        }
        this.setFlashMode();
    }

    private void setFlashMode() {
        Flash flash = Flash.ON;
        switch (this.flashMode) {
            case FlashMode.auto:
                flash = Flash.ON;
                this.flashButton.setImageResource(R.mipmap.flash_auto);
                break;
            case FlashMode.on:
                flash = Flash.TORCH;
                this.flashButton.setImageResource(R.mipmap.flash_on);
                break;
            case FlashMode.off:
                flash = Flash.OFF;
                this.flashButton.setImageResource(R.mipmap.flash_off);
                break;
        }
        this.cameraView.setFlash(flash);
    }


    public void sendFile(View v) {
        if (bytes == null) return;
        final Long timestamps = new Date().getTime();

        File directory = getApplicationContext().getCacheDir();
        File file = new File(directory.getPath(), timestamps + ".jpg");
        final Intent intent = new Intent();

        try {
            FileOutputStream fos = new FileOutputStream(file.getPath());
            fos.write(bytes);
            fos.close();
            final Uri uri = Uri.parse(file.getPath());
            intent.putExtra("path", uri.toString());
            setResult(Activity.RESULT_OK, intent);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

    private void setPreview(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);
        cameraButtons.setVisibility(View.GONE);
        previewButtons.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.GONE);
    }
}