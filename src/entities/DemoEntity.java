/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import interfaces.Entity;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * It is important that the entities represent the structure of the table, and nothing more.
 * @author Mike
 */
public class DemoEntity implements Entity {
    /**
     * First, the columns of the table, appropriately named
     */
    private Integer id;
    private String name;
    private String description;

    /**
     * Then the requisite methods from the Entity interface.
     */
    
    /**
     * getId returns the id field, regardless of name. Necessary for EntityConverter.
     * @return 
     */
    @Override
    public Integer getId() {
        return id;
    }

    /**
     * setId sets the id field to the given parameter.
     * @param id Integer representing the id field of the Entity.
     */
    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * In order for the ResultMapper to work, it needs to know what class to 
     * reflect and then map this stuff to. That's what this is for.
     * @return 
     */
    @Override
    public Class<?> getEntityClass() {
        return DemoEntity.class;
    }

    /**
     * Returns the name of the primary key for this table.
     * @return 
     */
    @Override
    public String getKeyName() {
        return "demo_id";
    }

    /**
     * Returns the name of this table.
     * @return 
     */
    @Override
    public String getTableName() {
        return "demo";
    }

    /**
     * Maps class properties to column names. We use weakhashmap here in order to
     * help with garbage collection.
     * @return 
     */
    @Override
    public Map<String, String> getColumnMap() {
        Map<String, String> map = new WeakHashMap();
        map.put("id", "demo_id");
        map.put("name", "demo_name");
        map.put("description", "demo_description");
        return map;
    }
    
    /**
     * Then the getters and setters for the properties. Ideally, this won't 
     * include the id field, but no promises.
     */
    
    /**
     * 
     * @return 
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return 
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description 
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
