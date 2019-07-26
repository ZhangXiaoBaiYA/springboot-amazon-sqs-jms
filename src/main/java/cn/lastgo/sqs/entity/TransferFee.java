package cn.lastgo.sqs.entity;

import javax.persistence.*;

@Entity
public class TransferFee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(length = 256)
    public String transaction;
}
