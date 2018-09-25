package org.droidplanner.android.wprecyclerview;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.droidplanner.android.R;
import org.droidplanner.android.SocketDataReceiver;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class WpAdapter extends RecyclerView.Adapter<WpAdapter.ViewHolder> {
    private List<WpModel> WpModel;
    private Context context;

    public WpAdapter(Context context, List<WpModel> wpModel) {
        this.WpModel = wpModel;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_wp_list, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WpAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.name.setText(WpModel.get(position).getName());
        viewHolder.id.setText(WpModel.get(position).getId());

        final String[] tempDate = new String[1];

        viewHolder.dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear + 1) + "/" + String.valueOf(year);
                        tempDate[0] = date;
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });

        final String[] tempTime = new String[1];

        viewHolder.tButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar myCalender = Calendar.getInstance();
                int hour = myCalender.get(Calendar.HOUR_OF_DAY);
                int minute = myCalender.get(Calendar.MINUTE);
                final int second = 00; //myCalender.get(Calendar.SECOND);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        String time = String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(second);
                        tempTime[0] = time;
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cat = tempDate[0] + " " + tempTime[0];
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                //dateFormat.setLenient(false);
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+6:00"));
                Date date = new Date();
                try {
                    date = dateFormat.parse(cat);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long startTime = ((long) date.getTime() / 1000) + 21600 ;
                long flightTime = startTime + 10;
                //String schCreate = "{\"u\":\"ground\",\"action\":\"wp_schedule_create\",\"s_id\":" + (WpModel.get(position).getId()) + ",\"drone_power_on\":" + startTime + ",\"flight_start\":" + flightTime + "}";
                String schCreate = "{\"u\":\"g\",\"135\":\"117\",\"s_id\":" + (WpModel.get(position).getId()) + ",\"drone_power_on\":" + startTime + ",\"flight_start\":" + flightTime + "}";
                SocketDataReceiver.attemptSend(schCreate);
            }
        });

        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //String specificWpDelete = "{\"u\":\"ground\",\"action\":\"dl_wp_by_id\",\"id\":" + (WpModel.get(position).getId()) + "}";
                String specificWpDelete = "{\"u\":\"g\",\"135\":\"123\",\"id\":" + (WpModel.get(position).getId()) + "}";
                SocketDataReceiver.attemptSend(specificWpDelete);
                removeItem(position);
                return true;
            }
        });
    }

    public void removeItem(int position){
        try {
            WpModel.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }catch (Exception ex){
            Toast.makeText(context, "" + ex, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return (null != WpModel ? WpModel.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView id;
        Button tButton;
        Button dButton;
        CardView cardView;  // wp_each_item

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.wpNameText);
            id = (TextView) view.findViewById(R.id.wpIdText);
            tButton = (Button) view.findViewById(R.id.tpicker);
            dButton = (Button) view.findViewById(R.id.dpicker);
            cardView = (CardView) view.findViewById(R.id.cardView);
        }
    }
}