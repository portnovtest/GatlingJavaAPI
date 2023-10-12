package videogamedb.finalsimulation;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;


public class VideoGameDbFullTest extends Simulation {

    // HTTP PROTOCOL
    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://videogamedb.uk/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // RUNTIME PARAMETERS
    private static final int USER_COUNT = Integer.parseInt(System.getProperty("USERS", "5"));
    private static final int RAMP_DURATION = Integer.parseInt(System.getProperty("RAMP_DURATION", "10"));
    private static final int TEST_DURATION = Integer.parseInt(System.getProperty("TEST_DURATION", "60"));

    // FEEDER FOR TEST - CSV, JSON etc.
    private static final FeederBuilder.FileBased<Object> jsonFeeder = jsonFile("data/gameJsonFile.json").random();

    // BEFORE BLOCK
    @Override
    public void before() {
        System.out.printf("Running test with %d users%n", USER_COUNT);
        System.out.printf("Running users over %d seconds%n", RAMP_DURATION);
        System.out.printf("Total test duration: %d seconds%n", TEST_DURATION);
    }

    // HTTP CALLS
    private static final ChainBuilder authenticate =
            exec(http("Authenticate")
                    .post("/authenticate")
                    .body(StringBody("""
                            {
                              "password": "admin",
                              "username": "admin"
                            }"""))
                    .check(jmesPath("token").saveAs("jwtToken")));

    private static final ChainBuilder getAllVideoGames =
            exec(http("Get all video games")
                    .get("/videogame"));

    private static final ChainBuilder createNewGame =
            feed(jsonFeeder)
                    .exec(http("Create New Game - #{name}")
                            .post("/videogame")
                            .header("authorization", "Bearer #{jwtToken}")
                            .body(ElFileBody("bodies/newGameTemplate.json")).asJson()
                            .check(bodyString().saveAs("responseBody")))
                    .exec(session -> {
                        System.out.println(session.getString("responseBody"));
                        return session;
                    });

    private static final ChainBuilder getLastPostedGame =
            exec(http("Get Last Posted Game - #{name}")
                    .get("/videogame/#{id}")
                    .check(jmesPath("name").isEL("#{name}")));

    private static final ChainBuilder deleteLastPostedGame =
            exec(http("Delete game - #{name}")
                    .delete("/videogame/#{id}")
                    .header("authorization", "Bearer #{jwtToken}")
                    .check(bodyString().is("Video game deleted")));

    // SCENARIO OR USER JOURNEY
    // 1. Get all video games
    // 2. Create a new game
    // 3. Get details of newly created game
    // 4. Delete newly created game
    private final ScenarioBuilder scn = scenario("Video game db - final simulation")
            .forever().on(
                    exec(getAllVideoGames)
                            .pause(2)
                            .exec(authenticate)
                            .pause(2)
                            .exec(createNewGame)
                            .pause(2)
                            .exec(getLastPostedGame)
                            .pause(2)
                            .exec(deleteLastPostedGame)
            );

    {
        setUp(
                scn.injectOpen(
                        nothingFor(5),
                        rampUsers(USER_COUNT).during(RAMP_DURATION)
                ).protocols(httpProtocol)
        ).maxDuration(TEST_DURATION);
    }

    // AFTER BLOCK
    @Override
    public void after() {
        System.out.println("Stress test completed");
    }
}
