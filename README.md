# reader



=======================
Running the application
=======================
 - Nothing out of the ordinary, I used tomcat to run it on my local. I used maven project management.
 
 - in the index jsp page enter the requested parameters in the input areas they way the label specifies (this is for the monthly average closing and opening prices data)
 
 - there are two more links for Max-profit-Day and Biggest-Loser-security that you can click on and see the Json response. Since these two didn't require date range, 
	I have used the full data, therefore, no parameters needed.
	
	
=======================
About the application
=======================
 - It is Jersey REST api application.
 
 - Index.jsp is minimally developed jsp page just to send Http GET requests to the REST api's
 
 - response is displayed in JSON format.
