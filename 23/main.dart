/*
    2020 - Day 23: 

    The solution consist of playing the game with a linkedlist, as the game
    requires deletion and insertion of cups. However, linkedlist is slow at getting
    the value by index, so in addition, we create a new array with the deck-value as index,
    pointing at the corresponding entry in the linkedlist. With this approach, we get the best of
    both worlds, O(1) deletion and insertion, and O(1) lookup.

    PS: Due to the limitations of Dart, and me being lazy to not implement one my self,
    I was not using a circular linkedlist.
*/

import 'dart:collection';

const String MAIN = "487912365";
const String TEST = "389125467";

class Entry<T> extends LinkedListEntry<Entry<T>> {
    T value;
    Entry(this.value);
    @override
    String toString() => '${super.toString()}: ${value}';
}

void main() {
    var input = parse(MAIN);

    print("Part 01: ${part01(input, moves: 100)}");
    print("Part 02: ${part02(input, moves: 10000000)}");
}

String part01(List<int> input, {int moves = 100}) {
    var l = LinkedList<Entry<int>>();
    var deck = List<int>.from(input);
    deck.forEach((element) {
        l.add(Entry(element));
    });

    return playGame(l, moves: moves).join();
}

int part02(List<int> input, {int moves = 100, int targetLength = 1000000}) {
    // Create a linkedlist of input
    var deck = LinkedList<Entry<int>>();
    input.forEach((value) {
        deck.add(Entry(value));
    });

    // Add the extra million
    var maxOfInput = input.reduce((previousValue, element) => 
        (previousValue > element) ? previousValue : element
    );
    deck.addAll(List<Entry<int>>.generate(
        targetLength - input.length, 
        (index) => Entry(maxOfInput + index + 1))
    );

    // Play the game
    var result = playGame(deck, moves: moves).sublist(0, 10);

    // Multiply the two first values of the deck 
    return result[0] * result[1];
}

List<int> playGame(LinkedList<Entry<int>> deck, {int moves = 100}) {
    
    // Calculate max and min value
    var min = 1;
    var max = deck.fold(0, (previousValue, element) => previousValue > element.value ? previousValue : element.value);

    // Store every cupValue's entry in the linkedlist by value in a new list. Lookup by value is then O(1).
    var entriesByValue = new List<Entry<int>>(deck.length);
    deck.forEach((entry) {
        entriesByValue[entry.value-1] = entry;
    });

    // Start at the beginning of the deck
    var curCupEntry = deck.first;

    var move = 0;
    while(move < moves) {

        // Remove three cups
        var startIndex = curCupEntry.next ?? deck.first;
        var middleIndex = startIndex.next ?? deck.first;
        var endIndex = middleIndex.next ?? deck.first;
        var pickedUp = [startIndex, middleIndex, endIndex];
        pickedUp.forEach((entry) {
           entry.unlink();
        });
  

        // Select destination cup. Find target value that is the current cupValue
        // decremented until it is not in the "picked up" list.
        var targetDestination = curCupEntry.value;
        do {
            targetDestination--;
            if(targetDestination < min) {
                targetDestination = max;
            }
        } while(pickedUp.any((entry) => entry.value == targetDestination));

        // Get the destinationEntry from the entries-list
        var destinationEntry = entriesByValue[targetDestination-1];

        // Insert back the three cups behind the destination entry
        pickedUp.forEach((entry) {
            destinationEntry.insertAfter(entry);
            destinationEntry = entry;
        });

        // Select a new current cup
        curCupEntry = curCupEntry.next ?? deck.first;

        move++;
    }

    // Start the deck after value '1'
    var deckStartingAfterValueOne = deck
        .skipWhile((value) => value.value != min)
        .map((e) => e.value)
        .toList()
        .sublist(1);
    deckStartingAfterValueOne.addAll(deck.takeWhile((value) => value.value != min).map((e) => e.value));
    return deckStartingAfterValueOne;
}

List<int> parse(String input) {
    return input.runes.map((e) => int.parse((e - 48).toString())).toList();
}