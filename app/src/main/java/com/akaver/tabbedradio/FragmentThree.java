package com.akaver.tabbedradio;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by akaver on 07/04/2017.
 */

public class FragmentThree extends Fragment implements View.OnClickListener {

    private static final String TAG = FragmentThree.class.getSimpleName();

    public interface RadioStationNamesTransferer {
        HashMap getStationNames() throws JSONException;
        void setStationNames(HashMap names);
    }

    private RecyclerView recycler;
    private RecyclerViewAdapter recyclerAdapter;
    private Map.Entry<String, HashMap<String, String>> selectedStation;
    private EditText name;
    private EditText url;
    private Button save;
    private Button delete;
    private Button add;
    private Button reset;

    private RadioStationNamesTransferer transferer;


    private HashMap<String, HashMap<String, String>> radiostations = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(TAG,"onCreateView");

        View view = inflater.inflate(R.layout.fragment_three, container, false);

        name = (EditText) view.findViewById(R.id.name);
        url = (EditText) view.findViewById(R.id.url);
        save = (Button) view.findViewById(R.id.save);
        delete = (Button) view.findViewById(R.id.delete);
        add = (Button) view.findViewById(R.id.add);
        reset = (Button) view.findViewById(R.id.reset);

        save.setOnClickListener(this);
        delete.setOnClickListener(this);
        add.setOnClickListener(this);
        reset.setOnClickListener(this);


        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerAdapter = new RecyclerViewAdapter(radiostations, new RecyclerViewAdapter.OnClickHandler() {
            @Override
            public void onClick(Object station) {
                selectedStation = (Map.Entry) station;
                name.setText(selectedStation.getValue().get("name"));
                url.setText(selectedStation.getValue().get("url"));
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(recyclerAdapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"onCreate");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG,"onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG,"onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG,"onResume");

        try {
            radiostations = transferer.getStationNames();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recyclerAdapter.setItems(radiostations);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"onDestroy");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG,"onDestroy");

        transferer.setStationNames(radiostations);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof RadioStationNamesTransferer) {
            transferer = (RadioStationNamesTransferer) context;
        } else {
            throw new RuntimeException(context.toString()
            + " must implement RadioStationNamesTransferer");
        }

        Log.v(TAG,"onAttach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG,"onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(TAG,"onDetach");
    }

    public void saveClicked(View v) {
//        selectedStation.put("name", name.getText().toString());
//        selectedStation.put("url", url.getText().toString());
        radiostations.get(selectedStation.getKey()).put("name", name.getText().toString());
        radiostations.get(selectedStation.getKey()).put("url", url.getText().toString());

        recyclerAdapter.setItems(radiostations);
    }

    public void deleteClicked(View v) {

        radiostations.remove(selectedStation.getKey());
        recyclerAdapter.setItems(radiostations);
    }

    public void addClicked(View v) {

        HashMap<String, String> newStation = new HashMap<>();
        newStation.put("name", name.getText().toString());
        newStation.put("url", url.getText().toString());

        radiostations.put(newStation.get("name"), newStation);
        recyclerAdapter.setItems(radiostations);
    }

    public void resetClicked(View v) {

        setDefaultStations();
        recyclerAdapter.setItems(radiostations);
    }

    public void setDefaultStations() {

        HashMap<String, String> stationMap = new HashMap<>();
        stationMap.put("name","skyplus");
        stationMap.put("url", "http://skyplus.m3u8");

        HashMap<String, String> stationMap2 = new HashMap<>();
        stationMap2.put("name","some random radio");
        stationMap2.put("url", "http://random.m3u8");

        radiostations.clear();
        radiostations.put("skyplus", stationMap);
        radiostations.put("some random radio", stationMap2);
        recyclerAdapter.setItems(radiostations);
    }

    @Override
    public void onClick(View v) {

        switch (((Button)v).getId()) {
            case R.id.save:
                saveClicked(v);
                break;
            case R.id.add:
                addClicked(v);
                break;
            case R.id.reset:
                resetClicked(v);
                break;
            case R.id.delete:
                deleteClicked(v);
                break;
        }
    }

    static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private Object[] mItems;
        private OnClickHandler mClickHandler;

        public RecyclerViewAdapter(HashMap<String, HashMap<String, String>> radiostations, OnClickHandler listener) {
            mItems = radiostations.entrySet().toArray();
            radiostations.entrySet().toArray();
            mClickHandler = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.station.setText(((Map.Entry<String, HashMap<String, String>>) mItems[position]).getValue().get("name"));
            holder.setIndexInDataSet(position);
        }

        @Override
        public int getItemCount() {
            return mItems.length;
        }

        public void setItems(Object items) {

            try {
                mItems = ((HashMap)items).entrySet().toArray();
                notifyDataSetChanged();
            } catch (Exception e) {
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView station;

            private int indexInDataSet = -1;

            public ViewHolder(View itemView) {
                super(itemView);
                station = (TextView) itemView.findViewById(R.id.station);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                mClickHandler.onClick(mItems[indexInDataSet]);
            }

            public void setIndexInDataSet(int index) {
                this.indexInDataSet = index;
            }
        }

        interface OnClickHandler {
            void onClick(Object mItem);
        }
    }
}
