package videogamedb.scriptfundamentals;


import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;


import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameDb extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://videogamedb.uk/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private static final ChainBuilder authenticate =
            exec(http("Authenticate")
                    .post("/authenticate")
                    .body(StringBody("""
                            {
                              "password": "admin",
                              "username": "admin"
                            }"""))
                    .check(jmesPath("token").saveAs("jwtToken")));

    private final ChainBuilder createNewGame =
            exec(http("Create new game")
                    .post("/videogame")
                    .header("Authorization", "Bearer #{jwtToken}")
                    .body(StringBody(
                            """
                                    {
                                      "category": "Platform",
                                      "name": "Mario",
                                      "rating": "Mature",
                                      "releaseDate": "2012-05-04",
                                      "reviewScore": 85
                                    }"""
                    )));

    private final ScenarioBuilder scn = scenario("Video Game Db - Section 5 code")
            .exec(authenticate)
            .exec(createNewGame);


    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }
}
