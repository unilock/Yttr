#!/bin/bash -e
mcversion="$1"
version="$2"
jar="$3"
rid=$(curl -fsL -X POST -H "Authorization: token $GITEA_KEY" -H 'Content-Type: application/json' -H 'Accept: application/json' \
		https://git.sleeping.town/api/v1/repos/unascribed/Yttr/releases \
		-d "$(jq --slurp -R "{body:.,draft:false,name:\"v$version\",tag_name:\"$version\",target_commitish:\"$mcversion\"}" CHANGELOG.md)" \
	|jq -r '.id')
curl -fsL -X POST -H "Authorization: token $GITEA_KEY" -H 'Content-Type: multipart/form-data' -H 'Accept: application/json' \
		"https://git.sleeping.town/api/v1/repos/unascribed/Yttr/releases/$rid/assets" --url-query "name=$(basename "$jar")" \
		-F "attachment=@$jar;type=application/x-java-archive" \
	|jq -r '.browser_download_url'
