# Build from Gradle

1. Build WAR and deploy manually to Tomcat
    - Execute from module folder

        gradle war

    - OR execute from project root folder

        gradle :backend:war

2. Build WAR and deploy by gradle to to remote Tomcat server
    - Execute from module folder

        gradle cargoDeployRemote

    - OR execute from project root folder

        gradle :backend:cargoDeployRemote

**Note:** if applications already deployed you should execute "gradle cargoRedeployRemote"

3. Create command line distribution for various administrative tasks
    - Execute from module folder

        gradle distZip

    - OR execute from project root folder

        gradle :backend:distZip
