package rtp.demo.debtor.complete.payment.pojo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

public interface BusinessCentralTaskInterface {

	@PUT
	@Path("/services/rest/server/containers/RTPProcessingEngine_1.0.0-SNAPSHOT/cases/instances/{caseId}/tasks/RTP%20Payment%20Fraud%20Detection%20Complete")
	@Produces(MediaType.APPLICATION_JSON)

	public void triggerAdhocTask(@PathParam("caseId") String caseId, String body);
}
