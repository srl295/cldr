<%@ include file="stcontext.jspf" %>
<%
	/* if(ctx.session.user != null) { */   // uncomment to restrict to logged in users
%>

<p class='hang'>Easy steps: 
<%
WebContext subCtx = new WebContext(ctx);
subCtx.setQuery(SurveyMain.QUERY_LOCALE,ctx.localeString());
%>
<%= subCtx.sm.getMenu(subCtx,subCtx.field(SurveyMain.QUERY_SECTION),	"r_steps","Step-By-Step") %>

</p>
<%
	/* } */   // uncomment to restrict to logged in users
%>
