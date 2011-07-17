package controllers;

import play.mvc.With;

/**
 * @author Roy Sindre Norangshol
 */
@Check("moderator")
@With(Secure.class)
@CRUD.For(models.BusStop.class)
public class BusStopAdmin extends CRUD {

}
