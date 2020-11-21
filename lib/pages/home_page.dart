import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'dart:io';

import 'package:image_picker/image_picker.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:take_photo/native/cameraview.dart';

class HomePage extends StatefulWidget {
  HomePage({Key key}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  Uint8List _bytes;
  bool _storageOk;

  @override
  void initState() {
    super.initState();
    _init();
  }

  void _init() async {
    if (Platform.isAndroid) {
      _storageOk = await Permission.storage.isGranted;
      if (!_storageOk) {
        final status = await Permission.storage.request();
        _storageOk = status == PermissionStatus.granted;
      }
    }
  }

  void _takePhotoWithImagePicker() async {
    final PickedFile pickedFile = await ImagePicker().getImage(source: ImageSource.camera);
    if (pickedFile != null) {
      this._bytes = await pickedFile.readAsBytes();
      setState(() {});
    }
  }

  void _takePhotoWithCameraView() async {
    if (_storageOk) {
      final String imagePath = await CameraView.instance.takePicture();
      if (imagePath != null) {
        this._bytes = await File(imagePath).readAsBytes();
        setState(() {});
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Container(
          width: double.infinity,
          height: double.infinity,
          child: Column(
            children: [
              Expanded(
                child: _bytes == null
                    ? Center(
                        child: Text("Take a photo"),
                      )
                    : Image.memory(_bytes),
              ),
              SizedBox(height: 20),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  FlatButton(
                    onPressed: _takePhotoWithImagePicker,
                    child: Text("image_picker"),
                    color: Colors.redAccent.withOpacity(0.3),
                  ),
                  SizedBox(width: 10),
                  FlatButton(
                    onPressed: _takePhotoWithCameraView,
                    color: Colors.blue.withOpacity(0.3),
                    child: Text(
                      "cameraview (Android only)",
                    ),
                  ),
                ],
              ),
              SizedBox(height: 20)
            ],
          ),
        ),
      ),
    );
  }
}
