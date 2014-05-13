/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import interfaces.Entity;
import org.apache.log4j.Logger;

/**
 *
 * @author Mike
 */
public class DatabaseManager<T> {

    private Logger log = Logger.getLogger(DatabaseManager.class);
    private final String JNDI_NAME = "java:/jdbc/name-goes-here";

    public void insert(Entity e) {
        Map<String, Collection<?>> insert = SqlBuilder.buildInsert(e);
        String sql = getSql(insert);
        Collection<?> params = getParams(insert);
        Class<?> clazz = e.getEntityClass();
        write(sql, params, clazz);
    }

    public void insert(List<Entity> le) {
        List<DatabasePackage> list = new ArrayList();
        for (Entity e : le) {
            DatabasePackage dp = new DatabasePackage();
            Map<String, Collection<?>> map = SqlBuilder.buildInsert(e);
            dp.setSql(getSql(map));
            dp.setParams(getParams(map));
            dp.setClazz(e.getEntityClass());
            list.add(dp);
        }
        write(list);
    }

    public void update(Entity e) {
        Map<String, Collection<?>> update = SqlBuilder.buildUpdate(e);
        String sql = getSql(update);
        Collection<?> params = getParams(update);
        Class<?> clazz = e.getEntityClass();
        write(sql, params, clazz);
    }

    public void update(List<Entity> le) {
        List<DatabasePackage> list = new ArrayList();
        for (Entity e : le) {
            DatabasePackage dp = new DatabasePackage();
            Map<String, Collection<?>> map = SqlBuilder.buildUpdate(e);
            dp.setSql(getSql(map));
            dp.setParams(getParams(map));
            dp.setClazz(e.getEntityClass());
            list.add(dp);
        }
        write(list);
    }

    public void delete(Entity e) {
        Map<String, Collection<?>> delete = SqlBuilder.buildDelete(e);
        String sql = getSql(delete);
        Collection<?> params = getParams(delete);
        Class<?> clazz = e.getEntityClass();
        write(sql, params, clazz);
    }

    public void delete(List<Entity> le) {
        List<DatabasePackage> list = new ArrayList();
        for (Entity e : le) {
            DatabasePackage dp = new DatabasePackage();
            Map<String, Collection<?>> map = SqlBuilder.buildDelete(e);
            dp.setSql(getSql(map));
            dp.setParams(getParams(map));
            dp.setClazz(e.getEntityClass());
            list.add(dp);
        }
        write(list);
    }

    public Integer retrieveId(Entity e) {
        String sql = "SELECT (%s) FROM %s WHERE %s";
        Map<String, ?> values = SqlBuilder.getNonNullValues(e);
        String columnList = SqlBuilder.getColumnList(values.keySet());
        String table = e.getTableName();
        String columnMap = SqlBuilder.getColumnMap(values.keySet());
        sql = String.format(sql, columnList, table, columnMap);
        List<T> list = read(sql, values.values(), e.getEntityClass());
        if (!list.isEmpty()) {
            Entity entity = (Entity) list.get(0);
            return entity.getId();
        }
        return null;
    }

    public List<T> getAll(Entity e) {
        String sql = "SELECT * FROM %s";
        String table = e.getTableName();
        Class<?> clazz = e.getEntityClass();
        sql = String.format(sql, table);
        return read(sql, null, clazz);
    }

