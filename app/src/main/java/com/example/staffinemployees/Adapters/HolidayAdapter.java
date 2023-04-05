package com.example.staffinemployees.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.staffinemployees.R;
import com.example.staffinemployees.Response.AllHolidays;

import java.util.List;

public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder> {
    Context context;
    List<AllHolidays> holidays;

    public HolidayAdapter(Context context, List<AllHolidays> holidays) {
        this.context = context;
        this.holidays = holidays;
    }

    @NonNull
    @Override
    public HolidayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.holiday_layout, parent, false);
        return new HolidayAdapter.HolidayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolidayAdapter.HolidayViewHolder holder, int position) {
//        if (position % 3 == 0) {
//            holder.holidayName.setText("Ram Navami");
//            holder.holidayTime.setText("5AM TO 7PM");
//            holder.holidayDate.setText("30");
//            holder.holidayDay.setText("Fri");
//            holder.ll2.setBackgroundResource(R.color.mainColor);
//        } else if (position % 4 == 0) {
//            holder.holidayName.setText("Hanuman Janmotsav");
//            holder.holidayTime.setText("5AM TO 7PM");
//            holder.holidayDate.setText("6");
//            holder.holidayDay.setText("Tue");
//            holder.ll2.setBackgroundResource(R.color.yellow);
//        } else if (position % 2 == 0) {
//            holder.holidayName.setText("Republic day");
//            holder.holidayTime.setText("5AM TO 7PM");
//            holder.holidayDate.setText("26");
//            holder.holidayDay.setText("Fri");
//            holder.ll2.setBackgroundResource(R.color.pink);
//        } else if (position % 5 == 0) {
//            holder.holidayName.setText("Independence Day");
//            holder.holidayTime.setText("5AM TO 7PM");
//            holder.holidayDate.setText("15");
//            holder.holidayDay.setText("Mon");
//            holder.ll2.setBackgroundResource(R.color.green);
//        } else if (position % 1 == 0) {
//            holder.holidayName.setText("Diwali");
//            holder.holidayTime.setText("5AM TO 7PM");
//            holder.holidayDate.setText("27");
//            holder.holidayDay.setText("Sun");
//            holder.ll2.setBackgroundResource(R.color.green);
//        }

        AllHolidays singleUnit = holidays.get(position);
        holder.holidayName.setText(singleUnit.getOccassion());
        String[] tempDate = singleUnit.getDate().split("-");
        holder.holidayDate.setText(tempDate[2]);
        holder.holidayDay.setText(singleUnit.getHoliday_day());
        holder.holidayTime.setText(singleUnit.getHoliday_description());
        if (position % 3 == 0) {
            holder.ll2.setBackgroundResource(R.color.mainColor);
        } else if (position % 5 == 0) {
            holder.ll2.setBackgroundResource(R.color.yellow);
        } else if (position % 2 == 0) {
            holder.ll2.setBackgroundResource(R.color.pink);
        } else {
            holder.ll2.setBackgroundResource(R.color.green);
        }
    }

    @Override
    public int getItemCount() {
        return holidays.size();
    }

    public class HolidayViewHolder extends RecyclerView.ViewHolder {
        CardView card2;
        LinearLayout ll2;
        TextView holidayName, holidayTime, holidayDate, holidayDay;

        public HolidayViewHolder(@NonNull View itemView) {
            super(itemView);
            card2 = itemView.findViewById(R.id.card2);
            ll2 = itemView.findViewById(R.id.ll2);
            holidayName = itemView.findViewById(R.id.holidayName);
            holidayTime = itemView.findViewById(R.id.holidayTime);
            holidayDate = itemView.findViewById(R.id.holidayDate);
            holidayDay = itemView.findViewById(R.id.holidayDay);
        }
    }
}
