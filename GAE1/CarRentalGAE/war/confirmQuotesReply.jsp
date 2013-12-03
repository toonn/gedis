<%@page import="ds.gae.view.JSPSite"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
	String renter = (String)session.getAttribute("renter");
	JSPSite currentSite = JSPSite.CONFIRM_QUOTES_RESPONSE;
	String token = (String) session.getAttribute("token");

%>   
 
<%@include file="_header.jsp" %>

<% 
if (currentSite != JSPSite.LOGIN && currentSite != JSPSite.PERSIST_TEST && renter == null) {
 %>
	<meta http-equiv="refresh" content="0;URL='/login.jsp'">
<% 
  request.getSession().setAttribute("lastSiteCall", currentSite);
} 
 %>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="style.css" />
	<title>Car Rental Application</title>
</head>
<body>
	<script src='/_ah/channel/jsapi'></script>
	<script>
    	var quotes = "";
    	
    	onOpened = function() {
    		document.getElementById("response").innerHTML="Your reservations are currently being processed, results will be shown in a moment.";
		}

		onMessage = function(msg) {
			data = JSON.parse(m.data);
			document.getElementById("response").innerHTML=data;
			/*quotes += msg;
			document.getElementById("response").innerHTML=quotes;*/
		}

		onError = function(err) {
   		 	alert(err);
		}

		onClose = function() {
			alert('channel closed');
		}
		
  	  	/*channel = new goog.appengine.Channel('<%= token %>');
   	 	socket = channel.open();
   	 	socket.onopen = onOpened;
    	socket.onmessage = onMessage;
    	socket.onerror = onError;
    	socket.onclose = onClose;*/
    	
    	
    	openChannel = function() {
			var token = '<%= token %>';
			var channel = new goog.appengine.Channel(token);
			var handler = {
				'onopen': onOpened,
				'onmessage': onMessage,
				'onerror': function() {},
				'onclose': function() {}
			};
			var socket = channel.open(handler);
			socket.onopen = onOpened;
			socket.onmessage = onMessage;
		}

		initialize = function() {
			openChannel();
		}

		initialize();
  	</script>

	<div id="mainWrapper">
		<div id="headerWrapper">
			<h1>Car Rental Application</h1>
		</div>
		<div id="navigationWrapper">
			<ul>
<% 
for (JSPSite site : JSPSite.publiclyLinkedValues()) {
	if (site == currentSite) {
 %> 
				<li><a class="selected" href="<%=site.url()%>"><%=site.label()%></a></li>
<% } else {
 %> 
				<li><a href="<%=site.url()%>"><%=site.label()%></a></li>
<% }}
 %> 

				</ul>
		</div>
		<div id="contentWrapper">
<% if (currentSite != JSPSite.LOGIN) { %>
			<div id="userProfile">
				<span>Logged-in as <%= renter %> (<a href="/login.jsp">change</a>)</span>
			</div>
<%
   }
 %>
			<div class="frameDiv" style="margin: 150px 150px;">
				<div class="groupLabel">Reply</div>
				<div class="group">
					<p id="response">
					Something's wrong..
					</p>
				</div>
			</div>

<%@include file="_footer.jsp" %>

