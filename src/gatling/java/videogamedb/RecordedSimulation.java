package videogamedb;


import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;


import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;


public class RecordedSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://videogamedb.uk")
            .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
            .acceptHeader("application/json")
            .acceptEncodingHeader("gzip, deflate, br")
            .acceptLanguageHeader("en-US,en;q=0.9,ru-RU;q=0.8,ru;q=0.7,tr;q=0.6")
            .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");

    private final Map<CharSequence, String> headers_0 = Map.ofEntries(
            Map.entry("Sec-Fetch-Dest", "empty"),
            Map.entry("Sec-Fetch-Mode", "cors"),
            Map.entry("Sec-Fetch-Site", "same-origin"),
            Map.entry("sec-ch-ua", "Google Chrome\";v=\"117\", \"Not;A=Brand\";v=\"8\", \"Chromium\";v=\"117"),
            Map.entry("sec-ch-ua-mobile", "?0"),
            Map.entry("sec-ch-ua-platform", "Windows")
    );

    private final Map<CharSequence, String> headers_2 = Map.ofEntries(
            Map.entry("Content-Type", "application/json"),
            Map.entry("Origin", "https://videogamedb.uk"),
            Map.entry("Sec-Fetch-Dest", "empty"),
            Map.entry("Sec-Fetch-Mode", "cors"),
            Map.entry("Sec-Fetch-Site", "same-origin"),
            Map.entry("sec-ch-ua", "Google Chrome\";v=\"117\", \"Not;A=Brand\";v=\"8\", \"Chromium\";v=\"117"),
            Map.entry("sec-ch-ua-mobile", "?0"),
            Map.entry("sec-ch-ua-platform", "Windows")
    );

    private final Map<CharSequence, String> headers_3 = Map.ofEntries(
            Map.entry("Content-Type", "application/json"),
            Map.entry("Origin", "https://videogamedb.uk"),
            Map.entry("Sec-Fetch-Dest", "empty"),
            Map.entry("Sec-Fetch-Mode", "cors"),
            Map.entry("Sec-Fetch-Site", "same-origin"),
            Map.entry("authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY5Njg4NjA1MSwiZXhwIjoxNjk2ODg5NjUxfQ.sgcLDcAyz_v5ciNYwgNrP5H_fEM6wrZHQlDvzb2wLbA"),
            Map.entry("sec-ch-ua", "Google Chrome\";v=\"117\", \"Not;A=Brand\";v=\"8\", \"Chromium\";v=\"117"),
            Map.entry("sec-ch-ua-mobile", "?0"),
            Map.entry("sec-ch-ua-platform", "Windows")
    );

    private final Map<CharSequence, String> headers_5 = Map.ofEntries(
            Map.entry("Origin", "https://videogamedb.uk"),
            Map.entry("Sec-Fetch-Dest", "empty"),
            Map.entry("Sec-Fetch-Mode", "cors"),
            Map.entry("Sec-Fetch-Site", "same-origin"),
            Map.entry("authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY5Njg4NjA1MSwiZXhwIjoxNjk2ODg5NjUxfQ.sgcLDcAyz_v5ciNYwgNrP5H_fEM6wrZHQlDvzb2wLbA"),
            Map.entry("sec-ch-ua", "Google Chrome\";v=\"117\", \"Not;A=Brand\";v=\"8\", \"Chromium\";v=\"117"),
            Map.entry("sec-ch-ua-mobile", "?0"),
            Map.entry("sec-ch-ua-platform", "Windows")
    );


    private final ScenarioBuilder scn = scenario("RecordedSimulation")
            .exec(
                    http("request_0")
                            .get("/api/videogame")
                            .headers(headers_0)
            )
            .pause(33)
            .exec(
                    http("request_1")
                            .get("/api/videogame/2")
                            .headers(headers_0)
            )
            .pause(44)
            .exec(
                    http("request_2")
                            .post("/api/authenticate")
                            .headers(headers_2)
                            .body(RawFileBody("videogamedb/recordedsimulation/0002_request.json"))
            )
            .pause(71)
            .exec(
                    http("request_3")
                            .post("/api/videogame")
                            .headers(headers_3)
                            .body(RawFileBody("videogamedb/recordedsimulation/0003_request.json"))
            )
            .pause(21)
            .exec(
                    http("request_4")
                            .put("/api/videogame/3")
                            .headers(headers_3)
                            .body(RawFileBody("videogamedb/recordedsimulation/0004_request.json"))
            )
            .pause(16)
            .exec(
                    http("request_5")
                            .delete("/api/videogame/3")
                            .headers(headers_5)
            );

    {
        setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
    }
}