    public T getById(Entity e) {
        String sql = "SELECT * FROM %s WHERE %s = ?";
        String table = e.getTableName();
        String key = e.getKeyName();
        Class<?> clazz = e.getEntityClass();
        sql = String.format(sql, table, key);
        List<T> list = read(sql, Arrays.asList(e.getId()), clazz);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public void write(List<DatabasePackage> list) {
        for (DatabasePackage dp : list) {
            try {
                Connection conn = getConnection();
                conn.setAutoCommit(false);
                conn.setSavepoint();
                try {
                    String sql = dp.getSql();
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    try {
                        Collection<?> params = dp.getParams();
                        if (params != null) {
                            Iterator<?> itr = params.iterator();
                            for (int count = 0; itr.hasNext(); count++) {
                                stmt.setObject(count, itr.next());
                            }
                        }
                        ResultSet rs = stmt.executeQuery();
                        try {
                            //Do nothing?
                        } finally {
                            rs.close();
                        }
                    } finally {
                        stmt.close();
                    }
                } finally {
                    conn.close();
                }
            } catch (SQLException e) {
                log.fatal(e.getMessage());
            }
        }
    }

    public void write(String sql, Class<?> clazz) {
        write(sql, null, clazz);
    }

    public void write(String sql, Collection<?> params, Class<?> clazz) {
        try {
            Connection conn = getConnection();
            try {
                PreparedStatement stmt = conn.prepareStatement(sql);
                try {
                    if (params != null) {
                        Iterator<?> itr = params.iterator();
                        for (int count = 0; itr.hasNext(); count++) {
                            stmt.setObject(count, itr.next());
                        }
                    }
                    ResultSet rs = stmt.executeQuery();
                    try {
                        // Do nothing, because we expect nothing.
                    } finally {
                        rs.close();
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            log.fatal(e.getMessage());
        }
    }

    public List<T> read(String sql, Class<?> clazz) {
        return read(sql, null, clazz);
    }

    public List<T> read(String sql, Collection<?> params, Class<?> clazz) {
        List<Entity> returns = new ArrayList();
        try {
            Connection conn = getConnection();
            try {
                PreparedStatement stmt = conn.prepareStatement(sql);
                try {
                    if (params != null) {
                        Iterator<?> itr = params.iterator();
                        for (int count = 0; itr.hasNext(); count++) {
                            stmt.setObject(count, itr.next());
                        }
                    }
                    ResultSet rs = stmt.executeQuery();
                    try {
                        returns = ResultMapper.map(rs, clazz);
                    } finally {
                        rs.close();
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            log.fatal(e.getMessage());
        }
        List<T> list = new ArrayList();
        for (Entity entity : returns) {
            list.add((T) entity);
        }
        return list;
    }

    public List<List<T>> read(List<DatabasePackage> incoming) {
        List<List<Entity>> returns = new ArrayList();
        for (DatabasePackage dp : incoming) {
            try {
                Connection conn = getConnection();
                conn.setAutoCommit(false);
                conn.setSavepoint();
                try {
                    String sql = dp.getSql();
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    try {
                        Collection<?> params = dp.getParams();
                        if (params != null) {
                            Iterator<?> itr = params.iterator();
                            for (int count = 0; itr.hasNext(); count++) {
                                stmt.setObject(count, itr.next());
                            }
                        }
                        ResultSet rs = stmt.executeQuery();
                        try {
                            List<Entity> rows = ResultMapper.map(rs, dp.getClazz());
                            returns.add(rows);
                        } finally {
                            rs.close();
                        }
                    } finally {
                        stmt.close();
                    }
                } finally {
                    conn.close();
                }
            } catch (SQLException e) {
                log.fatal(e.getMessage());
            }
        }
        List<List<T>> list = new ArrayList();
        for (List<Entity> le : returns) {
            List<T> inner = new ArrayList();
            for (Entity entity : le) {
                inner.add((T) entity);
            }
            list.add(inner);
        }
        return list;
    }

    private Connection getConnection() {
        Connection conn = null;
        try {
            DataSource ds = getDataSource();
            conn = ds.getConnection();
        } catch (SQLException e) {
            log.fatal(e.getMessage());
        }
        return conn;
    }

    private DataSource getDataSource() {
        try {
            Context ctx = new InitialContext();
            DataSource dataSource = (DataSource) ctx.lookup(JNDI_NAME);
            return dataSource;
        } catch (NamingException ne) {
            log.fatal(ne.getMessage());
        }
        return null;
    }

    private String getSql(Map<String, Collection<?>> map) {
        String[] stringTemp = {""};
        String[] sqlArray = map.keySet().toArray(stringTemp);
        String sql = sqlArray[0];
        return sql;
    }

    private Collection<?> getParams(Map<String, Collection<?>> map) {
        Collection<?>[] collectionTemp = {new ArrayList()};
        Collection<?>[] paramsArray = map.values().toArray(collectionTemp);
        Collection<?> params = paramsArray[0];
        return params;
    }
}
