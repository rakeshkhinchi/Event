package com.amura.rakesh.event;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AdapterEventList extends RecyclerView.Adapter<AdapterEventList.MyViewHolder> {

    private final ArrayList<WeekViewEvent> events;
    private final MainActivity baseActivity;

    public AdapterEventList(MainActivity baseActivity, ArrayList<WeekViewEvent> events) {

        this.events = events;
        this.baseActivity = baseActivity;

    }

    @NonNull
    @Override
    public AdapterEventList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem = layoutInflater.inflate(R.layout.event_list_item, viewGroup, false);
        AdapterEventList.MyViewHolder viewHolder = new AdapterEventList.MyViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterEventList.MyViewHolder myViewHolder, int i) {
        final WeekViewEvent myListData = events.get(i);
        myViewHolder.tvAgendaTitle.setText(myListData.getName() + " ( " + myListData.getLocation() + " )");
        myViewHolder.tvFromToDateTime.setText("From (" + convertDateTime(myListData.getStartTime()) + " )   To (" + convertDateTime(myListData.getEndTime()) + " )");

        myViewHolder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(baseActivity, AddEventActivity.class);

                intent.putExtra("EVENT_ID", myListData.getId());
                intent.putExtra("AGENDA", myListData.getName());
                intent.putExtra("EMAIL", myListData.getLocation());
                intent.putExtra("AGENDA_START_DATE", "" + convertToDate(myListData.getStartTime()));
                intent.putExtra("AGENDA_START_TIME", "" + convertToTime(myListData.getStartTime()));
                intent.putExtra("AGENDA_END_DATE", "" + convertToDate(myListData.getEndTime()));
                intent.putExtra("AGENDA_END_TIME", "" + convertToTime(myListData.getEndTime()));

                intent.putExtra("EVENT_COLOR", myListData.getColor());

                intent.putExtra("ACTION", "VIEW");

                baseActivity.startActivityForResult(intent, 2);

            }
        });


    }

    // convert date into specific time
    private String convertToTime(Calendar endTime) {

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String currentDate = dateFormat.format(endTime.getTime());

        return currentDate;

    }

    // convert date into specific format
    private String convertToDate(Calendar startTime) {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = dateFormat.format(startTime.getTime());

        return currentDate;

    }

    // convert date into specific date time
    private String convertDateTime(Calendar startTime) {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String currentDate = dateFormat.format(startTime.getTime());

        return currentDate;

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFromToDateTime;
        private final TextView tvAgendaTitle;
        private final LinearLayout llItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAgendaTitle = (TextView) itemView.findViewById(R.id.tv_agenda_title);
            tvFromToDateTime = (TextView) itemView.findViewById(R.id.tv_from_to_datetime);
            llItem = (LinearLayout) itemView.findViewById(R.id.ll_item);

        }
    }
}
