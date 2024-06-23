package com.example.myapplication.ui.Memory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class MemoryViewModel extends ViewModel {
    private final MutableLiveData<List<Memory>> memories = new MutableLiveData<>();

    public LiveData<List<Memory>> getMemories() {
        return memories;
    }

    public void setMemories(List<Memory> memoryList) {
        memories.setValue(memoryList);
    }
}
