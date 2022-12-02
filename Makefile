compile:
	sbt clean compile

test:
	sbt test

check:
	sbt "-Dfatal-warnings=true" check

format:
	sbt fixStyle

documentation:
	cd website; npm run start
