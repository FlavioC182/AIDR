package qa.qcri.aidr.collector.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.aidr.collector.beans.CollectionTask;
import qa.qcri.aidr.collector.beans.FacebookCollectionTask;
import qa.qcri.aidr.collector.beans.FacebookProfile;
import qa.qcri.aidr.collector.beans.ResponseWrapper;
import qa.qcri.aidr.collector.collectors.FacebookFeedTracker;
import qa.qcri.aidr.collector.service.CollectionService;
import qa.qcri.aidr.collector.utils.CollectorConfigurationProperty;
import qa.qcri.aidr.collector.utils.CollectorConfigurator;
import qa.qcri.aidr.collector.utils.GenericCache;

@RestController
@RequestMapping("/facebook")
public class FacebookCollectionController extends BaseController<FacebookCollectionTask> {

	private static Logger logger = Logger.getLogger(FacebookCollectionController.class);
    private static CollectorConfigurator configProperties = CollectorConfigurator.getInstance();
    
    @Autowired 
    CollectionService collectionService;
    
    @RequestMapping(value = "/start", method={RequestMethod.POST})
	public Response startCollection(@RequestBody FacebookCollectionTask task) {
        ResponseWrapper response = new ResponseWrapper();

        //check if all fb specific information is available in the request
        if (!task.checkSocialConfigInfo()) {
            response.setStatusCode(configProperties.getProperty(CollectorConfigurationProperty.STATUS_CODE_COLLECTION_ERROR));
            response.setMessage("One or more Facebook authentication token(s) are missing");
            return Response.ok(response).build();
        }

        //check if all query parameters are missing in the query
        if (!task.isToTrackAvailable() && !task.isToFollowAvailable()) {
            response.setStatusCode(configProperties.getProperty(CollectorConfigurationProperty.STATUS_CODE_COLLECTION_ERROR));
            response.setMessage("Missing one or more fields (toTrack & toFollow). At least one field is required");
            return Response.ok(response).build();
        }
        String collectionCode = task.getCollectionCode();

        //check if a task is already running with same configurations
        logger.info("Checking OAuth parameters for " + collectionCode);
        GenericCache cache = GenericCache.getInstance();
		if (cache.isConfigExists(task)) {
            String msg = "Provided OAuth configurations already in use. Please stop this collection and then start again.";
            logger.info(collectionCode + ": " + msg);
            response.setMessage(msg);
            response.setStatusCode(configProperties.getProperty(CollectorConfigurationProperty.STATUS_CODE_COLLECTION_ERROR));
            return Response.ok(response).build();
        }

		task.setStatusCode(configProperties.getProperty(CollectorConfigurationProperty.STATUS_CODE_COLLECTION_INITIALIZING));
		logger.info("Initializing connection with Twitter streaming API for collection " + collectionCode);
		try {
	
			FacebookFeedTracker tracker = new FacebookFeedTracker(task);
			tracker.start();

			String cacheKey = task.getCollectionCode();
			cache.incrCounter(cacheKey, new Long(0));

			// if twitter streaming connection successful then change the status
			// code
			task.setStatusCode(configProperties.getProperty(CollectorConfigurationProperty.STATUS_CODE_COLLECTION_RUNNING));
			task.setStatusMessage(null);
			cache.setFbConfigMap(cacheKey, task);
			cache.setFacebookTracker(cacheKey, tracker);
			if(task.getPersist()!=null){
				if(task.getPersist()){
					startPersister(collectionCode, task.isSaveMediaEnabled());
				}
			}
			else{
				if (Boolean.valueOf(configProperties.getProperty(CollectorConfigurationProperty.DEFAULT_PERSISTANCE_MODE))) {
					startPersister(collectionCode, task.isSaveMediaEnabled());
				}
			}
			response.setMessage(configProperties.getProperty(CollectorConfigurationProperty.STATUS_CODE_COLLECTION_INITIALIZING));
			response.setStatusCode(configProperties.getProperty(CollectorConfigurationProperty.STATUS_CODE_COLLECTION_INITIALIZING));
		} catch (Exception ex) {
			logger.error("Exception in creating TwitterStreamTracker for collection " + collectionCode, ex);
			response.setMessage(ex.getMessage());
			response.setStatusCode(configProperties.getProperty(CollectorConfigurationProperty.STATUS_CODE_COLLECTION_ERROR));
		}
		return Response.ok(response).build();
	}

