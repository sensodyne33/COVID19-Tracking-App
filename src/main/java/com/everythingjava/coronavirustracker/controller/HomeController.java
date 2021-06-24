package com.everythingjava.coronavirustracker.controller;

import com.everythingjava.coronavirustracker.CoronavirusData;
import com.everythingjava.coronavirustracker.models.LocationStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//controller is used to display data in the webpage
//controller returns data in an HTML UI
@Controller
public class HomeController {

    //allow us to call CoronovirusData class using Autowired and we can set variable using it
    @Autowired
    CoronavirusData coronavirusData;

    //getmapping help us return to home.html
    //this work because we have thymelead dependency
    @GetMapping("/")
    public String home(Model model) {

        //here we basically set an attribute and referred to it in the home.html
        //it will then print the attributevalue to the web which is TEST

        //taking a list of objects and converting them into strings and then mapping each
        //to an integer value and then we sum the strings into integer
        //this new variable totalReportedCases is the new variable from home.html
        List<LocationStats> allStats = coronavirusData.getAllStats();
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);
        return "home";
    }

}
