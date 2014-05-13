/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package factories;

import entities.DemoEntity;
import support.DatabaseManager;

/**
 *
 * @author Mike
 */
public class DatabaseManagerFactory {
    DatabaseManager<DemoEntity> demoEntityManager = null;
    
    public DatabaseManager<DemoEntity> getDemoEntity() {
        if(demoEntityManager == null) {
            demoEntityManager = new DatabaseManager<DemoEntity>();
        }
        return demoEntityManager;
    }
}
