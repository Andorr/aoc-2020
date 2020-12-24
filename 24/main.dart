

import 'dart:io';

void main() {

    var input = parse("input.txt");
    
    var result = part01(input);
    print("Part 01: ${result[0]}");
    print("Part 02: ${part02(result[1])}");

}

List part01(List<List<String>> input) {

    var locations = Map<String, bool>();

    for(var directions in input) {
        int y = 0;
        int x = 0;

        for(var direction in directions) {
            var steps = directionToVector(direction);
            y += steps[1];
            x += steps[0];
        }

        var coords = coord(y, x);
        locations[coord(y, x)] = locations.containsKey(coords) ? !locations[coords] : true;
    }
    
    return [locations.values.where((it) => it).length, locations];
}

int part02(Map<String, bool> input, {int days = 100}) {
    // Game of Life on a hexagon.

    // The size of the grid needs to be big enough.
    int minX = -160;
    int maxX = 160;
    int minY = -160;
    int maxY = 160;

    var prev = Map<String, bool>.from(input); 
    for(var day = 0; day < days; day++) {
        var grid = Map<String, bool>.from(prev); 

        for(var y = minY; y <= maxY; y++) {
            for(var x = minX; x <= maxX; x++) {
                var coords = coord(y, x);
                var isBlack = prev.containsKey(coords) ? prev[coords] : false;

                var numBlackNeighbours = [
                    [1, 0],
                    [-1, 0],
                    [-1, 1],
                    [0, 1],
                    [0, -1],
                    [1, -1],
                ].map((direction) {
                    var yy = y + direction[1];
                    var xx = x + direction[0];
                    var neighbour = coord(yy, xx);
                    return prev.containsKey(neighbour) ? prev[neighbour] : false;
                })
                .where((isNeighbourBlack) => isNeighbourBlack)
                .length;

                if(isBlack && (numBlackNeighbours == 0 || numBlackNeighbours > 2)) {
                    grid[coords] = false;
                }
                else if(!isBlack && numBlackNeighbours == 2) {
                    grid[coords] = true;
                }
            }
        }


        prev = grid; 
    }

    return prev.values.where((isBlack) => isBlack).length;
}

String coord(int y, int x) {
    return "$y-$x";
}

List<int> directionToVector(String direction) {
    switch(direction) {
        case 'e': {
            return [1, 0];
        }
        case 'w': {
            return [-1, 0];
        }
        case 'nw': {
            return [-1, 1];
        }
        case 'ne': {
            return [0, 1];
        }
        case 'sw': {
            return [0, -1];
        }
        case 'se': {
            return [1, -1];
        }
        default: {
            return [0, 0];
        }
    }
}

List<List<String>> parse(String filename) {
    return File(filename)
        .readAsLinesSync()
        .map<List<String>>(
            (line) {
                var list = new List<String>();
                var i = 0;

                while(i < line.length) {
                    if(line[i] == 'w' || line[i] == 'e') {
                        list.add(line[i].toString());
                        i++;
                    } else {
                        list.add(line[i] + line[i + 1]);
                        i += 2;
                    }
                }
   

                return list;
            }
    ).toList();
}