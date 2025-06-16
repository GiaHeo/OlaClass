package com.example.olaclass.data.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;
import com.example.olaclass.data.model.Classroom;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.rxjava3.core.Single;


public class ClassroomPagingSource extends RxPagingSource<Integer, Classroom> {
    private static final int PAGE_SIZE = 10;
    private final Query query;

    public ClassroomPagingSource(Query query) {
        this.query = query;
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Classroom> state) {
        Integer anchorPosition = state.getAnchorPosition();
        if (anchorPosition == null) {
            return null;
        }
        
        LoadResult.Page<Integer, Classroom> anchorPage = state.closestPageToPosition(anchorPosition);
        if (anchorPage == null) {
            return null;
        }
        
        Integer prevKey = anchorPage.getPrevKey();
        if (prevKey != null) {
            return prevKey + 1;
        }
        
        Integer nextKey = anchorPage.getNextKey();
        if (nextKey != null) {
            return nextKey - 1;
        }
        
        return null;
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, Classroom>> loadSingle(@NonNull LoadParams<Integer> params) {
        return Single.<LoadResult<Integer, Classroom>>fromCallable(() -> {
            int page = params.getKey() != null ? params.getKey() : 1;
            QuerySnapshot querySnapshot = com.google.android.gms.tasks.Tasks.await(query.limit(PAGE_SIZE).get());
            List<Classroom> classrooms = new ArrayList<>();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                Classroom classroom = doc.toObject(Classroom.class);
                classroom.setId(doc.getId());
                classrooms.add(classroom);
            }
            Integer nextKey = classrooms.size() < PAGE_SIZE ? null : page + 1;
            Integer prevKey = page > 1 ? page - 1 : null;
            return (LoadResult<Integer, Classroom>) new LoadResult.Page<>(classrooms, prevKey, nextKey);
        }).onErrorReturn(e -> new LoadResult.Error<Integer, Classroom>(e));
    }
}
