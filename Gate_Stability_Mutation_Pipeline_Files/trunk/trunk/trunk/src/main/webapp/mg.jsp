<%@ page contentType="text/html; charset=iso-8859-1" language="java" %>
<%@ page import="mg.ExampleDocuments, java.util.Map, java.util.Map.Entry "
%>
<html>
<body bgcolor="#fbf9ba">

 <H1>Mutation Grounder Demo</H1>

<FORM ACTION="mg.jsp" METHOD="POST">
	<select name="select1" id="select1" width="300" style="width: 300px;font-face='Arial, Helvetica, sans-serif'" >  
	<option>Load text of example document from list</option>  
	<% 
	Map<String,String> documents = ExampleDocuments.getDocuments();
	for(Entry e : documents.entrySet()){   
		String key = (String)e.getKey();
		String value1 = (String)e.getValue();
		key = key.replaceAll("Authors"," AUTHORS");
		%> <option value="<%=value1%>"><%=key%></option><%
	}%>  
	</select>           
	<INPUT TYPE="SUBMIT" VALUE="Load">
</FORM>


<FORM ACTION="startMg.jsp" METHOD="POST">
	Please enter your text:
    <BR>
    <%
    String text = null;
    if(request.getParameter("select1") != null){
    	text  = request.getParameter("select1");
    }
    %>
    <TEXTAREA NAME="textarea1" ROWS="20" COLS="80"><%=text%></TEXTAREA>
    <BR>
    <INPUT TYPE="SUBMIT" VALUE="Submit">
</FORM>
</body>
</html>
  