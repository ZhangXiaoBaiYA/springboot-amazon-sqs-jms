package cn.lastgo.sqs.entity;

import javax.persistence.*;

@Entity
public class TransferFeeDailyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(length = BasicConfig.Length.PUBLIC_AND_SECRET_KEY)
    public String addr;

    @Column(length = BasicConfig.Length.LONG_TEXT)
    public String feeAssetName;

    @Column(length = BasicConfig.Length.LONG_TEXT)
    public String feeAssetIssuer;

    @Column(length = BasicConfig.Length.AMOUNT_AND_PRICE)
    public String feeAmount;

    @Enumerated(EnumType.STRING)
    public Status status;

    @Column(length = BasicConfig.Length.LONG_TEXT)
    public String resultXdr;

    @Column(length = BasicConfig.Length.LONG_TEXT)
    public String envolopeXdr;

    @Column(length = BasicConfig.Length.SHORT_TEXT)
    public String memo;

    public enum Status {
        WAIT_PAY, TO_PAY, PAYED, FAIL
    }

    @Column(length = BasicConfig.Length.SHORT_TEXT)
    public String createTime;
    public Long updateTime = System.currentTimeMillis();
    @Version
    public Long version;
}
