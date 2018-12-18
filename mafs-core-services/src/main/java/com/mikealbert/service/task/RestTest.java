package com.mikealbert.service.task;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.mikealbert.data.enumeration.DocumentNameEnum;

public class RestTest {

public static void main(String[] args) {
		
		try {
			
		
		    
		   //org.codehaus.jackson.jaxrs.JacksonJsonProvider
			POArchieveTask task = new POArchieveTask();
			task.setTaskId(601L);			
			task.setDocId(122L);
			task.setDocNameEnumType(DocumentNameEnum.THIRD_PARTY_PURCHASE_ORDER);
			
			System.out.println(" start RestTemplate -1");
	
			MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
			jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			RestTemplate restTemplate = new RestTemplate();;
			restTemplate.getMessageConverters().add(jsonHttpMessageConverter);
			 
			final String uri = "http://localhost:8080/generic-task-processing/tasks/create";
			TaskResponse result = restTemplate.postForObject( uri, task, TaskResponse.class);
			
			
	
			System.out.println(" end -RestTemplate 1"+result);
			
		} catch (Throwable e) {
			System.out.println(" Throwable -1");
			e.printStackTrace();
		}
		
}
		

/*
public static void main(String[] args) {
	
	try {
		
		List<Object> providers = new ArrayList<Object>();
	    providers.add( new JacksonJsonProvider() );
	    
	   
		POArchieveTask arctask = new POArchieveTask();
		arctask.setTaskId(101L);
		arctask.setDocId(122L);
		arctask.setDocType("TP DOC");
		
		TALTransactionTask trxtask = new TALTransactionTask();
		trxtask.setTaskId(601L);		
		trxtask.setState("OH");
		trxtask.setTrxCode(155L);
		
		
		POArchieveTaskRequest taskReq = new POArchieveTaskRequest();
		taskReq.setTaskId(99L);			
		taskReq.setDocId(991L);
		taskReq.setDocType("TP DOC 99");
		
		
		
		@SuppressWarnings("static-access")
		Client client = ClientBuilder.newBuilder().newClient().register(JacksonJsonProvider.class);
		Invocation.Builder builder = client.target("http://localhost:8080/generic-task-processing/tasks").path("/create").request(MediaType.APPLICATION_JSON_TYPE);
	
	//	ProducerTemplate template = camelContext.createProducerTemplate();
		
		System.out.println(" start arctask-"+arctask);	
		Entity entity = Entity.entity(arctask,MediaType.APPLICATION_JSON_TYPE);
		GenericTaskResponse response =	builder.post(entity,GenericTaskResponse.class);			
		System.out.println(" end arctask -response"+response);
	
		System.out.println(" start trxtask-"+trxtask);	
		 entity = Entity.entity(trxtask,MediaType.APPLICATION_JSON_TYPE);
		 response =	builder.post(entity,GenericTaskResponse.class);			
		System.out.println(" end arctask -response"+response);
	
	
		
	} catch (Throwable e) {
		System.out.println(" Throwable -1");
		e.printStackTrace();
	}
}*/

}
