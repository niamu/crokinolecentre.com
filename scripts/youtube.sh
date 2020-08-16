#!/bin/sh

set -euxo pipefail

mkdir -p resources/templates/themes/crokinolecentre/{css,js}/

# Create download-archive from existing files
touch scripts/_downloaded.txt
grep -riEo "{:id \"(.*?)\"" resources/templates/md/posts | \
    grep -Eo " \"(\w+)\"" | \
    sed -E 's/ \"(.*)\"/youtube \1/' > scripts/_downloaded.txt

# Download JSON metadata of all videos in channel
youtube-dl \
    -w \
    --download-archive scripts/_downloaded.txt \
    --skip-download \
    --write-info-json \
    --output "resources/youtube-dl/%(upload_date)s-%(title)s.%(ext)s" \
    https://www.youtube.com/user/lshgmail/videos

# Parse JSON files into Cryogen Markdown posts
clojure -A:youtube-posts

rm scripts/_downloaded.txt
rm resources/youtube-dl/*

for i in resources/templates/images/thumbnails/*.jpg.old
do
	ffmpeg -y -i "$i" "${i%.*}"
	rm "$i"
done
