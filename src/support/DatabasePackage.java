/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Mike
 */
public class DatabasePackage {
    private String sql;
    private Collection<?> params;
    private Class<?> clazz;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Collection<?> getParams() {
        return params;
    }

    public void setParams(Collection<?> params) {
        this.params = params;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

}
