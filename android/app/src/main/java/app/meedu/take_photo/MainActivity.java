package app.meedu.take_photo;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    MethodChannel.Result flutterResult;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        final BinaryMessenger messenger = flutterEngine.getDartExecutor().getBinaryMessenger();
        final MethodChannel channel = new MethodChannel(messenger, "app.meedu/take_photo");

        channel.setMethodCallHandler((call, result) -> {
            if (call.method.equals("take")) {
                this.flutterResult = result;
                final Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivityForResult(intent, CameraActivity.CAMERA_VIEW_REQUEST_CODE);
            } else {
                result.notImplemented();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraActivity.CAMERA_VIEW_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String path = data.getStringExtra("path");
                this.flutterResult.success(path);
            } else {
                this.flutterResult.success(null);
            }
        }
    }
}
