package com.bereket.reader.controller;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.*;

import com.bereket.reader.model.LoseDataInfo;
import com.bereket.reader.model.MonthAverageData;
import com.bereket.reader.model.PriceDataInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("read")
public class MyResource {

	@GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/monthlyAverage")
    public Map<String, List<MonthAverageData>> getMonthlyAverage2(@QueryParam("security") String security, @QueryParam("startYear") int startYear, 
    		@QueryParam("endYear") int endYear, @QueryParam("startMonth") int startMonth, @QueryParam("endMonth") int endMonth) throws IOException {
    	
    	JSONObject pricingData = fetchPricingData();
    	
    	return getMonthlyAverageOpenClosePrice(pricingData, security, startYear, endYear, startMonth, endMonth);
    	
    }
	
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/dailyMaxProfit")
    public PriceDataInfo getDailyMaxProfit() throws IOException {
    	JSONObject pricingData = fetchPricingData();
    	
    	return getDailyMaxProfit(pricingData);
    	
    }
    
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/biggestLoser")
    public LoseDataInfo getBiggestLoser() throws IOException {
    	JSONObject pricingData = fetchPricingData();
    	
    	return findBiggestLoser(pricingData);
    	
    }
    
    private JSONObject fetchPricingData() throws IOException {
    	String apiURL = "https://www.quandl.com/api/v3/datatables/WIKI/PRICES.json?api_key=s-GMZ_xkw6CrkGYUWs1p";
    	URL url = new URL(apiURL);
    	
    	HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
    	con.setRequestMethod("GET");
    	BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
    	String inLine;
    	StringBuffer resp = new StringBuffer();
    	while((inLine = br.readLine()) != null) {
    		resp.append(inLine);
    	}
    	br.close();
    	JSONObject pricingData = new JSONObject(resp.toString());
    	
    	return pricingData;
    }
    
	public PriceDataInfo getDailyMaxProfit(JSONObject jsonData) {
		JSONObject datatable = jsonData.getJSONObject("datatable");
    	JSONArray data = datatable.getJSONArray("data");
    	
    	JSONArray maxProfitArr = data.getJSONArray(0);
    	double maxProfit = maxProfitArr.getDouble(3) - maxProfitArr.getDouble(4);
    	
    	for(int i = 1; i < data.length(); i++) {
    		JSONArray arr = data.getJSONArray(i);
    		double profit = arr.getDouble(3) - arr.getDouble(4);
    		if(profit > maxProfit) {
    			maxProfitArr = arr;
    			maxProfit = profit;
    		}
    	}
    	
    	PriceDataInfo maxProfPriceData = new PriceDataInfo();
    	maxProfPriceData.setSecurity(maxProfitArr.getString(0));
    	maxProfPriceData.setDate(maxProfitArr.getString(1));
    	maxProfPriceData.setProfit(Math.round(maxProfit*100)/100d);
    	
		return maxProfPriceData;
	}
	
	public Map<String, List<MonthAverageData>> getMonthlyAverageOpenClosePrice(JSONObject fullData, String securities, int styr, int endyr, int stMon, int endMonth) {
		Map<String, Map<Integer, Map<Integer, List<double[]>>>> data = filterData(fullData, securities, stMon, endMonth, styr, endyr);
		Map<String, List<MonthAverageData>> resp = new HashMap<>();
		for(String sec: data.keySet()) {
			List<MonthAverageData> monthData = new ArrayList<>();
			Map<Integer, Map<Integer, List<double[]>>> yrMap = data.get(sec);
			if(yrMap != null && !yrMap.isEmpty()) {
				for(Integer yr: yrMap.keySet()) {
					Map<Integer, List<double[]>> monthMap = yrMap.get(yr);
					if(monthMap != null && !monthMap.isEmpty()) {
						for(Integer mon: monthMap.keySet()) {
							List<double[]> dayList = monthMap.get(mon);
							if(dayList != null && !dayList.isEmpty()) {
								monthData.add(getPriceDataInfo(yr, mon, dayList));
							}
						}
					}
				}
			}
			resp.put(sec, monthData);
		}
		return resp;
	}
	
