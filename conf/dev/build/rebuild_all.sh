/opt/servers/stop_all.sh

cd /opt/_repo/unidata
svn update .
. /opt/_repo/unidata/main/init-env.sh

cd /opt/_repo/unidata/main/database
gradle flywayMigrate

cd /opt/_repo/unidata/main
./gradle.sh war

# delete deployed apps
rm -rf /opt/servers/apache-tomcat-7.0.61/webapps/unidata-backend
rm -rf /opt/servers/apache-tomcat-7.0.61/webapps/unidata-frontend
rm -rf /opt/servers/apache-tomcat-7.0.61/webapps/api-docs

# delete /work/* contents in all Tomcat instances
rm -rf /opt/servers/apache-tomcat-7.0.61/work/*

cp -f /opt/_repo/unidata/main/target/unidata-backend.war /opt/servers/apache-tomcat-7.0.61/webapps
cp -f /opt/_repo/unidata/main/target/unidata-frontend.war /opt/servers/apache-tomcat-7.0.61/webapps
cp -f /opt/_repo/unidata/main/target/api-docs.war /opt/servers/apache-tomcat-7.0.61/webapps

/opt/servers/start_all.sh
