package com.shubham.interactionlog;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class MessageAdapter extends ArrayAdapter<String>{

	Context context;
	List<String> numbers;
	List<String> time;	
	List<String> day_type;	
	List<String> body;	
	public MessageAdapter(Context context, List<String> number, List<String> time, List<String> day_type, List<String> body) {
		super(context, R.layout.message_log_design, R.id.msg_number, number);
		this.context = context;
		this.numbers = number;
		this.time = time;
		this.day_type = day_type;
		this.body = body;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.message_log_design, parent, false);
		
		TextView tempnumber = (TextView) row.findViewById(R.id.msg_number);
		TextView temptime = (TextView) row.findViewById(R.id.msg_time);
		TextView tempdaytype = (TextView) row.findViewById(R.id.msg_day_type);
		TextView tempbody = (TextView) row.findViewById(R.id.msg_body);
		
		tempnumber.setText(numbers.get(position));
		temptime.setText(time.get(position));
		tempdaytype.setText(day_type.get(position));
		tempbody.setText(body.get(position));
		return row;
	}
}

