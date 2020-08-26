package com.sofar.daogenerator;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

/**
 * 红点存储
 */
public class BadgeNumberDaoGenerator {

  public static void main(String args[]) throws Exception {
    Schema schema = new Schema(1, "com.sofar.fun.badge.db");

    Entity entity = schema.addEntity("BadgeRecord");
    entity.addLongProperty("id").primaryKey().autoincrement();
    entity.addIntProperty("type").unique();
    entity.addIntProperty("count");
    entity.addIntProperty("displayMode");

    new DaoGenerator().generateAll(schema, "./core/fun/src/main/java");
  }

}
