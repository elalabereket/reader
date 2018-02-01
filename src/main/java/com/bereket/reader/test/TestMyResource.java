package com.bereket.reader.test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.json.*;
import org.junit.Before;
import org.junit.Test;

import com.bereket.reader.controller.MyResource;
import com.bereket.reader.model.LoseDataInfo;
import com.bereket.reader.model.MonthAverageData;
import com.bereket.reader.model.PriceDataInfo;

public class TestMyResource {
	
	private MyResource resource;
	
	private JSONObject testData;
	
	private JSONObject dataTable;

	private JSONArray data = new JSONArray("[[\"A\",\"1999-01-02\",1.0,2.0,3.0,4.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"A\",\"1999-01-03\",1.0,10.0,2.0,4.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"A\",\"1999-01-04\",1.0,7.0,3.0,4.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"A\",\"1999-02-02\",2.0,7.0,3.0,6.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"A\",\"1999-02-03\",2.0,7.0,3.0,6.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"A\",\"1999-02-04\",2.0,7.0,3.0,6.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"A\",\"1999-03-01\",2.0,7.0,3.0,6.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"A\",\"1999-03-02\",2.0,5.0,3.0,1.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"BB\",\"1999-01-01\",1.0,5.0,3.0,4.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"BB\",\"1999-01-02\",1.0,2.0,3.0,4.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"BB\",\"1999-01-03\",1.0,2.0,3.0,4.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"BB\",\"1999-02-01\",2.0,2.0,3.0,6.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"BB\",\"1999-02-01\",2.0,7.0,3.0,6.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"BB\",\"1999-02-01\",2.0,4.0,3.0,0.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"BB\",\"1999-03-01\",2.0,3.0,3.0,6.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0],"
			+ "[\"BB\",\"1999-03-02\",2.0,4.0,3.0,1.0,5.0,1.0,10.0,2.0,4.0,5.0,4.0,5.0]]");
	
	@Before
	public void setUp() {
		testData = new JSONObject();
		dataTable =  new JSONObject();
		
		dataTable.put("data", data);
		testData.put("datatable", dataTable);
		
		resource = new MyResource();
	}
	
	@Test
	public void test_getMonthlyAverageOpenClosePrice() {
		
		Map<String, List<MonthAverageData>> response = resource.getMonthlyAverageOpenClosePrice(testData, "A-BB", 1999, 1999, 01, 02);

		assertTrue(2 == response.get("A").size());
		assertTrue(2 == response.get("BB").size());
		assertTrue(1.0 == response.get("A").get(0).getAverageOpenPrice());
		assertTrue(6.0 == response.get("BB").get(1).getAverageClosePrice());
	}
	
	@Test
	public void test_getDailyMaxProfit() {
		PriceDataInfo maxProfitPriceData = resource.getDailyMaxProfit(testData);
		
		assertTrue("A".equalsIgnoreCase(maxProfitPriceData.getSecurity()));
		assertTrue("1999-01-03".equalsIgnoreCase(maxProfitPriceData.getDate()));
		assertTrue(8.0 == maxProfitPriceData.getProfit());
	}
	
	@Test
	public void test_findBiggestLoser() {
		LoseDataInfo loseDataInfo = resource.findBiggestLoser(testData);
		
		assertTrue("BB".equalsIgnoreCase(loseDataInfo.getSecurity()));
		assertTrue(2 == loseDataInfo.getNumberOfLoseDays());
	}

}
