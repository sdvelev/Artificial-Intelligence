package bg.sofia.uni.fmi.ai.puzzle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class RoutesCollection {
    private final static int NUMBER_OF_ROUTES_COEFFICIENT = 16;
    private final static int MAX_COORDINATES_SCOPE = 600;
    private final static double ROUTES_IN_ALGORITHM_COEFFICIENT = 0.5;
    private final static Random RANDOM_GENERATOR = new Random();

    private final int numberOfRoutes;
    private final List<Route> routesCollection;
    private final int numberOfCitiesInRoute;
    private int numberOfRoutesInAlgorithm;
    private final List<City> currentEpochRoute;

    public RoutesCollection(int numberOfCitiesInRoute) {
        this.numberOfRoutes = NUMBER_OF_ROUTES_COEFFICIENT * numberOfCitiesInRoute;
        this.numberOfCitiesInRoute = numberOfCitiesInRoute;
        this.numberOfRoutesInAlgorithm = (int) (ROUTES_IN_ALGORITHM_COEFFICIENT * numberOfCitiesInRoute);

        if (numberOfRoutesInAlgorithm % 2 != 0) {
            --numberOfRoutesInAlgorithm;
        }

        this.currentEpochRoute = generateFirstEpoch();
        this.routesCollection = generateFirstPopulation();
    }

    public RoutesCollection(int numberOfCitiesInRoute, List<City> currentEpochRoute) {
        this.numberOfRoutes = NUMBER_OF_ROUTES_COEFFICIENT * numberOfCitiesInRoute;
        this.numberOfCitiesInRoute = numberOfCitiesInRoute;
        this.numberOfRoutesInAlgorithm = (int) (ROUTES_IN_ALGORITHM_COEFFICIENT * numberOfCitiesInRoute);

        if (numberOfRoutesInAlgorithm % 2 != 0) {
            --numberOfRoutesInAlgorithm;
        }

        this.currentEpochRoute = currentEpochRoute;
        this.routesCollection = generateFirstPopulation();
    }

    private List<City> generateFirstEpoch() {
        List<City> firstEpochList = new ArrayList<>();

        for (int i = 0; i < numberOfCitiesInRoute; i++) {
//            firstEpochList.add(City.
//                createCityWithoutName(RANDOM_GENERATOR.nextDouble(-MAX_COORDINATES_SCOPE, MAX_COORDINATES_SCOPE),
//                    RANDOM_GENERATOR.nextDouble(-MAX_COORDINATES_SCOPE, MAX_COORDINATES_SCOPE)));
            firstEpochList.add(new City(Integer.toString(i), RANDOM_GENERATOR.nextDouble(-MAX_COORDINATES_SCOPE, MAX_COORDINATES_SCOPE),
                    RANDOM_GENERATOR.nextDouble(-MAX_COORDINATES_SCOPE, MAX_COORDINATES_SCOPE)));
        }

        return firstEpochList;
    }

    private List<Route> generateFirstPopulation() {
        List<Route> firstPopulationList = new ArrayList<>();

        for (int i = 0; i < numberOfRoutes; i++) {
            Route routeToGenerate;

            do {
                routeToGenerate = generateRoute();
            } while (firstPopulationList.contains(routeToGenerate));

            firstPopulationList.add(routeToGenerate);
        }

        return firstPopulationList;
    }

    private Route generateRoute() {
        Route routeToGenerate = new Route(numberOfCitiesInRoute);

        for (int i = 0; i < numberOfCitiesInRoute; i++) {
            int j;

            do {
                j = RANDOM_GENERATOR.nextInt(numberOfCitiesInRoute);
            } while (routeToGenerate.getCityAtIndex(j) != null);


            routeToGenerate.setCityAtIndex(j, currentEpochRoute.get(i));
        }

        routeToGenerate.calculateTotalDistanceBetweenCities();

        return routeToGenerate;
    }

    public List<Route> getRoutesCollection() {
        return routesCollection;
    }

    public List<Route> findTheShortestRoutesInCollection() {
        List<Route> shortestRoutesList = new ArrayList<>();
        Queue<Route> shortestRoutesQueue = new PriorityQueue<>(Comparator.comparing(Route::getTotalDistanceBetweenCities));

        shortestRoutesQueue.addAll(this.routesCollection);

        for (int i = 0; i < numberOfRoutesInAlgorithm; i++) {
            shortestRoutesList.add(shortestRoutesQueue.poll());
        }

        return shortestRoutesList;
    }

    public List<Route> findTheLongestRoutesInCollection() {
        List<Route> longestRoutesList = new ArrayList<>();
        Queue<Route> longestRoutesQueue = new PriorityQueue<>(Comparator.comparing(Route::calculateFitnessFunction));

        longestRoutesQueue.addAll(this.routesCollection);

        for (int i = 0; i < numberOfRoutesInAlgorithm; i++) {
            longestRoutesList.add(longestRoutesQueue.poll());
        }

        return longestRoutesList;
    }

    public List<Route> findTheRandomShortestRoutes() {
        List<Route> shortestRoutesList = new ArrayList<>();

        for (int i = 0; i < numberOfRoutesInAlgorithm; i++) {
            Route shortestRouteFromRandomEpoch;

            do {
                shortestRouteFromRandomEpoch = findTheShortestRouteFromRandomEpoch();
            } while (shortestRoutesList.contains(shortestRouteFromRandomEpoch));

            shortestRoutesList.add(shortestRouteFromRandomEpoch);
        }

        return shortestRoutesList;
    }

    private Route findTheShortestRouteFromRandomEpoch() {
        List<Route> routesFromRandomEpoch = generateRandomRoutesForAlgorithm();

        Queue<Route> routesFromRandomEpochQueue = new PriorityQueue<>(Comparator.comparing(Route::getTotalDistanceBetweenCities));

        routesFromRandomEpochQueue.addAll(routesFromRandomEpoch);

        return routesFromRandomEpochQueue.poll();
    }

    public List<Route> findTheRandomLongestRoutes() {
        List<Route> longestRoutesList = new ArrayList<>();

        for (int i = 0; i < numberOfRoutesInAlgorithm; i++) {
            Route longestRouteFromRandomEpoch;

            do {
                longestRouteFromRandomEpoch = findTheLongestRouteFromRandomEpoch();
            } while (longestRoutesList.contains(longestRouteFromRandomEpoch));

            longestRoutesList.add(longestRouteFromRandomEpoch);
        }

        return longestRoutesList;
    }

    private Route findTheLongestRouteFromRandomEpoch() {
        List<Route> routesFromRandomEpoch = generateRandomRoutesForAlgorithm();

        Queue<Route> routesFromRandomEpochQueue = new PriorityQueue<>(Comparator.comparing(Route::calculateFitnessFunction));

        routesFromRandomEpochQueue.addAll(routesFromRandomEpoch);

        return routesFromRandomEpochQueue.poll();
    }

    private List<Route> generateRandomRoutesForAlgorithm() {
        List<Route> generatedRoutesList = new ArrayList<>();

        for (int i = 0; i < numberOfRoutesInAlgorithm; i++) {
            int randomIndexOfRoute;

            do {
                randomIndexOfRoute = RANDOM_GENERATOR.nextInt(numberOfRoutes);
            } while (generatedRoutesList.contains(this.routesCollection.get(randomIndexOfRoute)));

            generatedRoutesList.add(this.routesCollection.get(randomIndexOfRoute));
        }

        return generatedRoutesList;
    }

    public Route calculateTheShortestRoute() {
        Route shortestRoute = null;
        double shortestRouteFitnessFunction = 0;

        for (int i = 0; i < numberOfRoutes; i++) {
            if (routesCollection.get(i).calculateFitnessFunction() > shortestRouteFitnessFunction) {
                shortestRouteFitnessFunction = routesCollection.get(i).calculateFitnessFunction();
                shortestRoute = routesCollection.get(i);
            }
        }

        return shortestRoute;
    }



}
