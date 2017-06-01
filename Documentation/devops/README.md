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

Create a Jenkins server using the Jenkins Persistent template.  It is recommended you allocate more than the minimum 1GB of storage to Jenkins, as otherwise you will frequently have to cull files from Jenkins.

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

- `oc project servicebc-customer-flow-dev`
- Allow the dev project to access the tools project
`oc policy add-role-to-user system:image-puller system:serviceaccount:servicebc-customer-flow-dev:default -n servicebc-customer-flow-tools`
- Process and create the Environment Template
- `oc process -f sbc-qsystem-environment-template.json | oc create -f -`
- Load the new office template
- `oc create -f sbc-qsystem-additional-office.json`

Deploying to Additional Offices
-------------------------------
A key requirement when deploying to additional offices is that a database user with the ability to create databases must be present.  

Due to the way jdbc and mysql work, you will need to adjust the credentials for this account if that process has not been done in the OpenShift Project environment you are working with.  The reason for this is MySQL stores credentials based on hostname.

A further convention is the Root user uses the same password as the appication user.

For example, if you were to set the root password to "Not Recommended", you would execute the following:

1. `oc rsh <mysql pod name>`
2. `mysql`
3. `SET PASSWORD FOR 'root'@'127.0.0.1' = PASSWORD('Not Recommended');

Once you have credentials you can add the office.

It is recommended you do this via the web user interface.  Use the following procedure:
1. Navigate to the OpenShift Project where you want to add an additional office.
2. Click the "Add to Project" button
3. Type sbc-qsystem-office to find the template
	1. If no results are found, follow the instructions above in the Environment Template section to add the template.
4. Carefully enter the required fields.
	1. Office Service Name - very important, the service name for the new office.  Must be lowercase and in hostname format, for example office-10
	2. Database Service Name - must match the database service name for the database in the project.
	3. DB Username - must match the database user name for a regular user
	4. DB Root Username - must match the database user name for a root user
	5. Database Password - the database password (by design this password is used by both the root and unprivileged user)
	6. Database Name - the new name for the database.  Must not be in use.  
	7. Application Image Name - image name to use the office - the default should work.
	8. Namespace containing application images - the default should work.
	9. Image tag containing application images - change to the tag for the project you are working with.  For example, set to test if working on the test project.





