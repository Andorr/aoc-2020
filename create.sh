mkdir $1 && cp -r ./tmp/. ./$1 && mv ./$1/tmp.iml ./$1/aoc$1.iml

sed -i "s/tmp/aoc$1/g" ./$1/.idea/modules.xml
