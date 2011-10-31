<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<form:form commandName="busbuddyapikeyDAO">
<table>
        <tr>
                <td>app name:</td>
                <td><form:input path="appName" /></td>

        </tr>
        <tr>
            <td>apiKey: </td>
            <td><form:input path="apiKey" /></td>
        </tr>
        <tr>
            <td>applicationType</td>


        </tr>
        <tr>
                <td colspan="2"><input type="submit" value="Save Changes" /></td>
        </tr>
</table>
</form:form>