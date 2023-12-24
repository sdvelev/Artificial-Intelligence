import math
import random
from collections import defaultdict
from matplotlib import pyplot


MAX_ITERATIONS_BEFORE_STOP = 300

def read_file(filename):
    with open(filename, 'r') as file:
        points_dictionary = []

        for line in file:
            current_point_coordinates = line.split()
            current_point = (float(current_point_coordinates[0]), float(current_point_coordinates[1]))
            points_dictionary.append(current_point)

        return points_dictionary

def kmeans_algorithm(points_dictionary, k, is_enhanced=None):

    if is_enhanced is None:
        kmeans_algorithm(points_dictionary, k, False)
        kmeans_algorithm(points_dictionary, k, True)
        return
    elif is_enhanced:
        centroids_initial_coordinates = init_kmeans_plus_plus_coordinates(points_dictionary, k)
    else:
        centroids_initial_coordinates = init_random_initial_coordinates(points_dictionary, k)

    iterations = 0
    previous_centroids = []

    while not do_stop_iterations(centroids_initial_coordinates, previous_centroids, iterations):
        previous_centroids = centroids_initial_coordinates.copy()
        current_clusters = assign_points_to_clusters(points_dictionary, centroids_initial_coordinates)
        centroids_initial_coordinates = recalculate_centroids_coordinates(current_clusters)
        iterations += 1

    visualize_clusters_diagram(current_clusters, previous_centroids, is_enhanced)

def assign_points_to_clusters(points_dictionary, centroids_coordinates):
    clusters_dictionary = defaultdict(list)

    for current_point in points_dictionary:
        distances_list = []
        for index_of_centroid, centroid_coordinates in enumerate(centroids_coordinates):
            current_distance = euclidean_distance(current_point, centroid_coordinates)
            distances_list.append((index_of_centroid, current_distance))
        shortest_distance_cluster = min(distances_list, key=lambda distance: distance[1])
        shortest_distance_cluster_index = shortest_distance_cluster[0]

        clusters_dictionary[shortest_distance_cluster_index].append(current_point)

    return clusters_dictionary

def recalculate_centroids_coordinates(clusters):
    new_centroids_coordinates_list = []

    for current_cluster in clusters:
        points_for_current_cluster = clusters[current_cluster]

        sum_of_x_coordinates_in_current_cluster = sum(current_point[0] for current_point in points_for_current_cluster)
        sum_of_y_coordinates_in_current_cluster = sum(current_point[1] for current_point in points_for_current_cluster)

        mean_of_x_coordinates_in_current_cluster = sum_of_x_coordinates_in_current_cluster / len(points_for_current_cluster)
        mean_of_y_coordinates_in_current_cluster = sum_of_y_coordinates_in_current_cluster / len(points_for_current_cluster)

        new_centroid_coordinates = mean_of_x_coordinates_in_current_cluster, mean_of_y_coordinates_in_current_cluster
        new_centroids_coordinates_list.append(new_centroid_coordinates)

    return new_centroids_coordinates_list


def do_stop_iterations(current_chosen_centroids, previous_centroids, iterations):
    if iterations > MAX_ITERATIONS_BEFORE_STOP:
        return True
    return current_chosen_centroids == previous_centroids


def init_random_initial_coordinates(points_dictionary, k):
    centroids = random.sample(points_dictionary, k)
    return centroids

def init_kmeans_plus_plus_coordinates(points_dictionary, k):
    chosen_centroids = [random.choice(points_dictionary)]
    while len(chosen_centroids) < k:
        distances = []
        for current_point in points_dictionary:
            min_distance = min(euclidean_distance(current_point, current_centroid) for current_centroid in chosen_centroids)
            distances.append(min_distance ** 2)
        total_distance = sum(distances)
        probabilities = [current_distance / total_distance for current_distance in distances]
        chosen_index = random.choices(range(len(points_dictionary)), probabilities)[0]
        chosen_centroids.append(points_dictionary[chosen_index])
    return chosen_centroids


def euclidean_distance(first_point, second_point):
    total_distance = 0
    for i in range(len(first_point)):
        total_distance += (float(second_point[i]) - float(first_point[i]))**2
    return math.sqrt(total_distance)

def visualize_clusters_diagram(clusters, centroids, is_enhanced):
    colormap = pyplot.colormaps['tab20']

    for cluster_index, (label, points_dictionary) in enumerate(clusters.items()):
        x_coordinate = [point[0] for point in points_dictionary]
        y_coordinate = [point[1] for point in points_dictionary]

        current_color = colormap(cluster_index / len(clusters))

        pyplot.scatter(x_coordinate, y_coordinate, color=current_color, label=f'Cluster #{label}')

    for current_centroid_index, current_centroid_coordinates in enumerate(centroids):
        # current_color = colormap(current_centroid_index / len(clusters))

        pyplot.scatter(current_centroid_coordinates[0], current_centroid_coordinates[1], color='red', marker='^', linewidths=1, label=f'Centroid #{current_centroid_index + 1}')

    if (is_enhanced):
        pyplot.title('kMeans++', fontsize=26)
    else:
        pyplot.title('kMeans', fontsize=26)

    pyplot.show()


if __name__ == '__main__':
    file_name = input('Enter file name: ')
    k_coefficient = int(input('Enter number of clusters (k): '))
    points_dictionary = read_file('./data/' + file_name)

    kmeans_algorithm(points_dictionary, k_coefficient)
    # kmeans_algorithm(points_dictionary, k_coefficient)