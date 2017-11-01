Transport endpoint disconnected healthcheck:
---

A liveness probe indicates whether a container is running; if it fails, the container is killed and subject to its restart poicy. The transport endpoint disconnect healthcheck is a liveness probe checking the persistence of the glusterFS/NFS/Remote mount volume at `/opt/app-root/src/source/dist/temp`. If the volume dismounts, the container will restart and the application unavailable until such time as the volume can be remounted.

In our templates, a qsystem deployment config `livenessProbe` stanza goes as follows:
```
"livenessProbe": {
  "exec": {
    "command": ["ls","/opt/app-root/src/source/dist/temp"]
  },
  "initialDelaySeconds": 10,
  "timeoutSeconds": 5,
  "periodSeconds": 60,
  "successThreshold": 1,
  "failureThreshold": 3
  },  
```

similarly, in cerberus pods, the `livenessProbe` is the same except `"command": ["ls","/pubilc/videos/"]`.
