<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Welcome to caArray 2</title>
</head>
<body>
<h1>Welcome to caArray 2</h1>
<f:view>
<h:messages/>
<f:verbatim>
<form method="POST" action="j_security_check">

  Username: <input type="text"     name="j_username"><br />
  Password: <input type="password" name="j_password"><br />
  <br />

  <input type="submit" value="Login">
  <input type="reset"  value="Reset">

</form>
</f:verbatim>
</f:view>
</body>
</html>