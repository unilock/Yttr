#!/bin/bash
curl -fsLi -H 'accept: application/json' \
	"https://git.sleeping.town/api/v1/repos/unascribed/Yttr/commits?sha=$CI_COMMIT_SHA&limit=1" \
	|grep -i '^x-total:' \
	|cut -d: -f2
