package top.boking.escore.syn.dao;

import java.util.List;

public interface PushDao<T> {
    List<T> idCursorQuery(long offset, int limit);
}
