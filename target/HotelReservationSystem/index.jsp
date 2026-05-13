<%@ page contentType="text/html;charset=UTF-8" %>
<%
  // Redirect root index.jsp to /dashboard
  response.sendRedirect(request.getContextPath() + "/dashboard");
%>