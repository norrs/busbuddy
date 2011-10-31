package no.norrs.busbuddy.webfrontend.controller;

import no.norrs.busbuddy.api.dao.ApplicationTypeDAO;
import no.norrs.busbuddy.api.model.BusBuddyApiKey;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.logging.Logger;

/**
 * @author Roy Sindre Norangshol
 */
@Controller
@RequestMapping("/person.form")
public class BusBuddyApiKeyController {

    private ApplicationTypeDAO applicationTypeDAO;

    public BusBuddyApiKeyController(ApplicationTypeDAO applicationTypeDAO) {
        this.applicationTypeDAO = applicationTypeDAO;
    }

    Logger logger = Logger.getAnonymousLogger();

    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(ModelMap model) {

        model.addAttribute("busbuddyapikeyDAO", new BusBuddyApiKey());
//        model.addAttribute("applicationTypes", applicationTypeDAO.findAll());

        return "details.jsp";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processForm(@ModelAttribute("busbuddyapikeyDAO") BusBuddyApiKey busBuddyApiKey) {
        logger.info(busBuddyApiKey.toString());
        return "success.jsp";
    }
}

