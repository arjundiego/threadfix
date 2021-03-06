package com.denimgroup.threadfix.plugin.zap.action;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;

import org.apache.log4j.Logger;
import org.parosproxy.paros.extension.ViewDelegate;
import org.parosproxy.paros.model.Model;

import com.denimgroup.threadfix.plugin.zap.dialog.ConfigurationDialogs;
import com.denimgroup.threadfix.plugin.zap.dialog.UrlDialog;

public class EndpointsAction extends JMenuItem {

	private static final long serialVersionUID = -3141841416510322529L;

	private static final Logger logger = Logger.getLogger(EndpointsAction.class);
	public static final String GENERIC_INT_SEGMENT = "\\{id\\}";

    private AttackThread attackThread = null;

    public EndpointsAction(final ViewDelegate view, final Model model) {
        logger.info("Initializing ThreadFix endpoint menu item");
        setText("ThreadFix: Import Endpoints");

        addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {

                logger.info("About to show dialog.");
                
                boolean configured = ConfigurationDialogs.show(view);
                boolean completed = false;
                
                if (configured) {
	                logger.info("Got application id, about to generate XML and use REST call.");
	
	                String csv = RestUtils.getEndpoints();
	                
	                if (csv == null || csv.trim().isEmpty()) {
	                	view.showWarningDialog("Failed to retrieve endpoints from ThreadFix. Check your key and url.");
	                } else {
	
	                	logger.info(csv);
	
		                for (String line : csv.split("\n")) {
		                    if (line != null && line.contains(",")) {
		                    	String endPoint = line.split(",")[1];
		                    	if(endPoint.startsWith("/")){
		                    		endPoint = endPoint.substring(1);
		                    	}
		                    	endPoint.replaceAll(GENERIC_INT_SEGMENT, "1");
		                    	nodes.add(endPoint);
		                    	String params = line.split(",")[2];
		                    	if(!params.equals("[]")){
		                    		params = params.substring(1,params.length()-1);
		                    		for(String p : params.split(" ")){
		                    			nodes.add(endPoint+"?"+p+"=true");
//		                    			nodes.add(endPoint+"?"+p+"=1");
		                    		}
		                    	}
		                    }
		                }
		                
		                String url = UrlDialog.show(view);
		
		                if (url != null) { // cancel not pressed
			                try {
			                	if(!url.substring(url.length()-1).equals("/")){
			                		url = url+"/";
			                	}
			                    attack(new URL(url));
			                    completed = true;
			                } catch (MalformedURLException e1) {
			                    logger.warn("Bad URL format.");
			                    view.showWarningDialog("Invalid URL.");
			                }
		                }
	                }
                }

                if (completed) {
                	view.showMessageDialog("The endpoints were successfully imported from ThreadFix.");
                }
            }
        });
    }

    public void notifyProgress(AttackThread.Progress progress) {
        logger.info("Status is " + progress);
    }

    public void attack (URL url) {
        logger.info("Starting url " + url);

        if (attackThread != null && attackThread.isAlive()) {
            return;
        }
        attackThread = new AttackThread(this);
        attackThread.setNodes(nodes);
        attackThread.setURL(url);
        attackThread.start();

    }

    List<String> nodes = new ArrayList<>();

}
