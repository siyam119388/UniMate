package com.unimate.dao;

import java.sql.SQLException;
import java.util.List;

/** Every DAO implements this, so they all look the same from the outside. */
public interface BaseDAO<T> {

    T findById(int id) throws SQLException;

    List<T> findAll() throws SQLException;

    boolean insert(T item) throws SQLException;
}
