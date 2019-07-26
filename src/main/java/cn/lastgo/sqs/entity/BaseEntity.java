package cn.lastgo.sqs.entity;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
@EntityListeners(BaseEntityListener.class)
public abstract class BaseEntity {
  public Long createTime = System.currentTimeMillis();
  public Long updateTime = System.currentTimeMillis();
  @Version
  public Long version;
}
