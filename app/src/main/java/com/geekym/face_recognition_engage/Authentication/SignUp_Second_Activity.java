package com.geekym.face_recognition_engage.Authentication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.geekym.face_recognition_engage.R;
import com.geekym.face_recognition_engage.SimilarityClassifier;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SignUp_Second_Activity extends AppCompatActivity {

    FaceDetector detector;
    Interpreter tfLite;

    ImageButton Add;
    ImageView Face_Preview;
    boolean start = true, flipX = false;
    float[][] embeddings;

    int[] intValues;
    int inputSize = 112;  //Input size for model
    boolean isModelQuantized = false;
    float IMAGE_MEAN = 128.0f;
    float IMAGE_STD = 128.0f;
    int OUTPUT_SIZE = 192; //Output size of model
    private static int SELECT_PICTURE = 1;
    ProcessCameraProvider cameraProvider;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    //Camera Preview Variables
    PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    CameraSelector cameraSelector; //Camera Facing

    String modelFile = "mobile_face_net.tflite"; //model name

    private HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>(); //saved Faces

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_second);

        //Camera Permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        Initialize();

        //Initialize Face Detector
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .build();
        detector = FaceDetection.getClient(highAccuracyOpts);

        cameraBind();

        //Load model
        try {
            tfLite = new Interpreter(loadModelFile(SignUp_Second_Activity.this, modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Add.setOnClickListener(view -> addFace());

    }

    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void Initialize() {
        Add = findViewById(R.id.addButton);
        Face_Preview = findViewById(R.id.face_preview);
    }

    private void addFace() {
        start = false;

        //Create and Initialize new object with Face embeddings and Name.
        SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition(
                "0", "", -1f);
        result.setExtra(embeddings);

        start = true;
    }

    //Bind camera and preview view
    private void cameraBind() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        previewView = findViewById(R.id.previewView);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this in Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cameraSelector.LENS_FACING_FRONT)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(640, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //Latest frame is shown
                        .build();

        Executor executor = Executors.newSingleThreadExecutor();
        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                try {
                    Thread.sleep(0);  //Camera preview refreshed every 10 millisec(adjust as required)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                InputImage image = null;


                @SuppressLint({"UnsafeExperimentalUsageError", "UnsafeOptInUsageError"})
                // Camera Feed-->Analyzer-->ImageProxy-->mediaImage-->InputImage(needed for ML kit face detection)

                Image mediaImage = imageProxy.getImage();

                if (mediaImage != null) {
                    image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                }

                //Process acquired image to detect faces
                Task<List<Face>> result = detector.process(image).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                    @Override
                    public void onSuccess(List<Face> faces) {

                        if (faces.size() != 0) {

                            Face face = faces.get(0); //Get first face from detected faces

                            //mediaImage to Bitmap
                            Bitmap frame_bmp = toBitmap(mediaImage);

                            int rot = imageProxy.getImageInfo().getRotationDegrees();

                            //Adjust orientation of Face
                            Bitmap frame_bmp1 = rotateBitmap(frame_bmp, rot, false, false);

                            //Get bounding box of face
                            RectF boundingBox = new RectF(face.getBoundingBox());

                            //Crop out bounding box from whole Bitmap(image)
                            Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);

                            if (flipX)
                                cropped_face = rotateBitmap(cropped_face, 0, flipX, false);
                            //Scale the acquired Face to 112*112 which is required input for model
                            Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);

                            if (start)
                                recognizeImage(scaled); //Send scaled bitmap to create face embeddings.
                        }
                    }
                }).addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                            }
                        }).addOnCompleteListener(new OnCompleteListener<List<Face>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Face>> task) {
                        imageProxy.close(); //v.important to acquire next frame for analysis
                    }
                });
            }
        });

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);

    }

    public void recognizeImage(final Bitmap bitmap) {

        // set Face to Preview
        Face_Preview.setImageBitmap(bitmap);

        //Create ByteBuffer to store normalized image

        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);

        imgData.order(ByteOrder.nativeOrder());

        intValues = new int[inputSize * inputSize];

        //get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        imgData.rewind();

        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                int pixelValue = intValues[i * inputSize + j];
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((byte) ((pixelValue >> 16) & 0xFF));
                    imgData.put((byte) ((pixelValue >> 8) & 0xFF));
                    imgData.put((byte) (pixelValue & 0xFF));
                } else { // Float model
                    imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);

                }
            }
        }
        //imgData is input to our model
        Object[] inputArray = {imgData};

        Map<Integer, Object> outputMap = new HashMap<>();


        embeddings = new float[1][OUTPUT_SIZE]; //output of model will be stored in this variable

        outputMap.put(0, embeddings);

        tfLite.runForMultipleInputsOutputs(inputArray, outputMap); //Run model
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private static Bitmap getCropBitmapByCPU(Bitmap source, RectF cropRectF) {
        Bitmap resultBitmap = Bitmap.createBitmap((int) cropRectF.width(),
                (int) cropRectF.height(), Bitmap.Config.ARGB_8888);
        Canvas cavas = new Canvas(resultBitmap);

        // draw background
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.WHITE);
        cavas.drawRect(
                new RectF(0, 0, cropRectF.width(), cropRectF.height()),
                paint);

        Matrix matrix = new Matrix();
        matrix.postTranslate(-cropRectF.left, -cropRectF.top);

        cavas.drawBitmap(source, matrix, paint);

        if (source != null && !source.isRecycled()) {
            source.recycle();
        }

        return resultBitmap;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int rotationDegrees, boolean flipX, boolean flipY) {
        Matrix matrix = new Matrix();

        // Rotate the image back to straight.
        matrix.postRotate(rotationDegrees);

        // Mirror the image along the X or Y axis.
        matrix.postScale(flipX ? -1.0f : 1.0f, flipY ? -1.0f : 1.0f);
        Bitmap rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // Recycle the old bitmap if it has changed.
        if (rotatedBitmap != bitmap) {
            bitmap.recycle();
        }
        return rotatedBitmap;
    }

    //IMPORTANT. If conversion not done ,the toBitmap conversion does not work on some devices.
    private static byte[] YUV_420_888toNV21(Image image) {

        int width = image.getWidth();
        int height = image.getHeight();
        int ySize = width * height;
        int uvSize = width * height / 4;

        byte[] nv21 = new byte[ySize + uvSize * 2];

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); // U
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); // V

        int rowStride = image.getPlanes()[0].getRowStride();
        assert (image.getPlanes()[0].getPixelStride() == 1);

        int pos = 0;

        if (rowStride == width) { // likely
            yBuffer.get(nv21, 0, ySize);
            pos += ySize;
        } else {
            long yBufferPos = -rowStride; // not an actual position
            for (; pos < ySize; pos += width) {
                yBufferPos += rowStride;
                yBuffer.position((int) yBufferPos);
                yBuffer.get(nv21, pos, width);
            }
        }

        rowStride = image.getPlanes()[2].getRowStride();
        int pixelStride = image.getPlanes()[2].getPixelStride();

        assert (rowStride == image.getPlanes()[1].getRowStride());
        assert (pixelStride == image.getPlanes()[1].getPixelStride());

        if (pixelStride == 2 && rowStride == width && uBuffer.get(0) == vBuffer.get(1)) {
            // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
            byte savePixel = vBuffer.get(1);
            try {
                vBuffer.put(1, (byte) ~savePixel);
                if (uBuffer.get(0) == (byte) ~savePixel) {
                    vBuffer.put(1, savePixel);
                    vBuffer.position(0);
                    uBuffer.position(0);
                    vBuffer.get(nv21, ySize, 1);
                    uBuffer.get(nv21, ySize + 1, uBuffer.remaining());

                    return nv21; // shortcut
                }
            } catch (ReadOnlyBufferException ex) {
                // unfortunately, we cannot check if vBuffer and uBuffer overlap
            }

            // unfortunately, the check failed. We must save U and V pixel by pixel
            vBuffer.put(1, savePixel);
        }

        // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
        // but performance gain would be less significant

        for (int row = 0; row < height / 2; row++) {
            for (int col = 0; col < width / 2; col++) {
                int vuPos = col * pixelStride + row * rowStride;
                nv21[pos++] = vBuffer.get(vuPos);
                nv21[pos++] = uBuffer.get(vuPos);
            }
        }

        return nv21;
    }

    private Bitmap toBitmap(Image image) {

        byte[] nv21 = YUV_420_888toNV21(image);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Similar Analyzing Procedure
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == SELECT_PICTURE) {
//                Uri selectedImageUri = data.getData();
//                try {
//                    InputImage impphoto = InputImage.fromBitmap(getBitmapFromUri(selectedImageUri), 0);
//                    detector.process(impphoto).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
//                        @Override
//                        public void onSuccess(List<Face> faces) {
//
//                            if (faces.size() != 0) {
//                                recognize.setText("Recognize");
//                                add_face.setVisibility(View.VISIBLE);
//                                reco_name.setVisibility(View.INVISIBLE);
//                                face_preview.setVisibility(View.VISIBLE);
//                                preview_info.setText("1.Bring Face in view of Camera.\n\n2.Your Face preview will appear here.\n\n3.Click Add button to save face.");
//                                Face face = faces.get(0);
//
//                                //write code to recreate bitmap from source
//                                //Write code to show bitmap to canvas
//
//                                Bitmap frame_bmp = null;
//                                try {
//                                    frame_bmp = getBitmapFromUri(selectedImageUri);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                Bitmap frame_bmp1 = rotateBitmap(frame_bmp, 0, flipX, false);
//
//                                RectF boundingBox = new RectF(face.getBoundingBox());
//
//
//                                Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);
//
//                                Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);
//                                // face_preview.setImageBitmap(scaled);
//
//                                recognizeImage(scaled);
//                                addFace();
////                                System.out.println(boundingBox);
//                                try {
//                                    Thread.sleep(100);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            start = true;
//                            Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    face_preview.setImageBitmap(getBitmapFromUri(selectedImageUri));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

}