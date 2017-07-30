.PHONY: all

all:
	rm *.class
	rm src/*.class
	javac -g -verbose Main.java

failed:
	rm src/*.class
	javac -g -verbose Main.java

new:
	javac -g -verbose Main.java
