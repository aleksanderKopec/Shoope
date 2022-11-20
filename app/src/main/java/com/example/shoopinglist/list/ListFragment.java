package com.example.shoopinglist.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoopinglist.R;
import com.example.shoopinglist.databinding.FragmentListBinding;

public class ListFragment extends Fragment {

    private static final String TAG = "ListFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";

    private FragmentListBinding binding;
    private RecyclerView listRecyclerView;
    private RecyclerView.LayoutManager listLayoutManager;
    private ListItemAdapter listAdapter;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentListBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        listRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_recycler_view);

        listLayoutManager = new LinearLayoutManager(getActivity());
        listRecyclerView.setLayoutManager(listLayoutManager);

        listAdapter = new ListItemAdapter();
        createSampleDataset(listAdapter, 20);
        listRecyclerView.setAdapter(listAdapter);


        return rootView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonAddList.setOnClickListener(view1 -> NavHostFragment.findNavController(ListFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createSampleDataset(ListItemAdapter adapter, int amount) {
        for (int i = 0; i < amount; i++) {
            adapter.addItem(new ListItem("This is item #" + i));
        }
    }

}