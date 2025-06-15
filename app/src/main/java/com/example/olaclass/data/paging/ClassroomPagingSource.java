package com.example.olaclass.data.paging;

import androidx.annotation.NonNull;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;
import com.example.olaclass.data.model.Classroom;
import java.util.ArrayList;
import java.util.List;

public class ClassroomPagingSource extends PagingSource<Integer, Classroom> {
    private static final int PAGE_SIZE = 10;
    // TODO: Replace with Firestore data source. For now, use mock data.
    @NonNull
    @Override
    public LoadResult<Integer, Classroom> load(@NonNull LoadParams<Integer> params) {
        int page = params.getKey() == null ? 1 : params.getKey();
        List<Classroom> data = new ArrayList<>();
        for (int i = 0; i < PAGE_SIZE; i++) {
            int idNum = (page - 1) * PAGE_SIZE + i + 1;
            data.add(new Classroom(
                String.valueOf(idNum),
                "Lớp học " + idNum,
                "Mô tả lớp học số " + idNum,
                "teacher_" + ((idNum % 3) + 1)
            ));
        }
        Integer prevKey = (page == 1) ? null : page - 1;
        Integer nextKey = (data.size() < PAGE_SIZE) ? null : page + 1;
        return new LoadResult.Page<>(data, prevKey, nextKey);
    }

    @NonNull
    @Override
    public PagingState<Integer, Classroom> getRefreshKey(@NonNull PagingState<Integer, Classroom> state) {
        return null;
    }
}
