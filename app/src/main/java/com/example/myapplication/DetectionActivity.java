package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.ml.ModelUnquant;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class DetectionActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button captureButton, selectButton;
    private TextView result;
    private Bitmap bitmap;
    private BarChart barChart;
    int imageSize = 224;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        imageView = findViewById(R.id.imageView);
        selectButton = findViewById(R.id.buttonUploadImage);
        captureButton = findViewById(R.id.buttonTakePicture);
        result = findViewById(R.id.result);
        barChart = findViewById(R.id.barChart); // Reference the BarChart

        // Adjust bar chart layout based on screen size dynamically
        adjustChartSize();

        getPermission();

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);
            }
        });
    }

    // Adjust chart based on device screen size
    private void adjustChartSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels / 3; // Set height to 1/3 of the screen size
        barChart.getLayoutParams().height = height;
        barChart.requestLayout();
    }

    void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 11);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 11) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPermission();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10 && data != null) {
            imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, false);
                imageView.setImageBitmap(bitmap);
                classifyImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 12 && resultCode == RESULT_OK && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            classifyImage(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void classifyImage(Bitmap image) {
        try {
            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            String[] classes = {
                    "Tomato___Bacterial_spot", "Tomato___Early_blight", "Tomato___healthy",
                    "Tomato___Late_blight", "Tomato___Leaf_Mold", "Tomato___Septoria_leaf_spot",
                    "Tomato___Spider_mites", "Tomato___Target_Spot",
                    "Tomato___Tomato_mosaic_virus", "Tomato___Tomato_Yellow_Leaf_Curl_Virus"
            };

            result.setText(classes[maxPos]);
            showBarChart(confidences, classes);

            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showBarChart(float[] confidences, String[] classes) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> topClasses = new ArrayList<>();

        for (int i = 0; i < 3; i++) { // Show only top 3 classes
            int maxPos = 0;
            float maxConfidence = 0;
            for (int j = 0; j < confidences.length; j++) {
                if (confidences[j] > maxConfidence) {
                    maxConfidence = confidences[j];
                    maxPos = j;
                }
            }
            entries.add(new BarEntry(i, maxConfidence * 100)); // Convert to percentage
            topClasses.add(classes[maxPos].replace("Tomato___", ""));
            confidences[maxPos] = 0; // Mark as used
        }

        BarDataSet dataSet = new BarDataSet(entries, "Top 3 Disease Matches");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(10f); // Reduce value text size to fit better on screen

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f); // Reduce bar width to make them thinner

        barChart.setData(barData);
        barChart.setFitBars(true);

        // Customize X-Axis to show top classes
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(topClasses));
        xAxis.setGranularity(1f); // Ensure space between labels
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f); // Reduce label text size to fit

        // Set explicit X-axis limits to ensure all labels fit
        xAxis.setAxisMinimum(-0.5f);  // Start just before the first bar to give more space
        xAxis.setAxisMaximum(2.5f);   // End just after the third bar

        xAxis.setLabelCount(3); // Ensure exactly 3 labels are shown

        // No label rotation
        xAxis.setLabelRotationAngle(0);

        // Adjust extra offsets to avoid cutting off
        barChart.setExtraOffsets(15, 15, 15, 15); // Increase padding to avoid cutting off labels

        // Chart settings
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setAutoScaleMinMaxEnabled(true);

        barChart.invalidate(); // Refresh chart to apply changes
    }







}
