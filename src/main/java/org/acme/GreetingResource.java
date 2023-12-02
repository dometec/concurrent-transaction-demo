package org.acme;

import java.util.List;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.logging.Logger;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("hello")
public class GreetingResource {

	private static final Logger LOG = Logger.getLogger(GreetingResource.class);

	@Inject
	MyEntityDao dao;

	@Inject
	ManagedExecutor managedExecutor;

	@PostConstruct
	public void init() {
		dao.insert();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<PanacheEntityBase> listAll() {
		List<PanacheEntityBase> allPersons = MyEntityA.listAll();
		LOG.info("Size " + allPersons.size());
		return allPersons;
	}

	@POST
	@Transactional
	@Path("deadlock")
	public void deadlock() {

		managedExecutor.submit(() -> {

			QuarkusTransaction.requiringNew().run(() -> {

				dao.incrementA();

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					/* ignore */}

				dao.incrementB();

			});

		});

		dao.incrementB();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			/* ignore */}

		dao.incrementA();

	}

	@POST
	@Transactional
	@Path("locktimeout")
	public void locktimeout() throws Exception {
		dao.incrementA();
		Thread.sleep(100);
		dao.incrementAOnNewTransaction();
	}

	@POST
	@Transactional
	@Path("trasisolation")
	public void trasisolation() throws Exception {
		MyEntityA a = dao.getA();
		System.out.println(a);

		Thread.sleep(250);

		dao.incrementAOnNewTransaction();

		Thread.sleep(250);

		dao.refresh(a);
		System.out.println(a);

	}

}
