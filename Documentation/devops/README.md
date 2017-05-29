# SBC-QSYSTEM DEVOPS

Nexus
-----
S2i builds such as that used for this project can be extremely slow to execute.  Build times of more than 30 minutes are normal, and sometimes a build can take more than several hours.  To speed up the build, a local nexus can be used.

The following commands can be used to configure a local nexus:

`oc project servicebc-customer-flow-tools`

`oc new-app sonatype/nexus`

`oc expose svc/nexus`

`oc volumes dc/nexus --add --name nexus-volume-1 --type persistentVolumeClaim --mount-path /sonatype-work/ --claim-name nexus-pv --claim-size 5G --overwrite`


Build Template
--------------
The Build template is used to provision the Tools project.  Be sure to review the contents of the template and substitute appropriate values for the template parameters when the template is processed.

If no changes to the default parameters are required, the template can be processed and used to create OpenShift objects with the following commands:

`oc project servicebc-customer-flow-tools`
`oc process -f sbc-qsystem-build-template.json | oc create -f -` 

Environment Template
--------------------

- `oc project servicebc-customer-flow-dev`
- Allow the dev project to access the tools project
`oc policy add-role-to-user system:image-puller system:serviceaccount:csnr-dmod-dev:default -n servicebc-customer-flow-tools`
- Process and create the Environment Template
- `oc process -f sbc-qsystem-environment-template.json | oc create -f -`

Jenkins
-------

Create a Jenkins server using the Jenkins Persistent template.  It is recommended you allocate more than the minimum 1GB of storage to Jenkins, as otherwise you will frequently have to cull files from Jenkins.

Jenkins Slave
-----------
Slaves are created automatically by the master Jenkins server.  If this is not working then you likely are using the wrong Jenkins image.  Use openshift/jenkins:latest.

Pipeline
--------

The repository contains a Jenkinsfile which defines the pipeline.  

To set the Pipeline to run from a branch, configure the refspec as follows:

`+refs/heads/<branch name>:refs/remotes/origin/<branch name>`

The pipeline can be configured to fire from a webhook setup in the github repository.

More information on webhooks is here:

https://developer.github.com/webhooks/





