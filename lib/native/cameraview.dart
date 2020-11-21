import 'package:flutter/services.dart';

class CameraView {
  CameraView._internal();

  static final _instance = CameraView._internal();
  static CameraView get instance => _instance;

  final _channel = MethodChannel("app.meedu/take_photo");

  Future<String> takePicture() async {
    try {
      String path = await _channel.invokeMethod<String>("take");
      return path;
    } catch (e) {
      return null;
    }
  }
}
