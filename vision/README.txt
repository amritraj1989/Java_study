Themes are created using jQuery UI ThemeRoller then they are compiled into a JAR
// TODO: see <Link> to show how to do this.

This archetype shows/uses Spring Data JPA (no enterprise level base class; but you can 
create a base DAO as well as concrete DAOImpl classes  per your discretion in specific Applications)

This archetype uses an embedded HQSQL DB as well as *.sql files to create a DB that the JPA layer works against
you will need to update your DB information in ApplicationContext.xml as well as Persistence.xml to point to 
your project's real DB (i.e. Oracle).

You need to change the web context root in web project settings in eclipse as well as the project name in your POM.xml
file to what you want your project name and context root to be (creating a new project off the archetype does not do this
for you).

TODO: the message bundle localization approach does not really support
localization; this should be revisited if you need to support full localization.

TODO: auditing is not included

TODO: a help system has not been added
