#!/bin/sh

echo "Starting mount test."

mount_point=$1
mount_timeout=$2

echo "Checking: $mount_point for $mount_timeout seconds."

# check directory until reached or timeout
for (( i=1 ; i<$mount_timeout ; i++ )) ; do
  if [ ! -d "$mount_point" ]; then
    sleep 1
  else
    break
  fi
done

# final mount test
if [ ! -d "$mount_point" ]; then
  echo "Could not list ${mount_point} in $mount_timeout sec."
  curl -X POST --data-urlencode "payload={\"channel\": \"#sbc-cfms\", \"username\": \"mountbot\", \"text\": \"Liveness Probe failed on ${HOSTNAME}: could not reach ${mount_point} after $mount_timeout seconds.\", \"icon_emoji\": \":sleuth_or_spy:\"}" $SLACKHOOK
  exit 1
else
    echo "File test write success"
    exit 0
  fi
fi
