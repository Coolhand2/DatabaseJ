/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import java.util.Map;

/**
 *
 * @author Mike
 */
public interface Entity {
    public Integer getId();
    public void setId(Integer id);
    public Class<?> getEntityClass();
    public String getKeyName();
    public String getTableName();
    public Map<String, String> getColumnMap();
}
