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

    private final int totalNumberOfRoutes;
    private final List<Route> routesCollection;
    private final int numberOfCitiesInRoute;
    private int numberOfRoutesInAlgorithm;
    private final List<City> currentEpochRoute;

    public RoutesCollection(int numberOfCitiesInRoute) {
        this.totalNumberOfRoutes = NUMBER_OF_ROUTES_COEFFICIENT * numberOfCitiesInRoute;
        this.numberOfCitiesInRoute = numberOfCitiesInRoute;
        this.numberOfRoutesInAlgorithm = (int) (ROUTES_IN_ALGORITHM_COEFFICIENT * numberOfCitiesInRoute);
        if (numberOfRoutesInAlgorithm % 2 != 0) {
            --numberOfRoutesInAlgorithm;
        }

        this.currentEpochRoute = generateFirstEpoch();
        this.routesCollection = generateFirstPopulation();
    }

    public RoutesCollection(int numberOfCitiesInRoute, List<City> currentEpochRoute) {
        this.totalNumberOfRoutes = NUMBER_OF_ROUTES_COEFFICIENT * numberOfCitiesInRoute;
        this.numberOfCitiesInRoute = numberOfCitiesInRoute;
        this.numberOfRoutesInAlgorithm = (int) (ROUTES_IN_ALGORITHM_COEFFICIENT * numberOfCitiesInRoute);
        if (numberOfRoutesInAlgorithm % 2 != 0) {
            --numberOfRoutesInAlgorithm;
        }

        this.currentEpochRoute = currentEpochRoute;
        this.routesCollection = generateFirstPopulation();
    }

    public List<Route> getRoutesCollection() {
        return routesCollection;
    }

    public List<Route> findTheExtremumRoutesInCollection(boolean shortest) {
        List<Route> extremumRoutesList = new ArrayList<>();
        Queue<Route> extremumRoutesQueue = shortest
            ? new PriorityQueue<>(Comparator.comparing(Route::getTotalDistanceBetweenCities))
            : new PriorityQueue<>(Comparator.comparing(Route::calculateFitnessFunction));

        extremumRoutesQueue.addAll(this.routesCollection);
        for (int i = 0; i < numberOfRoutesInAlgorithm; i++) {
            extremumRoutesList.add(extremumRoutesQueue.poll());
        }

        return extremumRoutesList;
    }

    public List<Route> findTheRandomExtremumRoutes(boolean shortest) {
        List<Route> extremumRoutesList = new ArrayList<>();
        for (int i = 0; i < numberOfRoutesInAlgorithm; i++) {
            Route extremumRouteFromRandomEpoch;
            do {
                extremumRouteFromRandomEpoch = findTheExtremumRouteFromRandomEpoch(shortest);
            } while (extremumRoutesList.contains(extremumRouteFromRandomEpoch));

            extremumRoutesList.add(extremumRouteFromRandomEpoch);
        }

        return extremumRoutesList;
    }

    public Route calculateTheShortestRoute() {
        Route shortestRoute = null;
        double shortestRouteFitnessFunction = 0;
        for (int i = 0; i < totalNumberOfRoutes; i++) {
            if (routesCollection.get(i).calculateFitnessFunction() > shortestRouteFitnessFunction) {
                shortestRouteFitnessFunction = routesCollection.get(i).calculateFitnessFunction();
                shortestRoute = routesCollection.get(i);
            }
        }

        return shortestRoute;
    }

    private List<City> generateFirstEpoch() {
        List<City> firstEpochList = new ArrayList<>();
        for (int i = 0; i < numberOfCitiesInRoute; i++) {
            firstEpochList.add(new City(Integer.toString(i), RANDOM_GENERATOR.nextDouble(-MAX_COORDINATES_SCOPE, MAX_COORDINATES_SCOPE),
                    RANDOM_GENERATOR.nextDouble(-MAX_COORDINATES_SCOPE, MAX_COORDINATES_SCOPE)));
        }

        return firstEpochList;
    }

    private List<Route> generateFirstPopulation() {
        List<Route> firstPopulationList = new ArrayList<>();

        for (int i = 0; i < totalNumberOfRoutes; i++) {
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

    private Route findTheExtremumRouteFromRandomEpoch(boolean shortest) {
        List<Route> routesFromRandomEpoch = generateRandomRoutesForAlgorithm();

        Queue<Route> routesFromRandomEpochQueue = shortest
            ? new PriorityQueue<>(Comparator.comparing(Route::getTotalDistanceBetweenCities))
            : new PriorityQueue<>(Comparator.comparing(Route::calculateFitnessFunction));

        routesFromRandomEpochQueue.addAll(routesFromRandomEpoch);

        return routesFromRandomEpochQueue.poll();
    }

    private List<Route> generateRandomRoutesForAlgorithm() {
        List<Route> generatedRoutesList = new ArrayList<>();
        for (int i = 0; i < numberOfRoutesInAlgorithm; i++) {
            int randomIndexOfRoute;
            do {
                randomIndexOfRoute = RANDOM_GENERATOR.nextInt(totalNumberOfRoutes);
            } while (generatedRoutesList.contains(this.routesCollection.get(randomIndexOfRoute)));

            generatedRoutesList.add(this.routesCollection.get(randomIndexOfRoute));
        }

        return generatedRoutesList;
    }
}