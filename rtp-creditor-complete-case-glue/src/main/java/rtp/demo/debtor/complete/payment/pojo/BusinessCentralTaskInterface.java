package rtp.demo.debtor.complete.payment.pojo;

import rtp.demo.debtor.domain.model.payment.Payment;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

public interface BusinessCentralTaskInterface {

   @PUT
   @Path("/services/rest/server/containers/RTPProcessingEngine_1.0.0-SNAPSHOT/cases/instances/{caseId}/tasks/Transaction%20Completed")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)

   public void triggerAdhocTask(@PathParam("caseId") String caseId, String body);

   @POST
   @Path("/services/rest/server/containers/RTPProcessingEngine_1.0.0-SNAPSHOT/cases/instances/{caseId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public void closeCase(@PathParam("caseId") String caseId);

   @GET
   @Path("/services/rest/server/containers/RTPProcessingEngine_1.0.0-SNAPSHOT/cases/instances/{caseId}/caseFile")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public String getCaseFile(@PathParam("caseId") String caseId);

   @GET
   @Path("/services/rest/server/containers/RTPProcessingEngine_1.0.0-SNAPSHOT/cases/instances/{caseId}")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public String getCaseMetrics(@PathParam("caseId") String caseId);


}
