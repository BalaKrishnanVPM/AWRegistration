package AirwatchRegistration;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import AirwatchRegistration.AccessExcel;
import AirwatchRegistration.ExcelRead;
import jxl.read.biff.BiffException;


public class AirwatchRun 
{
	private static final String req_files = new File(System.getProperty("user.dir")).getParent() + "\\req\\";
	
	static ReadingFiles read = new ReadingFiles();
	static WebDriver driver;
	static Properties property = new Properties();

	public static void main(String[] args) 
	{
		Logger logger=Logger.getLogger("AirwatchRun");
		PropertyConfigurator.configure(req_files+"log4j.properties");
		
		try 
		{
			runTest(driver, logger);
		} 
		catch (Exception e) 
		{
			read.createErrorFile(driver, logger);
			driver.quit();
			System.out.println("Exception occured in runTest Method--> "+e.toString());
			e.printStackTrace();
		}

	}

	public static void runTest(WebDriver driver,Logger logger) throws InterruptedException, BiffException 
	{
		ExcelRead elementAccess = new ExcelRead();
		AccessExcel accessexcel = new AccessExcel();
		ReadingFiles read = new ReadingFiles();
		read.readingEnrollmentNumber(logger);
		
		WebElement element;
		Map<String, String> objects;

		elementAccess.readDataSheet();
		elementAccess.linkDataSheet();
		try
		{
			for (Map<String, String> data : elementAccess.linklist) 
			{
				driver = launchBrowser(logger);
				driver.get(data.get("URL"));
			
				for (int size = 0; size < elementAccess.datalist.size(); size++) 
				{
					objects = elementAccess.datalist.get(size);
					element = accessexcel.getElement(driver, objects, logger);
					accessexcel.doAction( driver,element, objects, logger);
				}
			}
			driver = exitBrowser(logger);
		}
		catch(Exception e)
		{
			read.createErrorFile(driver, logger);
			logger.error("An Exception has occured in runTest method --> "+e.toString());
			driver.quit();
			System.exit(0);
		}
	}
	

	public static WebDriver launchBrowser(Logger logger) 
	{
		try
		{
			System.setProperty("webdriver.chrome.driver", req_files+"chromedriver.exe");
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--start-maximized");
			driver = new ChromeDriver(chromeOptions);
		}
		catch(Exception e)
		{
			read.createErrorFile(driver, logger);
			logger.error("An Exception has occured in launchBrowser method --> "+e.toString());
			driver.quit();
			System.exit(0);
		}
		return driver;
	}
	
	public static WebDriver exitBrowser(Logger logger)
	{
		logger.info("Successfully Completed the Registration Process!!!");
		driver.quit();
		System.exit(0);
		return driver;
	}
}