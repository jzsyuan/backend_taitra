package models;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Model;
import play.db.ebean.EbeanConfig;
import utils.EbeanUtils;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseModel extends Model {
    @Override
    public void save() {
        getEbeanServer().save(this);
    }

    @Override
    public void update() {
        getEbeanServer().update(this);
    }

    @Override
    public boolean delete() {
        return getEbeanServer().delete(this);
    }


    public static EbeanServer getEbeanServer() {
        return Ebean.getDefaultServer();
    }

    public static EbeanServer getEbeanServer(String dbName) {
        return Ebean.getServer(dbName);
        //return Ebean.getServer("default");
        //EbeanServer secondary = Ebean.getServer("secondary");
    }
}
