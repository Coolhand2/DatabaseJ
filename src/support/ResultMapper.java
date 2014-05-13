package support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import interfaces.Entity;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ResultMapper {

    private static Logger log = LogManager.getLogger(ResultMapper.class.getName());

    @SuppressWarnings("unchecked")
    public static List<Entity> map(ResultSet rs, Class outputClass) {
        List<Entity> outputList = new ArrayList();;
        try {
            // make sure resultset is not null
            if (rs != null) {
                // get the resultset metadata
                ResultSetMetaData rsmd = rs.getMetaData();
                // get all the attributes of outputClass
                Method[] methods = outputClass.getDeclaredMethods();
                Map<String, Method> methodMap = new WeakHashMap();
                for (Method m : methods) {
                    methodMap.put(m.getName().toLowerCase(), m);
                }
                while (rs.next()) {
                    Entity bean = (Entity) outputClass.newInstance();
                    for (int _iterator = 1; _iterator <= rsmd.getColumnCount(); _iterator++) {
                        // getting the SQL column name
                        String columnName = rsmd.getColumnName(_iterator);
                        String columnType = rsmd.getColumnTypeName(_iterator);
                        columnName = columnName.replace("_", "");
                        Object columnValue = rs.getObject(_iterator);
                        Method m = methodMap.get("set" + columnName.toLowerCase());
                        if (m == null) {
                            log.fatal("No setter found for column: " + columnName);
                            continue;
                        }
                        if (columnName.contains("id")) {
                            Entity e = EntityConverter.convert(columnValue, m.getParameterTypes()[0].getName());
                            m.invoke(bean, e);
                        } else {
                            m.invoke(bean, columnValue);
                        }
                    }
                    outputList.add(bean);
                }
            } else {
                return null;
            }
        } catch (IllegalAccessException ex) {
            log.fatal(ex.getMessage());
        } catch (SQLException ex) {
            log.fatal(ex.getMessage());
        } catch (InstantiationException ex) {
            log.fatal(ex.getMessage());
        } catch (InvocationTargetException ex) {
            log.fatal(ex.getMessage());
        }
        return outputList;
    }
}