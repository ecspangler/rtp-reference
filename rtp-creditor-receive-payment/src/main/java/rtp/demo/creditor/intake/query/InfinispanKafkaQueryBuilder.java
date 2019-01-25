package rtp.demo.creditor.intake.query;

import org.apache.camel.component.infinispan.InfinispanQueryBuilder;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;

import rtp.demo.creditor.domain.account.Account;

public class InfinispanKafkaQueryBuilder implements InfinispanQueryBuilder {

	@Override
	public Query build(QueryFactory queryFactory) {
		return queryFactory.from(Account.class).having("accountNumber").like("%12000194212199001%").build();
	}

}
