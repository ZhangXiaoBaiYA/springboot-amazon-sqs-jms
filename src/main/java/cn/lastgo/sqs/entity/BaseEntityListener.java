package cn.lastgo.sqs.entity;

import javax.persistence.PreUpdate;

public class BaseEntityListener {
  @PreUpdate
  public void preUpdate(BaseEntity entity) {
    entity.updateTime = System.currentTimeMillis();
  }
}
