package com.example.staffinemployees;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.staffinemployees.Interface.ApiInterface;
import com.example.staffinemployees.RealPathUtilGithub.RealPathUtil;
import com.example.staffinemployees.Response.EmployeeProfileResponse;
import com.example.staffinemployees.Response.EmployeeResult;
import com.example.staffinemployees.Retrofit.RetrofitServices;
import com.example.staffinemployees.databinding.ActivityPersonalDetailsBinding;

import java.io.CharArrayReader;
import java.io.File;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalDetailsActivity extends AppCompatActivity {
    ActivityPersonalDetailsBinding binding;
    static Boolean dpImageBoolean = false;
    private static String imagePath;
    ApiInterface apiInterface;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String id;

    Uri profileImage;
    String uripi = " ";
    File PImg;
    String name, fName, dob, gender, email, localAddress, permanentAddress, finalGender, number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonalDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("staffin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        id = sharedPreferences.getAll().get("Id").toString();
        apiInterface = RetrofitServices.getRetrofit().create(ApiInterface.class);
        getUserApi();
        setOnClickListener();


    }

    private void setOnClickListener() {
        binding.editBtn.setOnClickListener(v -> {
            Intent editImg = new Intent(Intent.ACTION_PICK);
            editImg.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(editImg, 123);
        });
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
        binding.dobEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(PersonalDetailsActivity.this,
                        (view, year1, monthOfYear, dayOfMonth) -> {
                            binding.dobEt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1);
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (!dpImageBoolean) {
//                    Toast.makeText(getApplicationContext(), "Please Upload your Profile Image", Toast.LENGTH_SHORT).show();
//                }
//                else
                if (binding.employeeIdEt.getText().toString().isEmpty()) {
                    binding.employeeIdEt.setError("Enter Your Name");
                    binding.employeeIdEt.requestFocus();
                } else if (binding.departmentEt.getText().toString().isEmpty()) {
                    binding.departmentEt.setError("Enter Your Father's Name");
                    binding.departmentEt.requestFocus();
                } else if (binding.dobEt.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Select Your DOB", Toast.LENGTH_SHORT).show();
                } else if (!binding.rbMale.isChecked() && !binding.rbFemale.isChecked() && !binding.rbOther.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please Select Your Gender", Toast.LENGTH_SHORT).show();
                } else if (binding.mobileEt.getText().toString().trim().isEmpty()) {
                    binding.mobileEt.setError("Enter Mobile Number");
                    binding.mobileEt.requestFocus();
                } else if (binding.mobileEt.getText().toString().trim().length() < 10) {
                    binding.mobileEt.setError("Enter Correct Mobile Number");
                    binding.mobileEt.requestFocus();
                } else if (binding.emailEt.getText().toString().trim().isEmpty() ||
                        !binding.emailEt.getText().toString().trim().contains("@") ||
                        !binding.emailEt.getText().toString().trim().contains(".")) {
                    binding.emailEt.setError("Enter Correct Email");
                    binding.emailEt.requestFocus();
                } else if (binding.localAddEt.getText().toString().isEmpty()) {
                    binding.localAddEt.setError("Enter Your Address");
                    binding.localAddEt.requestFocus();
                } else if (binding.permAddEt.getText().toString().isEmpty()) {
                    binding.permAddEt.setError("Enter Your Permanent Local Address");
                    binding.permAddEt.requestFocus();
                } else {

                    name = binding.employeeIdEt.getText().toString();
                    fName = binding.departmentEt.getText().toString();
                    dob = binding.dobEt.getText().toString();
                    gender = "";
                    if (binding.rbMale.isChecked()) {
                        binding.rbMale.getText().toString();
                        gender = "Male";
                    } else if (binding.rbFemale.isChecked()) {
                        binding.rbFemale.getText().toString();
                        gender = "Female";
                    } else {
                        binding.rbOther.getText().toString();
                        gender = "Other";
                    }
                    finalGender = gender;
                    number = binding.mobileEt.getText().toString();
                    email = binding.emailEt.getText().toString();
                    localAddress = binding.localAddEt.getText().toString();
                    permanentAddress = binding.permAddEt.getText().toString();

                    updateDetails(Integer.parseInt(id), uripi, name, fName, dob, finalGender, number, email, localAddress, permanentAddress);

                }
            }
        });
    }

    private void updateDetails(int id, String uripi, String name, String fName, String xdob, String finalGender, String number, String email, String localAddress, String permanentAddress) {
        RequestBody fullname = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody father = RequestBody.create(MediaType.parse("text/plain"), fName);
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), xdob);
        RequestBody mobile = RequestBody.create(MediaType.parse("text/plain"), number);
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), finalGender);
        RequestBody mail = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody lAddress = RequestBody.create(MediaType.parse("text/plain"), localAddress);
        RequestBody pAddress = RequestBody.create(MediaType.parse("text/plain"), permanentAddress);

        final ProgressDialog progressDialog = new ProgressDialog(PersonalDetailsActivity.this);
        progressDialog.setMessage("Loading...");

        if (dpImageBoolean) {
            PImg = new File(uripi);
            RequestBody proImg = RequestBody.create(MediaType.parse("image/*"), PImg);
            MultipartBody.Part profile_img = MultipartBody.Part.createFormData("profile_image", PImg.getName(), proImg);

            Call<EmployeeProfileResponse> callUpdateEmployee = apiInterface.postUpdateEmployee(id, profile_img, fullname, father, dob, mobile, gender, mail, lAddress, pAddress);
            if (isNetworkAvailable()) {
                progressDialog.show();
                callUpdateEmployee.enqueue(new Callback<EmployeeProfileResponse>() {
                    @Override
                    public void onResponse(Call<EmployeeProfileResponse> call, Response<EmployeeProfileResponse> response) {
                        if (response.isSuccessful()) {
                            progressDialog.dismiss();
                            finish();
                        } else {
                            Log.d("fkdjfnsdf", response.message());
                            progressDialog.dismiss();
                            Toast.makeText(PersonalDetailsActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeeProfileResponse> call, Throwable t) {
                        Log.d("Pdkjfnsdf", t.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(PersonalDetailsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Internet Not Available", Toast.LENGTH_SHORT).show();
            }
//           image le k api chlegi
        } else {

            Call<EmployeeProfileResponse> callPostUpdateEmployeeWithoutImage = apiInterface.postUpdateEmployeeWithoutImage(id, name, fName, xdob, number, finalGender, email, localAddress, permanentAddress);
            if (isNetworkAvailable()) {
                progressDialog.show();
                callPostUpdateEmployeeWithoutImage.enqueue(new Callback<EmployeeProfileResponse>() {
                    @Override
                    public void onResponse(Call<EmployeeProfileResponse> call, Response<EmployeeProfileResponse> response) {
                        if (response.isSuccessful()) {
                            progressDialog.dismiss();
                            finish();
                        } else {
                            Log.d("fkgfdgddjfnsdf", response.message());
                            progressDialog.dismiss();
                            Toast.makeText(PersonalDetailsActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EmployeeProfileResponse> call, Throwable t) {
                        Log.d("dfgdfgd", t.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(PersonalDetailsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Internet Not Available", Toast.LENGTH_SHORT).show();
            }
//           image k bina api chlegi
        }

    }

    private void getUserApi() {
        final ProgressDialog progressDialog = new ProgressDialog(PersonalDetailsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Call<EmployeeProfileResponse> employeeProfileResponseCall = apiInterface.getEmployeeProfile(Integer.parseInt(id));
        employeeProfileResponseCall.enqueue(new Callback<EmployeeProfileResponse>() {
            @Override
            public void onResponse(Call<EmployeeProfileResponse> call, Response<EmployeeProfileResponse> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    EmployeeResult singleUser = response.body().getEmployeeResult().get(0);

                    Glide.with(getApplicationContext()).load(singleUser.getProfileImage()).placeholder(R.drawable.img_dp).into(binding.dpImg);

                    binding.employeeIdEt.setText(singleUser.getFullName());
                    binding.departmentEt.setText(singleUser.getFatherName());
                    String DOB = singleUser.getDateOfBirth();
                    DOB = DOB.split("T")[0];
                    binding.dobEt.setText(DOB);

                    if (singleUser.getGender().equalsIgnoreCase("male")) {
                        binding.rbMale.setChecked(true);
                    } else if (singleUser.getGender().equalsIgnoreCase("female")) {
                        binding.rbFemale.setChecked(true);
                    } else {
                        binding.rbOther.setChecked(true);
                    }

                    binding.mobileEt.setText(singleUser.getMobileNumber());
                    binding.emailEt.setText(singleUser.getEmail());
                    binding.localAddEt.setText(singleUser.getLocalAddress());
                    binding.permAddEt.setText(singleUser.getPermanentAddress());
                } else {
                    progressDialog.dismiss();
                    Log.d("dkfnsdf", response.message());
                    Toast.makeText(PersonalDetailsActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EmployeeProfileResponse> call, Throwable t) {
                Log.d("nsdfsdf", t.getMessage());
                progressDialog.dismiss();
                Toast.makeText(PersonalDetailsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();

            }
        });


    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == 123) {
//            if (!data.equals(null)) {
//                binding.dpImg.setImageURI(data.getData());
//                profileImage = data.getData();
//                uripi = getRealPathFromURI(profileImage);
//                dpImageBoolean = true;
//            }
//        }
//    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 123) {
            binding.dpImg.setImageURI(data.getData());
            profileImage = data.getData();
            uripi = getRealPathFromURI(profileImage);
            dpImageBoolean = true;
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}