package de.mtc.jira.wasaut;


import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

/**
 * Simple JIRA listener using the atlassian-event library and demonstrating
 * plugin lifecycle integration.
 */
@Scanned
@Component
public class IssueListener implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(IssueListener.class);

    @ComponentImport
    private final EventPublisher eventPublisher;

    /**
     * Constructor.
     * @param eventPublisher injected {@code EventPublisher} implementation.
     */
    @Autowired
    public IssueListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Called when the plugin has been enabled.
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
    	log.info("\n==================================\n");
    	log.info("\n   Enabling Jira-Holiday Plugin   \n");
    	log.info("\n   Jira-Holiday has been enabled    \n");
        eventPublisher.register(this);
    }

    /**
     * Called when the plugin is being disabled or removed.
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
    	log.info("Plugin jira-holiday is destroyed");
        eventPublisher.unregister(this);
    }

    
    /**
     * Receives any {@code IssueEvent}s sent by JIRA.
     * @param issueEvent the IssueEvent passed to us
     */
    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) {
    	System.out.println("An event has happened");
        Long eventTypeId = issueEvent.getEventTypeId();
        // Issue issue = issueEvent.getIssue();
        // if it's an event we're interested in, log it
        if(eventTypeId.equals(EventType.ISSUE_UPDATED_ID)) {
    		log.debug("Registered issue update event ... ");
    		if(!Arrays.asList(PluginConstants.PROJECTS).contains(issueEvent.getProject().getKey())) {
    			log.debug("... but not in my projects");
    		}		
    		Issue issue = issueEvent.getIssue();
    		// debugFields(issue, "BEFORE");
    		try {
    			new ProjectHelper().updateFields(issue, CSVParser.getData());
    		} catch (Exception e) {
    			e.printStackTrace();
    			log.error(e.getMessage());
    		}
    		//debugFields(issue, "AFTER");
        }
        
//        if (eventTypeId.equals(EventType.ISSUE_CREATED_ID)) {
//            log.info("Issue {} has been created at {}.", issue.getKey(), issue.getCreated());
//        } else if (eventTypeId.equals(EventType.ISSUE_RESOLVED_ID)) {
//            log.info("Issue {} has been resolved at {}.", issue.getKey(), issue.getResolutionDate());
//        } else if (eventTypeId.equals(EventType.ISSUE_CLOSED_ID)) {
//            log.info("Issue {} has been closed at {}.", issue.getKey(), issue.getUpdated());
//        }
    }
}


