package com.example.buddyhang.adapters;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.buddyhang.R;
import com.example.buddyhang.models.PrivateEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolderAdapter>
{
    private final ArrayList<String> days;
    private final OnItemListener onItemListener;
    private List<String> acceptedEvents;
    private HashSet<String> eventDaysSet;
    public final String year;
    public final String month;

    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener, String month,String year)
    {
        this.days = daysOfMonth;
        this.onItemListener = onItemListener;
        this.year = year;
        this.month = month;
        this.eventDaysSet = new HashSet<>();
    }

    @NonNull
    @Override
    public CalendarViewHolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_item, parent, false);
        view.getLayoutParams().height = (int) (parent.getHeight() * 0.12);

        return new CalendarViewHolderAdapter(view, onItemListener);
    }

    public interface OnItemListener
    {
        void onItemClick(int position, String dayText);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolderAdapter holder, int position)
    {
        holder.day.setText(days.get(position));
        acceptedEvents = new ArrayList<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("Accepts").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    acceptedEvents.add(snapshot.getKey());

                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            PrivateEvent event = snapshot.getValue(PrivateEvent.class);
                            for (String id : acceptedEvents) {
                                Log.i("New event date", "event date" + event.getEventDate());
                                if (event.getEventDate() != null) {

                                    String[] eventDateArray = event.getEventDate().trim().split(" ");
                                    String eventDay = eventDateArray[0];
                                    String eventMonth = eventDateArray[1];
                                    String eventYear = eventDateArray[2];

                                    if (eventMonth.compareToIgnoreCase(month) == 0 && eventYear.compareToIgnoreCase(year) == 0) {
                                        eventDaysSet.add(eventDay);
                                    }
                                }

                            }
                        }
                        if (eventDaysSet.contains(holder.day.getText())) {
                            holder.day.setTextColor(Color.rgb(255,31,38));
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    @Override
    public int getItemCount()
    {
        return days.size();
    }


}