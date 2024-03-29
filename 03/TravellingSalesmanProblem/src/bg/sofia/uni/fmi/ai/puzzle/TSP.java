package bg.sofia.uni.fmi.ai.puzzle;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class TSP {
    private final static int MAX_EPOCHS_COEFFICIENT = 18;
    private final static int NUMBER_OF_MIDDLE_EPOCHS_TO_PRINT = 8;
    private final static double ROUTES_IN_ALGORITHM_COEFFICIENT = 0.5;
    private final static int MUTATION_PROBABILITY_INDEX = 5;
    private final static int MUTATION_SWAP_TWO_CITIES_IN_ROUTE_INDEX = 0;
    private final static int MUTATION_INSERT_CITY_BETWEEN_TWO_CITIES_INDEX = 1;
    private final static int MUTATION_REVERSE_CITIES_IN_INTERVAL_INDEX = 2;
    private final static double ROUTES_TO_MUTATE_IF_SIMILAR_COEFFICIENT = 0.1;
    private final static Random RANDOM_GENERATOR = new Random();
    private static final DecimalFormat DECIMAL_FORMAT_ROUND_TWELVE = new DecimalFormat("0.000000000000");

    private final int numberOfCities;
    private int numberOfRoutesInAlgorithm;
    private final int maxEpochs;
    private final RoutesCollection routesCollection;

    public TSP(int numberOfCities) {
        this.numberOfCities = numberOfCities;
        this.numberOfRoutesInAlgorithm = (int) (ROUTES_IN_ALGORITHM_COEFFICIENT * numberOfCities);
        if (numberOfRoutesInAlgorithm % 2 != 0) {
            --numberOfRoutesInAlgorithm;
        }

        this.routesCollection = new RoutesCollection(numberOfCities);
        this.maxEpochs = MAX_EPOCHS_COEFFICIENT * numberOfCities;
    }

    public TSP(int numberOfCities, List<City> currentEpochRoute) {
        this.numberOfCities = numberOfCities;
        this.numberOfRoutesInAlgorithm = (int) (ROUTES_IN_ALGORITHM_COEFFICIENT * numberOfCities);
        if (numberOfRoutesInAlgorithm % 2 != 0) {
            --numberOfRoutesInAlgorithm;
        }

        this.routesCollection = new RoutesCollection(numberOfCities, currentEpochRoute);
        this.maxEpochs = MAX_EPOCHS_COEFFICIENT * numberOfCities;
    }

    public void geneticAlgorithm() {
        Route firstShortestRoute = routesCollection.calculateTheShortestRoute();
        System.out.println("In first epoch 0, the route with the shortest distance is with length: "
            + DECIMAL_FORMAT_ROUND_TWELVE.format(firstShortestRoute.getTotalDistanceBetweenCities()) + " City sequence: "
            + firstShortestRoute.getNameOfCitiesInRoute());

        int equalShortestPathEpochsCounter = 0;
        boolean isRandom = false;
        List<Integer> indexesOfEpochsToPrint = chooseIndexesOfEpochsToBePrinted();
        for (int indexOfEpoch = 1; indexOfEpoch < maxEpochs - 1; indexOfEpoch++) {
            Route currentlyShortestRouteBeforeChildren = routesCollection.calculateTheShortestRoute();
            if (indexesOfEpochsToPrint.contains(indexOfEpoch)) {
                System.out.println("In epoch " + indexOfEpoch + ", the route with the shortest distance is with length: "
                    + DECIMAL_FORMAT_ROUND_TWELVE.format(currentlyShortestRouteBeforeChildren.getTotalDistanceBetweenCities())
                    + " City Sequence: "
                    + currentlyShortestRouteBeforeChildren.getNameOfCitiesInRoute());
            }

            reproductionPhase(isRandom);

            Route currentlyShortestRouteAfterChildren = routesCollection.calculateTheShortestRoute();
            if ((int) currentlyShortestRouteBeforeChildren.getTotalDistanceBetweenCities() ==
                (int) currentlyShortestRouteAfterChildren.getTotalDistanceBetweenCities()) {
                ++equalShortestPathEpochsCounter;
            } else {
                equalShortestPathEpochsCounter = 0;
            }

            if (equalShortestPathEpochsCounter > MAX_EPOCHS_COEFFICIENT) {
                isRandom = true;
                for (int i = 0;
                     i < (int) (ROUTES_TO_MUTATE_IF_SIMILAR_COEFFICIENT * this.routesCollection.getRoutesCollection().size());
                     i++){
                    int randomRouteIndex = RANDOM_GENERATOR.nextInt(MAX_EPOCHS_COEFFICIENT * numberOfCities);
                    Route randomRute = this.routesCollection.getRoutesCollection().get(randomRouteIndex);

                    this.routesCollection.getRoutesCollection().set(randomRouteIndex, mutateRandomlyByRoute(randomRute));
                }
            } else {
                isRandom = false;
            }
        }

        Route finalShortestRoute = routesCollection.calculateTheShortestRoute();
        System.out.println("In last epoch " + (maxEpochs - 1) + ", the route with the shortest distance is with length: "
            + DECIMAL_FORMAT_ROUND_TWELVE.format(finalShortestRoute.getTotalDistanceBetweenCities()) + " City sequence: "
            + finalShortestRoute.getNameOfCitiesInRoute());
    }

    private List<Integer> chooseIndexesOfEpochsToBePrinted() {
        List<Integer> indexesOfEpochsToPrint = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_MIDDLE_EPOCHS_TO_PRINT; i++) {
            int generatedIndexOfEpochToPrint;
            do {
                generatedIndexOfEpochToPrint = RANDOM_GENERATOR.nextInt(maxEpochs - 1);
            } while (indexesOfEpochsToPrint.contains(generatedIndexOfEpochToPrint));

            indexesOfEpochsToPrint.add(generatedIndexOfEpochToPrint);
        }

        return indexesOfEpochsToPrint;
    }

    private void reproductionPhase(boolean randomSearch) {
        List<Route> shortestRoutesToCrossOver = !randomSearch
            ? this.routesCollection.findTheExtremumRoutesInCollection(true)
            : this.routesCollection.findTheRandomExtremumRoutes(true);

        List<Route> newRoutesResultsList = new ArrayList<>();
        for (int i = 0; i < numberOfRoutesInAlgorithm - 1; i += 2) {
            List<Route> newPairOfRoutesAfterCrossOver = twoPointCrossOver(shortestRoutesToCrossOver.get(i),
                shortestRoutesToCrossOver.get(i + 1));

            mutateRandomlyByRoute(newPairOfRoutesAfterCrossOver.get(0));
            mutateRandomlyByRoute(newPairOfRoutesAfterCrossOver.get(1));

            newRoutesResultsList.add(newPairOfRoutesAfterCrossOver.get(0));
            newRoutesResultsList.add(newPairOfRoutesAfterCrossOver.get(1));
        }

        List<Route> longestRoutesToBeDestroyed = !randomSearch
            ? this.routesCollection.findTheExtremumRoutesInCollection(false)
            : this.routesCollection.findTheRandomExtremumRoutes(false);

        for (int i = 0; i < numberOfRoutesInAlgorithm; i++) {
            Route currentLongestRoute = longestRoutesToBeDestroyed.get(i);
            int currentLongestRouteIndexInPreviousEpoch = routesCollection.getRoutesCollection().indexOf(currentLongestRoute);
            routesCollection.getRoutesCollection().set(currentLongestRouteIndexInPreviousEpoch, newRoutesResultsList.get(i));
        }
    }

    private List<Route> twoPointCrossOver(Route firstRouteBeforeCrossOver, Route secondRouteBeforeCrossOver) {
        List<Route> routesAfterCrossOver = new ArrayList<>();
        int leftIndex, rightIndex;
        do {
            rightIndex = RANDOM_GENERATOR.nextInt(numberOfCities);
            if (rightIndex != 0) {
                leftIndex = RANDOM_GENERATOR.nextInt(rightIndex);
            } else {
                leftIndex = 0;
            }
        } while (leftIndex == rightIndex);

        Route firstRouteAfterCrossOver = new Route(numberOfCities);
        Route secondRouteAfterCrossOver = new Route(numberOfCities);

        while (leftIndex <= rightIndex) {
            firstRouteAfterCrossOver.setCityAtIndex(leftIndex, firstRouteBeforeCrossOver.getCityAtIndex(leftIndex));
            secondRouteAfterCrossOver.setCityAtIndex(leftIndex, secondRouteBeforeCrossOver.getCityAtIndex(leftIndex));
            ++leftIndex;
        }

        int positionToOccupy = rightIndex + 1;
        if (rightIndex >= numberOfCities - 1) {
            positionToOccupy = 0;
            rightIndex = -1;
        }

        for (int i = rightIndex + 1; firstRouteAfterCrossOver.containsNull(); i++) {
            if (positionToOccupy == numberOfCities) {
                positionToOccupy = 0;
            }

            if (!firstRouteAfterCrossOver.containsCity(secondRouteBeforeCrossOver.getCityAtIndex(i))) {
                firstRouteAfterCrossOver.setCityAtIndex(positionToOccupy++, secondRouteBeforeCrossOver.getCityAtIndex(i));
            }

            if (i == numberOfCities - 1) {
                i = -1;
            }
        }

        positionToOccupy = rightIndex + 1;
        for (int i = rightIndex + 1; secondRouteAfterCrossOver.containsNull() ; i++) {
            if (positionToOccupy == numberOfCities) {
                positionToOccupy = 0;
            }

            if (!secondRouteAfterCrossOver.containsCity(firstRouteBeforeCrossOver.getCityAtIndex(i))) {
                secondRouteAfterCrossOver.setCityAtIndex(positionToOccupy++, firstRouteBeforeCrossOver.getCityAtIndex(i));
            }

            if (i == numberOfCities - 1) {
                i = -1;
            }
        }

        routesAfterCrossOver.add(firstRouteAfterCrossOver);
        routesAfterCrossOver.add(secondRouteAfterCrossOver);
        return routesAfterCrossOver;
    }

    private Route mutateRandomlyByRoute(Route routeToMutate) {
        int mutationProbabilityIndex = RANDOM_GENERATOR.nextInt(MUTATION_PROBABILITY_INDEX);

        boolean mutationResult;
        if (mutationProbabilityIndex == MUTATION_SWAP_TWO_CITIES_IN_ROUTE_INDEX) {
            do {
                List<Integer> mutationIndexes = generateMutationIndexes();
                int mutationFirstIndex = mutationIndexes.get(0);
                int mutationSecondIndex = mutationIndexes.get(1);
                mutationResult = routeToMutate.swapTwoCitiesInRoute(routeToMutate.getCityAtIndex(mutationFirstIndex),
                    routeToMutate.getCityAtIndex(mutationSecondIndex));
            } while (!mutationResult);
        } else if (mutationProbabilityIndex == MUTATION_INSERT_CITY_BETWEEN_TWO_CITIES_INDEX) {
            do {
                List<Integer> mutationIndexes = generateMutationIndexes();
                int mutationFirstIndex = mutationIndexes.get(0);
                int mutationSecondIndex = mutationIndexes.get(1);
                mutationResult = routeToMutate.insertCityBetweenTwoCities(routeToMutate.getCityAtIndex(mutationFirstIndex),
                    mutationSecondIndex);
            } while (!mutationResult);
        } else if (mutationProbabilityIndex == MUTATION_REVERSE_CITIES_IN_INTERVAL_INDEX) {
            do {
                List<Integer> mutationIndexes = generateMutationIndexes();
                int mutationFirstIndex = mutationIndexes.get(0);
                int mutationSecondIndex = mutationIndexes.get(1);
                mutationResult = routeToMutate.reverseCitiesInInterval(mutationFirstIndex, mutationSecondIndex);
            } while (!mutationResult);
        }

        routeToMutate.calculateTotalDistanceBetweenCities();
        return routeToMutate;
    }

    private List<Integer> generateMutationIndexes() {
        int mutationSecondIndex = RANDOM_GENERATOR.nextInt(numberOfCities);
        int mutationFirstIndex;
        if (mutationSecondIndex != 0) {
            mutationFirstIndex = RANDOM_GENERATOR.nextInt(mutationSecondIndex);
        } else {
            mutationFirstIndex = 0;
        }

        return List.of(mutationFirstIndex, mutationSecondIndex);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numberOfCities = scanner.nextInt();

        List<City> ukCities = new ArrayList<>();
        for (int i = 0; i < numberOfCities; i++) {
            double locationXCoordinate = scanner.nextDouble();
            double locationYCoordinate = scanner.nextDouble();
            String cityName = scanner.next();
            ukCities.add(new City(cityName, locationXCoordinate, locationYCoordinate));
        }

        TSP puzzle = new TSP(numberOfCities, ukCities);
        puzzle.geneticAlgorithm();
        scanner.close();
    }
}
//Input for UK Cities:

//12
//0.000190032 -0.000285946 Aberystwyth
//383.458	-0.000608756 Brighton
//-27.0206 -282.758 Edinburgh
//335.751	-269.577 Exeter
//69.4331	-246.78 Glasgow
//168.521	31.4012 Inverness
//320.35	-160.9 Liverpool
//179.933	-318.031 London
//492.671	-131.563 Newcastle
//112.198	-110.561 Nottingham
//306.32	-108.09 Oxford
//217.343	-447.089 Stratford

// Expected output

//Shortest distance: 1595.738522033024
//City sequence: Aberystwyth -> Inverness -> Nottingham -> Glasgow -> Edinburgh -> London -> Stratford -> Exeter -> Liverpool -> Oxford -> Brighton -> Newcastle
//Reversed city sequence: Newcastle -> Brighton -> Oxford -> Liverpool -> Exeter -> Stratford -> London -> Edinburgh -> Glasgow -> Nottingham -> Inverness -> Aberystwyth