package org.droidplanner.android.customScheduler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.droidplanner.android.R;
import org.droidplanner.android.SocketDataReceiver;
import org.droidplanner.android.wprecyclerview.WpAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CustomScheduleAdapter extends RecyclerView.Adapter<CustomScheduleAdapter.ViewHolder> {

    private List<CustomScheduleModel> CustomScheduleModel;
    private Context context;

    public CustomScheduleAdapter(Context context, List<CustomScheduleModel> CustomScheduleModel) {
        this.CustomScheduleModel = CustomScheduleModel;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_schedule_list, viewGroup, false);
        CustomScheduleAdapter.ViewHolder viewHolder = new CustomScheduleAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomScheduleAdapter.ViewHolder holder, final int position) {
        holder.id.setText(CustomScheduleModel.get(position).getId());
        holder.name.setText("Name: " + CustomScheduleModel.get(position).getName());
        holder.date.setText("Date: " + CustomScheduleModel.get(position).getDate());
        holder.start.setText("Start Time: " + CustomScheduleModel.get(position).getStartTime());
        holder.fly.setText("Flight Time: " + CustomScheduleModel.get(position).getFlyTime());
        holder.end.setText("End Time: " + CustomScheduleModel.get(position).getEndTime());
        holder.distance.setText("Distance: " + CustomScheduleModel.get(position).getDistance());

        // delete schedule by id on longClick
        holder.customSchView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //String specificDelete = "{\"u\":\"ground\",\"action\":\"dl_schedule_by_id\",\"id\":" + (CustomScheduleModel.get(position).getId()) + "}";
                String specificDelete = "{\"u\":\"g\",\"135\":\"124\",\"id\":" + (CustomScheduleModel.get(position).getId()) + "}";
                SocketDataReceiver.attemptSend(specificDelete);
                removeItem(position);
                return true;
            }
        });
    }

    public void removeItem(int position) {
        try {
            CustomScheduleModel.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        } catch (Exception ex) {
            Toast.makeText(context, "" + ex, Toast.LENGTH_SHORT).show(); 
        }
    }

    @Override
    public int getItemCount() {
        return (null != CustomScheduleModel ? CustomScheduleModel.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView id;
        TextView name;
        TextView date;
        TextView start;
        TextView fly;
        TextView end;
        TextView distance;
        CardView customSchView;

        public ViewHolder(View view) {
            super(view);
            Log.d("cusviewholder", "custom ViewHolder is called");
            id = (TextView) view.findViewById(R.id.idText);
            name = (TextView) view.findViewById(R.id.nameText);
            date = (TextView) view.findViewById(R.id.dateText);
            start = (TextView) view.findViewById(R.id.startText);
            fly = (TextView) view.findViewById(R.id.flyText);
            end = (TextView) view.findViewById(R.id.endText);
            distance = (TextView) view.findViewById(R.id.distanceText);
            customSchView = (CardView) view.findViewById(R.id.customSchCard);
        }
    }
}