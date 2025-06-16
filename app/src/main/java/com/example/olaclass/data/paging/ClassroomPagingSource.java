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
        android.util.Log.d("ClassroomPagingSource", "Bắt đầu load trang mới");
        
        return Single.create(emitter -> {
            int page = params.getKey() != null ? params.getKey() : 1;
            
            // Thực hiện query Firestore bất đồng bộ
            query.limit(PAGE_SIZE).get()
                .addOnSuccessListener(querySnapshot -> {
                    try {
                        List<Classroom> classrooms = new ArrayList<>();
                        if (querySnapshot.isEmpty()) {
                            android.util.Log.w("ClassroomPagingSource", "QuerySnapshot trả về rỗng!");
                        }
                        
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Classroom classroom = doc.toObject(Classroom.class);
                            classroom.setId(doc.getId());
                            android.util.Log.d("ClassroomPagingSource", "Classroom: " + classroom.getId() + " - " + classroom.getName());
                            classrooms.add(classroom);
                        }
                        
                        android.util.Log.d("ClassroomPagingSource", "Số lượng classroom lấy được: " + classrooms.size());
                        Integer nextKey = classrooms.size() < PAGE_SIZE ? null : page + 1;
                        Integer prevKey = page > 1 ? page - 1 : null;
                        
                        emitter.onSuccess(new LoadResult.Page<>(
                            classrooms,
                            prevKey,
                            nextKey
                        ));
                    } catch (Exception e) {
                        android.util.Log.e("ClassroomPagingSource", "Lỗi khi xử lý dữ liệu: " + e.getMessage(), e);
                        emitter.onError(e);
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("ClassroomPagingSource", "Lỗi khi truy vấn Firestore: " + e.getMessage(), e);
                    emitter.onError(e);
                });
        });
    }
}
