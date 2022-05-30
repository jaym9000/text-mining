<%@ page contentType="text/html; charset=iso-8859-1" language="java" %>
<%@ page import="java.util.List, java.util.ArrayList, java.io.File,
	ca.unbsj.cbakerlab.mutation.impact.DocumentInformation,
	ca.unbsj.cbakerlab.mutation.impact.Utils,
	ca.unbsj.cbakerlab.mutation.impact.Pipeline,
	gate.util.GateException,
	gate.Document,
	gate.Factory
	" %>

<%
String inputText=request.getParameter("textarea1");
Main
Utils.initGate();
// Load ANNIE plugin.
File pluginsHome = gate.Gate.getPluginsHome();
gate.Gate.getCreoleRegister().registerDirectories(new File(pluginsHome, "ANNIE").toURI().toURL());

Pipeline pipeline = new Pipeline();
pipeline.init(true,true,true,true,true,true,true,true,true,false);
 
gate.Document doc = gate.Factory.newDocument(inputText);   
gate.Corpus gateCorpus = gate.Factory.newCorpus("corpus"+doc.getName());    			
gateCorpus.add(doc);


pipeline.setCorpus(gateCorpus);    		
pipeline.execute();
	
/*/ DEBUG Intermediate Output in XML.
BufferedWriter outt = new BufferedWriter(new FileWriter(fileName+".xml"));
outt.write(doc.toXml());
outt.close();
*/

DocumentInformation docInfo = new DocumentInformation(doc);
//corpus.getDocInfos().put(docInfo.getName().split("\\.")[0], docInfo);   

String[][] result = docInfo.resultsAsTable();


List<String[]> list = new ArrayList<String[]>();
for (int x = 0 ; x <result.length ; x++){
	list.add(result[x]);
}

%>
<b>Results:</b>
<TABLE BORDER="1">
        <TR>
            <TH>Mutation</TH>
            <TH>UniProtID</TH>
        </TR>
<%
for(String[] array : list){
	%>
    
        <TR>
        	<%
        	for(String s : array){
        		%>
        		<TD> <%= s %> </TD>
        		<%
        	}
        	%>           

        </TR>

<% 
}
%>
    </TABLE>
<html>
<body bgcolor="#fbf9ba">
<br>

<br>
<p><b>The input text:</b></p><br><%=inputText%>
</body>
</html>
