package videogamedb.feeders;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import org.apache.commons.lang3.RandomStringUtils;


import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameDbFeeders extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://videogamedb.uk/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    public static LocalDate randomDate() {
        var hundredYears = 100 * 365;
        return LocalDate.ofEpochDay(ThreadLocalRandom.current().nextInt(-hundredYears, hundredYears));
    }

    private static final FeederBuilder.FileBased<String> csvFeeder = csv("data/gameCsvFile.csv").circular();
    private static final FeederBuilder.FileBased<Object> jsonFeeder = jsonFile("data/gameJsonFile.json").random();

    private static final Iterator<Map<String, Object>> customFeeder =
            Stream.generate((Supplier<Map<String, Object>>) () -> {
                        Random rand = new Random();
                        var gameId = rand.nextInt(10 - 1 + 1) + 1;
                        var gameName = RandomStringUtils.randomAlphabetic(5) + "-gameName";
                        var releaseDate = randomDate().toString();
                        var reviewScore = rand.nextInt(100);
                        var category = RandomStringUtils.randomAlphabetic(5) + "-category";
                        var rating = RandomStringUtils.randomAlphabetic(4) + "-rating";

                        var hmap = new HashMap<String, Object>();
                        hmap.put("gameId", gameId);
                        hmap.put("gameName", gameName);
                        hmap.put("releaseDate", releaseDate);
                        hmap.put("reviewScore", reviewScore);
                        hmap.put("category", category);
                        hmap.put("rating", rating);
                        return hmap;
                    }
            ).iterator();

    private static final ChainBuilder authenticate =
            exec(http("Authenticate")
                    .post("/authenticate")
                    .body(StringBody("""
                            {
                              "password": "admin",
                              "username": "admin"
                            }"""))
                    .check(jmesPath("token").saveAs("jwtToken")));

    private static final ChainBuilder createNewGame =
            feed(customFeeder)
                    .exec(http("Create New Game - #{gameName}")
                            .post("/videogame")
                            .header("authorization", "Bearer #{jwtToken}")
                            .body(ElFileBody("bodies/newGameTemplate.json")).asJson()
                            .check(bodyString().saveAs("responseBody")))
                    .exec(session -> {
                        System.out.println(session.getString("responseBody"));
                        return session;
                    });

    private final ScenarioBuilder scn = scenario("Video Game Db - Section 6 code")
            .exec(authenticate)
            .repeat(10).on(
                    exec(createNewGame)
                            .pause(1)
            );


    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }
}
