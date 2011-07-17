package controllers;

/**
 * @author Roy Sindre Norangshol
 */
@Check("admin")
@With(Secure.class)
@CRUD.For(models.BusStop.class)
public class BusStopAdmin  extends CRUD {

}
