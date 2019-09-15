package rtp.demo.debtor.complete.payment.pojo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

public interface BusinessCentralTaskInterface {

	@PUT
	@Path("/services/rest/server/containers/RTPProcessingEngine_1.0.0-SNAPSHOT/cases/instances/{caseId}/tasks/RTP%20Payment%20Account%20Validation%20Complete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void triggerAdhocTask(@PathParam("caseId") String caseId, String body);
}
