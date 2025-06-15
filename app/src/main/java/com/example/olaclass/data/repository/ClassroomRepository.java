package com.example.olaclass.data.repository;

import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingSource;
import androidx.paging.rxjava2.RxPagingSource;
import io.reactivex.Flowable;
import com.example.olaclass.data.model.Classroom;
import com.example.olaclass.data.paging.ClassroomPagingSource;

public class ClassroomRepository {
    public Flowable<PagingData<Classroom>> getClassroomsPaged() {
        return new Pager<>(
                new PagingConfig(10),
                () -> new ClassroomPagingSource()
        ).flowable();
    }
}
