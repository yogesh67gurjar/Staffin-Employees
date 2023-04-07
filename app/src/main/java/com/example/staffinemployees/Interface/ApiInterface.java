package com.example.staffinemployees.Interface;

import com.example.staffinemployees.Response.EmployeeBankDetails;
import com.example.staffinemployees.Response.EmployeeProfileResponse;
import com.example.staffinemployees.Response.HolidayResponse;
import com.example.staffinemployees.Response.LoginResponse;
import com.example.staffinemployees.Response.TotalEmployeeResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("emp-login")
    Call<LoginResponse> postEmpLogin(@Field("mobile") String mobile,
                                     @Field("password") String password);

    @GET("get-all-employee-details")
    Call<TotalEmployeeResponse> getTotalEmployee();

    //get all holidays
    @GET("get-holiday")
    Call<HolidayResponse> getAllHolidays();

    //get employee details
    @GET("get-employee-details/{id}")
    Call<EmployeeProfileResponse> getEmployeeProfile(@Path("id") int id);

    //get employee bank details
    @GET("get-bank-details/{id}")
    Call<EmployeeBankDetails> getEmployeeBankDetails(@Path("id") int id);

    @FormUrlEncoded
    @POST("employee-leave-apply/{id}")
    Call<LoginResponse> postLeaveRequest(@Field("date") String date,
                                         @Field("day") String day,
                                         @Field("leave_type") String leave_type,
                                         @Field("reason") String reason,
                                         @Path("id") int id);

}
