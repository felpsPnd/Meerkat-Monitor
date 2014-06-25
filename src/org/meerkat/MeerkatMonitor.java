// $codepro.audit.disable logExceptions
/**
 * Meerkat Monitor - Network Monitor Tool
 * Copyright (C) 2012 Merkat-Monitor
 * mailto: contact AT meerkat-monitor DOT org
 * 
 * Meerkat Monitor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Meerkat Monitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with Meerkat Monitor.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.meerkat;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.SwingUtilities;
import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;
import org.meerkat.db.EmbeddedDB;
import org.meerkat.group.AppGroupCollection;
import org.meerkat.gui.SplashScreen;
import org.meerkat.httpServer.HttpServer;
import org.meerkat.network.MailManager;
import org.meerkat.network.NetworkUtil;
import org.meerkat.network.RSS;
import org.meerkat.util.LogSettings;
import org.meerkat.util.MasterKeyManager;
import org.meerkat.util.MeerkatGeneralOperations;
import org.meerkat.util.PropertiesLoader;
import org.meerkat.util.sql.SQLDriverLoader;
import org.meerkat.webapp.WebAppCollection;
import org.meerkat.ws.MeerkatWebService;

public class MeerkatMonitor {

	private static String version = "0.6.0";

	private static Logger log = Logger.getLogger(MeerkatMonitor.class);
	private static Integer webserverPort;
	private static String propertiesFile = "meerkat.properties";
	private static String configFile = "meerkat.webapps.xml";
	private static Properties properties;
	private static HttpServer httpWebServer;
	private static NetworkUtil netUtil = new NetworkUtil();
	private static String hostname = netUtil.getHostname();
	private static String webServiceRoot = "/api";
	private static RSS rssFeed;
	private static MasterKeyManager mkm;
	private static MeerkatGeneralOperations mgo;
	private static boolean isSplashSupported = false;
	private static SplashScreen splashScreen;

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int numberStepsToProgress = 14;
		double percentIncrease = 100 / numberStepsToProgress;
		double currProgress = 0;
		try{
			splashScreen = new SplashScreen(version);
		}catch (Exception e){
			log.warn("No Graphical interface available: "+e.getMessage());
		}
		// Show splash screen if available
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					splashScreen.showScreen();
					isSplashSupported = true;
				}
				catch ( Exception e ) {
					log.info("Desktop environment not available. [Running in console mode]");
				}
			}	
		});
		
		if(isSplashSupported){
			splashScreen.setProgress("Meekat-Monitor v."+version+" starting...",(int)Math.round(currProgress));
			currProgress += percentIncrease;
		}
		log.info("Meekat-Monitor v."+version+" starting...");
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			log.error("Faile to sleep thread! "+e1.getMessage());
		}


		// Setup general log settings
		if(isSplashSupported){
			splashScreen.setProgress("Loading settings...", (int)Math.round(currProgress));
			currProgress += percentIncrease;
		}
		log.info("Updating log setting...");
		LogSettings ls = new LogSettings();
		ls.setupLogGeneralOptions();
		// Set up apache cxf log through log4j
		java.util.logging.Logger jlog = java.util.logging.Logger.getLogger("org.apache.cxf");
		jlog.setLevel(Level.WARNING);
		// Set httpclient log to error only
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "error");
		System.setProperty("derby.locks.deadlockTrace", "true");

		// Load SQL Drivers
		if(isSplashSupported){
			splashScreen.setProgress("Loading JDBC Drivers and DB...", (int)Math.round(currProgress));
			currProgress += percentIncrease;
		}
		log.info("Loading JDBC Drivers...");
		SQLDriverLoader sqlDL = new SQLDriverLoader();
		sqlDL.loadDrivers();

		// Setup embedded Database
		if(isSplashSupported){
			splashScreen.setProgress("Setting up embedded database...", (int)Math.round(currProgress));
			currProgress += percentIncrease;
		}
		log.info("Setting up embedded database...");
		EmbeddedDB ebd = new EmbeddedDB();
		// Load the driver first time
		ebd.loadDriver();		
		ebd.initializeDB();
		
		/**
		if(isSplashSupported){
			splashScreen.setProgress("Executing database maintenance...", (int)Math.round(currProgress));
			currProgress += percentIncrease;
		}
		ebd.executeDBMaintenance();
		*/

		// Prepare applications settings
		if(isSplashSupported){
			splashScreen.setProgress("Loading application settings...", (int)Math.round(currProgress));
			currProgress += percentIncrease;
		}
		log.info("Loading application settings...");
		PropertiesLoader pL = new PropertiesLoader(propertiesFile);
		pL.validateProperties(); // validate present properties
		properties = pL.getPropetiesFromFile();

		// Prepare temporary working directory
		if(isSplashSupported){
			splashScreen.setProgress("Creating applications...", (int)Math.round(currProgress));
			currProgress += percentIncrease;
		}
		log.info("Setting temporary dir: ");
		mgo = new MeerkatGeneralOperations(configFile, version);
		String tempWorkingDir = mgo.getTmpWorkingDir();

		// Loading / Creating applications
		WebAppCollection webAppsCollection = mgo.loadWebAppsXML();

		// Create the password manager MasterKeyManager
		mkm = new MasterKeyManager(propertiesFile, webAppsCollection);

		// Generate applications groups
		if(isSplashSupported){
			splashScreen.setProgress("Creating groups...", (int)Math.round(currProgress));
			currProgress += percentIncrease;
		}
		log.info("Creating applications groups...");
		AppGroupCollection appGroupCollection = new AppGroupCollection();
		appGroupCollection.populateGroups(webAppsCollection);
		appGroupCollection.printLogGroupMembers();
		webAppsCollection.setGroupCollection(appGroupCollection); // Set group to app collection

		// Setup email settings
		if(isSplashSupported){
			splashScreen.setProgress("Setting up email settings...", (int)Math.round(currProgress));
			currProgress += percentIncrease;
		}
		log.info("Setting up email settings...");
		MailManager mailManager = new MailManager(propertiesFile);
		boolean sendEmails = Boolean.parseBoolean(properties.getProperty("meerkat.email.send.emails"));
		boolean testEmailSending = Boolean.parseBoolean(properties.getProperty("meerkat.email.sending.test"));
		if(sendEmails && testEmailSending){
			mailManager.sendTestEmail();
		}

		// Create the RSS Feed
		if(isSplashSupported){
			splashScreen.setProgress("Setting up RSS...", (int)Math.round(currProgress));
			currProgress += percentIncrease;
		}
		log.info("Creating RSS service...");
		rssFeed = new RSS("Meerkat Monitor", "Meerkat Monitor RSS Alerts", "", new File(mgo.getTmpWorkingDir()).getAbsolutePath());
		rssFeed.refreshRSSFeed();
		webserverPort = Integer.parseInt(properties.getProperty("meerkat.webserver.port"));
		rssFeed.setServerPort(webserverPort);

		// Extract needed resources
		if(isSplashSupported){
			splashScreen.setProgress("Extracting resources...", (int)Math.round(currProgress));
			currProgress += percentIncrease;
		}
		log.info("Extracting resources...");
		mgo.extractWebResourcesResources();

		// Link DB to app
		webAppsCollection.setDB(ebd);

		// Setup web server
		if(isSplashSupported){
			splashScreen.setProgress("Setting up embedded server...", (int)Math.round(currProgress));
			currProgress += percentIncrease;
		}
		log.info("Setting up embedded server...");
		String wsdlEndpoint = "http://"+hostname+":"+(webserverPort+1)+webServiceRoot;
		String wsdlUrl = wsdlEndpoint+"?wsdl";
		httpWebServer = new HttpServer(webserverPort, version, wsdlUrl, tempWorkingDir);
		httpWebServer.setDataSources(webAppsCollection, appGroupCollection);
		// publish web services
		Endpoint.publish(wsdlEndpoint, new MeerkatWebService(mkm, webAppsCollection, httpWebServer));
		// set the httpServer to webapp collection
		webAppsCollection.setHttpServer(httpWebServer);

		// Open Dashboard in default browser if available
		if(isSplashSupported){
			splashScreen.setProgress("Finalizing...", (int)Math.round(currProgress));
			currProgress += percentIncrease;
		}
		try {
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
			java.net.URI uri = new java.net.URI("http://"+hostname+":"+webserverPort);
			desktop.browse( uri );
		}
		catch ( Exception e ) {
			log.info("[Console mode] Please open URL manually: http://"+hostname+":"+webserverPort+")");
		}

		// Start monitor
		log.info("Setting up monitor...");
		Monitor monitor = new Monitor(ebd, webAppsCollection, appGroupCollection, httpWebServer, rssFeed, propertiesFile);
		if(isSplashSupported){
			splashScreen.setProgress("Done!", (int)Math.round(currProgress));
			currProgress += percentIncrease;
			splashScreen.close();
		}
		monitor.startMonitor();

	}

}
