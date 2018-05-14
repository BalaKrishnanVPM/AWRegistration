package AirwatchRegistration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public class AccessExcel 
{
	WebElement element = null;
	String req = null;

	ReadingFiles read = new ReadingFiles();
	AirwatchRun run = new AirwatchRun();
	int checkcount =0;
	static int checkdrp = 0;
	
	
	public WebElement getElement(WebDriver driver,Map<String, String> objects,Logger logger) throws InterruptedException
	{
		String selectorType = objects.get("Selectortype");
		String selector = objects.get("Selector");
		String conditions = objects.get("Conditions");
		String keyboard = objects.get("Keyboard");
		String date = date();
		int time=0;
		if(objects.get("Time")!="")
		{
			time = Integer.parseInt(objects.get("Time"));
		}
		String wait = objects.get("Wait");
		try
		{
			if(selectorType.equals("xpath")&& conditions.isEmpty() && keyboard.isEmpty())
			{
				element = driver.findElement(By.xpath(selector));
			}
			else if(selectorType.equals("xpath")&& wait.contains("wait"))
			{
				read.eWait(driver, conditions, selector, time);
			}
			else if(selectorType.equals("xpath")&& keyboard.contains("k_enter"))
			{
				Thread.sleep(1000);
				driver.findElement(By.xpath(selector)).sendKeys(Keys.ENTER);
			}
			else if(selectorType.equals("xpath")&& keyboard.contains("k_tab"))
			{
				Thread.sleep(1000);
			}
			else if(selectorType.equals("id"))
			{
				element = driver.findElement(By.id(selector));
			}
			else if(selectorType.equals("Screenshot"))
			{
				captureScreenshot(driver,objects,date,logger);
			}
			else if(selectorType.equals("alert"))
			{
				boolean popupdlg = IsPopupDialogPresent(driver);
				logger.info("Popup dialog appearence is ==> "+popupdlg );
				if(popupdlg==true)
				{
					driver.findElement(By.xpath("//*[@id='welcome_modal']/div/div/div[1]/a")).click();
				}
			}
			else if(selectorType.equals(""))
			{
			}
			else
			{
				System.out.println("invalid selector");
			}
		}
		catch(Exception e)
		{
			logger.error("An Exception has occured in getElement method --> "+e.toString());
			read.createErrorFile(driver, logger);
			driver.quit();
			System.exit(0);
		}
		return element;
	}



	public void doAction(WebDriver driver,WebElement element,Map<String, String> objects,Logger logger) throws InterruptedException
	{
		Actions action1 = new Actions(driver);

		String action = objects.get("Action");
		String input = objects.get("Input");

		List<String> zpl = new ArrayList<String>();
		zpl = read.readingEnrollmentNumber(logger);
		String zplno = zpl.get(0);

		List<String> compareText = new ArrayList<String>();
		compareText.add(zplno);
		compareText.add("Registered");

		List<String> comparedrpdwn = new ArrayList<String>();
		comparedrpdwn.add("Country");
		comparedrpdwn.add("US");
		comparedrpdwn.add("Platform");
		comparedrpdwn.add("9000");
		comparedrpdwn.add("AssetNumber");
		comparedrpdwn.add(zplno);

		try
		{
			if(action.equals("click"))
			{
				try 
				{
					element.click();
					Thread.sleep(2000);
					infoLog(objects, logger);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			else if(action.equals("sendkeys"))
			{
				Thread.sleep(2000);
				element.sendKeys(input);
				infoLog(objects, logger);
			}
			else if(action.equals("sendkeysdrpdown"))
			{
				if(input.equals("ZPLNO"))
				{
					Thread.sleep(3000);
					element.sendKeys(zplno);
					infoLog(objects, logger);
					Thread.sleep(3000);
				}
				else
				{
					Thread.sleep(3000);
					element.sendKeys(input);
					infoLog(objects, logger);
				}
			}
			else if(action.equals("dropdownbyindex"))
			{
				Select sel = new Select(element);
				sel.selectByIndex(Integer.parseInt(input));
				infoLog(objects, logger);
			}
			else if(action.equals("sendkeyszpl"))
			{
				Thread.sleep(2000L);
				element.sendKeys(zplno);
				Thread.sleep(1000L);
				infoLog(objects, logger);
			}
			else if(action.equals("compare"))
			{
				WebDriverWait wait1 = new WebDriverWait(driver, 30);
				wait1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(".//*[@id='aw-console']/div[3]/div[2]")));
				boolean TxtBoxContent = IsTestElementPresent(driver);
				infoLog(objects, logger);
				if(TxtBoxContent==false)
				{
					read.createAWRecordTxt(0 ,logger);
					logger.info("ZPL number Already Exists !!!!!");
					logger.info("Terminating the script!!!!");
					driver.quit();
					System.exit(0);
				}
				else
				{
					logger.info("ZPL Number is Unused not Enrolled.");
				}
			}
			else if(action.equals("alert"))
			{
				element.click();
				infoLog(objects, logger);
			}
			else if(action.equals("action"))
			{
				Thread.sleep(2000);
				action1.moveToElement(element).build().perform();
				infoLog(objects, logger);
			}
			else if(action.equals("gettext"))
			{
				String txtboxvalue = element.getText().trim();
				infoLog(objects, logger);
				if(!txtboxvalue.contentEquals(compareText.get(checkcount).trim()))
				{
					logger.error("Zpl or Registered text is not matching in the console...");
					read.createErrorFile(driver, logger);
					driver.quit();
					System.exit(0);
				}
				if(checkcount == 1)
				{
					read.createAWRecordTxt(1 ,logger);
				}
				checkcount++;
			}
			else if(action.equals("drpdwnvalcheck"))
			{
				Select sel = new Select(element);
				WebElement option = sel.getFirstSelectedOption();
				String dropdown = option.getText();
				infoLog(objects, logger);

				if(!dropdown.contentEquals(comparedrpdwn.get(checkdrp)))
				{
					logger.info("Customattribute Dropdown value is not equal for either Country or Platform or Assetnumber ...");
					logger.info("Terminating the Program.");
					read.createErrorFile(driver, logger);
					driver.quit();
					System.exit(0);
				}
				checkdrp++;
			}
			else if(action.isEmpty())
			{
			}
			else
			{
				System.out.println("invalid action");
			}
		}
		catch(Exception e)
		{
			read.createErrorFile(driver, logger);
			logger.error("An Exception has occured in doAction method --> "+e.toString());
			driver.quit();
			System.exit(0);
		}
	}

	public boolean IsTestElementPresent(WebDriver driver)
	{
		try
		{
			driver.findElement(By.xpath(".//div[@class='grid_no_results'][text()='No Records Found']"));
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public boolean IsPopupDialogPresent(WebDriver driver)
	{
		try
		{
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.findElement(By.xpath("//*[@id='welcome_modal']/div/div/div[1]/a"));
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}


	public void infoLog(Map<String, String> objects,Logger logger)
	{
		String infoLog = objects.get("LoggerInfo");
		if(!infoLog.isEmpty())
		{
			logger.info(infoLog);
		}
	}


	public void captureScreenshot(WebDriver driver, Map<String, String> objects, String date,Logger logger)
	{
		
	}
	
	
	public static String date()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();
		String nms_date = dateFormat.format(now);
		return nms_date;
	}


	@Override
	public String toString() 
	{
		return "accessExcel []";
	}

}

