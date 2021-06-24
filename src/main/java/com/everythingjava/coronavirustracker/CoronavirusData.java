package com.everythingjava.coronavirustracker;

import com.everythingjava.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

//PURPOSE OF THIS CLASS: get the data from github and parse it

@Service
//@service tells spring to run our method fetchVirusData
public class CoronavirusData {

    //VARIABLES SECTION
    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    //tells spring after it constructs a instance of CoronovirusData Class, run this method
    @PostConstruct
    //tells spring to update this app every hour
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {

        //create a new list
        List<LocationStats> newStats = new ArrayList<>();

        //calls http link by creating a client
        HttpClient client = HttpClient.newHttpClient();

        //converts string to uri by creating a request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();

        //get response from client by sending the request
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(httpResponse.body());

        //copy header auto detection from commmon-cvs and helps us detect header
        StringReader cvsBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(cvsBodyReader);

        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();
            //loops through every state and prints them
            locationStat.setState(record.get("Province/State"));

            //loops through every country and prints them
            locationStat.setCountry(record.get("Country/Region"));

            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));

            //loops through every latest cases
            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDiffFromPrevDay(latestCases - prevDayCases);
            //use toString method to print it and show it in the log
            //System.out.println(locationStat);
            newStats.add(locationStat);
        }
        this.allStats = newStats;
    }
}
