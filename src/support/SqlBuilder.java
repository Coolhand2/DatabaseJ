/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import interfaces.Entity;
import org.apache.log4j.Logger;

/**
 *
 * @author Mike
 */
public class SqlBuilder {
    private static Logger log = Logger.getLogger(SqlBuilder.class);

    public static Map<String, Collection<?>> buildInsert(Entity e) {
        Map<String, Collection<?>> returns = new WeakHashMap();
        String insert = "INSERT INTO %s (%s) VALUES (%s)";
        Map<String, ?> values = getNonNullValues(e);
        String table = e.getTableName();
        String columns = getColumnList(values.keySet());
        String placeholders = getPlaceholders(values.keySet());
        insert = String.format(insert, table, columns, placeholders);
        returns.put(insert, values.values());
        return returns;
    }

    public static Map<String, Collection<?>> buildUpdate(Entity e) {
        Map<String, Collection<?>> returns = new WeakHashMap();
        String update = "UPDATE %s SET %s WHERE %s = ?";
        Map<String, ?> values = getNonNullValues(e);
        String table = e.getTableName();
        String columnMap = getColumnMap(values.keySet());
        String key = e.getKeyName();
        update = String.format(update, table, columnMap, key);
        returns.put(update, values.values());
        return returns;
    }

    public static Map<String, Collection<?>> buildDelete(Entity e) {
        Map<String, Collection<?>> returns = new WeakHashMap();
        String delete = "DELTE FROM %s WHERE %s = ?";
        String table = e.getTableName();
        String key = e.getKeyName();
        Integer id = e.getId();
        delete = String.format(delete, table, key);
        returns.put(delete, Arrays.asList(id));
        return returns;
    }

    public static Map<String, ?> getNonNullValues(Entity e) {
        Map<String, Object> values = new WeakHashMap();
        Map<String, String> columnMap = e.getColumnMap();
        Class<?> clazz = e.getEntityClass();
        Field[] fields = clazz.getFields();
        for(Field field : fields) {
            try {
                Object o = field.get(e);
                if(o != null) {
                    String name = field.getName();
                    values.put(columnMap.get(name), o);
                }
            } catch (IllegalArgumentException ex) {
                log.fatal(ex);
            } catch (IllegalAccessException ex) {
                log.fatal(ex);
            }
        }
        return values;
    }

    public static String getPlaceholders(Collection<String> columns) {
        StringBuilder sb = new StringBuilder();
        for(String column : columns) {
            sb.append("?, ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    public static String getColumnList(Collection<String> columns) {
        StringBuilder sb = new StringBuilder();
        for(String column : columns) {
            sb.append(column);
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    public static String getColumnMap(Collection<String> columns) {
        StringBuilder sb = new StringBuilder();
        for(String column : columns) {
            sb.append(column);
            sb.append(" = ?, ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}
