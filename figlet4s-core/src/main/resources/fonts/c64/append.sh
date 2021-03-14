#!/bin/bash

for file in * ; do

    lines="$(wc -l "$file" | cut -f1 -d' ')"
    if [[ $lines == 768 ]] ; then
        echo "Adding to file $file"
        cat >> $file <<EOF
$        @
$        @
$        @
$        @
$        @
$        @
$        @@
$        @
$        @
$        @
$        @
$        @
$        @
$        @
$        @@
$        @
$        @
$        @
$        @
$        @
$        @
$        @
$        @@
$        @
$        @
$        @
$        @
$        @
$        @
$        @
$        @@
$        @
$        @
$        @
$        @
$        @
$        @
$        @
$        @@
$        @
$        @
$        @
$        @
$        @
$        @
$        @
$        @@
$        @
$        @
$        @
$        @
$        @
$        @
$        @
$        @@
EOF
    fi

done
