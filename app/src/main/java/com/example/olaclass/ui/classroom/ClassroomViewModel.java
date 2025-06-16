package com.example.olaclass.ui.classroom;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagingData;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.example.olaclass.data.model.Classroom;
import com.example.olaclass.data.repository.ClassroomRepository;
// Removed duplicate imports
import androidx.lifecycle.MutableLiveData;

public class ClassroomViewModel extends ViewModel {
    private final ClassroomRepository repository;
    private final MutableLiveData<PagingData<Classroom>> classroomPagingData = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final String userRole;

    public ClassroomViewModel(ClassroomRepository repository, String userRole) {
        this.repository = repository;
        this.userRole = userRole;
        loadClassrooms();
    }

    private void loadClassrooms() {
        // Clear previous disposables if any to prevent memory leaks and duplicate subscriptions
        disposables.clear(); 
        // Get the Flowable from repository
        Flowable<PagingData<Classroom>> flowable;

        if ("teacher".equals(userRole)) {
            flowable = repository.getClassroomsPaged();
        } else { // Assume student if not teacher, or add specific "student" check
            flowable = repository.getJoinedClassroomsPaged();
        }
        
        // Subscribe to the Flowable and update LiveData
        disposables.add(
            flowable
                .subscribeOn(Schedulers.io())
                .subscribe(
                    pagingData -> classroomPagingData.postValue(pagingData),
                    throwable -> {
                        // Handle error
                        throwable.printStackTrace();
                    }
                )
        );
    }

    public LiveData<PagingData<Classroom>> getClassrooms() {
        return classroomPagingData;
    }

    // Method to trigger a refresh of classrooms
    public void refreshClassrooms() {
        loadClassrooms(); // Re-trigger the data loading
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
