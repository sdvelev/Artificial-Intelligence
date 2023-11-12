package bg.sofia.uni.fmi.ai.puzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Route {
    private final int numberOfCities;
    private List<City> citiesInRouteList;
    private double totalDistanceBetweenCities;

    public Route(int numberOfCities) {
        this.numberOfCities = numberOfCities;
        this.citiesInRouteList = new ArrayList<>(numberOfCities);

        for (int i = 0; i < numberOfCities; i++) {
            this.citiesInRouteList.add(null);
        }

        this.totalDistanceBetweenCities = 0;
    }

    public Route(int numberOfCities, List<City> citiesInRouteList) {
        this.numberOfCities = numberOfCities;
        this.citiesInRouteList = new ArrayList<>(numberOfCities);

        for (int i = 0; i < numberOfCities; i++) {
            this.citiesInRouteList.add(null);
        }

        this.citiesInRouteList.addAll(citiesInRouteList);
        this.totalDistanceBetweenCities = calculateTotalDistanceBetweenCities();

    }

    public boolean addCity(City cityToAdd) {
        if (cityToAdd != null && citiesInRouteList.size() < numberOfCities) {
            this.totalDistanceBetweenCities +=
                citiesInRouteList.get(citiesInRouteList.size() - 1).findDistanceToCity(cityToAdd);
            return citiesInRouteList.add(cityToAdd);
        }
        return false;
    }

    public City getCityAtIndex(int indexCity) {
        return citiesInRouteList.get(indexCity);
    }

    public double getTotalDistanceBetweenCities() {
        return totalDistanceBetweenCities;
    }

    public void setCityAtIndex(int index, City cityToSet) {
        citiesInRouteList.set(index, cityToSet);
    }

    public double calculateFitnessFunction() {
        return 1 / totalDistanceBetweenCities;
    }

    public boolean swapTwoCitiesInRoute(City firstCity, City secondCity) {
        int indexFirstCity = citiesInRouteList.indexOf(firstCity);
        int indexSecondCity = citiesInRouteList.indexOf(secondCity);

        if (indexFirstCity == -1 || indexSecondCity == -1) {
            return false;
        }

        citiesInRouteList.set(indexFirstCity, secondCity);
        citiesInRouteList.set(indexSecondCity, firstCity);

        return true;
    }

    public boolean insertCityBetweenTwoCities(City cityToMove, int desiredPositionIndex) {
        int indexCityToMove = citiesInRouteList.indexOf(cityToMove);

        if (desiredPositionIndex >= citiesInRouteList.size() || indexCityToMove == -1 ||
            desiredPositionIndex == indexCityToMove) {
            return false;
        }

        if (desiredPositionIndex > indexCityToMove) {
            citiesInRouteList.add(desiredPositionIndex, cityToMove);
            citiesInRouteList.remove(cityToMove);
        } else {
            citiesInRouteList.remove(cityToMove);
            citiesInRouteList.add(desiredPositionIndex, cityToMove);
        }

        return true;
    }

    public boolean reverseCitiesInInterval(int leftIndex, int rightIndex) {
        if (leftIndex >= rightIndex || leftIndex >= citiesInRouteList.size() - 1 ||
            rightIndex >= citiesInRouteList.size()) {
            return false;
        }

        List<City> resultList = new ArrayList<>();
        for (int i = 0; i < leftIndex; i++) {
            resultList.add(citiesInRouteList.get(i));
        }

        List<City> subListToReverse = citiesInRouteList.subList(leftIndex, rightIndex);
        Collections.reverse(subListToReverse);

        resultList.addAll(subListToReverse);

        for (int j = rightIndex; j < citiesInRouteList.size(); j++) {
            resultList.add(citiesInRouteList.get(j));
        }

        citiesInRouteList = resultList;
        return true;
    }

    public boolean containsNull() {
        return this.citiesInRouteList.contains(null);
    }

    public boolean containsCity(City cityToCheck) {
        return this.citiesInRouteList.contains(cityToCheck);
    }

    public String getNameOfCitiesInRoute() {
        StringBuilder citiesName = new StringBuilder();

        for (City currentCity : this.citiesInRouteList) {

            if (currentCity.name().isBlank()) {
                return null;
            }

            citiesName.append(currentCity.name());
            citiesName.append(" -> ");
        }

        for (int i = 0; i < 4; i++) {
            citiesName.deleteCharAt(citiesName.length() -1);
        }

        return citiesName.toString();
    }

    public double calculateTotalDistanceBetweenCities() {
        totalDistanceBetweenCities = 0;

        for (int i = 0; i < citiesInRouteList.size() - 1; i++) {
            totalDistanceBetweenCities += citiesInRouteList.get(i).findDistanceToCity(citiesInRouteList.get(i + 1));
        }

        return totalDistanceBetweenCities;
    }
}
