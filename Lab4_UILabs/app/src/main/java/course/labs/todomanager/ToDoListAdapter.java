package course.labs.todomanager;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ToDoListAdapter extends BaseAdapter {

	private final List<ToDoItem> mItems = new ArrayList<ToDoItem>();
	private final Context mContext;
    private final List<PriorityListItem> mpriorityList;
    private final Resources mResources;

	private static final String TAG = "Lab-UserInterface";

	public ToDoListAdapter(Context context) {

		mContext = context;
        mResources = mContext.getResources();

        mpriorityList = new ArrayList<PriorityListItem>();
        PriorityListItem item = new PriorityListItem(ToDoItem.Priority.HIGH, mResources.getString(R.string.priority_high_string));
        mpriorityList.add(item);
        item = new PriorityListItem(ToDoItem.Priority.MED, mResources.getString(R.string.priority_medium_string));
        mpriorityList.add(item);
        item = new PriorityListItem(ToDoItem.Priority.LOW, mResources.getString(R.string.priority_low_string));
        mpriorityList.add(item);
	}

	// Add a ToDoItem to the adapter
	// Notify observers that the data set has changed

	public void add(ToDoItem item) {

		mItems.add(item);
		notifyDataSetChanged();

	}

	// Clears the list adapter of all items.

	public void clear() {

		mItems.clear();
		notifyDataSetChanged();

	}

	// Returns the number of ToDoItems

	@Override
	public int getCount() {

		return mItems.size();

	}

	// Retrieve the number of ToDoItems

	@Override
	public Object getItem(int pos) {

		return mItems.get(pos);

	}

	// Get the ID for the ToDoItem
	// In this case it's just the position

	@Override
	public long getItemId(int pos) {

		return pos;

	}

	// Create a View for the ToDoItem at specified position
	// Remember to check whether convertView holds an already allocated View
	// before created a new View.
	// Consider using the ViewHolder pattern to make scrolling more efficient
	// See: http://developer.android.com/training/improving-layouts/smooth-scrolling.html
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Get the current ToDoItem
		final ToDoItem toDoItem = (ToDoItem)getItem(position);
        ViewHolder holder = null;

		// Inflate the View for this ToDoItem
		// from todo_item.xml
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.todo_item, parent, false);

            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.titleView);
            holder.statusView = (CheckBox) convertView.findViewById(R.id.statusCheckBox);
            //holder.priorityView = (TextView) convertView.findViewById(R.id.priorityView);
            holder.dateView = (TextView) convertView.findViewById(R.id.dateView);
            holder.prioritySelector = (Spinner) convertView.findViewById(R.id.prioritySelector);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final View tempView = convertView;

		// Fill in specific ToDoItem data
		// Remember that the data that goes in this View
		// corresponds to the user interface elements defined
		// in the layout file

		// Display Title in TextView
		final TextView titleView = holder.titleView;
        titleView.setText(toDoItem.getTitle());


		// Set up Status CheckBox
		final CheckBox statusView = holder.statusView;
        boolean done = toDoItem.getStatus() == ToDoItem.Status.DONE;
        statusView.setChecked(toDoItem.getStatus() == ToDoItem.Status.DONE ? true : false);
        setItemColor(tempView, statusView);

		// Must also set up an OnCheckedChangeListener,
		// which is called when the user toggles the status checkbox

		statusView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
                        toDoItem.setStatus(isChecked ? ToDoItem.Status.DONE : ToDoItem.Status.NOTDONE);
                        setItemColor(tempView, statusView);
					}
				});


		// Display Priority in a TextView
		//final TextView priorityView = holder.priorityView;

        final Spinner spinner = holder.prioritySelector;
        ArrayAdapter<PriorityListItem> adapter =
                new ArrayAdapter<PriorityListItem>(mContext, R.layout.spinner_item, mpriorityList);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, android.view.View view, int i, long l) {
                PriorityListItem priority = (PriorityListItem)adapterView.getItemAtPosition(i);
                toDoItem.setPriority(priority.code);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> adapterView) {

            }
        });

        switch(toDoItem.getPriority()){
            case HIGH:
                spinner.setSelection(0);
                break;
            case LOW:
                spinner.setSelection(2);
                break;
            default:
                spinner.setSelection(1);
                break;
        }


		// Display Time and Date.
		// Hint - use ToDoItem.FORMAT.format(toDoItem.getDate()) to get date and
		// time String
		final TextView dateView = holder.dateView;
        dateView.setText(ToDoItem.FORMAT.format(toDoItem.getDate()));

		// Return the View you just created
		return convertView;

	}

    private final void setItemColor(View view, CheckBox statusView){
        view.setBackgroundColor(statusView.isChecked() ? mContext.getResources().getColor(R.color.green) : mContext.getResources().getColor(R.color.red));
        statusView.setBackgroundColor(mContext.getResources().getColor(R.color.black));
    }

    static class ViewHolder{
        TextView titleView;
        CheckBox statusView;
        TextView dateView;
        Spinner prioritySelector;
    }

    static class PriorityListItem{
        ToDoItem.Priority code;
        String name;

        public PriorityListItem(ToDoItem.Priority code, String name){
            this.code = code;
            this.name = name;
        }

        @Override
        public String toString(){
            return name;
        }
    }
}
