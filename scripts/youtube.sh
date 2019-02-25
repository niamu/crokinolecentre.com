#!/bin/sh

# Create download-archive from existing files
grep -riEo "\"display_id\": \"(.*?)\"" resources/youtube-dl | \
    grep -Eo " \"(\w+)\"" | \
    sed -E 's/ \"(.*)\"/youtube \1/' > scripts/_downloaded.txt

# Download JSON metadata of all videos in channel
youtube-dl \
    -q \
    -w \
    --download-archive scripts/_downloaded.txt \
    --skip-download \
    --write-info-json \
    --output "resources/youtube-dl/%(upload_date)s-%(title)s.%(ext)s" \
    https://www.youtube.com/user/lshgmail/videos

# Parse JSON files into Cryogen Markdown posts
clojure -A:youtube-posts
