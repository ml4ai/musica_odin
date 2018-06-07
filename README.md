# The musica_odin project

Using Processors, Odin and a webapp, this project provides a framework
for developing grammar rules for parsing.

## Running the webapp

Once installed, from the project root run:
`$ sbt webapp/run`
and point a browser to `localhost:9000`

## Scala project installation notes

### Mac (High Sierra 10.13.5)

- Requires Java 1.8 (Java 8)

- IntelliJ
..- Open project, select project root (containing build.sbt)
..- Dialog box:
....- Select options: (a) download sbt sources and (b) using sbt console
....- Ensure you're using Java 1.8 JDK and JRE

- First run will take a while as it downloads all related packages
-- The following maintain the project-specific sbt and scala
--- ~/.ivy2  # storage of package cache ; ends up about 1.6G
--- ~/.sbt   # storage of sbt and scala versions
