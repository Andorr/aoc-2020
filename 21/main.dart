import 'dart:io';

void main() {
    var input = parse("input.txt");

    print("Part 01: ${part01(input)}");
    print("Part 02: ${part02(input[1])}");
}

int part01(List<Map> input) {
    var ingredients = input[0] as Map<String, Map>;
    var allergenes = input[1] as Map<String, Set<String>>;

    // Count ingredient-occurrences of ingredients that is not in allergenes.  
    return ingredients.keys.fold(0, (acc, ingredient) {
        var count = allergenes.values.any((ingredients) => ingredients.contains(ingredient)) ? 0 : ingredients[ingredient]["count"];
        return acc + count;
    });
}

String part02(Map<String, Set<String>> input) {
    var allergenes = Map<String, Set<String>>.from(input); 
    var m = Map<String, String>(); // Ingredient, allergene 

    // 1. Read each allergene with ingredient-count == 1
    // 2. Declare new allergene-ingredient pair
    // 3. Remove found ingredient from other allergene-ingredient sets.
    // 4. Repeat
    while(true) {
        var singleEntries = allergenes.entries.where((entry) => entry.value.length == 1).toList();
        if(singleEntries.length == 0) {
            break;
        }

        for(var entry in singleEntries) {
            // Add entry into "m"
            var ingredient = entry.value.first;
            m[ingredient] = entry.key;

            // Remove allergene from "allergenes" buffer
            allergenes.remove(entry.key);

            // Remove ingredient in other entries
            allergenes.forEach((key, ingredients) {
                if(ingredients.contains(ingredient)) {
                    ingredients.remove(ingredient);
                }
            });
        }
    }

    // Sort results by allergene and merge ingredient into string. 
    var result = m.entries.toList();
    result.sort((a, b) => a.value.compareTo(b.value));
    return result.map((e) => e.key).join(",");
}


List<Map> parse(String filename) {
    var allergenes = Map<String, Set<String>>(); // Allergenes with their potential ingredients
    var ingredients = Map<String, Map>(); // Ingredients with all their potential allegenes and occurrence count


    var lines = new File(filename)
        .readAsStringSync()
        .split("\n")
        .map((String s) => s.split(" (contains "));

    for(var line in lines) {
        var lineAllergenes = line[1].substring(0, line[1].length - 1).split(", ");
        var lineIngredients = line[0].split(" ");

        // Allergenes' ingredients intersected with other allergene-ingredients 
        for(var allergene in lineAllergenes) {
            allergenes[allergene] = (allergenes[allergene]?.intersection(Set.from(lineIngredients)) ?? Set.from(lineIngredients));
        }

        // All ingredient with their potential allergenes
        for(var ingredient in lineIngredients) {
            ingredients[ingredient] = {
                "count": 1 + (ingredients.containsKey(ingredient) ? ingredients[ingredient]["count"] : 0),
                "list": Set.from([...lineAllergenes, ...(ingredients.containsKey(ingredient) ? ingredients[ingredient]["list"].toList() : [])]),
            };
        }
    }

    return [ingredients, allergenes];
}
