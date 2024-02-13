package bg.sofia.uni.fmi.ai.puzzle;

public record City(String cityName, double locationXCoordinate, double locationYCoordinate) {
    public double findDistanceToCity(City otherCity) {
        double distanceBetweenXCoordinates = Math.abs(locationXCoordinate - otherCity.locationXCoordinate());
        double distanceBetweenYCoordinates = Math.abs(locationYCoordinate - otherCity.locationYCoordinate());
        return Math.sqrt(distanceBetweenXCoordinates * distanceBetweenXCoordinates +
            distanceBetweenYCoordinates * distanceBetweenYCoordinates);
    }
}