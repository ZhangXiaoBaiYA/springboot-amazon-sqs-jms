package cn.lastgo.sqs.repo;

import cn.lastgo.sqs.entity.TransferFee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferFeeRepo extends JpaRepository<TransferFee,Long> {
}
