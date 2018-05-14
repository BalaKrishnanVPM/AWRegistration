package AirwatchRegistration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ReadingFiles 
{
	private static final String file_paths = new File(System.getProperty("user.dir")).getParent() +"\\req\\Enrollment_number.txt";
	private static final String AWfile = new File(System.getProperty("user.dir")).getParent() +"\\req\\AirwatchRecord.txt";
	private static final String error_file = new File(System.getProperty("user.dir")).getParent() +"\\Exe\\AWEnrollError.txt";
	
	public static String fInput = null;
	public static List<String> inputs;
	public static WebDriverWait wait;
	
	static int writeCount =0;
	WebElement element;
	
	String output;
	public List<String> readingEnrollmentNumber(Logger logger) 
	{
		inputs = new ArrayList<String>();
		String zplpath = null;
		try 
		{
			try 
			{
				zplpath = FileUtils.readFileToString(new File(file_paths), "UTF-8");
			} 
			catch (IOException e1) 
			{
				logger.error("An Exception has occured while reading the contents from Enrollment_number.txt file --> \n"+e1.getMessage());
				System.exit(0);
			}
			String input_data[] = zplpath.split("\n");
			
			inputs.add(input_data[0]);//zpl number		 
		}
		catch (Exception e) 
		{
			logger.error("An Exception has occured --> "+e.toString());
			System.exit(0);
		}
		return inputs;
	}
	
	
	public String createAWRecordTxt(int writeCount, Logger logger) 
	{
		try
		{
			File file = new File(AWfile);
			FileWriter writer = new FileWriter(file);
			file.createNewFile();
			if(writeCount == 0)
			{
				writer.write("Exist");
				output = "Exist";
			}
			else
			{
				writer.write("Registered");
				output = "Registered";
			}
			writer.flush();
			writer.close();
		}
		catch(Exception e)
		{
			logger.error("An Exception has occured while creating and writing the data in AirwatchRecord.txt text file --> \n"+e.toString());
			System.exit(0);
		}
		return output;
	}
	
	
	
	public void createErrorFile(WebDriver driver,Logger logger) 
	{
		try
		{
			File file = new File(error_file);
			FileWriter writer = new FileWriter(file);
			file.createNewFile();

			writer.write("Queued Product was not exists");
			writer.flush();
			writer.close();
		}
		catch(Exception e)
		{
			logger.error("An Exception has occured while creating Error.txt text file --> \n"+e.toString());
			driver.quit();
			System.exit(0);
		}
	}
	

	public void eWait(WebDriver driver,String conditions, String selector, int time) 
	{
		wait = new WebDriverWait(driver, time);
		
		if(conditions.equals("vElement"))
		{
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selector)));
		}
		else if(conditions.equals("ivElement"))
		{
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(selector)));
		}
		else
		{
			System.out.println("Invalid wait condition");
		}
	}
	
}
