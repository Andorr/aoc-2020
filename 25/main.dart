import 'dart:io';

void main() {
    var input = parse("input.txt");

    print("Part 01: ${part01(input)}");
    print("Part 02: Merry Christmas! :D");
}

int part01(List<int> input) {

    // Find loop sizes
    var cardPubKey = input[0];
    var doorPubKey = input[1];

    int calcLoopSize(int pubkey, int subjectId) {
        var value = 1;
        var loopSize = 0;
        while(value != pubkey) {
            value = (value * subjectId) % 20201227;
            loopSize++;
        }
        return loopSize;
    }

    int transform(int subjectId, int loopSize) {
        var value = 1;
        for(int i = 0; i < loopSize; i++) {
            value = (value * subjectId) % 20201227;
        }
        return value;
    }

    var doorLoopSize = calcLoopSize(doorPubKey, 7);
    var encryptionKey = transform(cardPubKey, doorLoopSize);
    
    return encryptionKey;
}

List<int> parse(String filename) => File(filename).readAsLinesSync().map((e) => int.parse(e)).toList();