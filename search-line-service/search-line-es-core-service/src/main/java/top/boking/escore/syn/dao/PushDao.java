package top.boking.escore.syn.dao;

import java.util.List;

public interface PushDao<T> {
    List<T> batchQueryComments(long offset, int limit);
}
