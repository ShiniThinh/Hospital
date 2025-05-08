package dao;
import model.Patient;

import java.util.List;

public interface DAOInterface<T> {
    public void insert(T t);

    public List<T> selectAll();

    public List<T> selectByCondition(String condition);

}
