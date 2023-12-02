package org.acme;

import org.jboss.logging.Logger;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@Startup
@ApplicationScoped
public class MyEntityDao {

	private static final Logger LOG = Logger.getLogger(GreetingResource.class);

	@Transactional(value = TxType.REQUIRED)
	public void insert() {

		MyEntityA myEntityA = new MyEntityA();
		myEntityA.field = "entityA";
		myEntityA.counter = -1;
		myEntityA.persistAndFlush();

		MyEntityB myEntityB = new MyEntityB();
		myEntityB.field = "entityB";
		myEntityB.counter = -1;
		myEntityB.persistAndFlush();

		LOG.info("Inserted");

	}

	@Transactional(value = TxType.REQUIRED)
	public MyEntityA getA() {
		return MyEntityA.findById(1);
	}

	@Transactional(value = TxType.REQUIRED)
	public void refresh(MyEntityA a) {
		MyEntityA.getEntityManager().refresh(a);
	}

	@Transactional(value = TxType.REQUIRED)
	public void incrementA() {

		MyEntityA entity = MyEntityA.findById(1);
		entity.counter = entity.counter + 1;
		entity.persistAndFlush();

		LOG.info("After increment 1 A " + entity);

	}

	@Transactional(value = TxType.REQUIRED)
	public void incrementB() {

		MyEntityB entity = MyEntityB.findById(1);
		entity.counter = entity.counter + 1;
		entity.persistAndFlush();

		LOG.info("After increment 1 B " + entity);

	}

	@Transactional(value = TxType.REQUIRES_NEW)
	public void incrementAOnNewTransaction() {
		incrementA();
	}

}
