package com.example.olaclass.ui.classroom;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagingData;
import androidx.paging.rxjava2.RxPagedListBuilder;
import androidx.paging.rxjava2.PagingRx;
import io.reactivex.Flowable;
import com.example.olaclass.data.model.Classroom;
import com.example.olaclass.data.repository.ClassroomRepository;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import androidx.lifecycle.MutableLiveData;

public class ClassroomViewModel extends ViewModel {
    private final ClassroomRepository repository = new ClassroomRepository();
    private final MutableLiveData<PagingData<Classroom>> classroomPagingData = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    public ClassroomViewModel() {
        Flowable<PagingData<Classroom>> flowable = repository.getClassroomsPaged();
        disposables.add(
            flowable.subscribeOn(Schedulers.io())
                .subscribe(classroomPagingData::postValue)
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
