<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<form:form commandName="busbuddyapikey">
<table>
        <tr>
                <td>app name:</td>
                <td><form:input path="getAppName" /></td>
        </tr>
        <tr>
                <td colspan="2"><input type="submit" value="Save Changes" /></td>
        </tr>
</table>
</form:form>