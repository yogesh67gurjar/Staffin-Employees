package com.example.staffinemployees.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.staffinemployees.Interface.ApiInterface;
import com.example.staffinemployees.MainActivity;
import com.example.staffinemployees.Response.LoginResponse;
import com.example.staffinemployees.Retrofit.RetrofitServices;
import com.example.staffinemployees.databinding.FragmentClaimExpencesBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Part;
import retrofit2.http.Path;


public class ClaimExpences extends Fragment {
    List<String> imagePath;
    FragmentClaimExpencesBinding binding;
    TextView addText;
    static int count = 0;
    String naame = " ";
    String priice = " ";
    boolean atleastOne = false;
    String Id;
    ProgressDialog progress;
    SharedPreferences sharedPreferences;
    ApiInterface apiInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentClaimExpencesBinding.inflate(inflater, container, false);
        clickListeners();
        progress = new ProgressDialog(getContext());
        progress.setMessage("please wait...");
        progress.setCancelable(false);
        sharedPreferences = getContext().getSharedPreferences("staffin", Context.MODE_PRIVATE);
        Id = sharedPreferences.getAll().get("Id").toString();
        return binding.getRoot();
    }

    private void clickListeners() {
        imagePath = new ArrayList<>();
        apiInterface = RetrofitServices.getRetrofit().create(ApiInterface.class);
        binding.openCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 101);
                } else {
                    Toast.makeText(getActivity(), "Your Device Does not Support This Functionality\n Please Click On (Click To Upload Images)", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.clickToAddTv.setOnClickListener(v -> {
            Intent imgIntent = new Intent(Intent.ACTION_PICK);
            imgIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(imgIntent, 100);
        });

        binding.SubmitBtn.setOnClickListener(v -> {
            if (binding.titleEt.getText().toString().isEmpty()) {
                binding.titleEt.setError("enter title");
                binding.titleEt.requestFocus();
            } else if (!atleastOne) {
                Toast.makeText(getActivity(), "upload atleast 1 image", Toast.LENGTH_SHORT).show();
            } else if (binding.amountEt.getText().toString().isEmpty()) {
                binding.amountEt.setError("enter amount");
                binding.amountEt.requestFocus();
            } else {
                naame = binding.titleEt.getText().toString();
                priice = binding.amountEt.getText().toString();
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), naame);
                RequestBody price = RequestBody.create(MediaType.parse("text/plain"), priice);

                MultipartBody.Part[] bill_image = new MultipartBody.Part[imagePath.size()];
                for (int i = 0; i < imagePath.size(); i++) {
                    Log.d("TAG", i + "   " + imagePath.get(i));
                    File file = new File(imagePath.get(i));
                    RequestBody rb = RequestBody.create(MediaType.parse("image/*"), file);
                    bill_image[i] = MultipartBody.Part.createFormData("bill_image", file.getName(), rb);
                }



                Call<LoginResponse> callPostExpenses = apiInterface.postExpenses(Integer.parseInt(Id), bill_image, name, price);

                progress.show();
                Log.d("SizeOfMultipart", String.valueOf(bill_image.length));
                callPostExpenses.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                        if (response.isSuccessful()) {
                            progress.dismiss();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            Toast.makeText(getContext(), "submitted", Toast.LENGTH_SHORT).show();
                            Log.d("SizeOfMultipartPass", String.valueOf(bill_image.length));
                            Log.d("imagePathImagePath", String.valueOf(imagePath));
                            getActivity().finish();
                        } else {
                            Log.d("dfsdfsdf", response.message());
                            Toast.makeText(getContext(), "some error occured", Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Log.d("dfsdf", t.getMessage());
                        progress.dismiss();
                        Toast.makeText(getContext(), "failure", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String imgString;

        if (resultCode == RESULT_OK && requestCode == 100) {
            imgString = getRealPathFromURI(data.getData());
            Log.d("ndfsdnf", imgString);
            imagePath.add(imgString);
            addText = new TextView(getContext());

            if (count == 0) {
                count = 1;
                addText.setText(count + ".  " + imgString);
            } else {
                count += 1;
                addText.setText(count + ".  " + imgString);
            }

            addText.setTextSize(12);
            addText.setMaxLines(1);
            addText.setTextColor(Color.parseColor("#808080"));
            binding.llVertical.addView(addText);
            atleastOne = true;
        }

        if (requestCode == 101 && resultCode == RESULT_OK) {


            if (data.getExtras().get("data") == null) {

                Toast.makeText(getContext(), "this device is unable to perform this functionality", Toast.LENGTH_SHORT).show();
            } else {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri tempUri = getImageUri(getActivity(), photo);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));

                // Set the image in imageview for display
                Log.d("ndfsdnf", finalFile.toString());

                imgString = finalFile.toString();
                imagePath.add(finalFile.toString());
                addText = new TextView(getContext());

                if (count == 0) {
                    count = 1;
                    addText.setText(count + ".  " + imgString);
                } else {
                    count += 1;
                    addText.setText(count + ".  " + imgString);
                }

                addText.setTextSize(12);
                addText.setMaxLines(1);
                addText.setTextColor(Color.parseColor("#808080"));
                binding.llVertical.addView(addText);
                atleastOne = true;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        count = 0;
    }

    public String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = requireContext().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage,
                "Title", null);
        return Uri.parse(path);
    }

}