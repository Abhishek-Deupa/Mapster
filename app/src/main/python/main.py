import heapq
import time

floor_graph = {
        '306 IPDC Lab': [('307A Classroom', (1.5, 'LEFT then take RIGHT')), ('Elevator', (1, 'RIGHT'))],
        '307A Classroom': [('306 IPDC Lab', (1.5, 'LEFT then again take LEFT ')), ('307B Control Lab', (2, 'STRAIGHT'))],
        '307B Control Lab': [('307A Classroom', (2, 'STRAIGHT')), ('308A Classroom', (0.5, 'STRAIGHT'))],
        '308A Classroom': [('307B Control Lab', (0.5, 'STRAIGHT')), ('309A Classroom', (2, 'STRAIGHT'))],
        '309A Classroom': [('308A Classroom', (2, 'STRAIGHT')), ('309B Analog Lab', (2.5, 'RIGHT then take LEFT')), ('Washroom', (4.5, 'STRAIGHT'))],
        '309B Analog Lab': [('309A Classroom', (2.5, 'RIGHT then again take RIGHT')), ('310 Classroom', (2.5, 'STRAIGHT'))],
        'Washroom': [('309A Classroom', (4.5, 'STRAIGHT')), ('313B Classroom', (1.5, 'RIGHT')), ('314A Food Science Lab', (4, 'STRAIGHT towards balcony then take RIGHT'))],
        '310 Classroom': [('309B Analog Lab', (2.5, 'STRAIGHT')), ('311 Faculty Room', (1.5, 'RIGHT'))],
        '311 Faculty Room': [('310 Classroom', (1.5, 'LEFT')), ('312A Classroom', (2.5, 'STRAIGHT'))],
        '312A Classroom': [('311 Faculty Room', (2.5, 'STRAIGHT')), ('313A Classroom', (1.5, 'RIGHT'))],
        '313A Classroom': [('312A Classroom', (1.5, 'LEFT')), ('313B Classroom', (2.5, 'STRAIGHT'))],
        '313B Classroom': [('313A Classroom', (2.5, 'STRAIGHT')), ('Washroom', (1.5, 'LEFT'))],
        '314A Food Science Lab': [('Washroom', (4, 'towards Balcony then take LEFT')), ('314B Biology Lab', (2, 'STRAIGHT'))],
        '314B Biology Lab': [('314A Food Science Lab', (2, 'STRAIGHT')), ('Elevator', (3, 'STRAIGHT towards Junction then take RIGHT'))],
        'Elevator': [('314B Biology Lab', (3, 'After Exiting from lift take RIGHT then LEFT')), ('306 IPDC Lab', (1, 'After exiting from Lift take LEFT'))]
    }

# Function to get the user's destination input
def get_target_location(floor_graph):
    print("Available destinations: ")
    for location in floor_graph:
        print(location)
    target_location = input("Please enter your destination: ")
    return target_location

# Dijkstra's algorithm with performance tracking
def dijkstra(graph, start):
    distances = {node: float('inf') for node in graph}  # Initialize distances to infinity
    distances[start] = 0  # Distance to the start node is 0
    priority_queue = [(0, start)]  # Priority queue to store nodes and distances
    previous_nodes = {node: None for node in graph}  # To store the shortest path
    directions = {node: [] for node in graph}  # To store directions (left, right, etc.)
    nodes_expanded = 0  # To track number of nodes expanded

    start_time = time.time()  # Start timing

    while priority_queue:
        current_distance, current_node = heapq.heappop(priority_queue)
        nodes_expanded += 1  # Increment nodes expanded

        if current_distance > distances[current_node]:
            continue

        for neighbor, (weight, direction) in graph[current_node]:
            distance = current_distance + weight
            if distance < distances[neighbor]:
                distances[neighbor] = distance
                previous_nodes[neighbor] = current_node  # Track the path
                directions[neighbor] = directions[current_node] + [(neighbor, direction, weight)]
                heapq.heappush(priority_queue, (distance, neighbor))

    end_time = time.time()  # End timing
    dijkstra_time = end_time - start_time  # Total time taken

    # Results
    return {
        "distances": distances,
        "previous_nodes": previous_nodes,
        "directions": directions,
        "performance": {
            "time": dijkstra_time,
            "nodes_expanded": nodes_expanded
        }
    }

# Function to reconstruct the shortest path with directions and distances in meters
def reconstruct_path_with_directions(directions, start, target):
    if target not in directions:
        return None

    path_directions = directions[target]
    steps = []
    total_distance = 0
    for i, (location, direction, distance) in enumerate(path_directions):
        total_distance += distance
        step = f"Step {i+1}: Go {direction} to {location} (Distance: {distance} meters)"
        steps.append(step)

    steps.append(f"\nTotal distance to {target}: {total_distance} meters")
    return steps

# Function to map the QR code location to the graph's node name
def map_location(qr_location):
    location_mapping = {
        'IPDC Lab': '306 IPDC Lab',
        'Control/Comm Lab': '307B Control/Comm Lab',
        'Classroom 307A': '307A Classroom',
        'Classroom 308A': '308A Classroom',
        'Classroom 309A': '309A Classroom',
        'Analog Circuit Lab': '309B Analog Circuit Lab',
        'Classroom 310': '310 Classroom',
        'Faculty Room 311': '311 Faculty Room',
        'Classroom 312A': '312A Classroom',
        'Classroom 313A': '313A Classroom',
        'Classroom 313B': '313B Classroom',
        'Food Science Lab': '314A Food Science Lab',
        'Cyber Security Lab': '315 Cyber Security Lab',
        'CAD Simulation Lab': '316 CAD Simulation Lab',
        'Male Washroom': 'WR016 Male Washroom',
        'Female Washroom': 'WR013 Female Washroom',
        'Faculty Room 317': '317 Faculty Room',
        'Dept of CS & App': '301 Dept of CS & App',
        'Board Room': '302 Board Room',
        'HOD Room': '303 CS HOD Room',
        'Molecular Biology Lab': '304/305 Molecular Biology',
        'Library': 'Library',
        'CSE Office': '302D CSE Office',
        # Add other QR location mappings as needed
    }
    return location_mapping.get(qr_location, qr_location)  # Returns mapped location or the same location if not found

def masterFunction(current_location, target_location):
  current_location = map_location(current_location)
  if current_location not in floor_graph:
      return "Invalid QR Code"
  target_location = target_location
  result = dijkstra(floor_graph, current_location)
  distances, previous_nodes, directions = result['distances'], result['previous_nodes'], result['directions']
  performance = result['performance']
  path_directions = reconstruct_path_with_directions(directions, current_location, target_location)
  result_string = ""
  if path_directions:
    result_string += "Directions From " + current_location + " " + target_location + ":\n\n"
    for step in path_directions:
      result_string += step + "\n"
    result_string += "\nPerformance Metrics:\n"
    timeFloat = performance['time']
    timeStr = "{:.20f}".format(timeFloat)
    result_string += "Time Taken:" + timeStr + " seconds\n"
    result_string += f"Nodes Expanded: {performance['nodes_expanded']}\n"
  else:
      result_string = "No paths found."
  return result_string