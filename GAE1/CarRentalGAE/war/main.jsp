<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="ds.gae.view.JSPSite"%>
<%@page import="ds.gae.view.ViewTools"%>
<%@page import="ds.gae.CarRentalModel"%>
<%@page import="ds.gae.entities.Quote"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
	JSPSite currentSite = JSPSite.CREATE_QUOTES;
	String renter = (String)session.getAttribute("renter");
	HashMap<String, List<Quote>> quotes = (HashMap<String, List<Quote>>)session.getAttribute("quotes"); 
	boolean anyQuotes = false;
%>   
 
<%@include file="_header.jsp" %>

		<div class="frameDiv">
<% 
for (String crc : CarRentalModel.get().getAllRentalCompanyNames()) {
 %> <!-- begin of CRC loop -->
			<h2>Car Rental Company: <%= crc %></h2>
			
			<div class="groupLabel">Create a Quote</div>
			<form method="POST" action="/createQuote">
			<div class="group">
				<div class="form">
					<span>From: <input type="text" name="startDate" value="01.01.2012" size="10"> (dd.mm.yyyy)</span>
					<span>To: <input type="text" name="endDate" value="01.11.2012" size="10"> (dd.mm.yyyy)</span>
				</div>
				<div class="form">
					<span>
					CarType: 
					<select name="carType"> 
	<% for (String carTypeName : CarRentalModel.get().getCarTypesNames(crc) ) { 
	 %>
						<option><%= carTypeName %></option>
	<% } 
	 %> 
					</select>
					</span>
				</div>
				<div class="formsubmit">
					<input type="hidden" name="crc" value="<%= crc %>"/>  
					<input type="submit" value="Create New Quote" />
				</div>
				</form>
			</div>
	<% if ( quotes != null && quotes.containsKey(crc) && quotes.get(crc).size() > 0) {
		List<Quote> quotesForCrc = quotes.get(crc);
		anyQuotes = true;
	 %>
			<div class="groupLabel">Current Quotes</div>
			<div class="group">
				<table>
					<tr>
						<th>Car Type</th>
						<th>Start Date</th>
						<th>End Date</th>
						<th>Rental Price</th>			
					</tr>
						
		<%
			for (Quote q : quotesForCrc) { 
		 %>
					<tr>
						<td><%= q.getCarType()%></td>
						<td><%= ViewTools.DATE_FORMAT.format(q.getStartDate())%></td>
						<td><%= ViewTools.DATE_FORMAT.format(q.getEndDate())%></td>
						<td class="numbers"><%= q.getRentalPrice()%> €</td>
					</tr>
		<%
			} 
		 %>
				</table>
			</div>


	<%} %>

<%} 
 %> <!-- end of CRC loop -->

 <% if ( anyQuotes ) {
 %>
			<div class="formsubmit">
				<form method="POST" action="/confirmQuotes">
					<input id="confirmSubmitButton" type="submit" value=" >> Confirm all Quotes << " />
				</form>
			</div>
 <%} 
 %> 
		</div>

 
<%@include file="_footer.jsp" %>