    @RequestMapping("/status")
    public Response getStatus(@RequestParam("id") String id) {
        ResponseWrapper response = new ResponseWrapper();
        if (StringUtils.isEmpty(id)) {
            response.setMessage("Invalid key. No running collector found for the given id.");
            response.setStatusCode(configProperties.getProperty(CollectorConfigurationProperty.STATUS_CODE_COLLECTION_NOTFOUND));
            return Response.ok(response).build();
        }
        
        CollectionTask failedTask = GenericCache.getInstance().getFailedCollectionTask(id);
        if (failedTask != null) {
        	Response stopTaskResponse = stopCollection(id);
        	
            if(stopTaskResponse == null) {
    	        ResponseWrapper responseWrapper = new ResponseWrapper();
    	        responseWrapper.setMessage("Invalid key. No running collector found for the given id.");
    	        responseWrapper.setStatusCode(configProperties.getProperty(CollectorConfigurationProperty.STATUS_CODE_COLLECTION_NOTFOUND));
    	        stopTaskResponse = Response.ok(responseWrapper).build();
            } 
           
            return stopTaskResponse;
        }
        
        FacebookCollectionTask task = GenericCache.getInstance().getFacebookConfig(id);
        if (task != null) { 
        	return Response.ok(task).build();
        }

        response.setMessage("Invalid key. No running collector found for the given id.");
        response.setStatusCode(configProperties.getProperty(CollectorConfigurationProperty.STATUS_CODE_COLLECTION_NOTFOUND));
        return Response.ok(response).build();
    }
    
    @RequestMapping(value = "/searchProfiles", method={RequestMethod.POST})
    public Response searchProfiles(@RequestBody String accessToken, @RequestParam String keyword, 
    		@RequestParam Integer limit, @RequestParam Integer offset) {
		FacebookFeedTracker tracker = new FacebookFeedTracker(accessToken);
    	List<FacebookProfile> fbProfiles = tracker.searchProfiles(keyword, limit, offset);
    	Response.ok(fbProfiles).build();
    	return Response.ok(fbProfiles).build();
    }
    
    @RequestMapping("/rerun")
    public Map<String, String> rerunCollection(@RequestParam("code") String code) {
    	if(GenericCache.getInstance().getFbConfigMap(code) != null 
    			&& !GenericCache.getInstance().getFbConfigMap(code).isPullInProgress()) {
	    	FacebookFeedTracker tracker = GenericCache.getInstance().getFacebookTracker(code);
	    	tracker.start();
	    	logger.info("Collection " + code + " started.");
    	}
    	Map<String, String> message = new HashMap<String, String>();
    	message.put("message", "SUCCESS");
    	return message;
    }

    protected Response stopCollection(String collectionCode) {
    	GenericCache cache = GenericCache.getInstance();

    	cache.setFbSyncStateMap(collectionCode, 1);
    	FacebookFeedTracker tracker = cache.getFacebookTracker(collectionCode);
    	FacebookCollectionTask task = cache.getFacebookConfig(collectionCode);
    	
    	cache.delFbSyncObjMap(collectionCode);
    	cache.delFbSyncStateMap(collectionCode);
        cache.delFailedCollection(collectionCode);
        cache.deleteCounter(collectionCode);
        cache.delFbConfigMap(collectionCode);
        cache.delLastDownloadedDoc(collectionCode);
        cache.delFacebookTracker(collectionCode);
        cache.delReconnectAttempts(collectionCode);

		if (tracker != null) {
			try {
				tracker.close();
			} catch (IOException e) {
				ResponseWrapper response = new ResponseWrapper();
				response.setMessage(e.getMessage());
				response.setStatusCode(configProperties.getProperty(CollectorConfigurationProperty.STATUS_CODE_COLLECTION_NOTFOUND));
				return Response.ok(response).build();
			}
			if(task.getPersist()!=null){
				if(task.getPersist()){
					stopPersister(collectionCode);
				}
			}
			else{
				if (Boolean.valueOf(configProperties.getProperty(CollectorConfigurationProperty.DEFAULT_PERSISTANCE_MODE))) {
					stopPersister(collectionCode);
				}
			}
            logger.info(collectionCode + ": " + "Collector has been successfully stopped.");
        } else {
            logger.info("No collector instances found to be stopped with the given id:" + collectionCode);
        }
		if (task != null) {
        	task.setStatusCode(configProperties.getProperty(CollectorConfigurationProperty.STATUS_CODE_COLLECTION_STOPPED));
            return Response.ok(task).build();
        }
		return null;
    }

}
