# SBC-QSYSTEM DEVOPS

Nexus
-----
S2i builds such as that used for this project can be extremely slow to execute.  Build times of more than 30 minutes are normal, and sometimes a build can take more than several hours.  To speed up the build, a local nexus can be used.

The following commands can be used to configure a local nexus:

`oc project servicebc-customer-flow-tools`

`oc new-app sonatype/nexus`

`oc expose svc/nexus`

`oc volumes dc/nexus --add --name nexus-volume-1 --type persistentVolumeClaim --mount-path /sonatype-work/ --claim-name nexus-pv --claim-size 5G --overwrite`


Running templates
----------------
All of the following instructions have the following prerequisites:

1. The "oc" command is installed properly and can be run from any directory
2. The operator has Admin access to OpenShift as has logged in
3. The `oc project` command shows that the operator is in the correct project
3. The Git repository for SBC-QSystem has been cloned to the operator's computer
4. The operator has changed to the directory containing the templates (Openshift/templates)

Build Template
--------------
The Build template is used to provision the Tools project.  Be sure to review the contents of the template and substitute appropriate values for the template parameters when the template is processed.

If no changes to the default parameters are required, the template can be processed and used to create OpenShift objects with the following commands:

`oc project servicebc-customer-flow-tools`
`oc process -f sbc-qsystem-build-template.json | oc create -f -`

Jenkins
-------

Create a Jenkins server using the Jenkins Persistent template.  It is recommended you allocate more than the minimum 1GB of persistent storage to Jenkins, as otherwise you will frequently have to cull files from Jenkins.

**Important Note**

By default the Kubernetes plugin does not override the OpenShift defaults for memory consumption.  In order for the build pipeline to work you will need to allocate **4Gi** of RAM to the **maven** node template.  (More may be required as the volume of code increases).  

To increase the amount of RAM available in the maven node template, do the following:
- login to Jenkins as administrator
- Click on Manage Jenkins
- Scroll down until you are at the maven kubernetes template area
- Click the advanced button to expose the memory limit fields
- Set the memory limit to 4Gi
- Click save   

Note:  On the OpenShift Dashboard you will see two deployment objects under the group Jenkins Persistent.  This is because the Jenkins server has two services.  One service is the main web based user interface, and the other is a JNLP interface that the slaves use to communicate with the main server.

Jenkins Slave
-----------
Slaves are created automatically by the master Jenkins server.  If this is not working then you likely are using the wrong Jenkins image.  Use openshift/jenkins:latest.

Pipelines
--------

The repository contains Jenkinsfiles which defines the pipelines.

- /Jenkinsfile defines the main pipeline which builds the QSystem application image.
- /Jenkinsfile.test defines the pipeline used to promote to Test
- /Jenkinsfile.prod defines the pipeline used to promote to Prod

All pipleines will need to be setup in Jenkins.  Add a new item to Jenkins, choose Pipeline as the type.  Use the Pipeline from SCM option, enter the git details for the project source, and the appropriate value for the Jenkinsfile.   

To set the Pipeline to run from a branch, configure the refspec as follows:

`+refs/heads/<branch name>:refs/remotes/origin/<branch name>`

The pipeline can be configured to fire from a webhook setup in the github repository.

More information on webhooks is here:

https://developer.github.com/webhooks/

Environment Template
--------------------

The environment template is used to create the objects used to deploy the application.

These objects include:
1. A secret, which is used to store various database credentials
2. Persistent storage for the database
3. The database server
4. The first instance of QSystem.  Additional instances can be deployed using the process mentioned below in this document.

To create the environment, execute the following commands when logged in via oc:

- `oc project servicebc-customer-flow-dev` (or the name of the project you wish to deploy the environment)
- Allow the dev project to access the tools project
`oc policy add-role-to-user system:image-puller system:serviceaccount:servicebc-customer-flow-dev:default -n servicebc-customer-flow-tools`
- Process and create the Environment Template
- `oc process -f sbc-qsystem-environment-template.json  -p APP_DEPLOYMENT_TAG=<DEPLOYMENT TAG> | oc create -f -`
	- Substitute latest (dev enviornment), test (test enviornment) or prod (prod enviornment) for the <DEPLOYMENT TAG>
- Load the new office template
- `oc create -f sbc-qsystem-additional-office.json`

Configure NGINX
---------------
This product features a special nginx configuration that will automatically route traffic to offices as they are added.  However you will need to configure an environment variable in the NGINX pod before this will be enabled.

The environment variable is `SEARCH_DOMAIN`.  This should be set to the default search domain for the OpenShift Project you are deploying to.  For example, `servicebc-customer-flow-dev.svc.cluster.local`.

You should not need to set this value, as the environment template will configure it.

Other Notes:

1. If you modify the NGINX configuration, you will need to rebuild the image in the tools section.  Note that this will automatically update all pods in all enviornments.
2. There is a persistent storage setup to add movies for the digital signage.

Deploying to Additional Offices
-------------------------------
A key requirement when deploying to additional offices is that a database user with the ability to create databases must be present.  

The application will obtain the root credentials from the project secret.   

It is recommended you add the office via the web user interface.  Use the following procedure:
1. Navigate to the OpenShift Project where you want to add an additional office.
2. Click the "Add to Project" button
3. Type sbc-qsystem-office to find the template
	1. If no results are found, follow the instructions above in the Environment Template section to add the template.
4. Carefully enter the required fields.
	1. Office Service Name - very important, the service name for the new office.  Must be lowercase and in hostname format, for example office-10
	2. Image tag containing application images - change to the tag for the project you are working with.  For example, set to test if working on the test project.
	3. Application Image Name - image name to use the office - the default should work.
	4. Namespace containing application images - the default should work.

Automatic Office Route
----------------------
Nginx has been configured such that new offices will automatically get a route.

Navigate to <nginx url>/<office-name>/ to go to the office instance of QSystem.

Navigate to <nginx url>/<office-name>/reports/ to go to the reports for that office.

Removing an Office
------------------

To remove an office, do the following:
- Delete the Deployment for the office with `oc delete dc <office-name>
- Delete the Service for the office with `oc delete svc <office-name>
- Delete the permanent storage for the office
- `oc rsh <mysql_podname>` and then `mysql`
- `drop database <office-name>`
