<%@ page contentType="text/html; charset=UTF-8" %>
<html>
	<head>
		<title>Unicode | CLDR | About the Survey Tool</title>
		<link rel='stylesheet' type='text/css' href='./surveytool.css' />
	</head>
    
    <h2 class="selected">About the Survey Tool</h2>

        <p class="hang">For more information about the Survey Tool, see <a href="http://www.unicode.org/cldr">unicode.org/cldr</a>.</p>
        
    <h4 class="selected">Java Versions</h4>
        <table class="userlist" border="2">
            <%
                String props[] = {  "java.version", "java.vendor", "java.vm.version", "java.vm.vendor",
                    "java.vm.name", "os.name", "os.arch", "os.version" };
                for(int i=0;i<props.length;i++) {
                    out.println("<tr class='row"+(i%2)+"'><th><tt>"+props[i]+"</tt></th><td>"+java.lang.System.getProperty(props[i])+"</td></tr>\n");
                }
            %>
            
        </table>
        
    <h4 class="selected">Other Versions</h4>

    <% 
        { int i=0;
    %>
    
    <table class="userlist" border="2">
        <tr class="row<%= ((i++)%2) %>">
            <th>CLDRFile.GEN_VERSION</th>
            <td> <%= org.unicode.cldr.util.CLDRFile.GEN_VERSION %>  </td>
        </tr>
        <tr class="row<%= ((i++)%2) %>">
            <th>ICU</th>
            <td> <%= com.ibm.icu.util.VersionInfo.ICU_VERSION %>  </td>
        </tr>
        <tr class="row<%= ((i++)%2) %>">
            <th>Server</th>
            <td> <%= application.getServerInfo() %>  </td>
        </tr>
        <tr class="row<%= ((i++)%2) %>">
            <th>Java Servlet API</th>
            <td> <%= application.getMajorVersion() %>.<%= application.getMinorVersion() %>  </td>
        </tr>
        <tr class="row<%= ((i++)%2) %>">
            <th>Database</th>
            <td><%= org.apache.derby.tools.sysinfo.getProductName() %> <%= org.apache.derby.tools.sysinfo.getVersionString() %> <br> build <%= org.apache.derby.tools.sysinfo.getBuildNumber() %>  </td>
        </tr>
        
    </table>

    <% 
        }
    %>
    
	<body>
		<hr/>
		<a href="./survey">Return to Survey Tool</a> |
		<a href="./index.jsp">Return to CLDR Applications</a> |
		<a target="_new" href="http://dev.icu-project.org/cgi-bin/cldrwiki.pl?SurveyToolHelp/About">Help on this page (in a new window)</a>
	</body>
</html>
