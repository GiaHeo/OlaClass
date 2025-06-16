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
    private final ClassroomRepository repository = new ClassroomRepository();
    private final MutableLiveData<PagingData<Classroom>> classroomPagingData = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    public ClassroomViewModel() {
        // Get the Flowable from repository
        Flowable<PagingData<Classroom>> flowable = repository.getClassroomsPaged();
        
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

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