	private Map<String, Map<Integer, Map<Integer, List<double[]>>>> filterData(JSONObject fullData, String securities, int stMon, int endMonth, int styr, int endyr) {
		JSONObject datatable = fullData.getJSONObject("datatable");
		JSONArray data = datatable.getJSONArray("data");

		List<String> secs = new ArrayList<>(Arrays.asList(securities.split("-")));
		Map<String, Map<Integer, Map<Integer, List<double[]>>>> filteredData = new HashMap<>();
		for(int i = 0; i < data.length(); i++) {
			JSONArray dayInfo = data.getJSONArray(i);
			if(secs.contains(dayInfo.getString(0))) {
				String sec = dayInfo.getString(0);
				Integer yr = Integer.valueOf(dayInfo.getString(1).split("-")[0]);
				Map<Integer, Map<Integer, List<double[]>>> temp = filteredData.get(sec);
				if(temp == null && yr >= styr && yr <= endyr) {
					temp = new HashMap<Integer, Map<Integer, List<double[]>>>();
					filteredData.put(sec, temp);
				}
				if(yr >= styr && yr <= endyr) {
					Map<Integer, List<double[]>> tempyr = temp.get(yr);
					if(tempyr == null) {
						tempyr = new HashMap<Integer, List<double[]>>();
						temp.put(yr, tempyr);
					}
					Integer mn = Integer.valueOf(dayInfo.getString(1).split("-")[1]);
					boolean isMonthSameYearValid = styr == endyr && mn >= stMon && mn <= endMonth; 
					boolean isMonthDiffYearValid = styr != endyr && ((yr == styr && mn >= stMon) || (yr == endyr && mn <= endMonth));
					if(isMonthSameYearValid || isMonthDiffYearValid) {
						List<double[]> tempmn = tempyr.get(mn);
						if(tempmn == null) {
							tempmn = new ArrayList<double[]>();
							tempyr.put(mn, tempmn);
						}
						double[] dayData = {dayInfo.getDouble(2), dayInfo.getDouble(3), dayInfo.getDouble(4), dayInfo.getDouble(5), dayInfo.getDouble(6), dayInfo.getDouble(7), dayInfo.getDouble(8), dayInfo.getDouble(9), dayInfo.getDouble(10), dayInfo.getDouble(11), dayInfo.getDouble(12), dayInfo.getDouble(13)};
						tempmn.add(dayData);
					}
				}
			}
		}

		return filteredData;
	}

	private MonthAverageData getPriceDataInfo(Integer yr, Integer mon, List<double[]> dayList) {
		double openPriceSum = 0d;
		double closePriceSum = 0d;
		int numDays = dayList.size();

		for(double[] arr: dayList) {
			openPriceSum += arr[0];
			closePriceSum += arr[3];
		}
		double openAv = openPriceSum / numDays;
		double closeAv = closePriceSum / numDays;

		MonthAverageData dataInfo = new MonthAverageData();
		dataInfo.setMonth(yr + "-" + mon);
		dataInfo.setAverageOpenPrice(Math.round(openAv * 100) / 100d);
		dataInfo.setAverageClosePrice(Math.round(closeAv * 100) / 100d);

		return dataInfo;
	}
	
	public LoseDataInfo findBiggestLoser(JSONObject pricingData) {
		JSONObject datatable = pricingData.getJSONObject("datatable");
		JSONArray data = datatable.getJSONArray("data");
		
		String biggestLoserSec = "";
		int loseDays = 0;
		Map<String, Integer> securityLoseDays = new HashMap<>();
		for(int i = 0; i < data.length(); i++) {
			JSONArray dayInfo = data.getJSONArray(i);
			String sec = dayInfo.getString(0);
			if(dayInfo.getDouble(5) < dayInfo.getDouble(2)) {
				Integer days = securityLoseDays.get(sec);
				if(days == null) {
					days = 0;
				}
				days++;
				securityLoseDays.put(sec, days);
			}
		}
		
		for(String sec : securityLoseDays.keySet()) {
			if(securityLoseDays.get(sec) > loseDays) {
				biggestLoserSec = sec;
				loseDays = securityLoseDays.get(sec);
			}
		}
		
		LoseDataInfo info = new LoseDataInfo();
		info.setSecurity(biggestLoserSec);
		info.setNumberOfLoseDays(loseDays);
		
		return info;
	}

	
    
}
