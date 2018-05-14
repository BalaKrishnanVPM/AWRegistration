package AirwatchRegistration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelRead 
{
	public ArrayList<Map<String,String>> datalist = new ArrayList<Map<String,String>>();
	public ArrayList<Map<String,String>> linklist = new ArrayList<Map<String,String>>();
	public ArrayList<Map<String,String>> dropdownlist = new ArrayList<Map<String,String>>();
	
	int drpcheck = 1;
	int drpchecklimit = 0;
	
	String linksheet = new File(System.getProperty("user.dir")).getParent() + "\\ExcelData\\AWEnroll.xls";
	String enrolldatasheet = new File(System.getProperty("user.dir")).getParent() + "\\ExcelData\\AWEnroll.xls";
	
	public ArrayList<Map<String,String>> linkDataSheet() throws BiffException
	{
		Map<String,String> linkmap ;
		try 
		{
			FileInputStream fs = new FileInputStream(linksheet);
			Workbook wb = Workbook.getWorkbook(fs);
			Sheet sh = wb.getSheet(0);
			
			for(int row=1;row<sh.getRows();row++)
			{
				linkmap = new HashMap<String, String>();
				for(int column=0;column<sh.getColumns();column++)
				{
					linkmap.put(sh.getCell(column, 0).getContents(), sh.getCell(column, row).getContents());
				}
				linklist.add(linkmap);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return linklist;
	}
	
	
	public ArrayList<Map<String,String>> readDataSheet() throws BiffException
	{
		Map<String,String> datamap;
		try 
		{
			FileInputStream fs = new FileInputStream(enrolldatasheet);
			Workbook wb = Workbook.getWorkbook(fs);
			Sheet sh = wb.getSheet(1);
			
			for(int row=1;row<sh.getRows();row++)
			{
				datamap = new HashMap<String, String>();
				for(int column=0;column<sh.getColumns();column++)
				{
					datamap.put(sh.getCell(column, 0).getContents(), sh.getCell(column, row).getContents());
					
				}
				datalist.add(datamap);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return datalist;
	}
	

	@Override
	public String toString() 
	{
		return "Excelread[]";
	}
	
}