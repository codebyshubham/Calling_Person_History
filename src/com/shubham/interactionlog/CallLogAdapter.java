package com.shubham.interactionlog;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class CallLogAdapter extends ArrayAdapter<String>{

	Context context;
	List<String> numbers;
	List<String> time;	
	List<String> type;	
	List<String> day_duration;	
	public CallLogAdapter(Context context, List<String> number, List<String> time, List<String> type, List<String> day_duration) {
		super(context, R.layout.call_log_view, R.id.caller_number, number);
		this.context = context;
		this.numbers = number;
		this.time = time;
		this.type = type;
		this.day_duration = day_duration;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.call_log_view, parent, false);
		
		TextView tempnumber = (TextView) row.findViewById(R.id.caller_number);
		TextView temptime = (TextView) row.findViewById(R.id.call_time);
		TextView temptype = (TextView) row.findViewById(R.id.call_type);
		TextView tempdayduration = (TextView) row.findViewById(R.id.call_day_duration);
		
		tempnumber.setText(numbers.get(position));
		temptime.setText(time.get(position));
		temptype.setText(type.get(position));
		tempdayduration.setText(day_duration.get(position));
		return row;
	}
}