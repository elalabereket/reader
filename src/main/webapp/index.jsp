<html>
<body>
    <h2>Monthly Average Opening and Closing Prices: </h2>
	    <form action="webapi/read/monthlyAverage" method="get">
	    	<label>Enter Security symbols separated by '-'</label> : <input type="text" name="security" /><br/><br/>
	    	<label>Start Year(YYYY)</label> : <input type="number" name="startYear" maxlength="4"/><br/><br/>
	    	<label>Start Month(MM)</label> : <input type="number" name="startMonth" maxlength="2"/><br/><br/>
	    	<label>End Year(YYYY)</label> : <input type="number" name="endYear" maxlength="4"/><br/><br/>
	    	<label>End Month(MM)</label> : <input type="number" name="endMonth" maxlength="2"/><br/><br/>
	    	<input type="submit" name="Submit" /><br/><br/>
	    </form>
    <h2>Maximum Daily Profit: </h2>
    <p>
    	<a href="webapi/read/dailyMaxProfit">Get max profit day</a>
   	</p>
   	<h2>Biggest loser: </h2>
   	<p>
    	<a href="webapi/read/biggestLoser">Get the biggest loser security</a>
   	</p>
</body>
</html>
